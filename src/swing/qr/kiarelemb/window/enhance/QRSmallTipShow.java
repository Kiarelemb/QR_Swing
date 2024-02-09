package swing.qr.kiarelemb.window.enhance;

import method.qr.kiarelemb.utils.QRSleepUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import method.qr.kiarelemb.utils.QRThreadBuilder;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: '
 * @create 2022-11-21 19:20
 **/
public final class QRSmallTipShow extends QREmptyDialog {
	private static final ThreadPoolExecutor TIP_SHOW = QRThreadBuilder.singleThread("tipShow");
	private static final ThreadPoolExecutor TIP_AUTO_CLOSE = QRThreadBuilder.singleThread("TIP_AUTO_CLOSE");
	private static final ThreadPoolExecutor TIP_AUTO_CLOSE2 = QRThreadBuilder.singleThread("TIP_AUTO_CLOSE2");
	private long closeWaitTime = 500;
	private boolean isExisting = false;
	private boolean autoClose = true;

	/**
	 * @param owner 父窗体
	 * @param text  欲显示的内容
	 */
	private QRSmallTipShow(Window owner, String text) {
		super(owner, false);
		setAlwaysOnTop(true);
		QRPanel q = getContentPane();

		QRPanel qp = new QRPanel();
		qp.setLayout(new BorderLayout());
		qp.setBorder(new LineBorder(this.backgroundColor, 8));
		qp.setBackground(this.backgroundColor);
		QRLabel nameLabel = new QRLabel(text);
		qp.add(nameLabel, BorderLayout.CENTER);
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		q.add(qp, BorderLayout.CENTER);
		q.setBorder(new LineBorder(QRColorsAndFonts.BORDER_COLOR, 1));
		setSize(100, 50);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				if (QRSmallTipShow.this.autoClose) {
					final Future<?> submit = TIP_AUTO_CLOSE.submit(() -> {
						QRSleepUtils.sleep(QRSmallTipShow.this.closeWaitTime);
						QRSmallTipShow.this.isExisting = true;
						QRSmallTipShow.this.dispose();
					});
					TIP_AUTO_CLOSE2.execute(() -> {
						long times = QRSmallTipShow.this.closeWaitTime / 10 + 10;
						for (int i = 0; i < times; i++) {
							QRSleepUtils.sleep(10);
							if (submit.isDone() || QRSmallTipShow.this.isExisting) {
								break;
							}
						}
						if (!submit.isDone() && !QRSmallTipShow.this.isExisting) {
							submit.cancel(true);
						}
					});
				}
			}
		});
	}

	public void setAutoCloseFalse() {
		this.autoClose = false;
	}

	public void setCloseWaitTime(long closeWaitTime) {
		if (this.closeWaitTime != closeWaitTime) {
			this.closeWaitTime = closeWaitTime;
		}
	}

	public static QRSmallTipShow getInstance(Window owner, String message) {
		QRSmallTipShow gns = new QRSmallTipShow(owner, message);
		gns.setCloseWaitTime(10000000);
		gns.pack();
		gns.setLocation(owner.getX() + owner.getWidth() / 2 - gns.getWidth() / 2,
				owner.getY() + owner.getHeight() / 2 - gns.getHeight() / 2);
		if (QRSwing.windowRound) {
			QRSystemUtils.setWindowRound(gns, QRSwing.windowTransparency);
		} else {
			QRSystemUtils.setWindowTrans(gns, QRSwing.windowTransparency);
		}
		return gns;
	}

	public static void display(Window owner, String message) {
		display(owner, message, 500);
	}

	public static void display(Window owner, String message, long closeWaitTime) {
		TIP_SHOW.execute(() -> {
			QRSmallTipShow gns = new QRSmallTipShow(owner, message);
			gns.setCloseWaitTime(closeWaitTime);
			gns.pack();
			gns.setLocation(owner.getX() + owner.getWidth() / 2 - gns.getWidth() / 2,
					owner.getY() + owner.getHeight() / 2 - gns.getHeight() / 2);
			if (QRSwing.windowRound) {
				QRSystemUtils.setWindowRound(gns, QRSwing.windowTransparency);
			} else {
				QRSystemUtils.setWindowTrans(gns, QRSwing.windowTransparency);
			}
			gns.setVisible(true);
		});
	}

	public static void display(String message, long closeWaitTime) {
		TIP_SHOW.execute(() -> {
			QRSmallTipShow gns = new QRSmallTipShow(null, message);
			gns.setCloseWaitTime(closeWaitTime);
			gns.setLocationRelativeTo(null);
			gns.pack();
			if (QRSwing.windowRound) {
				QRSystemUtils.setWindowRound(gns, QRSwing.windowTransparency);
			} else {
				QRSystemUtils.setWindowTrans(gns, QRSwing.windowTransparency);
			}
			gns.setVisible(true);
		});
	}
}

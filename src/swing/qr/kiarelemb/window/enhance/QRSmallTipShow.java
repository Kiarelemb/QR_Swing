package swing.qr.kiarelemb.window.enhance;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: '
 * @create 2022-11-21 19:20
 **/
public final class QRSmallTipShow extends QREmptyDialog {
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
                    Timer timer = new Timer(closeDelay(), event -> {
                        QRSmallTipShow.this.isExisting = true;
                        QRSmallTipShow.this.dispose();
                    });
                    timer.setRepeats(false);
                    timer.start();
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
        if (SwingUtilities.isEventDispatchThread()) {
            return createTip(owner, message, 10000000, false);
        }
        final QRSmallTipShow[] result = new QRSmallTipShow[1];
        try {
            SwingUtilities.invokeAndWait(() -> result[0] = createTip(owner, message, 10000000, false));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
        return result[0];
    }

    public static void display(Window owner, String message) {
        display(owner, message, 500);
    }

    public static void display(Window owner, String message, long closeWaitTime) {
        QRComponentUtils.runOnEdt(() -> createTip(owner, message, closeWaitTime, true).setVisible(true));
    }

    public static void display(String message, long closeWaitTime) {
        QRComponentUtils.runOnEdt(() -> createTip(null, message, closeWaitTime, true).setVisible(true));
    }

    private static QRSmallTipShow createTip(Window owner, String message, long closeWaitTime, boolean autoClose) {
        QRSmallTipShow gns = new QRSmallTipShow(owner, message);
        gns.setCloseWaitTime(closeWaitTime);
        if (!autoClose) {
            gns.setAutoCloseFalse();
        }
        gns.pack();
        if (owner == null) {
            gns.setLocationRelativeTo(null);
        } else {
            gns.setLocation(owner.getX() + owner.getWidth() / 2 - gns.getWidth() / 2,
                    owner.getY() + owner.getHeight() / 2 - gns.getHeight() / 2);
        }
        if (QRSwing.windowRound) {
            QRSystemUtils.setWindowRound(gns, QRSwing.windowTransparency);
        } else {
            QRSystemUtils.setWindowTrans(gns, QRSwing.windowTransparency);
        }
        return gns;
    }

    private int closeDelay() {
        return (int) Math.max(0, Math.min(Integer.MAX_VALUE, closeWaitTime));
    }
}

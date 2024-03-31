package swing.qr.kiarelemb.window.basic;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.combination.QRBorderContentPanel;
import swing.qr.kiarelemb.component.listener.QRWindowListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRParentWindowMove;
import swing.qr.kiarelemb.inter.listener.add.QRWindowListenerAdd;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个空白对话框窗体，没有任何东西，仅可鼠标移动
 * @create 2022-11-04 17:21
 **/
public class QREmptyDialog extends JDialog implements QRParentWindowMove, QRComponentUpdate, QRWindowListenerAdd {
	protected final QRBorderContentPanel contentPane;
	protected final Color backgroundColor;
	private QRWindowListener windowListener;
	private final Point p = new Point();
	private boolean windowCanMove = true;

	/**
	 * @param owner 父窗体
	 */
	public QREmptyDialog(Window owner) {
		this(owner, true);
	}

	/**
	 * @param owner        父窗体
	 * @param parentUnable 父窗体是否禁用
	 */
	public QREmptyDialog(Window owner, boolean parentUnable) {
		super(owner, null, parentUnable ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
		setSize(200, 100);
		setResizable(false);
		setIconImage(QRSwing.windowIcon.getImage());
		setUndecorated(true);
		this.backgroundColor = QRColorsAndFonts.FRAME_COLOR_BACK;
		this.contentPane = new QRBorderContentPanel();
		setContentPane(this.contentPane);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);

		//region listeners
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				QREmptyDialog.this.p.x = e.getX();
				QREmptyDialog.this.p.y = e.getY();
				setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (QREmptyDialog.this.windowCanMove) {
					motion(e);
				}
			}
		});
		//endregion

		if (owner != null) {
			if (owner instanceof QRFrame baseFrame) {
				baseFrame.addChildWindow(this);
			}
		}
		if (QRSwing.windowRound) {
			QRSystemUtils.setWindowRound(this, QRSwing.windowTransparency);
		}
	}

	@Override
	public final void addWindowListener() {
		if (this.windowListener == null) {
			this.windowListener = new QRWindowListener();
			this.windowListener.add(QRWindowListener.TYPE.OPEN, e -> windowOpened((WindowEvent) e));
			this.windowListener.add(QRWindowListener.TYPE.CLOSED, e -> windowClosed((WindowEvent) e));
			this.windowListener.add(QRWindowListener.TYPE.CLOSING, e -> windowClosing((WindowEvent) e));
			this.windowListener.add(QRWindowListener.TYPE.ACTIVATED, e -> windowActivated((WindowEvent) e));
			this.windowListener.add(QRWindowListener.TYPE.DEACTIVATED, e -> windowDeactivated((WindowEvent) e));
			this.windowListener.add(QRWindowListener.TYPE.ICONIFIED, e -> windowIconified((WindowEvent) e));
			this.windowListener.add(QRWindowListener.TYPE.DEICONIFIED, e -> windowDeiconified((WindowEvent) e));
			addWindowListener(this.windowListener);
		}
	}

	@Override
	public final void addWindowAction(QRWindowListener.TYPE type, QRActionRegister ar) {
		if (this.windowListener != null) {
			this.windowListener.add(type, ar);
		}
	}

	public void setCursorWait() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void setCursorDefault() {
		setCursor(Cursor.getDefaultCursor());
	}

	private void motion(MouseEvent e) {
		Point point = getLocation();
		final int x = point.x + e.getX() - this.p.x;
		final int y = point.y + e.getY() - this.p.y;
		setLocation(x, y);
	}

	protected void setFreelyMotionFailed() {
		this.windowCanMove = false;
	}

	/**
	 * 父窗体移动时将自动更新此位置
	 */
	public void updateLocation() {
	}

	/**
	 * 重写前请先调用 {@link #addWindowListener()}
	 */
	public void windowOpened(WindowEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addWindowListener()}
	 */
	public void windowClosing(WindowEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addWindowListener()}
	 */
	public void windowClosed(WindowEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addWindowListener()}
	 */
	public void windowIconified(WindowEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addWindowListener()}
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addWindowListener()}
	 */
	public void windowActivated(WindowEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addWindowListener()}
	 */
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public QRPanel getContentPane() {
		return this.contentPane;
	}

	/**
	 * 父窗体移动时，本窗体也移动
	 */
	@Override
	public void ownerMoved() {
		updateLocation();
	}

	@Override
	public void componentFresh() {
		this.contentPane.componentFresh();
	}
}
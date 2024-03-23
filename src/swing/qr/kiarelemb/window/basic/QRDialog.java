package swing.qr.kiarelemb.window.basic;

import method.qr.kiarelemb.utils.QRStringUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.adapter.QRButtonMouseListener;
import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.combination.QRBorderContentPanel;
import swing.qr.kiarelemb.component.listener.QRWindowListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRParentWindowMove;
import swing.qr.kiarelemb.inter.listener.add.QRWindowListenerAdd;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

import static swing.qr.kiarelemb.theme.QRColorsAndFonts.BORDER_COLOR;
import static swing.qr.kiarelemb.theme.QRColorsAndFonts.LINE_COLOR;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 普通自定义窗体边框的对话框
 * @create 2022-11-21 18:51
 **/
public class QRDialog extends JDialog implements QRParentWindowMove, QRComponentUpdate, QRWindowListenerAdd {
	public final static int LEFT = SwingConstants.LEFT;
	public final static int CENTER = SwingConstants.CENTER;
	protected final QRPanel contentPane;
	protected final QRPanel mainPanel;
	protected final QRButton closeButton;
	private final QRLabel titleLabel;
	private final Window parent;
	private final QRPanel topPanel;
	private boolean parentWindowFollowMove = true;
	private boolean resizable = false;
	private QRWindowListener windowListener;
	protected final QRActionRegister disposeAction;

	private class MouseAdapte extends MouseAdapter {
		private int pressPointX = 0;
		private int pressPointY = 0;
		private int height = 0;
		private int width = 0;
		private boolean down = false;
		private boolean right = false;
		private boolean left = false;
		private boolean up = false;
		private boolean downAndRight = false;
		private boolean downAndLeft = false;
		private boolean upAndRight = false;
		private boolean upAndLeft = false;
		private Point p = null;

		@Override
		public void mousePressed(MouseEvent e) {
			this.p = e.getPoint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			clear();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			//鼠标相对屏幕的x坐标
			int eXOnScreen = e.getXOnScreen();
			int eYOnScreen = e.getYOnScreen();
			int eY = e.getY();
			int eX = e.getX();
			if (this.upAndLeft) {
				setBounds(eXOnScreen, eYOnScreen, this.width + this.pressPointX - eXOnScreen, this.height + this.pressPointY - eYOnScreen);
				setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			} else if (this.upAndRight) {
				setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				setBounds(getX(), eYOnScreen, eX, this.height + this.pressPointY - eYOnScreen);
			} else if (this.downAndLeft) {
				int width = this.width + this.pressPointX - eXOnScreen;
				setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
				setBounds(eXOnScreen, getY(), width, eY);
			} else if (this.downAndRight) {
				setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				setSize(eX, eY);
			} else if (this.up) {
				int height = this.height + this.pressPointY - eYOnScreen;
				int width = this.width;
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				setBounds(getX(), eYOnScreen, width, height);
			} else if (this.left) {
				int height = this.height;
				final int width = this.width + this.pressPointX - eXOnScreen;
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				setBounds(eXOnScreen, getY(), width, height);
			} else if (this.down) {
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				setSize(this.width, eY);
			} else if (this.right) {
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				setSize(eX, this.height);
			} else {
				//只能在窗体的标题栏进行移动
				if (eY < QRDialog.this.topPanel.getHeight()) {
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					if (this.p == null) {
						return;
					}
					setLocation(e.getXOnScreen() - this.p.x, e.getYOnScreen() - this.p.y);
				}
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int eX = e.getX();
			int eY = e.getY();
			int rights = Math.abs(eX - getWidth());
			int downs = Math.abs(eY - getHeight());
			getXYWH(e);
			if (!QRDialog.this.resizable) {
				return;
			}
			if (eY <= QRFrame.DIS && eX <= QRFrame.DIS) {
				setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
				this.upAndLeft = true;
			} else if (eY <= QRFrame.DIS && rights <= QRFrame.DIS) {
				setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				this.upAndRight = true;
			} else if (downs <= QRFrame.DIS && eX <= QRFrame.DIS) {
				setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
				this.downAndLeft = true;
			} else if (downs <= QRFrame.DIS && rights <= QRFrame.DIS) {
				setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				this.downAndRight = true;
			} else if (eY <= QRFrame.DIS) {
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				this.up = true;
			} else if (eX <= QRFrame.DIS) {
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				this.left = true;
			} else if (rights <= QRFrame.DIS) {
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				this.right = true;
			} else if (downs <= QRFrame.DIS) {
				setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				this.down = true;
			} else {
				setCursorDefault();
				clear();
			}
		}

		private void clear() {
			this.down = false;
			this.right = false;
			this.left = false;
			this.up = false;
			this.downAndRight = false;
			this.upAndLeft = false;
			this.downAndLeft = false;
			this.upAndRight = false;
			this.pressPointX = 0;
			this.pressPointY = 0;
			this.height = 0;
			this.width = 0;
		}

		private void getXYWH(MouseEvent e) {
			this.pressPointX = e.getXOnScreen();
			this.pressPointY = e.getYOnScreen();
			this.width = getWidth();
			this.height = getHeight();
		}
	}

	/**
	 * 创建一个默认标题居中、窗体大小不可调整、禁用父窗体的对话框，
	 * <p>使用方法
	 * <p>super(parent);
	 * <p>setTitle("");
	 * <p>setTitlePlace(QRDialog.CENTER);
	 * <p>setSize(400, 300);
	 * <p><code>mainPanel</code> 是已自带的主面板，默认布局为null
	 *
	 * @param parent 父窗体
	 */
	public QRDialog(Window parent) {
		this(parent, true);
	}

	/**
	 * 创建一个默认标题居中、窗体大小不可调整、可用父窗体的对话框，
	 * <p>使用方法
	 * <p>super(parent);
	 * <p>setTitle("");
	 * <p>setTitlePlace(QRDialog.CENTER);
	 * <p>setSize(400, 300);
	 * <p><code>mainPanel</code> 是已自带的主面板，默认布局为null
	 *
	 * @param parent 父窗体
	 */
	public QRDialog(Window parent, boolean parentUnable) {
		super(parent, null, parentUnable ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
		this.parent = parent;
		setIconImage(QRSwing.windowIcon.getImage());
		setUndecorated(true);
		this.contentPane = new QRBorderContentPanel();
		this.contentPane.setLayout(new BorderLayout(5, 5));
		this.contentPane.setBorder(new LineBorder(BORDER_COLOR, 1));
		setContentPane(this.contentPane);

		this.topPanel = new QRPanel();
		this.topPanel.setLayout(new BorderLayout());
		this.topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR));
		this.contentPane.add(this.topPanel, BorderLayout.NORTH);

		QRPanel titlePanel = new QRPanel();
		titlePanel.setLayout(new BorderLayout(2, 0));
		this.topPanel.add(titlePanel, BorderLayout.CENTER);

		QRLabel iconLabel = QRLabel.getIconLabel();
		titlePanel.add(iconLabel, BorderLayout.WEST);

		this.titleLabel = new QRLabel();
		this.titleLabel.setHorizontalAlignment(CENTER);
		this.titleLabel.setForeground(QRColorsAndFonts.MENU_COLOR);
		titlePanel.add(this.titleLabel, BorderLayout.CENTER);

		this.closeButton = new QRButton(QRFrame.CLOSE_MARK) {
			@Override
			protected void actionEvent(ActionEvent o) {
				dispose();
			}

			@Override
			public void componentFresh() {
				super.componentFresh();
				setFont(QRColorsAndFonts.PROCESS_BUTTON_FONT.deriveFont(11f).deriveFont(Font.BOLD));
			}
		};

		this.closeButton.disableListener();
		this.closeButton.setForeground(QRColorsAndFonts.MENU_COLOR);
		this.closeButton.addMouseListener(new QRButtonMouseListener(this.closeButton) {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				//解决一个当鼠(hf)标在关闭按钮右侧的时候，进入按钮仍然是剪头的bug
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		titlePanel.add(this.closeButton, BorderLayout.EAST);

		MouseAdapte adapte = new MouseAdapte();

		addMouseMotionListener(adapte);

		addMouseListener(adapte);
		this.mainPanel = new QRPanel();

		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		this.contentPane.add(this.mainPanel, BorderLayout.CENTER);
		this.mainPanel.setLayout(null);

		if (parent != null) {
			if (parent instanceof QRFrame frame) {
				frame.addChildWindow(this);
			}
		}
		this.disposeAction = e -> {
			if (QRDialog.this.isFocused()) {
				QRDialog.this.dispose();
			}
		};
		QRSwing.registerGlobalAction(KeyEvent.VK_ESCAPE, this.disposeAction, false);
	}

	public void setParentWindowNotFollowMove() {
		this.parentWindowFollowMove = false;
	}

	private void windowStateUpdate() {
		if (QRSwing.windowRound) {
			QRSystemUtils.setWindowRound(this);
		} else {
			QRSystemUtils.setWindowNotRound(this);
		}
		QRSystemUtils.setWindowTrans(this, QRSwing.windowTransparency);
	}

	@Override
	public void componentFresh() {
		this.contentPane.componentFresh();
		this.topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR));
		this.titleLabel.setForeground(QRColorsAndFonts.MENU_COLOR);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
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

	@Override
	public void ownerMoved() {
	}

	@Override
	public final void setTitle(String title) {
		this.titleLabel.setText(QRStringUtils.A_WHITE_SPACE.concat(title));
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			if (this.parent != null) {
				setLocation(this.parent.getX() + this.parent.getWidth() / 2 - getWidth() / 2, this.parent.getY() + this.parent.getHeight() / 2 - getHeight() / 2);
			} else {
				setLocationRelativeTo(null);
			}
		}
		super.setVisible(b);
	}

	@Override
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	@Override
	public final void setSize(int width, int height) {
//		if (resizable) {
		final int minSize = 20;
		if (width < minSize || height < minSize) {
			return;
		}
		super.setSize(width, height);
		if (this.parentWindowFollowMove) {
			if (this.parent != null) {
				setLocation(this.parent.getX() + this.parent.getWidth() / 2 - getWidth() / 2, this.parent.getY() + this.parent.getHeight() / 2 - getHeight() / 2);
			} else {
				setLocationRelativeTo(null);
			}
		}
	}

	@Override
	public final void setBounds(int x, int y, int width, int height) {
		super.setBounds(Math.max(x, 0), Math.max(y, 0), width, height);
		windowStateUpdate();
	}

	@Override
	public void dispose() {
		QRSwing.registerGlobalActionRemove(QRStringUtils.getKeyStroke(KeyEvent.VK_ESCAPE), this.disposeAction, false);
		super.dispose();
	}

	public void setCursorWait() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void setCursorDefault() {
		setCursor(Cursor.getDefaultCursor());
	}

	public final void setTitlePlace(int p) {
		this.titleLabel.setHorizontalAlignment(p);
	}

	protected void setCloseButtonEnable(boolean enable) {
		this.closeButton.setEnabled(enable);
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
}
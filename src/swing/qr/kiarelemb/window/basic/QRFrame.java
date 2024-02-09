package swing.qr.kiarelemb.window.basic;

import method.qr.kiarelemb.utils.QRFileUtils;
import method.qr.kiarelemb.utils.QRFontUtils;
import method.qr.kiarelemb.utils.QRPropertiesUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.adapter.QRButtonMouseListener;
import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.component.assembly.QRBackgroundBorder;
import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.combination.QRBorderContentPanel;
import swing.qr.kiarelemb.component.combination.QRMenuPanel;
import swing.qr.kiarelemb.component.listener.QRWindowListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRParentWindowMove;
import swing.qr.kiarelemb.inter.listener.add.QRWindowListenerAdd;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 一个带标题栏和仨按钮的空白窗体。一般，一个窗体程序只有一个总的 {@link QRFrame}
 * @create 2022-11-04 16:33
 **/
public class QRFrame extends JFrame implements QRComponentUpdate, QRWindowListenerAdd {
	public static final String MIN_MARK = " — ";
	public static final String CLOSE_MARK = "   ╳   ";
	public static final String MAX_MARK = "❐";
	public static final Font PROCESS_BUTTON_FONT = QRFontUtils.loadFontFromURL(QRSwingInfo.loadUrl("seguisym.ttf"));
	public static final int DIS = 5;
	protected final QRButton closeButton;
	protected final QRButton maximumButton;
	protected final QRButton minimumButton;
	protected final QRPanel mainPanel;
	private final QRBorderContentPanel contentPane;
	private final QRPanel topPanel;
	private final QRLabel iconLabel;
	private final QRLabel titleLabel;
	private final QRPanel threeButtonPanel;
	private final ArrayList<QRParentWindowMove> childWindows = new ArrayList<>();
	private final ArrayList<QRActionRegister> actionOnDispose = new ArrayList<>();
	private final QRPanel titlePanel;
	/**
	 * 用于记忆窗体位置及大小信息
	 */
	protected final Properties prop;
	/**
	 * 若设置了 {@link QRSwing#windowTitleMenu} 为 {@code true}，则将自动实例化
	 */
	protected QRMenuPanel titleMenuPanel;
	private QRWindowListener windowListener;
	private QRBackgroundBorder backgroundBorder;
	private String imagePath;
	private Image backgroundImage;
	private double imageRatio;
	private Dimension minimumSize;
	private int originalWidth = 500;
	private boolean backgroundImageSet = false;

	//region class-MouseAdapte
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
		public void mouseClicked(MouseEvent e) {
			windowClickAction();
//            if (e.getClickCount() == 2 && e.getY() < topPanel.getHeight()) {
//                maxWindow();
//            }
		}

		@Override
		public void mousePressed(MouseEvent e) {
//            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//            p = new Point(e.getX() + contentPane.getX(), e.getY() + contentPane.getY());
			this.p = e.getPoint();
//			System.out.println("p = " + p);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			clear();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			childWindowLocationUpdate();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			//鼠标相对屏幕的x坐标
			int eXOnScreen = e.getXOnScreen();
			if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
				setExtendedState(JFrame.NORMAL);
				final int[] screenSize = QRSystemUtils.getScreenSize();
				//屏幕宽度
				final int displayWidth = screenSize[0];
				//计算还原后窗体的新x坐标
				int x = eXOnScreen - (QRFrame.this.originalWidth * eXOnScreen) / displayWidth;
				//更新窗体位置
				setLocation(x, e.getY());
				//更新P点x坐标
				this.p.setLocation(eXOnScreen - x, e.getY());
				return;
			}
			int eYOnScreen = e.getYOnScreen();
			int eY = e.getY();
			int eX = e.getX();
			boolean sizeChanged = true;
			boolean backgroundImageSet = QRFrame.this.backgroundImage != null && QRFrame.this.backgroundBorder.scale();
			if (this.upAndLeft) {
				if (backgroundImageSet) {
					eXOnScreen = (int) Math.min(eXOnScreen, eYOnScreen * QRFrame.this.imageRatio);
					eYOnScreen = (int) (eXOnScreen / QRFrame.this.imageRatio);
				}
				setBounds(eXOnScreen, eYOnScreen, this.width + this.pressPointX - eXOnScreen, this.height + this.pressPointY - eYOnScreen);
				setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			} else if (this.upAndRight) {
				if (backgroundImageSet) {
					eYOnScreen = (int) (eXOnScreen / QRFrame.this.imageRatio);
				}
				int height = this.height + this.pressPointY - eYOnScreen;
				setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				setBounds(getX(), eYOnScreen, eX, height);
			} else if (this.downAndLeft) {
				int width = this.width + this.pressPointX - eXOnScreen;
				if (backgroundImageSet) {
					eXOnScreen = (int) Math.min(eXOnScreen, eYOnScreen * QRFrame.this.imageRatio);
					width = this.width + this.pressPointX - eXOnScreen;
					eY = (int) (width / QRFrame.this.imageRatio);
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
				setBounds(eXOnScreen, getY(), width, eY);
			} else if (this.downAndRight) {
				if (backgroundImageSet) {
					final double w = QRFrame.this.imageRatio * eY;
					if (eX > w) {
						eX = (int) w;
					} else {
						eY = (int) (eX / QRFrame.this.imageRatio);
					}
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				setSize(eX, eY);
			} else if (this.up) {
				int height = this.height + this.pressPointY - eYOnScreen;
				int width = this.width;
				if (backgroundImageSet) {
					width = (int) (height * QRFrame.this.imageRatio);
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				setBounds(getX(), eYOnScreen, width, height);
			} else if (this.left) {
				int height = this.height;
				final int width = this.width + this.pressPointX - eXOnScreen;
				if (backgroundImageSet) {
					height = (int) (width / QRFrame.this.imageRatio);
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				setBounds(eXOnScreen, getY(), width, height);
			} else if (this.down) {
				if (backgroundImageSet) {
					this.width = (int) (QRFrame.this.imageRatio * eY);
					eY = (int) (this.width / QRFrame.this.imageRatio);
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				setSize(this.width, eY);
			} else if (this.right) {
				if (backgroundImageSet) {
					this.height = (int) (eX / QRFrame.this.imageRatio);
					eX = (int) (QRFrame.this.imageRatio * this.height);
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				setSize(eX, this.height);
			} else {
				sizeChanged = false;
				//只能在窗体的标题栏进行移动
				if (eY < QRFrame.this.topPanel.getHeight()) {
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					if (this.p == null) {
						return;
					}
					setLocation(e.getXOnScreen() - this.p.x, e.getYOnScreen() - this.p.y);
				}
			}
			if (sizeChanged) {
				windowSizeChangedAction();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
				int eX = e.getX();
				int eY = e.getY();
				int rights = Math.abs(eX - getWidth());
				int downs = Math.abs(eY - getHeight());
				getXYWH(e);
				if (eY <= DIS && eX <= DIS) {
					setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					this.upAndLeft = true;
				} else if (eY <= DIS && rights <= DIS) {
					setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					this.upAndRight = true;
				} else if (downs <= DIS && eX <= DIS) {
					setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					this.downAndLeft = true;
				} else if (downs <= DIS && rights <= DIS) {
					setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					this.downAndRight = true;
				} else if (eY <= DIS) {
					setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					this.up = true;
				} else if (eX <= DIS) {
					setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					this.left = true;
				} else if (rights <= DIS) {
					setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					this.right = true;
				} else if (downs <= DIS) {
					setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					this.down = true;
				} else {
					setCursorDefault();
					mainPanel.setCursorDefault();
					clear();
				}
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
	//endregion class-MouseAdapte

	public QRFrame() {
		QRFileUtils.fileCreate(QRSwing.WINDOW_PROP_PATH);
		this.prop = QRPropertiesUtils.loadProp(QRSwing.WINDOW_PROP_PATH);

		//region 加载资源
		final int[] screenSize = QRSystemUtils.getScreenSize();
		int sizeWidth = QRPropertiesUtils.getPropInInteger(this.prop, "window.size.width", screenSize[0] / 2);
		int sizeHeight = QRPropertiesUtils.getPropInInteger(this.prop, "window.size.height", screenSize[1] / 2);
		int locationX = QRPropertiesUtils.getPropInInteger(this.prop, "window.start.X", screenSize[0] / 4);
		int locationY = QRPropertiesUtils.getPropInInteger(this.prop, "window.start.Y", screenSize[1] / 4);
		//endregion

		super.setSize(sizeWidth, sizeHeight);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		setIconImage(QRSwing.windowIcon.getImage());
		setLocation(locationX, locationY);
		setUndecorated(true);

		this.contentPane = new QRBorderContentPanel();
		this.contentPane.setLayout(new BorderLayout());
		setContentPane(this.contentPane);
		this.contentPane.setBorder(new LineBorder(QRColorsAndFonts.BORDER_COLOR, 1));

		this.topPanel = new QRPanel();
		this.topPanel.setLayout(new BorderLayout());
		this.topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
		this.contentPane.add(this.topPanel, BorderLayout.NORTH);

		this.titlePanel = new QRPanel();
		this.titlePanel.setLayout(new BorderLayout());
		this.titlePanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 0, 0));
		this.topPanel.add(this.titlePanel, BorderLayout.CENTER);

		this.iconLabel = QRLabel.getIconLabel();
		this.titlePanel.add(this.iconLabel, BorderLayout.WEST);

		this.titleLabel = new QRLabel();
		this.titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

		if (QRSwing.windowTitleMenu) {
			this.titleMenuPanel = new QRMenuPanel();
			QRPanel topCenterPanel = new QRPanel();
			topCenterPanel.setLayout(new BorderLayout());
			topCenterPanel.add(this.titleMenuPanel, BorderLayout.WEST);
			topCenterPanel.add(this.titleLabel, BorderLayout.CENTER);
			this.titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
			this.titlePanel.add(topCenterPanel, BorderLayout.CENTER);
		} else {
			//试图让标签居中
			this.titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 70, 0, 0));
			this.titlePanel.add(this.titleLabel, BorderLayout.CENTER);
		}

		this.threeButtonPanel = new QRPanel();
		this.threeButtonPanel.setLayout(new GridLayout(1, 3, 3, 0));
		this.titlePanel.add(this.threeButtonPanel, BorderLayout.EAST);

		this.minimumButton = new QRButton(MIN_MARK) {
			@Override
			public void componentFresh() {
				super.componentFresh();
				setFont(PROCESS_BUTTON_FONT.deriveFont(13f));
			}
		};
		this.minimumButton.addActionListener(e -> minWindow());
		this.threeButtonPanel.add(this.minimumButton);

		this.maximumButton = new QRButton(MAX_MARK) {
			@Override
			public void componentFresh() {
				super.componentFresh();
				setFont(PROCESS_BUTTON_FONT.deriveFont(16f));
			}
		};
		this.maximumButton.setFont(PROCESS_BUTTON_FONT.deriveFont(16f));
		this.maximumButton.addActionListener(e -> maxWindow());
		this.threeButtonPanel.add(this.maximumButton);

		this.closeButton = new QRButton(CLOSE_MARK) {
			@Override
			public void componentFresh() {
				super.componentFresh();
				setFont(PROCESS_BUTTON_FONT.deriveFont(11f).deriveFont(Font.BOLD));

			}
		};
		this.closeButton.setToolTipText("关闭");
		this.closeButton.setFont(PROCESS_BUTTON_FONT.deriveFont(11f).deriveFont(Font.BOLD));
		this.closeButton.disableListener();
		this.closeButton.addMouseListener(new QRButtonMouseListener(this.closeButton) {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				//解决一个当鼠(hf)标在关闭按钮右侧的时候，进入按钮仍然是剪头的bug
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		this.closeButton.addActionListener(e -> dispose());
		this.threeButtonPanel.add(this.closeButton);
		final MouseAdapte mouseAdapte = new MouseAdapte();
		addMouseListener(mouseAdapte);
		addMouseMotionListener(mouseAdapte);
		MouseAdapter titleMouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					maxWindow();
				} else {
					windowClickAction();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mouseAdapte.mousePressed(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseAdapte.mouseReleased(e);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				setCursorDefault();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseAdapte.mouseDragged(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				mouseAdapte.mouseMoved(e);
			}
		};
		this.titlePanel.addMouseMotionListener(titleMouseAdapter);
		this.titlePanel.addMouseListener(titleMouseAdapter);

		//子类直接向 mainPanel 中添加控件
		this.mainPanel = new QRPanel() {
			@Override
			public void componentFresh() {
				super.componentFresh();
				if (backgroundBorder != null) {
					setBorder(backgroundBorder);
				}
			}
		};
		this.contentPane.add(this.mainPanel, BorderLayout.CENTER);
		addWindowListener();
		addWindowAction(QRWindowListener.TYPE.OPEN, e -> this.minimumSize = getMinimumSizes());
	}

	/**
	 * 一个简单的开始：
	 * <p>{@code QRSwing.start("可找到的properties文件路径");}
	 * <p>{@code QRFrame window = new QRFrame("这是一个测试窗体")}
	 * <p>{{
	 * <p>{@code setTitleCenter();}//设置窗体标题居中
	 * <p>{@code setCloseButtonSystemExit();}//设置单击关闭按钮后窗体淡化退出并结束程序
	 * <p>}};
	 * <p>{@code window.setVisible(true);}//设置窗体可见
	 */
	public QRFrame(String title) {
		this();
		setTitle(title);
	}


	@Override
	public void componentFresh() {
		this.titlePanel.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, QRColorsAndFonts.FRAME_COLOR_BACK));
		this.topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		this.contentPane.componentFresh();
		if (this.childWindows.size() > 0) {
			synchronized (this.childWindows) {
				childWindows.forEach(childWindow -> {
					if (childWindow instanceof QRComponentUpdate window) {
						window.componentFresh();
					}
				});
			}
		}
		mainPanel.setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.TEXT_COLOR_BACK, 5));
		titleMenuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
		revalidate();
		repaint();
	}

	/**
	 * 已在实例化时自动添加
	 */
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

	public QRButton getCloseButton() {
		return this.closeButton;
	}

	/**
	 * 设置单击关闭按钮后退出程序
	 */
	public void setCloseButtonSystemExit() {
		this.closeButton.addActionListener(e -> dispose(true));
	}

	/**
	 * 设置窗体的标题是否居中
	 */
	public void setTitleCenter() {
//		if (!QRSwing.windowTitleMenu) {
		//没有启用标题菜单就可以居中
		this.titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
//		}
	}

	public final void setBackgroundImage(String filePath) {
		if (filePath == null && this.backgroundImageSet) {
//			QRSwing.setBackgroundFilePath(null);
			this.mainPanel.setBorder(null);
			this.backgroundBorder = null;
			imagePath = null;
			this.backgroundImage = null;
			this.backgroundImageSet = false;
			QRSwing.setWindowBackgroundImagePath(null);
			return;
		}
		if (QRFileUtils.fileExists(filePath)) {
			Image imageToSet = QRSwingInfo.loadImage(filePath);
			if (imageToSet != null && !filePath.equals(this.imagePath)) {
				this.backgroundImage = imageToSet;
				imagePath = filePath;
				QRSwing.setWindowBackgroundImagePath(filePath);
				this.backgroundImageSet = true;
				int height = this.backgroundImage.getHeight(null);
				int width = this.backgroundImage.getWidth(null);
				this.imageRatio = (double) width / height;
				this.backgroundBorder = new QRBackgroundBorder(this.backgroundImage);
				this.mainPanel.setBorder(this.backgroundBorder);
				QRComponentUtils.loopComsForBackgroundSetting(this.mainPanel);
				QRSwing.windowTransparency = 0.999f;
				QRSystemUtils.setWindowNotTrans(this);
			}
		}
	}

	public final void setBackgroundBorderScale(boolean scale) {
		if (this.backgroundBorder != null) {
			this.backgroundBorder.setScale(scale);
			QRSwing.setWindowScale(scale);
			QRComponentUtils.windowFresh(this.mainPanel);
		}
	}

	public final void setBackgroundBorderAlpha(float alpha) {
		if (this.backgroundBorder != null) {
			this.backgroundBorder.setAlpha(alpha);
			QRSwing.setWindowAlpha(alpha);
			QRComponentUtils.windowFresh(this.mainPanel);
		}
	}

	public final boolean isBackgroundImageSet() {
		return this.backgroundBorder == null && this.backgroundImage == null;
	}

	public void minWindow() {
		this.setExtendedState(JFrame.ICONIFIED);
	}

	public void maxWindow() {
		if (!this.maximumButton.isEnabled()) {
			return;
		}
		if (this.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
			this.setExtendedState(JFrame.NORMAL);
		} else {
			this.originalWidth = getWidth();
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		windowStateUpdate();
	}

	private void windowStateUpdate() {
		if (QRSwing.windowRound) {
			QRSystemUtils.setWindowRound(this);
		} else {
			QRSystemUtils.setWindowNotRound(this);
		}
		if (backgroundImageSet) {
			QRSystemUtils.setWindowNotTrans(this);
		} else {
			QRSystemUtils.setWindowTrans(this, QRSwing.windowTransparency);
		}
	}

	public void setMaxEnable(boolean b) {
		this.maximumButton.setEnabled(b);
	}

	public void setMinEnable(boolean b) {
		this.minimumButton.setEnabled(b);
	}

	public boolean backgroundImageSet() {
		return this.backgroundImageSet;
	}

	public final void dispose(boolean systemExit) {
		dispose();
		QRComponentUtils.runActions(this.actionOnDispose);
		QRSystemUtils.setWindowCloseSlowly(this, QRSwing.windowTransparency, systemExit);
	}

	/**
	 * 在窗体退出前的操作。
	 * <p>当调用 {@link QRFrame#dispose(boolean)} 时，不确定 {@link QRFrame#windowClosing(WindowEvent)} 和 {@link QRFrame#windowClosed(WindowEvent)} 是否起作用
	 * <p>但本方法一定起作用
	 *
	 * @param ar 其参数 {@link QRActionRegister#action(Object)} 为 {@code null}
	 */
	public final void addActionBeforeDispose(QRActionRegister ar) {
		this.actionOnDispose.add(ar);
	}

	public Dimension getMinimumSizes() {
		int titleWidth = this.iconLabel.getWidth() + QRFontUtils.getTextInWidth(this.titleLabel, this.titleLabel.getText()) + this.threeButtonPanel.getWidth() + 20;
		return new Dimension(titleWidth, titleWidth / 2);
	}

	public void setCursorWait() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public void setCursorDefault() {
		setCursor(Cursor.getDefaultCursor());
	}

	@Override
	public String getTitle() {
		return this.titleLabel.getText();
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		this.titleLabel.setText(title);
		if (isVisible()) {
			//更新
			this.minimumSize = getMinimumSizes();
		}
	}

	@Override
	public void setSize(int width, int height) {
		if (width < this.minimumSize.width || height < this.minimumSize.height) {
			return;
		}
		super.setSize(width, height);
	}

	/**
	 * 子类继承，用于自动保存窗体大小和位置信息
	 */
	@Override
	public final void dispose() {
		int width = getWidth();
		int height = getHeight();
		int x = getX();
		int y = getY();
		this.prop.setProperty("window.size.width", Integer.toString(width));
		this.prop.setProperty("window.size.height", Integer.toString(height));
		this.prop.setProperty("window.start.X", Integer.toString(x));
		this.prop.setProperty("window.start.Y", Integer.toString(y));
		//保存
		QRPropertiesUtils.storeProp(this.prop, QRSwing.WINDOW_PROP_PATH);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		if (getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			if (QRSwing.windowScreenAdsorb) {
				final int[] screenSize = QRSystemUtils.getScreenSize();
				final int displayWidth = screenSize[0];
				final int displayHeight = screenSize[1];
				if (width > displayWidth || height > displayHeight) {
					width = displayWidth / 2;
					height = displayHeight / 2;
					x = displayWidth / 4;
					y = displayHeight / 4;
				} else {
					int adsorbent = 15;
					if (Math.abs(x) < adsorbent) {
						x = 0;
					}
					int rightX = x + width;
					if (rightX < displayWidth + adsorbent && rightX > displayWidth - adsorbent) {
						x = displayWidth - width;
					}
					if (Math.abs(y) < adsorbent) {
						y = 0;
					}
					int bottomY = y + height;
					if (bottomY < displayHeight + adsorbent && bottomY > displayHeight - adsorbent) {
						y = displayHeight - height;
					}
				}
			}
			super.setBounds(x, y, Math.max(width, 100), Math.max(height, 50));
			windowStateUpdate();
		}
	}

	/**
	 * 窗体单击事件
	 */
	protected void windowClickAction() {
	}

	/**
	 * 窗体大小改变事事件
	 */
	protected void windowSizeChangedAction() {

	}

	protected void childWindowLocationUpdate() {
		synchronized (this.childWindows) {
			for (QRParentWindowMove childWindow : this.childWindows) {
				childWindow.ownerMoved();
			}
		}
	}

	public void addChildWindow(QRParentWindowMove w) {
		this.childWindows.add(w);
	}

	/**
	 * 已自动添加监听器，可直接重写
	 */
	public void windowOpened(WindowEvent e) {
	}

	/**
	 * 已自动添加监听器，可直接重写
	 */
	public void windowClosing(WindowEvent e) {
	}

	/**
	 * 已自动添加监听器，可直接重写
	 */
	public void windowClosed(WindowEvent e) {
	}

	/**
	 * 已自动添加监听器，可直接重写
	 */
	public void windowIconified(WindowEvent e) {
	}

	/**
	 * 已自动添加监听器，可直接重写
	 */
	public void windowDeiconified(WindowEvent e) {
	}

	/**
	 * 已自动添加监听器，可直接重写
	 */
	public void windowActivated(WindowEvent e) {
	}

	/**
	 * 已自动添加监听器，可直接重写
	 */
	public void windowDeactivated(WindowEvent e) {
	}
}

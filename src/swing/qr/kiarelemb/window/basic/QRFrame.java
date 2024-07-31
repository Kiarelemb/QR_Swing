package swing.qr.kiarelemb.window.basic;

import method.qr.kiarelemb.utils.QRFileUtils;
import method.qr.kiarelemb.utils.QRFontUtils;
import method.qr.kiarelemb.utils.QRPropertiesUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.combination.QRBorderContentPanel;
import swing.qr.kiarelemb.combination.QRMenuPanel;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRParentWindowMove;
import swing.qr.kiarelemb.inter.listener.add.QRWindowListenerAdd;
import swing.qr.kiarelemb.listener.QRWindowListener;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRCloseButton;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 一个带标题栏和仨按钮的空白窗体。一般，一个窗体程序只有一个总的 {@link QRFrame}
 * @create 2022-11-04 16:33
 **/
public class QRFrame extends JFrame implements QRComponentUpdate, QRWindowListenerAdd {
    public static final String MIN_MARK = "—";
    public static final String CLOSE_MARK = "  ╳  ";
    public static final String MAX_MARK = "❐";
    public static final int DIS = 5;
    protected final QRCloseButton closeButton;
    protected final QRButton maximumButton;
    protected final QRButton minimumButton;
    /**
     * 所继承的各主窗体所添加的控件基本都需要放在这个 {@link #mainPanel} 中
     */
    protected final QRPanel mainPanel;
    /**
     * 用于记忆窗体位置及大小信息
     */
    protected final Properties prop;
    protected final QRPanel topPanel;
    private final QRBorderContentPanel contentPane;
    private final QRLabel iconLabel;
    private final QRPanel windowFunctionPanel;
    private final ArrayList<QRParentWindowMove> childWindows = new ArrayList<>();
    private final ArrayList<QRActionRegister<Boolean>> actionOnDispose = new ArrayList<>();
    private final QRPanel titlePanel;
    /**
     * 若设置了 {@link QRSwing#windowTitleMenu} 为 {@code true}，则将自动实例化
     */
    protected QRMenuPanel titleMenuPanel;
    private QRWindowListener windowListener;
    //    private QRBackgroundBorder backgroundBorder;
    private String imagePath;
    private String title = "";
    private Image backgroundImage;
    private double imageRatio;
    private Dimension minimumSize = new Dimension(10, 10);
    private int originalWidth = 500;
    private int titlePlace = SwingConstants.LEFT;

    public QRFrame() {
        QRFileUtils.fileCreate(QRSwing.WINDOW_PROP_PATH);
        this.prop = QRPropertiesUtils.loadProp(QRSwing.WINDOW_PROP_PATH);
        setUndecorated(true);
        addWindowListener();

        //region 加载资源
        final int[] screenSize = QRSystemUtils.getScreenSize();
        int sizeWidth = QRPropertiesUtils.getPropInInteger(this.prop, "window.size.width", screenSize[0] / 2);
        int sizeHeight = QRPropertiesUtils.getPropInInteger(this.prop, "window.size.height", screenSize[1] / 2);
        int locationX = QRPropertiesUtils.getPropInInteger(this.prop, "window.start.X", screenSize[0] / 4);
        int locationY = QRPropertiesUtils.getPropInInteger(this.prop, "window.start.Y", screenSize[1] / 4);
        super.setSize(sizeWidth, sizeHeight);
        super.setLocation(locationX, locationY);
        //endregion

        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        if (QRSwing.windowIcon != null) {
            setIconImage(QRSwing.windowIcon.getImage());
        }

        this.contentPane = new QRBorderContentPanel();
        this.contentPane.setLayout(new BorderLayout());
        setContentPane(this.contentPane);
        this.contentPane.setBorder(new LineBorder(QRColorsAndFonts.BORDER_COLOR, 1));


        this.topPanel = new QRPanel();
        this.topPanel.setLayout(new BorderLayout());
        this.topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
        this.contentPane.add(this.topPanel, BorderLayout.NORTH);

        this.iconLabel = QRLabel.getIconLabel();
        this.windowFunctionPanel = new QRPanel();
        final MouseAdapte mouseAdapte = new MouseAdapte();
        this.titlePanel = new QRPanel() {
            private final Font font = QRColorsAndFonts.DEFAULT_FONT_MENU;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (title == null) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) g;
                Rectangle2D r = QRFontUtils.getStringBounds(title, font);
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                float x;
                // 设置为左或右时，相应地产生 5 的偏差。此处设置为 20 是为了拉大距离。但如果是设置为中间，则无偏差
                if (QRSwing.windowTitleMenu) {
                    if (titlePlace == SwingConstants.RIGHT) {
                        x = (float) (panelWidth - windowFunctionPanel.getWidth() - r.getWidth() - 20);
                    } else if (titlePlace == SwingConstants.CENTER) {
                        x = (float) (topPanel.getWidth() - r.getWidth()) / 2;
                    } else {
                        x = iconLabel.getWidth() + titleMenuPanel.getWidth() + 20;
                    }
                } else {
                    if (titlePlace == SwingConstants.RIGHT) {
                        x = (float) (panelWidth - windowFunctionPanel.getWidth() - r.getWidth()) - 20;
                    } else if (titlePlace == SwingConstants.CENTER) {
                        //使标题绝对居中而不顾图标与功能按钮。该行的设定，是取消添加标签而独自 drawString 的主要原因
                        x = (float) (panelWidth / 2 - r.getWidth() / 2);
                    } else {
                        x = iconLabel.getWidth() + 20;
                    }
                }
                float y = (float) (getHeight() - r.getHeight() / 2);

                //无损绘制字体，改编自 SwingUtilities2
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        this.getClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING));
                Map<TextAttribute, Object> map = new HashMap<>();
                map.put(TextAttribute.FONT, font);
                map.put(TextAttribute.FOREGROUND, QRColorsAndFonts.MENU_COLOR);
                map.put(TextAttribute.NUMERIC_SHAPING, this.getClientProperty(TextAttribute.NUMERIC_SHAPING));
                TextLayout layout = new TextLayout(title, map, g2.getFontRenderContext());
                layout.draw(g2, x, y);
            }

            @Override
            public void mouseClick(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    maxWindow();
                } else {
                    windowClickAction();
                }
            }
        };
        this.titlePanel.addMouseListener(mouseAdapte);
        this.titlePanel.addMouseMotionListener(mouseAdapte);
        this.titlePanel.setLayout(new BorderLayout());
        this.titlePanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 0, 0));
        this.topPanel.add(this.titlePanel, BorderLayout.CENTER);

        if (QRSwing.windowTitleMenu) {
            this.titleMenuPanel = new QRMenuPanel();
            QRPanel westPanel = new QRPanel();
            westPanel.setLayout(new BorderLayout());
            westPanel.add(iconLabel, BorderLayout.WEST);
            westPanel.add(this.titleMenuPanel, BorderLayout.CENTER);
            this.titlePanel.add(westPanel, BorderLayout.WEST);
        } else {
            this.titlePanel.add(this.iconLabel, BorderLayout.WEST);
        }

        this.windowFunctionPanel.setLayout(new GridLayout(1, 3, 3, 0));
        this.titlePanel.add(this.windowFunctionPanel, BorderLayout.EAST);
        this.minimumButton = new QRButton(MIN_MARK) {
            @Override
            public void componentFresh() {
                super.componentFresh();
                setFont(QRColorsAndFonts.PROCESS_BUTTON_FONT.deriveFont(13f));
            }
        };
        this.minimumButton.addClickAction(e -> minWindow());
        this.windowFunctionPanel.add(this.minimumButton);

        this.maximumButton = new QRButton(MAX_MARK) {
            @Override
            public void componentFresh() {
                super.componentFresh();
                setFont(QRColorsAndFonts.PROCESS_BUTTON_FONT.deriveFont(16f));
            }
        };
        this.maximumButton.addClickAction(e -> maxWindow());
        this.windowFunctionPanel.add(this.maximumButton);

        this.closeButton = new QRCloseButton();
        this.closeButton.setCloseButton();
        this.closeButton.setToolTipText("关闭");
        this.closeButton.addClickAction(e -> dispose());
        this.windowFunctionPanel.add(this.closeButton);

        this.contentPane.addMouseListener(mouseAdapte);
        this.contentPane.addMouseMotionListener(mouseAdapte);
        this.titlePanel.addMouseMotionListener();
        this.titlePanel.addMouseListener();


        //子类直接向 mainPanel 中添加控件
        this.mainPanel = new QRPanel();
        this.contentPane.add(this.mainPanel, BorderLayout.CENTER);
        this.contentPane.setBorder(new EmptyBorder(1, 1, 5, 1));
        setBackgroundImage(QRSwing.windowBackgroundImagePath);
        addWindowAction(QRWindowListener.TYPE.OPEN, e -> {
            this.minimumSize = getMinimumSizes();
            // 窗口打开时，设置窗口背景图片
            if (QRSwing.windowImageEnable) {
                setBackgroundImage(QRSwing.windowBackgroundImagePath);
                setBackgroundImageAlpha(QRSwing.windowBackgroundImageAlpha);
            }
        });
    }

    /**
     * 一个简单的开始：
     * <pre><code>
     * QRSwing. start("可找到的properties文件路径");
     * QRFrame window = new QRFrame("这是一个测试窗体");
     * // 设置窗体标题居中
     * setTitleCenter();
     * // 设置单击关闭按钮后窗体淡化退出并结束程序
     * setCloseButtonSystemExit();
     * // 设置窗体可见
     * window. setVisible(true);
     * </code></pre>
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
        getRootPane().setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        this.contentPane.componentFresh();
        if (!this.childWindows.isEmpty()) {
            synchronized (this.childWindows) {
                childWindows.forEach(childWindow -> {
                    if (childWindow instanceof QRComponentUpdate window) {
                        window.componentFresh();
                    }
                });
            }
        }
        mainPanel.setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.TEXT_COLOR_BACK, 5));
        if (titleMenuPanel != null) {
            if (QRSwing.windowTitleMenu) {
                titleMenuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
            } else {
                titleMenuPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
            }
        }
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
            this.windowListener.add(QRWindowListener.TYPE.OPEN, this::windowOpened);
            this.windowListener.add(QRWindowListener.TYPE.CLOSED, this::windowClosed);
            this.windowListener.add(QRWindowListener.TYPE.CLOSING, this::windowClosing);
            this.windowListener.add(QRWindowListener.TYPE.ACTIVATED, this::windowActivated);
            this.windowListener.add(QRWindowListener.TYPE.DEACTIVATED, this::windowDeactivated);
            this.windowListener.add(QRWindowListener.TYPE.ICONIFIED, this::windowIconified);
            this.windowListener.add(QRWindowListener.TYPE.DEICONIFIED, this::windowDeiconified);
            this.windowListener.addWindowMoveAction(this::windowMoved);
            addWindowListener(this.windowListener);
        }
    }

    @Override
    public final void addWindowAction(QRWindowListener.TYPE type, QRActionRegister<WindowEvent> ar) {
        if (this.windowListener != null) {
            this.windowListener.add(type, ar);
        }
    }


    @Override
    public final void addWindowMoveAction(QRActionRegister<Point> ar) {
        if (this.windowListener != null) {
            this.windowListener.addWindowMoveAction(ar);
        }
    }

    public QRButton getCloseButton() {
        return this.closeButton;
    }

    /**
     * 设置单击关闭按钮后退出程序
     */
    public void setCloseButtonSystemExit() {
        this.closeButton.addClickAction(e -> dispose(true));
    }

    /**
     * 设置窗体的标题是否居中
     */
    public void setTitleCenter() {
        setTitlePlace(SwingConstants.CENTER);
    }

    /**
     * 设置窗体标题位置
     *
     * @param p 该值有三种取法：{@link SwingConstants#LEFT}, {@link SwingConstants#CENTER} 和 {@link SwingConstants#RIGHT},
     */
    public final void setTitlePlace(int p) {
        titlePlace = p;
    }

    /**
     * 当 {@link QRSwing#windowTitleMenu} 为 {@code false} 时，可以手动调用该方法实例化并添加菜单面板到窗体
     */
    public void setTitlePanel() {
        if (!QRSwing.windowTitleMenu) {
            titleMenuPanel = new QRMenuPanel();
            titleMenuPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
            this.topPanel.add(titleMenuPanel, BorderLayout.SOUTH);
        }
    }

    public final void setBackgroundImage(String filePath) {
        if (!QRSwing.windowImageEnable || !QRFileUtils.fileExists(filePath)) {
            imagePath = null;
            this.backgroundImage = null;
            QRSwing.windowImageSet = false;
            this.contentPane.setImage(null);
            QRSwing.setWindowBackgroundImagePath(filePath);
            return;
        }
        Image imageToSet = QRSwingInfo.loadImage(filePath);
        if (imageToSet != null && !filePath.equals(this.imagePath)) {
            this.backgroundImage = imageToSet;
            imagePath = filePath;
            QRSwing.setWindowBackgroundImagePath(filePath);
            QRSwing.windowImageSet = true;
            int height = this.backgroundImage.getHeight(null);
            int width = this.backgroundImage.getWidth(null);
            this.imageRatio = (double) width / height;
            this.contentPane.setImage(this.backgroundImage);
            QRComponentUtils.componentLoopToSetOpaque(this.contentPane, false);
            QRSwing.windowTransparency = 1f;
            QRSystemUtils.setWindowNotTrans(this);
            QRComponentUtils.windowFresh(this.contentPane);
        }
    }

    public final void setBackgroundImageScale(boolean scale) {
        if (this.contentPane.image() != null) {
            this.contentPane.setScale(scale);
            QRSwing.setWindowScale(scale);
            QRComponentUtils.windowFresh(this.contentPane);
        }
    }

    public final void setBackgroundImageAlpha(float alpha) {
        if (this.contentPane.image() != null) {
            this.contentPane.setAlpha(alpha);
            QRSwing.setWindowBackgroundImageAlpha(alpha);
            QRComponentUtils.windowFresh(this.mainPanel);
        }
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
        if (QRSwing.windowImageSet) {
            QRSystemUtils.setWindowNotTrans(this);
        }
    }

    public void setMaxEnable(boolean b) {
        this.maximumButton.setEnabled(b);
    }

    public void setMinEnable(boolean b) {
        this.minimumButton.setEnabled(b);
    }

    public boolean backgroundImageSet() {
        return QRSwing.windowImageSet;
    }

    public final void dispose(boolean systemExit) {
        dispose();
        QRComponentUtils.runActions(this.actionOnDispose, systemExit);
        QRSystemUtils.setWindowCloseSlowly(this, QRSwing.windowTransparency, systemExit);
    }

    /**
     * 在窗体退出前的操作。
     * <p>当调用 {@link QRFrame#dispose(boolean)} 时，不确定 {@link QRFrame#windowClosing(WindowEvent)} 和
     * {@link QRFrame#windowClosed(WindowEvent)} 是否起作用
     * <p>但本方法一定起作用
     *
     * @param ar 其参数 {@link QRActionRegister#action(Object)} 为 {@code Boolean} 值
     */
    public final void addActionBeforeDispose(QRActionRegister<Boolean> ar) {
        this.actionOnDispose.add(ar);
    }

    public Dimension getMinimumSizes() {
        int titleWidth = this.iconLabel.getWidth() + QRFontUtils.getTextInWidth(this.titlePanel, this.title) + this.windowFunctionPanel.getWidth() + 20;
        return new Dimension(titleWidth, titleWidth / 2);
    }

    public void setCursorWait() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void setCursorDefault() {
        setCursor(Cursor.getDefaultCursor());
    }

    public int getTitlePlace() {
        return titlePlace;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        this.title = title;
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
        windowSizeChangedAction();
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        if (windowListener == null) {
            return;
        }
        Point point = new Point(x, y);
        this.windowListener.windowMoved(point);
    }

    /**
     * 该方法<b>并不会</b>关闭窗体。
     * <p>子类继承，用于自动保存窗体大小和位置信息。
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
                } else if (QRSwing.windowScreenAdsorb) {
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

    protected void childWindowLocationUpdate(Point location) {
        synchronized (this.childWindows) {
            for (QRParentWindowMove childWindow : this.childWindows) {
                if (((Window) childWindow).isVisible()) {
                    childWindow.ownerMoved(location);
                }
            }
        }
    }

    public void addChildWindow(QRParentWindowMove w) {
        this.childWindows.add(w);
    }

    public void removeChildWindow(QRParentWindowMove w) {
        this.childWindows.remove(w);
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

    /**
     * 已自动添加监听器，可直接重写
     */
    public void windowMoved(Point p) {
    }

    //region 取得监听器
    public QRWindowListener windowListener() {
        return this.windowListener;
    }
    //endregion

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
        public void mouseExited(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            windowClickAction();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.p = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            clear();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            childWindowLocationUpdate(getLocation());
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
            QRSwing.windowImageSet = QRFrame.this.backgroundImage != null && QRFrame.this.contentPane.scale();
            if (this.upAndLeft) {
                if (QRSwing.windowImageSet) {
                    eXOnScreen = (int) Math.min(eXOnScreen, eYOnScreen * QRFrame.this.imageRatio);
                    eYOnScreen = (int) (eXOnScreen / QRFrame.this.imageRatio);
                }
                setBounds(eXOnScreen, eYOnScreen, this.width + this.pressPointX - eXOnScreen,
                        this.height + this.pressPointY - eYOnScreen);
                setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
            } else if (this.upAndRight) {
                if (QRSwing.windowImageSet) {
                    eYOnScreen = (int) (eXOnScreen / QRFrame.this.imageRatio);
                }
                int height = this.height + this.pressPointY - eYOnScreen;
                setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                setBounds(getX(), eYOnScreen, eX, height);
            } else if (this.downAndLeft) {
                int width = this.width + this.pressPointX - eXOnScreen;
                if (QRSwing.windowImageSet) {
                    eXOnScreen = (int) Math.min(eXOnScreen, eYOnScreen * QRFrame.this.imageRatio);
                    width = this.width + this.pressPointX - eXOnScreen;
                    eY = (int) (width / QRFrame.this.imageRatio);
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                setBounds(eXOnScreen, getY(), width, eY);
            } else if (this.downAndRight) {
                if (QRSwing.windowImageSet) {
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
                if (QRSwing.windowImageSet) {
                    width = (int) (height * QRFrame.this.imageRatio);
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                setBounds(getX(), eYOnScreen, width, height);
            } else if (this.left) {
                int height = this.height;
                final int width = this.width + this.pressPointX - eXOnScreen;
                if (QRSwing.windowImageSet) {
                    height = (int) (width / QRFrame.this.imageRatio);
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                setBounds(eXOnScreen, getY(), width, height);
            } else if (this.down) {
                if (QRSwing.windowImageSet) {
                    this.width = (int) (QRFrame.this.imageRatio * eY);
                    eY = (int) (this.width / QRFrame.this.imageRatio);
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                setSize(this.width, eY);
            } else if (this.right) {
                if (QRSwing.windowImageSet) {
                    this.height = (int) (eX / QRFrame.this.imageRatio);
                    eX = (int) (QRFrame.this.imageRatio * this.height);
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                setSize(eX, this.height);
            } else {
                //只能在窗体的标题栏进行移动
                if (eY < QRFrame.this.topPanel.getHeight()) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    if (this.p == null) {
                        return;
                    }
                    int x = e.getXOnScreen() - this.p.x;
                    int y = e.getYOnScreen() - this.p.y;
                    setLocation(x, y);
                }
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
}
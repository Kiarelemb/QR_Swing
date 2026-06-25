package swing.qr.kiarelemb.window.basic;

import method.qr.kiarelemb.utils.QRStringUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.assembly.QRWindowMouseAdapter;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.combination.QRBorderContentPanel;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRParentWindowMove;
import swing.qr.kiarelemb.inter.listener.add.QRWindowListenerAdd;
import swing.qr.kiarelemb.listener.QRWindowListener;
import swing.qr.kiarelemb.listener.QRWindowListener.TYPE;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRCloseButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import static swing.qr.kiarelemb.theme.QRColorsAndFonts.BORDER_COLOR;
import static swing.qr.kiarelemb.theme.QRColorsAndFonts.LINE_COLOR;

/**
 * QR Swing 的基础对话框窗口。
 *
 * <p>该类基于 {@link JDialog}，提供无系统边框的自定义标题栏、关闭按钮、
 * 主题刷新、窗口透明度/圆角处理、ESC 关闭、窗口事件注册以及可选的边缘拖拽缩放能力。
 *
 * <p>子类通常只需要向 {@link #mainPanel} 添加内容，并按需重写窗口事件方法或关闭行为。
 * 如果该对话框由 {@link QRFrame} 打开，显示时会自动注册为父窗口的子窗口，
 * 以支持父窗口移动时跟随。
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-21 18:51
 */
public class QRDialog extends JDialog implements QRParentWindowMove, QRComponentUpdate, QRWindowListenerAdd {
    public final static int LEFT = SwingConstants.LEFT;
    public final static int CENTER = SwingConstants.CENTER;
    protected final QRPanel contentPane;
    /**
     * 自带的主面板，默认布局为 {@code null}
     */
    protected final QRPanel mainPanel;
    protected final QRCloseButton closeButton;
    protected final Window parent;
    protected final QRPanel topPanel;
    private final QRLabel titleLabel;
    private boolean parentWindowFollowMove = true;
    private boolean resizable = false;
    private QRWindowListener windowListener;
    protected final QRActionRegister<KeyStroke> disposeAction;
    /**
     * 该字段用于设置窗体打开时，是否遍历组件，为 <code>true</code> 则设置组件透明
     */
    protected boolean toSetOpaque = true;

    private class MouseAdapte extends QRWindowMouseAdapter {
        @Override
        protected void windowMoved(int x, int y) {
            if (parentWindowFollowMove && parent != null) {
                //居中
                int px = x + (getWidth() - parent.getWidth()) / 2;
                int py = y + (getHeight() - parent.getHeight()) / 2;
                parent.setLocation(px, py);
                if (parent instanceof QRFrame) {
                    Point p = new Point(px, py);
                    ((QRFrame) parent).childWindowLocationUpdate(p);
                }
            }
        }

        @Override
        protected Window window() {
            return QRDialog.this;
        }

        @Override
        protected int moveAreaHeight() {
            return QRDialog.this.titleLabel.getHeight();
        }

        @Override
        protected boolean resizable() {
            return QRDialog.this.resizable;
        }

        @Override
        protected void setCursorDefault() {
            QRDialog.this.setCursorDefault();
        }

        @Override
        protected QRPanel mainPanel() {
            return QRDialog.this.mainPanel;
        }
    }

    /**
     * 创建一个默认标题居中、窗体大小不可调整、禁用父窗体的对话框，
     * <p>使用方法：
     * <pre>
     * {@code
     * super(parent);
     * setTitle("");
     * setTitlePlace(QRDialog.CENTER);
     * setSize(400, 300);
     * }</pre>
     * <p><code>mainPanel</code> 是已自带的主面板，默认布局为 {@code null}
     *
     * @param parent 父窗体
     */
    public QRDialog(Window parent) {
        this(parent, true);
    }

    /**
     * 创建一个默认标题居中、窗体大小不可调整、可用父窗体的对话框，
     * <p>使用方法：
     * <pre>{@code
     * super(parent);
     * setTitle("");
     * setTitlePlace(QRDialog.CENTER);
     * setSize(400, 300);
     * }</pre>
     * <p><code>mainPanel</code> 是已自带的主面板，默认布局为 {@code null}
     *
     * @param parent       父窗体
     * @param parentUnable 是否禁用父窗体
     */
    public QRDialog(Window parent, boolean parentUnable) {
        super(parent, null, parentUnable ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        this.parent = parent;
        if (QRSwing.windowIcon != null) {
            setIconImage(QRSwing.windowIcon.getImage());
        }
        setUndecorated(true);
        this.contentPane = new QRBorderContentPanel();
        this.contentPane.setLayout(new BorderLayout(5, 5));
        this.contentPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        setContentPane(this.contentPane);

        this.topPanel = new QRPanel();
        this.topPanel.setLayout(new BorderLayout());
        this.topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR));
        this.contentPane.add(this.topPanel, BorderLayout.NORTH);

        MouseAdapte adapte = new MouseAdapte();

        QRPanel titlePanel = new QRPanel(new BorderLayout(2, 0));
        this.topPanel.add(titlePanel, BorderLayout.CENTER);

        QRLabel iconLabel = QRLabel.getIconLabel();
        titlePanel.add(iconLabel, BorderLayout.WEST);

        this.titleLabel = new QRLabel();
        this.titleLabel.setHorizontalAlignment(CENTER);
        this.titleLabel.setForeground(QRColorsAndFonts.MENU_COLOR);
        titlePanel.add(this.titleLabel, BorderLayout.CENTER);

        this.closeButton = new QRCloseButton();
        this.closeButton.setToolTipText("关闭");
        this.closeButton.addClickAction(e -> dispose());
        titlePanel.add(this.closeButton, BorderLayout.EAST);

        this.mainPanel = new QRPanel(null);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        this.contentPane.add(this.mainPanel, BorderLayout.CENTER);

        titlePanel.addMouseListener(adapte);
        titlePanel.addMouseMotionListener(adapte);
        this.contentPane.addMouseListener(adapte);
        this.contentPane.addMouseMotionListener(adapte);
        this.contentPane.setBorder(new EmptyBorder(1, 1, 5, 1));
        this.disposeAction = e -> {
            if (QRDialog.this.isFocused()) {
                QRDialog.this.dispose();
            }
        };

        addWindowListener();
        addWindowAction(TYPE.OPEN, e -> {
//            if (toSetOpaque) QRComponentUtils.componentLoopToSetOpaque(this.contentPane, false);
        });
    }

    /**
     * 关闭父窗口移动跟随。
     *
     * <p>默认情况下，父窗口移动时对话框会随之移动；调用该方法后，
     * 对话框会保持自身位置，不再跟随父窗口。
     */
    public void setParentWindowNotFollowMove() {
        this.parentWindowFollowMove = false;
    }

    /**
     * 对话框在打开时自动设置所有控件不透明。启用此设置，可以不设置窗体透明度，而使各控件照旧
     */
    public void setComponentsOpaqueDefault() {
        toSetOpaque = false;
    }

    private void windowStateUpdate() {
        if (QRSwing.windowRound) {
            QRSystemUtils.setWindowRound(this);
        } else {
            QRSystemUtils.setWindowNotRound(this);
        }
        QRSystemUtils.setWindowTrans(this, QRSwing.windowTransparency);
    }

    /**
     * 按当前主题刷新对话框及其内容区域的颜色和边框。
     */
    @Override
    public void componentFresh() {
        this.contentPane.componentFresh();
        this.topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR));
        this.titleLabel.setForeground(QRColorsAndFonts.MENU_COLOR);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
    }

    /**
     * 安装 QR Swing 统一的窗口监听器。
     *
     * <p>该监听器会把 Swing 原生窗口事件转发到本类的 protected 回调方法，
     * 并额外支持窗口移动事件注册。
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

    @Override
    public void ownerMoved(Point parentWindowLocation) {
    }

    @Override
    public final void setTitle(String title) {
        this.titleLabel.setText(QRStringUtils.A_WHITE_SPACE.concat(title));
    }

    /**
     * 显示或隐藏对话框。
     *
     * <p>显示时会相对父窗口居中，并在父窗口是 {@link QRFrame} 时注册为子窗口；
     * 同时注册 ESC 全局快捷键用于关闭对话框。隐藏时会移除该快捷键。
     *
     * @param b {@code true} 表示显示，{@code false} 表示隐藏
     */
    @Override
    public void setVisible(boolean b) {
        if (b) {
            setLocationRelativeTo(this.parent);
            if (this.parent != null && this.parent instanceof QRFrame frame) {
                frame.addChildWindow(this);
            }
        }
        if (b) {
            QRSwing.registerGlobalAction(KeyEvent.VK_ESCAPE, this.disposeAction, false);
        } else {
            QRSwing.registerGlobalActionRemove(QRStringUtils.getKeyStroke(KeyEvent.VK_ESCAPE), this.disposeAction, false);
        }
        super.setVisible(b);
    }

    /**
     * 设置对话框是否允许通过边缘拖拽改变大小。
     *
     * @param resizable {@code true} 表示允许拖拽缩放
     */
    @Override
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    /**
     * 设置对话框大小。
     *
     * <p>为了避免拖拽缩放时窗口过小导致标题栏和内容区域不可用，
     * 宽高小于内部最小值时会忽略本次设置。
     */
    @Override
    public void setSize(int width, int height) {
        final int minSize = 20;
        if (width < minSize || height < minSize) {
            return;
        }
        super.setSize(width, height);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        windowStateUpdate();
    }

    /**
     * 设置对话框位置，并向窗口监听器发送移动事件。
     */
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
     * 释放对话框资源。
     *
     * <p>释放前会移除 ESC 全局关闭快捷键，并从父 {@link QRFrame} 的子窗口列表中注销。
     */
    @Override
    public void dispose() {
        QRSwing.registerGlobalActionRemove(QRStringUtils.getKeyStroke(KeyEvent.VK_ESCAPE), this.disposeAction, false);
        if (parent instanceof QRFrame frame) {
            frame.removeChildWindow(this);
        }
        super.dispose();
    }

    public void setCursorWait() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void setCursorDefault() {
        setCursor(Cursor.getDefaultCursor());
    }

    /**
     * 设置标题文本在标题栏中的水平对齐方式。
     *
     * @param p 可使用 {@link SwingConstants#LEFT}、{@link SwingConstants#CENTER}
     *          或 {@link SwingConstants#RIGHT}
     */
    public final void setTitlePlace(int p) {
        this.titleLabel.setHorizontalAlignment(p);
    }

    /**
     * 设置右上角关闭按钮是否可用。
     *
     * @param enable {@code true} 表示启用关闭按钮
     */
    protected void setCloseButtonEnable(boolean enable) {
        this.closeButton.setEnabled(enable);
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

    public void windowMoved(Point p) {
    }

    //region 取得监听器
    public QRWindowListener getWindowListener() {
        return this.windowListener;
    }
    //endregion
}
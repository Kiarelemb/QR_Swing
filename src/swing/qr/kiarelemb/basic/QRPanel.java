package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.data.QRInternalScrollBarData;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRInternalScrollbarUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRFocusListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseWheelListenerAdd;
import swing.qr.kiarelemb.listener.QRFocusListener;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.listener.QRMouseWheelListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 面板类
 * @create 2022-11-04 16:29
 **/
public class QRPanel extends JPanel implements QRComponentUpdate, QRMouseMotionListenerAdd, QRFocusListenerAdd, QRMouseListenerAdd, QRMouseWheelListenerAdd, QRInternalScrollbarUpdate {

    private QRMouseMotionListener mouseMotionListener;
    private QRMouseListener mouseListener;
    private QRFocusListener focusListener;
    private QRMouseWheelListener mouseWheelListener;
    private QRScrollPane scrollPane;
    protected QRInternalScrollPane internalScrollPane;
    private QRInternalScrollBarData data;

    public QRPanel() {
        setOpaque(false);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
    }

    public QRPanel(boolean opaque) {
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        setOpaque(opaque);
    }

    public QRPanel(LayoutManager layout) {
        super(layout);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
    }

    public QRPanel(boolean opaque, LayoutManager layout) {
        this(opaque);
        setLayout(layout);
    }

    public void setPreferredSize(int width, int height) {
        super.setPreferredSize(new Dimension(width, height));
    }

    public void setCursorWait() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void setCursorDefault() {
        setCursor(Cursor.getDefaultCursor());
    }

    public void setCursorHand() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * 添加鼠标移动事件
     */
    @Override
    public final void addMouseMotionListener() {
        if (this.mouseMotionListener == null) {
            this.mouseMotionListener = new QRMouseMotionListener();
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.DRAG, e -> mouseDrag((MouseEvent) e));
            this.mouseMotionListener.add(QRMouseMotionListener.TYPE.MOVE, e -> mouseMove((MouseEvent) e));
            addMouseMotionListener(this.mouseMotionListener);
        }
    }

    /**
     * 添加鼠标移动事件
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister ar) {
        if (this.mouseMotionListener != null) {
            this.mouseMotionListener.add(type, ar);
        }
    }

    /**
     * 添加鼠标事件
     */
    @Override
    public final void addMouseListener() {
        if (this.mouseListener == null) {
            this.mouseListener = new QRMouseListener();
            this.mouseListener.add(QRMouseListener.TYPE.CLICK, e -> mouseClick((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.PRESS, e -> mousePress((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.RELEASE, e -> mouseRelease((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.ENTER, e -> mouseEnter((MouseEvent) e));
            this.mouseListener.add(QRMouseListener.TYPE.EXIT, e -> mouseExit((MouseEvent) e));
            addMouseListener(this.mouseListener);
        }
    }

    /**
     * 添加鼠标事件
     *
     * @param type 类型
     * @param ar   操作
     */
    @Override
    public final void addMouseAction(QRMouseListener.TYPE type, QRActionRegister ar) {
        if (this.mouseListener != null) {
            this.mouseListener.add(type, ar);
        }
    }

    /**
     * 添加鼠标滚轮事件
     */
    public void addMouseWheelListener() {
        if (this.mouseWheelListener == null) {
            this.mouseWheelListener = new QRMouseWheelListener();
            this.mouseWheelListener.add(e -> mouseWheel((MouseWheelEvent) e));
            addMouseWheelListener(this.mouseWheelListener);
        }
    }

    @Override
    public void addMouseWheelAction(QRActionRegister ar) {
        this.mouseWheelListener.add(ar);
    }

    /**
     * 添加鼠标滚轮事件
     *
     * @param ar 操作
     */
    public void addMouseWheelListener(QRActionRegister ar) {
        if (this.mouseWheelListener != null) {
            this.mouseWheelListener.add(ar);
        }
    }

    /**
     * 添加焦点事件
     */
    @Override
    public void addFocusListener() {
        if (this.focusListener == null) {
            this.focusListener = new QRFocusListener();
            this.focusListener.add(QRFocusListener.TYPE.GAIN, e -> this.focusGained((FocusEvent) e));
            this.focusListener.add(QRFocusListener.TYPE.LOST, e -> this.focusLost((FocusEvent) e));
            addFocusListener(this.focusListener);
        }
    }

    /**
     * 添加焦点事件
     */
    @Override
    public void addFocusAction(QRFocusListener.TYPE type, QRActionRegister ar) {
        if (this.focusListener != null) {
            this.focusListener.add(type, ar);
        }
    }


    /**
     * 添加滚动条
     *
     * @return 滚动条本身
     */
    public QRScrollPane addScrollPane() {
        if (internalScrollPane != null) {
            throw new UnsupportedOperationException("只能添加一个 ScrollPane");
        }
        if (this.scrollPane == null) {
            this.scrollPane = new QRScrollPane();
            this.scrollPane.setViewportView(this);
            // 单次滚动 150
            this.scrollPane.setScrollSmoothly(15);
            return this.scrollPane;
        }
        return this.scrollPane;
    }

    public QRInternalScrollPane addInternalScrollbar() {
        if (scrollPane != null) {
            throw new UnsupportedOperationException("只能添加一个 ScrollPane");
        }
        if (this.internalScrollPane == null) {
            this.internalScrollPane = new QRInternalScrollPane(this);
        }
        return this.internalScrollPane;
    }

    @Override
    public QRInternalScrollBarData getScrollBarData() {
        if (data == null) {
            data = new QRInternalScrollBarData();
        }
        return data;
    }

    @Override
    public void scrollBarValueUpdate() {
        data.size = getSize();
        data.location = getLocation();
        data.parentSize = getParent().getSize();
        ////// 计算纵向值
        // 父容器高度
        double ph = data.parentSize.getHeight();
        // 文本面板高度
        int height = data.size.height;
        // 滚动条高度
        data.sh = ph * ph / height;
        data.maxY = ph - data.sh;
        // 滚动条纵向的位置范围：[0, ph - sh]
        data.sy = -data.location.y * data.maxY / (height - ph);

        data.verticalScrollbarVisible = (int) ph != (int) data.sh;
        ////// 计算横向值
        double pw = data.parentSize.getWidth();
        int width = data.size.width;
        data.sw = pw * pw / width;
        data.maxX = pw - data.sw;
        data.sx = -data.location.x * data.maxX / (width - pw);
        data.horizontalScrollbarVisible = (int) pw != (int) data.sw;
    }

    /**
     * 重写前请先调用 {@link #addMouseMotionListener()}
     */
    protected void mouseDrag(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseMotionListener()}
     */
    protected void mouseMove(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mouseClick(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mousePress(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mouseRelease(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mouseEnter(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseListener()}
     */
    protected void mouseExit(MouseEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addMouseWheelListener()}
     */
    protected void mouseWheel(MouseWheelEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addFocusListener()}
     */
    protected void focusGained(FocusEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addFocusListener()}
     */
    protected void focusLost(FocusEvent e) {
    }

    /**
     * 遍历组件数组，对每个组件执行不同的操作取决于它是否是 {@link QRPanel} 的实例。
     *
     * @param panel           容器面板，从中获取组件数组进行遍历。
     * @param isQRPanelAction 可为 {@code null}，如果组件是 {@link QRPanel} 的实例，将调用此操作接口，该操作参数是 {@link QRPanel}
     */
    public static void componentLoop(QRPanel panel, QRActionRegister isQRPanelAction) {
        QRComponentUtils.componentLoop(panel, QRPanel.class, isQRPanelAction, null);
    }


    /**
     * 遍历组件数组，对每个组件执行不同的操作取决于它是否是 {@link QRPanel} 的实例。
     *
     * @param panel           容器面板，从中获取组件数组进行遍历。
     * @param isQRPanelAction 可为 {@code null}，如果组件是 {@link QRPanel} 的实例，将调用此操作接口，该操作参数是 {@link QRPanel}
     * @param elseAction      可为 {@code null}，如果组件不是 {@link QRPanel} 的实例，将调用此操作接口，该操作参数是 {@link Component}
     */
    public static void componentLoop(QRPanel panel, QRActionRegister isQRPanelAction, QRActionRegister elseAction) {
        QRComponentUtils.componentLoop(panel, QRPanel.class, isQRPanelAction, elseAction);
    }

    @Override
    public void componentFresh() {
        componentLoop(this, e -> ((QRPanel) e).componentFresh(), e -> {
            if (e instanceof QRComponentUpdate com) {
                com.componentFresh();
            }
        });
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
    }

    @Override
    public Component getComponent(int n) {
        final int components = getComponentCount();
        if (n >= components) {
            return this.getComponent(components - 1);
        }
        Component component;
        try {
            component = super.getComponent(n);
        } catch (Exception ignore) {
            return super.getComponent(components - 1);
        }
        return component;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // 用于绘制内置滚动条
        if (internalScrollPane == null) {
            return;
        }
        scrollBarValueUpdate();
        if (!data.verticalScrollbarVisible && !data.horizontalScrollbarVisible) {
            return;
        }

        g.translate(getWidth() - 10, -data.location.y);
        g.setColor(QRColorsAndFonts.SCROLL_COLOR);
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.addRenderingHints(rh);
        if (data.verticalScrollbarVisible) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, data.mousePressedVertical ? 1f : (data.mouseEnteredVertical ? 0.8f : 0.6f)));
            g2.fillRect(0, (int) data.sy, 10, (int) data.sh);
        }
        if (data.horizontalScrollbarVisible) {
            g2.translate(0, data.size.height - 10);
            g2.setColor(QRColorsAndFonts.SCROLL_COLOR);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, data.mousePressedHorizontal ? 1f : (data.mouseEnteredHorizontal ? 0.8f : 0.6f)));
            g2.fillRect((int) data.sx, 0, (int) data.sw, 10);
        }
    }
}
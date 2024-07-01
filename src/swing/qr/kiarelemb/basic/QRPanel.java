package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.listener.QRFocusListener;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRFocusListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 面板类
 * @create 2022-11-04 16:29
 **/
public class QRPanel extends JPanel implements QRComponentUpdate, QRMouseMotionListenerAdd, QRFocusListenerAdd, QRMouseListenerAdd {

    private QRMouseMotionListener mouseMotionListener;
    private QRMouseListener mouseListener;
    private QRFocusListener focusListener;
    private QRScrollPane scrollPane;

    public QRPanel() {
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
    }

    public QRPanel(boolean opaque) {
        this();
        setOpaque(opaque);
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
        if (this.scrollPane == null) {
            this.scrollPane = new QRScrollPane();
            this.scrollPane.setViewportView(this);
            // 单次滚动 150
            this.scrollPane.setScrollSmoothly(15);
            return this.scrollPane;
        }
        return this.scrollPane;
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
}
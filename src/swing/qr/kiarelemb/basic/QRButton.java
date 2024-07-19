package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.combination.QRMenuButton;
import swing.qr.kiarelemb.combination.QRMenuButtonOriginal;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRActionListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.listener.QRActionListener;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 按扭
 * @create 2022-11-04 16:13
 **/
public class QRButton extends JButton implements QRComponentUpdate, QRActionListenerAdd, QRMouseMotionListenerAdd,
        QRMouseListenerAdd {
    //	protected QRButtonMouseListener bml = new QRButtonMouseListener(this, QRColorsAndFonts.ENTER_COLOR,
//			QRColorsAndFonts.PRESS_COLOR);
    protected final QRActionRegister actionRegister = e -> this.clickInvokeLater();
    private QRMouseMotionListener mouseMotionListener;
    private QRMouseListener mouseListener;
    private QRActionListener clickListener;
    protected Color defaultBackColor = QRColorsAndFonts.FRAME_COLOR_BACK;
    protected Color enterColor = QRColorsAndFonts.ENTER_COLOR;
    protected Color pressColor = QRColorsAndFonts.PRESS_COLOR;

    public QRButton() {
        this(null);
    }

    public QRButton(String text) {
        setFocusPainted(false);
        setBorderPainted(false);
        setBorder(null);
        setOpaque(false);
        addActionListener();
        setText(text);
        componentFresh();

        mouseListenerInit();
    }

    private boolean mouseEntered = false;
    private boolean mousePressed = false;

    private void mouseListenerInit() {
        addMouseListener();
        addMouseAction(QRMouseListener.TYPE.ENTER, e -> mouseEntered = true);
        addMouseAction(QRMouseListener.TYPE.EXIT, e -> mouseEntered = false);
        addMouseAction(QRMouseListener.TYPE.RELEASE, e -> mousePressed = false);
        addMouseAction(QRMouseListener.TYPE.PRESS, e -> mousePressed = true);
    }

    public QRButton(String text, String toolTipText) {
        this(text);
        setToolTipText(toolTipText);
    }

    //region 各种添加

    /**
     * 给按钮添加单击事件，在实例化时已自动添加
     */
    @Override
    public void addActionListener() {
        if (this.clickListener == null) {
            this.clickListener = new QRActionListener();
            this.clickListener.add(e -> actionEvent((ActionEvent) e));
            addActionListener(this.clickListener);
        }
    }

    /**
     * 添加单击事件
     *
     * @param ar 操作
     */
    @Override
    public final void addClickAction(QRActionRegister ar) {
        if (this.clickListener != null) {
            this.clickListener.add(ar);
        }
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
     * 添加鼠标事件，请先调用 {@link #addMouseListener()}
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
    //endregion

    //region 按钮颜色设置
    public void setEnterColor(Color enterColor) {
        this.enterColor = enterColor;
//		this.bml.setEnterColor(enterColor);
    }

    public void setPressColor(Color pressColor) {
        this.pressColor = pressColor;
//		this.bml.setPressColor(pressColor);
    }

    public void setBackColor(Color defaultBackColor) {
        this.defaultBackColor = defaultBackColor;
//		this.bml.setBackColor(defaultBackColor);
    }
    //endregion

    //region 各种重写

    /**
     * 已自动添加监听器，可直接重写
     */
    protected void actionEvent(ActionEvent o) {
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

    //endregion

    public void add(QRMenuItem qmi) {
        if (!(this instanceof QRMenuButton) && !(this instanceof QRMenuButtonOriginal)) {
            throw new IllegalStateException("该方法只为菜单按钮而设立！");
        }
    }

    /**
     * 本方法绕过鼠标点击的模拟，直接运行 {@link #clickListener} 中的 {@link QRActionListener#actionPerformed(ActionEvent)}
     * 方法。这就意味着，只有本类中的 {@link #actionEvent(ActionEvent)} 和调用了 {@link #addClickAction(QRActionRegister)} 中的事件将被触发
     * <p>需要注意的是，若运行的事件中大量包含界面 UI 的绘制，那本方法可能比 {@link #click()} 更合适</p>
     */
    public void clickInvokeLater() {
        SwingUtilities.invokeLater(this::click);
    }

    /**
     * 本方法绕过鼠标点击的模拟，直接运行 {@link #clickListener} 中的 {@link QRActionListener#actionPerformed(ActionEvent)}
     * 方法。这就意味着，只有本类中的 {@link #actionEvent(ActionEvent)} 和调用了 {@link #addClickAction(QRActionRegister)} 中的事件将被触发
     */
    public void click() {
        clickListener.actionPerformed(null);
    }

    /**
     * 推荐使用本类中的 {@link #addClickAction(QRActionRegister)} 方法
     */
    @Deprecated()
    @Override
    public void addActionListener(ActionListener l) {
        super.addActionListener(l);
    }

    /**
     * 已被本类中的 {@link #click()} 方法取代
     */
    @Deprecated()
    @Override
    public void doClick() {
        super.doClick();
    }

    public QRActionRegister actionRegister() {
        return actionRegister;
    }

    @Override
    public JToolTip createToolTip() {
        QRToolTip tip = new QRToolTip();
        tip.setComponent(tip);
        return tip;
    }

    @Override
    public void componentFresh() {
        setForeground(QRColorsAndFonts.MENU_COLOR);
        setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        if ((mouseEntered || mousePressed) && isEnabled()) {
//            if(cl)
            g.setColor(mousePressed ? pressColor : enterColor);
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.addRenderingHints(rh);
            // 使滚动条透明
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, QRSwing.windowImageSet ? (mousePressed ? 1f : (mouseEntered ? 0.7f : 0.5f)) : 1f));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        super.paint(g);
    }
}
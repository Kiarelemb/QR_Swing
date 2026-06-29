package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRActionListenerAdd;
import swing.qr.kiarelemb.listener.QRActionListener;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * QR Swing 的主题复选框。
 *
 * <p>该类基于 {@link JCheckBox}，使用 QR Swing 内置图片资源绘制选中、未选中、禁用、
 * 悬停和按下状态，并通过 {@link QRActionListener} 统一分发点击动作。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRCheckBox top = new QRCheckBox("窗口置顶", QRSwing.windowAlwaysOnTop);
 * top.addClickAction(event -> QRSwing.setWindowAlwaysOnTop(top.isSelected()));
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-21 18:44
 **/
public class QRCheckBox extends JCheckBox implements QRComponentUpdate, QRActionListenerAdd {

    private QRActionListener clickListener;

    public QRCheckBox() {
        setOpaque(false);
        addActionListener();
        componentFresh();

        ImageIcon selectedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_selected.png"));
        ImageIcon notSelectedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_not_selected.png"));
        ImageIcon pressedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_pressed.png"));
        ImageIcon disabledIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_disable.png"));
        ImageIcon disabledSelectedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_disable_selected.png"));
        ImageIcon overIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_over.png"));
        ImageIcon overSelectedIcon = new ImageIcon(QRSwingInfo.loadUrl("check_box_over_selected.png"));
        setIcon(notSelectedIcon);
        setSelectedIcon(selectedIcon);
        setDisabledIcon(disabledIcon);
        setDisabledSelectedIcon(disabledSelectedIcon);
        setPressedIcon(pressedIcon);
        setRolloverIcon(overIcon);
        setRolloverSelectedIcon(overSelectedIcon);
    }

    public QRCheckBox(String text) {
        this();
        setText(text);
    }

    public QRCheckBox(String text, boolean selected) {
        this(text);
        setSelected(selected);
    }

    public QRCheckBox(String text, boolean selected, boolean enabled) {
        this(text, selected);
        setEnabled(enabled);
    }

    //region 取得监听器
    /**
     * 取得内部点击监听器。
     *
     * <p>通常不需要直接操作监听器，外部注册点击动作请使用 {@link #addClickAction(QRActionRegister)}。</p>
     *
     * @return 点击监听器
     */
    public QRActionListener getClickListener() {
        return this.clickListener;
    }
    //endregion


    /**
     * 安装内部点击监听器，在实例化时已自动添加。
     */
    @Override
    public void addActionListener() {
        if (this.clickListener == null) {
            this.clickListener = new QRActionListener();
            this.clickListener.add(this::actionEvent);
            addActionListener(this.clickListener);
        }
    }

    /**
     * 添加点击动作。
     *
     * <p>动作会在复选框状态已经切换后执行，因此回调里可以直接读取 {@link #isSelected()}。</p>
     *
     * @param ar 操作
     */
    @Override
    public final void addClickAction(QRActionRegister<ActionEvent> ar) {
        if (this.clickListener == null) {
            addActionListener();
        }
        if (this.clickListener != null) {
            this.clickListener.add(ar);
        }
    }

    /**
     * 点击回调，子类可直接重写。
     *
     * <p>外部调用方通常使用 {@link #addClickAction(QRActionRegister)}。</p>
     */
    protected void actionEvent(ActionEvent o) {
    }

    @Override
    public JToolTip createToolTip() {
        QRToolTip tip = new QRToolTip();
        tip.setComponent(tip);
        return tip;
    }

    @Override
    public void componentFresh() {
        setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
        setForeground(QRColorsAndFonts.MENU_COLOR);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
    }
}

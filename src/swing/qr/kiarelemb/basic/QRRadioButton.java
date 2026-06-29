package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRActionListenerAdd;
import swing.qr.kiarelemb.listener.QRActionListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * QR Swing 的主题单选按钮。
 *
 * <p>该类基于 {@link JRadioButton}，统一主题字体和颜色，并使用
 * {@link QRActionListener} 分发点击动作。通常与 {@link ButtonGroup} 或
 * {@link swing.qr.kiarelemb.assembly.QRButtonGroup} 配合使用。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRRadioButton light = new QRRadioButton("浅色", true);
 * QRRadioButton dark = new QRRadioButton("深色");
 * ButtonGroup group = new ButtonGroup();
 * group.add(light);
 * group.add(dark);
 * dark.addClickAction(event -> QRSwing.setTheme("深色"));
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2023-01-09 16:52
 **/
public class QRRadioButton extends JRadioButton implements QRComponentUpdate, QRActionListenerAdd {
    private QRActionListener clickListener;

    public QRRadioButton(String text) {
        super(text);
        addActionListener();
        componentFresh();
    }

    public QRRadioButton(String text, boolean selected) {
        this(text);
        setSelected(selected);
    }

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
     * <p>动作会在单选按钮状态更新后执行，可直接读取 {@link #isSelected()}。</p>
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
     */
    protected void actionEvent(ActionEvent o) {
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        int w = QRFontUtils.getTextInWidth(this, getText()) + 35;
        super.setBounds(x, y, w, height);
    }

    //region 取得监听器
    public QRActionListener clickListener() {
        return this.clickListener;
    }
    //endregion

    @Override
    public JToolTip createToolTip() {
        QRToolTip tip = new QRToolTip();
        tip.setComponent(tip);
        return tip;
    }

    @Override
    public void componentFresh() {
        setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
        setForeground(QRColorsAndFonts.MENU_COLOR);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
    }
}

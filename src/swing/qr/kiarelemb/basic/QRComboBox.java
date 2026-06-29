package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRStringUtils;
import method.qr.kiarelemb.utils.QRTimeCountUtil;
import swing.qr.kiarelemb.assembly.QRBasicComboBoxUI;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.event.QRItemEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.listener.QRItemListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * QR Swing 的字符串下拉框。
 *
 * <p>该类基于 {@link JComboBox}，统一了主题样式、自定义下拉 UI、文本读取/设置、
 * item 变化事件封装，以及数字/小数输入过滤。组件的模型固定使用字符串项，
 * 如需显示对象建议在调用端先转换为展示文本。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRComboBox box = new QRComboBox("浅色", "深色");
 * box.addItemChangeListener(event -> QRSwing.setTheme(event.after()));
 *
 * QRComboBox sizeBox = new QRComboBox(true);
 * sizeBox.setModel("12", "14", "16");
 * sizeBox.setText("18"); // 若 model 中没有 18，会自动加入并选中
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-21 18:45
 **/
public class QRComboBox extends JComboBox<String> implements QRComponentUpdate {

    private final StringBuilder allowInputChar = new StringBuilder();
    private boolean itemChangeLock = false;
    private String preValue = null;
    private QRItemListener itemChangedListener;
    public final QRBasicComboBoxUI boxUI;

    public QRComboBox() {
        addItemChangedListener();
        boxUI = new QRBasicComboBoxUI();
        componentFresh();
    }

    public QRComboBox(boolean numberOnly) {
        addItemChangedListener();
        if (numberOnly) {
            numberOnly();
        }
        boxUI = new QRBasicComboBoxUI();
        componentFresh();
    }

    public QRComboBox(String... array) {
        addItemChangedListener();
        setModel(array);
        boxUI = new QRBasicComboBoxUI();
        componentFresh();
    }

    /**
     * 设置当前文本。
     *
     * <p>如果当前 model 中已有该值，则直接选中；如果不存在，会把该值追加到 model 后再选中。
     * {@code null} 会清空当前选择。</p>
     *
     * @param value 要显示/选中的文本
     */
    public void setText(String value) {
        if (value == null) {
            setSelectedItem(null);
            return;
        }
        ComboBoxModel<String> cbm = getModel();
        if (cbm != null && cbm.getSize() > 0) {
            ArrayList<String> ele = new ArrayList<>();
            for (int i = 0; i < cbm.getSize(); i++) {
                String e = cbm.getElementAt(i);
                ele.add(e);
                if (Objects.equals(e, value)) {
                    setSelectedItem(value);
                    return;
                }
            }
            ele.add(value);
            setModel(ele.toArray(QRStringUtils.ARR_EMPTY));
            setSelectedItem(value);
        } else {
            setModel(value);
            setSelectedItem(value);
        }
    }

    /**
     * 取得当前选中项文本。
     *
     * @return 当前选中项字符串；没有选中项时返回空字符串
     */
    public String getText() {
        Object selectedItem = getSelectedItem();
        return selectedItem == null ? "" : selectedItem.toString();
    }

    /**
     * 用字符串数组替换下拉框模型。
     *
     * @param array 选项数组；为 null 或空数组时清空模型
     */
    public void setModel(String... array) {
        if (array != null && array.length > 0) {
            setModel(new DefaultComboBoxModel<>(array));
        } else {
            setModel(new DefaultComboBoxModel<>());
        }
    }

    /**
     * 设置只能选择/输入数字字符。
     *
     * <p>该过滤在 item 状态变化后检查当前文本；发现非数字字符时会回退到第一个选项。</p>
     */
    public void numberOnly() {
        allowInputChar.append("0123456789");
        addItemListener(e -> numberItemStateChange());
    }

    /**
     * 设置只能选择/输入数字和小数点。
     *
     * <p>该方法只做字符级过滤，不保证最终文本是合法小数。</p>
     */
    public void decimalOnly() {
        allowInputChar.append("0123456789.");
        addItemListener(e -> decimalItemStateChange());
    }

    /**
     * 实例化时已自动添加
     */
    private void addItemChangedListener() {
        if (itemChangedListener == null) {
            itemChangedListener = new QRItemListener();
            //每次改动会调用两次，我们需要减少一次
            QRTimeCountUtil qcu = new QRTimeCountUtil((short) 300);
            addItemListener(e -> {
                if (qcu.isPassedMmTime()) {
                    qcu.startTimeUpdate();
                    String text = getText();
                    itemChangedListener.itemChangedAction(new QRItemEvent(preValue, text));
                    preValue = text;
                }
            });
            itemChangedListener.add(this::itemChangedAction);
        }
    }

    /**
     * 添加选中项变化动作。
     *
     * <p>事件参数为 {@link QRItemEvent}，可读取变化前后的文本值。内部会做短时间去重，
     * 以减少 Swing item 事件一次选择触发多次的问题。</p>
     *
     * @param ar 操作，其参数 {@link QRActionRegister#action(Object)} 为 {@link QRItemEvent} 的对象
     */
    public void addItemChangeListener(QRActionRegister<QRItemEvent> ar) {
        if (itemChangedListener == null) {
            addItemChangedListener();
        }
        if (itemChangedListener != null) {
            itemChangedListener.add(ar);
        }
    }

    /**
     * 选中项变化回调。
     *
     * <p>子类可重写该方法处理变化；外部调用方通常使用 {@link #addItemChangeListener(QRActionRegister)}。</p>
     *
     * @param e 事件参数
     */
    protected void itemChangedAction(QRItemEvent e) {

    }

    //region 取得监听器
    public QRItemListener getItemChangedListener() {
        return itemChangedListener;
    }
    //endregion


    @Override
    public void setSelectedIndex(int anIndex) {
        itemChangeLock = true;
        super.setSelectedIndex(anIndex);
        itemChangeLock = false;
    }

    @Override
    public void componentFresh() {

        setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
        setForeground(QRColorsAndFonts.MENU_COLOR);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        setBorder(new LineBorder(QRColorsAndFonts.BORDER_COLOR, 1));

        boxUI.componentFresh();
        setUI(boxUI);
    }

    private void numberItemStateChange() {
        if (!itemChangeLock) {
            String text = getText();
            final String allowInputCharStr = allowInputChar.toString();
            if (Arrays.stream(QRStringUtils.stringToStringArr(text)).anyMatch(s -> !allowInputCharStr.contains(s))) {
                setSelectedIndex(0);
            }
        }
    }

    private void decimalItemStateChange() {
        if (!itemChangeLock) {
            String text = getText();
            boolean show = text.startsWith(".") || text.endsWith(".");
            if (!show && Arrays.stream(QRStringUtils.stringToStringArr(text)).anyMatch(s -> !allowInputChar.toString().contains(s))) {
                setSelectedIndex(0);
            }
        }
    }

    @Override
    public JToolTip createToolTip() {
        QRToolTip tip = new QRToolTip();
        tip.setComponent(tip);
        return tip;
    }
}

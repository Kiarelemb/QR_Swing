package swing.qr.kiarelemb.component.basic;

import method.qr.kiarelemb.utils.QRStringUtils;
import method.qr.kiarelemb.utils.QRTimeCountUtil;
import swing.qr.kiarelemb.component.assembly.QRBasicComboBoxUI;
import swing.qr.kiarelemb.component.assembly.QRToolTip;
import swing.qr.kiarelemb.component.event.QRItemEvent;
import swing.qr.kiarelemb.component.listener.QRItemListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
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

    public void setText(String value) {
        ComboBoxModel<String> cbm = getModel();
        if (cbm != null && cbm.getSize() > 0) {
            ArrayList<String> ele = new ArrayList<>();
            for (int i = 0; i < cbm.getSize(); i++) {
                String e = cbm.getElementAt(i);
                ele.add(e);
                if (e.equals(value)) {
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

    public String getText() {
        return Objects.requireNonNull(getSelectedItem()).toString();
    }

    public void setModel(String... array) {
        if (array.length > 0) {
            setModel(new DefaultComboBoxModel<>(array));
        }
    }

    /**
     * 设置只能输入数字
     */
    public void numberOnly() {
        allowInputChar.append("0123456789");
        addItemListener(e -> numberItemStateChange());
    }

    /**
     * 设置只能输入小数
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
            itemChangedListener.add(e -> itemChangedAction((QRItemEvent) e));
        }
    }

    /**
     * 可直接调用
     *
     * @param ar 操作，其参数 {@link QRActionRegister#action(Object)} 为 {@link QRItemEvent} 的对象
     *           <p> -> {@code QRItemEvent event = (QRItemEvent) e;}
     */
    public void addItemChangeListener(QRActionRegister ar) {
        if (itemChangedListener != null) {
            itemChangedListener.add(ar);
        }
    }

    /**
     * 可直接重写
     *
     * @param e 事件参数
     */
    protected void itemChangedAction(QRItemEvent e) {

    }

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
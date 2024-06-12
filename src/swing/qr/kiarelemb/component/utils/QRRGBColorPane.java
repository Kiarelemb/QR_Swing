package swing.qr.kiarelemb.component.utils;

import method.qr.kiarelemb.utils.QRArrayUtils;
import method.qr.kiarelemb.utils.QRSleepUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.basic.QRTextField;
import swing.qr.kiarelemb.component.event.QRColorChangedEvent;
import swing.qr.kiarelemb.component.listener.QRMouseListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @apiNote 一个集成多个控件的颜色选择、显示、设置的面板
 * @create 2024-03-13 11:57
 */
public class QRRGBColorPane extends QRPanel {

    private final QRLabel showColorLabel;
    private ColorTextField rt;
    private ColorTextField gt;
    private ColorTextField bt;
    private final QRActionRegister colorChangedAction;
    private Color color;

    private class ColorTextField extends QRTextField {
        protected boolean insertBlock;

        public ColorTextField(int value) {
            setText(String.valueOf(value));
            numbersOnly();
            setPreferredSize(new Dimension(50, 30));
            setTextCenter();
            addDocumentListener();
        }

        @Override
        public void copy() {
            String c = getColor(color);
            QRSystemUtils.putTextToClipboard(c);
        }

        @Override
        public void paste() {
            pasteAction();
        }

        @Override
        public void setText(String t) {
            insertBlock = true;
            try {
                super.setText(t);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            insertBlock = false;
        }

        @Override
        protected boolean meetCondition() {
            final String text = getText();
            if (QRStringUtils.isNumber(text)) {
                final int value = Integer.parseInt(text);
                if (value < 0) {
                    setText("0");
                } else if (value > 255) {
                    setText("255");
                }
            } else {
                setText("0");
            }
            colorChanged();
            return super.meetCondition();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            if (insertBlock) {
                return;
            }
            insertBlock = true;
            QRSleepUtils.sleep(20);
            meetCondition();
            insertBlock = false;
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (insertBlock) {
                return;
            }
            meetCondition();
        }

        @Override
        public int getValue() {
            final String text = getText();
            final int value = Integer.parseInt(text);
            if (valueInRange(value)) {
                return value;
            }
            setText("0");
            return 0;
        }

        public void setValue(int value) {
            setText(valueInRange(value) ? String.valueOf(value) : "0");
        }

        private boolean valueInRange(int value) {
            return value >= 0 && value <= 255;
        }

        @Override
        public void componentFresh() {
            setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
            setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
            setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
            setCaretColor(QRColorsAndFonts.CARET_COLOR);
        }
    }

    /**
     * 构造方法，初始化一个RGB颜色面板。自加添加显示颜色的标签。
     *
     * @param color              面板显示的初始颜色
     * @param colorChangedAction 颜色改变时触发的动作，参数是 {@link QRColorChangedEvent}
     */
    public QRRGBColorPane(Color color, QRActionRegister colorChangedAction) {
        this.showColorLabel = new QRLabel();
        this.color = color;
        this.colorChangedAction = colorChangedAction;
        showColorLabel.setPreferredSize(new Dimension(30, 30));
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        add(showColorLabel);
        initComponents(color);
        setSize(250, 30);
    }

    /**
     * 构造方法，初始化一个RGB颜色面板。
     *
     * @param color              面板显示的初始颜色
     * @param showColorLabel     显示颜色的标签。注意，该构造器不会把标签添加到面板中，需要手动添加。
     * @param colorChangedAction 颜色改变时触发的动作，参数是 {@link QRColorChangedEvent}
     */
    public QRRGBColorPane(Color color, QRLabel showColorLabel, QRActionRegister colorChangedAction) {
        this.showColorLabel = showColorLabel;
        this.color = color;
        this.colorChangedAction = colorChangedAction;
        setLayout(new GridLayout(1, 0, 0, 5));
        initComponents(color);
        setSize(202, 30);
    }

    private void initComponents(Color color) {
        rt = new ColorTextField(color.getRed());
        gt = new ColorTextField(color.getGreen());
        bt = new ColorTextField(color.getBlue());
        QRLabel rl = new QRLabel("r:");
        QRLabel gl = new QRLabel("g:");
        QRLabel bl = new QRLabel("b:");
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 3);
        rl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 3));
        gl.setBorder(emptyBorder);
        bl.setBorder(emptyBorder);
        rl.setHorizontalAlignment(SwingConstants.RIGHT);
        gl.setHorizontalAlignment(SwingConstants.RIGHT);
        bl.setHorizontalAlignment(SwingConstants.RIGHT);

        showColorLabel.setOpaque(true);
        showColorLabel.setBackground(color);
        showColorLabel.setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.LINE_COLOR, 1));

        showColorLabel.addMouseListener();
        showColorLabel.addMouseAction(QRMouseListener.TYPE.CLICK, e -> {
            Color selected = JColorChooser.showDialog(QRRGBColorPane.this, "选择颜色", QRRGBColorPane.this.color);
            if (selected != null) {
                rt.setValue(selected.getRed());
                gt.setValue(selected.getGreen());
                bt.setValue(selected.getBlue());
                colorChanged();
            }
        });
        showColorLabel.addMouseAction(QRMouseListener.TYPE.ENTER, e -> showColorLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)));
        showColorLabel.addMouseAction(QRMouseListener.TYPE.EXIT, e -> showColorLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)));


        add(rl);
        add(rt);
        add(gl);
        add(gt);
        add(bl);
        add(bt);
    }

    public void colorChanged() {
        if (rt == null || gt == null || bt == null) {
            return;
        }
        if (rt.getText().isEmpty() || gt.getText().isEmpty() || bt.getText().isEmpty()) {
            return;
        }
        Color from = this.color;
        Color newColor = new Color(rt.getValue(), gt.getValue(), bt.getValue());
        if (newColor.equals(from)) {
            return;
        }
        setColor(newColor);
        Color to = this.color;
        colorChangedAction.action(new QRColorChangedEvent(from, to));
    }

    public Color getColor() {
        return color;
    }

    public QRLabel showColorLabel() {
        return showColorLabel;
    }

    public void setColor(Color color) {
        this.color = color;
        rt.setValue(color.getRed());
        gt.setValue(color.getGreen());
        bt.setValue(color.getBlue());
        showColorLabel.setBackground(color);
    }

    private void pasteAction() {
        final String text = QRSystemUtils.getSysClipboardText();
        if (text != null) {
            final int[] ints = QRStringUtils.splitWithNotNumber(text);
            Color c = null;
            if (ints.length == 3) {
                c = new Color(
                        colorValueCheckBound(ints[0]),
                        colorValueCheckBound(ints[1]),
                        colorValueCheckBound(ints[2])
                );
            } else {
                final String tmp = QRStringUtils.spaceClear(text, true);
                if (tmp.length() == 7 && tmp.startsWith("#")) {
                    c = parseColor(tmp.substring(1));
                }
            }
            if (c != null) {
                setColor(c);
            }
        }
    }

    public static int colorValueCheckBound(int value) {
        return Math.max(Math.min(value, 255), 0);
    }

    public static Color parseColor(String hexLen) {
        final LinkedList<String> split = QRArrayUtils.splitWithLength(hexLen, 2);
        assert split.size() == 3;
        int[] rgb = new int[3];
        int i = 0;
        for (String s : split) {
            rgb[i++] = Integer.parseInt(s, 16);
        }
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    public static String getColor(Color c) {
        return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    }


    public static Color parseColor(String rgb, char seperator) {
        int[] values = QRArrayUtils.splitToInt(rgb, seperator);
        return new Color(values[0], values[1], values[2]);
    }
}
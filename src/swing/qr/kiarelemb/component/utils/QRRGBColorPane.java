package swing.qr.kiarelemb.component.utils;

import method.qr.kiarelemb.utils.QRArrayUtils;
import method.qr.kiarelemb.utils.QRSleepUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.basic.QRTextField;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @apiNote 一个集成多个控件的颜色选择、显示、设置的面板
 * @create 2024-03-13 11:57
 */
public class QRRGBColorPane extends QRPanel {

    private final QRLabel showColorLabel;
    private final ColorTextField rt;
    private final ColorTextField gt;
    private final ColorTextField bt;
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
            focusLost(null);
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

    public QRRGBColorPane(Window tew, Color color, QRLabel showColorLabel, QRActionRegister colorChangedAction) {
//        setBorder(BorderFactory.createLineBorder(Color.GREEN, 1));
        this.showColorLabel = showColorLabel;
        this.color = color;
        this.colorChangedAction = colorChangedAction;
        showColorLabel.setOpaque(true);
        showColorLabel.setBackground(color);
        setLayout(new GridLayout(1, 0, 0, 5));
        rt = new ColorTextField(color.getRed());
        gt = new ColorTextField(color.getGreen());
        bt = new ColorTextField(color.getBlue());
        QRLabel rl = new QRLabel("r:");
        QRLabel gl = new QRLabel("g:");
        QRLabel bl = new QRLabel("b:");
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 3);
        rl.setBorder(emptyBorder);
        gl.setBorder(emptyBorder);
        bl.setBorder(emptyBorder);
        rl.setHorizontalAlignment(SwingConstants.RIGHT);
        gl.setHorizontalAlignment(SwingConstants.RIGHT);
        bl.setHorizontalAlignment(SwingConstants.RIGHT);
        showColorLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color selected = JColorChooser.showDialog(QRRGBColorPane.this, "选择颜色", QRRGBColorPane.this.color);
                if (selected != null) {
                    rt.setValue(selected.getRed());
                    gt.setValue(selected.getGreen());
                    bt.setValue(selected.getBlue());
                    colorChanged();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                tew.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tew.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        add(rl);
        add(rt);
        add(gl);
        add(gt);
        add(bl);
        add(bt);
        setSize(192, 30);
    }

    public void colorChanged() {
        if (bt == null || rt.getText().isEmpty() || gt.getText().isEmpty() || bt.getText().isEmpty()) {
            return;
        }
        setColor(new Color(rt.getValue(), gt.getValue(), bt.getValue()));
        colorChangedAction.action(null);
    }

    public Color getColor() {
        return color;
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

    private String getColor(Color c) {
        return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    }

    private int colorValueCheckBound(int value) {
        return Math.max(Math.min(value, 255), 0);
    }

    private Color parseColor(String hexLen) {
        final LinkedList<String> split = QRArrayUtils.splitWithLength(hexLen, 2);
        assert split.size() == 3;
        int[] rgb = new int[3];
        int i = 0;
        for (String s : split) {
            rgb[i++] = Integer.parseInt(s, 16);
        }
        return new Color(rgb[0], rgb[1], rgb[2]);
    }
}
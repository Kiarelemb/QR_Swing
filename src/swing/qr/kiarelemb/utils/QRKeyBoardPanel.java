package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRArrayUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRKeyBoardPanel
 * @description 键盘布局
 * @create 2024/4/13 15:23
 */
public class QRKeyBoardPanel extends QRPanel {
    public final ArrayList<QRLabel> labelList;
    protected final String[] text;

    public QRKeyBoardPanel() {
        super(null);
        setSize(0, 0);
        text = new String[]{"`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "←", "Tab", "Q", "W", "E", "R", "Y",
                "T", "U", "I", "O", "P", "[", "]", "\\", "Caps", "A", "S", "D", "F", "H", "G", "J", "K", "L", ";", "'", "ENTER",
                "SHIFT", "Z", "X", "C", "B", "V", "N", "M", ",", ".", "/", "SHIFT", "ALT", "SPACE"};
        int[][] position = {{20, 20, 80}, {110, 20, 80}, {200, 20, 80}, {290, 20, 80}, {380, 20, 80}, {560, 20, 80},
                {470, 20, 80}, {650, 20, 80}, {740, 20, 80}, {830, 20, 80}, {920, 20, 80}, {1010, 20, 80}, {1100, 20, 80},
                {1190, 20, 140}, {20, 120, 110}, {140, 120, 80}, {230, 120, 80}, {320, 120, 80}, {410, 120, 80},
                {590, 120, 80}, {500, 120, 80}, {680, 120, 80}, {770, 120, 80}, {860, 120, 80}, {950, 120, 80},
                {1040, 120, 80}, {1130, 120, 80}, {1220, 120, 110}, {20, 220, 160}, {190, 220, 80}, {280, 220, 80},
                {370, 220, 80}, {460, 220, 80}, {640, 220, 80}, {550, 220, 80}, {730, 220, 80}, {820, 220, 80},
                {910, 220, 80}, {1000, 220, 80}, {1090, 220, 80}, {1180, 220, 150}, {20, 320, 200}, {230, 320, 80},
                {320, 320, 80}, {410, 320, 80}, {590, 320, 80}, {500, 320, 80}, {680, 320, 80}, {770, 320, 80},
                {860, 320, 80}, {950, 320, 80}, {1040, 320, 80}, {1130, 320, 200}, {230, 420, 80}, {320, 420, 710}};
        int length = text.length;
        labelList = new ArrayList<>(length);
        int height = 80;
        for (int i = 0; i < length; i++) {
            Label label = new Label(i);
            label.setText(text[i]);
            int[] data = position[i];
            label.setBounds(data[0], data[1], data[2], height);
            labelList.add(label);
            add(label);
        }
    }

    /**
     * 大小固定，但还是需要调用来设置它的大小
     *
     * @param width  the new width of this component in pixels
     * @param height the new height of this component in pixels
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize(1360, 515);
    }

    public QRLabel label(String text) {
        int index = QRArrayUtils.objectIndexOf(this.text, QRStringUtils.toUpperCase(text));
        if (checkIndex(index)) {
            throw new NoSuchElementException(text);
        }
        return labelList.get(index);
    }

    public int labelIndex(QRLabel label) {
        if (label instanceof Label l) {
            return l.index;
        }
        return -1;
    }

    /**
     * 在这里自定义设计标签的属性，如字体设置、是否透明等
     *
     * @param label 当前标签
     */
    protected void labelComponentFresh(QRLabel label) {
    }

    /**
     * 在这里自定义标签其他内容的绘制，如添加其他字符等
     *
     * @param label 当前标签
     * @param g     绘制工具
     */
    protected void labelPaint(QRLabel label, Graphics g) {
    }

    private boolean checkIndex(int index) {
        return index >= labelList.size() || index < 0;
    }

    private class Label extends QRLabel {
        final int index;

        public Label(int index) {
            setTextCenter();
            this.index = index;
            labelComponentFresh(this);
        }

        @Override
        protected void paintBorder(Graphics g) {
            super.paintBorder(g);
            labelPaint(this, g);
        }

        @Override
        public void componentFresh() {
            super.componentFresh();
            setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.LINE_COLOR, 1));
        }
    }
}
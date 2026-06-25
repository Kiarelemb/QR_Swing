package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.basic.QRComboBox;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.util.Objects;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个加载字体的 {@link QRComboBox}
 * @create 2023-01-31 16:48
 **/
public class QRFontComboBox extends QRComboBox {
    protected static final String[] FONT_NAMES = QRFontUtils.getSystemFontNames();

    /**
     * 构造对象，使用默认的字体名称数组 {@link #FONT_NAMES} 作为选项。
     */
    public QRFontComboBox() {
        this(FONT_NAMES);
    }

    /**
     * 创建对象，可选择是否显示所有字体。
     *
     * @param showAllFont 是否以本字体显示字体名，如果为 {@code true} 则使得每个字体都显示为名称字体，否则只以默认字体显示字体名称
     */
    public QRFontComboBox(boolean showAllFont) {
        this(FONT_NAMES, showAllFont);
    }


    /**
     * 构造对象，并设置其渲染器为 {@link QRComboBoxRenderer}。
     *
     * @param fontNames 字体名称数组
     */
    public QRFontComboBox(String[] fontNames) {
        super(fontNames);
        setRenderer(new QRComboBoxRenderer());
    }


    /**
     * 创建对象，指定字体名称数组和是否以本字体显示字体名
     *
     * @param fontNames   字体名称数组
     * @param showAllFont 是否以本字体显示字体名，如果为 {@code true} 则使得每个字体都显示为名称字体，否则只以默认字体显示字体名称
     */
    public QRFontComboBox(String[] fontNames, boolean showAllFont) {
        super(fontNames);
        setRenderer(new QRComboBoxRenderer(showAllFont));
    }


    /**
     * 创建对象，并设置其选择的字体为 {@code fontName}。
     *
     * @param fontName 字体名称
     */
    public QRFontComboBox(String fontName) {
        this();
        setText(fontName);
    }

    public QRFontComboBox(String fontName, boolean showAllFont) {
        this(showAllFont);
        setText(fontName);
    }

    @Override
    public void setText(String value) {
        super.setText(value);
        setFont(QRFontUtils.getFont(value, QRColorsAndFonts.DEFAULT_FONT_MENU.getSize()));
    }

    @Override
    public void componentFresh() {
        super.componentFresh();
        setFont(getSelectedItem() == null ? QRColorsAndFonts.DEFAULT_FONT_MENU : QRFontUtils.getFont(getText(), QRColorsAndFonts.DEFAULT_FONT_MENU.getSize()));
    }

    static class QRComboBoxRenderer extends BasicComboBoxRenderer {
        /**
         * 默认使得每个字体都显示为名称字体
         */
        private final boolean showAllFont;

        public QRComboBoxRenderer() {
            this(true);
        }

        public QRComboBoxRenderer(boolean showAllFont) {
            this.showAllFont = showAllFont;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (showAllFont) {
                fontUpdate(label, value.toString());
            } else {
                if (isSelected) {
                    fontUpdate(label, value.toString());
                } else {
                    label.setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
                }
            }
            return label;
        }

        private void fontUpdate(JLabel label, String fontName) {
            if (!Objects.equals(label.getFont().getFontName(), fontName)) {
                Font font = QRFontUtils.getFont(fontName, QRColorsAndFonts.DEFAULT_FONT_MENU.getSize());
                label.setFont(font);
            }
        }
    }
}
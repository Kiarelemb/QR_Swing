package swing.qr.kiarelemb.component.utils;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.component.basic.QRComboBox;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个加载字体的 {@link QRComboBox}
 * @create 2023-01-31 16:48
 **/
public class QRFontComboBox extends QRComboBox {
    protected static final String[] FONT_NAMES = QRFontUtils.getSystemFontNames();

    public QRFontComboBox() {
        this(FONT_NAMES);
    }

    public QRFontComboBox(boolean showAllFont) {
        this(FONT_NAMES, showAllFont);
    }

    public QRFontComboBox(String[] fontNames) {
        super(fontNames);
        setRenderer(new QRComboBoxRenderer());
    }

    public QRFontComboBox(String[] fontNames, boolean showAllFont) {
        super(fontNames);
        setRenderer(new QRComboBoxRenderer(showAllFont));
    }

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
                Font font = QRFontUtils.getFont(value.toString(), QRColorsAndFonts.DEFAULT_FONT_MENU.getSize());
                label.setFont(font);
            } else {
                MouseAdapter mouseAdapter = new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        label.setFont(QRFontUtils.getFont(value.toString(), QRColorsAndFonts.DEFAULT_FONT_MENU.getSize()));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        label.setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
                    }
                };
                label.addMouseListener(mouseAdapter);
                label.addMouseMotionListener(mouseAdapter);
            }
            return label;
        }
    }
}
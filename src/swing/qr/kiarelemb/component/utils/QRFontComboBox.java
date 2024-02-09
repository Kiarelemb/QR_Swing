package swing.qr.kiarelemb.component.utils;

import method.qr.kiarelemb.utils.QRFontUtils;
import swing.qr.kiarelemb.component.basic.QRComboBox;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个加载字体的 {@link QRComboBox}
 * @create 2023-01-31 16:48
 **/
public class QRFontComboBox extends QRComboBox {
	protected static final String[] FONT_NAMES = QRFontUtils.getSystemFontNames();

	public QRFontComboBox() {
		super(FONT_NAMES);
		setRenderer(new QRComboBoxRenderer());
	}


	public QRFontComboBox(String fontName) {
		this();
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
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			Font font = QRFontUtils.getFont(value.toString(), QRColorsAndFonts.DEFAULT_FONT_MENU.getSize());
			label.setFont(font);
			return label;
		}
	}
}

package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTableCellRenderer
 * @create 2026/6/10
 */
public class QRTableCellRenderer extends DefaultTableCellRenderer {
	public QRTableCellRenderer() {
		setHorizontalAlignment(CENTER);
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
												   int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setHorizontalAlignment(CENTER);
		setFont(table == null ? QRColorsAndFonts.STANDARD_FONT_TEXT : table.getFont());
		if (isSelected) {
			setForeground(table == null ? QRColorsAndFonts.TEXT_COLOR_FORE : table.getSelectionForeground());
			setBackground(table == null ? QRColorsAndFonts.PRESS_COLOR : table.getSelectionBackground());
		} else {
			setForeground(table == null ? QRColorsAndFonts.TEXT_COLOR_FORE : table.getForeground());
			setBackground(table == null ? QRColorsAndFonts.FRAME_COLOR_BACK : table.getBackground());
		}
		if (column == 0) {
			setBorder(new CompoundBorder(
					new MatteBorder(0, 1, 0, 0, QRColorsAndFonts.LINE_COLOR),
					new EmptyBorder(0, 8, 0, 8)
			));
		} else {
			setBorder(new EmptyBorder(0, 8, 0, 8));
		}
		return this;
	}
}

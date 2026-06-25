package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTableHeaderRenderer
 * @description TODO
 * @create 2026/6/6 20:25
 */
public class QRTableHeaderRenderer extends DefaultTableCellRenderer {
	public QRTableHeaderRenderer() {
		setHorizontalAlignment(CENTER);
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
												   int row, int column) {
		super.getTableCellRendererComponent(table, value, false, false, row, column);
		JTableHeader header = table == null ? null : table.getTableHeader();
		setFont(header == null ? QRColorsAndFonts.STANDARD_FONT_TEXT : header.getFont());
		setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
		setBackground(QRColorsAndFonts.TEXT_COLOR_BACK);
		setBorder(new CompoundBorder(
				new MatteBorder(0, column == 0 ? 1 : 0, 1, 1, QRColorsAndFonts.LINE_COLOR),
				new EmptyBorder(0, 8, 0, 8)
		));
		return this;
	}
}

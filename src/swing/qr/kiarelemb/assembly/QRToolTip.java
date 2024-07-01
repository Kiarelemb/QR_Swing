package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-29 15:38
 **/
public class QRToolTip extends JToolTip implements QRComponentUpdate {
	/**
	 * 可以在使用前进行更改
	 */
	public static int MARGIN = 5;

	public QRToolTip() {
		componentFresh();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g2);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(QRColorsAndFonts.BORDER_COLOR);
		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	@Override
	public void componentFresh() {
		setBorder(new EmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		setFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
	}
}
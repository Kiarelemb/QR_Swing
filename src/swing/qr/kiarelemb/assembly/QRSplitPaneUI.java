package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-22 11:32
 **/
public class QRSplitPaneUI extends BasicSplitPaneUI {

	public QRSplitPaneUI() {

	}

	@Override
	public BasicSplitPaneDivider createDefaultDivider() {
		return new BasicSplitPaneDivider(this) {
			public void setBorder(Border b) {

			}

			@Override
			public void paint(Graphics g) {
				g.setColor(QRColorsAndFonts.LINE_COLOR);
				Dimension size = getSize();
				g.fillRect(0, 0, size.width, size.height);
				super.paint(g);
			}


		};
	}
}
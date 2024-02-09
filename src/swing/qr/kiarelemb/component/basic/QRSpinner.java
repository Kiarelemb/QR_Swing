package swing.qr.kiarelemb.component.basic;

import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-30 13:27
 **/
public class QRSpinner extends JSpinner implements QRComponentUpdate {


	public QRSpinner() {
		UIManager.put("Spinner.editorAlignment", SwingConstants.CENTER);
		componentFresh();
	}

	public QRSpinner(SpinnerModel model) {
		this();
		setModel(model);
	}

	@Override
	protected JComponent createEditor(SpinnerModel model) {
		DefaultEditor jc = (DefaultEditor) super.createEditor(model);
		jc.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		JFormattedTextField editor = jc.getTextField();
		editor.setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
		editor.setForeground(QRColorsAndFonts.MENU_COLOR);
		editor.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		return jc;
	}

	@Override
	public void componentFresh() {

		setUI(new QRBasicSpinnerUI());

		setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
		setForeground(QRColorsAndFonts.MENU_COLOR);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		setBorder(new LineBorder(QRColorsAndFonts.BORDER_COLOR, 1));

		DefaultEditor jc = (DefaultEditor) super.getEditor();
		jc.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);

		JFormattedTextField editor = jc.getTextField();
		editor.setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
		editor.setForeground(QRColorsAndFonts.MENU_COLOR);
		editor.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
	}

	public static class QRBasicSpinnerUI extends BasicSpinnerUI {

		@Override
		protected Component createPreviousButton() {
			return getButton(true);
		}

		@Override
		protected Component createNextButton() {
			return getButton(false);
		}

		private QRButton getButton(boolean previous) {
			QRButton button = new QRButton(previous ? "  ▽  " : "  △  ");
			button.setFont(button.getFont().deriveFont(10f));
			button.setName(previous ? "Spinner.previousButton" : "Spinner.nextButton");
			if (previous) {
				installPreviousButtonListeners(button);
			} else {
				installNextButtonListeners(button);
			}
			return button;
		}
	}
}

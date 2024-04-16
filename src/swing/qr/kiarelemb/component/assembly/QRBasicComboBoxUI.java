package swing.qr.kiarelemb.component.assembly;

import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.component.basic.QRScrollPane;
import swing.qr.kiarelemb.component.basic.QRTextField;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRBasicComboBoxUI
 * @description TODO
 * @create 2024/4/14 22:20
 */
public class QRBasicComboBoxUI extends BasicComboBoxUI implements QRComponentUpdate {
	private QRTextField textField;
	private QRButton button;
	private QRScrollPane scrollPane;

	@Override
	protected ComboBoxEditor createEditor() {
		return new BasicComboBoxEditor() {
			@Override
			protected JTextField createEditorComponent() {
				textField = new QRTextField() {
					@Override
					public void setBorder(Border border) {
						super.setBorder(new EmptyBorder(5, 5, 5, 5));
					}
				};
				textField.setBorder(null);
				return textField;
			}

		};
	}

	@Override
	protected JButton createArrowButton() {
		button = new QRButton(" ▼ ");
		button.setForeground(QRColorsAndFonts.SCROLL_COLOR);
		button.setFont(button.getFont().deriveFont(14f));
		button.setName("ComboBox.arrowButton");
		return button;
	}

	@Override
	protected ComboPopup createPopup() {
		return new BasicComboPopup(comboBox) {
			@Override
			protected JMenuItem createActionComponent(Action a) {
				JMenuItem item = super.createActionComponent(a);
				item.setHorizontalTextPosition(SwingConstants.CENTER);
				return item;
			}

			@Override
			protected JScrollPane createScroller() {
				scrollPane = new QRScrollPane();
				scrollPane.setViewportView(list);
				scrollPane.setHorizontalScrollBar(null);
				return scrollPane;
			}
		};
	}

	@Override
	public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
		Color t = g.getColor();
		g.setColor(QRColorsAndFonts.TEXT_COLOR_BACK);
		g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		g.setColor(t);
	}

	public QRTextField textField() {
		return textField;
	}

	public QRButton button() {
		return button;
	}

	public QRScrollPane scrollPane() {
		return scrollPane;
	}

	@Override
	public void componentFresh() {
		if (textField != null) {
			textField.componentFresh();
		}
		if (button != null) {
			button.componentFresh();
		}
		if (scrollPane != null) {
			scrollPane.componentFresh();
		}
	}
}
package swing.qr.kiarelemb.component.combination;

import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.basic.QRTextField;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRClearableTextField
 * @description TODO 带清除按钮的文本框
 * @create 2024/3/25 22:27
 */
public class QRClearableTextField extends QRPanel {
	public final QRTextField textField;

	public QRClearableTextField(boolean right) {
		textField = new TextField();
		QRButton clearButton = new Button();
		setLayout(new BorderLayout());

		add(textField, BorderLayout.CENTER);
		add(clearButton, right ? BorderLayout.EAST : BorderLayout.WEST);
	}

	protected boolean meetCondition() {
		return true;
	}

	protected void focusGainedAction() {
		setEnterBorder();
	}

	/**
	 * 已自动添加监听器，可直接重写，但不建议完全重写
	 */
	protected void focusLostAction() {
		if (textField == null) {
			return;
		}
		String text = textField.getText();
		if (text.isEmpty()) {
			//内容为空的边框
			setEmptyBorder();
			return;
		}
		if (textField.isOkay()) {
			//符合条件的边框
			setRightBorder();
			return;
		}
		//不符合条件的边框
		setErrorBorder();
	}

	//region 边框设置

	/**
	 * 获得焦点时的边框
	 */
	protected void setEnterBorder() {
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.BLUE_LIGHT));
	}

	/**
	 * 内容为空时的边框
	 */
	protected void setEmptyBorder() {
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.YELLOW));
	}

	/**
	 * 符合条件的边框
	 */
	protected void setRightBorder() {
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LIGHT_GREEN));
	}

	/**
	 * 不符合条件的边框
	 */
	protected void setErrorBorder() {
		setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.RED_NORMAL, 1));
	}
	//endregion 边框设置

	//region 类重写

	private class Button extends QRButton {
		public Button() {
			super(QRFrame.CLOSE_MARK);
			setToolTipText("清除");
		}

		@Override
		protected void actionEvent(ActionEvent o) {
			textField.clear();
		}
	}

	private class TextField extends QRTextField {
		@Override
		public void setBorder(Border border) {
			super.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}

		@Override
		protected boolean meetCondition() {
			return QRClearableTextField.this.meetCondition();
		}

		@Override
		protected void focusGainedAction() {
			QRClearableTextField.this.focusGainedAction();
		}

		@Override
		protected void focusLostAction() {
			QRClearableTextField.this.focusLostAction();
		}

	}
	//endregion 类重写
}
package swing.qr.kiarelemb.component.combination;

import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.basic.QRTextField;
import swing.qr.kiarelemb.component.utils.QRCloseButton;
import swing.qr.kiarelemb.component.utils.QRFilePathTextField;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRClearableTextField
 * @description 带清除按钮的文本框
 * @create 2024/3/25 22:27
 */
public class QRClearableTextField extends QRPanel {
	public final QRTextField textField;

	/**
	 * 默认清空按钮在右侧
	 */
	public QRClearableTextField() {
		this(true);
	}

	public QRClearableTextField(boolean right) {
		textField = new TextField();
		setLayout(new BorderLayout());

		add(textField, BorderLayout.CENTER);
		add(new ClearButton(), right ? BorderLayout.EAST : BorderLayout.WEST);
		setEmptyBorder();
	}

	/**
	 * 此构造器用于将 {@link QRTextField} 设置成 {@link QRFilePathTextField}，那么本类的 {@link #meetCondition()} 将失效
	 *
	 * @param right         清空按钮是否在右侧
	 * @param filePathModel 是否设置为 {@link QRFilePathTextField}
	 * @param path          文件路径，可为 {@code null}
	 * @param btn           确定按钮，可为 {@code null}
	 */
	public QRClearableTextField(boolean right, boolean filePathModel, String path, JButton btn) {
		if (filePathModel) {
			textField = new QRFilePathTextField(path, btn) {
				@Override
				public void setBorder(Border border) {
					super.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				}

				@Override
				protected void focusGained(FocusEvent e) {
					QRClearableTextField.this.focusGainedAction();
				}

				@Override
				protected void focusLost(FocusEvent e) {
					QRClearableTextField.this.focusLostAction();
				}
			};
		} else {

			textField = new TextField();
		}
		setLayout(new BorderLayout());

		add(textField, BorderLayout.CENTER);
		add(new ClearButton(), right ? BorderLayout.EAST : BorderLayout.WEST);
		setEmptyBorder();
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

	private class ClearButton extends QRCloseButton {
		public ClearButton() {
			setText(" ╳ ");
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
		protected void focusGained(FocusEvent e) {
			QRClearableTextField.this.focusGainedAction();
		}

		@Override
		protected void focusLost(FocusEvent e) {
			QRClearableTextField.this.focusLostAction();
		}

	}
	//endregion 类重写
}
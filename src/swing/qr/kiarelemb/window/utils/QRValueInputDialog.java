package swing.qr.kiarelemb.window.utils;

import method.qr.kiarelemb.utils.QRTools;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRRoundButton;
import swing.qr.kiarelemb.component.basic.QRTextField;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QREmptyDialog;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * @author Kiarelemb QR
 * @create  2024.03.13
 * @apiNote 本类使用方法：
 * <p>Input input = new Input(owner, textFieldTooltip, inputLabelText);
 * <p>input.setVisible(true);
 * <p>String answer = input.getAnswer();
 */
public class QRValueInputDialog extends QREmptyDialog {
	protected String answer;
	protected QRTextField textField;

	/**
	 *
	 * @param owner 父窗体
	 * @param textFieldTooltip 输入框的 Tooltip
	 * @param inputLabelText 输入内容提示
	 */
	public QRValueInputDialog(Window owner, String textFieldTooltip, String inputLabelText) {
		super(owner);
		int width = 320;
		int height = 160;
		setSize(width, height);
		setLocation(owner.getX() + owner.getWidth() / 2 - width / 2,
				owner.getY() + owner.getHeight() / 2 - height / 2);
//        setAlwaysOnTop(true);

		contentPane.setLayout(null);

		textField = new QRTextField();
		textField.setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
		textField.setBounds(22, 64, 280, 37);
		textField.setToolTipText(textFieldTooltip);
		contentPane.add(textField);

		QRLabel label = new QRLabel(inputLabelText);
		label.setBounds(22, 26, 280, 18);
		contentPane.add(label);

		QRRoundButton buttonSure = new QRRoundButton("确定");
		buttonSure.addActionListener(e -> {
			if (meetCondition()) {
				if (setAnswer(textField.getText())) {
					dispose();
				}
			}
		});
		buttonSure.setBounds(236, 118, 66, 29);
		buttonSure.setToolTipText("Enter");
		contentPane.add(buttonSure);

		QRRoundButton buttonCancel = new QRRoundButton("取消");
		buttonCancel.addActionListener(e -> dispose());
		buttonCancel.setBounds(158, 118, 66, 29);
		buttonCancel.setToolTipText("ESC");
		contentPane.add(buttonCancel);

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case KeyEvent.VK_ENTER:
						buttonSure.doClick();
						break;
					case KeyEvent.VK_ESCAPE:
						buttonCancel.doClick();
						break;
					default:
						QRTools.doNothing();
				}
			}
		});
	}


	/**
	 * 继承请重写
	 * @return 是否符合条件
	 */
	public boolean meetCondition() {
		return true;
	}

	/**
	 * 取得输入的内容
	 */
	public String getAnswer() {
		return answer == null ? "" : answer;
	}

	boolean setAnswer(String answer) {
		this.answer = answer;
		return true;
	}
}
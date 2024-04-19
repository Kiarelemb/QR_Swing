package swing.qr.kiarelemb.window.enhance;

import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRRoundButton;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 18:58
 **/
public final class QROpinionDialog extends QRDialog {
	public static final int OK = 0;
	public static final int CANCEL = 1;
	private final QRLabel message;
	private final JLabel image;
	private int selection = CANCEL;
	private final static String INFO = "提示";
	private final static String ERROR = "错误";
	private final static String MSG = "信息";

	private final QRActionRegister sureDisposeAction;

	private QROpinionDialog(Window parent) {
		super(parent);
		setSize(380, 220);
		message = new QRLabel();
		message.setHorizontalAlignment(SwingConstants.CENTER);
		message.setForeground(QRColorsAndFonts.DEFAULT_COLOR_LABEL);
		message.setBounds(42, 23, 313, 95);
		mainPanel.add(message);
		image = new JLabel();
		sureDisposeAction = e -> {
			if (QROpinionDialog.this.isFocused()) {
				sure();
			}
		};
		KeyStroke keyStroke = QRStringUtils.getKeyStroke(KeyEvent.VK_ENTER);
		QRSwing.registerGlobalAction(keyStroke, sureDisposeAction, false);
	}

	private void setImage(String imageFileName) {
		message.setBounds(117, 23, 228, 95);
		image.setForeground(Color.RED);
		image.setBounds(43, 42, 64, 64);
		image.setIcon(new ImageIcon(QRSwingInfo.loadUrl(imageFileName)));
		mainPanel.add(image);
	}

	private void sure() {
		selection = OK;
		dispose();
	}

	private void cancel() {
		selection = CANCEL;
		dispose();
	}

	@Override
	public void dispose() {
		setVisible(false);
		QRSwing.registerGlobalActionRemove(QRStringUtils.getKeyStroke(KeyEvent.VK_ESCAPE), disposeAction, false);
		QRSwing.registerGlobalActionRemove(QRStringUtils.getKeyStroke(KeyEvent.VK_ENTER), sureDisposeAction, false);
	}

	private int getSelection() {
		return selection;
	}

	private void setMessage(String message) {
		StringBuilder sb = new StringBuilder("<html>");
		char[] chars = message.toCharArray();
		int fontSize = this.message.getFont().getSize();
		FontMetrics fm = this.message.getFontMetrics(this.message.getFont());
		int start = 0;
		int len = 0;
		int lines = 0;
		Font font = new Font("微软雅黑", Font.PLAIN, fontSize);
		while (start + len < message.length()) {
			while (true) {
				len++;
				if (start + len > message.length()) {
					break;
				}
				if (fm.charsWidth(chars, start, len) > this.message.getWidth()) {
					break;
				}
			}
			lines++;
			sb.append(chars, start, len - 1).append("<br/>");
			if (lines / 4 > 0) {
				fontSize--;
				this.message.setFont(font);
				fm = this.message.getFontMetrics(font);
			}
			start = start + len - 1;
			len = 0;
		}
		sb.append(chars, start, message.length() - start);
		sb.append("</html>");
		this.message.setText(sb.toString());
	}

	private void sureOpinion(String message, String title) {
		setTitle(title);
		setMessage(message);
		QRRoundButton sureButton = new QRRoundButton("确定");
		sureButton.setToolTipText("Enter");
		sureButton.setBounds(155, 124, 70, 33);
		sureButton.addClickAction(e -> sure());
		mainPanel.add(sureButton);
		setVisible(true);
	}

	private void sureAndCancelOpinion(String message, String title) {
		setTitle(title);
		setMessage(message);
		QRRoundButton sureButton = new QRRoundButton("确定");
		sureButton.setToolTipText("Enter");
		sureButton.addClickAction(e -> sure());
		mainPanel.add(sureButton);

		QRRoundButton cancelButton = new QRRoundButton("取消");
		cancelButton.setToolTipText("Esc");
		cancelButton.addClickAction(e -> cancel());
		mainPanel.add(cancelButton);
		sureButton.setBounds(104, 124, 71, 33);
		cancelButton.setBounds(204, 124, 71, 33);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				requestFocus();
			}
		});
		setVisible(true);
	}

	/**
	 * 该单向信息提示框采用的是sureOpinion()方法
	 *
	 * @param parentComponent 主窗体
	 * @param message         内容
	 */
	public static void messageTellShow(Window parentComponent, String message) {
		if (message == null) {
			return;
		}
		QROpinionDialog qod = new QROpinionDialog(parentComponent);
		qod.setImage("tell.png");
		qod.sureOpinion(message, QROpinionDialog.MSG);
	}

	/**
	 * 该信息提示框采用的是sureAndCancelOpinion()方法
	 *
	 * @param parentComponent 主窗体
	 * @param message         内容
	 * @return 选译
	 */
	public static int messageInfoShow(Window parentComponent, String message) {
		if (message == null) {
			throw new RuntimeException("信息不能为空");
		}
		QROpinionDialog qod = new QROpinionDialog(parentComponent);
		qod.setImage("info.png");
		qod.sureAndCancelOpinion(message, QROpinionDialog.INFO);
		return qod.getSelection();
	}

	/**
	 * 该错误提示框采用的是sureOpinion()方法
	 *
	 * @param parentComponent 主窗体
	 * @param message         内容
	 */
	public static void messageErrShow(Window parentComponent, String message) {
		if (message == null) {
			return;
		}
		QROpinionDialog qod = new QROpinionDialog(parentComponent);
		qod.setImage("err.png");
		qod.sureOpinion(message, QROpinionDialog.ERROR);
	}
}
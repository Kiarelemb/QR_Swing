package swing.qr.kiarelemb.window.utils;

import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRDialog;

import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * 通用文本编辑对话框。
 * <p>提供 QRTextPane +「保存」/「取消」按钮的骨架布局。
 * 子类覆盖 {@link #onSave()}、{@link #initialText()} 以及可选的行为钩子来完成具体业务。
 *
 * <pre><code>
 * public class MyDialog extends QRTextInputDialog {
 *     private MyDialog() {
 *         super(parent, "标题", 600, 500);
 *     }
 *
 *     {@literal @}Override
 *     protected void initTextPane() {
 *         textPane.setLineWrap(false);
 *     }
 *
 *     {@literal @}Override
 *     protected String initialText() {
 *         return "默认内容";
 *     }
 *
 *     {@literal @}Override
 *     protected void onSave() {
 *         String content = getTextContent();
 *         // 校验、写入、关闭…
 *         dispose();
 *     }
 *
 *     {@literal @}Override
 *     protected void onWindowOpened() {
 *         textPane.grabFocus();
 *     }
 * }
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2025-01-14 15:30
 */
public class QRTextInputDialog extends QRDialog {

	/**
	 * 子类可直接操作的文本编辑区。
	 */
	protected final QRTextPane textPane = new QRTextPane();

	/**
	 * 「保存」按钮。
	 */
	private final QRRoundButton saveButton;

	/**
	 * 「取消」按钮。
	 */
	private final QRRoundButton cancelButton;

	/**
	 * @param parent 父窗口（不可为 {@code null})
	 * @param title  对话框标题
	 * @param width  对话框宽度
	 * @param height 对话框高度
	 */
	public QRTextInputDialog(Window parent, String title, int width, int height) {
		super(parent);
		setTitle(title);
		setTitlePlace(CENTER);
		setSize(width, height);
		setLocationRelativeTo(getParent());
		setParentWindowNotFollowMove();

		mainPanel.setLayout(new BorderLayout(5, 5));

		// ---- 中心编辑区 ----
		QRPanel center = new QRPanel(false, new BorderLayout());
		center.setBorder(new LineBorder(QRColorsAndFonts.LINE_COLOR, 3));
		textPane.addUndoManager();
		initTextPane();
		textPane.setText(initialText());
		center.add(textPane.addScrollPane(), BorderLayout.CENTER);
		mainPanel.add(center, BorderLayout.CENTER);

		// ---- 底部按钮栏 ----
		QRPanel bottom = new QRPanel(false, new FlowLayout(FlowLayout.RIGHT, 8, 0));
		bottom.setBorder(new LineBorder(QRColorsAndFonts.FRAME_COLOR_BACK, 5));

		cancelButton = new QRRoundButton("取消");
		cancelButton.setPreferredSize(new Dimension(80, 30));
		cancelButton.addClickAction(event -> dispose());

		saveButton = new QRRoundButton("保存");
		saveButton.setPreferredSize(new Dimension(80, 30));
		saveButton.addClickAction(event -> onSave());

		bottom.add(cancelButton);
		bottom.add(saveButton);
		mainPanel.add(bottom, BorderLayout.SOUTH);
	}

	/**
	 * 在此设置 {@link #textPane} 的属性，如 {@code setLineWrap(false)}、{@code setFont(...)} 等。
	 * <p>默认实现为空。
	 */
	protected void initTextPane() {
	}

	/**
	 * 返回对话框打开时填充到 {@link #textPane} 的初始文本。
	 * <p>默认返回空字符串。
	 *
	 * @return 初始文本
	 */
	protected String initialText() {
		return "";
	}

	/**
	 * 点击「保存」时回调。
	 * <p>子类在此做校验、写文件/数据库、关闭对话框。
	 * 骨架不捕获异常，子类自行 try-catch 并展示错误消息。
	 */
	protected void onSave() {
	}

	/**
	 * 获取 {@link #textPane} 当前文本（trim）。
	 *
	 * @return 当前文本，不会为 {@code null}
	 */
	protected String getTextContent() {
		return textPane.getText() == null ? "" : textPane.getText().trim();
	}

	public QRRoundButton getSaveButton() {
		return saveButton;
	}

	public QRRoundButton getCancelButton() {
		return cancelButton;
	}

	public QRTextPane getTextPane() {
		return textPane;
	}
}
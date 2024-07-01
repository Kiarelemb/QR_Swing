package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRFileUtils;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.window.enhance.QROpinionDialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-02-08 15:33
 **/
public class QRFileSelectRoundButton extends QRRoundButton {
	private final Window parent;
	private final String fileType;
	private final String[] extension;
	private File selectedFile;
	private String selectedFilePath;
	private final ArrayList<QRActionRegister> successes = new ArrayList<>();
	private final ArrayList<QRActionRegister> failures = new ArrayList<>();


	/**
	 * @param text      文本
	 * @param parent    父类窗体
	 * @param fileType  文件类型的言语上的名称
	 * @param extension 拓展名，可不加点
	 */
	public QRFileSelectRoundButton(String text, Window parent, String fileType, String... extension) {
		this.parent = parent;
		this.extension = extension;
		this.fileType = fileType;
		setText(text);
	}

	@Override
	protected final void actionEvent(ActionEvent o) {
		fileSelectAction();
	}

	private void fileSelectAction() {
		File file = QRFileUtils.fileSelect(this.parent, this.fileType, this.extension);
		if (file == null || !QRFileUtils.fileExists(file.getAbsolutePath())) {
			failedAction();
			QRComponentUtils.runActions(failures);
			return;
		}

		if (this.selectedFilePath != null && this.selectedFilePath.equals(file.getAbsolutePath())) {
			sameFileSelectedAction();
			return;
		}
		this.selectedFile = file;
		this.selectedFilePath = file.getAbsolutePath();
		successAction(this.selectedFile, this.selectedFilePath);
		QRComponentUtils.runActions(this.successes,this.selectedFile);
	}

	/**
	 * @param ar 其参数 {@link QRActionRegister#action(Object)} 为 {@link #selectedFile}
	 */
	public final void addSuccessAction(QRActionRegister ar) {
		successes.add(ar);
	}


	/**
	 * @param ar 其参数 {@link QRActionRegister#action(Object)} 为 null
	 */
	public final void addFailureAction(QRActionRegister ar) {
		failures.add(ar);
	}

	protected void sameFileSelectedAction() {
		String message = "该文件已被选中！";
		QROpinionDialog.messageTellShow(this.parent, message);
	}

	protected void successAction(File selectedFile, String selectedFilePath) {
	}

	protected void failedAction() {

	}

	/**
	 * 获取选择的文件路径
	 */
	public String selectedFilePath() {
		return this.selectedFilePath;
	}

	/**
	 * 获取选择的文件
	 */
	public File selectedFile() {
		return this.selectedFile;
	}

	public void setSelectedFilePath(String selectedFilePath) {
		this.selectedFilePath = selectedFilePath;
	}
}
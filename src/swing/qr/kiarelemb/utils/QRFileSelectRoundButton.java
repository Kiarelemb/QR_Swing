package swing.qr.kiarelemb.utils;

import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.window.enhance.QROpinionDialog;
import swing.qr.kiarelemb.window.utils.QRFileSelectDialog;

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
    private final QRFileSelectDialog.SelectMode selectMode;
    private File selectedFile;
    private String selectedFilePath;
    private final ArrayList<QRActionRegister<File>> successes = new ArrayList<>();
    private final ArrayList<QRActionRegister<File>> failures = new ArrayList<>();


    /**
     * @param text      文本
     * @param parent    父类窗体
     * @param fileType  文件类型的言语上的名称
     * @param extension 拓展名，可不加点
     */
    public QRFileSelectRoundButton(String text, Window parent, String fileType, String... extension) {
        this(text, parent, QRFileSelectDialog.SelectMode.FILE_ONLY, fileType, extension);
    }

    public QRFileSelectRoundButton(String text, Window parent, QRFileSelectDialog.SelectMode selectMode, String fileType, String... extension) {
        this.parent = parent;
        this.selectMode = selectMode == null ? QRFileSelectDialog.SelectMode.FILE_ONLY : selectMode;
        this.extension = extension;
        this.fileType = fileType;
        setText(text);
    }

    @Override
    protected final void actionEvent(ActionEvent o) {
        fileSelectAction();
    }

    private void fileSelectAction() {
        QRFileSelectDialog dialog = new QRFileSelectDialog(this.parent, this.selectMode, this.fileType, this.extension);
        if (this.selectedFilePath != null) {
            dialog.setSelectedFilePath(this.selectedFilePath);
        }
        if (!dialog.showDialog()) {
            failedAction();
            QRComponentUtils.runActions(failures, null);
            return;
        }
        File file = dialog.selectedFile();

        if (file == null || !file.exists()) {
            failedAction();
            QRComponentUtils.runActions(failures, file);
            return;
        }
        String filePath = file.getAbsolutePath();
        if (this.selectedFilePath != null && this.selectedFilePath.equals(filePath)) {
            sameFileSelectedAction();
            return;
        }
        this.selectedFile = file;
        this.selectedFilePath = filePath;
        successAction(this.selectedFile, this.selectedFilePath);
        QRComponentUtils.runActions(this.successes, this.selectedFile);
    }

    /**
     * @param ar 其参数 {@link QRActionRegister#action(Object)} 为 {@link #selectedFile}
     */
    public final void addSuccessAction(QRActionRegister<File> ar) {
        successes.add(ar);
    }


    /**
     * @param ar 其参数 {@link QRActionRegister#action(Object)} 为 null
     */
    public final void addFailureAction(QRActionRegister<File> ar) {
        failures.add(ar);
    }

    protected void sameFileSelectedAction() {
        String message = this.selectMode == QRFileSelectDialog.SelectMode.DIRECTORY_ONLY ? "该文件夹已被选中！" : "该文件已被选中！";
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

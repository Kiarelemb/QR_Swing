package swing.qr.kiarelemb.utils;

import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.window.enhance.QROpinionDialog;
import swing.qr.kiarelemb.window.utils.QRFileSelectDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个用来选择文件的按钮，可以设置文字，或设置图片
 * @create 2022-11-22 15:28
 **/
public class QRFileSelectButton extends QRButton {
    private final Window parent;
    private final String fileType;
    private final String[] extension;
    private final QRFileSelectDialog.SelectMode selectMode;
    private File selectedFile;

    private String selectedFilePath;
    private final ArrayList<QRActionRegister<File>> successes = new ArrayList<>();
    private final ArrayList<QRActionRegister<File>> failures = new ArrayList<>();

    /**
     * 使用内置的选择图标作为默认，则推荐长宽的大小为 {@code 32}
     *
     * @param parent    父类窗体
     * @param fileType  文件类型的言语上的名称
     * @param extension 拓展名，可不加点
     */
    public QRFileSelectButton(Window parent, String fileType, String... extension) {
        this(parent, QRFileSelectDialog.SelectMode.FILE_ONLY, fileType, extension);
    }

    public QRFileSelectButton(Window parent, QRFileSelectDialog.SelectMode selectMode, String fileType, String... extension) {
        setIcon(new ImageIcon(QRSwingInfo.loadUrl("select.png")));
        this.parent = parent;
        this.selectMode = selectMode == null ? QRFileSelectDialog.SelectMode.FILE_ONLY : selectMode;
        this.extension = extension;
        this.fileType = fileType;
    }

    public QRFileSelectButton(String text, Window parent, String fileType, String... extension) {
        this(text, parent, QRFileSelectDialog.SelectMode.FILE_ONLY, fileType, extension);
    }

    public QRFileSelectButton(String text, Window parent, QRFileSelectDialog.SelectMode selectMode, String fileType, String... extension) {
        setText(text);
        this.parent = parent;
        this.selectMode = selectMode == null ? QRFileSelectDialog.SelectMode.FILE_ONLY : selectMode;
        this.extension = extension;
        this.fileType = fileType;
    }

    public QRFileSelectButton(Icon imageIcon, Window parent, String fileType, String... extension) {
        this(imageIcon, parent, QRFileSelectDialog.SelectMode.FILE_ONLY, fileType, extension);
    }

    public QRFileSelectButton(Icon imageIcon, Window parent, QRFileSelectDialog.SelectMode selectMode, String fileType, String... extension) {
        setIcon(imageIcon);
        this.parent = parent;
        this.selectMode = selectMode == null ? QRFileSelectDialog.SelectMode.FILE_ONLY : selectMode;
        this.extension = extension;
        this.fileType = fileType;
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

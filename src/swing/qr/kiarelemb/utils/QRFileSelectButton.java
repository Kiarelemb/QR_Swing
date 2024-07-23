package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRFileUtils;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.window.enhance.QROpinionDialog;

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
        this.parent = parent;
        this.extension = extension;
        this.fileType = fileType;
        setIcon(new ImageIcon(QRSwingInfo.loadUrl("select.png")));
    }

    public QRFileSelectButton(String text, Window parent, String fileType, String... extension) {
        this.parent = parent;
        this.extension = extension;
        this.fileType = fileType;
        setText(text);
    }

    public QRFileSelectButton(Icon imageIcon, Window parent, String fileType, String... extension) {
        this.parent = parent;
        this.extension = extension;
        this.fileType = fileType;
        setIcon(imageIcon);
    }

    @Override
    protected final void actionEvent(ActionEvent o) {
        fileSelectAction();
    }

    private void fileSelectAction() {
        File file = QRFileUtils.fileSelect(this.parent, this.fileType, this.extension);
        if (file == null || !QRFileUtils.fileExists(file.getAbsolutePath())) {
            failedAction();
            QRComponentUtils.runActions(failures, file);
            return;
        }

        if (this.selectedFilePath != null && this.selectedFilePath.equals(file.getAbsolutePath())) {
            sameFileSelectedAction();
            return;
        }
        this.selectedFile = file;
        this.selectedFilePath = file.getAbsolutePath();
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
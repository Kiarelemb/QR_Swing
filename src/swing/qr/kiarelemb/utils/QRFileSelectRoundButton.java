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
 * 圆角样式的文件选择按钮。
 *
 * <p>行为与 {@link QRFileSelectButton} 基本一致，只是基类改为 {@link QRRoundButton}。
 * 点击后打开 {@link QRFileSelectDialog}，成功时更新 {@link #selectedFile()} 和
 * {@link #selectedFilePath()} 并执行成功回调。</p>
 *
 * <p>注意：当前实现会要求返回的 {@link File#exists()} 为 true，因此不适合作为
 * {@link QRFileSelectDialog.SelectMode#SAVE_FILE} 的保存路径按钮。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
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
     * 添加选择成功动作。
     *
     * @param ar 其参数为当前 {@link #selectedFile}
     */
    public final void addSuccessAction(QRActionRegister<File> ar) {
        successes.add(ar);
    }


    /**
     * 添加选择失败动作。
     *
     * <p>用户取消时参数为 null；如果对话框返回了无效文件，参数为该文件对象。</p>
     *
     * @param ar 失败动作
     */
    public final void addFailureAction(QRActionRegister<File> ar) {
        failures.add(ar);
    }

    /**
     * 用户重复选择同一路径时的处理。
     */
    protected void sameFileSelectedAction() {
        String message = this.selectMode == QRFileSelectDialog.SelectMode.DIRECTORY_ONLY ? "该文件夹已被选中！" : "该文件已被选中！";
        QROpinionDialog.messageTellShow(this.parent, message);
    }

    /**
     * 选择成功回调，子类可重写。
     *
     * @param selectedFile     选择到的文件或目录
     * @param selectedFilePath 绝对路径
     */
    protected void successAction(File selectedFile, String selectedFilePath) {
    }

    /**
     * 选择失败回调，子类可重写。
     */
    protected void failedAction() {

    }

    /**
     * 获取最近一次成功选择的文件路径。
     *
     * @return 文件绝对路径；尚未成功选择时为 null
     */
    public String selectedFilePath() {
        return this.selectedFilePath;
    }

    /**
     * 获取最近一次成功选择的文件。
     *
     * @return 文件或目录；尚未成功选择时为 null
     */
    public File selectedFile() {
        return this.selectedFile;
    }

    /**
     * 预设选择路径。
     *
     * @param selectedFilePath 文件或目录路径
     */
    public void setSelectedFilePath(String selectedFilePath) {
        this.selectedFilePath = selectedFilePath;
    }
}

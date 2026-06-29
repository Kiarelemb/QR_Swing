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
 * 打开 {@link QRFileSelectDialog} 的文件选择按钮。
 *
 * <p>按钮点击后会弹出文件/文件夹选择对话框。选择成功后会更新
 * {@link #selectedFile()} 和 {@link #selectedFilePath()}，并执行成功回调；
 * 用户取消、关闭对话框或选中无效路径时执行失败回调。</p>
 *
 * <p>注意：当前实现会要求返回的 {@link File#exists()} 为 true，因此不适合作为
 * {@link QRFileSelectDialog.SelectMode#SAVE_FILE} 的保存路径按钮；保存路径场景应直接使用
 * {@link QRFileSelectDialog}。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRFileSelectButton button = new QRFileSelectButton(
 *         "选择图片", this, QRFileSelectDialog.SelectMode.FILE_ONLY, "图片", "png", "jpg");
 * button.addSuccessAction(file -> preview(file));
 * button.addFailureAction(file -> QRSmallTipShow.display(this, "未选择图片"));
 * </code></pre>
 *
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
     *
     * <p>默认弹出“该文件/文件夹已被选中”的提示；子类可重写。</p>
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
     * <p>下次打开选择对话框时会尝试定位到该路径。</p>
     *
     * @param selectedFilePath 文件或目录路径
     */
    public void setSelectedFilePath(String selectedFilePath) {
        this.selectedFilePath = selectedFilePath;
    }
}

package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRFileUtils;
import swing.qr.kiarelemb.basic.QRTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

/**
 * 可与按钮联动的文件路径输入框。
 *
 * <p>该类继承 {@link QRTextField}，自动启用撤销、居中文本、文件名非法字符过滤和文档监听。
 * 当输入框内容指向的文件不存在时，绑定按钮会被禁用；文件存在时按钮恢复可用。
 * 常用于“选择文件后确认”“输入文件路径后启用确定按钮”等场景。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRRoundButton ok = new QRRoundButton("确定");
 * QRFilePathTextField pathField = new QRFilePathTextField(ok);
 * pathField.setText("/path/to/file.txt");
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个能够与按钮联动的文件文本框。当其内容所指向的文件不存在时，该按钮不能使用。而该按钮往往是“确定”等类似功能的按钮。
 * @create 2023-02-02 20:42
 **/
public class QRFilePathTextField extends QRTextField {
    private final JButton btn;

    /**
     * 创建文件路径输入框，并绑定一个随路径有效性启停的按钮。
     *
     * @param btn 要联动的按钮，可为 null
     */
    public QRFilePathTextField(JButton btn) {
        this.btn = btn;
        final char[] notAllowed = {'*', '?', '<', '>', '|', '"'};
        addForbiddenChar(notAllowed);
        setTextCenter();
        //设置使之能够撤回
        addUndoManager();
        addDocumentListener();
    }

    /**
     * 创建未绑定按钮的文件路径输入框。
     */
    public QRFilePathTextField() {
        this(null);
    }

    /**
     * 创建带初始路径并绑定按钮的文件路径输入框。
     *
     * @param path 初始路径
     * @param btn  要联动的按钮，可为 null
     */
    public QRFilePathTextField(String path, JButton btn) {
        this(btn);
        setText(path);
    }

    @Override
    protected boolean meetCondition() {
        return QRFileUtils.fileExists(getText());
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        boolean b = meetCondition();
        if (btn != null) {
            btn.setEnabled(b);
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        insertUpdate(e);
    }

    @Override
    protected void changedUpdate(DocumentEvent e) {
        insertUpdate(e);
    }
}

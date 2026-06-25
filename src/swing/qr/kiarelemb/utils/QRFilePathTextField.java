package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRFileUtils;
import swing.qr.kiarelemb.basic.QRTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个能够与按钮联动的文件文本框。当其内容所指向的文件不存在时，该按钮不能使用。而该按钮往往是“确定”等类似功能的按钮。
 * @create 2023-02-02 20:42
 **/
public class QRFilePathTextField extends QRTextField {
    private final JButton btn;

    public QRFilePathTextField(JButton btn) {
        this.btn = btn;
        final char[] notAllowed = {'*', '?', '<', '>', '|', '"', '\\', '/', ':'};
        addForbiddenChar(notAllowed);
        setTextCenter();
        //设置使之能够撤回
        addUndoManager();
        addDocumentListener();
    }

    public QRFilePathTextField() {
        this(null);
    }

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
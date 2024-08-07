package swing.qr.kiarelemb.window.utils;

import swing.qr.kiarelemb.basic.QRTextArea;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.window.basic.QRDialog;

import java.awt.*;

/**
 * @author Kiarelemb QR
 * @create 2024.03.13
 * @apiNote 一个可以调节窗体大小的不可编辑的文本面板窗体
 */
public class QRResizableTextShowDialog extends QRDialog {
    private final QRTextArea textArea;

    /**
     * @param parent        父窗体
     * @param width         本窗体宽
     * @param height        本窗体高
     * @param title         本窗体标题
     * @param content       文本内容
     * @param scrollInFirst 滚轮是否移到最上面
     */
    public QRResizableTextShowDialog(Window parent, int width, int height, String title, String content, boolean scrollInFirst) {
        super(parent);

        setSize(width, height);
        setTitlePlace(QRDialog.CENTER);
        setTitle(title);

        textArea = new QRTextArea(true);
        textArea.setTabSize(4);
        textArea.setPreferredSize(null);
        textArea.setText(content);
        textArea.setEditable(false);
        textArea.caret.setVisible(true);
        textArea.addMouseListener();
        textArea.addMouseAction(QRMouseListener.TYPE.ENTER, e -> textArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)));
        if (scrollInFirst) {
            textArea.setCaretPosition(0);
        }
        setParentWindowNotFollowMove();
        setResizable(true);
        contentPane.add(textArea.addScrollPane(), BorderLayout.CENTER);
    }

    public void setSelection(int startIndex, int length) {
        textArea.setSelectionStart(startIndex);
        textArea.setSelectionEnd(startIndex + length);
    }
}
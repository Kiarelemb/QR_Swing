package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRDocumentListener;

import javax.swing.event.DocumentEvent;

public interface QRDocumentListenerAdd {
    /**
     * 添加 {@link javax.swing.event.DocumentListener} 事件
     */
    void addDocumentListener();

    /**
     * 为 {@link javax.swing.event.DocumentListener} 事件添加操作
     *
     * @param type 类型
     * @param ar   操作
     */
    void addDocumentListenerAction(QRDocumentListener.TYPE type, QRActionRegister<DocumentEvent> ar);
}
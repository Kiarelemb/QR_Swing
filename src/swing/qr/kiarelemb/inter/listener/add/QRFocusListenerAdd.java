package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRFocusListener;

public interface QRFocusListenerAdd {
    /**
     * 添加 {@link java.awt.event.FocusListener} 事件
     */
    void addFocusListener();

    /**
     * 为 {@link java.awt.event.FocusListener} 事件添加操作
     *
     * @param type 类型
     * @param ar   操作
     */
    void addFocusAction(QRFocusListener.TYPE type, QRActionRegister ar);
}
package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRMouseListener;

import java.awt.event.MouseEvent;

public interface QRMouseListenerAdd {
    /**
     * 添加 {@link java.awt.event.MouseListener} 事件
     */
    void addMouseListener();

    /**
     * 为 {@link java.awt.event.MouseListener} 事件添加操作
     *
     * @param type 类型
     * @param ar   操作
     */
    void addMouseAction(QRMouseListener.TYPE type, QRActionRegister<MouseEvent> ar);
}
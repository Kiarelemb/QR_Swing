package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;

import java.awt.event.MouseEvent;

public interface QRMouseMotionListenerAdd {
    /**
     * 添加 {@link java.awt.event.MouseMotionListener} 事件
     */
    void addMouseMotionListener();

    /**
     * 为 {@link java.awt.event.MouseMotionListener} 事件添加操作
     *
     * @param type 类型
     * @param ar   操作
     */
    void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister<MouseEvent> ar);
}
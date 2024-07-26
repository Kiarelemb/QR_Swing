package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRWindowListener;

import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-27 23:20
 **/
public interface QRWindowListenerAdd {
    /**
     * 添加 {@link QRWindowListener} 事件
     */
    void addWindowListener();

    /**
     * 为 {@link QRWindowListener} 事件添加操作
     *
     * @param type 类型
     * @param ar   操作
     */
    void addWindowAction(QRWindowListener.TYPE type, QRActionRegister<WindowEvent> ar);

    void addWindowMoveAction(QRActionRegister<Point> ar);
}
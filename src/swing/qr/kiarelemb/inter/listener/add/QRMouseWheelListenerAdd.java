package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRMouseWheelListenerAdd
 * @description TODO
 * @create 2024/7/17 下午7:22
 */
public interface QRMouseWheelListenerAdd {
    /**
     * 添加 {@link java.awt.event.MouseWheelListener} 事件
     */
    void addMouseWheelListener();

    /**
     * 为 {@link java.awt.event.MouseWheelListener} 事件添加操作
     *
     * @param ar 鼠标滚轮事件
     */
    void addMouseWheelAction(QRActionRegister ar);
}
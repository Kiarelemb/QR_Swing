package swing.qr.kiarelemb.inter;

import swing.qr.kiarelemb.window.basic.QRFrame;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 子窗体随父窗体移动的事件
 * @create 2022-11-04 15:11
 **/
public interface QRParentWindowMove {
    /**
     * 当父窗体 {@link QRFrame} 移动时，子窗体调用该事件
     */
    void ownerMoved();
}
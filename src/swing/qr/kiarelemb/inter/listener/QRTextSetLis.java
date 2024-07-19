package swing.qr.kiarelemb.inter.listener;

import swing.qr.kiarelemb.event.QREventObject;

import java.util.EventListener;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-27 14:03
 **/
public interface QRTextSetLis extends EventListener {
    void setTextAction(QREventObject event);
}
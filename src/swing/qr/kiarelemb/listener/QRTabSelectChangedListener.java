package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.event.QRTabSelectEvent;
import swing.qr.kiarelemb.inter.listener.QRTabSelectLis;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-05 12:32
 **/
public class QRTabSelectChangedListener extends QRAction implements QRTabSelectLis {
    @Override
    public final void tabSelectChangeAction(QRTabSelectEvent event) {
        action(event);
    }
}
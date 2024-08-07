package swing.qr.kiarelemb.listener;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-25 08:55
 **/
public class QRMouseWheelListener extends QRAction<MouseWheelEvent> implements MouseWheelListener {
    @Override
    public final void mouseWheelMoved(MouseWheelEvent e) {
        action(e);
    }
}
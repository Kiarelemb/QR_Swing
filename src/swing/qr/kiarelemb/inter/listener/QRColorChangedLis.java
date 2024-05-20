package swing.qr.kiarelemb.inter.listener;

import swing.qr.kiarelemb.component.event.QRColorChangedEvent;

import java.util.EventListener;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-30 15:58
 **/
public interface QRColorChangedLis extends EventListener {
	void colorChanged(QRColorChangedEvent e);
}
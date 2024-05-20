package swing.qr.kiarelemb.inter.listener;

import swing.qr.kiarelemb.component.event.QRItemEvent;

import java.util.EventListener;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-12-10 21:23
 **/
public interface QRItemLis extends EventListener {
	void itemChangedAction(QRItemEvent event);
}
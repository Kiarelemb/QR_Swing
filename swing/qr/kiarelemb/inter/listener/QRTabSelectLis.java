package swing.qr.kiarelemb.inter.listener;

import swing.qr.kiarelemb.component.event.QRTabSelectEvent;

import java.util.EventListener;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2023-01-05 12:29
 **/
public interface QRTabSelectLis extends EventListener {
	void tabSelectChangeAction(QRTabSelectEvent event);
}

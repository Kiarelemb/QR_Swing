package swing.qr.kiarelemb.component.event;

import java.awt.*;
import java.util.EventObject;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2022-11-30 15:59
 **/
public class QRColorChangedEvent extends EventObject {

	private final Color from;
	private final Color to;

	public QRColorChangedEvent(Color from, Color to) {
		super(to);
		this.from = from;
		this.to = to;
	}

	public Color from() {
		return from;
	}

	public Color to() {
		return to;
	}
}

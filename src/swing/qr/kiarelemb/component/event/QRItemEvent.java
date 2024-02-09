package swing.qr.kiarelemb.component.event;

import java.util.EventObject;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2022-12-10 21:24
 **/
public class QRItemEvent extends EventObject {
	private final String before;
	private final String after;

	public QRItemEvent(String before, String after) {
		super(after);
		this.before = before;
		this.after = after;
	}

	/**
	 * 等于同 {@link #after()}
	 */
	@Override
	public Object getSource() {
		return super.getSource();
	}

	public String before() {
		return before;
	}

	public String after() {
		return after;
	}
}

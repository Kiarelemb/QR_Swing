package swing.qr.kiarelemb.component.event;

import swing.qr.kiarelemb.component.combination.QRTabbedContentPanel;

import java.util.EventObject;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2023-01-05 12:29
 **/
public class QRTabSelectEvent extends EventObject {
	private final int before;
	private final int after;

	private final QRTabbedContentPanel tabbedContentPanel;

	public QRTabSelectEvent(int before, int after, QRTabbedContentPanel tabbedContentPanel) {
		super(after);
		this.before = before;
		this.after = after;
		this.tabbedContentPanel = tabbedContentPanel;
	}

	public int before() {
		return before;
	}

	public int after() {
		return after;
	}

	public QRTabbedContentPanel tabbedContentPanel() {
		return tabbedContentPanel;
	}
}

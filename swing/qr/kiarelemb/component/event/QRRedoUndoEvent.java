package swing.qr.kiarelemb.component.event;

import javax.swing.text.AbstractDocument;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
 * @create 2023-01-04 16:47
 **/
public class QRRedoUndoEvent extends EventObject {
	private final ArrayList<AbstractDocument.DefaultDocumentEvent> edits;

	public QRRedoUndoEvent(ArrayList<AbstractDocument.DefaultDocumentEvent> list) {
		super(list);
		this.edits = list;
	}

	public ArrayList<AbstractDocument.DefaultDocumentEvent> edits() {
		return edits;
	}
}

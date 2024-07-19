package swing.qr.kiarelemb.event;

import swing.qr.kiarelemb.basic.QRTextPane;

import javax.swing.*;
import java.util.EventObject;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 15:29
 **/
public class QRTextSelectionEndEvent extends EventObject {
    private final int start;
    private final int end;
    private final String selectedText;
    private final QRTextPane.QRCaretNearData caretNearData;

    public QRTextSelectionEndEvent(JEditorPane pane, QRTextPane.QRCaretNearData caretNearData, int start, int end, String selectedText) {
        super(pane);
        this.start = start;
        this.end = end;
        this.selectedText = selectedText;
        this.caretNearData = caretNearData;
    }

    public QRTextPane.QRCaretNearData caretNearData() {
        return caretNearData;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public String selectedText() {
        return selectedText;
    }
}
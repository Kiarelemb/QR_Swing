package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 14:20
 **/
public class QRDocumentListener implements DocumentListener {
    public enum TYPE {
        INSERT, REMOVE, CHANGED
    }

    private final LinkedList<QRActionRegister<DocumentEvent>> insert = new LinkedList<>();
    private final LinkedList<QRActionRegister<DocumentEvent>> remove = new LinkedList<>();
    private final LinkedList<QRActionRegister<DocumentEvent>> changed = new LinkedList<>();

    public void add(TYPE type, QRActionRegister<DocumentEvent> ar) {
        switch (type) {
            case INSERT -> this.insert.add(ar);
            case REMOVE -> this.remove.add(ar);
            case CHANGED -> this.changed.add(ar);
        }
    }

    public boolean remove(TYPE type, QRActionRegister<DocumentEvent> ar) {
        return switch (type) {
            case INSERT -> this.insert.remove(ar);
            case REMOVE -> this.remove.remove(ar);
            case CHANGED -> this.changed.remove(ar);
        };
    }

    @Override
    public final void insertUpdate(DocumentEvent e) {
        QRComponentUtils.runActions(insert, e);

    }

    @Override
    public final void removeUpdate(DocumentEvent e) {
        QRComponentUtils.runActions(remove, e);
    }

    @Override
    public final void changedUpdate(DocumentEvent e) {
        QRComponentUtils.runActions(changed, e);
    }
}
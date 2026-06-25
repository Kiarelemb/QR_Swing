package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-27 13:14
 **/
public class QRFocusListener extends QRAction<FocusEvent> implements FocusListener {
    public enum TYPE {
        GAIN, LOST
    }

    private final LinkedList<QRActionRegister<FocusEvent>> gain = new LinkedList<>();
    private final LinkedList<QRActionRegister<FocusEvent>> lost = new LinkedList<>();

    public void add(QRFocusListener.TYPE type, QRActionRegister<FocusEvent> ar) {
        if (type == QRFocusListener.TYPE.GAIN) {
            gain.add(ar);
        } else {
            lost.add(ar);
        }
    }

    public boolean remove(QRFocusListener.TYPE type, QRActionRegister<FocusEvent> ar) {
        return type == TYPE.GAIN ? gain.remove(ar) : lost.remove(ar);
    }

    @Override
    public final void focusGained(FocusEvent e) {
        QRComponentUtils.runActions(gain, e);
    }

    @Override
    public final void focusLost(FocusEvent e) {
        QRComponentUtils.runActions(lost, e);
    }
}
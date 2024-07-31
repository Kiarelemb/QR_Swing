package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 15:10
 **/
public class QRMouseMotionListener implements MouseMotionListener {
    public enum TYPE {
        DRAG, MOVE
    }

    private final LinkedList<QRActionRegister<MouseEvent>> drag = new LinkedList<>();
    private final LinkedList<QRActionRegister<MouseEvent>> move = new LinkedList<>();

    public void add(TYPE type, QRActionRegister<MouseEvent> ar) {
        if (type == TYPE.DRAG) {
            drag.add(ar);
        } else {
            move.add(ar);
        }
    }

    public boolean remove(TYPE type, QRActionRegister<MouseEvent> ar) {
        return type == TYPE.DRAG ? drag.remove(ar) : move.remove(ar);
    }

    @Override
    public final void mouseDragged(MouseEvent e) {
        QRComponentUtils.runActions(drag, e);
    }

    @Override
    public final void mouseMoved(MouseEvent e) {
        QRComponentUtils.runActions(move, e);
    }
}
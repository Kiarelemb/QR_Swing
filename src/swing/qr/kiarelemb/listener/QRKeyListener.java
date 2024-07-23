package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 14:54
 **/
public class QRKeyListener implements KeyListener {
    public enum TYPE {
        TYPE, PRESS, RELEASE
    }

    private final LinkedList<QRActionRegister<KeyEvent>> type = new LinkedList<>();
    private final LinkedList<QRActionRegister<KeyEvent>> press = new LinkedList<>();
    private final LinkedList<QRActionRegister<KeyEvent>> release = new LinkedList<>();

    public void add(TYPE type, QRActionRegister<KeyEvent> ar) {
        switch (type) {
            case TYPE -> this.type.add(ar);
            case PRESS -> this.press.add(ar);
            case RELEASE -> this.release.add(ar);
        }
    }

    @Override
    public final void keyTyped(KeyEvent e) {
        QRComponentUtils.runActions(type, e);
    }

    @Override
    public final void keyPressed(KeyEvent e) {
        QRComponentUtils.runActions(press, e);
    }

    @Override
    public final void keyReleased(KeyEvent e) {
        QRComponentUtils.runActions(release, e);
    }
}
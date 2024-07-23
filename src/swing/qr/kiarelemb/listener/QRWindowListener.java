package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 15:10
 **/
public class QRWindowListener implements WindowListener {
    public enum TYPE {
        OPEN, CLOSING, CLOSED, ICONIFIED, DEICONIFIED, ACTIVATED, DEACTIVATED
    }

    private final LinkedList<QRActionRegister<WindowEvent>> open = new LinkedList<>();
    private final LinkedList<QRActionRegister<WindowEvent>> closing = new LinkedList<>();
    private final LinkedList<QRActionRegister<WindowEvent>> closed = new LinkedList<>();
    private final LinkedList<QRActionRegister<WindowEvent>> iconified = new LinkedList<>();
    private final LinkedList<QRActionRegister<WindowEvent>> deiconified = new LinkedList<>();
    private final LinkedList<QRActionRegister<WindowEvent>> activated = new LinkedList<>();
    private final LinkedList<QRActionRegister<WindowEvent>> deactivated = new LinkedList<>();

    public void add(TYPE type, QRActionRegister<WindowEvent> ar) {
        switch (type) {
            case CLOSING -> this.closing.add(ar);
            case CLOSED -> this.closed.add(ar);
            case ICONIFIED -> this.iconified.add(ar);
            case DEICONIFIED -> this.deiconified.add(ar);
            case ACTIVATED -> this.activated.add(ar);
            case DEACTIVATED -> this.deactivated.add(ar);
            default -> this.open.add(ar);
        }
    }

    @Override
    public final void windowOpened(WindowEvent e) {
        QRComponentUtils.runActions(this.open, e);
    }

    @Override
    public final void windowClosing(WindowEvent e) {
        QRComponentUtils.runActions(this.closing, e);
    }

    @Override
    public final void windowClosed(WindowEvent e) {
        QRComponentUtils.runActions(this.closed, e);
    }

    @Override
    public final void windowIconified(WindowEvent e) {
        QRComponentUtils.runActions(this.iconified, e);
    }

    @Override
    public final void windowDeiconified(WindowEvent e) {
        QRComponentUtils.runActions(this.deiconified, e);
    }

    @Override
    public final void windowActivated(WindowEvent e) {
        QRComponentUtils.runActions(this.activated, e);
    }

    @Override
    public final void windowDeactivated(WindowEvent e) {
        QRComponentUtils.runActions(this.deactivated, e);
    }
}
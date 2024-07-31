package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 15:15
 **/
public class QRMouseListener implements MouseListener {
    public enum TYPE {
        CLICK, PRESS, RELEASE, ENTER, EXIT
    }

    private final LinkedList<QRActionRegister<MouseEvent>> click = new LinkedList<>();
    private final LinkedList<QRActionRegister<MouseEvent>> press = new LinkedList<>();
    private final LinkedList<QRActionRegister<MouseEvent>> release = new LinkedList<>();
    private final LinkedList<QRActionRegister<MouseEvent>> enter = new LinkedList<>();
    private final LinkedList<QRActionRegister<MouseEvent>> exit = new LinkedList<>();

    public void add(TYPE type, QRActionRegister<MouseEvent> ar) {
        switch (type) {
            case CLICK -> this.click.add(ar);
            case PRESS -> this.press.add(ar);
            case RELEASE -> this.release.add(ar);
            case ENTER -> this.enter.add(ar);
            case EXIT -> this.exit.add(ar);
        }
    }

    public boolean remove(TYPE type, QRActionRegister<MouseEvent> ar) {
        return switch (type) {
            case CLICK -> this.click.remove(ar);
            case PRESS -> this.press.remove(ar);
            case RELEASE -> this.release.remove(ar);
            case ENTER -> this.enter.remove(ar);
            case EXIT -> this.exit.remove(ar);
        };
    }

    @Override
    public final void mouseClicked(MouseEvent e) {
        QRComponentUtils.runActions(click, e);
    }

    @Override
    public final void mousePressed(MouseEvent e) {
        QRComponentUtils.runActions(press, e);
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
        QRComponentUtils.runActions(release, e);
    }

    @Override
    public final void mouseEntered(MouseEvent e) {
        QRComponentUtils.runActions(enter, e);
    }

    @Override
    public final void mouseExited(MouseEvent e) {
        QRComponentUtils.runActions(exit, e);
    }
}
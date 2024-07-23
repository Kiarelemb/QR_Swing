package swing.qr.kiarelemb.listener.key;

import swing.qr.kiarelemb.event.QRNativeKeyEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRNativeKeyListener;

import javax.swing.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRNativeKeyTypedListener
 * @description TODO
 * @create 2024/7/20 下午5:56
 */
public class QRNativeKeyReleasedListener extends QRNativeKeyListener {

    @Override
    public void addEvent(TYPE type, boolean mainWindowFocus, KeyStroke keyStroke, QRActionRegister<KeyStroke> ar) {
        releaseKeyEvents.addEvent(keyStroke, mainWindowFocus, ar);
    }

    @Override
    public void removeEvent(TYPE type, KeyStroke keyStroke, boolean mainWindowFocus) {
        releaseKeyEvents.removeEvent(keyStroke, mainWindowFocus);
    }

    @Override
    public void removeEvent(TYPE type, KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
        releaseKeyEvents.removeEvent(keyStroke, ar, mainWindowFocus);
    }

    @Override
    public void add(boolean mainWindowFocus, QRActionRegister<QRNativeKeyEvent> ar) {
        releaseKeyEvents.add(mainWindowFocus, ar);
    }

    @Override
    public void remove(boolean mainWindowFocus, QRActionRegister<QRNativeKeyEvent> ar) {
        releaseKeyEvents.remove(mainWindowFocus, ar);
    }
}
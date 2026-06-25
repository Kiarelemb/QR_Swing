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
public class QRNativeKeyPressedListener extends QRNativeKeyListener {

    @Override
    public void addEvent(TYPE type, boolean mainWindowFocus, KeyStroke keyStroke, QRActionRegister<KeyStroke> ar) {
        pressKeyEvents.addEvent(keyStroke, mainWindowFocus, ar);
    }

    @Override
    public void removeEvent(TYPE type, KeyStroke keyStroke, boolean mainWindowFocus) {
        pressKeyEvents.removeEvent(keyStroke, mainWindowFocus);
    }

    @Override
    public void removeEvent(TYPE type, KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
        pressKeyEvents.removeEvent(keyStroke, ar, mainWindowFocus);
    }

    @Override
    public void add(boolean mainWindowFocus, QRActionRegister<QRNativeKeyEvent> ar) {
        pressKeyEvents.add(mainWindowFocus, ar);
    }

    @Override
    public void remove(boolean mainWindowFocus, QRActionRegister<QRNativeKeyEvent> ar) {
        pressKeyEvents.remove(mainWindowFocus, ar);
    }
}
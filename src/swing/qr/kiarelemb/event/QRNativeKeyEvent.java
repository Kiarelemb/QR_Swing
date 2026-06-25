package swing.qr.kiarelemb.event;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.listener.QRNativeKeyListener;

import javax.swing.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRNativeKeyEvent
 * @description TODO
 * @create 2024/7/20 下午4:16
 */
public class QRNativeKeyEvent extends NativeKeyEvent {

    public QRNativeKeyEvent(QRNativeKeyListener.TYPE type, NativeKeyEvent e) {
        super(e.getID(), e.getModifiers(), e.getRawCode(), e.getKeyCode(), e.getKeyChar(), e.getKeyLocation());
    }

    public KeyStroke getKeyStroke() {
        int keyCode = NativeKeyEvent.getAWTKeyCode(getKeyCode());
        int modifiers = getModifiers();
        return QRStringUtils.getKeyStroke(keyCode, isActionKey() ? 0 : modifiers);
    }
}
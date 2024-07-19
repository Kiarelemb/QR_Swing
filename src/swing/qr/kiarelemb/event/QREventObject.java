package swing.qr.kiarelemb.event;

import javax.swing.*;
import java.util.EventObject;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-27 14:03
 **/
public class QREventObject extends EventObject {

    private final JComponent component;
    private final Object[] object;


    public QREventObject(JComponent component, Object... object) {
        super(component);
        this.component = component;
        this.object = object;
    }

    public JComponent component() {
        return component;
    }

    public Object[] object() {
        return object;
    }
}
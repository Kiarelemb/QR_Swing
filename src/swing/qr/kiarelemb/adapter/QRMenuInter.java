package swing.qr.kiarelemb.adapter;

import java.io.Serializable;

public interface QRMenuInter extends Serializable {
    void setPressed(boolean b);

    void disenableAll();

    void reenablesAll();
}
package swing.qr.kiarelemb.event;

import java.awt.*;
import java.util.EventObject;

/**
 * 颜色变化事件。
 *
 * <p>该事件记录变化前后的颜色，常由 {@link swing.qr.kiarelemb.utils.QRRGBColorPane}
 * 发出。{@link #getSource()} 为变化后的颜色。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-30 15:59
 **/
public class QRColorChangedEvent extends EventObject {

    private final Color from;
    private final Color to;

    public QRColorChangedEvent(Color from, Color to) {
        super(to);
        this.from = from;
        this.to = to;
    }

    /**
     * @return 变化前的颜色
     */
    public Color from() {
        return from;
    }

    /**
     * @return 变化后的颜色
     */
    public Color to() {
        return to;
    }
}

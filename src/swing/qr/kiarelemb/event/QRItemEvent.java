package swing.qr.kiarelemb.event;

import java.util.EventObject;

/**
 * 下拉框或可选项变化事件。
 *
 * <p>该事件记录变化前后的字符串值，常由 {@link swing.qr.kiarelemb.basic.QRComboBox}
 * 的 item 变化监听器发出。{@link #getSource()} 与 {@link #after()} 含义相同，
 * 都表示变化后的值。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-12-10 21:24
 **/
public class QRItemEvent extends EventObject {
    private final String before;
    private final String after;

    public QRItemEvent(String before, String after) {
        super(after);
        this.before = before;
        this.after = after;
    }

    /**
     * 等同于 {@link #after()}。
     *
     * @return 变化后的值
     */
    @Override
    public Object getSource() {
        return super.getSource();
    }

    /**
     * @return 变化前的值
     */
    public String before() {
        return before;
    }

    /**
     * @return 变化后的值
     */
    public String after() {
        return after;
    }
}

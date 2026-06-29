package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.LinkedList;

/**
 * 文档变化事件分发器。
 *
 * <p>该类实现 Swing 原生 {@link DocumentListener}，按插入、删除和属性变化三类事件分发动作。
 * QR Swing 文本控件通常通过 {@code addDocumentListenerAction(...)} 或
 * {@code addDocumentListenerActionAll(...)} 间接使用它。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRTextField field = new QRTextField();
 * field.addDocumentListenerActionAll(event -> validate(field.getText()));
 * </code></pre>
 *
 * <p>注意：Swing 文档事件回调发生在文档修改过程中，不要在回调里直接再次修改同一个文档；
 * 如需修正文本，使用 {@link javax.swing.SwingUtilities#invokeLater(Runnable)} 延后执行。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-24 14:20
 **/
public class QRDocumentListener implements DocumentListener {
    /**
     * 文档事件类型。
     */
    public enum TYPE {
        INSERT, REMOVE, CHANGED
    }

    private final LinkedList<QRActionRegister<DocumentEvent>> insert = new LinkedList<>();
    private final LinkedList<QRActionRegister<DocumentEvent>> remove = new LinkedList<>();
    private final LinkedList<QRActionRegister<DocumentEvent>> changed = new LinkedList<>();

    /**
     * 为指定文档事件添加动作。
     *
     * @param type 事件类型
     * @param ar   动作，参数为 {@link DocumentEvent}
     */
    public void add(TYPE type, QRActionRegister<DocumentEvent> ar) {
        switch (type) {
            case INSERT -> this.insert.add(ar);
            case REMOVE -> this.remove.add(ar);
            case CHANGED -> this.changed.add(ar);
        }
    }

    /**
     * 移除指定文档事件下的动作。
     *
     * @param type 事件类型
     * @param ar   要移除的动作
     * @return 是否移除成功
     */
    public boolean remove(TYPE type, QRActionRegister<DocumentEvent> ar) {
        return switch (type) {
            case INSERT -> this.insert.remove(ar);
            case REMOVE -> this.remove.remove(ar);
            case CHANGED -> this.changed.remove(ar);
        };
    }

    @Override
    public final void insertUpdate(DocumentEvent e) {
        QRComponentUtils.runActions(insert, e);

    }

    @Override
    public final void removeUpdate(DocumentEvent e) {
        QRComponentUtils.runActions(remove, e);
    }

    @Override
    public final void changedUpdate(DocumentEvent e) {
        QRComponentUtils.runActions(changed, e);
    }
}

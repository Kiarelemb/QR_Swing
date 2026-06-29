package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

/**
 * 鼠标事件分发器。
 *
 * <p>该类实现 Swing 原生 {@link MouseListener}，并按事件类型维护多组
 * {@link QRActionRegister}。QR Swing 组件通常通过自身的 {@code addMouseAction(...)}
 * 方法间接使用它；只有需要手动绑定原生 Swing 组件时，才需要直接实例化。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRMouseListener listener = new QRMouseListener();
 * listener.add(QRMouseListener.TYPE.CLICK, event -> System.out.println(event.getClickCount()));
 * component.addMouseListener(listener);
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-24 15:15
 **/
public class QRMouseListener implements MouseListener {
    /**
     * 鼠标事件类型，对应 {@link MouseListener} 的五个回调。
     */
    public enum TYPE {
        CLICK, PRESS, RELEASE, ENTER, EXIT
    }

    private final LinkedList<QRActionRegister<MouseEvent>> click = new LinkedList<>();
    private final LinkedList<QRActionRegister<MouseEvent>> press = new LinkedList<>();
    private final LinkedList<QRActionRegister<MouseEvent>> release = new LinkedList<>();
    private final LinkedList<QRActionRegister<MouseEvent>> enter = new LinkedList<>();
    private final LinkedList<QRActionRegister<MouseEvent>> exit = new LinkedList<>();

    /**
     * 为指定鼠标事件类型添加动作。
     *
     * @param type 事件类型
     * @param ar   动作，参数为 {@link MouseEvent}
     */
    public void add(TYPE type, QRActionRegister<MouseEvent> ar) {
        switch (type) {
            case CLICK -> this.click.add(ar);
            case PRESS -> this.press.add(ar);
            case RELEASE -> this.release.add(ar);
            case ENTER -> this.enter.add(ar);
            case EXIT -> this.exit.add(ar);
        }
    }

    /**
     * 移除指定鼠标事件类型下的动作。
     *
     * @param type 事件类型
     * @param ar   要移除的动作
     * @return 是否移除成功
     */
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

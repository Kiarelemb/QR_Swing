package swing.qr.kiarelemb.listener;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 保存并分发一组 {@link QRActionRegister} 的基础监听器容器。
 *
 * <p>很多具体监听器会继承该类，例如 item、tab、redo/undo、颜色变化等事件监听器。
 * 子类在原生事件到达时调用 {@link #action(Object)}，本类会按注册顺序执行所有动作。</p>
 *
 * <p>执行动作时使用 {@link QRComponentUtils#runActions(java.util.List, Object)}，
 * 会复制动作列表，并保证单个动作异常不阻断后续动作。</p>
 *
 * @param <T> 事件参数类型
 */
public class QRAction<T> implements Serializable {
    private final LinkedList<QRActionRegister<T>> list = new LinkedList<>();

    /**
     * 添加一个动作。
     *
     * @param ar 动作，不能为 null
     */
    public void add(QRActionRegister<T> ar) {
        list.add(ar);
    }

    /**
     * 移除一个已注册动作。
     *
     * @param ar 要移除的动作
     * @return 是否移除成功
     */
    public boolean remove(QRActionRegister<T> ar) {
        return list.remove(ar);
    }

    /**
     * 分发事件给所有已注册动作。
     *
     * @param e 事件参数
     */
    protected void action(T e) {
        QRComponentUtils.runActions(list, e);
    }
}

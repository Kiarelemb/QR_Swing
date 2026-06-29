package swing.qr.kiarelemb.inter;

/**
 * QR Swing 统一的回调函数接口。
 *
 * <p>本项目多数控件和监听器都使用该接口保存业务动作，例如按钮点击、
 * 鼠标事件、键盘事件、文档变化、窗口事件和后台任务生命周期回调等。
 * 泛型 {@code T} 表示回调参数类型，通常是 Swing 原生事件对象或 QR Swing 自定义事件对象。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRActionRegister&lt;ActionEvent&gt; saveAction = event -> save();
 * button.addClickAction(saveAction);
 *
 * QRActionRegister&lt;KeyStroke&gt; shortcut = keyStroke -> save();
 * QRSwing.registerGlobalAction("ctrl s", shortcut, true);
 * </code></pre>
 *
 * @param <T> 回调参数类型
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-24 13:53
 **/
public interface QRActionRegister<T> {
    /**
     * 执行回调动作。
     *
     * <p>调用方应避免在回调中抛出异常；部分监听器会捕获异常并继续执行后续动作，
     * 但不应依赖异常作为正常控制流。</p>
     *
     * @param event 回调参数，可由具体监听器约定为 null
     */
    void action(T event);
}

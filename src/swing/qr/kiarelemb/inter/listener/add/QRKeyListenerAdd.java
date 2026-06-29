package swing.qr.kiarelemb.inter.listener.add;

import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRKeyListener;

import java.awt.event.KeyEvent;

/**
 * 提供统一的按键监听添加入口。
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * textField.addKeyListenerAction(QRKeyListener.TYPE.PRESS, e -> save(), "Ctrl + S");
 * textField.addKeyListenerAction(QRKeyListener.TYPE.PRESS, e -> submit(), KeyEvent.VK_ENTER);
 * textField.addKeyListenerAction(QRKeyListener.TYPE.PRESS, e -> close(), new int[]{KeyEvent.VK_ESCAPE});
 * textField.addKeyListenerAction(QRKeyListener.TYPE.PRESS, e -> help(), KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
 * }</pre>
 *
 * <p>按键过滤参数的详细规则见 {@link QRKeyListener#add(QRKeyListener.TYPE, QRActionRegister, Object...)}。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-08 13:49
 **/
public interface QRKeyListenerAdd {
	/**
	 * 添加 {@link java.awt.event.KeyListener} 事件。
	 */
	void addKeyListener();

	/**
	 * 为 {@link java.awt.event.KeyListener} 事件添加全量操作。
	 *
	 * <p>只要发生对应类型的按键事件，该操作就会执行，不判断具体按键。</p>
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	void addKeyListenerAction(QRKeyListener.TYPE type, QRActionRegister<KeyEvent> ar);

	/**
	 * 为 {@link java.awt.event.KeyListener} 事件添加按键过滤操作。
	 *
	 * <p>{@code keys} 支持 {@link Integer}、{@code int[]}、{@link String} 和
	 * {@link javax.swing.KeyStroke}。字符串可使用逗号分隔多个按键，例如
	 * {@code "shift b, ctrl b"}。无法识别的按键会被跳过。</p>
	 *
	 * @param type 类型
	 * @param ar   操作
	 * @param keys 按键过滤条件
	 */
	void addKeyListenerAction(QRKeyListener.TYPE type, QRActionRegister<KeyEvent> ar, Object... keys);
}

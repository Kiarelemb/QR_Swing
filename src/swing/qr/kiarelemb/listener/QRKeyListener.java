package swing.qr.kiarelemb.listener;

import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 按键监听器，将 Swing 的 {@link KeyListener} 事件分发到已注册的 {@link QRActionRegister}。
 *
 * <p>不传 {@code keys} 时，操作会在对应事件类型发生时全部执行；传入 {@code keys} 时，
 * 只有当前按键匹配时才执行。可识别的按键参数包括 {@link KeyStroke}、{@link String}、
 * {@link Integer} 和 {@code int[]}。字符串解析规则同 {@link QRStringUtils#getKeyStroke(String)}，
 * 且支持英文逗号分隔多个按键。</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * QRKeyListener listener = new QRKeyListener();
 *
 * // 任意按键按下时触发
 * listener.add(QRKeyListener.TYPE.PRESS, e -> System.out.println(e.getKeyCode()));
 *
 * // 指定快捷键触发
 * listener.add(QRKeyListener.TYPE.PRESS, e -> save(), "Ctrl + S");
 *
 * // 单个 keyCode、int[]、KeyStroke 都可作为按键参数
 * listener.add(QRKeyListener.TYPE.PRESS, e -> submit(), KeyEvent.VK_ENTER);
 * listener.add(QRKeyListener.TYPE.PRESS, e -> close(), new int[]{KeyEvent.VK_ESCAPE});
 * listener.add(QRKeyListener.TYPE.PRESS, e -> help(), KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
 *
 * // 一个 action 绑定多个按键
 * listener.add(QRKeyListener.TYPE.PRESS, e -> move(), "shift b, ctrl b");
 *
 * // 移除指定按键下的 action
 * listener.remove(QRKeyListener.TYPE.PRESS, saveAction, "Ctrl + S");
 * }</pre>
 *
 * <p>{@link TYPE#TYPE} 对应 {@link KeyListener#keyTyped(KeyEvent)}，更适合任意字符输入后的回调；
 * 快捷键、功能键和组合键过滤建议使用 {@link TYPE#PRESS} 或 {@link TYPE#RELEASE}。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 14:54
 **/
public class QRKeyListener implements KeyListener {
	/**
	 * 按键事件类型。
	 */
	public enum TYPE {
		/**
		 * 字符输入事件，对应 {@link KeyListener#keyTyped(KeyEvent)}。
		 */
		TYPE,
		/**
		 * 按键按下事件，对应 {@link KeyListener#keyPressed(KeyEvent)}。
		 */
		PRESS,
		/**
		 * 按键释放事件，对应 {@link KeyListener#keyReleased(KeyEvent)}。
		 */
		RELEASE
	}

	private final KeyActionStore type = new KeyActionStore();
	private final KeyActionStore press = new KeyActionStore();
	private final KeyActionStore release = new KeyActionStore();

	/**
	 * 为指定按键事件类型添加全量操作。
	 *
	 * <p>只要发生对应类型的按键事件，该操作就会执行，不判断具体按键。</p>
	 *
	 * @param type 事件类型
	 * @param ar   操作
	 */
	public void add(TYPE type, QRActionRegister<KeyEvent> ar) {
		KeyActionStore store = store(type);
		if (store != null) {
			store.all.add(ar);
		}
	}

	/**
	 * 为指定按键事件类型添加按键过滤操作。
	 *
	 * <p>{@code keys} 为空时等同于 {@link #add(TYPE, QRActionRegister)}。传入 {@code keys} 时，
	 * 只有当前事件匹配其中任一按键，操作才会执行。可识别的按键参数：</p>
	 *
	 * <ul>
	 *     <li>{@link Integer}：单个 {@link KeyEvent} 键值，例如 {@code KeyEvent.VK_ENTER}</li>
	 *     <li>{@code int[]}：一个或两个整数，分别表示 keyCode 或 keyCode + modifiers</li>
	 *     <li>{@link String}：例如 {@code "Ctrl + S"}、{@code "shift b, ctrl b"}</li>
	 *     <li>{@link KeyStroke}</li>
	 * </ul>
	 *
	 * <p>无法识别的按键参数会被跳过。</p>
	 *
	 * @param type 事件类型
	 * @param ar   操作
	 * @param keys 按键过滤条件
	 */
	public void add(TYPE type, QRActionRegister<KeyEvent> ar, Object... keys) {
		if (keys == null || keys.length == 0) {
			add(type, ar);
			return;
		}
		KeyActionStore store = store(type);
		if (store == null) {
			return;
		}
		for (KeyStroke keyStroke : QRComponentUtils.parseKeyStrokes(keys)) {
			String value = keyValue(keyStroke);
			if (value != null) {
				store.keyed.computeIfAbsent(value, k -> new LinkedList<>()).add(ar);
			}
		}
	}

	/**
	 * 移除指定按键事件类型中的一个全量操作。
	 *
	 * <p>该方法只移除通过 {@link #add(TYPE, QRActionRegister)} 或空 {@code keys}
	 * 注册的全量操作，不会移除按键过滤列表中的操作。</p>
	 *
	 * @param type 事件类型
	 * @param ar   操作
	 * @return 是否移除成功
	 */
	public boolean remove(TYPE type, QRActionRegister<KeyEvent> ar) {
		KeyActionStore store = store(type);
		return store != null && store.all.remove(ar);
	}

	/**
	 * 移除指定按键事件类型、指定按键下的操作。
	 *
	 * <p>{@code keys} 为空时等同于 {@link #remove(TYPE, QRActionRegister)}。传入 {@code keys} 时，
	 * 只从匹配按键的过滤列表中移除该操作。</p>
	 *
	 * @param type 事件类型
	 * @param ar   操作
	 * @param keys 按键过滤条件
	 */
	public void remove(TYPE type, QRActionRegister<KeyEvent> ar, Object... keys) {
		if (keys == null || keys.length == 0) {
			remove(type, ar);
			return;
		}
		KeyActionStore store = store(type);
		if (store == null) {
			return;
		}
		for (KeyStroke keyStroke : QRComponentUtils.parseKeyStrokes(keys)) {
			String value = keyValue(keyStroke);
			LinkedList<QRActionRegister<KeyEvent>> list = value == null ? null : store.keyed.get(value);
			if (list != null) {
				list.remove(ar);
				if (list.isEmpty()) {
					store.keyed.remove(value);
				}
			}
		}
	}

	@Override
	public final void keyTyped(KeyEvent e) {
		runActions(type, e);
	}

	@Override
	public final void keyPressed(KeyEvent e) {
		runActions(press, e);
	}

	@Override
	public final void keyReleased(KeyEvent e) {
		runActions(release, e);
	}

	private KeyActionStore store(TYPE type) {
		return switch (type) {
			case TYPE -> this.type;
			case PRESS -> this.press;
			case RELEASE -> this.release;
		};
	}

	private String keyValue(KeyStroke keyStroke) {
		return keyStroke == null ? null : QRStringUtils.getKeyStrokeValue(keyStroke);
	}

	private String keyValue(KeyEvent e) {
		return e == null ? null : keyValue(QRStringUtils.getKeyStroke(e));
	}

	private void runActions(KeyActionStore store, KeyEvent e) {
		QRComponentUtils.runActions(store.all, e);
		String value = keyValue(e);
		LinkedList<QRActionRegister<KeyEvent>> list = value == null ? null : store.keyed.get(value);
		QRComponentUtils.runActions(list, e);
	}

	private static final class KeyActionStore {
		private final LinkedList<QRActionRegister<KeyEvent>> all = new LinkedList<>();
		private final Map<String, LinkedList<QRActionRegister<KeyEvent>>> keyed = new HashMap<>();
	}
}
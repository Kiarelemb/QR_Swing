package swing.qr.kiarelemb.component.listener;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.keyboard.event.GlobalKeyListener;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.inter.QRActionRegister;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-22 16:51
 **/
public class QRGlobalKeyboardHookListener extends GlobalKeyboardHook {
	private static final Map<Integer, Integer> GLOBAL_SHIFT_TO_KEY_EVENT = new HashMap<>();

	static {
		GLOBAL_SHIFT_TO_KEY_EVENT.put(13, KeyEvent.VK_ENTER);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(186, 59);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(91, 524);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(187, 61);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(188, 44);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(189, 45);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(190, 46);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(191, 47);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(192, 96);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(219, 91);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(220, 92);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(221, 93);
		GLOBAL_SHIFT_TO_KEY_EVENT.put(222, 39);
	}

	List<QRActionRegister> press = new ArrayList<>();
	List<QRActionRegister> release = new ArrayList<>();

	public QRGlobalKeyboardHookListener() throws UnsatisfiedLinkError {
		super(false);
		press.add(event -> keyPress((KeyStroke) event));
		release.add(event -> keyRelease((KeyStroke) event));

		addKeyListener(new GlobalKeyListener() {
			@Override
			public void keyPressed(GlobalKeyEvent globalKeyEvent) {
//				System.out.println("globalKeyEvent = " + globalKeyEvent);
				KeyStroke keyStroke = getKeyEvents(globalKeyEvent);
				if (keyStroke != null) {
					if (press.size() == 1) {
						press.get(0).action(keyStroke);
					} else {
						List<QRActionRegister> list = new ArrayList<>(press);
						list.forEach(ar -> ar.action(keyStroke));
					}
				}
			}

			@Override
			public void keyReleased(GlobalKeyEvent globalKeyEvent) {
				KeyStroke keyStroke = getKeyEvents(globalKeyEvent);
				if (keyStroke != null) {
					if (release.size() == 1) {
						release.get(0).action(keyStroke);
					} else {
						List<QRActionRegister> list = new ArrayList<>(release);
						list.forEach(ar -> ar.action(keyStroke));
					}
				}
			}
		});
	}

	/**
	 * @param press 为 {@code true} 则是 {@code KeyPress} 事件，否则为 {@code KeyRelease} 事件
	 * @param ar    {@link QRActionRegister#action(Object)} 中的参数类型为 {@link KeyStroke}
	 */
	public void addKeyListenerAction(boolean press, QRActionRegister ar) {
		if (press) {
			this.press.add(ar);
		} else {
			this.release.add(ar);
		}
	}

	protected void keyPress(KeyStroke keyStroke) {
	}

	protected void keyRelease(KeyStroke keyStroke) {
	}

	public static KeyStroke getKeyEvents(GlobalKeyEvent e) {
		StringBuilder sb = new StringBuilder();
		int modifiers = -1;
		int modifiersCount = 0;
		if (e.isControlPressed()) {
			sb.append("Control ");
			modifiersCount++;
			modifiers = KeyEvent.CTRL_DOWN_MASK;
		}
		if (e.isMenuPressed()) {
			sb.append("Alt ");
			modifiersCount++;
			modifiers = KeyEvent.ALT_DOWN_MASK;
		}
		if (e.isShiftPressed()) {
			sb.append("Shift ");
			modifiersCount++;
			modifiers = KeyEvent.SHIFT_DOWN_MASK;
		}
		if (e.isWinPressed()) {
			return QRStringUtils.getKeyStroke(KeyEvent.VK_WINDOWS);
		}
		sb.append(e.getKeyChar());
		String key = sb.toString();
		KeyStroke k = QRStringUtils.getKeyStroke(key.trim());
		if (k != null) {
			return k;
		}
		int keyCode = e.getVirtualKeyCode();
//		System.out.println("VirtualKeyCode() = " + keyCode);
		Integer code = GLOBAL_SHIFT_TO_KEY_EVENT.get(keyCode);
		keyCode = code != null ? code : keyCode;
		if (modifiers == -1) {
			return QRStringUtils.getKeyStroke(keyCode);
		} else {
			KeyStroke stroke;
			if (modifiersCount == 2) {
				if (e.isControlPressed() && e.isShiftPressed()) {
					modifiers = 195;
				} else if (e.isShiftPressed() && e.isMenuPressed()) {
					modifiers = 585;
				} else if (e.isControlPressed() && e.isMenuPressed()) {
					modifiers = 650;
				}
			} else if (modifiersCount == 3) {
				if (e.isControlPressed() && e.isShiftPressed() && e.isMenuPressed()) {
					modifiers = 715;
				}
			}
			stroke = QRStringUtils.getKeyStroke(keyCode, modifiers);
			return stroke;
		}
	}
}
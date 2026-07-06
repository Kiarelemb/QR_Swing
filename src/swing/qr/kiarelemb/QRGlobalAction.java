package swing.qr.kiarelemb;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.event.QRNativeKeyEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRNativeKeyListener;
import swing.qr.kiarelemb.listener.key.QRNativeKeyPressedListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局键盘事件。
 *
 * <p>本类既是 QR Swing 基于 JNativeHook 的全局键盘监听入口，也可以作为一条可注册、可注销的
 * 全局快捷键事件实例使用。新代码更推荐实例化写法：构造时传入事件，链式设置快捷键和焦点作用域，
 * 再通过 {@link #load()} 注册，通过 {@link #close()} 注销。</p>
 *
 * <p>实例化使用例：</p>
 * <pre><code>
 * QRGlobalAction saveAction = new QRGlobalAction(keyStroke -> save())
 *         .key("ctrl s")
 *         .window(this);
 *
 * saveAction.load();
 * saveAction.close();
 * </code></pre>
 *
 * <p>{@link #load()} 和 {@link #close()} 都是幂等方法。重复调用 {@code load()} 不会重复注册事件，
 * 重复调用 {@code close()} 也不会抛异常。实例已 {@code load()} 后不能再修改快捷键、窗体或焦点配置，
 * 如需修改，应先调用 {@code close()}。</p>
 *
 * <p>实例快捷键支持三种焦点作用域：</p>
 * <ul>
 *     <li>指定窗体焦点：调用 {@link #window(Window)} 后，默认只有该窗体处于焦点时触发。</li>
 *     <li>主窗体焦点：未设置 {@link #window(Window)}，但调用 {@link #focus(boolean) focus(true)} 时，
 *     只有 {@link #registerGlobalEventWindow(Window)} 注册的主窗体处于焦点时触发。</li>
 *     <li>系统级全局快捷键：调用 {@link #focus(boolean) focus(false)} 时，不判断 QR Swing 窗体焦点。</li>
 * </ul>
 *
 * <p>如果需要继续使用静态方法，本类仍保留 {@code registerGlobalAction/removeGlobalAction} 等兼容入口。
 * 静态入口使用前需要先调用 {@link #registerGlobalKeyEvents()} 安装 native hook；如果使用旧的
 * {@code mainWindowFocus=true} 语义，还需要在主窗体创建完成后调用 {@link #registerGlobalEventWindow(Window)}
 * 绑定主窗体。</p>
 *
 * <p>静态主窗体快捷键使用例：</p>
 * <pre><code>
 * QRGlobalAction.registerGlobalKeyEvents();
 * MainWindow window = new MainWindow();
 * QRGlobalAction.registerGlobalEventWindow(window);
 *
 * QRGlobalAction.registerGlobalAction("ctrl s", keyStroke -> window.save(), true);
 * QRGlobalAction.registerGlobalAction(KeyEvent.VK_F5, keyStroke -> window.refresh(), true);
 * </code></pre>
 *
 * <p>静态子窗体快捷键使用例：</p>
 * <pre><code>
 * QRDialog dialog = new QRDialog(window, false);
 * QRActionRegister&lt;KeyStroke&gt; closeAction = keyStroke -> dialog.dispose();
 *
 * QRGlobalAction.registerGlobalAction(KeyEvent.VK_ESCAPE, closeAction, dialog);
 * QRGlobalAction.removeGlobalAction(KeyEvent.VK_ESCAPE, closeAction, dialog);
 * </code></pre>
 *
 * <p>静态系统级全局快捷键使用例：</p>
 * <pre><code>
 * QRGlobalAction.registerGlobalAction("ctrl alt h", keyStroke -> showHelp(), false);
 * QRGlobalAction.registerGlobalAction("shift F5", keyStroke -> refreshAll(), (Window) null);
 * </code></pre>
 *
 * <p>同一个快捷键可以同时注册到不同作用域。移除事件时必须使用与注册时一致的作用域参数，
 * 例如注册时传入某个 {@link Window}，移除时也要传入同一个窗体实例。</p>
 *
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRGlobalAction
 * @description 全局键盘事件
 * @create 2026/7/5 22:07
 */
public final class QRGlobalAction implements AutoCloseable {
	/**
	 * 获取该监听器请调用 {@link #getGlobalKeyListener()}
	 */
	private static QRNativeKeyListener globalKeyListener;

	private final QRActionRegister<KeyStroke> action;
	private final List<KeyStroke> keyStrokes = new ArrayList<>();
	private Window focusWindow;
	private Boolean focus;
	private boolean loaded;

	/**
	 * 创建一条可通过 {@link #load()} 注册、通过 {@link #close()} 注销的全局快捷键事件。
	 *
	 * <p>链式配置的焦点语义：</p>
	 * <ul>
	 *     <li>{@code window(window)}：默认只在指定窗体处于焦点时触发。</li>
	 *     <li>{@code focus(false)}：不限制窗体焦点，作为系统级全局快捷键触发。</li>
	 *     <li>{@code focus(true)} 且未设置 {@code window}：使用旧的主窗体焦点语义。</li>
	 * </ul>
	 *
	 * @param action 快捷键触发时执行的事件
	 */
	public QRGlobalAction(QRActionRegister<KeyStroke> action) {
		this.action = action;
	}

	/**
	 * 设置快捷键，支持以英文逗号分隔的多个快捷键。
	 *
	 * @param key 按键组合，格式同 {@link #registerGlobalAction(String, QRActionRegister, boolean)}
	 * @return 当前实例
	 */
	public QRGlobalAction key(String key) {
		ensureMutable();
		this.keyStrokes.clear();
		String[] keys = key.split(",");
		for (String k : keys) {
			this.keyStrokes.add(QRStringUtils.getKeyStroke(k));
		}
		return this;
	}

	/**
	 * 设置快捷键。
	 *
	 * @param keycode 键值
	 * @return 当前实例
	 */
	public QRGlobalAction key(int keycode) {
		return key(QRStringUtils.getKeyStroke(keycode));
	}

	/**
	 * 设置快捷键。
	 *
	 * @param keycode   键值
	 * @param modifiers 特殊键
	 * @return 当前实例
	 */
	public QRGlobalAction key(int keycode, int modifiers) {
		return key(QRStringUtils.getKeyStroke(keycode, modifiers));
	}

	/**
	 * 设置快捷键。
	 *
	 * @param keyStroke 按键组合
	 * @return 当前实例
	 */
	public QRGlobalAction key(KeyStroke keyStroke) {
		ensureMutable();
		this.keyStrokes.clear();
		if (keyStroke != null) {
			this.keyStrokes.add(keyStroke);
		}
		return this;
	}

	/**
	 * 设置指定窗体焦点作用域。
	 *
	 * @param focusWindow 指定的焦点判断窗体
	 * @return 当前实例
	 */
	public QRGlobalAction window(Window focusWindow) {
		ensureMutable();
		this.focusWindow = focusWindow;
		return this;
	}

	/**
	 * 设置是否要求窗体焦点。
	 *
	 * <p>设置了 {@link #window(Window)} 且 {@code focus=true} 时，事件只在指定窗体处于焦点时触发。
	 * 未设置 {@link #window(Window)} 且 {@code focus=true} 时，事件使用旧的主窗体焦点语义。
	 * {@code focus=false} 时，事件不限制窗体焦点。</p>
	 *
	 * @param focus 是否要求焦点
	 * @return 当前实例
	 */
	public QRGlobalAction focus(boolean focus) {
		ensureMutable();
		this.focus = focus;
		return this;
	}

	/**
	 * 获取快捷键触发时执行的事件。
	 *
	 * @return 快捷键事件
	 */
	public QRActionRegister<KeyStroke> action() {
		return this.action;
	}

	/**
	 * 获取当前实例保存的快捷键列表。
	 *
	 * @return 不可修改的快捷键列表
	 */
	public List<KeyStroke> keyStrokes() {
		return List.copyOf(this.keyStrokes);
	}

	/**
	 * 获取指定的焦点判断窗体。
	 *
	 * @return 焦点判断窗体，未设置时为 {@code null}
	 */
	public Window focusWindow() {
		return this.focusWindow;
	}

	/**
	 * 获取是否要求窗体焦点。
	 *
	 * @return 焦点配置；未显式调用 {@link #focus(boolean)} 时为 {@code null}
	 */
	public Boolean focus() {
		return this.focus;
	}

	/**
	 * 获取当前实例是否已经注册。
	 *
	 * @return 已注册时返回 {@code true}
	 */
	public boolean loaded() {
		return this.loaded;
	}

	/**
	 * 复制当前配置，返回一个未注册的新实例。
	 *
	 * <p>该方法会复制 action、快捷键、焦点窗体和焦点配置，但不会复制 {@link #loaded()} 状态。
	 * 返回的新实例可以继续通过链式方法修改个别字段后再 {@link #load()}。</p>
	 *
	 * @return 未注册的新实例
	 */
	public QRGlobalAction copy() {
		QRGlobalAction copied = new QRGlobalAction(this.action);
		copied.keyStrokes.addAll(this.keyStrokes);
		copied.focusWindow = this.focusWindow;
		copied.focus = this.focus;
		return copied;
	}

	/**
	 * 注册当前实例保存的全局快捷键事件。
	 *
	 * <p>该方法幂等：已注册时再次调用不会重复注册。</p>
	 */
	public void load() {
		if (this.loaded) {
			return;
		}
		if (this.action == null) {
			throw new NullPointerException("全局快捷键事件为空");
		}
		if (this.keyStrokes.isEmpty()) {
			throw new IllegalStateException("全局快捷键为空，请先调用 key(...) 方法");
		}
		registerGlobalKeyEvents();
		for (KeyStroke keyStroke : this.keyStrokes) {
			if (useWindowFocus()) {
				registerGlobalAction(keyStroke, this.action, this.focusWindow);
			} else {
				registerGlobalAction(keyStroke, this.action, Boolean.TRUE.equals(this.focus));
			}
		}
		this.loaded = true;
	}

	/**
	 * 注销当前实例保存的全局快捷键事件。
	 *
	 * <p>该方法幂等：未注册时调用不会抛异常。</p>
	 */
	@Override
	public void close() {
		if (!this.loaded) {
			return;
		}
		for (KeyStroke keyStroke : this.keyStrokes) {
			if (useWindowFocus()) {
				removeGlobalAction(keyStroke, this.action, this.focusWindow);
			} else {
				removeGlobalAction(keyStroke, this.action, Boolean.TRUE.equals(this.focus));
			}
		}
		this.loaded = false;
	}

	private boolean useWindowFocus() {
		return this.focusWindow != null && !Boolean.FALSE.equals(this.focus);
	}

	private void ensureMutable() {
		if (this.loaded) {
			throw new IllegalStateException("已注册的全局快捷键事件不能修改，请先调用 close()");
		}
	}

	//region 静态方法

	/**
	 * 用于注册全局键盘事件，监听器是 {@link QRNativeKeyPressedListener}
	 * <p>若主窗体在实例化过程中设置了快捷键，或注册了按键事件，则请在主窗体实例化之前，调用该方法。</p>
	 * <p>再在主窗体实例化完之后，调用 {@link #registerGlobalEventWindow(Window)}</p>
	 * 例如：
	 * <pre><code>
	 *     QRGlobalAction.registerGlobalKeyEvents();
	 *     MainWindow window = new MainWindow();
	 *     QRGlobalAction.registerGlobalEventWindow(window);
	 * </code></pre>
	 *
	 * @see #registerGlobalEventWindow(Window)
	 */
	public static void registerGlobalKeyEvents() {
		if (globalKeyListener == null) {
			try {
				GlobalScreen.registerNativeHook();
			} catch (NativeHookException e) {
				throw new RuntimeException(e);
			}
			globalKeyListener = new QRNativeKeyListener();
			GlobalScreen.addNativeKeyListener(globalKeyListener);
		}
	}

	/**
	 * 用于注册全局键盘事件的主窗体，注意，该方法的调用应晚于 {@link #registerGlobalKeyEvents()}
	 * <p>若主窗体在实例化过程中设置了快捷键，或注册了按键事件，则请在主窗体实例化之前，调用该方法。</p>
	 * <p>再在主窗体实例化完之后，调用 {@link #registerGlobalEventWindow(Window)}</p>
	 * 例如：
	 * <pre><code>
	 *     QRGlobalAction.registerGlobalKeyEvents();
	 *     MainWindow window = new MainWindow();
	 *     QRGlobalAction.registerGlobalEventWindow(window);
	 * </code></pre>
	 *
	 * @param window 主窗体
	 * @see #registerGlobalKeyEvents()
	 */
	public static void registerGlobalEventWindow(Window window) {
		getGlobalKeyListener().registerMainWindow(window);
	}

	/**
	 * 当已另注册了一个全局键盘监听器时，可以直接设置，而不用新实例化。
	 *
	 * @param globalKeyListener 已设置的监听器
	 */
	public static void setGlobalKeyEventsListener(QRNativeKeyListener globalKeyListener) {
		if (QRGlobalAction.globalKeyListener == null && globalKeyListener != null) {
			QRGlobalAction.globalKeyListener = globalKeyListener;
			try {
				GlobalScreen.registerNativeHook();
			} catch (NativeHookException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 获取全局键盘监听器。
	 *
	 * @return 全局键盘监听器
	 * @throws NullPointerException 未调用 {@link #registerGlobalKeyEvents()} 或
	 *                              {@link #setGlobalKeyEventsListener(QRNativeKeyListener)} 时抛出
	 */
	public static QRNativeKeyListener getGlobalKeyListener() {
		if (globalKeyListener == null) {
			throw new NullPointerException("全局键盘监听器为空，请先调用 QRGlobalAction.registerGlobalKeyEvents() 或 setGlobalKeyEventsListener(QRNativeKeyListener) 方法");
		}
		return globalKeyListener;
	}

	/**
	 * 添加键盘按键事件，提供多个快捷键对应一个Action的功能
	 * <p> 方法 {@link QRGlobalAction#registerGlobalKeyEvents()} 被调用了才生效
	 * <p>使用例：
	 * <pre><code>
	 * QRGlobalAction.registerGlobalAction("ctrl s, meta s", keyStroke -> save(), true);
	 * QRGlobalAction.registerGlobalAction("shift F5", keyStroke -> refresh(), false);
	 * </code></pre>
	 *
	 * @param key             按键组合，不同按键组合间以英文逗号{@code ,}分割
	 *                        <p>有+号则优先以+号分割，再以空格分割
	 *                        <p>支持格式 {@code Ctrl + Alt + Shift + s}、{@code a}、{@code shift a}、{@code shift b,ctrl a}、
	 *                        <p>{@code shift b, ctrl b}，但不支持 Windows 键的组合
	 * @param ar              事件，其参数是 {@link KeyStroke}
	 * @param mainWindowFocus 事件是否只在已注册主窗体处于焦点时触发。若为 {@code false}，则为系统级全局快捷键，
	 *                        不论主窗体是否处于焦点状态都会触发
	 */
	public static void registerGlobalAction(String key, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
		String[] keys = key.split(",");
		for (String k : keys) {
			var keyStroke = QRStringUtils.getKeyStroke(k);
			registerGlobalAction(keyStroke, ar, mainWindowFocus);
		}
	}

	/**
	 * 添加键盘按键事件，提供多个快捷键对应一个Action的功能
	 * <p> 方法 {@link QRGlobalAction#registerGlobalKeyEvents()} 被调用了才生效
	 *
	 * @param key         按键组合，格式同 {@link #registerGlobalAction(String, QRActionRegister, boolean)}
	 * @param ar          事件，其参数是 {@link KeyStroke}
	 * @param focusWindow 事件是否只在指定窗体处于焦点时触发。为 {@code null} 时，则为系统级全局快捷键，
	 *                    不论窗体是否处于焦点状态都会触发
	 */
	public static void registerGlobalAction(String key, QRActionRegister<KeyStroke> ar, Window focusWindow) {
		String[] keys = key.split(",");
		for (String k : keys) {
			var keyStroke = QRStringUtils.getKeyStroke(k);
			registerGlobalAction(keyStroke, ar, focusWindow);
		}
	}

	/**
	 * 添加键盘按键事件
	 * <p> 方法 {@link QRGlobalAction#registerGlobalKeyEvents()} 被调用了才生效
	 *
	 * @param keycode         键值
	 * @param ar              事件，其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
	 * @param mainWindowFocus 事件是否只在已注册主窗体处于焦点时触发。若为 {@code false}，则为系统级全局快捷键
	 */
	public static void registerGlobalAction(int keycode, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode);
		registerGlobalAction(keyStroke, ar, mainWindowFocus);
	}

	/**
	 * 添加键盘按键事件
	 * <p> 方法 {@link QRGlobalAction#registerGlobalKeyEvents()} 被调用了才生效
	 *
	 * @param keycode     键值
	 * @param ar          事件，其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
	 * @param focusWindow 事件是否只在指定窗体处于焦点时触发。若为 {@code null}，则为系统级全局快捷键
	 */
	public static void registerGlobalAction(int keycode, QRActionRegister<KeyStroke> ar, Window focusWindow) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode);
		registerGlobalAction(keyStroke, ar, focusWindow);
	}

	/**
	 * 添加键盘按键事件
	 * <p> 方法 {@link QRGlobalAction#registerGlobalKeyEvents()} 被调用了才生效
	 *
	 * @param keycode         键值
	 * @param modifiers       特殊键
	 * @param ar              事件，其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
	 * @param mainWindowFocus 事件是否只在已注册主窗体处于焦点时触发。若为 {@code false}，则为系统级全局快捷键
	 */
	public static void registerGlobalAction(int keycode, int modifiers, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode, modifiers);
		registerGlobalAction(keyStroke, ar, mainWindowFocus);
	}

	/**
	 * 添加键盘按键事件
	 * <p> 方法 {@link QRGlobalAction#registerGlobalKeyEvents()} 被调用了才生效
	 *
	 * @param keycode     键值
	 * @param modifiers   特殊键
	 * @param ar          事件，其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
	 * @param focusWindow 事件是否只在指定窗体处于焦点时触发。若为 {@code null}，则为系统级全局快捷键
	 */
	public static void registerGlobalAction(int keycode, int modifiers, QRActionRegister<KeyStroke> ar, Window focusWindow) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode, modifiers);
		registerGlobalAction(keyStroke, ar, focusWindow);
	}

	/**
	 * 添加键盘按键事件
	 * <p> 方法 {@link QRGlobalAction#registerGlobalKeyEvents()} 被调用了才生效
	 *
	 * @param keyStroke       按键组合
	 * @param ar              事件，其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
	 * @param mainWindowFocus 事件是否只在已注册主窗体处于焦点时触发。若为 {@code false}，则为系统级全局快捷键
	 */
	public static void registerGlobalAction(KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
		if (QRGlobalAction.globalKeyListener != null) {
			QRGlobalAction.globalKeyListener.addEvent(QRNativeKeyListener.TYPE.PRESSED, mainWindowFocus, keyStroke, ar);
		}
	}

	/**
	 * 添加键盘按键事件
	 * <p> 方法 {@link QRGlobalAction#registerGlobalKeyEvents()} 被调用了才生效
	 *
	 * @param keyStroke   按键组合
	 * @param ar          事件，其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
	 * @param focusWindow 事件是否只在指定窗体处于焦点时触发。若为 {@code null}，则为系统级全局快捷键
	 */
	public static void registerGlobalAction(KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, Window focusWindow) {
		if (QRGlobalAction.globalKeyListener != null) {
			QRGlobalAction.globalKeyListener.addEvent(QRNativeKeyListener.TYPE.PRESSED, focusWindow, keyStroke, ar);
		}
	}

	/**
	 * 清空某一快捷键所对应的全部事件
	 *
	 * @param keyStroke       按键组合
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void removeGlobalAction(KeyStroke keyStroke, boolean mainWindowFocus) {
		if (QRGlobalAction.globalKeyListener != null) {
			QRGlobalAction.globalKeyListener.removeEvent(QRNativeKeyListener.TYPE.PRESSED, keyStroke, mainWindowFocus);
		}
	}

	/**
	 * 清空某一快捷键所对应的全部事件
	 *
	 * @param keyStroke   按键组合
	 * @param focusWindow 事件是否是在指定窗体处于焦点时才触发。为 {@code null} 时清空系统级全局快捷键
	 */
	public static void removeGlobalAction(KeyStroke keyStroke, Window focusWindow) {
		if (QRGlobalAction.globalKeyListener != null) {
			QRGlobalAction.globalKeyListener.removeEvent(QRNativeKeyListener.TYPE.PRESSED, keyStroke, focusWindow);
		}
	}

	/**
	 * 移除指定的全局事件
	 *
	 * @param keyStroke       按键组合
	 * @param ar              事件
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void removeGlobalAction(KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
		if (QRGlobalAction.globalKeyListener != null) {
			QRGlobalAction.globalKeyListener.removeEvent(QRNativeKeyListener.TYPE.PRESSED, keyStroke, ar, mainWindowFocus);
		}
	}

	/**
	 * 移除指定的全局事件
	 *
	 * @param keyStroke   按键组合
	 * @param ar          事件
	 * @param focusWindow 事件是否是在指定窗体处于焦点时才触发。为 {@code null} 时从系统级全局快捷键中移除
	 */
	public static void removeGlobalAction(KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, Window focusWindow) {
		if (QRGlobalAction.globalKeyListener != null) {
			QRGlobalAction.globalKeyListener.removeEvent(QRNativeKeyListener.TYPE.PRESSED, keyStroke, ar, focusWindow);
		}
	}

	/**
	 * 清空某一快捷键所对应的全部事件
	 *
	 * @param key             按键组合，格式同 {@link #registerGlobalAction(String, QRActionRegister, boolean)}
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void removeGlobalAction(String key, boolean mainWindowFocus) {
		String[] keys = key.split(",");
		for (String k : keys) {
			var keyStroke = QRStringUtils.getKeyStroke(k);
			removeGlobalAction(keyStroke, mainWindowFocus);
		}
	}

	/**
	 * 清空某一快捷键所对应的全部事件
	 *
	 * @param key         按键组合，格式同 {@link #registerGlobalAction(String, QRActionRegister, boolean)}
	 * @param focusWindow 事件是否是在指定窗体处于焦点时才触发。为 {@code null} 时清空系统级全局快捷键
	 */
	public static void removeGlobalAction(String key, Window focusWindow) {
		String[] keys = key.split(",");
		for (String k : keys) {
			var keyStroke = QRStringUtils.getKeyStroke(k);
			removeGlobalAction(keyStroke, focusWindow);
		}
	}

	/**
	 * 移除指定的全局事件
	 *
	 * @param key             按键组合，格式同 {@link #registerGlobalAction(String, QRActionRegister, boolean)}
	 * @param ar              事件
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void removeGlobalAction(String key, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
		String[] keys = key.split(",");
		for (String k : keys) {
			var keyStroke = QRStringUtils.getKeyStroke(k);
			removeGlobalAction(keyStroke, ar, mainWindowFocus);
		}
	}

	/**
	 * 移除指定的全局事件
	 *
	 * @param key         按键组合，格式同 {@link #registerGlobalAction(String, QRActionRegister, boolean)}
	 * @param ar          事件
	 * @param focusWindow 事件是否是在指定窗体处于焦点时才触发。为 {@code null} 时从系统级全局快捷键中移除
	 */
	public static void removeGlobalAction(String key, QRActionRegister<KeyStroke> ar, Window focusWindow) {
		String[] keys = key.split(",");
		for (String k : keys) {
			var keyStroke = QRStringUtils.getKeyStroke(k);
			removeGlobalAction(keyStroke, ar, focusWindow);
		}
	}

	/**
	 * 清空某一快捷键所对应的全部事件
	 *
	 * @param keycode         键值
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void removeGlobalAction(int keycode, boolean mainWindowFocus) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode);
		removeGlobalAction(keyStroke, mainWindowFocus);
	}

	/**
	 * 清空某一快捷键所对应的全部事件
	 *
	 * @param keycode     键值
	 * @param focusWindow 事件是否是在指定窗体处于焦点时才触发。为 {@code null} 时清空系统级全局快捷键
	 */
	public static void removeGlobalAction(int keycode, Window focusWindow) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode);
		removeGlobalAction(keyStroke, focusWindow);
	}

	/**
	 * 移除指定的全局事件
	 *
	 * @param keycode         键值
	 * @param ar              事件
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void removeGlobalAction(int keycode, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode);
		removeGlobalAction(keyStroke, ar, mainWindowFocus);
	}

	/**
	 * 移除指定的全局事件
	 *
	 * @param keycode     键值
	 * @param ar          事件
	 * @param focusWindow 事件是否是在指定窗体处于焦点时才触发。为 {@code null} 时从系统级全局快捷键中移除
	 */
	public static void removeGlobalAction(int keycode, QRActionRegister<KeyStroke> ar, Window focusWindow) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode);
		removeGlobalAction(keyStroke, ar, focusWindow);
	}

	/**
	 * 清空某一快捷键所对应的全部事件
	 *
	 * @param keycode         键值
	 * @param modifiers       特殊键
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void removeGlobalAction(int keycode, int modifiers, boolean mainWindowFocus) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode, modifiers);
		removeGlobalAction(keyStroke, mainWindowFocus);
	}

	/**
	 * 清空某一快捷键所对应的全部事件
	 *
	 * @param keycode     键值
	 * @param modifiers   特殊键
	 * @param focusWindow 事件是否是在指定窗体处于焦点时才触发。为 {@code null} 时清空系统级全局快捷键
	 */
	public static void removeGlobalAction(int keycode, int modifiers, Window focusWindow) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode, modifiers);
		removeGlobalAction(keyStroke, focusWindow);
	}

	/**
	 * 移除指定的全局事件
	 *
	 * @param keycode         键值
	 * @param modifiers       特殊键
	 * @param ar              事件
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void removeGlobalAction(int keycode, int modifiers, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode, modifiers);
		removeGlobalAction(keyStroke, ar, mainWindowFocus);
	}

	/**
	 * 移除指定的全局事件
	 *
	 * @param keycode     键值
	 * @param modifiers   特殊键
	 * @param ar          事件
	 * @param focusWindow 事件是否是在指定窗体处于焦点时才触发。为 {@code null} 时从系统级全局快捷键中移除
	 */
	public static void removeGlobalAction(int keycode, int modifiers, QRActionRegister<KeyStroke> ar, Window focusWindow) {
		var keyStroke = QRStringUtils.getKeyStroke(keycode, modifiers);
		removeGlobalAction(keyStroke, ar, focusWindow);
	}

	/**
	 * 在外部运行快捷键事件
	 *
	 * @param window          主窗体
	 * @param keyStroke       快捷键
	 * @param mainWindowFocus 主窗体是否在焦点
	 */
	public static void invokeGlobalAction(Window window, KeyStroke keyStroke, boolean mainWindowFocus) {
		if (QRGlobalAction.globalKeyListener != null) {
			QRGlobalAction.globalKeyListener.invokeAction(window, QRNativeKeyListener.TYPE.PRESSED, keyStroke, mainWindowFocus);
		}
	}

	/**
	 * 在外部运行快捷键事件
	 *
	 * @param focusWindow 指定的焦点判断窗体。为 {@code null} 时运行系统级全局快捷键事件
	 * @param keyStroke   快捷键
	 */
	public static void invokeGlobalAction(Window focusWindow, KeyStroke keyStroke) {
		if (QRGlobalAction.globalKeyListener != null) {
			QRGlobalAction.globalKeyListener.invokeAction(QRNativeKeyListener.TYPE.PRESSED, focusWindow, keyStroke);
		}
	}

	//endregion
}
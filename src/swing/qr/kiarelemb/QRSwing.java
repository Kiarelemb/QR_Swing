package swing.qr.kiarelemb;

import method.qr.kiarelemb.utils.*;
import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.component.listener.QRGlobalKeyboardHookListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.io.File.separator;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 总设计类
 * @create 2022-11-04 15:05
 **/
public class QRSwing {
	/**
	 * {@link swing.qr.kiarelemb.window.basic.QRFrame} 的资源文件
	 */
	public static String WINDOW_PROP_PATH = "window.properties";
	public static final String WINDOW_IMAGE_ENABLE = "window.image.enable";
	public static final String WINDOW_THEME = "window.theme";
	public static final String WINDOW_IMAGE_PATH = "window.image.path";
	public static final String WINDOW_ROUND = "window.round";
	public static final String WINDOW_TITLE_MENU = "window.title.menu";
	public static final String WINDOW_TRANSPARENCY = "window.transparency";
	public static final String WINDOW_BACKGROUND_IMAGE_ALPHA = "window.background.image.alpha";
	public static final String WINDOW_BACKGROUND_IMAGE_SCALE = "window.background.image.scale";
	public static final String WINDOW_ALWAYS_TOP = "window.always.top";
	public static final String WINDOW_ABSORB = "window.absorb";
	/**
	 * 各种临时文件的存放目录
	 */
	public static final String TMP_DIRECTORY = "tmp" + separator;
	/**
	 * 主题文件的存放目录
	 */
	public static final String THEME_DIRECTORY = "theme" + separator;
	/**
	 * 单例模式
	 */
	public static QRSwing INSTANCE;
	/**
	 * 各种窗体的左上角的图标
	 */
	public static ImageIcon windowIcon;
	/**
	 * 窗体主题名
	 */
	public static String theme;
	/**
	 * 窗体是否圆角
	 */
	public static boolean windowRound;
	/**
	 * 是否启用主窗体的背景图片
	 */
	public static boolean windowImageEnable;
	/**
	 * 菜单栏是否放置于窗体的标题栏
	 */
	public static boolean windowTitleMenu;
	/**
	 * 窗体是否置顶
	 */
	public static boolean windowAlwaysOnTop;
	/**
	 * 设置窗体是否吸附屏幕
	 */
	public static boolean windowScreenAdsorb;
	/**
	 * 窗体的透明度
	 */
	public static float windowTransparency;
	/**
	 * 主窗体设置背景图时的透明度
	 */
	public static float windowAlpha = 0.8f;
	/**
	 * 主窗体设置背景图时是否让图片自适应
	 */
	public static boolean windowScale = true;

	public static String windowBackgroundImagePath;
	/**
	 * 字体名可能为 {@code null}，默认的全局字体为 微软雅黑
	 */
	public static Font globalFont;
	public static String GLOBAL_PROP_PATH;
	/**
	 * 全局配置文件
	 */
	public static final Properties GLOBAL_PROP = new Properties();
	/**
	 * 主窗体不处于焦点时的事件集
	 */
	private static final Map<KeyStroke, ArrayList<QRActionRegister>> GLOBAL_KEY_EVENTS = new HashMap<>();
	/**
	 * 所有事件集
	 */
	private static final Map<KeyStroke, ArrayList<QRActionRegister>> ALL_KEY_EVENTS = new HashMap<>();
	private static final ArrayList<QRActionRegister> actionAfterClose = new ArrayList<>();

	private QRSwing(String propPath) {
		GLOBAL_PROP_PATH = propPath;
		//设置为系统外观
		QRSystemUtils.setSystemLookAndFeel();
		//设置窗体可透明
		if (QRSystemUtils.IS_WINDOWS) {
			System.setProperty("sun.java2d.noddraw", "true");
		}
		//加载配置文件
		loadProp(propPath);
		//加载配置
		load();
		//创建临时文件夹
		QRFileUtils.dirCreate(TMP_DIRECTORY);
		//在程序退出时自动保存配置文件
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			QRComponentUtils.runActions(actionAfterClose);
			globalPropSave();
		}));
	}


	/**
	 * 本工具包开始于此方法。在所有窗体或控件调用前，都必须先调用该方法
	 *
	 * <p>在调用本方法时，将使用默认的设置进行配置，其文件 {@link QRSwing#GLOBAL_PROP_PATH} 和 {@link QRSwing#WINDOW_PROP_PATH} 将会创立在程序的根目录下</p>
	 */
	public static void start() {
		INSTANCE = new QRSwing("settings.properties");
		//加载主题
		QRColorsAndFonts.loadTheme();
	}

	/**
	 * 本工具包开始于此方法。在所有窗体或控件调用前，都必须先调用该方法
	 *
	 * <p>窗体位置的资源文件 {@link QRSwing#WINDOW_PROP_PATH} 将会创立在程序的根目录</p>
	 *
	 * @param propPath 资源文件的路径
	 */
	public static void start(String propPath) {
		INSTANCE = new QRSwing(propPath);
		//加载主题
		QRColorsAndFonts.loadTheme();
	}

	/**
	 * 本工具包开始于此方法。在所有窗体或控件调用前，都必须先调用该方法
	 *
	 * @param propPath       资源文件的路径
	 * @param windowPropPath 主窗体资源文件路径
	 */
	public static void start(String propPath, String windowPropPath) {
		QRSwing.WINDOW_PROP_PATH = windowPropPath;
		start(propPath);
	}

	/**
	 * 各种配置加载方法
	 *
	 * @param propFilePath properties 文件路径
	 */
	private void loadProp(String propFilePath) {
		if (!QRFileUtils.fileExists(propFilePath)) {
			if (!QRFileUtils.fileCreate(propFilePath)) {
				GLOBAL_PROP_PATH = "settings.properties";
			}
			globalPropBackToDefault();
			return;
		}
		QRPropertiesUtils.loadProp(GLOBAL_PROP, propFilePath);
	}

	private void load() {
		windowIcon = iconLoadLead("window.icon.path", "QR.png");
		theme = QRPropertiesUtils.getPropInString(GLOBAL_PROP, WINDOW_THEME, "深色", true);
		windowBackgroundImagePath = QRPropertiesUtils.getPropInString(GLOBAL_PROP, WINDOW_IMAGE_PATH, null, false);
		windowRound = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_ROUND, true, true);
		windowImageEnable = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_IMAGE_ENABLE, true, true);
		windowTitleMenu = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_TITLE_MENU, true, true);
		windowTransparency = QRPropertiesUtils.getPropInFloat(GLOBAL_PROP, WINDOW_TRANSPARENCY, 0.999f, true);
		windowAlpha = QRPropertiesUtils.getPropInFloat(GLOBAL_PROP, WINDOW_BACKGROUND_IMAGE_ALPHA, 0.8f, true);
		windowScale = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_BACKGROUND_IMAGE_SCALE, true, true);
		windowAlwaysOnTop = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_ALWAYS_TOP, false, true);
		windowScreenAdsorb = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_ABSORB, true, true);
		if (!QRFileUtils.fileExists(WINDOW_PROP_PATH)) {
			windowPropBackToDefault();
		}
		if (windowAlpha < 0 || windowAlpha > 1) {
			windowAlpha = 0.8f;
			GLOBAL_PROP.setProperty(WINDOW_BACKGROUND_IMAGE_ALPHA, String.valueOf(windowAlpha));
		}
		if (windowAlpha < 0.5) {
			windowAlpha = 1 - windowAlpha;
			GLOBAL_PROP.setProperty(WINDOW_BACKGROUND_IMAGE_ALPHA, String.valueOf(windowAlpha));
		}
	}

	public static ImageIcon iconLoadLead(String key, String defaultFileName) {
		String iconPath = GLOBAL_PROP.getProperty(key);
		if (QRFileUtils.fileExists(iconPath)) {
			return new ImageIcon(iconPath);
		}
		return new ImageIcon(QRSwingInfo.loadUrl(defaultFileName));
	}

	/**
	 * 保存全局配置文件
	 */
	public static void globalPropSave() {
		QRPropertiesUtils.storeProp(QRSwing.GLOBAL_PROP, GLOBAL_PROP_PATH);
	}

	/**
	 * 窗体大小及位置复原
	 */
	public static void windowPropBackToDefault() {
		Properties prop = new Properties();
		final int[] size = QRSystemUtils.getScreenSize();
		final int width = size[0];
		final int height = size[1];
		prop.setProperty("Window.size.width", String.valueOf(width / 2));
		prop.setProperty("Window.size.height", String.valueOf(height / 2));
		prop.setProperty("Window.start.X", String.valueOf(width / 4));
		prop.setProperty("Window.start.Y", String.valueOf(height / 4));
		QRPropertiesUtils.storeProp(prop, WINDOW_PROP_PATH);
	}

	public static void globalPropBackToDefault() {
		Properties prop = getDefaultSettingsProp();
		GLOBAL_PROP.putAll(prop);
		globalPropSave();
	}

	/**
	 * 获取全局默认配置的资源文件，该文件的内容为：
	 * <p>window.absorb=true
	 * <p>window.always.top=false
	 * <p>window.background.image.alpha=0.8
	 * <p>window.background.image.scale=true
	 * <p>window.image.enable=true
	 * <p>window.image.path=
	 * <p>window.round=true
	 * <p>window.theme=深色
	 * <p>window.title.menu=false
	 * <p>window.transparency=0.8
	 */
	public static Properties getDefaultSettingsProp() {
		URL url = QRSwingInfo.loadUrl("default_settings.properties");
		return QRPropertiesUtils.loadProp(url);
	}

	/**
	 * 修改全局默认的字体
	 *
	 * @param fontFamily 字体名称
	 */
	public static void customFontName(String fontFamily) {
		Font font = QRFontUtils.getFont(fontFamily, 10);
		customFontName(font);
	}

	/**
	 * 修改全局默认的字体
	 *
	 * @param font 字体
	 */
	public static void customFontName(Font font) {
		QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT = font.deriveFont(QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT.getSize2D());
		QRColorsAndFonts.DEFAULT_FONT_MENU = font.deriveFont(QRColorsAndFonts.DEFAULT_FONT_MENU.getSize2D());
		QRColorsAndFonts.STANDARD_FONT_TEXT = font.deriveFont(QRColorsAndFonts.STANDARD_FONT_TEXT.getSize2D());
		UIManager.put("ToolTip.font", QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT);
		globalFont = font;
	}

	/**
	 * 添加全局配置
	 *
	 * @param key   键
	 * @param value 值
	 */
	public static void setGlobalSetting(String key, String value) {
		if (value == null) {
			removeGlobalSetting(key);
		} else {
			GLOBAL_PROP.setProperty(key, value);
		}
	}

	/**
	 * 删除全局配置
	 *
	 * @param key 键
	 */
	public static void removeGlobalSetting(String key) {
		GLOBAL_PROP.remove(key);
	}

	/**
	 * 可以动态设置当前主题。只消将项目中继承了 {@link swing.qr.kiarelemb.window.basic.QRFrame} 的子类，调用 {@link QRFrame#componentFresh()} 即可。
	 *
	 * @param value 主题名
	 */
	public static void setTheme(String value) {
		QRSwing.theme = value;
		GLOBAL_PROP.setProperty(WINDOW_THEME, value);
		QRColorsAndFonts.loadTheme();
	}

	public static void setWindowImageEnable(boolean value) {
		QRSwing.windowImageEnable = value;
		GLOBAL_PROP.setProperty(WINDOW_IMAGE_ENABLE, String.valueOf(value));
	}

	public static void setWindowBackgroundImagePath(String value) {
		if (value == null) {
			QRSwing.windowBackgroundImagePath = null;
			GLOBAL_PROP.remove(WINDOW_IMAGE_PATH);
		} else {
			QRSwing.windowBackgroundImagePath = value;
			GLOBAL_PROP.setProperty(WINDOW_IMAGE_PATH, value);
		}
	}

	public static void setWindowRound(boolean value) {
		QRSwing.windowRound = value;
		GLOBAL_PROP.setProperty(WINDOW_ROUND, String.valueOf(value));
	}

	public static void setWindowAlwaysOnTop(boolean value) {
		QRSwing.windowAlwaysOnTop = value;
		GLOBAL_PROP.setProperty(WINDOW_ALWAYS_TOP, String.valueOf(value));
	}

	public static void setWindowScreenAdsorb(boolean value) {
		QRSwing.windowScreenAdsorb = value;
		GLOBAL_PROP.setProperty(WINDOW_ABSORB, String.valueOf(value));
	}

	public static void setWindowTransparency(float value) {
		QRSwing.windowTransparency = value;
		GLOBAL_PROP.setProperty(WINDOW_TRANSPARENCY, String.valueOf(value));
	}

	public static void setWindowAlpha(float value) {
		QRSwing.windowAlpha = value;
		GLOBAL_PROP.setProperty(WINDOW_BACKGROUND_IMAGE_ALPHA, String.valueOf(value));
	}

	public static void setWindowScale(boolean value) {
		QRSwing.windowScale = value;
		GLOBAL_PROP.setProperty(WINDOW_BACKGROUND_IMAGE_SCALE, String.valueOf(value));
	}

	private static QRGlobalKeyboardHookListener keyboardHook;

	/**
	 * 用于注册全局键盘事件
	 *
	 * @param mainWindow 主窗体
	 */
	public static void registerGlobalKeyEvents(Window mainWindow) {
		if (keyboardHook == null) {
			keyboardHook = new QRGlobalKeyboardHookListener() {
				@Override
				protected void keyPress(KeyStroke keyStroke) {
					ArrayList<QRActionRegister> list;
					if (mainWindow != null && mainWindow.isFocused()) {
						list = ALL_KEY_EVENTS.get(keyStroke);
					} else {
						list = GLOBAL_KEY_EVENTS.get(keyStroke);
					}
					if (list != null) {
						ArrayList<QRActionRegister> temp = new ArrayList<>(list);
						temp.forEach(ef -> ef.action(keyStroke));
					}
				}
			};
		}
	}

	/**
	 * 当已另注册了一个全局的键盘监听器时，可以直接设置，而不用新实例化
	 *
	 * @param listener   已设置的监听器
	 * @param mainWindow 主窗体
	 */
	public static void setGlobalKeyEventsListener(QRGlobalKeyboardHookListener listener, Window mainWindow) {
		if (keyboardHook == null && listener != null) {
			keyboardHook = listener;
			listener.addKeyListenerAction(true, e -> {
				KeyStroke keyStroke = (KeyStroke) e;
				ArrayList<QRActionRegister> list;
				if (mainWindow != null && mainWindow.isFocused()) {
					list = ALL_KEY_EVENTS.get(keyStroke);
				} else {
					list = GLOBAL_KEY_EVENTS.get(keyStroke);
				}
				if (list != null) {
					ArrayList<QRActionRegister> temp = new ArrayList<>(list);
					temp.forEach(ef -> ef.action(keyStroke));
				}
			});
		}
	}

	/**
	 * 添加键盘按键事件，提供多个快捷键对应一个Action的功能
	 * <p> 方法 {@link QRSwing#registerGlobalKeyEvents(Window)} 被调用了才生效
	 *
	 * @param key             按键组合，不同按键组合间以英文逗号{@code ,}分割
	 *                        <p>有+号则优先以+号分割，再以空格分割
	 *                        <p>支持格式 {@code Ctrl + Alt + Shift + s}、{@code a}、{@code shift a}、{@code shift b,ctrl a}、{@code shift b, ctrl b}，但不支持 Windows 键的组合
	 * @param ar              事件，其参数 {@link Object} 为 {@link KeyStroke} 类
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发。若为 {@code false}，则事件全乎全局，则不论主窗体是否处于焦点状态，都将触发事件
	 */
	public static void registerGlobalAction(String key, QRActionRegister ar, boolean mainWindowFocus) {
		if (key.indexOf(',') == -1) {
			KeyStroke keyStroke = QRStringUtils.getKeyStroke(key);
			registerGlobalAction(keyStroke, ar, mainWindowFocus);
		} else {
			String[] keys = key.split(",");
			for (String k : keys) {
				KeyStroke keyStroke = QRStringUtils.getKeyStroke(k);
				registerGlobalAction(keyStroke, ar, mainWindowFocus);
			}
		}
	}

	/**
	 * 添加键盘按键事件
	 * <p> 方法 {@link QRSwing#registerGlobalKeyEvents(Window)} 被调用了才生效
	 *
	 * @param keycode         键值
	 * @param ar              事件，其参数 {@link Object} 为 {@link KeyStroke} 类
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发。若为 {@code false}，则事件全乎全局，则不论主窗体是否处于焦点状态，都将触发事件
	 */
	public static void registerGlobalAction(int keycode, QRActionRegister ar, boolean mainWindowFocus) {
		KeyStroke keyStroke = QRStringUtils.getKeyStroke(keycode);
		registerGlobalAction(keyStroke, ar, mainWindowFocus);
	}

	/**
	 * 添加键盘按键事件
	 * <p> 方法 {@link QRSwing#registerGlobalKeyEvents(Window)} 被调用了才生效
	 *
	 * @param keycode         键值
	 * @param modifiers       特殊键
	 * @param ar              事件，其参数 {@link Object} 为 {@link KeyStroke} 类
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发。若为 {@code false}，则事件全乎全局，则不论主窗体是否处于焦点状态，都将触发事件
	 */
	public static void registerGlobalAction(int keycode, int modifiers, QRActionRegister ar, boolean mainWindowFocus) {
		KeyStroke keyStroke = QRStringUtils.getKeyStroke(keycode, modifiers);
		registerGlobalAction(keyStroke, ar, mainWindowFocus);
	}

	/**
	 * 添加键盘按键事件
	 * <p> 方法 {@link QRSwing#registerGlobalKeyEvents(Window)} 被调用了才生效
	 *
	 * @param keyStroke       按键组合
	 * @param ar              事件，其参数 {@link Object} 为 {@link KeyStroke} 类
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发。若为 {@code false}，则事件全乎全局，则不论主窗体是否处于焦点状态，都将触发事件
	 */
	public static void registerGlobalAction(KeyStroke keyStroke, QRActionRegister ar, boolean mainWindowFocus) {
		if (keyStroke != null) {
			ALL_KEY_EVENTS.computeIfAbsent(keyStroke, k -> new ArrayList<>()).add(ar);
			if (!mainWindowFocus) {
				GLOBAL_KEY_EVENTS.computeIfAbsent(keyStroke, k -> new ArrayList<>()).add(ar);
			}
		}
	}

	/**
	 * 清空某一快捷键所对应的全部事件
	 *
	 * @param keyStroke       按键组合
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void registerGlobalActionRemove(KeyStroke keyStroke, boolean mainWindowFocus) {
		ALL_KEY_EVENTS.remove(keyStroke);
		if (!mainWindowFocus) {
			GLOBAL_KEY_EVENTS.remove(keyStroke);
		}
	}

	/**
	 * 移除指定的全局事件
	 *
	 * @param keyStroke       按键组合
	 * @param ar              事件
	 * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
	 */
	public static void registerGlobalActionRemove(KeyStroke keyStroke, QRActionRegister ar, boolean mainWindowFocus) {
		{
			ArrayList<QRActionRegister> list = ALL_KEY_EVENTS.get(keyStroke);
			if (list != null) {
				list.remove(ar);
			}
		}
		if (!mainWindowFocus) {
			ArrayList<QRActionRegister> list = GLOBAL_KEY_EVENTS.get(keyStroke);
			if (list != null) {
				list.remove(ar);
			}
		}
	}

	/**
	 * 软件关闭后的操作
	 *
	 * @param ar 其参数 {@link Object} 为 {@code null}
	 */
	public static void registerSystemExitAction(QRActionRegister ar) {
		actionAfterClose.add(ar);
	}
}
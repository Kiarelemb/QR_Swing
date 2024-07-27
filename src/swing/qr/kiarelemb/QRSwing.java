package swing.qr.kiarelemb;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import method.qr.kiarelemb.utils.*;
import swing.qr.kiarelemb.event.QRNativeKeyEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRNativeKeyListener;
import swing.qr.kiarelemb.listener.key.QRNativeKeyPressedListener;
import swing.qr.kiarelemb.resource.QRSwingInfo;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.utils.QRComponentUtils;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

import static java.io.File.separator;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 总设计类
 * @create 2022-11-04 15:05
 **/
public final class QRSwing implements Serializable {

    public static final String WINDOW_IMAGE_ENABLE = "window.image.enable";
    public static final String WINDOW_THEME = "window.theme";
    public static final String WINDOW_IMAGE_PATH = "window.image.path";
    public static final String WINDOW_ROUND = "window.round";
    public static final String WINDOW_TITLE_MENU = "window.title.menu";
    public static final String WINDOW_TRANSPARENCY = "window.transparency";
    public static final String WINDOW_ALPHA = "window.alpha";
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
     * 全局配置文件
     */
    public static final Properties GLOBAL_PROP = new Properties();
    /**
     * 窗体关闭后执行的动作
     */
    public static final ArrayList<QRActionRegister<Object>> ACTION_AFTER_CLOSE = new ArrayList<>();
    /**
     * {@link swing.qr.kiarelemb.window.basic.QRFrame} 的资源文件
     */
    public static String WINDOW_PROP_PATH = "window.properties";
    /**
     * 单例模式
     */
    @Deprecated
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
    public static boolean windowImageSet;
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
     * 主窗体设置背景图时是否让图片自适应
     */
    public static boolean windowScale = true;
    public static String windowBackgroundImagePath;
    /**
     * 主窗体设置背景图时的透明度
     */
    public static float windowBackgroundImageAlpha;
    /**
     * 字体名可能为 {@code null}，默认的全局字体为 微软雅黑
     */
    public static Font globalFont;
    public static String GLOBAL_PROP_PATH;
    /**
     * 获取该监听器请调用 {@link #getGlobalKeyListener()}
     */
    private static QRNativeKeyListener globalKeyListener;

    private QRSwing(String propPath) {
        System.out.println("""
                
                 .88888.    888888ba  .d88888b             oo
                d8'   `8b   88    `8b 88.    "'
                88     88  a88aaaa8P' `Y88888b. dP  dP  dP dP 88d888b. .d8888b.
                88  db 88   88   `8b.       `8b 88  88  88 88 88'  `88 88'  `88
                Y8.  Y88P   88     88 d8'   .8P 88.88b.88' 88 88    88 88.  .88
                 `8888PY8b  dP     dP  Y88888P  8888P Y8P  dP dP    dP `8888P88
                oooooooooooooooooooooooooooooooooooooooooooooooooooooooo~~~~.88~
                                                                        d8888P
                """);
        GLOBAL_PROP_PATH = propPath;
        //动画优化
        Toolkit.getDefaultToolkit().sync();
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
            QRComponentUtils.runActions(ACTION_AFTER_CLOSE, null);
            globalPropSave();
        }));
    }

    /**
     * 本工具包开始于此方法。在所有窗体或控件调用前，都必须先调用该方法
     *
     * <p>在调用本方法时，将使用默认的设置进行配置，其文件 {@link QRSwing#GLOBAL_PROP_PATH} 和 {@link QRSwing#WINDOW_PROP_PATH} 将会创建在程序的根目录下</p>
     */
    public static void start() {
        INSTANCE = new QRSwing("settings.properties");
        //加载主题
        QRColorsAndFonts.loadTheme();
    }

    /**
     * 本工具包开始于此方法。在所有窗体或控件调用前，都必须先调用该方法
     *
     * <p>窗体位置的资源文件 {@link QRSwing#WINDOW_PROP_PATH} 将会创建在程序的根目录</p>
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

    public static ImageIcon iconLoadLead(String key, String defaultFileName) {
        var iconPath = GLOBAL_PROP.getProperty(key);
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
        if (!QRFileUtils.fileCreate(WINDOW_PROP_PATH)) {
            WINDOW_PROP_PATH = "window.properties";
        }
        var prop = new Properties();
        final var size = QRSystemUtils.getScreenSize();
        final var width = size[0];
        final var height = size[1];
        prop.setProperty("Window.size.width", String.valueOf(width / 2));
        prop.setProperty("Window.size.height", String.valueOf(height / 2));
        prop.setProperty("Window.start.X", String.valueOf(width / 4));
        prop.setProperty("Window.start.Y", String.valueOf(height / 4));
        QRPropertiesUtils.storeProp(prop, WINDOW_PROP_PATH);
    }

    public static void globalPropBackToDefault() {
        var prop = getDefaultSettingsProp();
        GLOBAL_PROP.putAll(prop);
        globalPropSave();
    }

    /**
     * 获取全局默认配置的资源文件，该文件的内容为：
     * <pre><code>
     * window.absorb=true
     * window.always.top=false
     * window.background.image.alpha=0.8
     * window.background.image.scale=true
     * window.image.enable=true
     * window.image.path=
     * window.round=true
     * window.theme=深色
     * window.title.menu=false
     * window.transparency=0.8
     * </code></pre>
     */
    public static Properties getDefaultSettingsProp() {
        var url = QRSwingInfo.loadUrl("default_settings.properties");
        return QRPropertiesUtils.loadProp(url);
    }

    /**
     * 修改全局默认的字体
     *
     * @param fontFamily 字体名称
     */
    public static void customFontName(String fontFamily) {
        var font = QRFontUtils.getFont(fontFamily, 10);
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
    public static void setGlobalSetting(String key, Object value) {
        if (value == null) {
            removeGlobalSetting(key);
        } else {
            GLOBAL_PROP.setProperty(key, value.toString());
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
            return;
        }
        QRSwing.windowBackgroundImagePath = value;
        GLOBAL_PROP.setProperty(WINDOW_IMAGE_PATH, value);

    }

    public static void setWindowBackgroundImageAlpha(float value) {
        QRSwing.windowBackgroundImageAlpha = value;
        GLOBAL_PROP.setProperty(WINDOW_BACKGROUND_IMAGE_ALPHA, String.valueOf(value));
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

    public static void setWindowTitleMenu(boolean value) {
        QRSwing.windowTitleMenu = value;
        GLOBAL_PROP.setProperty(WINDOW_TITLE_MENU, String.valueOf(value));
    }

    public static void setWindowScale(boolean value) {
        QRSwing.windowScale = value;
        GLOBAL_PROP.setProperty(WINDOW_BACKGROUND_IMAGE_SCALE, String.valueOf(value));
    }

    /**
     * 用于注册全局键盘事件，监听器是 {@link QRNativeKeyPressedListener}
     * <p>若主窗体在实例化过程中设置了快捷键，或注册了按键事件，则请在主窗体实例化之前，调用该方法。</p>
     * <p>再在主窗体实例化完之后，调用 {@link #registerGlobalEventWindow(Window)}</p>
     * 例如：
     * <pre><code>
     *     QRSwing.registerGlobalKeyEvents();
     *     MainWindow window = new MainWindow();
     *     QRSwing.registerGlobalEventWindow(window);
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
     *     QRSwing.registerGlobalKeyEvents();
     *     MainWindow window = new MainWindow();
     *     QRSwing.registerGlobalEventWindow(window);
     * </code></pre>
     *
     * @param window 主窗体
     * @see #registerGlobalKeyEvents()
     */
    public static void registerGlobalEventWindow(Window window) {
        globalKeyListener.registerMainWindow(window);
    }

    /**
     * 当已另注册了一个全局的键盘监听器时，可以直接设置，而不用新实例化
     *
     * @param globalKeyListener 已设置的监听器
     */
    public static void setGlobalKeyEventsListener(QRNativeKeyListener globalKeyListener) {
        if (QRSwing.globalKeyListener == null && globalKeyListener != null) {
            QRSwing.globalKeyListener = globalKeyListener;
            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static QRNativeKeyListener getGlobalKeyListener() {
        if (globalKeyListener == null) {
            throw new NullPointerException("全局键盘监听器为空，请先调用 QRSwing.registerGlobalKeyEvents() 或 setGlobalKeyEventsListener(QRNativeKeyListener) 方法");
        }
        return globalKeyListener;
    }

    /**
     * 添加键盘按键事件，提供多个快捷键对应一个Action的功能
     * <p> 方法 {@link QRSwing#registerGlobalKeyEvents()} 被调用了才生效
     *
     * @param key             按键组合，不同按键组合间以英文逗号{@code ,}分割
     *                        <p>有+号则优先以+号分割，再以空格分割
     *                        <p>支持格式 {@code Ctrl + Alt + Shift + s}、{@code a}、{@code shift a}、{@code shift b,ctrl a}、
     *                        {@code shift b, ctrl b}，但不支持 Windows 键的组合
     * @param ar              事件，其参数是 {@link KeyStroke}
     * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发。若为 {@code false}，则事件全乎全局，则不论主窗体是否处于焦点状态，都将触发事件
     */
    public static void registerGlobalAction(String key, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
        String[] keys = key.split(",");
        for (String k : keys) {
            var keyStroke = QRStringUtils.getKeyStroke(k);
            registerGlobalAction(keyStroke, ar, mainWindowFocus);
        }
    }

    /**
     * 添加键盘按键事件
     * <p> 方法 {@link QRSwing#registerGlobalKeyEvents()} 被调用了才生效
     *
     * @param keycode         键值
     * @param ar              事件，其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
     * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发。若为 {@code false}，则事件全乎全局，则不论主窗体是否处于焦点状态，都将触发事件
     */
    public static void registerGlobalAction(int keycode, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
        var keyStroke = QRStringUtils.getKeyStroke(keycode);
        registerGlobalAction(keyStroke, ar, mainWindowFocus);
    }

    /**
     * 添加键盘按键事件
     * <p> 方法 {@link QRSwing#registerGlobalKeyEvents()} 被调用了才生效
     *
     * @param keycode         键值
     * @param modifiers       特殊键
     * @param ar              事件，其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
     * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发。若为 {@code false}，则事件全乎全局，则不论主窗体是否处于焦点状态，都将触发事件
     */
    public static void registerGlobalAction(int keycode, int modifiers, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
        var keyStroke = QRStringUtils.getKeyStroke(keycode, modifiers);
        registerGlobalAction(keyStroke, ar, mainWindowFocus);
    }

    /**
     * 添加键盘按键事件
     * <p> 方法 {@link QRSwing#registerGlobalKeyEvents()} 被调用了才生效
     *
     * @param keyStroke       按键组合
     * @param ar              事件，其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
     * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发。若为 {@code false}，则事件全乎全局，则不论主窗体是否处于焦点状态，都将触发事件
     */
    public static void registerGlobalAction(KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
        if (QRSwing.globalKeyListener != null) {
            QRSwing.globalKeyListener.addEvent(QRNativeKeyListener.TYPE.PRESSED, mainWindowFocus, keyStroke, ar);
        }
    }

    /**
     * 清空某一快捷键所对应的全部事件
     *
     * @param keyStroke       按键组合
     * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
     */
    public static void registerGlobalActionRemove(KeyStroke keyStroke, boolean mainWindowFocus) {
        if (QRSwing.globalKeyListener != null) {
            QRSwing.globalKeyListener.removeEvent(QRNativeKeyListener.TYPE.PRESSED, keyStroke, mainWindowFocus);
        }
    }

    /**
     * 移除指定的全局事件
     *
     * @param keyStroke       按键组合
     * @param ar              事件
     * @param mainWindowFocus 事件是否是在主窗体处于焦点时才触发
     */
    public static void registerGlobalActionRemove(KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
        if (QRSwing.globalKeyListener != null) {
            QRSwing.globalKeyListener.removeEvent(QRNativeKeyListener.TYPE.PRESSED, keyStroke, ar, mainWindowFocus);
        }
    }

    /**
     * 在外部运行快捷键事件
     *
     * @param keyStroke       快捷键
     * @param mainWindowFocus 主窗体是否在焦点
     */
    public static void invokeAction(Window window, KeyStroke keyStroke, boolean mainWindowFocus) {
        if (QRSwing.globalKeyListener != null) {
            QRSwing.globalKeyListener.invokeAction(window, QRNativeKeyListener.TYPE.PRESSED, keyStroke, mainWindowFocus);
        }
    }

    /**
     * 软件关闭后的操作
     *
     * @param ar 其参数 {@link Object} 为 {@code null}
     */
    public static void registerSystemExitAction(QRActionRegister<Object> ar) {
        ACTION_AFTER_CLOSE.add(ar);
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
        windowBackgroundImageAlpha = QRPropertiesUtils.getPropInFloat(GLOBAL_PROP, WINDOW_BACKGROUND_IMAGE_ALPHA, 0.8f, false);
        windowRound = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_ROUND, true, true);
        windowImageEnable = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_IMAGE_ENABLE, true, true);
        windowTitleMenu = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_TITLE_MENU, true, true);
        windowTransparency = QRPropertiesUtils.getPropInFloat(GLOBAL_PROP, WINDOW_TRANSPARENCY, 0.999f, true);
        windowScale = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_BACKGROUND_IMAGE_SCALE, true, true);
        windowAlwaysOnTop = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_ALWAYS_TOP, false, true);
        windowScreenAdsorb = QRPropertiesUtils.getPropInBoolean(GLOBAL_PROP, WINDOW_ABSORB, true, true);
        if (!QRFileUtils.fileExists(WINDOW_PROP_PATH)) {
            windowPropBackToDefault();
        }
        if (windowBackgroundImageAlpha < 0 || windowBackgroundImageAlpha > 1) {
            setWindowBackgroundImageAlpha(0.8f);
            return;
        }
        windowImageSet = windowImageEnable && QRFileUtils.fileExists(windowBackgroundImagePath);
    }
}
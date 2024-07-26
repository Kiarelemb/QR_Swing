package swing.qr.kiarelemb.listener;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import swing.qr.kiarelemb.event.QRNativeKeyEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRNativeKeyListener
 * @description TODO
 * @create 2024/7/20 下午4:03
 */
public class QRNativeKeyListener implements NativeKeyListener {
    protected final KeyEvents typeKeyEvents;
    protected final KeyEvents pressKeyEvents;
    protected final KeyEvents releaseKeyEvents;

    public QRNativeKeyListener() {
        this.pressKeyEvents = new KeyEvents();
        this.typeKeyEvents = new KeyEvents();
        this.releaseKeyEvents = new KeyEvents();
    }

    public void registerMainWindow(Window window) {
        this.pressKeyEvents.mainWindow = window;
        this.typeKeyEvents.mainWindow = window;
        this.releaseKeyEvents.mainWindow = window;
    }

    public enum TYPE {
        PRESSED, TYPED, RELEASED
    }

    /**
     * @param type 欲添加的类型
     * @param ar   操作。其参数是 {@link QRNativeKeyEvent}，从外部运行时，其参数是 {@link KeyStroke}
     */
    public void addEvent(QRNativeKeyListener.TYPE type, boolean mainWindowFocus, KeyStroke keyStroke, QRActionRegister<KeyStroke> ar) {
        switch (type) {
            case PRESSED -> pressKeyEvents.addEvent(keyStroke, mainWindowFocus, ar);
            case TYPED -> typeKeyEvents.addEvent(keyStroke, mainWindowFocus, ar);
            case RELEASED -> releaseKeyEvents.addEvent(keyStroke, mainWindowFocus, ar);
        }
    }

    public void removeEvent(QRNativeKeyListener.TYPE type, KeyStroke keyStroke, boolean mainWindowFocus) {
        switch (type) {
            case PRESSED -> pressKeyEvents.removeEvent(keyStroke, mainWindowFocus);
            case TYPED -> typeKeyEvents.removeEvent(keyStroke, mainWindowFocus);
            case RELEASED -> releaseKeyEvents.removeEvent(keyStroke, mainWindowFocus);
        }
    }

    public void removeEvent(QRNativeKeyListener.TYPE type, KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
        switch (type) {
            case PRESSED -> pressKeyEvents.removeEvent(keyStroke, ar, mainWindowFocus);
            case TYPED -> typeKeyEvents.removeEvent(keyStroke, ar, mainWindowFocus);
            case RELEASED -> releaseKeyEvents.removeEvent(keyStroke, ar, mainWindowFocus);
        }
    }

    public void add(boolean mainWindowFocus, QRActionRegister<QRNativeKeyEvent> ar) {
        this.pressKeyEvents.add(mainWindowFocus, ar);
        this.typeKeyEvents.add(mainWindowFocus, ar);
        this.releaseKeyEvents.add(mainWindowFocus, ar);
    }

    public void remove(boolean mainWindowFocus, QRActionRegister<QRNativeKeyEvent> ar) {
        this.pressKeyEvents.remove(mainWindowFocus, ar);
        this.typeKeyEvents.remove(mainWindowFocus, ar);
        this.releaseKeyEvents.remove(mainWindowFocus, ar);
    }

    /**
     * 从外部运行指定键的事件，注意，操作的参数是 {@link KeyStroke}
     *
     * @param type            类型
     * @param keyStroke       快捷键
     * @param mainWindowFocus 是否主窗体焦点
     */
    public void invokeAction(Window window, QRNativeKeyListener.TYPE type, KeyStroke keyStroke, boolean mainWindowFocus) {
        switch (type) {
            case PRESSED -> pressKeyEvents.invokeAction(window, keyStroke, mainWindowFocus);
            case TYPED -> typeKeyEvents.invokeAction(window, keyStroke, mainWindowFocus);
            case RELEASED -> releaseKeyEvents.invokeAction(window, keyStroke, mainWindowFocus);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
        QRNativeKeyEvent e = new QRNativeKeyEvent(TYPE.TYPED, nativeEvent);
        typeKeyEvents.invokeAction(e);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        QRNativeKeyEvent e = new QRNativeKeyEvent(TYPE.PRESSED, nativeEvent);
        pressKeyEvents.invokeAction(e);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        QRNativeKeyEvent e = new QRNativeKeyEvent(TYPE.RELEASED, nativeEvent);
        releaseKeyEvents.invokeAction(e);
    }

    public static class KeyEvents {
        private final Map<KeyStroke, ArrayList<QRActionRegister<KeyStroke>>> GLOBAL_KEY_EVENTS;
        private final Map<KeyStroke, ArrayList<QRActionRegister<KeyStroke>>> FOCUS_KEY_EVENTS;
        private final LinkedList<QRActionRegister<QRNativeKeyEvent>> globalEventList;
        private final LinkedList<QRActionRegister<QRNativeKeyEvent>> focusEventList;
        private Window mainWindow;

        public KeyEvents() {
            FOCUS_KEY_EVENTS = new HashMap<>();
            GLOBAL_KEY_EVENTS = new HashMap<>();
            globalEventList = new LinkedList<>();
            focusEventList = new LinkedList<>();
        }

        public void addEvent(KeyStroke keyStroke, boolean mainWindowFocus, QRActionRegister<KeyStroke> ar) {
            if (keyStroke != null) {
                if (mainWindowFocus) {
                    this.FOCUS_KEY_EVENTS.computeIfAbsent(keyStroke, k -> new ArrayList<>()).add(ar);
                } else {
                    this.GLOBAL_KEY_EVENTS.computeIfAbsent(keyStroke, k -> new ArrayList<>()).add(ar);
                }
            }
        }

        public void removeEvent(KeyStroke keyStroke, boolean mainWindowFocus) {
            removeEvent(keyStroke, null, mainWindowFocus);
        }

        public void removeEvent(KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, boolean mainWindowFocus) {
            if (ar == null) {
                if (mainWindowFocus) {
                    this.FOCUS_KEY_EVENTS.remove(keyStroke);
                } else {
                    this.GLOBAL_KEY_EVENTS.remove(keyStroke);
                }
                return;
            }
            ArrayList<QRActionRegister<KeyStroke>> list;
            if (mainWindowFocus) {
                list = this.FOCUS_KEY_EVENTS.get(keyStroke);
            } else {
                list = this.GLOBAL_KEY_EVENTS.get(keyStroke);
            }
            if (list != null) {
                list.remove(ar);
            }
        }

        public void add(boolean mainWindowFocus, QRActionRegister<QRNativeKeyEvent> ar) {
            if (mainWindowFocus) {
                focusEventList.add(ar);
            } else {
                globalEventList.add(ar);
            }
        }

        public void remove(boolean mainWindowFocus, QRActionRegister<QRNativeKeyEvent> ar) {
            if (mainWindowFocus) {
                focusEventList.remove(ar);
            } else {
                globalEventList.remove(ar);
            }
        }

        /**
         * 指定键的运行优先于泛事件的运行
         */
        public void invokeAction(QRNativeKeyEvent event) {
            KeyStroke keyStroke = event.getKeyStroke();
//            System.out.println(keyStroke + "," + event.paramString());
            ArrayList<QRActionRegister<KeyStroke>> ars;
            LinkedList<QRActionRegister<QRNativeKeyEvent>> list;
            if (mainWindow != null && mainWindow.isFocused()) {
                ars = this.FOCUS_KEY_EVENTS.get(keyStroke);
                list = this.focusEventList;
                QRComponentUtils.runActions(ars, keyStroke);
                QRComponentUtils.runActions(list, event);
            }
            ars = this.GLOBAL_KEY_EVENTS.get(keyStroke);
            list = this.globalEventList;
            QRComponentUtils.runActions(ars, keyStroke);
            QRComponentUtils.runActions(list, event);
        }

        /**
         * 从外部运行指定键的事件
         *
         * @param keyStroke       快捷键
         * @param mainWindowFocus 是否主窗体焦点
         */
        public void invokeAction(Window mainWindow, KeyStroke keyStroke, boolean mainWindowFocus) {
            ArrayList<QRActionRegister<KeyStroke>> list;
            if (mainWindowFocus) {
                if (mainWindow == null || !mainWindow.isFocused()) {
                    return;
                }
                list = FOCUS_KEY_EVENTS.get(keyStroke);
                QRComponentUtils.runActions(list, keyStroke);
            }
            list = GLOBAL_KEY_EVENTS.get(keyStroke);
            QRComponentUtils.runActions(list, keyStroke);
        }
    }
}
package swing.qr.kiarelemb.assembly;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.unix.X11;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Locale;

/**
 * 在 GNOME Wayland 的 XWayland 后端中请求 Mutter 接管窗口移动。
 */
final class QRGnomeWindowMove {
    private static final String MOVE_RESIZE = "_NET_WM_MOVERESIZE";
    private static final int MOVE = 8;
    private static final int SOURCE_APPLICATION = 1;

    private QRGnomeWindowMove() {
    }

    /**
     * 尝试开始一次原生窗口移动。
     *
     * @return 已向窗口管理器成功发送请求时返回 {@code true}
     */
    static boolean begin(Window window, MouseEvent event) {
        if (!isTargetEnvironment() || event.getButton() != MouseEvent.BUTTON1
                || window == null || !window.isDisplayable()) {
            return false;
        }

        X11.Display display = null;
        try {
            long windowId = Native.getWindowID(window);
            if (windowId == 0) {
                return false;
            }

            X11 x11 = X11.INSTANCE;
            display = x11.XOpenDisplay(null);
            if (display == null) {
                return false;
            }

            X11.Window root = x11.XDefaultRootWindow(display);
            X11.Atom messageType = x11.XInternAtom(display, MOVE_RESIZE, false);

            X11.XClientMessageEvent clientMessage = new X11.XClientMessageEvent();
            clientMessage.type = X11.ClientMessage;
            clientMessage.serial = new NativeLong(0);
            clientMessage.send_event = 1;
            clientMessage.display = display;
            clientMessage.window = new X11.Window(windowId);
            clientMessage.message_type = messageType;
            clientMessage.format = 32;
            clientMessage.data.setType(NativeLong[].class);
            clientMessage.data.l[0] = new NativeLong(event.getXOnScreen());
            clientMessage.data.l[1] = new NativeLong(event.getYOnScreen());
            clientMessage.data.l[2] = new NativeLong(MOVE);
            clientMessage.data.l[3] = new NativeLong(MouseEvent.BUTTON1);
            clientMessage.data.l[4] = new NativeLong(SOURCE_APPLICATION);
            clientMessage.write();

            X11.XEvent xEvent = new X11.XEvent();
            xEvent.setType(X11.XClientMessageEvent.class);
            xEvent.xclient = clientMessage;
            xEvent.write();

            NativeLong eventMask = new NativeLong(
                    X11.SubstructureRedirectMask | X11.SubstructureNotifyMask);
            int sent = x11.XSendEvent(display, root, 0, eventMask, xEvent);
            x11.XFlush(display);
            return sent != 0;
        } catch (UnsatisfiedLinkError | RuntimeException ex) {
            // 原生能力不可用时由调用方继续使用 Swing 的 setLocation() 拖动。
            return false;
        } finally {
            if (display != null) {
                X11.INSTANCE.XCloseDisplay(display);
            }
        }
    }

    private static boolean isTargetEnvironment() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String session = System.getenv("XDG_SESSION_TYPE");
        String desktop = System.getenv("XDG_CURRENT_DESKTOP");
        String display = System.getenv("DISPLAY");
        return os.contains("linux")
                && "wayland".equalsIgnoreCase(session)
                && desktop != null && desktop.toLowerCase(Locale.ROOT).contains("gnome")
                && display != null && !display.isBlank();
    }
}

package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class QRWindowMouseAdapter extends MouseAdapter {
    protected int pressPointX = 0;
    protected int pressPointY = 0;
    protected int height = 0;
    protected int width = 0;
    protected Point p = null;
    private ResizeDirection resizeDirection = ResizeDirection.NONE;

    @Override
    public void mousePressed(MouseEvent e) {
        this.p = getWindowPoint(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        clear();
        window().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        windowReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (beforeDragged(e)) {
            return;
        }

        Point windowPoint = getWindowPoint(e);
        if (resizeDirection != ResizeDirection.NONE) {
            resizeWindow(e, windowPoint.x, windowPoint.y);
            return;
        }

        if (windowPoint.y < moveAreaHeight()) {
            window().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            if (this.p == null) {
                return;
            }
            int x = e.getXOnScreen() - this.p.x;
            int y = e.getYOnScreen() - this.p.y;
            window().setLocation(x, y);
            windowMoved(x, y);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!resizable()) {
            return;
        }

        Point windowPoint = getWindowPoint(e);
        int eX = windowPoint.x;
        int eY = windowPoint.y;
        int rights = Math.abs(eX - window().getWidth());
        int downs = Math.abs(eY - window().getHeight());
        getXYWH(e);

        if (eY <= QRFrame.DIS && eX <= QRFrame.DIS) {
            setResizeDirection(ResizeDirection.UP_LEFT);
        } else if (eY <= QRFrame.DIS && rights <= QRFrame.DIS) {
            setResizeDirection(ResizeDirection.UP_RIGHT);
        } else if (downs <= QRFrame.DIS && eX <= QRFrame.DIS) {
            setResizeDirection(ResizeDirection.DOWN_LEFT);
        } else if (downs <= QRFrame.DIS && rights <= QRFrame.DIS) {
            setResizeDirection(ResizeDirection.DOWN_RIGHT);
        } else if (eY <= QRFrame.DIS) {
            setResizeDirection(ResizeDirection.UP);
        } else if (eX <= QRFrame.DIS) {
            setResizeDirection(ResizeDirection.LEFT);
        } else if (rights <= QRFrame.DIS) {
            setResizeDirection(ResizeDirection.RIGHT);
        } else if (downs <= QRFrame.DIS) {
            setResizeDirection(ResizeDirection.DOWN);
        } else {
            setCursorDefault();
            QRPanel panel = mainPanel();
            if (panel != null) {
                panel.setCursorDefault();
            }
            clear();
        }
    }

    protected void resizeWindow(MouseEvent e, int eX, int eY) {
        int eXOnScreen = e.getXOnScreen();
        int eYOnScreen = e.getYOnScreen();
        switch (resizeDirection) {
            case UP_LEFT:
                window().setBounds(eXOnScreen, eYOnScreen, this.width + this.pressPointX - eXOnScreen,
                        this.height + this.pressPointY - eYOnScreen);
                window().setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                break;
            case UP_RIGHT:
                int height = this.height + this.pressPointY - eYOnScreen;
                window().setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                window().setBounds(window().getX(), eYOnScreen, eX, height);
                break;
            case DOWN_LEFT:
                int width = this.width + this.pressPointX - eXOnScreen;
                window().setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                window().setBounds(eXOnScreen, window().getY(), width, eY);
                break;
            case DOWN_RIGHT:
                window().setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                window().setSize(eX, eY);
                break;
            case UP:
                height = this.height + this.pressPointY - eYOnScreen;
                window().setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                window().setBounds(window().getX(), eYOnScreen, this.width, height);
                break;
            case LEFT:
                width = this.width + this.pressPointX - eXOnScreen;
                window().setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                window().setBounds(eXOnScreen, window().getY(), width, this.height);
                break;
            case DOWN:
                window().setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                window().setSize(this.width, eY);
                break;
            case RIGHT:
                window().setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                window().setSize(eX, this.height);
                break;
            case NONE:
                break;
        }
    }

    protected ResizeDirection resizeDirection() {
        return resizeDirection;
    }

    protected boolean beforeDragged(MouseEvent e) {
        return false;
    }

    protected void windowMoved(int x, int y) {
    }

    protected void windowReleased(MouseEvent e) {
    }

    protected abstract Window window();

    protected abstract int moveAreaHeight();

    protected abstract boolean resizable();

    protected abstract void setCursorDefault();

    protected abstract QRPanel mainPanel();

    protected void clear() {
        resizeDirection = ResizeDirection.NONE;
        this.pressPointX = 0;
        this.pressPointY = 0;
        this.height = 0;
        this.width = 0;
    }

    protected void getXYWH(MouseEvent e) {
        this.pressPointX = e.getXOnScreen();
        this.pressPointY = e.getYOnScreen();
        this.width = window().getWidth();
        this.height = window().getHeight();
    }

    protected Point getWindowPoint(MouseEvent e) {
        return SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), window());
    }

    private void setResizeDirection(ResizeDirection resizeDirection) {
        this.resizeDirection = resizeDirection;
        window().setCursor(Cursor.getPredefinedCursor(resizeDirection.cursor));
    }

    protected enum ResizeDirection {
        UP_LEFT(Cursor.NW_RESIZE_CURSOR),
        UP_RIGHT(Cursor.NE_RESIZE_CURSOR),
        DOWN_LEFT(Cursor.SW_RESIZE_CURSOR),
        DOWN_RIGHT(Cursor.SE_RESIZE_CURSOR),
        UP(Cursor.N_RESIZE_CURSOR),
        LEFT(Cursor.W_RESIZE_CURSOR),
        DOWN(Cursor.S_RESIZE_CURSOR),
        RIGHT(Cursor.E_RESIZE_CURSOR),
        NONE(Cursor.DEFAULT_CURSOR);

        private final int cursor;

        ResizeDirection(int cursor) {
            this.cursor = cursor;
        }
    }
}
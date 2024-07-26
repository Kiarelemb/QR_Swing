package swing.qr.kiarelemb.window.basic;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.combination.QRBorderContentPanel;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRParentWindowMove;
import swing.qr.kiarelemb.inter.listener.add.QRWindowListenerAdd;
import swing.qr.kiarelemb.listener.QRMouseListener.TYPE;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.listener.QRWindowListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 一个空白对话框窗体，没有任何东西，仅可鼠标移动
 * @create 2022-11-04 17:21
 **/
public class QREmptyDialog extends JDialog implements QRParentWindowMove, QRComponentUpdate, QRWindowListenerAdd {
    protected final QRBorderContentPanel contentPane;
    protected final Color backgroundColor;
    private QRWindowListener windowListener;
    private final Point p = new Point();
    private boolean windowCanMove = true;

    /**
     * @param owner 父窗体
     */
    public QREmptyDialog(Window owner) {
        this(owner, true);
    }

    /**
     * @param owner        父窗体
     * @param parentUnable 父窗体是否禁用
     */
    public QREmptyDialog(Window owner, boolean parentUnable) {
        super(owner, null, parentUnable ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        setSize(200, 100);
        setResizable(false);
        if (QRSwing.windowIcon != null) {
            setIconImage(QRSwing.windowIcon.getImage());
        }
        setUndecorated(true);
        this.backgroundColor = QRColorsAndFonts.FRAME_COLOR_BACK;
        this.contentPane = new QRBorderContentPanel();
        initContentPane();
        setContentPane(this.contentPane);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        if (owner != null) {
            if (owner instanceof QRFrame baseFrame) {
                baseFrame.addChildWindow(this);
            }
        }
        if (QRSwing.windowRound) {
            QRSystemUtils.setWindowRound(this, QRSwing.windowTransparency);
        }
    }

    private void initContentPane() {
        this.contentPane.addMouseListener();
        this.contentPane.addMouseMotionListener();
        this.contentPane.addMouseAction(TYPE.PRESS, e -> {
            if (e.getButton() == MouseEvent.BUTTON1) {
                QREmptyDialog.this.p.x = e.getX();
                QREmptyDialog.this.p.y = e.getY();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        });
        this.contentPane.addMouseAction(TYPE.RELEASE, e -> this.contentPane.setCursorDefault());
        this.contentPane.addMouseMotionAction(QRMouseMotionListener.TYPE.DRAG, e -> {
            if (QREmptyDialog.this.windowCanMove) motion(e);
        });
    }

    @Override
    public final void addWindowListener() {
        if (this.windowListener == null) {
            this.windowListener = new QRWindowListener();
            this.windowListener.add(QRWindowListener.TYPE.OPEN, this::windowOpened);
            this.windowListener.add(QRWindowListener.TYPE.CLOSED, this::windowClosed);
            this.windowListener.add(QRWindowListener.TYPE.CLOSING, this::windowClosing);
            this.windowListener.add(QRWindowListener.TYPE.ACTIVATED, this::windowActivated);
            this.windowListener.add(QRWindowListener.TYPE.DEACTIVATED, this::windowDeactivated);
            this.windowListener.add(QRWindowListener.TYPE.ICONIFIED, this::windowIconified);
            this.windowListener.add(QRWindowListener.TYPE.DEICONIFIED, this::windowDeiconified);
            this.windowListener.addWindowMoveAction(this::windowMoved);
            addWindowListener(this.windowListener);
        }
    }

    @Override
    public final void addWindowAction(QRWindowListener.TYPE type, QRActionRegister<WindowEvent> ar) {
        if (this.windowListener != null) {
            this.windowListener.add(type, ar);
        }
    }

    @Override
    public final void addWindowMoveAction(QRActionRegister<Point> ar) {
        if (this.windowListener != null) {
            this.windowListener.addWindowMoveAction(ar);
        }
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        if (windowListener == null) {
            return;
        }
        Point point = new Point(x, y);
        this.windowListener.windowMoved(point);
    }

    public void setCursorWait() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void setCursorDefault() {
        setCursor(Cursor.getDefaultCursor());
    }

    private void motion(MouseEvent e) {
        Point point = getLocation();
        final int x = point.x + e.getX() - this.p.x;
        final int y = point.y + e.getY() - this.p.y;
        setLocation(x, y);
    }

    public void setFreelyMotionFailed() {
        this.windowCanMove = false;
    }

    /**
     * 父窗体移动时将自动更新此位置
     */
    public void updateLocation(Point parentWindowLocation) {
    }

    /**
     * 重写前请先调用 {@link #addWindowListener()}
     */
    public void windowOpened(WindowEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addWindowListener()}
     */
    public void windowClosing(WindowEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addWindowListener()}
     */
    public void windowClosed(WindowEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addWindowListener()}
     */
    public void windowIconified(WindowEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addWindowListener()}
     */
    public void windowDeiconified(WindowEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addWindowListener()}
     */
    public void windowActivated(WindowEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addWindowListener()}
     */
    public void windowDeactivated(WindowEvent e) {
    }

    /**
     * 重写前请先调用 {@link #addWindowListener()}
     */
    public void windowMoved(Point p) {
    }

    @Override
    public QRPanel getContentPane() {
        return this.contentPane;
    }

    /**
     * 父窗体移动时，本窗体也移动
     */
    @Override
    public final void ownerMoved(Point parentWindowLocation) {
        updateLocation(parentWindowLocation);
    }

    @Override
    public void componentFresh() {
        this.contentPane.componentFresh();
    }
}
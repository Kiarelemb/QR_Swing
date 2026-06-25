package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.data.QRInternalScrollBarData;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRInternalScrollbarUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRDocumentListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseWheelListenerAdd;
import swing.qr.kiarelemb.listener.QRDocumentListener.TYPE;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轻量级内置滚动面板，用于承载实现了 {@link QRInternalScrollbarUpdate} 的组件。
 *
 * <p>这个组件主要面向 {@link QRTextPane} 使用。它不会像 Swing 原生
 * {@link JScrollPane} 那样把滚动条放在组件外部，而是把滚动条绘制并处理在内容组件内部，
 * 适合需要“内置滚动条 / 悬浮滚动条”视觉效果的文本面板。
 *
 * <p>如果需要普通 Swing 滚动行为，使用 {@link QRTextPane#addScrollPane()}；
 * 如果需要内置滚动条样式，使用 {@link QRTextPane#addInternalScrollPane()}。
 *
 * <p>它不是 {@link JScrollPane} 的通用替代品。被承载的组件需要实现
 * {@link QRInternalScrollbarUpdate}，并支持本类依赖的鼠标、拖拽和滚轮监听接口。
 *
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRInternalScrollPane
 * @create 2024/7/17 19:19
 */
public class QRInternalScrollPane extends QRPanel {
    private JComponent component;
    private QRInternalScrollbarUpdate barData;
    private QRInternalScrollBarData data;
    private JEditorPane editor;
    private Highlighter highlighter;
    private final AtomicInteger editorCaretIndex = new AtomicInteger(0);
    private final Timer verticalScrollTimer;
    private int targetY;

    public QRInternalScrollPane() {
        super(false, null);
        setLayout(null);
        setBorder(null);
        setOpaque(false);
        this.verticalScrollTimer = new Timer(10, e -> animateVerticalScroll());
        this.verticalScrollTimer.setCoalesce(true);
    }

    public QRInternalScrollPane(QRInternalScrollbarUpdate content) {
        this();
        setViewportView(content);
    }

    public void setViewportView(QRInternalScrollbarUpdate content) {
        if (!(content instanceof JComponent com)) {
            throw new UnsupportedOperationException("The content must be a JComponent");
        }
        if (content instanceof QRMouseListenerAdd ma
                && content instanceof QRMouseMotionListenerAdd mma
                && content instanceof QRMouseWheelListenerAdd mwa) {
            this.component = com;
            this.barData = content;
            this.data = barData.getScrollBarData();
            com.setLocation(0, 0);
            com.setBorder(new QRInternalScrollBar(content));
            add(com);
            scrollBarAdd(ma, mma, mwa);
            return;
        }
        throw new UnsupportedOperationException("The content must be a QRMouseListenerAdd, QRMouseMotionListenerAdd, QRMouseWheelListenerAdd");
    }

    public QRInternalScrollbarUpdate getScrollBarDate() {
        return barData;
    }

    public JComponent getContentPane() {
        return component;
    }

    public void refreshScrollBar() {
        SwingUtilities.invokeLater(() -> {
            refreshScrollBarNow();
            SwingUtilities.invokeLater(this::refreshScrollBarNow);
        });
    }

    private void refreshScrollBarNow() {
        refreshEditorSize();
        clampContentLocation();
        if (barData != null) {
            barData.scrollBarValueUpdate();
        }
        if (component != null) {
            component.repaint();
        }
        repaint();
    }

    private void scrollBarAdd(QRMouseListenerAdd ma, QRMouseMotionListenerAdd mma, QRMouseWheelListenerAdd mwa) {
        ma.addMouseListener();
        mma.addMouseMotionListener();
        mwa.addMouseWheelListener();
        if (ma instanceof QRDocumentListenerAdd da && component instanceof JEditorPane editorPane) {
            this.editor = editorPane;
            this.highlighter = editorPane.getHighlighter();
            da.addDocumentListener();
            QRActionRegister<DocumentEvent> actionRegister = e -> {
                refreshScrollBar();
            };
            da.addDocumentListenerAction(TYPE.INSERT, actionRegister);
            da.addDocumentListenerAction(TYPE.REMOVE, actionRegister);
            da.addDocumentListenerAction(TYPE.CHANGED, actionRegister);
        }

        mwa.addMouseWheelAction(es -> {
            int amount;
            if (component instanceof QRTextPane com && com.caret != null) {
                amount = es.getScrollAmount() * com.caret.caretHeight();
            } else {
                amount = 50;
            }
            amount = Math.max(1, amount);
            double rotation = es.getPreciseWheelRotation() == 0 ? es.getWheelRotation() : es.getPreciseWheelRotation();
            int baseY = verticalScrollTimer.isRunning() ? this.targetY : component.getY();
            int distance = (int) Math.round(amount * rotation);
            if (distance == 0 && rotation != 0) {
                distance = rotation > 0 ? 1 : -1;
            }
            int targetY = clampY(baseY - distance);
            scrollToY(targetY);
        });

        mma.addMouseMotionAction(QRMouseMotionListener.TYPE.MOVE, es -> {
            barData.scrollBarValueUpdate();
            Point p = es.getPoint();
            data.mouseEnteredHorizontal = isInHorizontalThumb(p);
            data.mouseEnteredVertical = isInVerticalThumb(p);
            if (data.mouseEnteredHorizontal || data.mouseEnteredVertical) {
                component.setCursor(Cursor.getDefaultCursor());
            } else {
                component.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }
            component.repaint();
        });

        mma.addMouseMotionAction(QRMouseMotionListener.TYPE.DRAG, this::dragScrollBar);

        ma.addMouseAction(QRMouseListener.TYPE.PRESS, es -> {
			if (es.getButton() != MouseEvent.BUTTON1) {
                return;
            }
            verticalScrollTimer.stop();
            barData.scrollBarValueUpdate();
            Point point = es.getPoint();
            data.mouseEnteredVertical = isInVerticalThumb(point);
            data.mouseEnteredHorizontal = isInHorizontalThumb(point);
            data.mousePressedVertical = data.mouseEnteredVertical;
            data.mousePressedHorizontal = data.mouseEnteredHorizontal;
            data.pressPoint = es.getLocationOnScreen();
            data.dragLocationY = component.getY();
            data.dragLocationX = component.getX();
            if (editor != null) {
                editorCaretIndex.set(editor.getCaretPosition());
                editor.getCaret().setVisible(false);
            }
            component.repaint();
        });

        ma.addMouseAction(QRMouseListener.TYPE.RELEASE, es -> {
            data.mousePressedVertical = false;
            data.mousePressedHorizontal = false;
            if (editor != null) {
                editorCaretIndex.set(editor.getCaretPosition());
                if (editor.getHighlighter() == null) {
                    editor.setHighlighter(highlighter);
                }
                editor.getCaret().setVisible(true);
            }
            component.repaint();
        });
    }

    private void scrollToY(int targetY) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> scrollToY(targetY));
            return;
        }
        this.targetY = clampY(targetY);
        if (!verticalScrollTimer.isRunning()) {
            verticalScrollTimer.start();
        }
    }

    private void animateVerticalScroll() {
        if (component == null) {
            verticalScrollTimer.stop();
            return;
        }
        int currentY = component.getY();
        int diff = targetY - currentY;
        if (diff == 0) {
            verticalScrollTimer.stop();
            barData.scrollBarValueUpdate();
            component.repaint();
            return;
        }
        int step = Math.max(1, Math.abs(diff) / 4);
        int nextY = currentY + (diff > 0 ? step : -step);
        if ((diff > 0 && nextY > targetY) || (diff < 0 && nextY < targetY)) {
            nextY = targetY;
        }
        component.setLocation(clampX(component.getX()), clampY(nextY));
        barData.scrollBarValueUpdate();
        component.repaint();
    }

    private void dragScrollBar(MouseEvent e) {
        if (!data.mousePressedVertical && !data.mousePressedHorizontal) {
            restoreEditorSelection();
            return;
        }
        if (editor != null) {
            editor.setHighlighter(null);
            editor.getCaret().setVisible(false);
            editor.setCaretPosition(editorCaretIndex.get());
        }
        int x = component.getX();
        int y = component.getY();
        if (data.mousePressedVertical && data.maxY > 0) {
            int diffY = e.getYOnScreen() - data.pressPoint.y;
            double scrollableHeight = data.size.height - data.parentSize.height;
            y = clampY((int) Math.round(data.dragLocationY - diffY * scrollableHeight / data.maxY));
        }
        if (data.mousePressedHorizontal && data.maxX > 0) {
            int diffX = e.getXOnScreen() - data.pressPoint.x;
            double scrollableWidth = data.size.width - data.parentSize.width;
            x = clampX((int) Math.round(data.dragLocationX - diffX * scrollableWidth / data.maxX));
        }
        component.setLocation(x, y);
        component.repaint();
    }

    private void restoreEditorSelection() {
        if (editor != null && editor.getHighlighter() == null) {
            editor.setHighlighter(highlighter);
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (component != null && barData.getScrollBarData() != null) {
            int contentHeight = Math.max(height, component.getHeight());
            component.setBounds(clampX(component.getX()), clampY(component.getY()), width, contentHeight);
            refreshEditorSize();
            clampContentLocation();
            barData.scrollBarValueUpdate();
        }
    }

    private void refreshEditorSize() {
        if (editor == null || getWidth() <= 0) {
            return;
        }
        if (editor.getWidth() != getWidth()) {
            editor.setSize(getWidth(), Math.max(getHeight(), editor.getHeight()));
        }
        Rectangle2D r;
        try {
            r = editor.modelToView2D(editor.getDocument().getLength());
            if (r == null) {
                return;
            }
        } catch (Exception ex) {
            return;
        }
        Insets insets = editor.getInsets();
        int height = Math.max(getHeight(), (int) Math.ceil(r.getY() + r.getHeight() + insets.top + insets.bottom));
        if (editor.getWidth() != getWidth() || editor.getHeight() != height) {
            editor.setSize(getWidth(), height);
            editor.revalidate();
        }
        barData.scrollBarValueUpdate();
    }

    private void clampContentLocation() {
        if (component != null) {
            component.setLocation(clampX(component.getX()), clampY(component.getY()));
        }
    }

    private int clampY(int y) {
        if (component == null) {
            return 0;
        }
        int minY = Math.min(0, getHeight() - component.getHeight());
        return Math.max(minY, Math.min(0, y));
    }

    private int clampX(int x) {
        if (component == null) {
            return 0;
        }
        int minX = Math.min(0, getWidth() - component.getWidth());
        return Math.max(minX, Math.min(0, x));
    }

    private boolean isInVerticalThumb(Point p) {
        if (!data.verticalScrollbarVisible) {
            return false;
        }
        int barSize = QRInternalScrollBarData.BAR_SIZE;
        double thumbX = data.parentSize.width - barSize - data.location.x;
        double thumbY = data.sy - data.location.y;
        return p.x >= thumbX && p.x <= thumbX + barSize
                && p.y >= thumbY && p.y <= thumbY + data.sh;
    }

    private boolean isInHorizontalThumb(Point p) {
        if (!data.horizontalScrollbarVisible) {
            return false;
        }
        int barSize = QRInternalScrollBarData.BAR_SIZE;
        double thumbX = data.sx - data.location.x;
        double thumbY = data.parentSize.height - barSize - data.location.y;
        return p.y >= thumbY && p.y <= thumbY + barSize
                && p.x >= thumbX && p.x <= thumbX + data.sw;
    }
}

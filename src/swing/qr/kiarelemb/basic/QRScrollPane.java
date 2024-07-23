package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRSleepUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import method.qr.kiarelemb.utils.QRThreadBuilder;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.assembly.QRCaret;
import swing.qr.kiarelemb.assembly.QRLineNumberComponent;
import swing.qr.kiarelemb.assembly.QRScrollBarUI;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRMouseWheelListenerAdd;
import swing.qr.kiarelemb.listener.QRMouseWheelListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 22:06
 **/
public class QRScrollPane extends JScrollPane implements QRComponentUpdate, QRMouseWheelListenerAdd {
    protected QRScrollBar vBar;
    protected QRScrollBar hBar;
    protected QRMouseWheelListener mouseWheelListener;
    protected QRLineNumberComponent lineNumberComponent;
    /**
     * 单次滚动休眠时间
     */
    public static int scrollSpeed = QRSystemUtils.IS_WINDOWS ? 1 : 3;
    private final QRScrollBarUI horUI;
    private final QRScrollBarUI verUI;
    private boolean borderPaint = false;
    private JEditorPane view = null;
    /**
     * 单次平滑滚动的行数
     */
    private int scrollLine = 3;
    /**
     * 文本面整的行高
     */
    private int scrollHeight = 10;
    private final ArrayList<QRScrollPane> followedToScroll = new ArrayList<>();

    public QRScrollPane() {
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.vBar = QRScrollBar.getVerticalScrollBar();
        this.hBar = QRScrollBar.getHorizontalScrollBar();
        setHorizontalScrollBar(this.hBar);
        setVerticalScrollBar(this.vBar);
        this.horUI = this.hBar.barUi();
        this.verUI = this.vBar.barUi();

        setBorder(null);
        setOpaque(false);
        componentFresh();
    }

    /**
     * 各滚动条的位置复原
     */
    public void locationFresh() {
        if (vBar.isVisible()) {
            vBar.setValue(0);
        }
        if (hBar.isVisible()) {
            hBar.setValue(0);
        }
    }

    /**
     * 设置平滑滚动
     */
    public void setScrollSmoothly() {
        this.vBar.setUnitIncrement(0);
        this.hBar.setUnitIncrement(0);
//        getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        addMouseWheelListener();
    }

    /**
     * 添加鼠标滚轮监听
     */
    @Override
    public void addMouseWheelListener() {
        if (this.mouseWheelListener == null) {
            this.mouseWheelListener = new QRMouseWheelListener();
            this.mouseWheelListener.add(e -> {
                MouseWheelEvent ev = e;
                ev.consume();
                mouseWheelMove(ev);
            });
            addMouseWheelListener(this.mouseWheelListener);
        }
    }

    /**
     * 已自动添加
     *
     * @param ar 鼠标滚轮事件
     */
    @Override
    public void addMouseWheelAction(QRActionRegister<MouseWheelEvent> ar) {
        if (this.mouseWheelListener != null) {
            this.mouseWheelListener.add(ar);
        }
    }

    /**
     * 添加同步滚动的滚动面板
     *
     * @param scrollPane 其他滚动面板
     */
    public void addFollowedToScrollPane(QRScrollPane scrollPane) {
        this.followedToScroll.add(scrollPane);
    }

    /**
     * 设置平滑滚动
     *
     * @param line 单次滚动的值
     */
    public void setScrollSmoothly(int line) {
        this.scrollLine = line;
        setScrollSmoothly();
    }

    public QRScrollBar verticalScrollBar() {
        return this.vBar;
    }

    public QRScrollBar horizontalScrollBar() {
        return this.hBar;
    }

    /**
     * 默认只有滚动文本的功能
     *
     * @param e 传入的事件
     */
    private void mouseWheelMove(MouseWheelEvent e) {
        if (e.isShiftDown()) {
            if (this.hBar.isVisible()) {
                ThreadPoolExecutor scroll = QRThreadBuilder.singleThread("scroll");
                if (followedToScroll.isEmpty()) {
                    scroll.submit(() -> setScrollBarRollSmoothly(e.getWheelRotation() < 0, this.hBar, empty));
                } else {
                    scroll.submit(() -> setScrollBarRollSmoothly(e.getWheelRotation() < 0, this.hBar, followedToScroll.stream().map(QRScrollPane::getHorizontalScrollBar).filter(Objects::nonNull).toList()));
                }
            }
        } else {
            if (this.vBar.isVisible()) {
                ThreadPoolExecutor scroll = QRThreadBuilder.singleThread("scroll");
                if (followedToScroll.isEmpty()) {
                    scroll.submit(() -> setScrollBarRollSmoothly(e.getWheelRotation() < 0, this.vBar, empty));
                } else {
                    scroll.submit(() -> setScrollBarRollSmoothly(e.getWheelRotation() < 0, this.vBar, followedToScroll.stream().map(QRScrollPane::getVerticalScrollBar).filter(Objects::nonNull).toList()));
                }
            }
        }
    }

    private static final ArrayList<JScrollBar> empty = new ArrayList<>(0);

    private void setScrollBarRollSmoothly(boolean up, QRScrollBar bar, List<JScrollBar> otherBar) {
        int value = bar.getValue();
        final int maxValue = bar.getMaximum();
        if (this.view != null) {
            if (this.view.getCaret() instanceof QRCaret c) {
                scrollHeight = c.caretHeight();
            } else {
                try {
                    Rectangle2D r = view.modelToView2D(0);
                    if (r != null) {
                        scrollHeight = (int) r.getHeight();
                    }
                } catch (BadLocationException ignored) {
                }
            }
        }
        int extent0 = this.scrollHeight * this.scrollLine;
        final int range;
        // 确保在循环 50 次内把滚动条更新完
        int extent;
        if (up) {
            range = Math.min(value, extent0);
            extent = Math.max(range / 50, 2);
            for (int i = 0; i < range; i += extent) {
                bar.minusValue(extent);
                if (!otherBar.isEmpty())
                    for (JScrollBar b : otherBar) {
                        b.setValue(Math.max(b.getMinimum(), b.getValue() - extent));
                    }
                QRSleepUtils.sleep(scrollSpeed);
            }
        } else {
            range = value + extent0 > maxValue ? maxValue - value : extent0;
            extent = Math.max(range / 50, 2);
            for (int i = 0; i < range; i += extent) {
                bar.plusValue(extent);
                if (!otherBar.isEmpty())
                    for (JScrollBar b : otherBar) {
                        b.setValue(Math.min(b.getMaximum(), b.getValue() + extent));
                    }
                QRSleepUtils.sleep(scrollSpeed);
            }
        }
    }

    /**
     * 为 {@link QRTextPane} 添加行号
     */
    public QRLineNumberComponent addLineNumberModelForTextPane() {
        if (this.lineNumberComponent == null && this.view != null && this.view instanceof QRTextPane) {
            this.lineNumberComponent = new QRLineNumberComponent((QRTextPane) this.view);
            this.lineNumberComponent.setAlignment(QRLineNumberComponent.RIGHT_ALIGNMENT);
            setRowHeaderView(this.lineNumberComponent);
        }
        return this.lineNumberComponent;
    }

    public void setBorderPaint(boolean borderPaint) {
        this.borderPaint = borderPaint;
    }

    public boolean borderPaint() {
        return this.borderPaint;
    }


    @Override
    public void setViewport(JViewport viewport) {
        super.setViewport(viewport);
        Component view = viewport.getView();
        if (view instanceof JEditorPane v) {
            this.view = v;
        }
    }

    @Override
    public JViewport getViewport() {
        JViewport viewport = super.getViewport();
        if (viewport != null) {
            viewport.setOpaque(false);
        }
        return viewport;
    }

    @Override
    public void setViewportView(Component view) {
        JViewport viewport = new JViewport();
        viewport.setView(view);
        setViewport(viewport);
        getViewport().setOpaque(false);
//        if (view instanceof JComponent com) {
//            com.setOpaque(false);
//        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        if (this.borderPaint) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, QRSwing.windowImageSet ? 0.5f : 1f));
            if (QRSwing.windowRound) {
                final int arc = 15;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(QRColorsAndFonts.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            } else {
                g.setColor(QRColorsAndFonts.BORDER_COLOR);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
            return;
        }
        super.paintBorder(g);
    }

    @Override
    public void componentFresh() {
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        this.horUI.componentFresh();
        this.verUI.componentFresh();
        this.vBar.componentFresh();
        this.hBar.componentFresh();
//        final Component view = getViewport().getView();
//        if (view instanceof QRComponentUpdate v) {
//            v.componentFresh();
//        } else {
//            getViewport().setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
//        }
    }

}
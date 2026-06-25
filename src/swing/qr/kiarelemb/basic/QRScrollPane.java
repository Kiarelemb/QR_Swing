package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRSystemUtils;
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

/**
 * QR Swing 的滚动面板。
 *
 * <p>该类基于 {@link JScrollPane}，统一使用 {@link QRScrollBar} 和
 * {@link QRScrollBarUI}，并提供平滑滚轮滚动、横向/纵向同步滚动、
 * 边框绘制以及为 {@link QRTextPane} 添加行号组件的能力。
 *
 * <p>普通文本、列表、表格等滚动场景优先使用该类；如果需要把滚动条绘制在
 * 文本组件内部，则使用 {@link QRTextPane#addInternalScrollPane()}。
 *
 * @author Kiarelemb QR
 * @program QR_Swing
 * @create 2022-11-21 22:06
 */
public class QRScrollPane extends JScrollPane implements QRComponentUpdate, QRMouseWheelListenerAdd {
    protected QRScrollBar vBar;
    protected QRScrollBar hBar;
    protected QRMouseWheelListener mouseWheelListener;
    protected QRLineNumberComponent lineNumberComponent;
    /**
     * 单次滚动休眠时间
     */
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
    private ScrollAnimator verticalScrollAnimator;
    private ScrollAnimator horizontalScrollAnimator;

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
        if (this.mouseWheelListener == null) {
            addMouseWheelListener();
        }
        if (this.mouseWheelListener != null) {
            this.mouseWheelListener.add(ar);
        }
    }

    //region 取得监听器

    public QRMouseWheelListener mouseWheelListener() {
        return mouseWheelListener;
    }

    //endregion

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
     * 处理平滑滚轮滚动。
     *
     * <p>按住 Shift 时优先滚动横向滚动条，否则滚动纵向滚动条。
     * 如果当前滚动面板配置了跟随滚动的其他 {@code QRScrollPane}，
     * 会把滚动距离同步应用到对应方向的滚动条上。
     *
     * @param e 鼠标滚轮事件
     */
    private void mouseWheelMove(MouseWheelEvent e) {
        QRScrollBar bar;
        List<JScrollBar> followedBars;
        if (e.isShiftDown()) {
            if (this.hBar.isVisible()) {
                bar = this.hBar;
                followedBars = followedToScroll.isEmpty()
                        ? empty
                        : followedToScroll.stream().map(QRScrollPane::getHorizontalScrollBar).filter(Objects::nonNull).toList();
            } else {
                return;
            }
        } else {
            if (this.vBar.isVisible()) {
                bar = this.vBar;
                followedBars = followedToScroll.isEmpty()
                        ? empty
                        : followedToScroll.stream().map(QRScrollPane::getVerticalScrollBar).filter(Objects::nonNull).toList();
            } else {
                return;
            }
        }
        scrollBarSmoothly(bar, followedBars, wheelDistance(e));
    }

    private static final ArrayList<JScrollBar> empty = new ArrayList<>(0);

    /**
     * 以较小步长滚动滚动条，形成平滑滚动效果。
     *
     * <p>滚动距离由当前文本行高和 {@link #scrollLine} 决定；如果绑定了其他滚动条，
     * 它们会使用相同步长同步移动。
     */
    private int wheelDistance(MouseWheelEvent e) {
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
        double rotation = e.getPreciseWheelRotation() == 0 ? e.getWheelRotation() : e.getPreciseWheelRotation();
        int unit = Math.max(1, this.scrollHeight * this.scrollLine);
        int distance = (int) Math.round(rotation * unit);
        if (distance == 0 && rotation != 0) {
            distance = rotation > 0 ? 1 : -1;
        }
        return distance;
    }

    private void scrollBarSmoothly(QRScrollBar bar, List<JScrollBar> otherBars, int distance) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> scrollBarSmoothly(bar, otherBars, distance));
            return;
        }
        ScrollAnimator animator = bar == this.hBar ? horizontalAnimator() : verticalAnimator();
        animator.scroll(distance, otherBars);
    }

    private ScrollAnimator verticalAnimator() {
        if (verticalScrollAnimator == null) {
            verticalScrollAnimator = new ScrollAnimator(this.vBar);
        }
        return verticalScrollAnimator;
    }

    private ScrollAnimator horizontalAnimator() {
        if (horizontalScrollAnimator == null) {
            horizontalScrollAnimator = new ScrollAnimator(this.hBar);
        }
        return horizontalScrollAnimator;
    }

    private static int maxBarValue(JScrollBar bar) {
        return Math.max(bar.getMinimum(), bar.getMaximum() - bar.getVisibleAmount());
    }

    private static int clampBarValue(JScrollBar bar, int value) {
        return Math.max(bar.getMinimum(), Math.min(maxBarValue(bar), value));
    }

    private static class ScrollAnimator {
        private static final int INTERVAL = QRSystemUtils.IS_WINDOWS ? 8 : 12;
        private static final int MIN_STEP = 1;

        private final JScrollBar bar;
        private final Timer timer;
        private int target;
        private List<JScrollBar> followedBars = empty;

        private ScrollAnimator(JScrollBar bar) {
            this.bar = bar;
            this.timer = new Timer(INTERVAL, e -> tick());
            this.timer.setCoalesce(true);
        }

        private void scroll(int distance, List<JScrollBar> followedBars) {
            this.followedBars = followedBars == null ? empty : followedBars;
            this.target = clampBarValue(bar, (timer.isRunning() ? target : bar.getValue()) + distance);
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        private void tick() {
            int value = bar.getValue();
            int diff = target - value;
            if (diff == 0) {
                timer.stop();
                syncFollowedBars(target);
                return;
            }
            int step = Math.max(MIN_STEP, Math.abs(diff) / 4);
            int next = value + (diff > 0 ? step : -step);
            if ((diff > 0 && next > target) || (diff < 0 && next < target)) {
                next = target;
            }
            bar.setValue(clampBarValue(bar, next));
            syncFollowedBars(bar.getValue());
        }

        private void syncFollowedBars(int value) {
            if (followedBars.isEmpty()) {
                return;
            }
            double ratio = maxBarValue(bar) == bar.getMinimum()
                    ? 0
                    : (value - bar.getMinimum()) / (double) (maxBarValue(bar) - bar.getMinimum());
            for (JScrollBar followedBar : followedBars) {
                int followedMax = maxBarValue(followedBar);
                int followedValue = followedBar.getMinimum() + (int) Math.round((followedMax - followedBar.getMinimum()) * ratio);
                followedBar.setValue(clampBarValue(followedBar, followedValue));
            }
        }
    }

    /**
     * 为当前视图中的 {@link QRTextPane} 添加行号组件。
     *
     * <p>该方法只会创建一个 {@link QRLineNumberComponent}，重复调用会返回已有实例。
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
        if (viewport == null) {
            return;
        }
        Component view = viewport.getView();
        if (view instanceof JEditorPane v) {
            this.view = v;
        }
        refreshViewportBackground();
    }

    @Override
    public JViewport getViewport() {
        return super.getViewport();
    }

    @Override
    public void setViewportView(Component view) {
        JViewport viewport = new JViewport();
        viewport.setView(view);
        setViewport(viewport);
        refreshViewportBackground();
    }

    /**
     * 让滚动面板空白区域跟随内部视图背景，避免表格、列表、文本框在未填满
     * viewport 时露出 JScrollPane/JViewport 的默认颜色。
     */
    protected void refreshViewportBackground() {
        JViewport viewport = super.getViewport();
        if (viewport == null) {
            return;
        }
        Component view = viewport.getView();
        Color background = view == null ? QRColorsAndFonts.FRAME_COLOR_BACK : view.getBackground();
        if (background == null) {
            background = QRColorsAndFonts.FRAME_COLOR_BACK;
        }
        viewport.setBackground(background);
        viewport.setOpaque(!(view instanceof JComponent component) || component.isOpaque());
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
        refreshViewportBackground();
    }
}
package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRScrollBarUI;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

/**
 * QR Swing 的主题滚动条。
 *
 * <p>该类基于 {@link JScrollBar}，使用 {@link QRScrollBarUI} 绘制主题滚动条，
 * 默认透明、单位滚动 30，并支持拖动时同步其他同方向滚动条。通常由 {@link QRScrollPane}
 * 自动创建，业务代码很少需要直接实例化。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-21 22:22
 **/
public class QRScrollBar extends JScrollBar implements QRComponentUpdate {
    private final QRScrollBarUI barUI;
    private final ArrayList<JScrollBar> synchronisedScrollBor = new ArrayList<>();

    private QRScrollBar(boolean horizontal) {
        setOrientation(horizontal ? JScrollBar.HORIZONTAL : JScrollBar.VERTICAL);
        barUI = new QRScrollBarUI(horizontal);
        setUI(barUI);
        setUnitIncrement(30);
        setOpaque(false);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                synchronisedScrollBor.forEach(bar -> {
                    if (bar.getOrientation() == getOrientation()) {
                        bar.setValue(getValue());
                    }
                });
            }
        });
        componentFresh();
    }

    /**
     * 隐藏滚动条外观但保留滚动条对象和数值功能。
     *
     * <p>调用后滚动条首选尺寸为 0，用户无法通过鼠标拖动它，但代码仍可读取或设置滚动值。</p>
     */
    public void setExistButVisibleFalse() {
        setUI(new QRScrollBarUI(getOrientation() == 0) {
            @Override
            public Dimension getPreferredSize(JComponent c) {
                Dimension preferredSize;
                preferredSize = new Dimension(0, 0);
                c.setPreferredSize(preferredSize);
                return preferredSize;
            }

            @Override
            protected void configureScrollBarColors() {
                //滚动条的颜色
                thumbColor = QRColorsAndFonts.SCROLL_COLOR;
                thumbDarkShadowColor = thumbColor;
                setThumbBounds(0, 0, 0, 0);
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

            }
        });
    }

    /**
     * 添加一个拖动时同步的滚动条。
     *
     * <p>只有方向相同的滚动条会同步值。</p>
     *
     * @param bar 要同步的滚动条
     */
    public void addSynchronisedScrollBor(JScrollBar bar) {
        synchronisedScrollBor.add(bar);
    }

    int scrollHeight = 10;

    private void rollSmoothly(boolean up) {
//        int value = getValue();
//        final int maxValue = getMaximum();
//        Rectangle2D r = null;
//        if (this.view instanceof QRTextPane t) {
//            r = t.positionRectangle(0);
//            if (r != null) {
//                scrollHeight = (int) r.getHeight();
//            }
//        }
//        if (this.view != null && this.view.getCaret() instanceof QRCaret c && r != null) {
//            scrollHeight = c.caretHeight();
//        }
//        int extent0 = this.scrollHeight * this.scrollLine;
//        final int range;
//        // 确保在循环 50 次内把滚动条更新完
//        int extent;
//        if (up) {
//            range = Math.min(value, extent0);
//            extent = Math.max(range / 50, 2);
//            for (int i = 0; i < range; i += extent) {
//                minusValue(extent);
//                if (!otherBar.isEmpty())
//                    for (JScrollBar b : otherBar) {
//                        b.setValue(Math.max(b.getMinimum(), b.getValue() - extent));
//                    }
//                QRSleepUtils.sleep(scrollSpeed);
//            }
//        } else {
//            range = value + extent0 > maxValue ? maxValue - value : extent0;
//            extent = Math.max(range / 50, 2);
//            for (int i = 0; i < range; i += extent) {
//                plusValue(extent);
//                if (!otherBar.isEmpty())
//                    for (JScrollBar b : otherBar) {
//                        b.setValue(Math.min(b.getMaximum(), b.getValue() + extent));
//                    }
//                QRSleepUtils.sleep(scrollSpeed);
//            }
//        }
    }

    /**
     * 向较小方向移动滚动值。
     *
     * @param value 移动量
     */
    public void minusValue(int value) {
        setValue(Math.max(getMinimum(), getValue() - value));
    }

    /**
     * 向较大方向移动滚动值。
     *
     * @param value 移动量
     */
    public void plusValue(int value) {
        setValue(Math.min(getMaximum(), getValue() + value));
    }

    /**
     * 按比例设置滚动条值。
     *
     * @param value 比例，{@code <= 0} 滚到最小值，{@code >= 1} 滚到最大值
     */
    public void setValue(double value) {
        if (value <= 0) {
            setValue(0);
            return;
        }

        int maximum = getMaximum();
        if (value >= 1) {
            setValue(maximum);
        } else {
            int v = Math.min((int) (maximum * value), maximum);
            setValue(v);
        }
    }

    /**
     * @return 当前滚动条使用的 UI 对象
     */
    public QRScrollBarUI barUi() {
        return barUI;
    }

    @Override
    public void componentFresh() {
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        barUI.componentFresh();
    }

    /**
     * 创建竖向 QR 滚动条。
     *
     * @return 竖向滚动条
     */
    public static QRScrollBar getVerticalScrollBar() {
        return new QRScrollBar(false);
    }

    /**
     * 创建横向 QR 滚动条。
     *
     * @return 横向滚动条
     */
    public static QRScrollBar getHorizontalScrollBar() {
        return new QRScrollBar(true);
    }
}

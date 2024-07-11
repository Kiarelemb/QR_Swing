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
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
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
     * 该设置不会使滚动条可见，也不会使滚动条失去它本该有的功能。即，它实际存在，可通过其他方式设置值，但看不到，也不能用鼠标拖动
     */
    public void setExistButVisibleFalse() {
        setUI(new QRScrollBarUI(getOrientation() == 0) {
            @Override
            public Dimension getPreferredSize (JComponent c){
                Dimension preferredSize;
                preferredSize = new Dimension(0, 0);
                c.setPreferredSize(preferredSize);
                return preferredSize;
            }

            @Override
            protected void configureScrollBarColors () {
                //滚动条的颜色
                thumbColor = QRColorsAndFonts.SCROLL_COLOR;
                thumbDarkShadowColor = thumbColor;
                setThumbBounds(0, 0, 0, 0);
            }
        });
    }

    public void addSynchronisedScrollBor(JScrollBar bar) {
        synchronisedScrollBor.add(bar);
    }

    public void minusValue(int value) {
        setValue(Math.max(getMinimum(), getValue() - value));
    }

    public void plusValue(int value) {
        setValue(Math.min(getMaximum(), getValue() + value));
    }

    /**
     * @param value 比例
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

    public QRScrollBarUI barUi() {
        return barUI;
    }

    @Override
    public void componentFresh() {
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        barUI.componentFresh();
    }

    public static QRScrollBar getVerticalScrollBar() {
        return new QRScrollBar(false);
    }

    public static QRScrollBar getHorizontalScrollBar() {
        return new QRScrollBar(true);
    }
}
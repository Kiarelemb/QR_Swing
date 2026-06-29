package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRSplitPaneUI;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;

/**
 * QR Swing 的主题分割面板。
 *
 * <p>该类基于 {@link JSplitPane}，使用自定义 {@link QRSplitPaneUI}，
 * 默认透明、连续布局、10 像素分隔条，并在主题刷新时尝试刷新左右/上下子组件。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRSplitPane split = new QRSplitPane(JSplitPane.VERTICAL_SPLIT);
 * split.setTopComponent(topPanel);
 * split.setBottomComponent(bottomPanel);
 * split.setDividerLocation(0.65);
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-22 11:09
 **/
public class QRSplitPane extends JSplitPane implements QRComponentUpdate {
    private boolean borderPaint = false;

    public QRSplitPane(int newOrientation) {
        super(newOrientation);
        setOpaque(false);
        setContinuousLayout(true);
        setDividerSize(10);
        setOrientation(orientation);
        setUI(new QRSplitPaneUI());
        componentFresh();
    }

    /**
     * 设置是否绘制边框。
     *
     * @param borderPaint true 表示允许绘制边框
     */
    public void setBorderPaint(boolean borderPaint) {
        this.borderPaint = borderPaint;
    }

    /**
     * @return 当前是否绘制边框
     */
    public boolean borderPaint() {
        return borderPaint;
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.Src);
        if (borderPaint) {
            super.paintBorder(g);
//			if (QRSwing.windowRound) {
//				final int arc = 15;
//				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//				g2.setColor(QRColorsAndFonts.BORDER_COLOR);
//				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
//			} else {
//				g.setColor(QRColorsAndFonts.BORDER_COLOR);
//				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//			}
//			return;
        }
    }

    @Override
    public void componentFresh() {
//        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        setBorder(BorderFactory.createEmptyBorder());
        setBorderPaint(false);
        panelLoop();
    }

    /**
     * 遍历子组件并刷新实现了 {@link QRComponentUpdate} 的组件。
     */
    public void panelLoop() {
        final Component[] components = getComponents();
        for (Component component : components) {
            try {
                ((QRComponentUpdate) component).componentFresh();
            } catch (Exception ignore) {
            }
        }
    }
}

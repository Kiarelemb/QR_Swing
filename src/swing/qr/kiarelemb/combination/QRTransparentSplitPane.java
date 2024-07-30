package swing.qr.kiarelemb.combination;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRTransparentSplitPane
 * @description TODO
 * @create 2024/7/20 下午11:32
 */
public class QRTransparentSplitPane extends QRPanel {
    protected Component divider;
    protected Component top;
    protected Component bottom;

    public QRTransparentSplitPane() {
        super(false, null);
        divider = add(new Divider());
    }

    public void setTopComponent(Component c) {
        if (top != null) remove(top);
        top = add(c);
    }

    public void setBottomComponent(Component c) {
        if (bottom != null) remove(bottom);
        bottom = add(c);
    }

    protected void paintDivider(Graphics2D g) {

        g.setColor(QRColorsAndFonts.LINE_COLOR);
        g.fillRect(0, 0, divider.getWidth(), 10);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        if (height < 10) {
            return;
        }
        if (top != null) {
            top.setBounds(0, 0, width, dividerLocation);
        }
        if (bottom != null) {
            divider.setBounds(0, dividerLocation, width, 10);
            bottom.setBounds(0, dividerLocation + 10, width, height - dividerLocation - 10);
        } else {
            divider.setBounds(0, dividerLocation, width, height - dividerLocation);
        }
        super.setBounds(x, y, width, height);
    }

    private int dividerLocation = 100;

    public void setDividerLocation(int dividerLocation) {
        this.dividerLocation = dividerLocation;
    }

    public int getDividerLocation() {
        return dividerLocation;
    }

    private class Divider extends QRPanel {

        private Point locationOnScreen;

        public Divider() {
            setPreferredSize(new Dimension(100, 10));
            addMouseMotionListener();
            addMouseListener();
        }

        @Override
        protected void mouseEnter(MouseEvent e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        }

        @Override
        protected void mouseExit(MouseEvent e) {
            setCursorDefault();
        }

        @Override
        protected void mousePress(MouseEvent e) {
            locationOnScreen = e.getLocationOnScreen();
        }

        @Override
        protected void mouseDrag(MouseEvent e) {
            if (QRTransparentSplitPane.this.top == null || QRTransparentSplitPane.this.bottom == null) {
                return;
            }
            Point point = e.getLocationOnScreen();
            int diff = point.y - locationOnScreen.y;
            Dimension size = QRTransparentSplitPane.this.getSize();
            Dimension topSize = QRTransparentSplitPane.this.top.getSize();

            // 大于0则向下拖动，小于0则向上拖动
            dividerLocation = topSize.height + diff;
            QRTransparentSplitPane.this.top.setPreferredSize(new Dimension(topSize.width, dividerLocation));
            QRTransparentSplitPane.this.bottom.setPreferredSize(new Dimension(topSize.width, size.height - dividerLocation - 10));
            revalidate();
            repaint();
            locationOnScreen = point;
            locationOnScreen.y = point.y;
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, QRSwing.windowBackgroundImageAlpha));
            g2.addRenderingHints(rh);
            paintDivider(g2);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            super.paintBorder(g);
        }
    }
}
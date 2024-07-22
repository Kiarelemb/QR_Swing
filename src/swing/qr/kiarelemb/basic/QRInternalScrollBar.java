package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.data.QRInternalScrollBarData;
import swing.qr.kiarelemb.inter.QRInternalScrollbarUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRInternalScrollBar
 * @description TODO
 * @create 2024/7/17 下午8:27
 */
@Deprecated
public class QRInternalScrollBar implements Border {
    private final QRInternalScrollbarUpdate internalBar;

    public QRInternalScrollBar(QRInternalScrollbarUpdate internalBar) {
        this.internalBar = internalBar;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        internalBar.scrollBarValueUpdate();
        QRInternalScrollBarData data = internalBar.getScrollBarData();
        if (!data.verticalScrollbarVisible && !data.horizontalScrollbarVisible) {
            return;
        }
        g.translate(data.size.width - 10, -data.location.y);
        g.setColor(QRColorsAndFonts.SCROLL_COLOR);
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.addRenderingHints(rh);
        if (data.verticalScrollbarVisible) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, data.mousePressedVertical ? 1f : (data.mouseEnteredVertical ? 0.8f : 0.6f)));
            g2.fillRect(0, (int) data.sy, 10, (int) data.sh);
        }
        if (data.horizontalScrollbarVisible) {
            g2.translate(0, data.size.height - 10);
            g2.setColor(QRColorsAndFonts.SCROLL_COLOR);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, data.mousePressedHorizontal ? 1f : (data.mouseEnteredHorizontal ? 0.8f : 0.6f)));
            g2.fillRect((int) data.sx, 0, (int) data.sw, 10);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(10, 10, 10, 10);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
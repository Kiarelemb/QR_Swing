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
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(QRColorsAndFonts.SCROLL_COLOR);
        int barSize = QRInternalScrollBarData.BAR_SIZE;
        if (data.verticalScrollbarVisible) {
            float alpha = data.mousePressedVertical ? 0.9f : (data.mouseEnteredVertical ? 0.7f : 0.45f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int thumbX = (int) Math.round(data.parentSize.width - barSize - data.location.x);
            int thumbY = (int) Math.round(data.sy - data.location.y);
            int thumbHeight = (int) Math.round(data.sh);
            g2.fillRoundRect(thumbX, thumbY, barSize - 1, thumbHeight, barSize, barSize);
        }
        if (data.horizontalScrollbarVisible) {
            float alpha = data.mousePressedHorizontal ? 0.9f : (data.mouseEnteredHorizontal ? 0.7f : 0.45f);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int thumbX = (int) Math.round(data.sx - data.location.x);
            int thumbY = (int) Math.round(data.parentSize.height - barSize - data.location.y);
            int thumbWidth = (int) Math.round(data.sw);
            g2.fillRoundRect(thumbX, thumbY, thumbWidth, barSize - 1, barSize, barSize);
        }
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, 0, 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
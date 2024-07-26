package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.QRSwing;

import javax.swing.text.DefaultHighlighter;
import java.awt.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRHighlighter
 * @description TODO
 * @create 2024/7/23 下午10:01
 */
public class QRHighlighter extends DefaultHighlighter {

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, QRSwing.windowImageSet? 0.5f : 1f));
        super.paint(g);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
}
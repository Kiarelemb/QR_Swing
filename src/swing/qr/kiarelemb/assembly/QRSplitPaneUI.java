package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-22 11:32
 **/
public class QRSplitPaneUI extends BasicSplitPaneUI {


    public QRSplitPaneUI() {
    }

    @Override
    public BasicSplitPaneDivider createDefaultDivider() {
        return new BasicSplitPaneDivider(this) {

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        divider.setCursor(Cursor.getDefaultCursor());
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        int orientation = getOrientation();
                        Cursor cursor = new Cursor(orientation == JSplitPane.VERTICAL_SPLIT ? Cursor.N_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR);
                        divider.setCursor(cursor);
                    }
                });
            }

            public void setBorder(Border b) {
            }

            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, QRSwing.windowImageSet ? 0.5f : 1f));
                g2.addRenderingHints(rh);
                g2.setColor(QRColorsAndFonts.LINE_COLOR);
                Dimension size = getSize();
                g2.fillRect(0, 0, size.width, size.height);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                super.paint(g);
            }
        };
    }
}
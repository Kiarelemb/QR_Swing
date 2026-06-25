package swing.qr.kiarelemb.utils;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRFrame;

import java.awt.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRCloseButton
 * @description 一个符号为 x 的按钮
 * @create 2024/3/31 22:37
 */
public class QRCloseButton extends QRButton {
    private final Font font = QRColorsAndFonts.PROCESS_BUTTON_FONT.deriveFont(11f).deriveFont(Font.BOLD);

    public QRCloseButton() {
        setBorderPainted(false);
        setPreferredSize(new Dimension(30, 30));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        var model = getModel();
        if ((model.isRollover() || model.isPressed()) && isEnabled()) {
            g.setColor(Color.RED);
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.addRenderingHints(rh);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, QRSwing.windowImageSet ? (model.isPressed() ? 1f : (model.isRollover() ? 0.7f : 0.5f)) : 1f));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        QRComponentUtils.componentStringDraw(this, g, QRFrame.CLOSE_MARK, font, QRColorsAndFonts.MENU_COLOR);
    }
}
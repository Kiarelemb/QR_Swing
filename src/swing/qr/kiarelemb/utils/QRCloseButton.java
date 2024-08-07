package swing.qr.kiarelemb.utils;

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

    /**
     * 设置按钮为关闭按钮，使其进入时，变红
     */
    public void setCloseButton() {
        pressColor = Color.RED;
        enterColor = Color.RED;
//        addMouseListener(new QRButtonMouseListener(this) {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                super.mouseEntered(e);
//                //解决一个当鼠(hf)标在关闭按钮右侧的时候，进入按钮仍然是剪头的bug
//                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//            }
//        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        QRComponentUtils.componentStringDraw(this, g, QRFrame.CLOSE_MARK, font, QRColorsAndFonts.MENU_COLOR);
    }
}
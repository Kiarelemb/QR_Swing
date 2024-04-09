package swing.qr.kiarelemb.component.utils;

import swing.qr.kiarelemb.adapter.QRButtonMouseListener;
import swing.qr.kiarelemb.component.basic.QRButton;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.basic.QRFrame;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRCloseButton
 * @description 一个符号为 x 的按钮
 * @create 2024/3/31 22:37
 */
public class QRCloseButton extends QRButton {
    public QRCloseButton() {
        super(QRFrame.CLOSE_MARK);
//		setPreferredSize(new Dimension(30,30));
    }

    /**
     * 设置按钮为关闭按钮，使其进入时，变红
     */
    public void setCloseButton() {
        disableListener();
        addMouseListener(new QRButtonMouseListener(this) {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                //解决一个当鼠(hf)标在关闭按钮右侧的时候，进入按钮仍然是剪头的bug
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    @Override
    public void componentFresh() {
        super.componentFresh();
        setForeground(QRColorsAndFonts.MENU_COLOR);
        setFont(QRColorsAndFonts.PROCESS_BUTTON_FONT.deriveFont(11f).deriveFont(Font.BOLD));
    }
}
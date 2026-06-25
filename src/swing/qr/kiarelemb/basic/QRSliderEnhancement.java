package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.inter.QRComponentUpdate;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRSliderEnhancement
 * @description TODO
 * @create 2024/8/3 上午8:59
 */
public class QRSliderEnhancement extends JComponent implements QRComponentUpdate {
    public QRSliderEnhancement() {
        setOpaque(false);
        setDoubleBuffered(true);
        setLayout(null);
    }

    @Override
    public void componentFresh() {

    }

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(false);
    }

    class Slide extends QRPanel {
        public Slide() {
            setSize(20,20);
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, 20,20);
        }

        @Override
        public void setOpaque(boolean isOpaque) {
            super.setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }
}
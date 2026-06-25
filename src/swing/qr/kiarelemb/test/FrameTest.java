package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.theme.QRSwingThemeDesigner;
import swing.qr.kiarelemb.window.basic.QRFrame;

public class FrameTest {
    public FrameTest() {
        QRFrame mainWindow = new QRFrame();
        // 主窗体的操作代码....

        QRSwingThemeDesigner designer = new QRSwingThemeDesigner(mainWindow);
        designer.setVisible(true);
    }
}
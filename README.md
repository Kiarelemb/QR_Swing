# QRSwing: Java Swing 强有力的拓展控件包
> QRSwing 集基础 `Swing` 控件的功能增加、自研大量拓展控件、自定义界面主题、高规格的窗体背景图功能。经过多年开发后，作者 **[QR](https://github.com/Kiarelebm)** 从过往开发的 `Java` 桌面项目中抽象出了常用的控件及其拓展方法，并封装成开源的包，`QRSwing` 的诞生便是如此。

## 走马观花
### 1. 写一个空白窗体，并在窗体打开时，弹窗 Hello World
```java
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.window.basic.QRDialog;
import swing.qr.kiarelemb.window.basic.QRFrame;

import java.awt.event.WindowEvent;

public class MyFrame extends QRFrame {

    public static void main(String[] args) {
        // 本工具包开始于此方法。在所有窗体或控件调用前，都必须先调用该方法
        // 在调用本方法时，将使用默认的设置进行配置，其文件 GLOBAL_PROP_PATH 和 WINDOW_PROP_PATH 将会创建在程序的根目录下
        QRSwing.start();
        // 注册全局键盘事件，以方便对话窗能按 ESC 关闭窗体
        QRSwing.registerGlobalKeyEvents();
        // 实例化主窗体
        MyFrame frame = new MyFrame();
        // 注册全局键盘事件的主窗体
        QRSwing.registerGlobalEventWindow(frame);
        // 显示窗体
        frame.setVisible(true);
    }

    public MyFrame() {
        // 设置窗体标题
        super("空白窗体");
        // 将窗体标题居中
        setTitleCenter();
        // 设置单击关闭按钮后窗体淡化退出并结束程序
        setCloseButtonSystemExit();
        // 在最开始时，窗体大小和窗体位置是自动计算的，长宽为屏幕的 1/2 ，位置自动据此居中。
        // 且窗体大小和位置将自动记住，并在下次启动时，会自动读取该配置，并设置窗体大小和位置。
    }
 
     /**
     * 该方法已自动添加监听器，可直接重写
     */
    @Override
    public void windowOpened(WindowEvent e) {
        // 实例化对话窗体，并将主窗体设置为其父窗体
        QRDialog dialog = new QRDialog(this);
        // 设置对话窗体标题
        dialog.setTitle("Hello World!");
        // 设置对话窗体标题居中
        dialog.setTitlePlace(QRDialog.CENTER);
        // 设置对话窗体大小
        dialog.setSize(400, 300);
        // 显示对话窗体
        dialog.setVisible(true);
    }
}
```

### 2. 设置窗体背景图，创建菜单栏，并向窗体添加文本面板
```java
import method.qr.kiarelemb.utils.QRRandomUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class MenuTest extends QRFrame {

    public MenuTest(String title) {
        super(title);
        //设置窗体标题居中
        setTitlePlace(SwingConstants.CENTER);
        //设置单击关闭按钮后窗体淡化退出并结束程序
        setCloseButtonSystemExit();
        // 设置主面板的布局
        this.mainPanel.setLayout(new BorderLayout());
        // 用循环添加菜单和子菜单
        for (int i = 0; i < 4; i++) {
            QRButton button = titleMenuPanel.add("Menu " + i);
            for (int j = 0, size = QRRandomUtils.getRandomInt(2, 9); j < size; j++) {
                button.add(new QRMenuItem(String.format("Menu %s of Button %s", i, j)));
            }
        }
        this.mainPanel.add(new QRTextPane().addScrollPane());
    }

    public static void main(String[] args) {
        // 自定义配置文件名及其路径
        QRSwing.start("res/settings.properties", "res/window.properties");
        // 设置菜单置于窗体标题栏
        QRSwing.setWindowTitleMenu(true);
        // 取消窗体圆角
        QRSwing.setWindowRound(false);
        MenuTest window = new MenuTest("测试窗体");
        // 设置窗体背景图遮罩透明度
        window.setBackgroundBorderAlpha(0.8f);
        // 设置窗体背景图
        window.setBackgroundImage("res/picture/background_image.png");
        //设置窗体可见
        window.setVisible(true);
    }
}
```
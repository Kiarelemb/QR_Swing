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
        var dialog = new QRDialog(this);
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
        for (var i = 0; i < 4; i++) {
            // 添加菜单
            QRButton button = titleMenuPanel.add("Menu " + i);
            for (var j = 0, size = QRRandomUtils.getRandomInt(2, 9); j < size; j++) {
                // 添加子菜单
                button.add(new QRMenuItem(String.format("Menu %s of Button %s", i, j)));
            }
        }
        // 添加文本面板
        var textPane = new QRTextPane();
        // 将文本面板置于滚动条中，并将滚动条面板置于主面板中
        this.mainPanel.add(textPane.addScrollPane());
    }

    public static void main(String[] args) {
        // 自定义配置文件名及其路径
        QRSwing.start("res/settings.properties", "res/window.properties");
        // 设置菜单置于窗体标题栏
        QRSwing.setWindowTitleMenu(true);
        // 取消窗体圆角
        QRSwing.setWindowRound(false);
        QRFrame window = new MenuTest("测试窗体");
        // 设置窗体背景图遮罩透明度
        window.setBackgroundBorderAlpha(0.8f);
        // 设置窗体背景图
        window.setBackgroundImage("res/picture/background_image.png");
        //设置窗体可见
        window.setVisible(true);
    }
}
```

## 环境搭建
`QRSwing` 使用 `Java 17` 的运行环境，所以我们推荐使用 `Java 17`，甚至更新的 JDK 版本。

#### - 下载 JDK 并自行配置运行环境

依据您的系统，选择点击下方的名称，以安装 [Java 17 GA](https://jdk.java.net/archive/) ：  
> [Windows x64](https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_windows-x64_bin.zip)  
> [Mac OS x64](https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_macos-x64_bin.tar.gz)  
> [Linux x64](https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_linux-x64_bin.tar.gz)

对于 `Windows 10+` 系统的环境配置，我们强烈推荐您参考 [这个网站](https://www.runoob.com/w3cnote/windows10-java-setup.html)。
## 深入了解 QRSwing

### ① 优雅的事件监听器
我们以鼠标监听器为例。
#### 1. 外部操作
在以往的监听器中，我们通常需要添加 `MouseAdapter` 类来重写需要使用的方法，这会极大地增加代码的缩进量：

```java
import javax.swing.JPanel;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Test {
    public static void main(String[] args) {
        JPanel panel = new JPanel();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // do something...
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // do something...
            }
        });
    }
}
```
而 `QRSwing` 则提供了一种更优雅的方式：为控件调用添加监听器方法，再添加你需要使用的具体方法：
```java
package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.listener.QRMouseListener.TYPE;

public class Test {
    public static void main(String[] args) {
        QRPanel panel = new QRPanel();
        panel.addMouseListener();
        // 其中，参数 e 的类型是 java.awt.event.MouseEvent
        panel.addMouseAction(TYPE.PRESS, e -> {
            // do something...
        });
        panel.addMouseAction(TYPE.RELEASE, e -> {
            // do something...
        });
    }
}
```
#### 2. 继承操作

即使是继承了 `JPanel`，我们需要做的事情依然没有改变：
```java
import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PaneTest extends JPanel {
    public PaneTest() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // do something...
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                // do something...
            }
        });
    }
}
```

但在继承了 `QRSwing` 的 `QRPanel` 类之后，这番操作便简易多了：
```java
import swing.qr.kiarelemb.basic.QRPanel;
import java.awt.event.MouseEvent;

public class PaneTest extends QRPanel {
    public PaneTest() {
        addMouseListener();
    }

    @Override
    protected void mousePress(MouseEvent e) {
        // do something...
    }

    @Override
    protected void mouseRelease(MouseEvent e) {
        // do something...
    }
}
```
当然，你仍然可以使用外部添加事件的方法来实现你的需求，不过直接重写方法看起来也非常不错。也许你还不清楚我们所测试的这个 `QRPanel` 是什么，
实际它的本质也是 `JPanel`，而只是它已经帮你做了烦琐的操作。

### ② 便捷的主题切换
还记得最开始我们 `QRSwing` 中的 `start()` 方法吗？在该方法里，我们设置了整个程序的主题。在不进行任何配置的情况下，主题默认为 `深色` 。在 `QRColorsAndFonts` 类中，定义了内置的几个主题：
```java
public static final String[] BASIC_THEMES = {"深色", "浅色", "粉色", "棕色", "灰色"};
```
我们可以通过 `QRSwing.setTheme(String value)` 方法来设置主题，其中 `theme` 参数是主题的名称，例如 `"深色"` 、 `"浅色"` 、 `"粉色"` 、 `"棕色"` 、 `"灰色"` 。

在设置完主题后，我们还有一步需要操作，那就是调用主窗体的控件刷新方法：
```java
import swing.qr.kiarelemb.window.basic.QRFrame;
public class FrameTest {
    public FrameTest() {
        QRFrame mainWindow = new QRFrame();
        // 切换主题的代码...
        // 最后调用该方法以刷新
        mainWindow.componentFresh();
    }
}
```
但主题并不代表只有这些，我们也可以自定义主题。为此，我们提供了专门的主题设计器。虽然该主题设计器目前还在开发中，但它仍然可以满足大部分需求，并方便使用。
```java
import swing.qr.kiarelemb.theme.QRSwingThemeDesigner;
import swing.qr.kiarelemb.window.basic.QRFrame;

public class FrameTest {
    public FrameTest() {
        QRFrame mainWindow = new QRFrame();
        // 主窗体的操作代码....
        
        // 实例化主题设计器，并将主窗体传入
        QRSwingThemeDesigner designer = new QRSwingThemeDesigner(mainWindow);
        designer.setVisible(true);
    }
}
```
### ③ 高档的窗体背景图
正在更新中...
### ④ 拓展丰富的常用控件


#### 码量之最 —— QRTextPane

#### 巅覆重写 —— QRTabbedPane

#### 跨平台 —— QRMenuButton & QRPopupMenu

#### 蜻蜓点水 —— 自研控件 QRInternalScrollPane & QRTransparentSplitPanel

### ⑤ 自搞一套 —— 重定义窗体

#### 全局主窗体 QRFrame
#### 便利对话框 QRDialog
#### 其他工具窗 QRSmallTipShow & QROpinionDialog
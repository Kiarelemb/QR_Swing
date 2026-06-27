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
        window.setBackgroundImageAlpha(0.8f);
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
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.listener.QRMouseListener.TYPE;

public class Test {
    public static void main(String[] args) {
        QRPanel panel = new QRPanel();
        // 其中，参数 e 的类型是 java.awt.event.MouseEvent
        // 类外添加操作事件，已不再需要添加监听器
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
		// 类内重写方法需要手动添加监听器
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
但主题并不代表只有这些，我们也可以自定义主题。为此， `QRSwing` 提供了专门的主题设计器。虽然该主题设计器目前还在开发中，但它仍然满足大部分需求，且方便使用。
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
我们已经在最开始的 `走马观花` 中使用了窗体背景图的功能。在主窗体实例化之前，我们可以手动设置窗体背景图，以在其实例化时自动加载：
```java
// 设置背景图路径
QRSwing.setWindowBackgroundImagePath(String value);
// 设置背景图透明度
QRSwing.setWindowBackgroundImageAlpha(float value);
```
在主窗体实例化之后，我们也有办法来设置窗体背景图：
```java
QRFrame mainWindow = new QRFrame();
// 设置背景图路径
mainWindow.setBackgroundImage(String filePath);
// 设置背景图透明度
mainWindow.setBackgroundImageAlpha(float alpha);
mainWindow.setVisible(true);
```
### ④ 拓展丰富的常用控件

`QRSwing` 的基础控件并不是简单地换了一层皮。它们通常会把常用监听器、主题刷新、滚动条、字体、快捷键提示、文本操作等逻辑一起封装好。你可以继续把它们当作熟悉的 `Swing` 控件使用，也可以使用它们额外提供的能力。

#### 码量之最 —— QRTextPane
`QRTextPane` 是目前拓展最多的文本控件。它继承自 `JTextPane`，适合替代普通的 `JTextArea` 或 `JTextPane` 来承担主要文本编辑区域。它内置了主题字体、主题颜色、自定义光标、撤销管理器、选择结束事件、便捷打印文本、滚动面板等能力。

最简单的使用方式如下：
```java
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.window.basic.QRFrame;

import java.awt.BorderLayout;

public class TextPaneTest extends QRFrame {
    public TextPaneTest() {
        super("QRTextPane");
        setCloseButtonSystemExit();
        mainPanel.setLayout(new BorderLayout());

        QRTextPane textPane = new QRTextPane();
        textPane.addUndoManager();
        textPane.println("这是第一行。");
        textPane.println("这是第二行。");
		// 文本面板添加滚动面板也不需要再单独去实例化一个滚动面板，而是直接调用 addScrollPane() 方法
        mainPanel.add(textPane.addScrollPane(), BorderLayout.CENTER);
    }
}
```

如果你需要监听文本变化、光标变化或选择结束，也不必再手动创建一堆监听器：
```java
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.listener.QRDocumentListener;

QRTextPane textPane = new QRTextPane();

textPane.addDocumentListenerAction(QRDocumentListener.TYPE.INSERT, e -> {
    System.out.println("插入了文本");
});

textPane.addSelectionEndAction(e -> {
    System.out.println("当前选中的文本：" + textPane.getSelectedText());
});
```

`QRTextPane#println(...)` 和 `QRTextPane#print(...)` 也支持指定字体、前景色、背景色和插入位置，因此它可以很方便地用于日志面板、带颜色提示的输出面板、编辑器面板等场景。

#### 巅覆重写 —— QRTabbedPane
`QRTabbedPane` 不是直接继承 `JTabbedPane`，而是用 `QRPanel` 重新组织了标签栏和内容面板。这样做的好处是标签位置、标签样式、关闭按钮、主题刷新和自定义事件都更容易控制。

一个标签页由标题和 `QRTabbedContentPanel` 组成：
```java
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.combination.QRTabbedContentPanel;
import swing.qr.kiarelemb.combination.QRTabbedPane;

import java.awt.BorderLayout;

QRTabbedPane tabbedPane = new QRTabbedPane(BorderLayout.NORTH);
tabbedPane.addTabCloseButton();

QRTabbedContentPanel first = new QRTabbedContentPanel(new BorderLayout());
first.add(new QRLabel("第一页内容"), BorderLayout.CENTER);

QRTabbedContentPanel second = new QRTabbedContentPanel(new BorderLayout());
second.add(new QRLabel("第二页内容"), BorderLayout.CENTER);

tabbedPane.addTab("第一页", first);
tabbedPane.addTab("第二页", second);
tabbedPane.setSelectedTab(0);

tabbedPane.addTabSelectChangedAction(e -> {
    System.out.println("当前标签索引：" + tabbedPane.getSelectedTabIndex());
});
```

当标签很多、标题较长，或你更希望用列表来做导航时，也可以使用 `QRListTabbedPane`。它把左侧列表和右侧内容区域组合在一起，适合设置页、工具页、文档目录等结构：
```java
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRList;
import swing.qr.kiarelemb.combination.QRListTabbedPane;
import swing.qr.kiarelemb.combination.QRTabbedContentPanel;

import java.awt.BorderLayout;

QRTabbedContentPanel normal = new QRTabbedContentPanel(new BorderLayout());
normal.add(new QRLabel("常规设置"), BorderLayout.CENTER);

QRTabbedContentPanel advanced = new QRTabbedContentPanel(new BorderLayout());
advanced.add(new QRLabel("高级设置"), BorderLayout.CENTER);

QRList list = new QRList(new String[]{"常规", "高级"});
QRListTabbedPane pane = new QRListTabbedPane(list, new QRTabbedContentPanel[]{normal, advanced});
pane.setSelectedTab(0);
```

#### 跨平台 —— QRMenuButton & QRPopupMenu
`QRFrame` 的标题栏菜单使用的是 `QRMenuButton` 和 `QRPopupMenu`。普通的 `JPopupMenu` 在不同系统、不同窗口透明与圆角设置下，可能表现不完全一致；`QRPopupMenu` 则直接基于 `QREmptyDialog` 实现，因此它能更好地配合 `QRSwing` 自己的窗体、主题、透明度和圆角。

在 `QRFrame` 中，通常不需要自己实例化 `QRMenuButton`，直接通过 `titleMenuPanel.add(...)` 添加即可：
```java
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.window.basic.QRFrame;

public class MenuFrame extends QRFrame {
    public MenuFrame() {
        super("菜单示例");
        setCloseButtonSystemExit();

        QRButton file = titleMenuPanel.add("文件");
        QRMenuItem open = new QRMenuItem("打开");
        QRMenuItem save = new QRMenuItem("保存");

        open.addClickAction(e -> System.out.println("打开"));
        save.addClickAction(e -> System.out.println("保存"));

        file.add(open);
        file.add(save);
    }
}
```

`QRMenuItem` 会按文本和快捷键提示自动计算宽度。如果你的菜单项绑定了快捷键，显示效果会比手动拼接字符串更稳定。

#### 蜻蜓点水 —— 自研控件 QRInternalScrollPane & QRTransparentSplitPanel
`QRInternalScrollPane` 是早期为透明背景和自绘滚动条准备的内部滚动面板。它已经被标记为 `@Deprecated`，新代码更推荐优先使用普通的 `addScrollPane()`。不过如果你维护旧项目，仍可能见到这样的写法：
```java
import swing.qr.kiarelemb.basic.QRTextPane;

QRTextPane textPane = new QRTextPane();
textPane.setLineWrap(true);
// 新项目优先使用：
textPane.addScrollPane();
// 旧项目中可能会见到：
textPane.addInternalScrollPane();
```

`QRTransparentSplitPane` 则是一个轻量的上下分割面板，分隔条使用透明绘制，适合与窗体背景图一起使用：
```java
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.combination.QRTransparentSplitPane;

QRTransparentSplitPane splitPane = new QRTransparentSplitPane();
splitPane.setTopComponent(new QRTextPane().addScrollPane());
splitPane.setBottomComponent(new QRTextPane().addScrollPane());
splitPane.setDividerLocation(180);
```

这类控件的定位不是替代所有 `Swing` 原生组件，而是在 `QRSwing` 的主题、透明窗体、背景图和自定义事件体系中，提供更协调的默认行为。

### ⑤ 自搞一套 —— 重定义窗体
`QRSwing` 对窗体的封装比普通控件更激进。`QRFrame`、`QRDialog` 和 `QREmptyDialog` 都使用了无边框窗体，并自行实现标题栏、移动、缩放、关闭按钮、主题刷新、圆角、透明度、父子窗体联动等行为。这样做的目的，是让整个程序的窗体和控件能处在同一套视觉体系里，而不是一半来自系统窗口，一半来自自定义控件。

需要注意的是，既然窗体被重新定义，部分方法的语义也会与原生 `JFrame`、`JDialog` 有所不同。例如 `QRFrame#dispose()` 被用于保存窗体大小与位置配置；如果你希望关闭主窗体并退出程序，通常应使用 `setCloseButtonSystemExit()`，或主动调用 `dispose(true)`。

#### 全局主窗体 QRFrame
`QRFrame` 是最常用的主窗体。它继承自 `JFrame`，但内部已经准备好了标题栏、主面板、标题菜单面板、最小化按钮、最大化按钮、关闭按钮、窗口配置保存、主题刷新和背景图绘制。

一个标准主窗体通常会这样写：
```java
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRTextPane;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;

public class MainWindow extends QRFrame {
    public static void main(String[] args) {
        QRSwing.start();
        QRSwing.registerGlobalKeyEvents();

        MainWindow window = new MainWindow();
        QRSwing.registerGlobalEventWindow(window);
        window.setVisible(true);
    }

    public MainWindow() {
        super("主窗体");
        setTitlePlace(SwingConstants.CENTER);
        setCloseButtonSystemExit();
        mainPanel.setLayout(new BorderLayout());

        QRTextPane textPane = new QRTextPane();
        textPane.println("主面板 mainPanel 可以像普通 Swing 容器一样添加控件。");
        mainPanel.add(textPane.addScrollPane(), BorderLayout.CENTER);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        System.out.println("窗体已打开");
    }
}
```

`QRFrame` 中最重要的几个区域是：

- `mainPanel`：主内容区，通常你的业务控件都放在这里。
- `titleMenuPanel`：标题栏菜单区，在 `QRSwing.setWindowTitleMenu(true)` 时会出现在标题栏中。
- `topPanel`：窗体顶部区域，包含标题栏和可能存在的菜单栏。

常见窗体控制方法如下：
```java
QRFrame frame = new QRFrame("示例窗体");

// 标题与关闭
frame.setTitleCenter();
frame.setCloseButtonSystemExit();

// 最大化、最小化按钮是否可用
frame.setMaxEnable(true);
frame.setMinEnable(true);

// 关闭前执行动作。参数为 true 时，表示本次关闭希望退出程序
frame.addActionBeforeDispose(systemExit -> {
    System.out.println("准备关闭，是否退出程序：" + systemExit);
});

// 背景图
frame.setBackgroundImage("res/picture/background_image.png");
frame.setBackgroundImageAlpha(0.8f);
frame.setBackgroundImageScale(true);
```

如果你希望手动添加标题菜单栏，也可以在未启用全局标题栏菜单时调用 `setTitlePanel()`：
```java
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRMenuItem;
import swing.qr.kiarelemb.window.basic.QRFrame;

QRSwing.setWindowTitleMenu(false);

public class MenuWindow extends QRFrame {
    public MenuWindow() {
        super("菜单窗体");
		// 手动添加标题菜单栏
        setTitlePanel();

        QRButton file = titleMenuPanel.add("文件");
        file.add(new QRMenuItem("打开"));
        file.add(new QRMenuItem("保存"));
    }
}
```

如果希望窗体状态在关闭时保存，请不要绕过 `QRFrame` 的关闭流程。`QRFrame#dispose()` 会写入窗口配置，`dispose(true)` 则会在保存配置后执行淡出关闭，并按参数决定是否退出程序。

#### 便利对话框 QRDialog
`QRDialog` 是带标题栏的对话框，继承自 `JDialog`。它默认是无边框、自绘标题栏、可拖动、不可调整大小，并且会在显示时注册 `ESC` 关闭行为。构造方法中的 `parent` 会作为父窗体；默认构造 `new QRDialog(parent)` 会禁用父窗体，适合模态操作。

一个普通确认对话框可以这样写：
```java
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.window.basic.QRDialog;

import java.awt.Window;
import java.awt.event.ActionEvent;

public class ConfirmDialog extends QRDialog {
    public ConfirmDialog(Window parent) {
        super(parent);
        setTitle("确认操作");
        setTitlePlace(QRDialog.CENTER);
        setSize(360, 180);

        QRLabel label = new QRLabel("是否继续执行？");
        label.setBounds(105, 35, 160, 30);
        mainPanel.add(label);

        QRRoundButton button = new QRRoundButton("确定");
        button.setBounds(145, 90, 70, 32);
        button.addClickAction(this::sureAction);
        mainPanel.add(button);
    }

    protected void sureAction(ActionEvent e) {
        dispose();
    }
}
```

`QRDialog` 的核心面板同样是 `mainPanel`。它默认使用 `null` 布局，因此示例中使用了 `setBounds(...)`；如果你更习惯布局管理器，也可以直接改掉布局：
```java
import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.window.basic.QRDialog;

import java.awt.BorderLayout;
import java.awt.Window;

public class TipDialog extends QRDialog {
    public TipDialog(Window parent) {
        super(parent);
        setTitle("提示");
        setSize(420, 220);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new QRLabel("使用布局管理器也没有问题。"), BorderLayout.CENTER);
    }
}
```

如果你希望对话框打开后不跟随父窗体移动，可以调用：
```java
dialog.setParentWindowNotFollowMove();
```

如果你希望创建一个不禁用父窗体的对话框，则使用第二个构造参数：
```java
QRDialog dialog = new QRDialog(parentWindow, false);
```

#### 其他工具窗 QRSmallTipShow & QROpinionDialog
除了基础窗体，`QRSwing` 还提供了一些可以直接使用的工具窗。

`QRSmallTipShow` 适合做轻量提示。它会显示在父窗体中央，并在指定时间后自动关闭：
```java
import swing.qr.kiarelemb.window.enhance.QRSmallTipShow;

// 默认 500 毫秒后关闭
QRSmallTipShow.display(parentWindow, "保存成功");

// 指定显示时间，单位为毫秒
QRSmallTipShow.display(parentWindow, "正在处理...", 1500);

// 没有父窗体时，也可以居中显示在屏幕上
QRSmallTipShow.display("操作完成", 1000);
```

如果你需要手动控制提示窗的关闭，可以获取实例：
```java
QRSmallTipShow tip = QRSmallTipShow.getInstance(parentWindow, "请稍候");
tip.setAutoCloseFalse();
tip.setVisible(true);

// 任务结束后关闭
tip.dispose();
```

`QROpinionDialog` 则是内置的消息、询问、错误提示对话框：
```java
import swing.qr.kiarelemb.window.enhance.QROpinionDialog;

// 普通消息
QROpinionDialog.messageTellShow(parentWindow, "操作已完成。");

// 错误消息
QROpinionDialog.messageErrShow(parentWindow, "文件读取失败。");

// 确认消息，返回 QROpinionDialog.OK 或 QROpinionDialog.CANCEL
int result = QROpinionDialog.messageInfoShow(parentWindow, "是否覆盖已有文件？");
if (result == QROpinionDialog.OK) {
    System.out.println("用户选择了确定");
}
```

这些工具窗的价值在于“够用且统一”：它们会沿用 `QRSwing` 的主题、字体、图标、圆角和透明度设置，不需要你每次都从 `JDialog` 开始重新拼装。
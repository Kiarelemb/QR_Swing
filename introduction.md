# QR_Swing 项目介绍

本文档基于当前源码阅读整理，目的是让后续 AI 或维护者快速理解本项目的定位、结构和重要实现约束。

## 项目定位

QR_Swing 是一个 Java Swing 扩展控件包，面向 Java 17 运行环境。项目不是一个单一业务应用，而是作者从桌面项目中抽象出的 Swing UI 基础库，提供自定义窗口、基础控件增强、组合控件、统一事件监听器、主题系统、资源加载、文件选择器、进度对话框和若干测试示例。

项目包名主体为 `swing.qr.kiarelemb`。根目录存在 `QR_Swing.jar`、`lib/`、`setting.properties`、`window.properties` 等运行和配置文件，源码位于 `src/swing/qr/kiarelemb`。当前源码约 164 个 Java 文件。

## 启动入口

`QRSwing` 是项目级入口类，使用方式通常是先调用：

```java
QRSwing.start();
```

或指定配置文件：

```java
QRSwing.start("settings.properties", "window.properties");
```

`QRSwing.start` 的主要职责：

- 加载全局配置 `GLOBAL_PROP`。
- 加载窗口配置文件路径 `WINDOW_PROP_PATH`。
- 初始化系统外观和透明窗口相关属性。
- 加载主题 `QRColorsAndFonts.loadTheme()`。
- 创建临时目录 `tmp/`。
- 注册 JVM shutdown hook，在退出时执行 `ACTION_AFTER_CLOSE` 并保存全局配置。
- 管理窗口圆角、透明度、背景图、标题栏菜单、窗口吸附、置顶等全局开关。
- 可选注册全局键盘事件，依赖 `jnativehook`。

## 主要模块

### `basic`

基础控件增强层，常见类包括：

- `QRPanel`
- `QRButton`
- `QRRoundButton`
- `QRLabel`
- `QRTextPane`
- `QRTextField`
- `QRTextArea`
- `QRTable`
- `QRTree`
- `QRList`
- `QRComboBox`
- `QRScrollPane`
- `QRProgressBar`

这些类通常在 Swing 原生组件基础上增加主题刷新、快捷监听器注册、绘制定制、滚动优化、文本操作或统一外观。

需要注意：`QRLabel` 中有图片读取、圆角处理和头像裁切逻辑，使用 `ImageIO.read/write` 与临时文件。若这些方法被 UI 事件直接触发，容易阻塞 EDT。

### `window.basic`

窗口基类：

- `QRFrame`：主窗口基类，继承 `JFrame`，自绘标题栏、最小化/最大化/关闭按钮、主面板 `mainPanel`、背景图、窗口位置记忆、圆角/透明度、子窗口跟随移动、窗口事件注册。
- `QRDialog`：基础对话框，继承 `JDialog`，提供自定义标题栏、关闭按钮、ESC 关闭、可选缩放、窗口事件注册。
- `QREmptyDialog`：更轻量的无标题空白对话框，用于提示、进度等小窗口。

窗口类普遍使用项目自定义监听器体系，将 Swing 原生事件转发到可重写方法或 `QRActionRegister` 回调。

### `window.utils`

工具窗口：

- `QRFileSelectDialog`：项目自研文件选择对话框，支持打开文件、选择目录、文件/目录、保存文件等模式。它会读取磁盘根目录、列目录、排序、读取系统图标并构建目录树。
- `QRProgressDialog`：用于显示任务进度的小型对话框，包含描述、细长进度条、百分比和取消按钮。当前只负责显示和取消回调，不直接拥有后台任务生命周期。
- `QRPicturePreviewDialog`
- `QRResizableTextShowDialog`
- `QRValueInputDialog`

### `theme`

主题系统主要在 `QRColorsAndFonts` 和 `QRSwingThemeDesigner` 中。

`QRColorsAndFonts` 管理默认字体、主题颜色、内置主题和自定义主题文件。主题文件存放在 `theme/` 目录，扩展名为 `.qr.th`，读取时会遍历主题目录、读取文件、解密并解析颜色。

### `listener`、`inter.listener`、`event`

项目大量封装 Swing 事件监听器：

- `QRMouseListener`
- `QRKeyListener`
- `QRDocumentListener`
- `QRWindowListener`
- `QRTabCloseListener`
- `QRTabSelectChangedListener`
- `QRNativeKeyListener`

`inter.listener.add` 中的接口用于让组件以统一方法添加事件，例如 `addMouseAction`、`addKeyListenerAction` 等。事件对象放在 `event` 包中。

整体设计倾向于减少匿名内部类和 `MouseAdapter` 模板代码，让业务或测试代码直接注册具体类型的回调。

### `combination`

组合控件层：

- `QRTabbedPane`
- `QRTabbedContentPanel`
- `QRTreeTabbedPane`
- `QRListTabbedPane`
- `QRMenuPanel`
- `QRMenuButton`
- `QRPopupMenu`
- `QRContractiblePanel`
- `QRTransparentSplitPane`
- `QRStatePanel`

这些控件主要用于把基础控件组合为更完整的 UI 模块，例如自定义 tab、菜单、弹出项、可收缩面板等。

### `utils`

项目工具控件和工具方法集中在此包：

- `QRComponentUtils`：布局辅助、绘制文本、批量运行回调、延迟执行和 EDT 调度。
- `QRFileSelectButton`、`QRFileSelectRoundButton`：触发文件选择器并回调成功/失败。
- `QRClearableTextField`
- `QRFontComboBox`
- `QRImagePanel`
- `QRPicturePreviewPanel`
- `QRRGBColorPane`

`QRComponentUtils.runOnEdt(Runnable)` 是当前线程边界的基础工具。`QRProgressDialog.setProgress` 和 `setProgressDescription` 会通过它调度界面更新。

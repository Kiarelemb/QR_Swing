# QRSwing 全局快捷键焦点窗体参数方案

## 背景

当前 `QRSwing` 的全局快捷键入口主要通过 `boolean mainWindowFocus` 区分两类事件：

- `true`：只有已注册的主窗体处于焦点时触发。
- `false`：不判断 QR Swing 窗体焦点，作为系统级全局快捷键触发。

这个设计能避免主窗体最小化或失焦后继续响应主窗体快捷键，但它把“需要某个窗口在焦点”固定成了“需要主窗体在焦点”。当快捷键注册在子窗体、对话框或工具窗口中时，实际焦点会落在子窗体上；如果继续传 `mainWindowFocus=true`，主窗体不在焦点，快捷键不会触发；如果传 `false`，又会变成不受窗口焦点限制的全局快捷键，调用方只能在 action 内部再手动判断窗口焦点。

这个问题在 `QRDialog`、`QROpinionDialog` 一类组件中已有绕行写法：注册为 `mainWindowFocus=false`，然后在 action 内部判断 `dialog.isFocused()`。这说明现有 API 已经不能准确表达“只在这个子窗体获得焦点时触发”。

## 目标

给全局快捷键注册、移除、外部触发方法增加可选的 `Window focusWindow` 作用域参数，让调用方能指定焦点判断窗口：

- 主窗体快捷键仍然可以依赖主窗体焦点。
- 子窗体快捷键可以依赖子窗体焦点。
- 不限制焦点的系统级快捷键继续存在。
- 旧的 `boolean mainWindowFocus` 调用保持源码兼容和行为兼容。

## 建议语义

把现有二元语义升级成三类焦点作用域：

| 作用域 | 建议表达 | 触发条件 |
| --- | --- | --- |
| 全局 | `focusWindow == null` | 不判断 QR Swing 窗口焦点 |
| 主窗体焦点 | 旧 API `mainWindowFocus=true` | 已注册主窗体处于焦点 |
| 指定窗体焦点 | 新 API 传入 `Window focusWindow` | 指定 `Window` 处于焦点 |

旧 API 的转发规则：

```java
registerGlobalAction(keyStroke, ar, true);
// 等价于注册到当前 mainWindow 焦点作用域

registerGlobalAction(keyStroke, ar, false);
// 等价于 registerGlobalAction(keyStroke, ar, (Window) null)
```

这样可以保留老调用点的行为，同时给新调用点一个更精确的入口。

## API 设计建议

在 `QRSwing` 中新增同名重载，而不是替换原签名：

```java
public static void registerGlobalAction(String key, QRActionRegister<KeyStroke> ar, Window focusWindow)
public static void registerGlobalAction(int keycode, QRActionRegister<KeyStroke> ar, Window focusWindow)
public static void registerGlobalAction(int keycode, int modifiers, QRActionRegister<KeyStroke> ar, Window focusWindow)
public static void registerGlobalAction(KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, Window focusWindow)
```

移除方法也需要同样重载，否则无法准确移除指定窗体作用域下的快捷键：

```java
public static void registerGlobalActionRemove(KeyStroke keyStroke, Window focusWindow)
public static void registerGlobalActionRemove(KeyStroke keyStroke, QRActionRegister<KeyStroke> ar, Window focusWindow)
public static void registerGlobalActionRemove(String key, Window focusWindow)
public static void registerGlobalActionRemove(String key, QRActionRegister<KeyStroke> ar, Window focusWindow)
public static void registerGlobalActionRemove(int keycode, Window focusWindow)
public static void registerGlobalActionRemove(int keycode, QRActionRegister<KeyStroke> ar, Window focusWindow)
public static void registerGlobalActionRemove(int keycode, int modifiers, Window focusWindow)
public static void registerGlobalActionRemove(int keycode, int modifiers, QRActionRegister<KeyStroke> ar, Window focusWindow)
```

外部触发方法建议也补齐：

```java
public static void invokeAction(Window focusWindow, KeyStroke keyStroke)
```

但这里需要避免和旧方法 `invokeAction(Window window, KeyStroke keyStroke, boolean mainWindowFocus)` 混淆。更清晰的做法是新增：

```java
public static void invokeActionByFocusWindow(Window focusWindow, KeyStroke keyStroke)
```

如果希望 API 更一致，也可以保留 `invokeAction(Window, KeyStroke)`，文档中明确参数是“焦点判断窗体”，不是事件来源窗体。

## 底层结构建议

`QRNativeKeyListener.KeyEvents` 当前有两套容器：

```java
GLOBAL_KEY_EVENTS
FOCUS_KEY_EVENTS
globalEventList
focusEventList
mainWindow
```

建议改成按 `Window` 分组的作用域容器：

```java
private final Map<KeyStroke, ArrayList<QRActionRegister<KeyStroke>>> globalKeyEvents;
private final Map<Window, Map<KeyStroke, ArrayList<QRActionRegister<KeyStroke>>>> windowKeyEvents;

private final LinkedList<QRActionRegister<QRNativeKeyEvent>> globalEventList;
private final Map<Window, LinkedList<QRActionRegister<QRNativeKeyEvent>>> windowEventLists;

private Window mainWindow;
```

其中：

- `globalKeyEvents` 保存不限制焦点的快捷键。
- `windowKeyEvents` 保存指定窗口焦点作用域的快捷键。
- 旧的 `mainWindowFocus=true` 注册时，内部转发为 `focusWindow = mainWindow`。
- 如果旧 API 在主窗体尚未注册前调用，则不能直接把事件放进 `windowKeyEvents` 的 `null` 组，否则会和全局快捷键混淆。

为了解决“先注册快捷键、后注册主窗体”的现有调用顺序，建议保留一个主窗体待绑定作用域：

```java
private final Map<KeyStroke, ArrayList<QRActionRegister<KeyStroke>>> mainWindowKeyEvents;
private final LinkedList<QRActionRegister<QRNativeKeyEvent>> mainWindowEventList;
```

旧 API `mainWindowFocus=true` 仍进入 `mainWindowKeyEvents`；新 API `focusWindow != null` 进入 `windowKeyEvents`。触发时先判断 `mainWindow`，再遍历指定窗口作用域。这样旧行为最稳，不依赖注册顺序。

## 触发流程建议

收到 native key event 后：

1. 解析 `KeyStroke`。
2. 若 `mainWindow != null && mainWindow.isFocused()`，执行主窗体焦点作用域事件。
3. 遍历 `windowKeyEvents`，对 `window != null && window.isFocused()` 的窗口执行对应事件。
4. 执行不限制焦点的全局事件。

这里建议维持当前优先级：焦点作用域事件先执行，全局事件后执行。这样现有行为不会反转。

需要注意一点：Swing 的 `Window.isFocused()` 只在该 `Window` 或其 owned window 成为 focused window 时返回 `true`。如果希望“子控件有焦点也算窗口有焦点”，通常是符合预期的；如果未来遇到 owned dialog、popup 或嵌套窗口边界问题，可以考虑改用：

```java
KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow()
```

然后判断 focused window 是否等于指定窗口，或是否属于指定窗口的 owned window 链。但第一版不建议扩大语义，先沿用现有 `isFocused()` 行为，风险更小。

## 对现有调用点的影响

应保持不变的调用点：

- `QRSwing.registerGlobalAction(..., true)`：仍表示主窗体焦点。
- `QRSwing.registerGlobalAction(..., false)`：仍表示不限制焦点。
- `QRSwing.registerGlobalActionRemove(..., true/false)`：仍从对应旧作用域移除。
- `QRSwing.registerGlobalEventWindow(window)`：仍只设置旧 API 的主窗体。

可以改进的调用点：

- `QRDialog` 的 ESC 关闭可以注册到 `this`，不再用 `false + action 内部 isFocused()`。
- `QROpinionDialog` 的 ENTER/ESC 可以注册到 `this`。
- `QRValueInputDialog` 当前使用 `true`，如果它是子窗体，应改成传自身窗口，否则依旧依赖主窗体焦点。
- `QRFileSelectDialog` 的 F5 如果只应在文件选择对话框焦点内生效，应改成传自身窗口；如果确实想全局刷新，则保留 `false`。
- `QRPicturePanel` 的缩放快捷键如果是面板级行为，单纯传 `Window` 仍不够精确，因为面板不等于窗口；这类可以先保持现状，后续另做 `Component` 作用域。

## 不建议这次一起做的事

不建议把参数直接改成 `Component`。虽然它能覆盖面板级快捷键，但焦点判断会变成 `component.isFocusOwner()`、`isAncestorOf(focusOwner)`、`SwingUtilities.getWindowAncestor(component)` 等多种语义选择，容易把这次问题扩大。不能将控件作为是否在焦点的判断条件，这样会使得焦点判断语义更复杂。

不建议删除 `boolean mainWindowFocus`。这是框架入口类，现有项目和测试代码已经广泛使用这个布尔参数，删除会造成不必要的迁移成本。

不建议把 `mainWindowFocus=false` 改成“当前 active window”。这会破坏原本“系统级全局快捷键”的语义，也会让最小化/失焦保护重新变得不清晰。

## 回归验证建议

落地后至少验证以下场景：

1. 旧主窗体快捷键：`registerGlobalAction(..., true)` 在主窗体焦点内触发，主窗体失焦或最小化时不触发。
2. 旧全局快捷键：`registerGlobalAction(..., false)` 在主窗体失焦时仍触发。
3. 新子窗体快捷键：`registerGlobalAction(..., dialog)` 在 dialog 焦点内触发，dialog 关闭或失焦后不触发。
4. 移除指定 action：同一个 `KeyStroke`、同一个 action 分别注册到主窗体和 dialog，移除 dialog 作用域不影响主窗体作用域。
5. 清空指定快捷键：清空 dialog 作用域下某个快捷键，不清空全局或主窗体作用域。
6. 注册顺序：先调用旧 API 注册 `mainWindowFocus=true`，后调用 `registerGlobalEventWindow(mainWindow)`，行为仍和现在一致。

## 我的结论

这个方向可行，但我建议把“新增 `Window focusWindow` 参数”理解为新增一个焦点作用域，而不是把旧 `mainWindowFocus` 简单替换成 `Window`。原因是旧 API 存在“主窗体尚未创建时先注册快捷键”的合法顺序，直接替换底层结构容易把旧的主窗体待绑定事件误归为全局事件。

最稳的落地路径是：

1. 保留所有旧布尔签名。
2. 新增 `Window focusWindow` 重载。
3. 底层同时保留主窗体待绑定作用域、指定窗体作用域、全局作用域。
4. 先只迁移明确属于子窗体的内部调用点。
5. 再根据实际需要决定是否继续设计 `Component` 级作用域。
6. 我已在 QRSwing 的同包之下新建了一个 QRGlobalAction 的 final 类。将 QRSwing 包里与事件相关的代码标注为过期，并将具体配置代码移到 QRGlobalAction 中，而现 QRSwing 只是作为一个中转站。旧 API 仍然可用，但已过时。所有与事件相关的方法、字段、属性都移到 QRGlobalAction 中，并添加 @Deprecated 注解。

## 2026-07-06 新想法：实例化全局事件类

新的想法是：不再只靠一组静态 `register/remove` 方法让调用方自己记住“注册时传了什么参数”，而是把一次全局快捷键注册抽象成一个实例。实例创建后通过 `load()` 注册，通过 `close()` 注销。

这个方向是值得做的，而且比继续增加静态移除重载更适合长期维护。原因是现在的移除 API 必须同时匹配：

- `KeyStroke`
- `QRActionRegister<KeyStroke>`
- `boolean mainWindowFocus` 或 `Window focusWindow`
- 未来可能还有 `TYPE`

调用方只要移除时少传一个参数，或传错焦点作用域，就会留下残余快捷键。实例化后，注册参数被对象自己保存，调用方只需要持有这个对象并关闭它，错误面会小很多。

建议把这个实例类定义为“注册句柄”，而不是“新的全局监听器”。也就是说：

- `QRGlobalAction` 继续负责 native hook 的安装、共享监听器和底层事件容器。
- 新实例只负责把一条或一组快捷键注册到 `QRGlobalAction`，并在 `close()` 时按原参数移除。
- 不要每实例化一个事件类就创建一个 `QRNativeKeyListener` 或调用一次 `GlobalScreen.registerNativeHook()`。

如果希望 API 更集中，可以不另起 `QRGlobalKeyBinding` 这类新名字，而是让 `QRGlobalAction` 本身同时承担两个角色：

- 静态兼容入口：保留当前 `registerGlobalAction/removeGlobalAction` 等方法，旧代码继续可用。
- 实例化注册对象：通过构造器接收 action，通过链式方法补齐快捷键、窗体和焦点语义，再用 `load()` / `close()` 管生命周期。

这个命名更符合用户直觉：`QRGlobalAction` 表示“一条可注册、可注销的全局动作”，静态方法只是它的兼容工具入口。实现上需要把当前 `private QRGlobalAction()` 改成公开构造器，同时保留静态字段和静态方法。

`AutoCloseable` 仍然适合这个类，因为调用方可以在窗口关闭、对话框隐藏、组件销毁时直接调用 `close()`，语义清晰。

## 建议 API 形态

第一版建议只覆盖最常用、最容易出错的快捷键注册，不急着把所有底层能力都包装进去。推荐形态可以是：

```java
QRGlobalAction action = new QRGlobalAction(keyStroke -> save())
        .key("ctrl s")
        .window(this)
        .focus(true);

action.load();
```

对话框快捷键可以这样写：

```java
private final QRGlobalAction escAction = new QRGlobalAction(keyStroke -> dispose())
        .key(KeyEvent.VK_ESCAPE)
        .window(this)
        .focus(true);
```

当窗口显示或初始化完成时：

```java
escAction.load();
```

当窗口隐藏或释放时：

```java
escAction.close();
```

这里 `.window(this).focus(true)` 建议解释为“只有指定 window 处于焦点时触发”。也就是说，一旦调用方显式传了 `window`，就优先使用新的指定窗体作用域；不再回退到旧的主窗体作用域。

系统级全局快捷键可以写成：

```java
QRGlobalAction action = new QRGlobalAction(keyStroke -> showHelp())
        .key("ctrl alt h")
        .focus(false);
```

此时没有设置 `window`，并且 `focus(false)` 表示不限制窗体焦点。

如果确实要表达旧的“主窗体焦点”语义，可以有两种选择：

```java
new QRGlobalAction(keyStroke -> save())
        .key("ctrl s")
        .focus(true);
```

或者提供一个更明确的链式方法：

```java
new QRGlobalAction(keyStroke -> save())
        .key("ctrl s")
        .mainWindowFocus();
```

我更建议保留 `.focus(true)` 对旧主窗体语义的兼容：未设置 `window` 时，`focus(true)` 走 `mainWindowFocus=true`；已设置 `window` 时，`focus(true)` 走指定窗体焦点。这样既符合你给出的链式调用示例，也能保留“先注册主窗体快捷键，后绑定主窗体”的能力。

为了减少歧义，文档和 Javadoc 中要明确这条规则：

| 链式配置 | 注册语义 |
| --- | --- |
| `.focus(false)` | 系统级全局快捷键，不判断窗体焦点 |
| `.window(window).focus(true)` | 指定窗体焦点快捷键 |
| `.focus(true)` 且未设置 window | 旧的主窗体焦点快捷键 |
| `.window(window)` 未显式 focus | 建议默认等价于 `.focus(true)` |

这个默认值很重要：调用方只要写 `.window(this)`，通常就是想绑定到这个窗体焦点，不应再额外强制写 `.focus(true)`。

## load 与 close 的行为建议

`load()` 和 `close()` 必须设计成幂等方法：

- 多次 `load()` 只注册一次。
- 多次 `close()` 只移除一次，不抛异常。
- `close()` 后允许再次 `load()`，这样对话框反复显示/隐藏时可以复用同一个实例。

内部可以保留一个状态：

```java
private boolean loaded;
```

`load()`：

```java
if (loaded) {
    return;
}
register();
loaded = true;
```

`close()`：

```java
if (!loaded) {
    return;
}
unregister();
loaded = false;
```

其中 `register()` / `unregister()` 内部按实例当前配置选择静态注册或移除方法：

- 如果有 `focusWindow`，调用 `QRGlobalAction.registerGlobalAction(keyStroke, action, focusWindow)`。
- 如果没有 `focusWindow`，调用 `QRGlobalAction.registerGlobalAction(keyStroke, action, focus)`。
- `close()` 使用同一套参数调用对应的 `removeGlobalAction`。

如果一个实例支持多个快捷键，例如 `"ctrl s, meta s"`，构造或 `key(...)` 时就应解析成 `List<KeyStroke>`，`load()` 和 `close()` 遍历同一份列表。不要在 `close()` 时重新解析字符串，避免解析规则变化或字符串被改动导致移除不干净。

建议限制：已 `load()` 的实例不允许再修改 `key/window/focus/action`。否则会出现“按新参数关闭旧注册”的风险。可以选择：

- 链式 setter 在 `loaded == true` 时抛出 `IllegalStateException`。
- 或者自动先 `close()` 再修改，但这类隐式副作用不够直观。

第一版建议抛异常，规则更清楚。

## 与当前方案的关系

这不是对昨天方案的推翻，而是补在昨天方案之上的更高层 API：

- 底层仍然需要 `globalKeyEvents`、`mainWindowKeyEvents`、`windowKeyEvents` 三类容器。
- `Window focusWindow` 重载仍然需要保留，因为实例类最终也要调用它们。
- `QRSwing` 中已过时的静态转发方法可以继续存在，保证旧项目不受影响。
- 新代码优先推荐实例化 `QRGlobalAction`，不再推荐从 `QRSwing` 注册全局事件。

也就是说，底层解决“事件应该放在哪个作用域”，实例句柄解决“调用方如何可靠注销”。

## 使用场景建议

对话框类最适合先迁移到实例句柄，例如：

- `QRDialog` 的 ESC 关闭。
- `QROpinionDialog` 的 ENTER/ESC。
- `QRValueInputDialog` 的 ENTER/ESC。
- `QRFileSelectDialog` 的 F5 刷新，如果语义确定是对话框内刷新。

这些场景都有明显生命周期：窗口显示时 `load()`，窗口隐藏或释放时 `close()`。用实例化 `QRGlobalAction` 后，不需要每个类都手写成对的注册参数和移除参数。

对于主窗体长期存在的快捷键，可以继续用静态注册，也可以用实例句柄保存到主窗体字段中。两者都可以，但新代码建议统一成字段：

```java
private final QRGlobalAction saveAction = new QRGlobalAction(keyStroke -> save())
        .key("ctrl s")
        .focus(true);
```

然后在主窗体初始化完成后 `saveAction.load()`，在关闭前 `saveAction.close()`。

## 需要避免的问题

不要把 `load()` 设计成构造器自动调用。构造器只保存 action，链式方法只保存配置，`load()` 才产生副作用。这样测试、字段初始化和窗口生命周期都会更清楚。

不要让 `close()` 调用 `GlobalScreen.unregisterNativeHook()`。单个快捷键实例关闭，只能移除自己的 action；native hook 是整个应用共享资源，应该仍由 `QRGlobalAction` 统一管理。

不要在实例中强引用大量临时窗口后长期不关闭。`windowKeyEvents` 当前按 `Window` 分组，忘记 `close()` 会保留窗口引用。实例化 `QRGlobalAction` 能降低这个问题，但不能替代生命周期管理。后续如果希望进一步防泄漏，可以再考虑 `WeakHashMap<Window, ...>`，但第一版不建议同时引入弱引用语义，先把显式 `close()` 做扎实。

## 调整后的推荐落地路径

1. 保留现有 `QRGlobalAction` 静态入口和 `Window focusWindow` 重载。
2. 将 `QRGlobalAction` 从纯工具类调整为可实例化类，新增 `public QRGlobalAction(QRActionRegister<KeyStroke> action)` 构造器。
3. 增加链式方法：`key(String)`、`key(int)`、`key(int, int)`、`key(KeyStroke)`、`window(Window)`、`focus(boolean)`。
4. 实例内部仍只调用现有静态 `registerGlobalAction/removeGlobalAction`，不直接操作底层容器。
5. `load()` / `close()` 做幂等处理，并实现 `AutoCloseable`。
6. 先迁移对话框、工具窗这类生命周期明确的内部调用点。
7. 文档中把实例化 `QRGlobalAction` 标为推荐用法，把 `QRSwing` 里的全局事件方法继续标为兼容旧代码的过时入口。

我的建议是：这个新想法可以作为最终 API 的主推荐形态，但底层不要改成“每个事件对象自己管理 native hook”。正确分层应是 `QRGlobalAction` 的静态部分管共享基础设施，`QRGlobalAction` 的实例部分管单个注册生命周期。

## 2026-07-06 落地记录

已将 `QRGlobalAction` 调整为可实例化类，并实现 `AutoCloseable`：

- `new QRGlobalAction(action).key(...).window(...).focus(...).load()` 可作为推荐注册方式。
- `load()` 幂等，已注册时重复调用不会重复添加事件。
- `close()` 幂等，未注册时调用不会抛异常。
- `load()` 会自动确保全局 native hook 已注册。
- 已 `load()` 的实例不允许继续修改 `key/window/focus`，需要先 `close()`。

已先迁移生命周期明确的内部调用点：

- `QRDialog`：ESC 关闭事件改为实例字段，显示时 `load()`，隐藏或释放时 `close()`。
- `QROpinionDialog`：ENTER 确认事件改为实例字段，随显示/隐藏注册注销。
- `QRValueInputDialog`：ENTER/ESC 改为实例字段，随窗口打开/关闭注册注销。
- `QRFileSelectDialog`：F5 刷新事件改为实例字段，随显示/隐藏/释放注册注销。

已通过针对性编译检查：

```shell
javac -d /tmp/qr_swing_javac_check -cp 'lib/*:src' src/swing/qr/kiarelemb/QRGlobalAction.java src/swing/qr/kiarelemb/window/basic/QRDialog.java src/swing/qr/kiarelemb/window/enhance/QROpinionDialog.java src/swing/qr/kiarelemb/window/utils/QRValueInputDialog.java src/swing/qr/kiarelemb/window/utils/QRFileSelectDialog.java
```

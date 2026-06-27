# SwingWorker 封装改进方案

本文档描述 QR_Swing 中 SwingWorker 封装的改进方向。目标不是简单增加一个工具方法，而是建立一套适合本项目控件库风格的后台任务模型，统一解决 EDT 阻塞、进度显示、取消、异常处理和 UI 回调问题。

## 现状判断

当前项目已经有 `QRComponentUtils.runOnEdt(Runnable)`，并且 `QRProgressDialog` 的进度和描述更新会回到 EDT。

`QRComponentUtils` 中也已有一个初步的 `run(Window, String, ProgressTask<T>, Consumer<T>, Consumer<String>)` 方法，内部使用 `SwingWorker<T, Integer>` 和 `QRProgressDialog`。

这个雏形有价值，但还不够成为公共基础设施：

- 方法名 `run` 过于模糊。
- 只支持整数百分比进度。
- 只支持一种进度对话框形态。
- 错误回调只给字符串。
- 取消机制没有任务侧上下文。
- 没有任务状态、阶段文本、完成事件、默认错误处理。
- 没有批量任务、无进度任务、无对话框任务。
- 没有纳入文件选择器、主题、图片处理等真正耗时路径。

## 改造目标

1. 所有耗时任务离开 EDT。
2. 所有 UI 更新明确回到 EDT。
3. 提供统一任务状态：`PENDING`、`RUNNING`、`SUCCEEDED`、`FAILED`、`CANCELLED`。
4. 支持进度、阶段描述、结果、异常、取消。
5. 和现有 `QRProgressDialog`、`QRActionRegister` 风格兼容。
6. 对已有 API 尽量保持兼容，内部逐步迁移。
7. 文档明确每个回调在哪个线程执行。

## 推荐新增包

建议新增：

```text
src/swing/qr/kiarelemb/task/
```

建议类：

- `QRTask<T>`：任务接口，运行在后台线程。
- `QRTaskContext`：任务上下文，提供进度、阶段文本、取消检查。
- `QRTaskResult<T>`：任务完成结果，包含成功值、异常、取消状态。
- `QRTaskStatus`：任务状态枚举。
- `QRTaskListener<T>`：生命周期监听器。
- `QRTaskWorker<T>`：对 `SwingWorker<T, QRTaskProgress>` 的封装。
- `QRTaskProgress`：进度事件，包含百分比、current、total、message。
- `QRTaskOptions`：执行选项，例如是否显示进度对话框、标题、是否可取消、是否禁用父窗口、完成后是否自动关闭。
- `QRTaskRunner`：对外静态入口。

## 核心 API 草案

### 后台任务接口

```java
@FunctionalInterface
public interface QRTask<T> {
    T run(QRTaskContext context) throws Exception;
}
```

### 任务上下文

```java
public interface QRTaskContext {
    void progress(int current, int total);

    void progress(int percent);

    void message(String message);

    boolean isCancelled();

    void checkCancelled() throws CancellationException;
}
```

约定：

- `QRTask.run` 在后台线程执行，不能直接更新 Swing 组件。
- `context.progress` 和 `context.message` 可在后台线程调用，由封装层转发到 EDT。
- 长循环、目录扫描、图片批处理必须定期调用 `checkCancelled()`。

### 任务启动入口

```java
public final class QRTaskRunner {
    public static <T> QRTaskWorker<T> run(QRTask<T> task);

    public static <T> QRTaskWorker<T> run(QRTaskOptions options, QRTask<T> task);

    public static <T> QRTaskWorker<T> runWithProgress(Window owner,
                                                       String title,
                                                       QRTask<T> task,
                                                       Consumer<T> onSuccess,
                                                       Consumer<Throwable> onError);
}
```

回调线程约定：

- `QRTask.run`：后台线程。
- `onSuccess`：EDT。
- `onError`：EDT。
- `onCancelled`：EDT。
- `QRTaskListener` 的状态回调：EDT。

## 与现有类的关系

### `QRComponentUtils`

保留：

- `runOnEdt(Runnable)`
- `runActions`
- `runActionsLater`
- 布局和绘制工具方法

迁移：

- 当前 `QRComponentUtils.run(...)` 可以标记为 `@Deprecated`，内部委托给 `QRTaskRunner.runWithProgress(...)`。
- `ProgressTask<T>` 可以保留一版兼容适配，但新代码推荐使用 `QRTask<T>`。

### `QRProgressDialog`

建议增强：

- 增加 `setIndeterminate(boolean)`，支持未知总量任务。
- 增加 `setCancelEnabled(boolean)`。
- 增加 `setCloseOnCancel(boolean)` 或由任务控制取消后的关闭。
- 增加 `bind(QRTaskWorker<?>)` 或由 `QRTaskRunner` 内部绑定。
- 取消按钮默认调用 worker cancel，而不是只 dispose。

当前 `QRProgressBar` 是手绘确定进度条，若要支持不确定进度，可以先在 `QRProgressDialog` 中使用描述文本表达“处理中”，后续再扩展 `QRProgressBar` 绘制动画。不确定进度，请参考我以前自己写的模仿微软的用圆点移动的动画，具体请参考 `/home/kiarelemb/IdeaProjects/QR_Scan_Grade/src/main/java/sg/qr/kiarelemb/data/BallTempData.java` 中的 `BallRollPane` 类（该类引用了`/home/kiarelemb/IdeaProjects/QR_Scan_Grade/src/main/java/sg/qr/kiarelemb/data/BallTempData.java` 类作为数据计算的支持。这样的话，在 paintComponent 中就不需要绘制进度条，而是直接用圆点滚动动画。

### `QRActionRegister`

可以继续作为项目风格的回调接口，但新任务 API 推荐兼容 JDK `Consumer`、`BiConsumer`。原因是任务结果和异常处理更接近 Java 标准函数式接口，降低外部调用成本。

## 优先接入点

### 第一优先级：`QRFileSelectDialog`

问题：

- 构造和交互中会执行磁盘根目录读取、目录枚举、排序、系统图标读取和树节点加载。
- 大目录、慢磁盘、网络挂载盘会直接卡 UI。

建议改造：

- `setCurrentDirectory` 不直接 `fillFileList`，改为提交后台任务扫描目录。
- 后台任务返回 `List<FileItemData>`，EDT 中一次性更新 `DefaultListModel`。
- 树节点展开 `loadDirectoryNode` 改为后台加载子目录，加载中显示占位节点。
- 对连续快速切换目录的任务做取消：新任务开始前取消旧任务。
- 系统图标可懒加载或缓存，避免列表渲染时频繁阻塞。

### 第二优先级：图片处理

涉及：

- `QRLabel.makeRoundedCorner`
- `QRLabel.cutHeadImages`
- `QRLabel.scaleByPercentage`
- `QRFrame.setBackgroundImage`
- 图片预览相关窗口

建议改造：

- 图片读取、缩放、圆角生成在后台执行。
- EDT 只负责设置 `ImageIcon`、刷新组件。
- 增加图片处理任务取消，用户切换图片时取消前一个任务。
- 临时文件写入失败应通过 `onError(Throwable)` 返回，而不是吞异常或只 `printStackTrace()`。

### 第三优先级：主题系统

涉及：

- `QRColorsAndFonts.loadTheme`
- `QRColorsAndFonts.getThemeColors`
- `QRColorsAndFonts.isThemeFile`
- `QRSwingThemeDesigner` 的导入、保存、复制

建议改造：

- 主题目录扫描和主题文件验证放后台。
- EDT 中只应用颜色和刷新组件。
- 主题设计器保存时显示进度或禁用保存按钮，完成后恢复。

### 第四优先级：提示窗口线程修正

`QRSmallTipShow` 当前通过线程池执行创建和显示对话框。Swing 组件创建、pack、setLocation、setVisible、dispose 都应在 EDT 上完成。

建议：

- 后台线程只负责等待时间或调度延迟。
- 对话框创建、显示、关闭使用 `QRComponentUtils.runOnEdt` 或 `Swing Timer`。
- 若保留线程池，也只让线程池触发 `SwingUtilities.invokeLater`。

## 实施步骤

### 第 1 步：建立任务基础设施

新增 `task` 包，实现：

- `QRTask<T>`
- `QRTaskContext`
- `QRTaskProgress`
- `QRTaskStatus`
- `QRTaskResult<T>`
- `QRTaskWorker<T>`
- `QRTaskOptions`
- `QRTaskRunner`

同时为 `QRTaskRunner` 添加最小示例和 JavaDoc，明确线程约定。

### 第 2 步：兼容现有 `QRComponentUtils.run`

将当前 `QRComponentUtils.run(...)` 改为委托：

```java
QRTaskRunner.runWithProgress(owner, description, context -> {
    return task.run((current, total) -> context.progress(current, total));
}, onSuccess, onError);
```

旧 API 暂不删除，避免破坏现有调用方。

### 第 3 步：增强 `QRProgressDialog`

把 `QRProgressDialog` 从“显示控件”提升为“可绑定任务的进度窗口”：

- 绑定 worker。
- 取消按钮触发 cancel。
- 支持失败/取消后的文本变化。
- 支持关闭策略。

### 第 4 步：改造 `QRFileSelectDialog`

先处理文件列表扫描，再处理目录树展开。

建议内部字段：

```java
private QRTaskWorker<FileListSnapshot> fileListWorker;
private QRTaskWorker<TreeNodeSnapshot> treeNodeWorker;
```

每次目录变化：

1. 取消旧的 `fileListWorker`。
2. 显示“加载中...”状态。
3. 后台扫描目录、排序、过滤。
4. EDT 更新 `fileListModel` 和 `statusLabel`。
5. 如果任务被取消，不更新 UI。

当前完成情况：

- 文件列表扫描已迁移到 `QRTaskWorker<FileListSnapshot>`。
- 目录切换时会取消旧的 `fileListWorker`，避免旧目录结果覆盖新目录 UI。
- 文件列表后台任务会完成目录枚举、排序、过滤、显示名和系统图标读取。
- `FileItemCellRenderer` 不再调用 `FileSystemView.getSystemIcon(...)` 或 `getSystemDisplayName(...)`，渲染阶段只读取已经准备好的普通数据。
- 目录树展开已迁移到 `QRTaskWorker<ArrayList<FileTreeNodeData>>`。
- `FileTreeNode` 已增加 `loading` 和 `loadWorker` 状态，防止重复展开任务。
- 刷新树节点和关闭文件选择器时会取消未完成的树加载任务。
- 树节点加载失败或取消时会恢复为未加载状态，并重新放回占位节点，允许用户再次展开。

仍需继续：

- 根目录初始化仍同步执行 `File.listRoots()` 和根节点图标读取，通常较轻，但慢系统盘或异常挂载点仍可能卡顿。
- `buildPathToCurrentDirectory()` 为了同步定位树路径，会调用异步的 `loadDirectoryNode(node)` 后立即查找子节点，深层路径可能无法一次完整展开。需要设计异步路径展开流程。
- 目录树和文件列表目前是在后台读取系统图标，已经避免渲染卡顿，但仍可能让后台扫描耗时偏长。后续可以加全局图标缓存或分阶段懒加载。
- `safeListFiles(...)` 捕获异常后返回空数组，调用方拿不到失败原因。后续应让后台任务保留异常或错误状态，便于状态栏显示更明确的错误信息。

### 第 5 步：改造图片和主题

将耗时图片处理封装为独立任务方法，例如：

```java
QRTaskRunner.runWithProgress(owner, "正在处理图片", context -> {
    context.message("正在读取图片...");
    BufferedImage image = ImageIO.read(file);
    context.message("正在缩放图片...");
    return process(image);
}, label::setIcon, QROpinionDialog::showError);
```

主题保存和导入也按同样模型处理。

### 第 6 步：测试与示例

新增或改造测试示例：

- `QRTaskRunnerTest`
- `QRProgressDialogTest` 增加真实 `SwingWorker` 示例，而不是只用 `Timer`。
- `QRFileSelectDialogTest` 增加大目录打开和取消场景。

测试重点：

- 成功回调在 EDT。
- 异常回调在 EDT。
- 取消后不更新旧 UI。
- 进度不超过 0 到 100。
- 快速切换目录不会出现旧结果覆盖新结果。

## API 设计注意事项

- 不要暴露裸 `SwingWorker` 给普通调用方；可以返回 `QRTaskWorker`，内部再持有 `SwingWorker`。
- 错误回调必须传 `Throwable`，不要只传字符串。
- 任务取消不能只依赖 `Thread.interrupt`，必须提供 `context.isCancelled()` 和 `context.checkCancelled()`。
- `done()` 中调用 `get()` 必须处理 `InterruptedException`、`CancellationException`、`ExecutionException`。
- 所有 Swing 组件读写都应在 EDT。后台线程需要的数据应先复制为普通数据对象。
- 后台任务不要直接持有大量 Swing 组件引用，避免泄漏窗口。
- 连续用户操作触发的任务必须有取消策略，尤其是文件选择器和图片预览。

## 当前落地进度

### 已完成

- 新增 `src/swing/qr/kiarelemb/task/` 任务基础设施：
  - `QRTask<T>`
  - `QRTaskContext`
  - `QRTaskProgress`
  - `QRTaskStatus`
  - `QRTaskResult<T>`
  - `QRTaskListener<T>`
  - `QRTaskWorker<T>`
  - `QRTaskOptions`
  - `QRTaskRunner`
- `QRComponentUtils.run(...)` 已标记为 `@Deprecated`，并委托给 `QRTaskRunner.runWithProgress(...)`。
- `QRProgressDialog` 已支持：
  - `setIndeterminate(boolean)`
  - `setCancelEnabled(boolean)`
  - `bind(QRTaskWorker<?>)`
  - 取消按钮触发 `worker.cancel(true)`
  - 任务失败、取消、完成后的文本和关闭策略
- `QRProgressBar` 已支持不确定进度动画，使用圆点滚动而不是确定进度条绘制。
- `QRFileSelectDialog` 已完成第一轮异步改造：
  - 文件列表扫描后台化。
  - 目录树展开后台化。
  - 连续切换目录时取消旧列表任务。
  - 刷新和关闭时取消树节点加载任务。
  - 列表和树渲染器不再同步读取系统图标和显示名。
- `QRSmallTipShow` 已完成线程修正：
  - 窗口创建、`pack`、定位、显示都回到 EDT。
  - 自动关闭改用 Swing `Timer`，`dispose()` 在 EDT 执行。
  - 删除原先用于 Swing UI 操作的后台线程池。

### 已验证

当前已通过以下编译验证：

```bash
javac -cp 'lib/*' -d /tmp/qr_swing_compile $(find src -name '*.java')
```

编译结果：

- 通过。
- 仅保留 `QRCaret.java uses or overrides a deprecated API` 提示。
- `git diff --check` 无空白错误，但 `QRSmallTipShow.java` 存在 Git 行尾提示：工作区 CRLF 将在 Git 触碰时转换为 LF。

## 下一步计划

### 下一步 1：收紧任务基础设施 API

目标：让任务模型更适合作为公共 API，而不是只满足当前内部调用。

建议处理：

- 给 `QRTaskRunner`、`QRTaskWorker`、`QRTaskOptions` 补齐 JavaDoc，明确每个回调线程。
- 考虑在 `QRTaskOptions` 中加入成功、失败、取消、完成回调，避免 `run(options, task)` 只能返回 worker 后再手动 `addListener`。
- 明确 `QRTaskWorker.status()` 的线程可见性，必要时将 `status` 改为 `volatile`。
- 为 `QRTaskWorker.addListener(...)` 增加“任务已完成后再添加监听器”的行为定义，避免调用方误判。
- 评估 `QRTaskRunner.runWithProgress(...)` 的 `title` 和 `description` 是否应拆开，避免标题和描述总是相同。

### 下一步 2：完善 `QRFileSelectDialog` 的深层树定位和错误反馈

目标：把第一优先级接入点从“可用”推进到“行为稳定”。

建议处理：

- 重做 `buildPathToCurrentDirectory()`，改成异步逐级展开：加载父节点完成后再选择或加载下一层。
- 对根目录初始化做后台化或延迟加载，减少构造窗口时的阻塞风险。
- 增加系统图标缓存，避免同类型文件重复请求系统图标。
- 将 `safeListFiles(...)` 的失败原因传到 `FileListSnapshot` 或单独的失败结果中，在状态栏显示“无权限”“读取失败”等更准确的文本。
- 快速连续切换目录、刷新、关闭窗口时，补充一个测试窗口或手动测试流程。

### 下一步 3：开始第二优先级图片处理改造

目标：把明显耗时的图片读取、缩放、圆角和背景图处理移出 EDT。

建议处理顺序：

1. 先梳理 `QRLabel.makeRoundedCorner`、`cutHeadImages`、`scaleByPercentage`、`createAutoAdjustIconAndRound` 的调用路径。
2. 抽出纯图片处理方法，确保后台线程只处理 `BufferedImage`、`File`、路径等普通对象。
3. 新增一个小型图片任务入口，例如 `QRImageTasks` 或 `QRLabel` 内部静态异步方法。
4. EDT 中只做 `label.setIcon(...)`、`frame.setBackgroundImage(...)`、`repaint()`。
5. 对连续切换图片的调用方保存 `QRTaskWorker<?>`，新任务开始前取消旧任务。

### 下一步 4：主题系统后台化

目标：主题文件扫描、验证、导入、保存不阻塞 UI。

建议处理：

- 先处理 `QRSwingThemeDesigner` 的导入和保存按钮动作。
- 保存期间禁用相关按钮，完成、失败、取消后恢复。
- `QRColorsAndFonts.isThemeFile(...)` 和主题目录扫描可放入后台任务。
- EDT 中只做颜色应用和组件刷新。

## 推荐验收标准

- 新任务 API 有 JavaDoc，明确线程模型。
- `QRComponentUtils.run(...)` 仍可用，但新实现委托给 `QRTaskRunner`。
- `QRProgressDialog` 可以直接绑定任务并正确取消。
- `QRFileSelectDialog` 打开大目录时窗口仍可移动、可取消或可切换目录。
- 图片处理不会冻结主窗口。
- 任务失败时能保留异常对象并在 UI 中给出友好提示。
- 至少有一个测试窗口能演示成功、失败、取消、进度更新四种路径。

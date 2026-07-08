# QRFileSelectDialog 文件列表右键菜单方案

## 背景

`QRFileSelectDialog` 右侧文件列表由 `QRList<FileItem> fileList` 承载，当前已经支持：

- 单击列表项后同步底部“选择/文件名”输入框。
- 双击 `..` 返回上一级。
- 双击文件夹进入该文件夹。
- 双击可选择文件时同步选择，保存模式下双击文件可直接确认。
- Enter 键执行打开或确认。
- 顶部按钮支持“上一级”“刷新”和排序。

本次目标是在右侧文件/文件夹显示列表上增加右键菜单。这个菜单应当复用项目已有的 `QRPopupMenu`，并保持文件选择器本身的选择语义，不引入和系统文件管理器一样重的文件操作能力。

## 设计原则

1. 右键菜单只绑定右侧 `fileList`，不影响左侧目录树。
2. 右键时先定位鼠标所在列表项，再更新列表选中项，避免菜单动作作用到旧选择。
3. 菜单动作复用现有私有方法，例如 `setCurrentDirectory`、`gotoParentDirectory`、`refreshCurrentDirectory`、`updateSelectedFile`、`approveSelection`。
4. 第一阶段不做删除、重命名、新建文件夹等破坏性或写磁盘动作，避免文件选择器职责膨胀。
5. 禁用不可用菜单项，而不是点击后再弹错误提示。
6. 保持跨平台右键触发逻辑：同时处理 `mousePressed` 和 `mouseReleased` 中的 `MouseEvent.isPopupTrigger()`。

## 推荐菜单项

第一阶段建议提供以下菜单项：

| 菜单项 | 作用对象 | 启用条件 | 行为 |
| --- | --- | --- | --- |
| 打开 | 文件夹或 `..` | 右键项为文件夹或父目录项 | `..` 调用 `gotoParentDirectory()`；文件夹调用 `setCurrentDirectory(item.file)` |
| 选择 | 文件或文件夹 | `canSelect(item.file)` 为 true | `updateSelectedFile(item.file)` |
| 确定选择 | 文件或文件夹 | `canSelect(item.file)` 为 true；保存模式下按当前文本框校验 | 同步选择后调用 `approveSelection()` |
| 刷新 | 当前目录 | `currentDirectory != null` | 调用 `refreshCurrentDirectory()` |
| 复制路径 | 文件、文件夹或当前目录 | 有右键项时复制该项路径；空白处复制当前目录路径 | 将绝对路径写入系统剪贴板 |

菜单显示建议：

```text
打开
选择
确定选择
----
刷新
复制路径
```

其中 `打开` 和 `选择` 的语义分开：

- `打开` 是导航行为，只进入目录或返回上一级。
- `选择` 是选择器行为，只更新底部选择框，不关闭窗口。
- `确定选择` 是选择并关闭窗口。

## 不建议第一阶段加入的菜单项

以下能力暂不建议加入：

- 删除：涉及确认、权限失败、回收站还是永久删除、树和列表同步刷新，风险较高。
- 重命名：需要输入弹窗、扩展名策略、保存模式冲突处理。
- 新建文件夹：属于写磁盘动作，且不同选择模式下语义不一致。
- 在系统文件管理器中打开：依赖桌面环境，失败处理复杂，可后续作为可选扩展。

如果后续确实需要文件管理能力，建议作为第二阶段单独设计，并加上明确开关，避免所有文件选择场景默认暴露写操作。

## 结构调整

建议在 `QRFileSelectDialog` 中新增字段：

```java
private QRPopupMenu fileListPopupMenu;
private QRMenuItem openMenuItem;
private QRMenuItem selectMenuItem;
private QRMenuItem approveMenuItem;
private QRMenuItem refreshMenuItem;
private QRMenuItem copyPathMenuItem;
private FileItem popupFileItem;
```

需要新增导入：

```java
import swing.qr.kiarelemb.combination.QRPopupMenu;

import java.awt.event.MouseEvent;
```

后续项目已有统一剪贴板工具，应优先改用项目工具：

```java
QRSystemUtils.putTextToClipboard(String);
QRSystemUtils.getSysClipboardText() ;
```


## 初始化位置

在 `initList()` 的末尾调用：

```java
initFileListPopupMenu();
```

这样菜单和列表的渲染、选择、双击、键盘逻辑放在同一个初始化块中，后续维护时不需要跨区域查找。

## 右键定位逻辑

新增方法：

```java
private void initFileListPopupMenu() {
    fileListPopupMenu = fileList.addPopupMenu(this::beforeShowFileListPopupMenu);

    openMenuItem = new QRMenuItem("打开");
    selectMenuItem = new QRMenuItem("选择");
    approveMenuItem = new QRMenuItem("确定选择");
    refreshMenuItem = new QRMenuItem("刷新");
    copyPathMenuItem = new QRMenuItem("复制路径");

    openMenuItem.addClickAction(e -> openPopupFileItem());
    selectMenuItem.addClickAction(e -> selectPopupFileItem(false));
    approveMenuItem.addClickAction(e -> selectPopupFileItem(true));
    refreshMenuItem.addClickAction(e -> refreshCurrentDirectory());
    copyPathMenuItem.addClickAction(e -> copyPopupPath());

    fileListPopupMenu.add(openMenuItem);
    fileListPopupMenu.add(selectMenuItem);
    fileListPopupMenu.add(approveMenuItem);
    fileListPopupMenu.addSeparator();
    fileListPopupMenu.add(refreshMenuItem);
    fileListPopupMenu.add(copyPathMenuItem);
}
```

这里不直接使用 `fileListPopupMenu.bind(fileList)`，而是使用 `QRList.addPopupMenu(beforeShow)`。`QRList` 负责右键前选中鼠标所在项，`QRFileSelectDialog` 在 `beforeShow` 回调中二次定位空白处并刷新菜单状态。

右键触发方法：

```java
private void beforeShowFileListPopupMenu(MouseEvent e) {
    popupFileItem = fileItemAt(e.getPoint());
    if (popupFileItem == null) {
        fileList.clearSelection();
    }
    updateFileListPopupMenuState();
}
```

列表项定位方法：

```java
private FileItem fileItemAt(Point point) {
    int index = fileList.locationToIndex(point);
    if (index < 0) {
        return null;
    }
    Rectangle cellBounds = fileList.getCellBounds(index, index);
    if (cellBounds == null || !cellBounds.contains(point)) {
        return null;
    }
    return fileListModel.getElementAt(index);
}
```

`locationToIndex` 会返回最接近的索引，因此必须用 `getCellBounds(...).contains(point)` 再判断一次，避免右键空白处误命中最后一个文件。

## 菜单状态规则

```java
private void updateFileListPopupMenuState() {
    boolean hasItem = popupFileItem != null;
    boolean isParent = hasItem && popupFileItem.parent;
    boolean isDirectory = hasItem && !isParent && popupFileItem.file.isDirectory();
    boolean selectable = hasItem && !isParent && canSelect(popupFileItem.file);

    openMenuItem.setEnabled(isParent || isDirectory);
    selectMenuItem.setEnabled(selectable);
    approveMenuItem.setEnabled(selectable);
    refreshMenuItem.setEnabled(currentDirectory != null);
    copyPathMenuItem.setEnabled(hasItem || currentDirectory != null);
}
```

保存模式需要特别处理：

- 右键文件时，“选择”可以填入文件名，保持和单击现有逻辑一致。
- “确定选择”应先调用 `updateSelectedFile(item.file)`，再走 `approveSelection()`，让已有覆盖确认、扩展名校验、目标路径校验继续生效。
- 右键文件夹时，“打开”进入目录；不建议把文件夹作为保存目标。

## 菜单动作

```java
private void openPopupFileItem() {
    if (popupFileItem == null) {
        return;
    }
    if (popupFileItem.parent) {
        gotoParentDirectory();
    } else if (popupFileItem.file.isDirectory()) {
        setCurrentDirectory(popupFileItem.file);
        if (selectMode == SelectMode.DIRECTORY_ONLY || selectMode == SelectMode.SAVE_FILE) {
            updateSelectedFile(currentDirectory);
        }
    }
}
```

```java
private void selectPopupFileItem(boolean approve) {
    if (popupFileItem == null || popupFileItem.parent) {
        return;
    }
    updateSelectedFile(popupFileItem.file);
    if (approve) {
        approveSelection();
    }
}
```

```java
private void copyPopupPath() {
    File file = null;
    if (popupFileItem != null) {
        file = popupFileItem.file;
    } else if (currentDirectory != null) {
        file = currentDirectory;
    }
    if (file == null) {
        return;
    }
    QRSystemUtils.putTextToClipboard(file.getAbsolutePath());
}
```

`..` 的 `popupFileItem.file` 是父目录，因此“复制路径”复制父目录路径。

## 和现有逻辑的关系

为了避免右键菜单和双击、Enter 逻辑分叉，建议把已有重复流程逐步抽出小方法：

- `openFileItem(FileItem item)`：处理 `..`、进入目录。
- `selectFileItem(FileItem item, boolean approve)`：处理选择和确认。
- `fileItemAt(Point point)`：供右键和未来拖拽等逻辑共用。

第一阶段可以只新增右键菜单方法，不强行重构现有双击逻辑；如果实现时发现代码重复明显，再把双击逻辑改为调用这些小方法。

## 测试方案

建议更新或新增 `src/swing/qr/kiarelemb/test/QRFileSelectDialogAsyncTest.java`，手动验证以下场景：

1. 在普通文件上右键：可“选择”“确定选择”“复制路径”，不可“打开”。
2. 在文件夹上右键：可“打开”；在 `FILE_AND_DIRECTORY` 或 `DIRECTORY_ONLY` 下可“选择”。
3. 在 `..` 上右键：只允许“打开”“刷新”“复制路径”。
4. 在列表空白处右键：只允许“刷新”和“复制路径”，复制当前目录路径。
5. 在 `FILE_ONLY` 模式下右键文件夹：“打开”可用，“选择/确定选择”不可用。
6. 在 `SAVE_FILE` 模式下右键文件：选择后底部文件名更新，确定选择仍触发已有覆盖确认。
7. 在加载中或无权限目录中右键空白处：菜单不抛异常，刷新可用性正确。

## 实施步骤

1. 为 `QRFileSelectDialog` 增加 `QRPopupMenu` 和菜单项字段。
2. 在 `initList()` 中追加 `initFileListPopupMenu()`。
3. 实现右键定位、菜单状态更新和菜单动作方法。
4. 运行现有 `QRFileSelectDialogAsyncTest` 或补充一个专用测试入口手动验证。
5. 如菜单项宽度或分割线显示异常，回到 `QRPopupMenu` 层修正，不在业务类里绕开主题控件。

## 结论

推荐把右键菜单作为 `QRFileSelectDialog` 内部能力实现，使用 `QRPopupMenu` 但不直接调用 `bind(fileList)`。文件列表右键需要先定位和选中鼠标所在项，再按当前选择模式更新菜单状态；这个前置逻辑放在 `QRFileSelectDialog` 内最清晰，也能最大限度复用现有选择、导航和确认流程。

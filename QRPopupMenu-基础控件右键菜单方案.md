# 基础控件右键菜单实现方案

## 背景

当前 `QRPanel`、`QRTextPane`、`QRTextArea`、`QRTable`、`QRList` 等控件已经有类似 `addScrollPane()` 的增强方法：由控件自身创建、缓存并返回增强组件实例，重复调用返回同一个对象。

右键菜单也可以沿用这个模式：

```java
QRTextPane textPane = new QRTextPane();
QRPopupMenu popupMenu = textPane.addPopupMenu();
popupMenu.add(new QRMenuItem("复制"));
popupMenu.add(new QRMenuItem("粘贴"));
```

目标是让基础控件可以方便地挂载 `QRPopupMenu`，并在鼠标右键触发时自动显示，同时保留调用方继续配置菜单项的能力。

## 推荐 API

建议统一使用：

```java
public QRPopupMenu addPopupMenu()
```

语义和 `addScrollPane()` 保持一致：

- 第一次调用时创建并绑定右键显示逻辑。
- 后续调用返回同一个 `QRPopupMenu` 实例。
- 调用方拿到实例后自行添加 `QRMenuItem`、`QRButton`、分割线等内容。
- 控件销毁时不需要额外释放，菜单由组件持有。

如需允许外部传入菜单实例，可以额外提供：

```java
public QRPopupMenu setPopupMenu(QRPopupMenu popupMenu)
```

但第一阶段不建议把这个作为主 API，避免生命周期和重复监听器变复杂。

## 方案一：在每个基础控件中各自实现

### 做法

在需要右键菜单的控件中增加字段：

```java
protected QRPopupMenu popupMenu;
```

然后在类内增加：
（一般基础控件都有自定义的addMouseListener(TYPE, ActionRegister)，所以这里用自定义的就行）
```java
public QRPopupMenu addPopupMenu() {
    if (this.popupMenu == null) {
        this.popupMenu = new QRPopupMenu(SwingUtilities.getWindowAncestor(this));
		addMouseListener(QRMouseListener.TYPE.RELEASE, e -> {
			showPopupMenuIfNeeded(e);
		});
    }
    return this.popupMenu;
}

private void showPopupMenuIfNeeded(MouseEvent e) {
    if (e.isPopupTrigger() && this.popupMenu != null) {
        this.popupMenu.show(this, e.getX(), e.getY());
    }
}
```

### 优点

- 和现有 `addScrollPane()` 风格最接近，调用体验统一。
- 不引入新接口、新工具类，改动直观。
- 每个控件可以根据自身场景定制行为，例如 `QRTable` 可以先选中右键所在行，`QRTextPane` 可以根据选中文本状态启用菜单项。

### 缺点

- 重复代码较多。
- 后续如果要调整右键触发规则，需要同步修改多个控件。
- 基础控件没有共同父类，无法只在一个基类里实现。

### 适用场景

适合先给少量控件提供右键菜单，或者不同控件的右键行为差异比较大时使用。

## 方案二：新增工具类统一绑定

### 做法

新增工具类，例如：

```java
public final class QRPopupMenuUtils {
    private QRPopupMenuUtils() {
    }

    public static QRPopupMenu createAndBind(Component component) {
        QRPopupMenu popupMenu = new QRPopupMenu(SwingUtilities.getWindowAncestor(component));
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showIfNeeded(component, popupMenu, e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showIfNeeded(component, popupMenu, e);
            }
        });
        return popupMenu;
    }

    private static void showIfNeeded(Component component, QRPopupMenu popupMenu, MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupMenu.show(component, e.getX(), e.getY());
        }
    }
}
```

各基础控件只保留自己的缓存字段：

```java
protected QRPopupMenu popupMenu;

public QRPopupMenu addPopupMenu() {
    if (this.popupMenu == null) {
        this.popupMenu = QRPopupMenuUtils.createAndBind(this);
    }
    return this.popupMenu;
}
```

### 优点

- 控件内代码很少，重复逻辑集中在工具类。
- 右键触发兼容逻辑可以统一维护。使用 `mousePressed` 和 `mouseReleased` 同时判断 `isPopupTrigger()`，能兼容不同平台。
- 不改变现有类继承结构。

### 缺点

- 每个控件仍然需要增加字段和 `addPopupMenu()` 方法。
- 如果某些控件有特殊行为，需要工具类提供回调或控件自己补充逻辑。

### 适用场景

这是比较稳妥的通用方案。它保留了 `addScrollPane()` 式 API，又避免把右键监听细节复制到每个类里。

## 方案三：新增接口提供默认方法

### 做法

新增接口，例如：

```java
public interface QRPopupMenuSupport {
    QRPopupMenu popupMenu();

    void popupMenu(QRPopupMenu popupMenu);

    default QRPopupMenu addPopupMenu(Component component) {
        QRPopupMenu menu = popupMenu();
        if (menu == null) {
            menu = QRPopupMenuUtils.createAndBind(component);
            popupMenu(menu);
        }
        return menu;
    }
}
```

控件实现接口后再包装成无参方法：

```java
public class QRTextPane extends JTextPane implements QRPopupMenuSupport {
    protected QRPopupMenu popupMenu;

    @Override
    public QRPopupMenu popupMenu() {
        return this.popupMenu;
    }

    @Override
    public void popupMenu(QRPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    public QRPopupMenu addPopupMenu() {
        return QRPopupMenuSupport.super.addPopupMenu(this);
    }
}
```

### 优点

- 把“缓存并返回同一个菜单”的逻辑抽出来。
- 控件仍能暴露无参 `addPopupMenu()`，调用体验不受影响。
- 后续可以通过接口识别哪些控件支持 `QRPopupMenu`。

### 缺点

- Java 接口不能直接持有实例字段，每个控件仍要写 getter/setter 和字段。
- 对当前项目来说抽象感偏重，新增代码不一定比方案二少。
- 如果只是为了减少重复代码，接口收益有限。

### 适用场景

适合未来计划让很多控件都具备统一的“增强能力声明”，并且希望外部代码能通过接口判断控件是否支持右键菜单。

## 方案四：改造 `QRPopupMenu`，让它自己绑定组件

### 做法

给 `QRPopupMenu` 增加绑定方法：

```java
public QRPopupMenu bind(Component component) {
    component.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            showIfNeeded(component, e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showIfNeeded(component, e);
        }
    });
    return this;
}

private void showIfNeeded(Component component, MouseEvent e) {
    if (e.isPopupTrigger()) {
        show(component, e.getX(), e.getY());
    }
}
```

控件中：

```java
public QRPopupMenu addPopupMenu() {
    if (this.popupMenu == null) {
        this.popupMenu = new QRPopupMenu(SwingUtilities.getWindowAncestor(this)).bind(this);
    }
    return this.popupMenu;
}
```

### 优点

- 菜单绑定逻辑和菜单类放在一起，使用上也直观。
- 不需要额外工具类。
- 外部也可以直接写 `new QRPopupMenu(parent).bind(component)`。

### 缺点

- `QRPopupMenu` 会同时承担菜单容器和组件事件绑定职责。
- 如果后续要给不同控件加前置行为，例如表格右键先选中行，`QRPopupMenu` 内部不容易处理。
- 菜单类会知道更多触发组件细节，职责边界不如工具类清楚。

### 适用场景

适合希望 `QRPopupMenu` 自身成为完整右键菜单能力入口，且特殊控件行为较少的情况。

## 推荐方案

推荐采用 **方案二：新增工具类统一绑定 + 各控件提供 `addPopupMenu()`**。

原因：

- API 和 `addScrollPane()` 一致，使用者容易理解。
- 不要求调整现有继承结构。
- 右键触发兼容逻辑集中维护，避免每个控件复制一份监听器。
- 控件仍然保留足够空间做特殊处理。

建议先支持这些基础控件：

- `QRTextField`
- `QRTextArea`
- `QRTextPane`
- `QREditorPane`
- `QRPanel`
- `QRList`
- `QRTable`
- `QRTree`

## 推荐实现细节

### 1. 工具类返回绑定后的菜单

建议工具类方法接收可选的显示前回调：

```java
public static QRPopupMenu createAndBind(Component component) {
    return createAndBind(component, null);
}

public static QRPopupMenu createAndBind(Component component, QRActionRegister<MouseEvent> beforeShow) {
    QRPopupMenu popupMenu = new QRPopupMenu(SwingUtilities.getWindowAncestor(component));
    component.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            showIfNeeded(component, popupMenu, beforeShow, e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            showIfNeeded(component, popupMenu, beforeShow, e);
        }
    });
    return popupMenu;
}
```

这样 `QRTable`、`QRList` 后续可以在显示菜单前同步选中鼠标所在行或项。

### 2. 控件中缓存菜单实例

每个控件保持简单写法：

```java
protected QRPopupMenu popupMenu;

public QRPopupMenu addPopupMenu() {
    if (this.popupMenu == null) {
        this.popupMenu = QRPopupMenuUtils.createAndBind(this);
    }
    return this.popupMenu;
}
```

### 3. 对表格和列表保留扩展点

`QRTable` 可以这样绑定：

```java
public QRPopupMenu addPopupMenu() {
    if (this.popupMenu == null) {
        this.popupMenu = QRPopupMenuUtils.createAndBind(this, e -> {
            int row = rowAtPoint(e.getPoint());
            if (row >= 0 && !isRowSelected(row)) {
                setRowSelectionInterval(row, row);
            }
        });
    }
    return this.popupMenu;
}
```

`QRList` 可以类似地根据 `locationToIndex(e.getPoint())` 更新选中项。

### 4. 父窗口获取时机

`QRPopupMenu` 构造需要 `Window parent`。如果组件调用 `addPopupMenu()` 时尚未加入窗口，`SwingUtilities.getWindowAncestor(component)` 可能返回 `null`。

有两种处理方式：

- 简单处理：允许 `parent` 为 `null`，保持当前构造逻辑不变。如果 `QREmptyDialog` 支持空父窗口，这是最低成本方案。
- 稳妥处理：工具类在首次显示时再检查窗口祖先；如果菜单还没有创建，则延迟创建菜单。

如果当前 `QREmptyDialog` 对 `null` 父窗口没有问题，可以先采用简单处理。若后续发现定位、焦点或置顶关系异常，再改为懒创建。

## 不推荐方案

不建议直接使用 Swing 原生 `JPopupMenu` 作为基础控件右键菜单。项目里已经有 `QRPopupMenu`，并且 README 中说明它是为了适配 QRSwing 自己的窗体、透明度、圆角和主题效果。基础控件继续返回 `QRPopupMenu` 更符合现有设计方向。

## 最小落地步骤

1. 新增 `QRPopupMenuUtils`，负责创建 `QRPopupMenu` 并绑定右键触发。
2. 在第一批基础控件中增加 `protected QRPopupMenu popupMenu` 字段。
3. 在第一批基础控件中增加 `public QRPopupMenu addPopupMenu()`。
4. 给 `QRTable`、`QRList` 预留显示前回调，用于右键时同步选中目标。
5. 增加一个简单测试类，验证右键菜单在文本控件、列表、表格上都能显示。

## 结论

如果目标是“像 `addScrollPane()` 一样返回 `QRPopupMenu` 实例”，最佳路径是：控件暴露 `addPopupMenu()`，内部缓存菜单实例；右键监听和显示逻辑由 `QRPopupMenuUtils` 统一处理。这样既保持调用方式简单，也给不同控件留下定制空间。
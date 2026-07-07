package swing.qr.kiarelemb.utils;

import method.qr.kiarelemb.utils.QRFontUtils;
import method.qr.kiarelemb.utils.QRStringUtils;
import swing.qr.kiarelemb.basic.QRButton;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.basic.QRRoundButton;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.task.QRTaskRunner;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * QR Swing 常用组件工具类。
 *
 * <p>该类集中提供绝对布局辅助、按钮快捷添加、文本绘制、富文本属性创建、
 * 批量执行回调、延迟执行、窗口重绘、递归遍历组件、透明背景适配以及 EDT 调度工具。
 *
 * <p>如果项目页面使用 {@code null} 布局，{@link #setBoundsAndAddToComponent(JComponent, JComponent, int, int, int, int)}
 * 是最常用的添加控件方法；如果窗口开启了背景图片，{@link #componentLoopToSetOpaque(JComponent, boolean)}
 * 和 {@link #windowFresh(JComponent)} 常配合设置项变更使用。
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-23 13:22
 **/
public class QRComponentUtils {

	/**
	 * 向 {@code parent} 中添加 {@code comToAdd}，并设置 {@code comToAdd} 的 {@link JComponent#setBounds(int, int, int, int)}
	 *
	 * <p>适用于父容器使用 {@code null} 布局的设置页、工具面板和固定尺寸弹窗。
	 * 如果父容器使用 {@link LayoutManager}，布局管理器可能会忽略这里设置的 bounds。</p>
	 *
	 * <pre><code>
	 * QRPanel panel = new QRPanel(null);
	 * QRLabel label = new QRLabel("用户名");
	 * QRTextField field = new QRTextField();
	 * QRComponentUtils.setBoundsAndAddToComponent(panel, label, 20, 20, 80, 30);
	 * QRComponentUtils.setBoundsAndAddToComponent(panel, field, 110, 20, 180, 30);
	 * </code></pre>
	 *
	 * @param parent   添加的父容器
	 * @param comToAdd 子控件
	 * @param x        子控件位置 x
	 * @param y        子控件位置 y
	 * @param width    子控件位置宽度
	 * @param height   子控件位置高度
	 */
	public static void setBoundsAndAddToComponent(JComponent parent, JComponent comToAdd, int x, int y, int width,
	                                              int height) {
		comToAdd.setBounds(x, y, width, height);
		parent.add(comToAdd);
	}

	/**
	 * 向 {@code parent} 中添加 {@code comToAdd}，并设置 {@code comToAdd} 的 {@link JComponent#setLocation(int, int)}
	 *
	 * <p>该重载只设置位置，不设置尺寸。调用前应已通过 {@link JComponent#setSize(int, int)}、
	 * {@link JComponent#setPreferredSize(Dimension)} 或组件自身逻辑确定尺寸。</p>
	 *
	 * @param parent   添加的父容器
	 * @param comToAdd 子控件
	 * @param x        子控件位置 x
	 * @param y        子控件位置 y
	 */
	public static void setBoundsAndAddToComponent(JComponent parent, JComponent comToAdd, int x, int y) {
		comToAdd.setLocation(x, y);
		parent.add(comToAdd);
	}

	/**
	 * 将按钮以指定尺寸添加到面板，并注册点击事件。
	 *
	 * <p>该方法封装了按钮加入面板的三个步骤：{@code add}、{@code setPreferredSize}、
	 * {@code addClickAction}，适合配合 {@link QRPanel}（基于 {@code null} 布局）快速布局按钮。
	 *
	 * @param parent    目标按钮容器
	 * @param button    要添加的按钮，支持 {@link QRButton} 及其子类（如 {@link QRRoundButton}）
	 * @param dimension 按钮的尺寸，用于 {@link JComponent#setPreferredSize(Dimension)}
	 * @param action    按钮点击时触发的操作，参数为 {@link ActionEvent}
	 */
	public static void addButtonAction(JComponent parent, QRButton button, Dimension dimension, QRActionRegister<ActionEvent> action) {
		addButtonAction(parent, button, null, dimension, action);
	}

	/**
	 * 将按钮以指定尺寸添加到面板，并注册点击事件。
	 *
	 * <p>该方法封装了按钮加入面板的三个步骤：{@code add}、{@code setPreferredSize}、
	 * {@code addClickAction}，适合配合 {@link QRPanel}（基于 {@code null} 布局）快速布局按钮。
	 *
	 * @param parent     目标按钮容器
	 * @param button     要添加的按钮，支持 {@link QRButton} 及其子类（如 {@link QRRoundButton}）
	 * @param constraint 按钮的布局信息，用于 {@link JComponent#add(Component, Object)}
	 * @param dimension  按钮的尺寸，用于 {@link JComponent#setPreferredSize(Dimension)}
	 * @param action     按钮点击时触发的操作，参数为 {@link ActionEvent}
	 */
	public static void addButtonAction(JComponent parent, QRButton button, Object constraint, Dimension dimension, QRActionRegister<ActionEvent> action) {
		parent.add(button, constraint);
		button.setPreferredSize(dimension);
		button.addClickAction(action);
	}

	/**
	 * 横纵居中绘制文字
	 *
	 * <p>常用于自绘按钮、占位面板和轻量提示组件。该方法只负责绘制，不会修改组件文本属性。</p>
	 *
	 * @param com   控件
	 * @param g     工具
	 * @param text  内容
	 * @param font  字体
	 * @param color 前景色
	 */
	public static void componentStringDraw(JComponent com, Graphics g, String text, Font font, Color color) {
		float y = com.getHeight() / 1.7f;
		componentStringDraw(com, g, text, font, color, y);
	}

	/**
	 * 居中绘制文字
	 *
	 * @param com   控件
	 * @param g     工具
	 * @param text  内容
	 * @param font  字体
	 * @param color 前景色
	 * @param y     纵位置
	 */
	public static void componentStringDraw(JComponent com, Graphics g, String text, Font font, Color color, float y) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle2D r = QRFontUtils.getStringBounds(text, font);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				com.getClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING));
		g2.setFont(font);
		g2.setColor(color);
		float x = (float) (com.getWidth() / 2f - r.getWidth() / 2);
		g2.drawString(text, x, y);
	}

	/**
	 * 指定坐标绘制文字
	 *
	 * @param com   控件
	 * @param g     工具
	 * @param text  内容
	 * @param font  字体
	 * @param color 前景色
	 * @param x     x     横位置
	 * @param y     纵位置
	 */
	public static void componentStringDraw(JComponent com, Graphics g, String text, Font font, Color color, float x, float y) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				com.getClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING));
		g2.setFont(font);
		g2.setColor(color);
		g2.drawString(text, x, y);
	}

	/**
	 * 创建并返回一个具有指定字体属性的 {@link SimpleAttributeSet} 对象。
	 *
	 * <p>该对象可直接用于 {@link javax.swing.text.StyledDocument#insertString(int, String, javax.swing.text.AttributeSet)}
	 * 或 {@link JTextPane#setCharacterAttributes(javax.swing.text.AttributeSet, boolean)}。</p>
	 *
	 * @param f         字体
	 * @param colorFore 文本前景色，用于设置文本的颜色。
	 * @param colorBack 文本背景色，用于设置文本的背景颜色。
	 * @return 返回一个具有指定字体属性的 {@link SimpleAttributeSet} 对象。
	 */
	public static SimpleAttributeSet getSimpleAttributeSet(Font f, Color colorFore, Color colorBack) {
		return getSimpleAttributeSet(f.getFamily(), f.getSize(), f.getStyle(), colorFore, colorBack);
	}

	/**
	 * 创建并返回一个具有指定字体属性的 {@link SimpleAttributeSet} 对象。
	 *
	 * @param fontFamily 字体家族名称，用于设置文本的字体家族。
	 * @param fontSize   字体大小，用于设置文本的字体大小。
	 * @param fontStyle  字体样式，可以是 {@link Font#PLAIN}, {@link Font#BOLD}, 或 {@link Font#ITALIC}，用于设置文本的字体样式。
	 * @param colorFore  文本前景色，用于设置文本的颜色。
	 * @param colorBack  文本背景色，用于设置文本的背景颜色。
	 * @return 返回一个具有指定字体属性的 {@link SimpleAttributeSet} 对象。
	 */
	public static SimpleAttributeSet getSimpleAttributeSet(String fontFamily, int fontSize, int fontStyle,
	                                                       Color colorFore, Color colorBack) {
		// 初始化一个SimpleAttributeSet对象，用于存储字体属性
		SimpleAttributeSet sas = new SimpleAttributeSet();
		// 设置字体名称
		StyleConstants.setFontFamily(sas, fontFamily);
		// 设置字体大小
		StyleConstants.setFontSize(sas, fontSize);
		// 根据fontStyle参数设置字体样式，支持粗体和斜体
		switch (fontStyle) {
			case Font.ITALIC -> StyleConstants.setItalic(sas, true);
			case Font.BOLD -> StyleConstants.setBold(sas, true);
		}
		// 设置文本的前景色
		StyleConstants.setForeground(sas, colorFore);
		// 设置文本的背景色
		StyleConstants.setBackground(sas, colorBack);
		// 返回设置好属性的SimpleAttributeSet对象
		return sas;
	}

	/**
	 * 将 {@link QRActionRegister} 列表使用 {@code obj} 参数运行，会检查其内容是否为空
	 *
	 * <p>执行前会复制一份列表，允许回调过程中增删原列表中的动作。单个动作抛异常只会打印堆栈，
	 * 不会阻断后续动作执行。</p>
	 *
	 * @param list 任务列表
	 * @param obj  传递给每个动作的参数
	 */
	public static <T> void runActions(List<QRActionRegister<T>> list, T obj) {
		if (list == null || list.isEmpty()) {
			return;
		}
		ArrayList<QRActionRegister<T>> temp = new ArrayList<>(list);
		temp.forEach(e -> {
			//确保每个都能完成而不影响之后的事件
			try {
				e.action(obj);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	/**
	 * 在GUI的事件调度线程中异步执行一系列动作。
	 * 此方法用于将一系列操作推迟到GUI线程中执行，以确保这些操作不会阻塞当前线程，
	 * 并且能够正确地与GUI组件交互。
	 *
	 * @param list 操作注册表列表，包含待执行的操作。
	 * @param obj  传递给每个操作的对象，用于执行操作时使用。
	 */
	public static <T> void runActionsLater(List<QRActionRegister<T>> list, T obj) {
		// 检查列表是否为空，以避免无意义的线程调度
		if (list == null || list.isEmpty()) {
			return;
		}

		// 创建列表的副本，以避免在异步操作中修改原始列表
		ArrayList<QRActionRegister<T>> temp = new ArrayList<>(list);
		// 使用SwingUtilities的invokeLater方法将操作推迟到GUI线程中执行
		SwingUtilities.invokeLater(() -> {
			// 遍历副本列表中的每个操作，并异步执行
			temp.forEach(e -> {
				// 尝试执行操作，并捕获任何异常，以防止线程中断
				// 确保每个都能完成而不影响之后的事件
				try {
					e.action(obj);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		});
	}

	/**
	 * 如果设置背景图片，调用此方法。窗体刷新会延迟 30 毫秒
	 *
	 * <p>设置项连续变化时优先使用该方法，避免立即重绘过于频繁。方法内部会查找
	 * {@code com} 所在的顶层窗口并调用重绘。</p>
	 *
	 * @param com 窗体内的一控件
	 */
	public static void windowFresh(JComponent com) {
		if (com == null) {
			return;
		}
		runLater(30L, e -> windowFreshRightNow(com));
	}

	/**
	 * 如果设置背景图片，调用此方法
	 *
	 * <p>当背景图、透明度或组件不透明状态已经更新，并且需要马上刷新窗口时调用。
	 * 如果只是在设置面板中响应滑块/复选框变化，通常使用 {@link #windowFresh(JComponent)}。</p>
	 *
	 * @param com 窗体内的一控件
	 */
	public static void windowFreshRightNow(JComponent com) {
		Window w = SwingUtilities.getWindowAncestor(com);
		if (w != null) {
			w.repaint();
		}
	}

	/**
	 * 使用定时器延迟执行指定动作。
	 * <p>
	 * 本方法通过创建一个 {@link Timer} 对象，并安排一个 {@link TimerTask} 在指定的延迟后执行。
	 * 注意：动作运行在 Timer 线程，不是 EDT；如果动作会读写 Swing 组件，应在动作内部使用
	 * {@link #runOnEdt(Runnable)} 或 {@link SwingUtilities#invokeLater(Runnable)}。
	 *
	 * @param millis 延迟执行的毫秒数，从现在开始计时。
	 * @param e      注册的操作接口，包含待执行的具体操作。该操作的参数是 {@code null}
	 * @see #runLater(long, QRActionRegister, Object)
	 */
	public static void runLater(long millis, QRActionRegister<Object> e) {
		runLater(millis, e, null);
	}


	/**
	 * 使用定时器延迟执行指定动作。
	 * <p>
	 * 本方法通过创建一个 {@link Timer} 对象，并安排一个 {@link TimerTask} 在指定的延迟后执行。
	 * 注意：动作运行在 Timer 线程，不是 EDT；如果动作会读写 Swing 组件，应在动作内部使用
	 * {@link #runOnEdt(Runnable)} 或 {@link SwingUtilities#invokeLater(Runnable)}。
	 *
	 * @param millis 延迟执行的毫秒数，从现在开始计时。
	 * @param e      注册的操作接口，包含待执行的具体操作。
	 * @param param  传递给动作的参数，用于在动作执行时提供必要的数据。
	 */
	public static <T> void runLater(long millis, QRActionRegister<T> e, T param) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				e.action(param);
			}
		}, millis);
	}

	/**
	 * 递归设置组件树中 {@link JComponent} 的不透明状态。
	 *
	 * <p>开启窗口背景图片时，通常需要把面板、按钮容器等组件设为透明，背景图才能透出；
	 * 关闭背景图或需要恢复普通实色界面时，可传入 {@code true}。</p>
	 *
	 * <p>该方法会从给定组件向下遍历，遇到 {@link QRPanel} 时继续递归其子组件。</p>
	 *
	 * @param com    遍历起点
	 * @param opaque 目标不透明状态
	 */
	public static void componentLoopToSetOpaque(JComponent com, boolean opaque) {
		QRActionRegister<Component> action = e -> {
			if (e instanceof JComponent jComponent) {
				jComponent.setOpaque(opaque);
			}
		};
		QRActionRegister<Component> panel = new QRActionRegister<>() {
			@Override
			public void action(Component e) {
				if (e instanceof JComponent jComponent) {
					action.action(jComponent);
					if (jComponent.getComponentCount() > 0) {
						QRComponentUtils.componentLoop(jComponent, JComponent.class, this, action);
					}
				}
			}
		};
		QRComponentUtils.componentLoop(com, QRPanel.class, panel, action);
	}

	/**
	 * 遍历组件数组，对每个组件执行不同的操作取决于它是否是 {@code aClass} 的实例。
	 *
	 * @param jc            容器面板，从中获取组件数组进行遍历。
	 * @param aClass        要匹配的类，用于判断组件是否属于该类，该类是 {@link JComponent} 的子类
	 * @param isClassAction 可为 {@code null}，如果组件是 {@code aClass} 的实例，将调用此操作接口，该操作参数是对应的实例 {@link Component}
	 */
	public static <T> void componentLoop(JComponent jc, Class<T> aClass, QRActionRegister<T> isClassAction) {
		// 获取控件里的所有组件
		Component[] components = jc.getComponents();
		// 遍历所有组件
		for (Component com : components) {
			// 判断组件是否为 aClass 的实例
			if (aClass.isInstance(com)) {
				// 如果是指定 aClass 的实例，调用 isClassAction
				if (isClassAction != null) {
					isClassAction.action(aClass.cast(com));
				}
			}
		}
	}

	/**
	 * 遍历组件数组，对每个组件执行不同的操作取决于它是否是 {@code aClass} 的实例。
	 *
	 * @param jc            容器面板，从中获取组件数组进行遍历。
	 * @param aClass        要匹配的类，用于判断组件是否属于该类，该类是 {@link JComponent} 的子类
	 * @param isClassAction 可为 {@code null}，如果组件是 {@code aClass} 的实例，将调用此操作接口，该操作参数是对应的实例 {@link Component}
	 * @param elseAction    可为 {@code null}，如果组件不是 {@code aClass} 的实例，将调用此操作接口，该操作参数是对应的实例 {@link Component}
	 */
	public static void componentLoop(JComponent jc, Class<?> aClass, QRActionRegister<Component> isClassAction, QRActionRegister<Component> elseAction) {
		// 获取控件里的所有组件
		Component[] components = jc.getComponents();
		// 遍历所有组件
		for (Component com : components) {
			// 判断组件是否为 aClass 的实例
			if (aClass.isInstance(com)) {
				// 如果是指定 aClass 的实例，调用 isClassAction
				if (isClassAction != null) {
					isClassAction.action(com);
				}
			} else {
				// 如果不是 aClass 的实例，调用 elseAction
				if (elseAction != null) {
					elseAction.action(com);
				}
			}
		}
	}


	/**
	 * 设置颜色的透明度（alpha 值）。
	 * <p>
	 * 该方法通过接收一个已有颜色对象和一个新的透明度值，创建并返回一个新的颜色对象。
	 * 新颜色对象的RGB值与输入颜色对象相同，但透明度（alpha）被更新为指定的新值。
	 * 透明度值被限制在0到255之间，以确保颜色值在整数范围内，适合颜色对象的构造函数。
	 *
	 * @param color 原始颜色对象，其RGB值将被新颜色对象继承。
	 * @param alpha 新的透明度值，范围为0.0（完全透明）到1.0（完全不透明）。
	 * @return 一个新的颜色对象，具有指定的透明度。
	 */
	public static Color setColorAlpha(Color color, float alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255));
	}

	/**
	 * 确保动作在 Swing EDT 上执行。
	 *
	 * <p>如果当前线程已经是 EDT，会立即执行；否则通过 {@link SwingUtilities#invokeLater(Runnable)}
	 * 异步排队执行。后台任务、Timer 回调或文件扫描线程需要更新 UI 时应使用该方法。</p>
	 *
	 * @param runnable 要在 EDT 执行的动作
	 */
	public static void runOnEdt(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
		}
	}

	/**
	 * 启动后台任务并显示进度对话框。
	 *
	 * <pre>{@code
	 * QRComponentUtils.run(
	 *     owner, "正在生成文件...",
	 *     progress -> {
	 *         for (int i = 0; i < 100; i++) {
	 *             Thread.sleep(10);
	 *             progress.accept(i + 1, 100);
	 *         }
	 *         return new File("result.xlsx");
	 *     },
	 *     file -> System.out.println("导出成功: " + file),
	 *     err -> System.err.println("导出失败: " + err)
	 * );
	 * }</pre>
	 *
	 * @param <T>         任务结果类型
	 * @param owner       父窗口，用于对话框定位
	 * @param description 进度描述文字
	 * @param task        后台任务，通过 {@link ProgressTask#run(BiConsumer)} 接收进度回调
	 * @param onSuccess   任务成功回调（EDT 执行）
	 * @param onError     任务失败回调（EDT 执行，参数为异常消息）
	 */
	@Deprecated
	public static <T> void run(Window owner,
	                           String description,
	                           ProgressTask<T> task,
	                           Consumer<T> onSuccess,
	                           Consumer<String> onError) {
		QRTaskRunner.runWithProgress(owner, description,
				context -> task.run(context::progress),
				onSuccess,
				throwable -> {
					if (onError != null) {
						onError.accept(throwable == null ? null : throwable.getMessage());
					}
				});
	}

	/**
	 * 将常用按键参数解析为 {@link KeyStroke} 列表。
	 *
	 * <p>支持 {@link KeyStroke}、{@link String}、{@link Integer} 和 {@code int[]}。
	 * 字符串可用英文逗号分隔多个按键，例如 {@code "shift b, ctrl b"}。
	 * 无法识别或解析失败的参数会被跳过，重复按键只保留第一次出现的位置。</p>
	 *
	 * @param keys 按键参数
	 * @return 解析后的按键列表
	 */
	public static List<KeyStroke> parseKeyStrokes(Object... keys) {
		ArrayList<KeyStroke> keyStrokes = new ArrayList<>();
		Set<String> values = new HashSet<>();
		if (keys == null) {
			return keyStrokes;
		}
		for (Object key : keys) {
			if (key instanceof KeyStroke keyStroke) {
				addKeyStroke(keyStrokes, values, keyStroke);
			} else if (key instanceof String str) {
				for (String split : str.split(",")) {
					addKeyStroke(keyStrokes, values, QRStringUtils.getKeyStroke(split));
				}
			} else if (key instanceof int[] valuesArray) {
				addKeyStroke(keyStrokes, values, QRStringUtils.getKeyStroke(valuesArray));
			} else if (key instanceof Integer value) {
				addKeyStroke(keyStrokes, values, QRStringUtils.getKeyStroke(value));
			}
		}
		return keyStrokes;
	}

	private static void addKeyStroke(List<KeyStroke> keyStrokes, Set<String> values, KeyStroke keyStroke) {
		String value = keyStroke == null ? null : QRStringUtils.getKeyStrokeValue(keyStroke);
		if (value != null && values.add(value)) {
			keyStrokes.add(keyStroke);
		}
	}

	/**
	 * 带进度回调的后台任务接口。
	 *
	 * @param <T> 任务结果类型
	 */
	@FunctionalInterface
	public interface ProgressTask<T> {
		/**
		 * 执行后台任务。
		 *
		 * @param progress 进度回调，参数为 (current, total)
		 * @return 任务结果
		 * @throws Exception 任何执行异常
		 */
		T run(BiConsumer<Integer, Integer> progress) throws Exception;
	}
}
package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QR Swing 的主题列表控件。
 *
 * <p>该类基于 {@link JList}，维护一份可直接操作的 {@link #contents()} 列表，
 * 并提供添加、删除、清空、去重控制、鼠标事件封装和滚动面板创建能力。
 * 默认使用单选模式。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRList&lt;String&gt; list = new QRList&lt;&gt;(List.of("项目 A", "项目 B"));
 * list.setRepeatable(false);
 * list.addItem("项目 C");
 * list.addMouseListener(QRMouseListener.TYPE.CLICK, event -> {
 *     if (event.getClickCount() == 2) {
 *         open(list.getSelectedValue());
 *     }
 * });
 * panel.add(list.addScrollPane());
 * </code></pre>
 *
 * @param <T> 列表元素类型
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-21 21:59
 **/
public class QRList<T> extends JList<T> implements QRComponentUpdate {
	protected QRScrollPane scrollPane;
	protected final LinkedList<T> contents = new LinkedList<>();
	/**
	 * true 表示不允许重复元素。
	 */
	protected boolean noRepeat;
	private QRMouseMotionListener mouseMotionListener;
	private QRMouseListener mouseListener;

	/**
	 * 默认只能选择一个
	 */
	public QRList() {
		personalize();
	}

	/**
	 * 默认只能选择一个
	 *
	 * @param contents 列表内容
	 */
	public QRList(T[] contents) {
		setContents(contents);
		personalize();
	}

	/**
	 * 默认只能选择一个
	 *
	 * @param contents 列表内容
	 */
	public QRList(List<T> contents) {
		setContents(contents);
		personalize();
	}

	public QRList(ListModel<T> dataModel) {
		super(dataModel);
		personalize();
	}

	private void personalize() {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addListSelectionListener(this::listSelectedAction);
		componentFresh();
	}

	/**
	 * 设置列表是否可以含有可重元素
	 *
	 * @param repeatable 为 {@code true} 则可重
	 */
	public void setRepeatable(boolean repeatable) {
		this.noRepeat = !repeatable;
	}

	/**
	 * 列表选择变化回调。
	 *
	 * <p>子类可重写该方法；外部调用方也可以直接使用原生 {@link #addListSelectionListener(javax.swing.event.ListSelectionListener)}。</p>
	 */
	protected void listSelectedAction(ListSelectionEvent listSelectionEvent) {
	}

	/**
	 * 取得当前选中项的字符串表示。
	 *
	 * @return 当前选中项字符串；未选中时返回字符串 {@code "null"}
	 */
	public String getSelected() {
		return String.valueOf(getSelectedValue());
	}

	/**
	 * 获取列表的大小
	 *
	 * @return 返回列表的大小
	 */
	public int getListSize() {
		return getModel().getSize();
	}

	/**
	 * @return 默认 {@link List} 的实例是 {@link LinkedList}
	 */
	public List<T> contents() {
		return this.contents;
	}

	/**
	 * 移除指定索引的元素并刷新模型。
	 *
	 * @param index 元素索引
	 */
	public void removeItem(int index) {
		this.contents.remove(index);
		contentUpdate();
	}

	/**
	 * 移除指定元素并刷新模型。
	 *
	 * @param item 要移除的元素
	 */
	public void removeItem(T item) {
		this.contents.remove(item);
		contentUpdate();
	}

	/**
	 * 在末尾添加元素。
	 *
	 * @param item 要添加的元素
	 * @return 添加成功时返回插入索引；因去重规则被拒绝时返回 -1
	 */
	public int addItem(T item) {
		int size = this.contents.size();
		return addItem(size, item) ? size : -1;
	}

	/**
	 * 在列表开头添加元素。
	 *
	 * @param item 要添加的元素
	 * @return 是否添加成功
	 */
	public boolean addFirst(T item) {
		return addItem(0, item);
	}

	/**
	 * 在指定位置添加元素。
	 *
	 * <p>如果已开启不允许重复元素，且列表中已有该元素，则不会添加并返回 false。</p>
	 *
	 * @param index 插入位置
	 * @param item  要添加的元素
	 * @return 是否添加成功
	 */
	public boolean addItem(int index, T item) {
		if (this.noRepeat && this.contents.contains(item)) {
			return false;
		}
		this.contents.add(index, item);
		contentUpdate();
		return true;
	}

	/**
	 * 使用数组替换列表内容。
	 *
	 * @param contents 新内容
	 */
	public void setContents(T[] contents) {
		setContents(Arrays.asList(contents));
	}

	/**
	 * 使用列表替换当前内容。
	 *
	 * <p>如果开启了不允许重复元素，会保留输入列表中每个元素首次出现的位置。</p>
	 *
	 * @param contents 新内容
	 */
	public void setContents(List<T> contents) {
		this.contents.clear();
		if (this.noRepeat) {
			for (T content : contents) {
				if (!this.contents.contains(content)) {
					this.contents.add(content);
				}
			}
		} else {
			this.contents.addAll(contents);
		}
		contentUpdate();
	}

	/**
	 * 清空列表内容并刷新模型。
	 */
	public void clear() {
		this.contents.clear();
		contentUpdate();
	}

	/**
	 * 用当前 {@link #contents} 重建列表模型。
	 *
	 * <p>直接修改 {@link #contents()} 返回的列表后，需要调用该方法让界面刷新。</p>
	 */
	public final void contentUpdate() {
		setModel(new AbstractListModel<>() {
			@Override
			public int getSize() {
				return contents.size();
			}

			@Override
			public T getElementAt(int i) {
				return contents.get(i);
			}
		});
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		Container parent = getParent();
		return parent instanceof JViewport && parent.getHeight() > getPreferredSize().height;
	}

	/**
	 * 添加鼠标移动事件
	 */
	public final void addMouseMotionListener() {
		if (this.mouseMotionListener == null) {
			this.mouseMotionListener = new QRMouseMotionListener();
			this.mouseMotionListener.add(QRMouseMotionListener.TYPE.DRAG, this::mouseDrag);
			this.mouseMotionListener.add(QRMouseMotionListener.TYPE.MOVE, this::mouseMove);
			addMouseMotionListener(this.mouseMotionListener);
		}
	}

	/**
	 * 添加鼠标移动事件
	 * 已自动添加 {@link #addMouseMotionListener()}
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	public final void addMouseMotionListener(QRMouseMotionListener.TYPE type, QRActionRegister<MouseEvent> ar) {
		if (this.mouseMotionListener == null) {
			addMouseMotionListener();
		}
		if (this.mouseMotionListener != null) {
			this.mouseMotionListener.add(type, ar);
		}
	}

	/**
	 * 添加鼠标事件
	 */
	public final void addMouseListener() {
		if (this.mouseListener == null) {
			this.mouseListener = new QRMouseListener();
			this.mouseListener.add(QRMouseListener.TYPE.CLICK, this::mouseClick);
			this.mouseListener.add(QRMouseListener.TYPE.PRESS, this::mousePress);
			this.mouseListener.add(QRMouseListener.TYPE.RELEASE, this::mouseRelease);
			this.mouseListener.add(QRMouseListener.TYPE.ENTER, this::mouseEnter);
			this.mouseListener.add(QRMouseListener.TYPE.EXIT, this::mouseExit);
			addMouseListener(this.mouseListener);

		}
	}

	/**
	 * 添加鼠标事件
	 * 已自动添加 {@link #addMouseListener()}
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	public final void addMouseListener(QRMouseListener.TYPE type, QRActionRegister<MouseEvent> ar) {
		if (this.mouseListener == null) {
			addMouseListener();
		}
		if (this.mouseListener != null) {
			this.mouseListener.add(type, ar);
		}
	}

	/**
	 * 添加滚动条
	 *
	 * @return 承载当前列表的滚动面板；重复调用返回同一实例
	 */
	public JScrollPane addScrollPane() {
		if (this.scrollPane == null) {
			this.scrollPane = new QRScrollPane();
			this.scrollPane.setViewportView(this);
		}
		return this.scrollPane;
	}


	/**
	 * 重写前请先调用 {@link #addMouseMotionListener()}
	 */
	protected void mouseDrag(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseMotionListener()}
	 */
	protected void mouseMove(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mouseClick(MouseEvent e) {

	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mousePress(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mouseRelease(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mouseEnter(MouseEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addMouseListener()}
	 */
	protected void mouseExit(MouseEvent e) {
	}

	//region 取得监听器

	public QRMouseMotionListener mouseMotionListener() {
		return mouseMotionListener;
	}

	public QRMouseListener mouseListener() {
		return mouseListener;
	}

	//endregion

	@Override
	public JToolTip createToolTip() {
		QRToolTip tip = new QRToolTip();
		tip.setComponent(tip);
		return tip;
	}

	@Override
	public void componentFresh() {
		setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
		setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
		setBackground(QRColorsAndFonts.TEXT_COLOR_BACK);
		setSelectionBackground(QRColorsAndFonts.PRESS_COLOR);
	}
}
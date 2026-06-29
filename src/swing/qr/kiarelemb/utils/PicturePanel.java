package swing.qr.kiarelemb.utils;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.basic.QRScrollPane;
import swing.qr.kiarelemb.inter.QRActionRegister;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

/**
 * 可拖拽、可缩放的图片预览面板。
 *
 * <p>该面板直接维护图片的缩放比例和内部平移量，不依赖 {@link JScrollPane}。
 * 鼠标左键拖拽可平移图片；Ctrl + 鼠标滚轮缩放；普通滚轮纵向平移；Shift + 滚轮横向平移。
 * 面板加入容器时会注册 Ctrl++、Ctrl+-、Ctrl+0 全局快捷键，移除时自动注销。</p>
 *
 * <p>使用例：
 * <pre><code>
 * PicturePanel panel = new PicturePanel(image, new Dimension(800, 600));
 * panel.setZoomRange(0.2, 5.0);
 * panel.setMouseWheelZoomStep(0.15);
 * panel.setZoom(1.5);
 * </code></pre>
 */
public class PicturePanel extends QRPanel {
	private static final boolean DEBUG_PAN = false;
	protected BufferedImage image;
	protected Dimension pictureSize;
	private double zoom = 1.0;
	private double minZoom = 0.1;
	private double maxZoom = 4.0;
	private double mouseWheelZoomStep = 0.1;
	private int panX;
	private int panY;
	private Point dragStart;
	private boolean shortcutsRegistered;
	private final QRActionRegister<KeyStroke> zoomInAction = e -> setZoom(zoom + mouseWheelZoomStep);
	private final QRActionRegister<KeyStroke> zoomOutAction = e -> setZoom(zoom - mouseWheelZoomStep);
	private final QRActionRegister<KeyStroke> zoomResetAction = e -> setZoom(1.0);

	public PicturePanel() {
		this(null, null);
	}

	public PicturePanel(BufferedImage image, Dimension pictureSize) {
		this.image = image;
		this.pictureSize = pictureSize == null ? imageSize(image) : pictureSize;
		if (this.pictureSize != null) {
			setPreferredSize(this.pictureSize);
		}
		addMouseListener();
		addMouseMotionListener();
		addMouseWheelListener();
	}

	/**
	 * 注册全局快捷键：Ctrl++ 放大、Ctrl+- 缩小、Ctrl+0 重置缩放。
	 */
	private void registerShortcuts() {
		if (shortcutsRegistered) {
			return;
		}
		// ── 放大 Ctrl+= / Ctrl+Shift+= / 小键盘 Ctrl+Add ──
		QRSwing.registerGlobalAction(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK, zoomInAction, false);
		QRSwing.registerGlobalAction(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, zoomInAction, false);
		QRSwing.registerGlobalAction(KeyEvent.VK_ADD, InputEvent.CTRL_DOWN_MASK, zoomInAction, false);
		// ── 缩小 Ctrl+- / 小键盘 Ctrl+Subtract ──
		QRSwing.registerGlobalAction(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK, zoomOutAction, false);
		QRSwing.registerGlobalAction(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_DOWN_MASK, zoomOutAction, false);
		// ── 重置缩放 Ctrl+0 ──
		QRSwing.registerGlobalAction(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK, zoomResetAction, false);
		shortcutsRegistered = true;
	}

	/**
	 * 移除全局快捷键。
	 */
	private void unregisterShortcuts() {
		if (!shortcutsRegistered) {
			return;
		}
		QRSwing.registerGlobalActionRemove(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK), zoomInAction, false);
		QRSwing.registerGlobalActionRemove(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK), zoomInAction, false);
		QRSwing.registerGlobalActionRemove(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, InputEvent.CTRL_DOWN_MASK), zoomInAction, false);
		QRSwing.registerGlobalActionRemove(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK), zoomOutAction, false);
		QRSwing.registerGlobalActionRemove(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_DOWN_MASK), zoomOutAction, false);
		QRSwing.registerGlobalActionRemove(KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_DOWN_MASK), zoomResetAction, false);
		shortcutsRegistered = false;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		registerShortcuts();
	}

	@Override
	public void removeNotify() {
		unregisterShortcuts();
		super.removeNotify();
	}

	/**
	 * 设置当前显示图片和预览尺寸，并重置缩放和平移状态。
	 *
	 * @param image       新图片，可为 null
	 * @param pictureSize 图片基础显示尺寸；为 null 时使用图片原始尺寸
	 */
	public void setImage(BufferedImage image, Dimension pictureSize) {
		this.image = image;
		this.pictureSize = pictureSize == null ? imageSize(image) : pictureSize;
		resetView();
		updatePreferredSize();
		revalidate();
		clampPan();
		repaint();
	}

	/**
	 * 设置缩放比例。
	 *
	 * <p>缩放锚点使用当前鼠标位置或面板中心，最终值会被限制在 {@link #setZoomRange(double, double)}
	 * 设置的范围内。</p>
	 *
	 * @param zoom 缩放比例，1.0 表示 100%
	 */
	public void setZoom(double zoom) {
		setZoom(zoom, zoomAnchor());
	}

	/**
	 * 以指定锚点设置缩放比例。
	 *
	 * <p>锚点用于在缩放后尽量保持该屏幕位置对应的图片内容不变。</p>
	 *
	 * @param zoom   缩放比例，1.0 表示 100%
	 * @param anchor 面板坐标中的锚点，可为 null
	 */
	public void setZoom(double zoom, Point anchor) {
		double oldZoom = this.zoom;
		double newZoom = limitZoom(zoom);
		if (Double.compare(oldZoom, newZoom) == 0) {
			return;
		}
		DrawBox oldBox = pictureBox();
		debugPan("setZoom.before", oldBox, anchor);
		this.zoom = newZoom;
		if (resizeWithZoom()) {
			updatePreferredSize();
		}
		revalidate();
		keepAnchorAfterZoom(oldBox, anchor);
		debugPan("setZoom.afterAnchor", pictureBox(), anchor);
		clampPan();
		debugPan("setZoom.afterClamp", pictureBox(), anchor);
		repaint();
		zoomChanged(this.zoom);
	}

	/**
	 * @return 当前缩放比例，1.0 表示 100%
	 */
	public double zoom() {
		return zoom;
	}

	/**
	 * 设置允许的缩放范围。
	 *
	 * @param minZoom 最小缩放比例，必须大于 0
	 * @param maxZoom 最大缩放比例，必须大于等于 {@code minZoom}
	 */
	public void setZoomRange(double minZoom, double maxZoom) {
		if (minZoom <= 0 || maxZoom < minZoom) {
			throw new IllegalArgumentException("Invalid zoom range: " + minZoom + " - " + maxZoom);
		}
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		setZoom(zoom);
	}

	/**
	 * 设置鼠标滚轮和快捷键每次缩放的步长。
	 *
	 * @param mouseWheelZoomStep 缩放步长，必须大于 0
	 */
	public void setMouseWheelZoomStep(double mouseWheelZoomStep) {
		if (mouseWheelZoomStep <= 0) {
			throw new IllegalArgumentException("Invalid mouse wheel zoom step: " + mouseWheelZoomStep);
		}
		this.mouseWheelZoomStep = mouseWheelZoomStep;
	}

	/**
	 * @return 当前横向平移量
	 */
	public int panX() {
		return panX;
	}

	/**
	 * @return 当前纵向平移量
	 */
	public int panY() {
		return panY;
	}

	/**
	 * 设置图片平移量。
	 *
	 * <p>设置后会自动限制在可见范围内。</p>
	 *
	 * @param x 横向平移量
	 * @param y 纵向平移量
	 */
	public void setPan(int x, int y) {
		this.panX = x;
		this.panY = y;
		clampPan();
		repaint();
	}

	/**
	 * 重置平移状态。
	 *
	 * <p>该方法不会重置缩放比例，只把拖拽状态和平移量清零。</p>
	 */
	public void resetView() {
		panX = 0;
		panY = 0;
		dragStart = null;
		clampPan();
	}

	@Override
	protected void mouseEnter(MouseEvent e) {
		setCursorHand();
	}

	@Override
	protected void mouseExit(MouseEvent e) {
		if (!dragging()) {
			setCursorDefault();
		}
	}

	/**
	 * 图片面板不再设计为放入 {@link JScrollPane} / {@link QRScrollPane} 中使用。
	 * <p>
	 * 早期实现会在检测到父容器是 {@link JViewport} 时，通过
	 * {@code viewport.setViewPosition(...)} 移动画面；否则通过内部的
	 * {@code panX}/{@code panY} 移动画面。两套坐标逻辑会让拖拽、鼠标锚点缩放
	 * 和 slider 中心缩放的行为更复杂，也更容易在不同容器中出现不一致。
	 * <p>
	 * 现在 {@code PicturePanel} 只维护自身内部平移量：拖拽修改 {@code panX}/{@code panY}，
	 * 缩放时按鼠标点或面板中心反推新的平移量。因此不需要也不应再为它添加滚动面板。
	 *
	 * @throws UnsupportedOperationException 始终抛出，提示该面板不支持滚动容器用法
	 * @deprecated 直接把 {@code PicturePanel} 添加到普通容器中使用。
	 */
	@Deprecated
	@Override
	public QRScrollPane addScrollPane() {
		throw new UnsupportedOperationException("无需为此图片面板添加滚动条");
	}

	@Override
	protected void mousePress(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}
		dragStart = e.getPoint();
		setCursorHand();
	}

	@Override
	protected void mouseDrag(MouseEvent e) {
		dragPanel(e);
	}

	@Override
	protected void mouseRelease(MouseEvent e) {
		dragStart = null;
		setCursorHand();
	}

	@Override
	protected void mouseWheel(MouseWheelEvent e) {
		if (e.isControlDown()) {
			e.consume();
			double direction = e.getWheelRotation() < 0 ? 1 : -1;
			setZoom(zoom + direction * mouseWheelZoomStep, e.getPoint());
			return;
		}
		int step = e.getWheelRotation() * 40;
		if (e.isShiftDown()) {
			panX -= step;
		} else {
			panY -= step;
		}
		clampPan();
		repaint();
	}

	protected Dimension pictureSize() {
		return pictureSize == null ? new Dimension(1, 1) : pictureSize;
	}

	protected DrawBox pictureBox() {
		Dimension size = pictureSize();
		int width = Math.max(1, (int) Math.round(size.width * zoom));
		int height = Math.max(1, (int) Math.round(size.height * zoom));
		return new DrawBox(panX, panY, width, height, Math.max(1, size.width), Math.max(1, size.height));
	}

	protected void paintPicture(Graphics2D g2, DrawBox box) {
		if (image != null) {
			g2.drawImage(image, box.x(), box.y(), box.w(), box.h(), null);
		}
	}

	protected void paintPictureOverlay(Graphics2D g2, DrawBox box) {
	}

	protected boolean resizeWithZoom() {
		return true;
	}

	protected void zoomChanged(double zoom) {
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			DrawBox box = pictureBox();
			paintPicture(g2, box);
			paintPictureOverlay(g2, box);
		} finally {
			g2.dispose();
		}
	}

	private void dragPanel(MouseEvent e) {
		if (dragStart == null) {
			return;
		}
		Point point = e.getPoint();
		debugPan("drag.before", pictureBox(), point);
		panX += point.x - dragStart.x;
		panY += point.y - dragStart.y;
		dragStart = point;
		debugPan("drag.afterMove", pictureBox(), point);
		clampPan();
		debugPan("drag.afterClamp", pictureBox(), point);
		repaint();
	}

	private void clampPan() {
		if (pictureSize == null) {
			debugPan("clamp.skip.noPicture", null, null);
			return;
		}
		DrawBox box = pictureBox();
		int imgW = box.w();
		int imgH = box.h();
		int panelW = getWidth();
		int panelH = getHeight();
		if (panelW <= 0 || panelH <= 0) {
			debugPan("clamp.skip.noPanelSize", box, null);
			return;
		}
		int beforeX = panX;
		int beforeY = panY;
		int minBoxX = imgW <= panelW ? (panelW - imgW) / 2 : panelW - imgW;
		int maxBoxX = imgW <= panelW ? (panelW - imgW) / 2 : 0;
		int minBoxY = imgH <= panelH ? (panelH - imgH) / 2 : panelH - imgH;
		int maxBoxY = imgH <= panelH ? (panelH - imgH) / 2 : 0;
		int clampedBoxX = Math.min(maxBoxX, Math.max(box.x(), minBoxX));
		int clampedBoxY = Math.min(maxBoxY, Math.max(box.y(), minBoxY));
		panX += clampedBoxX - box.x();
		panY += clampedBoxY - box.y();
		DrawBox clampedBox = pictureBox();
		if (DEBUG_PAN) {
			System.out.printf(
					"[PicturePanel] clamp: zoom=%.2f panel=%dx%d preferred=%dx%d picture=%dx%d image=%dx%d "
							+ "panX=%d->%d panY=%d->%d box=(%d,%d,%d,%d)->(%d,%d,%d,%d) "
							+ "rangeX=[%d,%d] rangeY=[%d,%d]%n",
					zoom, panelW, panelH, getPreferredSize().width, getPreferredSize().height,
					pictureSize.width, pictureSize.height, imgW, imgH,
					beforeX, panX, beforeY, panY, box.x(), box.y(), box.w(), box.h(),
					clampedBox.x(), clampedBox.y(), clampedBox.w(), clampedBox.h(),
					minBoxX, maxBoxX, minBoxY, maxBoxY);
		}
	}

	private void keepAnchorAfterZoom(DrawBox oldBox, Point anchor) {
		if (anchor == null || oldBox.w() <= 0 || oldBox.h() <= 0) {
			return;
		}
		double imageXRatio = (anchor.x - oldBox.x()) / (double) oldBox.w();
		double imageYRatio = (anchor.y - oldBox.y()) / (double) oldBox.h();
		DrawBox newBox = pictureBox();
		if (DEBUG_PAN) {
			System.out.printf(
					"[PicturePanel] keepAnchor: anchor=(%d,%d) oldBox=%s newBox=%s ratio=(%.4f,%.4f)%n",
					anchor.x, anchor.y, oldBox, newBox, imageXRatio, imageYRatio);
		}
		panX += (int) Math.round(anchor.x - (newBox.x() + newBox.w() * imageXRatio));
		panY += (int) Math.round(anchor.y - (newBox.y() + newBox.h() * imageYRatio));
	}

	private void debugPan(String tag, DrawBox box, Point point) {
		if (!DEBUG_PAN) {
			return;
		}
		Dimension preferred = getPreferredSize();
		System.out.printf(
				"[PicturePanel] %s: zoom=%.2f panel=%dx%d preferred=%dx%d picture=%s box=%s pan=(%d,%d) point=%s parent=%s%n",
				tag, zoom, getWidth(), getHeight(), preferred.width, preferred.height, pictureSize, box, panX, panY,
				point, getParent() == null ? null : getParent().getClass().getName());
	}

	private Point zoomAnchor() {
		return new Point(getWidth() / 2, getHeight() / 2);
	}

	private double limitZoom(double zoom) {
		return Math.max(minZoom, Math.min(maxZoom, zoom));
	}

	private void updatePreferredSize() {
		if (pictureSize == null) {
			return;
		}
		int width = Math.max(1, (int) Math.round(pictureSize.width * zoom));
		int height = Math.max(1, (int) Math.round(pictureSize.height * zoom));
		setPreferredSize(new Dimension(width, height));
	}

	private boolean dragging() {
		return dragStart != null;
	}

	private static Dimension imageSize(BufferedImage image) {
		return image == null ? null : new Dimension(image.getWidth(), image.getHeight());
	}

	public record DrawBox(int x, int y, int w, int h, int baseW, int baseH) {
	}
}

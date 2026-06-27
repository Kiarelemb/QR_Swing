package swing.qr.kiarelemb.utils;

import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.basic.QRScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

/**
 * 可拖拽、可缩放的图片预览面板。
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

	public void setImage(BufferedImage image, Dimension pictureSize) {
		this.image = image;
		this.pictureSize = pictureSize == null ? imageSize(image) : pictureSize;
		resetView();
		updatePreferredSize();
		revalidate();
		clampPan();
		repaint();
	}

	public void setZoom(double zoom) {
		setZoom(zoom, zoomAnchor());
	}

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

	public double zoom() {
		return zoom;
	}

	public void setZoomRange(double minZoom, double maxZoom) {
		if (minZoom <= 0 || maxZoom < minZoom) {
			throw new IllegalArgumentException("Invalid zoom range: " + minZoom + " - " + maxZoom);
		}
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		setZoom(zoom);
	}

	public void setMouseWheelZoomStep(double mouseWheelZoomStep) {
		if (mouseWheelZoomStep <= 0) {
			throw new IllegalArgumentException("Invalid mouse wheel zoom step: " + mouseWheelZoomStep);
		}
		this.mouseWheelZoomStep = mouseWheelZoomStep;
	}

	public int panX() {
		return panX;
	}

	public int panY() {
		return panY;
	}

	public void setPan(int x, int y) {
		this.panX = x;
		this.panY = y;
		clampPan();
		repaint();
	}

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

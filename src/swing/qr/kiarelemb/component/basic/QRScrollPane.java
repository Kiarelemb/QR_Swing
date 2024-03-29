package swing.qr.kiarelemb.component.basic;

import method.qr.kiarelemb.utils.QRSleepUtils;
import method.qr.kiarelemb.utils.QRSystemUtils;
import method.qr.kiarelemb.utils.QRThreadBuilder;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.assembly.QRCaret;
import swing.qr.kiarelemb.component.assembly.QRLineNumberComponent;
import swing.qr.kiarelemb.component.assembly.QRScrollBarUI;
import swing.qr.kiarelemb.component.listener.QRMouseWheelListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 22:06
 **/
public class QRScrollPane extends JScrollPane implements QRComponentUpdate {
	protected QRScrollBar vBar;
	protected QRScrollBar hBar;
	protected QRMouseWheelListener mouseWheelListener;
	protected QRLineNumberComponent lineNumberComponent;
	/**
	 * 单次滚动休眠时间
	 */
	public static int scrollSpeed = QRSystemUtils.IS_WINDOWS ? 1 : 3;
	private final QRScrollBarUI horUI;
	private final QRScrollBarUI verUI;
	private boolean borderPaint = false;
	private JTextPane view = null;
	/**
	 * 默认单次平滑滚动的量，如果 {@link #setViewportView(Component)} 的参数是 {@link JTextPane} 的子类，且 {@link JTextPane#getCaret()} 是 {@link QRCaret} 的实例 ，则是单次平滑滚动的行数
	 */
	private int scrollLine = 30;
	private QRCaret caret = null;

	public QRScrollPane() {
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.vBar = QRScrollBar.getVerticalScrollBar();
		this.hBar = QRScrollBar.getHorizontalScrollBar();
		setHorizontalScrollBar(this.hBar);
		setVerticalScrollBar(this.vBar);
		this.horUI = this.hBar.barUi();
		this.verUI = this.vBar.barUi();
		componentFresh();
	}

	/**
	 * 各滚动条的位置复原
	 */
	public void locationFresh() {
		if (vBar.isVisible()) {
			vBar.setValue(0);
		}
		if (hBar.isVisible()) {
			hBar.setValue(0);
		}
	}

	/**
	 * 设置平滑滚动
	 */
	public void setScrollSmoothly() {
		this.vBar.setUnitIncrement(0);
		this.hBar.setUnitIncrement(0);
		getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		if (this.mouseWheelListener == null) {
			this.mouseWheelListener = new QRMouseWheelListener();
			this.mouseWheelListener.add(e -> {
				MouseWheelEvent ev = (MouseWheelEvent) e;
				ev.consume();
				mouseWheelMove(ev);
			});
			addMouseWheelListener(this.mouseWheelListener);
		}
	}

	public void addMouseWheelListener(QRActionRegister ar) {
		if (this.mouseWheelListener != null) {
			this.mouseWheelListener.add(ar);
		}
	}

	/**
	 * 设置平滑滚动
	 *
	 * @param line 单次滚动的值
	 */
	public void setScrollSmoothly(int line) {
		this.scrollLine = line;
		if (this.view != null) {
			if (this.view.getCaret() instanceof QRCaret) {
				this.caret = (QRCaret) this.view.getCaret();
			}
		}
		setScrollSmoothly();
	}

	/**
	 * 默认只有滚动文本的功能
	 *
	 * @param e 传入的事件
	 */
	private void mouseWheelMove(MouseWheelEvent e) {
		if (e.isControlDown()) {
			if (this.hBar.isVisible()) {
				ThreadPoolExecutor scroll = QRThreadBuilder.singleThread("scroll");
				scroll.submit(() -> setScrollBarRollSmoothly(this.hBar, e.getWheelRotation() < 0));
			}
		} else {
			if (this.vBar.isVisible()) {
				ThreadPoolExecutor scroll = QRThreadBuilder.singleThread("scroll");
				scroll.submit(() -> setScrollBarRollSmoothly(this.vBar, e.getWheelRotation() < 0));
			}
		}
	}

	private void setScrollBarRollSmoothly(QRScrollBar bar, boolean up) {
//        synchronized (this) {
		int value = bar.getValue();
		final int maxValue = bar.getMaximum();
		int extent0;
		if (this.caret == null) {
			extent0 = this.scrollLine;
		} else {
			extent0 = this.caret.caretHeight() * this.scrollLine;
		}
		final int range;
		int extent = 2;
		if (up) {
			range = Math.min(value, extent0);
			for (int i = 0; i < range; i += extent) {
				bar.minusValue(extent);
				QRSleepUtils.sleep(scrollSpeed);
			}
		} else {
			range = value + extent0 > maxValue ? maxValue - value : extent0;
			for (int i = 0; i < range; i += extent) {
				bar.plusValue(extent);
				QRSleepUtils.sleep(scrollSpeed);
			}
		}
	}

	/**
	 * 为 {@link QRTextPane} 添加行号
	 */
	public QRLineNumberComponent addLineNumberModelForTextPane() {
		if (this.lineNumberComponent == null && this.view != null && this.view instanceof QRTextPane) {
			this.lineNumberComponent = new QRLineNumberComponent((QRTextPane) this.view);
			this.lineNumberComponent.setAlignment(QRLineNumberComponent.RIGHT_ALIGNMENT);
			setRowHeaderView(this.lineNumberComponent);
		}
		return this.lineNumberComponent;
	}

	public void setBorderPaint(boolean borderPaint) {
		this.borderPaint = borderPaint;
	}

	public boolean borderPaint() {
		return this.borderPaint;
	}


	@Override
	public void setViewportView(Component view) {
		super.setViewportView(view);
		JViewport viewport = getViewport();
		viewport.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		if (view instanceof JTextPane) {
			this.view = (JTextPane) view;
		}
	}

	@Override
	protected void paintBorder(Graphics g) {
		if (this.borderPaint) {
			if (QRSwing.windowRound) {
				final int arc = 15;
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(QRColorsAndFonts.BORDER_COLOR);
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
			} else {
				g.setColor(QRColorsAndFonts.BORDER_COLOR);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
			return;
		}
		super.paintBorder(g);
	}

	@Override
	public void componentFresh() {

		setBorder(null);
//		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
//		setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
//		setBorder(BorderFactory.createLineBorder(QRColorsAndFonts.FRAME_COLOR_BACK, 5));
//		setBorder(null);
		setOpaque(false);
		getViewport().setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
//		setBackground(new Color(0,0,0,0));
		this.horUI.componentFresh();
		this.verUI.componentFresh();
		this.vBar.componentFresh();
		this.hBar.componentFresh();
		final Component view = getViewport().getView();
		if (view instanceof QRComponentUpdate) {
			((QRComponentUpdate) view).componentFresh();
		}
	}
}
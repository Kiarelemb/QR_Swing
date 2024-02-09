package swing.qr.kiarelemb.component.basic;

import swing.qr.kiarelemb.component.assembly.QRSplitPaneUI;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-22 11:09
 **/
public class QRSplitPane extends JSplitPane implements QRComponentUpdate {
	private boolean borderPaint = false;

	public QRSplitPane(int newOrientation) {
		super(newOrientation);
		setOpaque(false);
		setContinuousLayout(true);
		setDividerSize(10);
		Cursor cursor = new Cursor(newOrientation == JSplitPane.VERTICAL_SPLIT ? Cursor.N_RESIZE_CURSOR : Cursor.W_RESIZE_CURSOR);
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				setCursor(cursor);
			}
		});
		setOrientation(orientation);
		setUI(new QRSplitPaneUI());
		componentFresh();
	}

	public void setBorderPaint(boolean borderPaint) {
		this.borderPaint = borderPaint;
	}

	public boolean borderPaint() {
		return borderPaint;
	}

	@Override
	protected void paintBorder(Graphics g) {
		if (borderPaint) {
			super.paintBorder(g);
//			if (QRSwing.windowRound) {
//				final int arc = 15;
//				Graphics2D g2 = (Graphics2D) g;
//				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//				g2.setColor(QRColorsAndFonts.BORDER_COLOR);
//				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
//			} else {
//				g.setColor(QRColorsAndFonts.BORDER_COLOR);
//				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//			}
//			return;
		}
	}

	@Override
	public void componentFresh() {
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		setBorder(BorderFactory.createEmptyBorder());
		setBorderPaint(false);
		panelLoop();
	}

	public void panelLoop() {
		final Component[] components = getComponents();
		for (Component component : components) {
			try {
				((QRComponentUpdate) component).componentFresh();
			} catch (Exception ignore) {
			}
		}
	}
}

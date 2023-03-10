package swing.qr.kiarelemb.component.combination;


import swing.qr.kiarelemb.component.QRComponentUtils;
import swing.qr.kiarelemb.component.basic.QRLabel;
import swing.qr.kiarelemb.component.basic.QRPanel;
import swing.qr.kiarelemb.component.event.QRTabSelectEvent;
import swing.qr.kiarelemb.component.event.QRTabbedPaneCloseEvent;
import swing.qr.kiarelemb.component.listener.QRActionListener;
import swing.qr.kiarelemb.component.listener.QRTabCloseListener;
import swing.qr.kiarelemb.component.listener.QRTabSelectChangedListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-27 14:27
 **/
public class QRTabbedPane extends QRPanel {

	private final ArrayList<QRTabPanel> arrTabs;
	/**
	 * 用于存放tabPane的Panel
	 */
	private final QRPanel tabs;
	JLabel selectedLabel;
	private int selectedIndex = -1;
	private boolean loadCloseButton = false;
	private QRTabPanel selectedTab;
	private QRTabCloseListener closeButtonActionListener;
	private QRActionListener actionListener;

	private QRTabSelectChangedListener tabSelectChangedListener;

	private Font tabFont = QRColorsAndFonts.MENU_ITEM_DEFAULT_FONT;

	public QRTabbedPane() {
		this(BorderLayout.NORTH);
	}

	public QRTabbedPane(String tabPositionFromBorderLayout) {
		super(false);
		this.arrTabs = new ArrayList<>();
		this.tabs = new QRPanel() {
			{
				addMouseMotionListener();
			}

			@Override
			protected void mouseMove(MouseEvent e) {
				setCursor(Cursor.getDefaultCursor());
			}
		};
		this.tabs.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		switch (tabPositionFromBorderLayout) {
			case BorderLayout.SOUTH -> this.tabs.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, QRColorsAndFonts.LINE_COLOR));
			case BorderLayout.EAST -> this.tabs.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, QRColorsAndFonts.LINE_COLOR));
			case BorderLayout.NORTH -> this.tabs.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.LINE_COLOR));
//			default -> throw new IllegalArgumentException("The tab position should be selected form BorderLayout.SOUTH, BorderLayout.EAST, BorderLayout.NORTH and BorderLayout.WEST");
			default -> {
				tabPositionFromBorderLayout = BorderLayout.WEST;
				this.tabs.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, QRColorsAndFonts.LINE_COLOR));
			}
		}
		setLayout(new BorderLayout());
		add(this.tabs, tabPositionFromBorderLayout);
	}

	public void addTabSelectChangedListener() {
		if (this.tabSelectChangedListener == null) {
			this.tabSelectChangedListener = new QRTabSelectChangedListener();
			this.tabSelectChangedListener.add(e -> tabSelectChangedAction((QRTabSelectEvent) e));
		}
	}

	public void addTabSelectChangedAction(QRActionRegister ar) {
		if (this.tabSelectChangedListener != null) {
			this.tabSelectChangedListener.add(ar);
		}
	}

	protected void tabSelectChangedAction(QRTabSelectEvent event) {

	}


	public void addTabCloseButton() {
		this.loadCloseButton = true;
		this.closeButtonActionListener = new QRTabCloseListener();
		this.closeButtonActionListener.add(e -> closeButtonAction((QRTabbedPaneCloseEvent) e));
		this.actionListener = new QRActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				QRTabbedPane.this.closeButtonActionListener.tabCloseButtonAction(new QRTabbedPaneCloseEvent(QRTabbedPane.this));
			}
		};
	}

	/**
	 * 设置当前欲显示的面板索引
	 *
	 * @param index
	 */
	public void setSelectedTab(int index) {
		setContentPaneIndex(index);
	}

	private void setContentPaneIndex(int index) {
		if (this.selectedIndex != index) {
			QRTabPanel tab = this.arrTabs.get(index);
			if (tab != null) {
				this.selectedTab = tab;
				setContentPane(this.selectedTab);
			}
		}
	}

	/**
	 * 在添加 Tab 前设置
	 *
	 * @param tabTitleFont 标签统一的字体设置
	 */
	public void setTabTitleFont(Font tabTitleFont) {
		this.tabFont = tabTitleFont;
	}

	public int getContentPaneIndex() {
		return this.selectedIndex;
	}

	public QRTabPanel getSelectedTab() {
		return this.selectedTab;
	}

	public int getTabSize() {
		return this.arrTabs.size();
	}

	public int addTab(String title, QRTabbedContentPanel content) {
		return addTab(title, null, content);
	}

	/**
	 * 添加一个标签，并设置它的图标
	 *
	 * @param title   标签名
	 * @param image   图片
	 * @param content 内容面板
	 * @return 添加的标签的索引
	 */
	public int addTab(String title, ImageIcon image, QRTabbedContentPanel content) {
		int index = getAddIndex();
		QRTabPanel tabPane = new QRTabPanel(title, content, index, image, this.loadCloseButton);
		tabPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				QRTabbedPane.this.setContentPane(tabPane);
				QRComponentUtils.windowFresh(QRTabbedPane.this);
			}
		});
		if (this.loadCloseButton) {
			tabPane.setCloseButtonAction(this.actionListener);
		}
		this.tabs.add(tabPane, index);
		this.arrTabs.add(index, tabPane);
		return index;
	}

	/**
	 * 关闭按钮的事件
	 */
	protected void closeButtonAction(QRTabbedPaneCloseEvent e) {

	}

	private void setContentPane(QRTabPanel tabPanel) {
//		if (selectedIndex != -1) {
//			remove(arrTabs.get(selectedIndex));
//		} else {
		Component[] components = getComponents();
		if (components != null && components.length > 0) {
			for (Component c : components) {
				if (c instanceof QRTabbedContentPanel) {
					remove(c);
				}
			}
		}
//		}
		int foreSelectedIndex = this.selectedIndex;
		if (this.selectedIndex != -1) {
			this.arrTabs.get(this.selectedIndex).setClicked(false);
		}
		tabPanel.setClicked(true);
		this.selectedLabel = tabPanel.getTitleLabel();
		this.selectedIndex = tabPanel.getIndex();
		add(tabPanel.getContentPane(), BorderLayout.CENTER);
		this.tabSelectChangedListener.tabSelectChangeAction(new QRTabSelectEvent(foreSelectedIndex, this.selectedIndex, tabPanel.content));
		repaint();
	}

	public QRTabbedContentPanel getContentPanel(int index) {
		if (this.arrTabs.size() <= index || index < 0) {
			throw new IndexOutOfBoundsException(index);
		}
		return this.arrTabs.get(index).getContentPane();
	}

	private int getAddIndex() {
		int index = this.arrTabs.size() - 1;
		if (index < 0) {
			index = 0;
		} else {
			index++;
		}
		return index;
	}

	/**
	 * @author Kiarelemb QR
	 * @program: QR_Swing
	 * @description:
	 * @create 2022-11-27 14:30
	 **/
	class QRTabPanel extends QRPanel {
		private final QRTabbedContentPanel content;
		private final QRLabel titleLabel;
		private QRLabel iconLabel;
		private int index;
		private QRTabCloseButton closeButton;

		QRTabPanel(String title, QRTabbedContentPanel content, int index, boolean loadCloseButton) {
			super(false);
			this.content = content;
			this.index = index;
			setFocusable(false);
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.FRAME_COLOR_BACK));
			this.titleLabel = new QRLabel("  " + title + "  ");
			this.titleLabel.setFont(QRTabbedPane.this.tabFont);
			this.titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			this.titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			setLayout(new BorderLayout(5, 5));
			add(this.titleLabel, BorderLayout.CENTER);
			if (loadCloseButton) {
				this.closeButton = new QRTabCloseButton();
				add(this.closeButton, BorderLayout.EAST);
			}
		}

		QRTabPanel(String title, QRTabbedContentPanel content, int index, ImageIcon image, boolean loadCloseButton) {
			this(title, content, index, loadCloseButton);
			setIconImage(image);
		}

		public void setIconImage(ImageIcon image) {
			if (image != null && this.iconLabel == null) {
				this.iconLabel = new QRLabel();
				this.iconLabel.setIcon(image);
				add(this.iconLabel, BorderLayout.WEST);
			}
		}

		public void setCloseButtonAction(QRActionListener action) {
			if (this.closeButton != null) {
				this.closeButton.addActionListener(action);
			}
		}

		@Override
		public void addMouseListener(MouseListener l) {
			super.addMouseListener(l);
			if (this.titleLabel == null) {
				return;
			}
			this.titleLabel.addMouseListener(l);
		}

		public int getIndex() {
			return this.index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setClicked(boolean clicked) {
			if (clicked) {
				//            setBackground(SELECTED_TAB_COLOR);
				this.titleLabel.setForeground(QRColorsAndFonts.CARET_COLOR);
				setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, QRColorsAndFonts.CARET_COLOR));
			} else {
				this.titleLabel.setForeground(QRColorsAndFonts.TEXT_COLOR_FORE);
				//            titleLabel.setForeground(DehorutoHuukaku.TEXT_COLOR_FORE);
				setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, QRColorsAndFonts.FRAME_COLOR_BACK));
			}
		}

		public QRLabel getTitleLabel() {
			return this.titleLabel;
		}

		public QRTabbedContentPanel getContentPane() {
			return this.content;
		}

		/**
		 * 关闭按钮
		 */
		static class QRTabCloseButton extends JButton {

			private QRTabCloseButton() {
				final int size = 12;
				setPreferredSize(new Dimension(size, size));
				setToolTipText("关闭");
				setUI(new BasicButtonUI());
				setContentAreaFilled(false);
				setFocusable(false);
				setBorderPainted(false);
				setRolloverEnabled(true);
			}

			@Override
			public void updateUI() {
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();

				if (getModel().isPressed()) {
					g2.translate(1, 1);
				}
				g2.setStroke(new BasicStroke(2));
				if (getModel().isRollover()) {
					g2.setColor(Color.RED);
				} else {
					g2.setColor(QRColorsAndFonts.TEXT_COLOR_FORE);
				}
				int delta = 3;
				int add = (getHeight() - 14) / 2;
				g2.drawLine(delta, add + delta, 14 - delta - 1, add + 14 - delta - 1);
				g2.drawLine(14 - delta - 1, add + delta, delta, add + 14 - delta - 1);
				g2.dispose();
			}
		}
	}
}

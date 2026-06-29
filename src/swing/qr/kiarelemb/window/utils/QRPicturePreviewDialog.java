package swing.qr.kiarelemb.window.utils;

import method.qr.kiarelemb.utils.QRSystemUtils;
import swing.qr.kiarelemb.basic.*;
import swing.qr.kiarelemb.utils.PicturePanel;
import swing.qr.kiarelemb.window.basic.QRDialog;

import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 图片预览对话框，支持单张/多张图片预览、拖拽平移、滚轮缩放。
 * @apiNote 本类使用方法：
 * <pre><code>
 *      // ── 预览单张图片 ──
 *      QRPicturePreviewDialog dialog = new QRPicturePreviewDialog(parent, new File("image.png"));
 *      dialog.setVisible(true);
 *      if (dialog.isConfirmed()) {
 *          // 用户点击了确定
 *      }
 *
 *      // ── 预览多张图片 ──
 *      File[] files = {new File("img1.png"), new File("img2.png"), new File("img3.png")};
 *      QRPicturePreviewDialog multi = new QRPicturePreviewDialog(parent, files);
 *      multi.setVisible(true);
 *
 *      // ── 多页预览时切换图片默认保持缩放与平移位置；可通过以下方式禁用 ──
 *      multi.setKeepViewOnPageChange(false);
 * </code></pre>
 * @create 2026/6/5 06:37
 */
public class QRPicturePreviewDialog extends QRDialog {
	private static final int HORIZONTAL_MARGIN = 28;
	private static final int VERTICAL_MARGIN = 72;
	private static final int BUTTON_PANEL_HEIGHT = 48;
	private static final int MIN_ZOOM = 10;
	private static final int MAX_ZOOM = 400;
	private BufferedImage image;
	private Dimension previewSize;
	private PicturePanel picturePanel;
	private final QRSlider zoomSlider = new QRSlider();
	private boolean confirmed = false;

	private File[] imageFiles;
	private int currentIndex;
	private boolean multiPage;
	private boolean keepViewOnPageChange = true;
	private QRButton prevButton;
	private QRButton nextButton;
	private QRLabel pageLabel;

	public QRPicturePreviewDialog(Window parent, File pictureFile) {
		this(parent, pictureFile, false);
	}

	public QRPicturePreviewDialog(Window parent, File pictureFile, boolean cancelButton) {
		super(parent);
		this.multiPage = false;
		initDialog(parent);
		this.image = readImage(pictureFile);
		this.previewSize = getPreviewSize(this.image);
		this.picturePanel = createPicturePanel();
		this.picturePanel.setZoomRange(MIN_ZOOM / 100.0, MAX_ZOOM / 100.0);
		this.picturePanel.setMouseWheelZoomStep(0.1);
		finishSetup(parent, cancelButton);
	}

	public QRPicturePreviewDialog(Window parent, File[] pictureFiles) {
		this(parent, pictureFiles, false);
	}

	public QRPicturePreviewDialog(Window parent, File[] pictureFiles, boolean cancelButton) {
		super(parent);
		if (pictureFiles == null || pictureFiles.length == 0) {
			throw new IllegalArgumentException("图片文件数组不能为空");
		}
		this.multiPage = true;
		this.imageFiles = pictureFiles;
		this.currentIndex = 0;
		initDialog(parent);
		this.image = readImage(pictureFiles[0]);
		this.previewSize = getPreviewSize(this.image);
		this.picturePanel = createPicturePanel();
		this.picturePanel.setZoomRange(MIN_ZOOM / 100.0, MAX_ZOOM / 100.0);
		this.picturePanel.setMouseWheelZoomStep(0.1);
		finishSetup(parent, cancelButton);
		updateTitle();
		updateNavigationState();
	}

	private void initDialog(Window parent) {
		setTitlePlace(QRDialog.CENTER);
		setTitle("预览图片");
		setParentWindowNotFollowMove();
		setResizable(false);
	}

	private PicturePanel createPicturePanel() {
		return new PicturePanel(this.image, this.previewSize) {
			@Override
			protected void zoomChanged(double zoom) {
				int value = (int) Math.round(zoom * 100);
				if (zoomSlider.getValue() != value) {
					zoomSlider.setValue(value);
				}
			}
		};
	}

	private void finishSetup(Window parent, boolean cancelButton) {
		mainPanel.setLayout(new BorderLayout(0, 8));
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		mainPanel.add(this.picturePanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel(cancelButton, multiPage), BorderLayout.SOUTH);

		setSize(previewSize.width + HORIZONTAL_MARGIN, previewSize.height + VERTICAL_MARGIN + BUTTON_PANEL_HEIGHT);
		setLocationRelativeTo(parent);
		QRSystemUtils.setWindowTrans(this, 0.99f);
	}

	private BufferedImage readImage(File pictureFile) {
		if (pictureFile == null || !pictureFile.exists() || !pictureFile.isFile()) {
			throw new IllegalArgumentException("图片文件不存在：" + pictureFile);
		}
		try {
			BufferedImage image = ImageIO.read(pictureFile);
			if (image == null) {
				throw new IllegalArgumentException("无法读取图片：" + pictureFile.getAbsolutePath());
			}
			return image;
		} catch (IOException e) {
			throw new IllegalArgumentException("无法读取图片：" + pictureFile.getAbsolutePath(), e);
		}
	}

	private Dimension getPreviewSize(BufferedImage image) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int maxWidth = Math.max(240, screenSize.width - 180);
		int maxHeight = Math.max(180, screenSize.height - 220);
		double scale = Math.min(maxWidth * 1.0 / image.getWidth(), maxHeight * 1.0 / image.getHeight());
		int width = Math.max(1, (int) Math.round(image.getWidth() * scale));
		int height = Math.max(1, (int) Math.round(image.getHeight() * scale));
		return new Dimension(width, height);
	}

	protected QRPanel buttonPanel(boolean cancelButton) {
		return buttonPanel(cancelButton, false);
	}

	protected QRPanel buttonPanel(boolean cancelButton, boolean multiPage) {
		QRPanel panel = new QRPanel(new BorderLayout(8, 0));
		panel.add(zoomPanel(), BorderLayout.WEST);

		if (multiPage) {
			panel.add(navigationPanel(), BorderLayout.CENTER);
		}

		QRPanel buttonPanel = new QRPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
		QRRoundButton sureButton = new QRRoundButton("确定");
		sureButton.setPreferredSize(74, 32);
		sureButton.addClickAction(e -> {
			confirmed = true;
			sureAction(e);
		});

		if (cancelButton) {
			QRRoundButton buttonCancel = new QRRoundButton("取消");
			buttonCancel.setPreferredSize(74, 32);
			buttonCancel.addClickAction(this::cancelAction);
			buttonPanel.add(buttonCancel);
		}
		buttonPanel.add(sureButton);
		panel.add(buttonPanel, BorderLayout.EAST);
		return panel;
	}

	private QRPanel navigationPanel() {
		QRPanel panel = new QRPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));

		prevButton = new QRButton("<");
		prevButton.setPreferredSize(36, 28);
		prevButton.addClickAction(e -> {
			if (currentIndex > 0) {
				loadAndShowImage(currentIndex - 1);
			}
		});

		pageLabel = new QRLabel();
		pageLabel.setHorizontalAlignment(QRLabel.CENTER);

		nextButton = new QRButton(">");
		nextButton.setPreferredSize(36, 28);
		nextButton.addClickAction(e -> {
			if (currentIndex < imageFiles.length - 1) {
				loadAndShowImage(currentIndex + 1);
			}
		});

		panel.add(prevButton);
		panel.add(pageLabel);
		panel.add(nextButton);
		return panel;
	}

	private void loadAndShowImage(int index) {
		if (imageFiles == null || index < 0 || index >= imageFiles.length) {
			return;
		}
		currentIndex = index;
		BufferedImage newImage = readImage(imageFiles[index]);
		Dimension newSize = getPreviewSize(newImage);
		this.image = newImage;
		this.previewSize = newSize;

		if (keepViewOnPageChange) {
			double savedZoom = picturePanel.zoom();
			int savedPanX = picturePanel.panX();
			int savedPanY = picturePanel.panY();
			picturePanel.setImage(newImage, newSize);
			picturePanel.setZoom(savedZoom);
			picturePanel.setPan(savedPanX, savedPanY);
			if (zoomSlider.getValue() != (int) Math.round(savedZoom * 100)) {
				zoomSlider.setValue((int) Math.round(savedZoom * 100));
			}
		} else {
			picturePanel.setImage(newImage, newSize);
			picturePanel.setZoom(1.0);
			zoomSlider.setValue(100);
		}

		updateTitle();
		updateNavigationState();
	}

	private void updateTitle() {
		if (multiPage && imageFiles != null) {
			setTitle("预览图片 (" + (currentIndex + 1) + "/" + imageFiles.length + ")");
		}
	}

	private void updateNavigationState() {
		if (prevButton != null) {
			prevButton.setEnabled(currentIndex > 0);
		}
		if (nextButton != null) {
			nextButton.setEnabled(currentIndex < imageFiles.length - 1);
		}
		if (pageLabel != null) {
			pageLabel.setText((currentIndex + 1) + " / " + imageFiles.length);
		}
	}

	protected QRPanel zoomPanel() {
		QRPanel panel = new QRPanel(new BorderLayout(8, 0));
		panel.setPreferredSize(260, 40);
		QRLabel label = new QRLabel("缩放");

		zoomSlider.setBoundValue(MIN_ZOOM, MAX_ZOOM);
		zoomSlider.setValue(100);
		zoomSlider.setPreferredSize(new Dimension(190, 32));
		zoomSlider.addChangeListener(e -> setZoom(zoomSlider.getValue()));

		panel.add(label, BorderLayout.WEST);
		panel.add(zoomSlider, BorderLayout.CENTER);
		return panel;
	}

	private void setZoom(int zoom) {
		picturePanel.setZoom(zoom / 100.0);
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	 * 设置切换图片时是否保持当前的视图状态（缩放与平移位置），默认为 {@code true}。
	 *
	 * <p>多张图片预览时，如果用户已经放大并拖拽到某个区域，切换下一张图片默认会沿用当前缩放和平移；
	 * 如果希望每次切换都回到 100% 缩放和初始位置，传入 {@code false}。</p>
	 *
	 * @param keepViewOnPageChange 是否保持当前视图状态
	 */
	public void setKeepViewOnPageChange(boolean keepViewOnPageChange) {
		this.keepViewOnPageChange = keepViewOnPageChange;
	}

	/**
	 * 返回用户是否点击了确定按钮。
	 *
	 * @return true 表示用户确认；取消或关闭窗口时为 false
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * 确定按钮点击事件，可通过重写覆盖。
	 *
	 * <p>默认关闭对话框。重写时如需保持原关闭行为，可调用 {@code super.sureAction(e)}。</p>
	 */
	protected void sureAction(ActionEvent e) {
		dispose();
	}

	/**
	 * 取消按钮点击事件，可通过重写覆盖。
	 *
	 * <p>默认关闭对话框。</p>
	 */
	protected void cancelAction(ActionEvent e) {
		dispose();
	}
}

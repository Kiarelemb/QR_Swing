package swing.qr.kiarelemb.utils;

import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 带图片缩略图和显示名的预览卡片。
 *
 * <p>该组件用于图片列表、模板列表等多图预览场景。构造时同步读取图片文件，
 * 中间显示缩略图，底部显示名称，并把鼠标设置为手形。图片读取失败时缩略图为空，
 * 组件仍会显示名称。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRPicturePreviewPanel card = new QRPicturePreviewPanel(file, file.getName());
 * card.addMouseAction(QRMouseListener.TYPE.CLICK, event -> preview(file));
 * </code></pre>
 *
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className PicturePreviewPanel
 * @description 带图片预览和显示名的卡片
 * @create 2026/6/6 08:02
 */
public class QRPicturePreviewPanel extends QRPanel {
	protected static final Dimension CARD_SIZE = new Dimension(210, 270);
	protected final File pictureFile;
	protected final String pictureName;
	protected final QRLabel nameLabel;
	protected final QRImagePanel imagePanel;
	protected BufferedImage image;

	/**
	 * 创建图片预览卡片。
	 *
	 * @param pictureFile 图片文件
	 * @param pictureName 展示名称
	 */
	public QRPicturePreviewPanel(File pictureFile, String pictureName) {
		setOpaque(true);
		this.pictureFile = pictureFile;
		this.pictureName = pictureName;

		setPreferredSize(CARD_SIZE);
		setLayout(new BorderLayout(0, 8));
		setBorder(new LineBorder(QRColorsAndFonts.FRAME_COLOR_BACK, 2));

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		loadImage();

		nameLabel = new QRLabel(pictureName);
		nameLabel.setFont(QRColorsAndFonts.createFont(15));
		nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

		imagePanel = new QRImagePanel(image);
		add(imagePanel, BorderLayout.CENTER);
		add(nameLabel, BorderLayout.SOUTH);
	}

	private void loadImage() {
		try {
			image = ImageIO.read(pictureFile);
		} catch (IOException e) {
			image = null;
		}
	}
}

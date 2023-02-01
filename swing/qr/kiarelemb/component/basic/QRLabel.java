package swing.qr.kiarelemb.component.basic;

import method.qr.kiarelemb.utils.QRFileUtils;
import method.qr.kiarelemb.utils.QRRandomUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.component.assembly.QRToolTip;
import swing.qr.kiarelemb.component.listener.QRMouseListener;
import swing.qr.kiarelemb.component.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.component.QRTextBasicActionSetting;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 标签类
 * @create 2022-11-04 16:24
 **/
public class QRLabel extends JLabel implements QRComponentUpdate, QRTextBasicActionSetting, QRMouseListenerAdd, QRMouseMotionListenerAdd {
	private QRMouseMotionListener mouseMotionListener;
	private QRMouseListener mouseListener;

	public QRLabel() {
		componentFresh();
	}

	public QRLabel(String text) {
		this();
		setText(text);
	}

	public QRLabel(Icon icon) {
		this();
		setIcon(icon);
	}

	//region 文本设置
	public void setText(int intValue) {
		setText(String.valueOf(intValue));
	}

	public void setText(double doubleValue) {
		setText(String.valueOf(doubleValue));
	}

	public void setText(float floatValue) {
		setText(String.valueOf(floatValue));
	}

	@Override
	public void setTextLeft() {
		setHorizontalAlignment(SwingConstants.LEFT);
	}

	@Override
	public void setTextCenter() {
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public void setTextRight() {
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	@Override
	public void clear() {
		setText(null);
	}
	//endregion

	//region 各种添加

	/**
	 * 添加鼠标移动事件
	 */
	@Override
	public final void addMouseMotionListener() {
		if (this.mouseMotionListener == null) {
			this.mouseMotionListener = new QRMouseMotionListener();
			this.mouseMotionListener.add(QRMouseMotionListener.TYPE.DRAG, e -> mouseDrag((MouseEvent) e));
			this.mouseMotionListener.add(QRMouseMotionListener.TYPE.MOVE, e -> mouseMove((MouseEvent) e));
			addMouseMotionListener(this.mouseMotionListener);

		}
	}

	/**
	 * 添加鼠标移动事件
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	@Override
	public final void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister ar) {
		if (this.mouseMotionListener != null) {
			this.mouseMotionListener.add(type, ar);
		}
	}

	/**
	 * 添加鼠标事件
	 */
	@Override
	public final void addMouseListener() {
		if (this.mouseListener == null) {
			this.mouseListener = new QRMouseListener();
			this.mouseListener.add(QRMouseListener.TYPE.CLICK, e -> mouseClick((MouseEvent) e));
			this.mouseListener.add(QRMouseListener.TYPE.PRESS, e -> mousePress((MouseEvent) e));
			this.mouseListener.add(QRMouseListener.TYPE.RELEASE, e -> mouseRelease((MouseEvent) e));
			this.mouseListener.add(QRMouseListener.TYPE.ENTER, e -> mouseEnter((MouseEvent) e));
			this.mouseListener.add(QRMouseListener.TYPE.EXIT, e -> mouseExit((MouseEvent) e));
			addMouseListener(this.mouseListener);

		}
	}

	/**
	 * 添加鼠标事件
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	@Override
	public final void addMouseAction(QRMouseListener.TYPE type, QRActionRegister ar) {
		if (this.mouseListener != null) {
			this.mouseListener.add(type, ar);
		}
	}
	//endregion

	//region 各种重写

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
		setForeground(QRColorsAndFonts.MENU_COLOR);
	}

	//region 静态方法

	public static ImageIcon createAutoAdjustIcon(URL url, boolean constrained) {
		try {
			return createAutoAdjustIcon(ImageIO.read(url), constrained);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 创建一个可以自适应组件大小的ImageIcon对象
	 *
	 * @param image       从<code> Image </code>对象来创建ImageIcon
	 * @param constrained 是否等比例缩放 。当为<code> true </code>时，可通过
	 *                    {@link javax.swing.JComponent#setAlignmentX(float)}和
	 *                    {@link javax.swing.JComponent#setAlignmentY(float)}方法设置组件对齐方式。
	 */
	public static ImageIcon createAutoAdjustIcon(Image image, boolean constrained) {
		return new ImageIcon(image) {
			@Override
			public synchronized void paintIcon(Component cmp, Graphics g, int x, int y) {
				//默认绘制起点
				Point startPoint = new Point(0, 0);
				//获取组件大小
				Dimension cmpSize = cmp.getSize();
				//获取图像大小
				Dimension imgSize = new Dimension(getIconWidth(), getIconHeight());
				//等比例缩放
				if (constrained) {
					//计算图像宽高比例
					double ratio = 1.0 * imgSize.width / imgSize.height;
					//计算等比例缩放后的区域大小
					imgSize.width = (int) Math.min(cmpSize.width, ratio * cmpSize.height);
					imgSize.height = (int) (imgSize.width / ratio);
					//计算绘制起点
//                    startPoint.x = (int)
//                            (cmp.getAlignmentX() * (cmpSize.width - imgSize.width));
//                    startPoint.y = (int)
//                            (cmp.getAlignmentY() * (cmpSize.height - imgSize.height));
				} else {//完全填充
					imgSize = cmpSize;
				}

				//根据起点和区域大小进行绘制
				if (getImageObserver() == null) {
					g.drawImage(getImage(), startPoint.x, startPoint.y, imgSize.width, imgSize.height, cmp);
				} else {
					g.drawImage(getImage(), startPoint.x, startPoint.y, imgSize.width, imgSize.height, getImageObserver());
				}
			}
		};
	}

	/**
	 * 创建一个可以自适应组件大小的Icon对象
	 *
	 * @param filename    指定文件名或者路径的字符串
	 * @param constrained 是否等比例缩放。当为<code> true </code>时，可通过
	 *                    {@link javax.swing.JComponent#setAlignmentX(float)}和
	 *                    {@link javax.swing.JComponent#setAlignmentY(float)}方法设置组件对齐方式。
	 */
	public static ImageIcon createAutoAdjustIcon(String filename, boolean constrained) {
		return createAutoAdjustIcon(new ImageIcon(filename).getImage(), constrained);
	}

	public static ImageIcon createAutoAdjustIconAndRound(String filename, boolean constrained) {
		return new ImageIcon() {
			@Override
			public synchronized void paintIcon(Component cmp, Graphics g, int x, int y) {
				final String processedImageFilePath = makeRoundedCorner(filename, cmp.getWidth(), cmp.getHeight());
				final ImageIcon icon = new ImageIcon(processedImageFilePath == null ? filename : processedImageFilePath);
				//默认绘制起点
				Point startPoint = new Point(0, 0);
				//获取组件大小
				Dimension cmpSize = cmp.getSize();
				//获取图像大小
				Dimension imgSize = new Dimension(icon.getIconWidth(), icon.getIconHeight());
				//等比例缩放
				if (constrained) {
					//计算图像宽高比例
					double ratio = 1.0 * imgSize.width / imgSize.height;
					//计算等比例缩放后的区域大小
					imgSize.width = (int) Math.min(cmpSize.width, ratio * cmpSize.height);
					imgSize.height = (int) (imgSize.width / ratio);
					startPoint.x = (cmpSize.width - imgSize.width) / 2;
					startPoint.y = (cmpSize.height - imgSize.height) / 2;
				} else {
					//完全填充
					imgSize = cmpSize;
				}

				//根据起点和区域大小进行绘制
				if (getImageObserver() == null) {
					g.drawImage(icon.getImage(), startPoint.x, startPoint.y, imgSize.width, imgSize.height, cmp);
				} else {
					g.drawImage(icon.getImage(), startPoint.x, startPoint.y, imgSize.width, imgSize.height, getImageObserver());
				}
			}
		};
	}

	public static String makeRoundedCorner(String filePath, int newWidth, int newHeight) {
		final File inputFile = new File(filePath);

		File result = new File(QRSwing.TMP_DIRECTORY + inputFile.getName());
		int cornerRadius = 30;
		try {
			BufferedImage bi1 = ImageIO.read(inputFile);
			BufferedImage image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
			Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, newWidth, newHeight);
			Graphics2D g2 = image.createGraphics();
			image = g2.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT);
			g2 = image.createGraphics();
			g2.setComposite(AlphaComposite.Clear);
			g2.fill(new Rectangle(newWidth, newHeight));
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
			g2.setClip(shape);
			g2 = image.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.fillRoundRect(0, 0, newWidth, newHeight, cornerRadius, cornerRadius);
			g2.setComposite(AlphaComposite.SrcIn);
			g2.drawImage(bi1, 0, 0, newWidth, newHeight, null);
			g2.dispose();
			if (ImageIO.write(image, "png", result)) {
				return result.getAbsolutePath();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static String cutHeadImages(String filePath, int width, int height) {
		BufferedImage avatarImage = null;
		try {
			avatarImage = ImageIO.read(new File(filePath));
			avatarImage = scaleByPercentage(avatarImage, width, height);
			// 透明底的图片
			BufferedImage formatAvatarImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D graphics = formatAvatarImage.createGraphics();
			//把图片切成一个园
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//留一个像素的空白区域，这个很重要，画圆的时候把这个覆盖
			int border = 1;
			//图片是一个圆型
			Ellipse2D.Double shape = new Ellipse2D.Double(border, border, width - border * 2, width - border * 2);
			//需要保留的区域
			graphics.setClip(shape);
			graphics.drawImage(avatarImage, border, border, width - border * 2, width - border * 2, null);
			graphics.dispose();
			//在圆图外面再画一个圆
			//新创建一个graphics，这样画的圆不会有锯齿
			graphics = formatAvatarImage.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int border1 = 3;
			//画笔是4.5个像素，BasicStroke的使用可以查看下面的参考文档
			//使画笔时基本会像外延伸一定像素，具体可以自己使用的时候测试
			Stroke s = new BasicStroke(5F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
			graphics.setStroke(s);
			graphics.setColor(Color.WHITE);
			graphics.drawOval(border1, border1, width - border1 * 2, width - border1 * 2);
			graphics.dispose();
			String fileName = QRRandomUtils.getRandomFileName(20);
			String filePaths = QRFileUtils.getTempDirectoryPath() + fileName + ".png";
			OutputStream os = new FileOutputStream(filePaths);
			ImageIO.write(formatAvatarImage, "PNG", os);
			return filePaths;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 缩小Image，此方法返回源图像按给定宽度、高度限制下缩放后的图像
	 *
	 * @param newHeight 压缩后高度
	 * @param newWidth  压缩后宽度
	 */
	public static BufferedImage scaleByPercentage(BufferedImage inputImage, int newWidth, int newHeight) {
		// 获取原始图像透明度类型
		try {
			int type = inputImage.getColorModel().getTransparency();
			int width = inputImage.getWidth();
			int height = inputImage.getHeight();
			// 开启抗锯齿
			RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// 使用高质量压缩
			renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			BufferedImage img = new BufferedImage(newWidth, newHeight, type);
			Graphics2D graphics2d = img.createGraphics();
			graphics2d.setRenderingHints(renderingHints);
			graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0, width, height, null);
			graphics2d.dispose();
			return img;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static QRLabel getIconLabel() {
		QRLabel iconLabel = new QRLabel() {
			@Override
			public void componentFresh() {
				super.componentFresh();
				setIcon(QRLabel.createAutoAdjustIcon(QRSwing.windowIcon.getImage(), true));
			}
		};
		iconLabel.setPreferredSize(new Dimension(30, 30));
		iconLabel.setIcon(createAutoAdjustIcon(QRSwing.windowIcon.getImage(), true));
		return iconLabel;
	}
	//endregion
}

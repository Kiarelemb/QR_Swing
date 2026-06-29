package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRFileUtils;
import method.qr.kiarelemb.utils.QRRandomUtils;
import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.assembly.QRToolTip;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.inter.QRTextBasicActionSetting;
import swing.qr.kiarelemb.inter.listener.add.QRFocusListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.listener.QRFocusListener;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * QR Swing 的基础标签。
 *
 * <p>该类基于 {@link JLabel}，统一了主题字体和颜色，提供文本对齐、清空文本、
 * 鼠标/焦点事件封装，以及标签文字根据组件尺寸自动缩放的能力。对于窗口标题、
 * 设置项说明、状态栏文本和图标展示，通常都使用该类。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRLabel title = new QRLabel("设置");
 * title.setTextCenter();
 *
 * QRLabel count = new QRLabel();
 * count.setText(12);
 *
 * QRLabel icon = new QRLabel(QRLabel.createAutoAdjustIcon("logo.png", true));
 * icon.setPreferredSize(new Dimension(48, 48));
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 标签类
 * @create 2022-11-04 16:24
 **/
public class QRLabel extends JLabel implements QRComponentUpdate, QRTextBasicActionSetting, QRMouseListenerAdd, QRFocusListenerAdd, QRMouseMotionListenerAdd {
	private QRMouseMotionListener mouseMotionListener;
	private QRMouseListener mouseListener;
	private QRFocusListener focusListener;

	private boolean autoAdjust = false;

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
	/**
	 * 设置整数文本。
	 *
	 * @param intValue 整数值
	 */
	public void setText(int intValue) {
		setText(String.valueOf(intValue));
	}

	/**
	 * 设置 double 文本。
	 *
	 * @param doubleValue double 值
	 */
	public void setText(double doubleValue) {
		setText(String.valueOf(doubleValue));
	}

	/**
	 * 设置 float 文本。
	 *
	 * @param floatValue float 值
	 */
	public void setText(float floatValue) {
		setText(String.valueOf(floatValue));
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		if (autoAdjust) {
			adjustFontSize();
		}
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

	private Font originalFont = null;             // 保存用户设置的原始字体
	private ComponentAdapter resizeListener;      // 组件大小变化监听器

	/**
	 * 启用/关闭文字大小自动适应组件尺寸的功能。
	 *
	 * <p>开启后，标签会在尺寸变化和文本变化时自动计算可放入组件的最大字号。
	 * 该功能适合固定尺寸数字牌、状态块和短标题，不适合长段落文本。</p>
	 *
	 * @param sizeAuto true 开启，false 关闭
	 */
	public void setFontSizeAutoAdjust(boolean sizeAuto) {
		if (this.autoAdjust == sizeAuto) {
			return;
		}
		this.autoAdjust = sizeAuto;

		if (autoAdjust) {

			if (resizeListener == null) {
				resizeListener = new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						if (autoAdjust) {
							adjustFontSize();
						}
					}
				};
			}

			// 开启自动调整：保存当前字体，添加监听器，并立即调整一次字体
			if (originalFont == null) {
				originalFont = getFont();
			}
			addComponentListener(resizeListener);
			adjustFontSize();
		} else {
			// 关闭自动调整：移除监听器，恢复原始字体
			removeComponentListener(resizeListener);
			if (originalFont != null) {
				super.setFont(originalFont);
			}
		}
	}

	/**
	 * 根据当前组件的尺寸计算最佳字体大小并应用。
	 *
	 * <p>通常由 {@link #setFontSizeAutoAdjust(boolean)} 自动触发；手动调用前应确保组件已布局，
	 * 即宽高大于 0。</p>
	 */
	public void adjustFontSize() {
		String text = getText();
		if (text == null || text.isEmpty() || getWidth() <= 0 || getHeight() <= 0) {
			return;
		}

		// 获取可用区域（考虑边框和内边距）
		Insets insets = getInsets();
		int availableWidth = getWidth() - insets.left - insets.right;
		int availableHeight = getHeight() - insets.top - insets.bottom;
		if (availableWidth <= 0 || availableHeight <= 0) {
			return;
		}

		// 获取当前图形环境，用于创建 FontMetrics
		Graphics g = getGraphics();
		if (g == null) return;

		// 初始字体大小：从 1 开始尝试，或基于高度比例快速定位（改进性能）
		int minFontSize = 1;
		int maxFontSize = Math.min(availableHeight, availableWidth / text.length());
		// 确保最大字体至少为 1
		maxFontSize = Math.max(minFontSize, maxFontSize);

		int bestSize = minFontSize;
		// 使用二分法寻找最大可用字体
		while (minFontSize <= maxFontSize) {
			int mid = (minFontSize + maxFontSize) / 2;
			Font testFont = getFont().deriveFont((float) mid);
			FontMetrics fm = g.getFontMetrics(testFont);
			int textWidth = fm.stringWidth(text);
			int textHeight = fm.getHeight();

			if (textWidth <= availableWidth && textHeight <= availableHeight) {
				bestSize = mid;
				minFontSize = mid + 1;
			} else {
				maxFontSize = mid - 1;
			}
		}

		// 应用最佳字体大小（保持原有样式：粗体、斜体等）
		Font newFont = getFont().deriveFont((float) bestSize);
		super.setFont(newFont);
		g.dispose();
	}

	@Override
	public void clear() {
		setText(null);
	}
	//endregion

	//region 各种添加

	/**
	 * 添加焦点事件
	 */
	@Override
	public void addFocusListener() {
		if (this.focusListener == null) {
			this.focusListener = new QRFocusListener();
			this.focusListener.add(QRFocusListener.TYPE.GAIN, this::focusGained);
			this.focusListener.add(QRFocusListener.TYPE.LOST, this::focusLost);
			addFocusListener(this.focusListener);
		}
	}

	/**
	 * 添加焦点事件
	 */
	@Override
	public void addFocusAction(QRFocusListener.TYPE type, QRActionRegister<FocusEvent> ar) {
		if (this.focusListener == null) {
			addFocusListener();
		}
		if (this.focusListener != null) {
			this.focusListener.add(type, ar);
		}
	}

	/**
	 * 添加鼠标移动事件
	 */
	@Override
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
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	@Override
	public final void addMouseMotionAction(QRMouseMotionListener.TYPE type, QRActionRegister<MouseEvent> ar) {
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
	@Override
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
	 *
	 * @param type 类型
	 * @param ar   操作
	 */
	@Override
	public final void addMouseAction(QRMouseListener.TYPE type, QRActionRegister<MouseEvent> ar) {
		if (this.mouseListener == null) {
			addMouseListener();
		}
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


	/**
	 * 重写前请先调用 {@link #addFocusListener()}
	 */
	protected void focusGained(FocusEvent e) {
	}

	/**
	 * 重写前请先调用 {@link #addFocusListener()}
	 */
	protected void focusLost(FocusEvent e) {
	}

	//endregion

	//region 取得监听器

	public QRMouseMotionListener mouseMotionListener() {
		return mouseMotionListener;
	}

	public QRMouseListener mouseListener() {
		return mouseListener;
	}

	public QRFocusListener focusListener() {
		return focusListener;
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
		setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
		if (autoAdjust) {
			adjustFontSize();
		}
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
	 * <p>返回的图标会在绘制时读取承载组件的大小，按组件尺寸缩放图片。
	 * {@code constrained} 为 true 时按比例缩放，否则拉伸填满组件。</p>
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
				Point startPoint = new Point(3, 5);
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
				} else {
					//完全填充
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
	 * <p>该方法直接按文件路径创建图片，文件不存在时仍会返回一个空图片图标；
	 * 如需先校验文件存在，可使用 {@link QRFileUtils#fileExists(String)}。</p>
	 *
	 * @param filename    指定文件名或者路径的字符串
	 * @param constrained 是否等比例缩放。当为<code> true </code>时，可通过
	 *                    {@link javax.swing.JComponent#setAlignmentX(float)}和
	 *                    {@link javax.swing.JComponent#setAlignmentY(float)}方法设置组件对齐方式。
	 */
	public static ImageIcon createAutoAdjustIcon(String filename, boolean constrained) {
		return createAutoAdjustIcon(new ImageIcon(filename).getImage(), constrained);
	}

	/**
	 * 创建一个可随组件缩放并裁剪为圆角的图标。
	 *
	 * <p>该方法在绘制时同步处理图片并写入临时文件，频繁重绘或大图场景可能产生性能开销；
	 * 普通图片缩放优先使用 {@link #createAutoAdjustIcon(String, boolean)}。</p>
	 *
	 * @param filename    图片文件路径
	 * @param constrained 是否等比例缩放
	 * @return 自适应圆角图标
	 */
	public static ImageIcon createAutoAdjustIconAndRound(String filename, boolean constrained) {
		return new ImageIcon() {
			@Override
			public synchronized void paintIcon(Component cmp, Graphics g, int x, int y) {
				// TODO: 该圆角图片路径目前很少使用，暂不接入 QRTaskWorker；后续真有调用方需要时再缓存处理结果并放到后台生成。
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

	/**
	 * TODO: 后续若该方法重新成为常用路径，再考虑抽出纯图片处理逻辑并由调用方通过 QRTaskWorker 后台执行。
	 */
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

	/**
	 * TODO: 头像裁剪逻辑先保持同步实现；未来有真实 UI 卡顿场景时，再补后台任务和失败回调。
	 */
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
	 * <p>TODO: 仅在未来恢复大图批处理或频繁预览时，再把调用方迁移到后台任务。
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

	/**
	 * 创建一个显示 {@link QRSwing#windowIcon} 的标签。
	 *
	 * <p>该标签会在 {@link #componentFresh()} 时重新读取全局窗口图标，通常用于
	 * {@link swing.qr.kiarelemb.window.basic.QRFrame} 和
	 * {@link swing.qr.kiarelemb.window.basic.QRDialog} 的标题栏。</p>
	 *
	 * @return 窗口图标标签
	 */
	public static QRLabel getIconLabel() {
		return new QRLabel() {
			@Override
			public void componentFresh() {
				super.componentFresh();
				if (QRSwing.windowIcon != null) {
					setPreferredSize(new Dimension(24, 24));
					setIcon(QRLabel.createAutoAdjustIcon(QRSwing.windowIcon.getImage(), true));
				}
			}
		};
	}
	//endregion
}

package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.theme.QRColorsAndFonts;
import swing.qr.kiarelemb.window.utils.QRProgressDialog;

import javax.swing.*;
import java.awt.*;

/**
 * 手动绘制的细长进度条。
 * 高度默认为 4 像素，用 {@link QRColorsAndFonts#PRESS_COLOR} 绘制已填充部分。
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: 进度条
 * @create 2026-06-26 22:30
 */
public class QRProgressBar extends QRPanel {
	private static final int DEFAULT_BALL_COUNT = 5;
	private static final int DEFAULT_BALL_DIAMETER = 5;

	/**
	 * 动画刷新间隔。15ms 约等于 66 FPS；Swing Timer 在 Linux 上可能有抖动，继续调小通常不会更稳。
	 * 如果动画仍不顺滑，优先检查 EDT 是否有耗时任务，而不是一味提高刷新频率。
 	 */
	private static final int DEFAULT_INDETERMINATE_TIMER_DELAY = 12;

	/**
	 * 小球间的时间偏移。值越小，队列越紧；值越大，队列越散。
	 */
	private static final double DEFAULT_BALL_DELAY_SECONDS = 0.06D;

	/**
	 * 一轮动画完成后留出的空白时间。值越大，两轮之间的停顿越明显。
	 */
	private static final double DEFAULT_CYCLE_REST_SECONDS = 0.2D;

	/**
	 * 入场/离场瞬时速度与中段匀速速度的比例。值越大，两端越快、中段越慢。
	 */
	private static final double DEFAULT_EDGE_TO_MIDDLE_SPEED_RATIO = 4.0D;

	private int value = 0;
	private int height = 4;
	private boolean indeterminate = false;
	private int ballDiameter = DEFAULT_BALL_DIAMETER;
	private double ballDelaySeconds = DEFAULT_BALL_DELAY_SECONDS;
	private double cycleRestSeconds = DEFAULT_CYCLE_REST_SECONDS;
	private double edgeToMiddleSpeedRatio = DEFAULT_EDGE_TO_MIDDLE_SPEED_RATIO;
	private double minTravelSeconds = 1.15D;
	private double maxTravelSeconds = 1.9D;
	private double baseTravelSeconds = 1.05D;
	private double travelWidthDivisor = 800D;
	private double[] ballXes = new double[DEFAULT_BALL_COUNT];
	private long indeterminateStartTime = System.currentTimeMillis();
	private final Timer indeterminateTimer = new Timer(DEFAULT_INDETERMINATE_TIMER_DELAY, e -> updateIndeterminate());

	public QRProgressBar() {
		setMinimumSize(new Dimension(10, 4));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
	}

	public QRProgressBar(int height) {
		this();
		this.height = height;
	}

	public void setHeight(int height) {
		this.height = height;
		repaint();
	}

	public int height() {
		return height;
	}

	public void setValue(int value) {
		this.value = value;
		repaint();
	}

	public int value() {
		return value;
	}

	public void setIndeterminate(boolean indeterminate) {
		if (this.indeterminate == indeterminate) {
			return;
		}
		this.indeterminate = indeterminate;
		this.indeterminateStartTime = System.currentTimeMillis();
		if (indeterminate && isDisplayable()) {
			indeterminateTimer.start();
		} else {
			indeterminateTimer.stop();
		}
		repaint();
	}

	public boolean indeterminate() {
		return indeterminate;
	}

	/**
	 * 设置不确定进度动画的小球数量。
	 *
	 * <p>数量越多，队列越长，等待动画的“拖尾”越明显；数量越少，动画越轻。
	 * 该值会直接重建内部位置数组，运行中的不确定动画会从当前时间重新开始。
	 * 当前默认值为 5。一般建议使用 4 ~ 7，过多会让中段显得拥挤，也会增加少量绘制开销。
	 *
	 * @param ballCount 小球数量，必须大于 0
	 */
	public void setIndeterminateBallCount(int ballCount) {
		if (ballCount <= 0) {
			throw new IllegalArgumentException("ballCount must be greater than 0");
		}
		if (ballXes.length == ballCount) {
			return;
		}
		ballXes = new double[ballCount];
		resetIndeterminateAnimation();
	}

	/**
	 * 设置不确定进度动画的小球直径。
	 *
	 * <p>该值使用像素作为单位，不会随组件高度自动缩放。值越大，小球越醒目，
	 * 但在高度较小的进度条中更容易贴近上下边缘；值越小，动画更精细但可能不够明显。
	 * 当前默认值为 5。若 {@link QRProgressDialog} 中的进度条高度仍为 10，通常建议使用 4 ~ 7。
	 *
	 * @param ballDiameter 小球直径，必须大于 0
	 */
	public void setIndeterminateBallDiameter(int ballDiameter) {
		if (ballDiameter <= 0) {
			throw new IllegalArgumentException("ballDiameter must be greater than 0");
		}
		this.ballDiameter = ballDiameter;
		repaint();
	}

	/**
	 * 设置不确定进度动画的刷新间隔。
	 *
	 * <p>该值传给 Swing {@link Timer}，单位为毫秒。值越小，理论帧率越高；
	 * 值越大，刷新次数越少，动画更省资源但更容易不连贯。当前默认值为 12。
	 * 在 Ubuntu/Linux 桌面环境下，Swing Timer 和 Java2D 刷新可能受桌面合成器、
	 * X11/Wayland、显卡驱动和 EDT 负载影响；继续低于 10ms 通常不会稳定提升观感。
	 * 实用范围通常是 12 ~ 17，分别约等于 83 ~ 59 FPS。
	 *
	 * @param delay 刷新间隔毫秒数，必须大于 0
	 */
	public void setIndeterminateTimerDelay(int delay) {
		if (delay <= 0) {
			throw new IllegalArgumentException("delay must be greater than 0");
		}
		indeterminateTimer.setDelay(delay);
		indeterminateTimer.setInitialDelay(delay);
	}

	/**
	 * 设置相邻小球之间的时间偏移。
	 *
	 * <p>这是调节小球队列松紧度的主要参数，单位为秒。值越小，相邻小球越贴近；
	 * 值越大，队列越拉开。当前默认值为 0.06。对当前 5px 小球和三段式速度曲线来说，
	 * 0.05 ~ 0.08 通常比较自然；如果中间段太挤，增大该值；如果中间段太散，减小该值。
	 * 修改后会重置不确定动画起点，使新间距立即稳定生效。
	 *
	 * @param seconds 相邻小球的时间偏移秒数，必须大于等于 0
	 */
	public void setIndeterminateBallDelaySeconds(double seconds) {
		if (seconds < 0D) {
			throw new IllegalArgumentException("seconds must be greater than or equal to 0");
		}
		this.ballDelaySeconds = seconds;
		resetIndeterminateAnimation();
	}

	/**
	 * 设置每轮动画结束后的空白停顿时间。
	 *
	 * <p>单位为秒。值越大，最后一颗小球离开后到下一轮第一颗小球出现之间的间隔越明显；
	 * 值越小，动画循环越连续。当前默认值为 0.2。若希望等待动画一直“流动”，
	 * 可调到 0 ~ 0.1；若希望每轮有清晰间隔，可调到 0.2 以上。
	 * 修改后会重置不确定动画起点。
	 *
	 * @param seconds 每轮循环后的空白时间秒数，必须大于等于 0
	 */
	public void setIndeterminateCycleRestSeconds(double seconds) {
		if (seconds < 0D) {
			throw new IllegalArgumentException("seconds must be greater than or equal to 0");
		}
		this.cycleRestSeconds = seconds;
		resetIndeterminateAnimation();
	}

	/**
	 * 设置三段式速度曲线中“两端高速”和“中段匀速”的速度比例。
	 *
	 * <p>动画按路程分为三段：前 1/3 高速入场并持续减速，中间 1/3 匀速，
	 * 最后 1/3 对称加速离场。该比例表示入场/离场瞬时速度是中段匀速速度的多少倍。
	 * 值越大，两端越快、中间越慢，小球在中段越容易靠拢；值越接近 1，整体越接近匀速。
	 * 当前默认值为 4.0。原闪屏硬编码数据约为 530 : 162.5，即 3.26。
	 * 通常建议在 2.5 ~ 4.5 之间调节。
	 * 修改后会重置不确定动画起点。
	 *
	 * @param ratio 两端速度与中段速度的比例，必须大于等于 1
	 */
	public void setIndeterminateEdgeToMiddleSpeedRatio(double ratio) {
		if (ratio < 1D) {
			throw new IllegalArgumentException("ratio must be greater than or equal to 1");
		}
		this.edgeToMiddleSpeedRatio = ratio;
		resetIndeterminateAnimation();
	}

	/**
	 * 设置小球横穿当前组件宽度所需的时间计算参数。
	 *
	 * <p>实际耗时按 {@code baseSeconds + width / widthDivisor} 计算，并限制在
	 * {@code minSeconds} 到 {@code maxSeconds} 之间。单位均为秒，{@code width} 为当前组件宽度像素。
	 * {@code baseSeconds} 越大，所有宽度下整体越慢；{@code widthDivisor} 越小，宽组件增加的耗时越多；
	 * {@code minSeconds} 和 {@code maxSeconds} 用于限制极窄或极宽组件下的速度。
	 * 当前默认值等价于 {@code min=1.15, max=1.9, base=1.05, widthDivisor=800}。
	 * 修改后会重置不确定动画起点。
	 *
	 * @param minSeconds 最短横穿耗时，必须大于 0
	 * @param maxSeconds 最长横穿耗时，必须大于等于 {@code minSeconds}
	 * @param baseSeconds 基础横穿耗时，必须大于等于 0
	 * @param widthDivisor 宽度耗时系数分母，必须大于 0
	 */
	public void setIndeterminateTravelSeconds(double minSeconds, double maxSeconds,
	                                          double baseSeconds, double widthDivisor) {
		if (minSeconds <= 0D) {
			throw new IllegalArgumentException("minSeconds must be greater than 0");
		}
		if (maxSeconds < minSeconds) {
			throw new IllegalArgumentException("maxSeconds must be greater than or equal to minSeconds");
		}
		if (baseSeconds < 0D) {
			throw new IllegalArgumentException("baseSeconds must be greater than or equal to 0");
		}
		if (widthDivisor <= 0D) {
			throw new IllegalArgumentException("widthDivisor must be greater than 0");
		}
		this.minTravelSeconds = minSeconds;
		this.maxTravelSeconds = maxSeconds;
		this.baseTravelSeconds = baseSeconds;
		this.travelWidthDivisor = widthDivisor;
		resetIndeterminateAnimation();
	}

	@Override
	public void addNotify() {
		super.addNotify();
		if (indeterminate) {
			indeterminateStartTime = System.currentTimeMillis();
			indeterminateTimer.start();
		}
	}

	@Override
	public void removeNotify() {
		indeterminateTimer.stop();
		super.removeNotify();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				indeterminate ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

		int w = getWidth();
		int h = getHeight();

		if (indeterminate) {
			paintIndeterminate(g2, w, h);
			g2.dispose();
			Toolkit.getDefaultToolkit().sync();
			return;
		}

		// 背景
		g2.setColor(QRColorsAndFonts.LINE_COLOR);
		g2.fillRect(0, Math.max(1, (h - height) / 2), w, height);

		// 已填充部分
		g2.setColor(QRColorsAndFonts.PRESS_COLOR);
		int fillWidth = w * value / 100;
		if (fillWidth > 0) {
			g2.fillRect(0, Math.max(1, (h - height) / 2), fillWidth, height);
		}

		g2.dispose();
	}

	private void updateIndeterminate() {
		if (!indeterminate || !isShowing()) {
			return;
		}
		updateBallPositions();
		repaint();
	}

	private void paintIndeterminate(Graphics2D g2, int width, int height) {
		updateBallPositions();
		int diameter = ballDiameter();
		int y = Math.max(0, (height - diameter) / 2);
		g2.setColor(QRColorsAndFonts.LIGHT_GREEN);
		for (double x : ballXes) {
			if (x > -diameter && x < width) {
				g2.fillOval((int) Math.round(x), y, diameter, diameter);
			}
		}
	}

	private void updateBallPositions() {
		int width = getWidth();
		if (width <= 0) {
			return;
		}
		double seconds = (System.currentTimeMillis() - indeterminateStartTime) / 1000D;
		double travelSeconds = travelSeconds(width);
		double cycleSeconds = travelSeconds + ballDelaySeconds * (ballXes.length - 1) + cycleRestSeconds;
		int diameter = ballDiameter();
		double startX = -diameter;
		double travelWidth = width + diameter;

		for (int i = 0; i < ballXes.length; i++) {
			double localSeconds = (seconds - ballDelaySeconds * i) % cycleSeconds;
			if (localSeconds < 0) {
				localSeconds += cycleSeconds;
			}
			if (localSeconds > travelSeconds) {
				ballXes[i] = width + diameter;
				continue;
			}
			double progress = localSeconds / travelSeconds;
			ballXes[i] = startX + travelWidth * speedCurve(progress);
		}
	}

	private int ballDiameter() {
		return ballDiameter;
	}

	private double travelSeconds(int width) {
		return Math.max(minTravelSeconds, Math.min(maxTravelSeconds, baseTravelSeconds + width / travelWidthDivisor));
	}

	private double speedCurve(double progress) {
		if (progress <= 0D) {
			return 0D;
		}
		if (progress >= 1D) {
			return 1D;
		}

		// 将路程分为三段：前 1/3 持续减速，中间 1/3 匀速，最后 1/3 对称加速。
		// 时间占比由速度比例反推，保证入场瞬时速度和离场瞬时速度一致。
		double ratio = edgeToMiddleSpeedRatio;
		double edgePhaseTime = 2D / (ratio + 5D);
		double middlePhaseTime = (ratio + 1D) / (ratio + 5D);
		double segmentDistance = 1D / 3D;
		double segmentAverageSpeed = (ratio + 1D) / 2D;

		if (progress < edgePhaseTime) {
			double t = progress / edgePhaseTime;
			double distanceInSegment = (ratio * t - (ratio - 1D) * t * t / 2D) / segmentAverageSpeed;
			return segmentDistance * distanceInSegment;
		}

		if (progress < edgePhaseTime + middlePhaseTime) {
			double t = (progress - edgePhaseTime) / middlePhaseTime;
			return segmentDistance + segmentDistance * t;
		}

		double t = (progress - edgePhaseTime - middlePhaseTime) / edgePhaseTime;
		double distanceInSegment = (t + (ratio - 1D) * t * t / 2D) / segmentAverageSpeed;
		return 2D * segmentDistance + segmentDistance * distanceInSegment;
	}

	private void resetIndeterminateAnimation() {
		indeterminateStartTime = System.currentTimeMillis();
		updateBallPositions();
		repaint();
	}
}
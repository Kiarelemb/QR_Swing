package swing.qr.kiarelemb.task;

import java.util.List;

/**
 * 后台任务进度事件。
 *
 * <p>事件可能只包含百分比、只包含阶段文本，或同时包含 current/total 和换算后的百分比。
 * 监听器收到该对象时一定已经位于 EDT。
 */
public record QRTaskProgress(Integer percent, Integer current, Integer total, String message) {

	/**
	 * 创建百分比进度事件。百分比会限制在 0 到 100。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskProgress progress = QRTaskProgress.percent(75);
	 * </code></pre>
	 *
	 * @param percent 百分比进度
	 * @return 进度事件
	 */
	public static QRTaskProgress percent(int percent) {
		return new QRTaskProgress(limit(percent), null, null, null);
	}

	/**
	 * 创建 current/total 进度事件，并自动换算百分比。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskProgress progress = QRTaskProgress.progress(done, total);
	 * </code></pre>
	 *
	 * @param current 当前进度值
	 * @param total   总进度值
	 * @return 进度事件
	 */
	public static QRTaskProgress progress(int current, int total) {
		int percent = total <= 0 ? 0 : (int) ((double) current / total * 100);
		return new QRTaskProgress(limit(percent), current, total, null);
	}

	/**
	 * 创建阶段描述事件。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskProgress progress = QRTaskProgress.message("正在保存...");
	 * </code></pre>
	 *
	 * @param message 阶段描述，可为 null
	 * @return 进度事件
	 */
	public static QRTaskProgress message(String message) {
		return new QRTaskProgress(null, null, null, message);
	}

	/**
	 * 合并同一批次中的最新进度字段和最新文字字段。
	 *
	 * <p>{@link javax.swing.SwingWorker#publish(Object[])} 可能把短时间内发布的多条事件合并到一次
	 * process 调用中。混合发布 message/progress 时，如果只取最后一条，会丢失另一类字段。
	 *
	 * @param chunks 同一批次的进度事件
	 * @return 合并后的进度事件；没有有效字段时返回 null
	 */
	public static QRTaskProgress mergeLatest(List<QRTaskProgress> chunks) {
		if (chunks == null || chunks.isEmpty()) {
			return null;
		}

		Integer percent = null;
		Integer current = null;
		Integer total = null;
		String message = null;

		for (QRTaskProgress chunk : chunks) {
			if (chunk == null) {
				continue;
			}
			if (chunk.message() != null) {
				message = chunk.message();
			}
			if (chunk.percent() != null) {
				percent = chunk.percent();
				current = chunk.current();
				total = chunk.total();
			}
		}

		if (percent == null && message == null) {
			return null;
		}
		return new QRTaskProgress(percent, current, total, message);
	}

	private static int limit(int value) {
		return Math.max(0, Math.min(100, value));
	}
}

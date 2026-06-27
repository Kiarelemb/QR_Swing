package swing.qr.kiarelemb.task;

import java.util.concurrent.CancellationException;

/**
 * 后台任务上下文。
 *
 * <p>该对象会传入 {@link QRTask#run(QRTaskContext)}，可在后台线程中调用。
 * 进度和消息会由 {@link QRTaskWorker} 转发到 EDT 上的监听器。
 */
public interface QRTaskContext {
	/**
	 * 发布当前值和总值进度。百分比会自动限制在 0 到 100。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * for (int i = 0; i &lt; files.length; i++) {
	 *     context.checkCancelled();
	 *     process(files[i]);
	 *     context.progress(i + 1, files.length);
	 * }
	 * </code></pre>
	 *
	 * @param current 当前进度值
	 * @param total   总进度值，小于等于 0 时百分比按 0 处理
	 */
	void progress(int current, int total);

	/**
	 * 发布百分比进度。百分比会自动限制在 0 到 100。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * context.progress(50);
	 * </code></pre>
	 *
	 * @param percent 百分比进度
	 */
	void progress(int percent);

	/**
	 * 发布阶段描述文本。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * context.message("正在生成结果...");
	 * </code></pre>
	 *
	 * @param message 阶段描述，可为 null
	 */
	void message(String message);

	/**
	 * 返回当前任务是否已经被请求取消。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * if (context.isCancelled()) {
	 *     return null;
	 * }
	 * </code></pre>
	 *
	 * @return 已请求取消时返回 true
	 */
	boolean isCancelled();

	/**
	 * 如果任务已经被请求取消，则抛出 {@link CancellationException}。
	 *
	 * <p>长循环、目录扫描、网络或磁盘批处理应定期调用该方法。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * while (hasNext()) {
	 *     context.checkCancelled();
	 *     handleNext();
	 * }
	 * </code></pre>
	 */
	default void checkCancelled() {
		if (isCancelled()) {
			throw new CancellationException("Task cancelled");
		}
	}
}

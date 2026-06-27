package swing.qr.kiarelemb.task;

/**
 * 后台任务状态。
 *
 * <p>使用例：
 * <pre><code>
 * if (worker.status() == QRTaskStatus.SUCCEEDED) {
 *     System.out.println("任务已成功");
 * }
 * </code></pre>
 */
public enum QRTaskStatus {
	PENDING,
	RUNNING,
	SUCCEEDED,
	FAILED,
	CANCELLED
}

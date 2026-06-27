package swing.qr.kiarelemb.task;

/**
 * 后台任务完成结果。
 *
 * <p>该对象只表达终态：成功、失败或取消。失败时保留原始异常对象，
 * 取消时 {@link #value()} 和 {@link #throwable()} 都为 null。
 *
 * @param <T> 任务结果类型
 */
public class QRTaskResult<T> {
	private final QRTaskStatus status;
	private final T value;
	private final Throwable throwable;

	private QRTaskResult(QRTaskStatus status, T value, Throwable throwable) {
		this.status = status;
		this.value = value;
		this.throwable = throwable;
	}

	/**
	 * 创建成功结果。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskResult&lt;String&gt; result = QRTaskResult.succeeded("完成");
	 * </code></pre>
	 *
	 * @param value 成功结果值
	 * @param <T>   任务结果类型
	 * @return 成功结果
	 */
	public static <T> QRTaskResult<T> succeeded(T value) {
		return new QRTaskResult<>(QRTaskStatus.SUCCEEDED, value, null);
	}

	/**
	 * 创建失败结果。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskResult&lt;String&gt; result = QRTaskResult.failed(throwable);
	 * </code></pre>
	 *
	 * @param throwable 失败原因
	 * @param <T>       任务结果类型
	 * @return 失败结果
	 */
	public static <T> QRTaskResult<T> failed(Throwable throwable) {
		return new QRTaskResult<>(QRTaskStatus.FAILED, null, throwable);
	}

	/**
	 * 创建取消结果。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskResult&lt;String&gt; result = QRTaskResult.cancelled();
	 * </code></pre>
	 *
	 * @param <T> 任务结果类型
	 * @return 取消结果
	 */
	public static <T> QRTaskResult<T> cancelled() {
		return new QRTaskResult<>(QRTaskStatus.CANCELLED, null, null);
	}

	/**
	 * 返回任务终态。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * if (result.status() == QRTaskStatus.SUCCEEDED) {
	 *     use(result.value());
	 * }
	 * </code></pre>
	 *
	 * @return 任务终态
	 */
	public QRTaskStatus status() {
		return status;
	}

	/**
	 * 返回成功结果值。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * String value = result.value();
	 * </code></pre>
	 *
	 * @return 成功结果值；失败或取消时为 null
	 */
	public T value() {
		return value;
	}

	/**
	 * 返回失败异常。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * Throwable error = result.throwable();
	 * </code></pre>
	 *
	 * @return 失败异常；成功或取消时为 null
	 */
	public Throwable throwable() {
		return throwable;
	}
}

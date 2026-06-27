package swing.qr.kiarelemb.task;

/**
 * 后台任务完成结果。
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

	public static <T> QRTaskResult<T> succeeded(T value) {
		return new QRTaskResult<>(QRTaskStatus.SUCCEEDED, value, null);
	}

	public static <T> QRTaskResult<T> failed(Throwable throwable) {
		return new QRTaskResult<>(QRTaskStatus.FAILED, null, throwable);
	}

	public static <T> QRTaskResult<T> cancelled() {
		return new QRTaskResult<>(QRTaskStatus.CANCELLED, null, null);
	}

	public QRTaskStatus status() {
		return status;
	}

	public T value() {
		return value;
	}

	public Throwable throwable() {
		return throwable;
	}
}

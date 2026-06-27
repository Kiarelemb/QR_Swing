package swing.qr.kiarelemb.task;

/**
 * 后台任务进度事件。
 */
public record QRTaskProgress(Integer percent, Integer current, Integer total, String message) {

	public static QRTaskProgress percent(int percent) {
		return new QRTaskProgress(limit(percent), null, null, null);
	}

	public static QRTaskProgress progress(int current, int total) {
		int percent = total <= 0 ? 0 : (int) ((double) current / total * 100);
		return new QRTaskProgress(limit(percent), current, total, null);
	}

	public static QRTaskProgress message(String message) {
		return new QRTaskProgress(null, null, null, message);
	}


	private static int limit(int value) {
		return Math.max(0, Math.min(100, value));
	}
}
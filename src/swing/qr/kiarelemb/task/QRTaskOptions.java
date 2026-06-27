package swing.qr.kiarelemb.task;

import java.awt.*;
import java.util.function.Consumer;

/**
 * 后台任务执行选项。所有回调都在 EDT 执行。
 */
public class QRTaskOptions {
	private Window owner;
	private String title = "正在处理";
	private String description = "正在处理...";
	private boolean showProgressDialog = false;
	private boolean parentUnable = false;
	private boolean cancellable = true;
	private boolean autoCloseDialog = true;
	private boolean indeterminate = false;
	private Runnable onStarted;
	private Consumer<QRTaskProgress> onProgress;
	private Consumer<Object> onSuccess;
	private Consumer<Throwable> onError;
	private Runnable onCancelled;
	private Consumer<QRTaskResult<?>> onFinished;

	public Window owner() {
		return owner;
	}

	/**
	 * 设置进度对话框所属窗口。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions().owner(this);
	 * </code></pre>
	 *
	 * @param owner 所属窗口，可为 null
	 * @return 当前选项对象
	 */
	public QRTaskOptions owner(Window owner) {
		this.owner = owner;
		return this;
	}

	public String title() {
		return title;
	}

	/**
	 * 设置进度对话框标题。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions().title("导出文件");
	 * </code></pre>
	 *
	 * @param title 标题，空白时使用默认标题
	 * @return 当前选项对象
	 */
	public QRTaskOptions title(String title) {
		this.title = title == null || title.isBlank() ? "正在处理" : title;
		return this;
	}

	public String description() {
		return description;
	}

	/**
	 * 设置进度对话框初始阶段描述。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions().description("正在准备...");
	 * </code></pre>
	 *
	 * @param description 初始阶段描述，null 会转为空字符串
	 * @return 当前选项对象
	 */
	public QRTaskOptions description(String description) {
		this.description = description == null ? "" : description;
		return this;
	}

	public boolean showProgressDialog() {
		return showProgressDialog;
	}

	/**
	 * 设置是否显示进度对话框。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions().showProgressDialog(true);
	 * </code></pre>
	 *
	 * @param showProgressDialog true 表示显示进度对话框
	 * @return 当前选项对象
	 */
	public QRTaskOptions showProgressDialog(boolean showProgressDialog) {
		this.showProgressDialog = showProgressDialog;
		return this;
	}

	public boolean parentUnable() {
		return parentUnable;
	}

	/**
	 * 设置显示进度对话框时是否禁用父窗口。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions()
	 *         .showProgressDialog(true)
	 *         .parentUnable(true);
	 * </code></pre>
	 *
	 * @param parentUnable true 表示禁用父窗口
	 * @return 当前选项对象
	 */
	public QRTaskOptions parentUnable(boolean parentUnable) {
		this.parentUnable = parentUnable;
		return this;
	}

	public boolean cancellable() {
		return cancellable;
	}

	/**
	 * 设置进度对话框取消按钮是否可用。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions().cancellable(false);
	 * </code></pre>
	 *
	 * @param cancellable true 表示允许用户请求取消
	 * @return 当前选项对象
	 */
	public QRTaskOptions cancellable(boolean cancellable) {
		this.cancellable = cancellable;
		return this;
	}

	public boolean autoCloseDialog() {
		return autoCloseDialog;
	}

	/**
	 * 设置任务结束后是否自动关闭进度对话框。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions().autoCloseDialog(false);
	 * </code></pre>
	 *
	 * @param autoCloseDialog true 表示结束后自动关闭
	 * @return 当前选项对象
	 */
	public QRTaskOptions autoCloseDialog(boolean autoCloseDialog) {
		this.autoCloseDialog = autoCloseDialog;
		return this;
	}

	public boolean indeterminate() {
		return indeterminate;
	}

	/**
	 * 设置进度对话框是否使用不确定进度动画。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions()
	 *         .showProgressDialog(true)
	 *         .indeterminate(true);
	 * </code></pre>
	 *
	 * @param indeterminate true 表示未知总量任务
	 * @return 当前选项对象
	 */
	public QRTaskOptions indeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
		return this;
	}

	public Runnable onStarted() {
		return onStarted;
	}

	/**
	 * 设置任务开始回调。该回调在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions()
	 *         .onStarted(() -> button.setEnabled(false));
	 * </code></pre>
	 *
	 * @param onStarted 开始回调
	 * @return 当前选项对象
	 */
	public QRTaskOptions onStarted(Runnable onStarted) {
		this.onStarted = onStarted;
		return this;
	}

	public Consumer<QRTaskProgress> onProgress() {
		return onProgress;
	}

	/**
	 * 设置进度回调。该回调在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions()
	 *         .onProgress(progress -> {
	 *             if (progress.message() != null) {
	 *                 label.setText(progress.message());
	 *             }
	 *         });
	 * </code></pre>
	 *
	 * @param onProgress 进度回调
	 * @return 当前选项对象
	 */
	public QRTaskOptions onProgress(Consumer<QRTaskProgress> onProgress) {
		this.onProgress = onProgress;
		return this;
	}

	public Consumer<Object> onSuccess() {
		return onSuccess;
	}

	/**
	 * 设置成功回调。该回调在 EDT 执行。
	 *
	 * <p>当前类为非泛型选项对象，内部使用 Object 保存回调；调用端通过该方法传入具体类型。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions()
	 *         .onSuccess((String result) -> label.setText(result));
	 * </code></pre>
	 *
	 * @param onSuccess 成功回调
	 * @param <T>       任务结果类型
	 * @return 当前选项对象
	 */
	@SuppressWarnings("unchecked")
	public <T> QRTaskOptions onSuccess(Consumer<T> onSuccess) {
		this.onSuccess = (Consumer<Object>) onSuccess;
		return this;
	}

	public Consumer<Throwable> onError() {
		return onError;
	}

	/**
	 * 设置失败回调。该回调在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions()
	 *         .onError(error -> label.setText("失败：" + error.getMessage()));
	 * </code></pre>
	 *
	 * @param onError 失败回调
	 * @return 当前选项对象
	 */
	public QRTaskOptions onError(Consumer<Throwable> onError) {
		this.onError = onError;
		return this;
	}

	public Runnable onCancelled() {
		return onCancelled;
	}

	/**
	 * 设置取消回调。该回调在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions()
	 *         .onCancelled(() -> label.setText("已取消"));
	 * </code></pre>
	 *
	 * @param onCancelled 取消回调
	 * @return 当前选项对象
	 */
	public QRTaskOptions onCancelled(Runnable onCancelled) {
		this.onCancelled = onCancelled;
		return this;
	}

	public Consumer<QRTaskResult<?>> onFinished() {
		return onFinished;
	}

	/**
	 * 设置结束回调。成功、失败或取消后都会调用。该回调在 EDT 执行。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * QRTaskOptions options = new QRTaskOptions()
	 *         .onFinished(result -> button.setEnabled(true));
	 * </code></pre>
	 *
	 * @param onFinished 结束回调
	 * @return 当前选项对象
	 */
	public QRTaskOptions onFinished(Consumer<QRTaskResult<?>> onFinished) {
		this.onFinished = onFinished;
		return this;
	}
}

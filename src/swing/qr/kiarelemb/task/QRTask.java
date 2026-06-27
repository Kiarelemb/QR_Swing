package swing.qr.kiarelemb.task;

/**
 * 后台任务接口。
 *
 * <p>{@link #run(QRTaskContext)} 在后台线程执行，不要在该方法中直接读写 Swing 组件。
 *
 * <p>使用例：
 * <pre><code>
 * QRTask&lt;String&gt; task = context -> {
 *     context.message("正在读取...");
 *     context.progress(30);
 *     context.checkCancelled();
 *     return "完成";
 * };
 * QRTaskRunner.run(task);
 * </code></pre>
 *
 * @param <T> 任务结果类型
 */
@FunctionalInterface
public interface QRTask<T> {
	/**
	 * 执行后台任务。
	 *
	 * <p>该方法在后台线程执行。需要更新进度或阶段文本时调用 {@code context}，
	 * 不要在这里直接读写 Swing 组件。
	 *
	 * <p>使用例：
	 * <pre><code>
	 * String value = task.run(context);
	 * </code></pre>
	 *
	 * @param context 任务上下文
	 * @return 任务结果
	 * @throws Exception 任务失败时抛出，封装层会转发到失败回调
	 */
	T run(QRTaskContext context) throws Exception;
}

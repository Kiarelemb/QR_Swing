package swing.qr.kiarelemb.task;

/**
 * 后台任务接口。
 *
 * <p>{@link #run(QRTaskContext)} 在后台线程执行，不要在该方法中直接读写 Swing 组件。
 *
 * @param <T> 任务结果类型
 */
@FunctionalInterface
public interface QRTask<T> {
	T run(QRTaskContext context) throws Exception;
}

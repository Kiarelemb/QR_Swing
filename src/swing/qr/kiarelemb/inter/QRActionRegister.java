package swing.qr.kiarelemb.inter;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 13:53
 **/
public interface QRActionRegister<T> {
    /**
     * 每个控件所添加事件将调用该方法，可以在控件中单独实现该方法再完成一些操作
     */
    void action(T event);
}
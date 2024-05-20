package swing.qr.kiarelemb.inter;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-11 15:25
 **/
public interface QRBackgroundUpdate {
	/**
	 * 当添加了背景图片时，就需要不断更新文本面板，以确保它透明
	 *
	 * <p>确保窗体中有面板设置了 {@link swing.qr.kiarelemb.component.assembly.QRBackgroundBorder}
	 */
	void addCaretListenerForBackgroundUpdate();
}
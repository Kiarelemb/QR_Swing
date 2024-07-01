package swing.qr.kiarelemb.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-25 12:20
 **/
public class QRActionListener extends QRAction implements ActionListener {
	@Override
	public final void actionPerformed(ActionEvent e) {
		action(e);
	}
}
package swing.qr.kiarelemb.component.assembly;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 18:37
 **/
public class QRButtonGroup extends ButtonGroup {

	Map<AbstractButton, Integer> data = new HashMap<>();
	private int nextIndex = 0;

	@Override
	public void add(AbstractButton b) {
		super.add(b);
		data.put(b, nextIndex++);
	}

	public int getSelectedIndex() {
		final Set<AbstractButton> abstractButtons = data.keySet();
		for (AbstractButton abstractButton : abstractButtons) {
			if (abstractButton.isSelected()) {
				return data.get(abstractButton);
			}
		}
		return nextIndex - 1;
	}
}

package swing.qr.kiarelemb.drag;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-11 12:06
 **/
public abstract class QRStringTransferHandler extends TransferHandler {

	@Override
	protected Transferable createTransferable(JComponent c) {
		return new StringSelection(c.getClass().toString());
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	@Override
	public boolean importData(JComponent c, Transferable t) {
		if (canImport(c, t.getTransferDataFlavors())) {
			try {
				String str = (String) t.getTransferData(DataFlavor.stringFlavor);
				stringImportAction(str);
				return true;
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for (DataFlavor flavor : flavors) {
			if (DataFlavor.stringFlavor.equals(flavor)) {
				return true;
			}
		}
		return false;
	}

	protected abstract void stringImportAction(String str);
}
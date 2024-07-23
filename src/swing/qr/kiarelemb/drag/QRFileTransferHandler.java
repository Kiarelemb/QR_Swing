package swing.qr.kiarelemb.drag;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2023-01-11 11:07
 **/
public abstract class QRFileTransferHandler extends TransferHandler {

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new StringSelection(c.getClass().toString());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                List list = (List) t.getTransferData(DataFlavor.javaFileListFlavor);
                @SuppressWarnings("unchecked") File[] files = (File[]) list.toArray(new File[0]);
                fileImportAction(files);
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
            if (DataFlavor.javaFileListFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    protected abstract void fileImportAction(File[] files);
}
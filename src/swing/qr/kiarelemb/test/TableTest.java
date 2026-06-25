package swing.qr.kiarelemb.test;

import swing.qr.kiarelemb.QRSwing;
import swing.qr.kiarelemb.basic.QRTable;
import swing.qr.kiarelemb.window.basic.QRFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className TableTest
 * @create 2026/6/6 20:15
 */
public class TableTest extends QRFrame {
    public TableTest() {
        super("QRTable Test");
        setTitlePlace(SwingConstants.CENTER);
        setCloseButtonSystemExit();
        mainPanel.setLayout(new BorderLayout(10, 10));

        QRTable table = new QRTable(tableModel());
        table.setAutoCreateRowSorter(true);
        table.setToolTipText("QRTable");
        mainPanel.add(table.addScrollPane(), BorderLayout.CENTER);
    }

    private DefaultTableModel tableModel() {
        String[] columns = {"ID", "Name", "Type", "Status", "Updated"};
        Object[][] rows = new Object[24][columns.length];
        for (int i = 0; i < rows.length; i++) {
            rows[i][0] = i + 1;
            rows[i][1] = "Item " + (i + 1);
            rows[i][2] = i % 3 == 0 ? "File" : "Directory";
            rows[i][3] = i % 2 == 0 ? "Enabled" : "Disabled";
            rows[i][4] = "2026-06-" + String.format("%02d", (i % 28) + 1);
        }
        return new DefaultTableModel(rows, columns);
    }

    public static void main(String[] args) {
        QRSwing.start("setting.properties");
        QRSwing.setTheme("深色");
        QRSwing.setWindowTitleMenu(true);
        QRSwing.setWindowRound(false);
        QRSwing.registerGlobalKeyEvents();

        TableTest window = new TableTest();
        QRSwing.registerGlobalEventWindow(window);
        window.setVisible(true);
    }
}
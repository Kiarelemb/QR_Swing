package swing.qr.kiarelemb.combination;

import swing.qr.kiarelemb.basic.QRLabel;
import swing.qr.kiarelemb.basic.QRPanel;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-24 13:27
 **/
public class QRStatePanel extends QRPanel {

    protected QRPanel leftPane;
    protected QRPanel rightPane;
    protected QRPanel stateCenterPane;

    /**
     * 一个状态栏一般情况下分为三部分：左、中、右。其中，“中”又用得很少。
     */
    public QRStatePanel() {
        super(new BorderLayout());
        EmptyBorder border = new EmptyBorder(10, 10, 10, 10);
        setBorder(border);
        this.leftPane = new QRPanel(false);
        this.rightPane = new QRPanel(false);

        add(this.leftPane, BorderLayout.WEST);
        add(this.rightPane, BorderLayout.EAST);
        this.leftPane.setLayout(new GridLayout());
        this.rightPane.setLayout(new GridLayout());
    }

    public QRStatePanel(int hgap) {
        this();
        this.leftPane.setLayout(new GridLayout(1, 0, hgap, 0));
        this.rightPane.setLayout(new GridLayout(1, 0, hgap, 0));
        this.stateCenterPane.setLayout(new GridLayout(1, 0, hgap, 0));
    }

    public void centerPanel() {
        if (this.stateCenterPane == null) {
            this.stateCenterPane = new QRPanel(false);
            this.stateCenterPane.setLayout(new GridLayout());
            add(this.stateCenterPane, BorderLayout.CENTER);
        }
    }

    public QRStatePanel leftAdd(JComponent com) {
        this.leftPane.add(com);
        return this;
    }

    public QRStatePanel rightAdd(JComponent com) {
        this.rightPane.add(com);
        return this;
    }

    public QRStatePanel leftAddAndSplit(JComponent com) {
        return leftAdd(com).leftSplit();
    }

    public QRStatePanel rightAddAndSplit(JComponent com) {
        return rightAdd(com).rightSplit();
    }

    public QRStatePanel leftSplit() {
        return split(this.leftPane);
    }

    public QRStatePanel rightSplit() {
        return split(this.rightPane);
    }

    protected QRStatePanel split(QRPanel panel) {
        panel.add(new SplitLabel());
        return this;
    }

    /**
     * 一个用来分割的标签，颜色为光标的颜色
     */
    private static class SplitLabel extends QRLabel {
        public SplitLabel() {
            super(" | ");
        }

        @Override
        public void componentFresh() {
            setForeground(QRColorsAndFonts.CARET_COLOR);
        }
    }
}
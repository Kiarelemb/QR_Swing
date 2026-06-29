package swing.qr.kiarelemb.basic;

import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;

/**
 * QR Swing 的主题数字/选项微调框。
 *
 * <p>该类基于 {@link JSpinner}，统一编辑框字体、前景/背景色、边框和上下按钮样式。
 * 可传入任意 Swing {@link SpinnerModel}，例如 {@link SpinnerNumberModel}。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRSpinner spinner = new QRSpinner(new SpinnerNumberModel(10, 0, 100, 1));
 * int value = (Integer) spinner.getValue();
 * </code></pre>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2023-01-30 13:27
 **/
public class QRSpinner extends JSpinner implements QRComponentUpdate {


    public QRSpinner() {
        UIManager.put("Spinner.editorAlignment", SwingConstants.CENTER);
        componentFresh();
    }

    public QRSpinner(SpinnerModel model) {
        this();
        setModel(model);
    }

    @Override
    protected JComponent createEditor(SpinnerModel model) {
        DefaultEditor jc = (DefaultEditor) super.createEditor(model);
        jc.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        JFormattedTextField editor = jc.getTextField();
        editor.setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
        editor.setForeground(QRColorsAndFonts.MENU_COLOR);
        editor.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        return jc;
    }

    @Override
    public void componentFresh() {

        setUI(new QRBasicSpinnerUI());

        setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
        setForeground(QRColorsAndFonts.MENU_COLOR);
        setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
        setBorder(new LineBorder(QRColorsAndFonts.BORDER_COLOR, 1));

        DefaultEditor jc = (DefaultEditor) super.getEditor();
        jc.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);

        JFormattedTextField editor = jc.getTextField();
        editor.setFont(QRColorsAndFonts.DEFAULT_FONT_MENU);
        editor.setForeground(QRColorsAndFonts.MENU_COLOR);
        editor.setBackground(QRColorsAndFonts.FRAME_COLOR_BACK);
    }

    public static class QRBasicSpinnerUI extends BasicSpinnerUI {

        @Override
        protected Component createPreviousButton() {
            return getButton(true);
        }

        @Override
        protected Component createNextButton() {
            return getButton(false);
        }

        private QRButton getButton(boolean previous) {
            QRButton button = new QRButton(previous ? "  ▽  " : "  △  ");
            button.setFont(button.getFont().deriveFont(10f));
            button.setName(previous ? "Spinner.previousButton" : "Spinner.nextButton");
            if (previous) {
                installPreviousButtonListeners(button);
            } else {
                installNextButtonListeners(button);
            }
            return button;
        }
    }
}

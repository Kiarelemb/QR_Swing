package swing.qr.kiarelemb.component.assembly;

import method.qr.kiarelemb.utils.QRFontUtils;
import method.qr.kiarelemb.utils.QRMathUtils;
import swing.qr.kiarelemb.inter.QRComponentUpdate;
import swing.qr.kiarelemb.theme.QRColorsAndFonts;

import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description:
 * @create 2022-11-21 18:41
 **/
public class QRCaret extends DefaultCaret implements QRComponentUpdate {
    private int caratHeight;
    private Color caretColor;
    private int fontHeight = -1;
    private String fontName;
    private int fontSize;
    private Rectangle rec;

    public QRCaret() {
        setBlinkRate(500);
        componentFresh();
    }

    public QRCaret(Font font) {
        this.fontName = font.getName();
        this.fontSize = font.getSize();
        update();
        setBlinkRate(500);
        componentFresh();
    }

    public int caretHeight() {
        return this.caratHeight;
    }

    /**
     * 当光标可见时，该 {@link #rec} 总是更新
     */
    public Rectangle rectangle() {
        return rec;
    }

    public void setCaretColor(Color caretColor) {
        this.caretColor = caretColor;
    }

    public void update() {
        Font font = QRFontUtils.getFont(this.fontName, this.fontSize);
        BufferedImage buff = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Rectangle2D bounds;
        boolean allDisplay = QRFontUtils.fontCanAllDisplay("你", font);
        if (allDisplay) {
            bounds = font.getStringBounds("你", buff.createGraphics().getFontRenderContext());
        } else {
            bounds = font.getStringBounds("j", buff.createGraphics().getFontRenderContext());
        }
        this.fontHeight = QRMathUtils.doubleToInt(bounds.getHeight());
    }

    /**
     * 请及时调用 {@link QRCaret#update()} 方法
     *
     * @param font 字体
     */
    public QRCaret setFont(Font font) {
        this.fontName = font.getFontName();
        this.fontSize = font.getSize();
        return this;
    }

    /**
     * 请及时调用 {@link QRCaret#update()} 方法
     *
     * @param fontSize 字体大小
     */
    public QRCaret setFontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    /**
     * 请及时调用 {@link QRCaret#update()} 方法
     *
     * @param fontName 字体名称
     */
    public QRCaret setFontName(String fontName) {
        this.fontName = fontName;
        return this;
    }

    @Override
    protected final synchronized void damage(Rectangle r) {
        //让黑影仅为光标大小
        if (r != null) {
            this.x = r.x;
            this.y = r.y + this.fontSize / 10;
            this.width = 2;
            this.height = this.fontHeight;
            repaint();
        }
    }

    @Override
    protected void moveCaret(MouseEvent e) {
        try {
            super.moveCaret(e);
        } catch (Exception ex) {
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        try {
            super.mousePressed(e);
        } catch (Exception ex) {
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        try {
            super.mouseEntered(e);
        } catch (Exception ex) {
        }
    }

    @Override
    public void paint(Graphics g) {
        if (isVisible()) {
            JTextComponent comp = getComponent();
            if (comp == null) {
                return;
            }
            try {
                //noinspection deprecation
                rec = comp.modelToView(getDot());
                if (rec == null) {
                    return;
                }
            } catch (Exception e) {
                return;
            }
            g.setColor(this.caretColor);
            this.caratHeight = rec.height;
            g.fillRect(rec.x, rec.y + this.fontSize / 10, 2, this.fontHeight == -1 ? rec.height : this.fontHeight);
        }
    }

    @Override
    public void componentFresh() {
        this.caretColor = QRColorsAndFonts.CARET_COLOR;
    }
}
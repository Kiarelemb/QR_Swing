package swing.qr.kiarelemb.data;

import java.awt.*;
import java.io.Serializable;


/**
 * @param r       鼠标当前位置所对应的 {@link Rectangle}
 * @param line    行号，从1开始
 * @param row     列号，从1开始
 * @param objects 如果重写方法，则可以再传其它参数
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @apiNote: {@link swing.qr.kiarelemb.basic.QRTextPane} 中的高级方法
 * @create 2022-11-23 15:33
 */
public record QRMousePointIndexData(Point mousePoint, Rectangle r, Integer caretPosition, int line, int row,
                                    Object... objects) implements Serializable {
}
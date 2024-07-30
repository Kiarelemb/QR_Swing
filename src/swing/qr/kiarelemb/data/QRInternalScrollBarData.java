package swing.qr.kiarelemb.data;

import java.awt.*;

@Deprecated
public final class QRInternalScrollBarData {
    public double maxX;
    public double maxY;
    public double sx;
    public double sy;
    public double sw;
    public double sh;
    public double dragLocationY;
    public double dragLocationX;
    public boolean horizontalScrollbarVisible;
    public boolean verticalScrollbarVisible;
    public boolean mousePressedVertical;
    public boolean mousePressedHorizontal;
    public boolean mouseEnteredVertical;
    public boolean mouseEnteredHorizontal;
    public Dimension size = new Dimension(10, 10);
    public Dimension parentSize = new Dimension(10, 10);
    public Point location = new Point(0, 0);
    public Point pressPoint = new Point(0, 0);
}
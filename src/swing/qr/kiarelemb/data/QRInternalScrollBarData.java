package swing.qr.kiarelemb.data;

import java.awt.*;

/**
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @description: 滚动条数据
 * @create 2023-02-05 16:05
 **/
public final class QRInternalScrollBarData {
    public static final int BAR_SIZE = 10;
    public static final int MIN_THUMB_SIZE = 30;
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

    public void update(Dimension contentSize, Point contentLocation, Dimension viewportSize, boolean horizontalEnabled) {
        size = contentSize == null ? new Dimension() : new Dimension(contentSize);
        location = contentLocation == null ? new Point() : new Point(contentLocation);
        parentSize = viewportSize == null ? new Dimension() : new Dimension(viewportSize);

        updateVertical();
        updateHorizontal(horizontalEnabled);
    }

    private void updateVertical() {
        double viewportHeight = Math.max(0, parentSize.getHeight());
        double contentHeight = Math.max(0, size.getHeight());
        verticalScrollbarVisible = viewportHeight > 0 && contentHeight > viewportHeight;
        if (!verticalScrollbarVisible) {
            sy = 0;
            sh = viewportHeight;
            maxY = 0;
            mouseEnteredVertical = false;
            mousePressedVertical = false;
            return;
        }

        sh = Math.min(viewportHeight, Math.max(MIN_THUMB_SIZE, viewportHeight * viewportHeight / contentHeight));
        maxY = Math.max(0, viewportHeight - sh);
        sy = clamp(-location.y * maxY / Math.max(1, contentHeight - viewportHeight), 0, maxY);
    }

    private void updateHorizontal(boolean horizontalEnabled) {
        double viewportWidth = Math.max(0, parentSize.getWidth());
        double contentWidth = Math.max(0, size.getWidth());
        horizontalScrollbarVisible = horizontalEnabled && viewportWidth > 0 && contentWidth > viewportWidth;
        if (!horizontalScrollbarVisible) {
            sx = 0;
            sw = viewportWidth;
            maxX = 0;
            mouseEnteredHorizontal = false;
            mousePressedHorizontal = false;
            return;
        }

        sw = Math.min(viewportWidth, Math.max(MIN_THUMB_SIZE, viewportWidth * viewportWidth / contentWidth));
        maxX = Math.max(0, viewportWidth - sw);
        sx = clamp(-location.x * maxX / Math.max(1, contentWidth - viewportWidth), 0, maxX);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
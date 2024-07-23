package swing.qr.kiarelemb.basic;

import method.qr.kiarelemb.utils.QRSleepUtils;
import swing.qr.kiarelemb.data.QRInternalScrollBarData;
import swing.qr.kiarelemb.inter.QRInternalScrollbarUpdate;
import swing.qr.kiarelemb.inter.listener.add.QRDocumentListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseMotionListenerAdd;
import swing.qr.kiarelemb.inter.listener.add.QRMouseWheelListenerAdd;
import swing.qr.kiarelemb.listener.QRMouseListener;
import swing.qr.kiarelemb.listener.QRMouseMotionListener;
import swing.qr.kiarelemb.utils.QRComponentUtils;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kiarelemb
 * @projectName QR_Swing
 * @className QRInternalScrollPane
 * @description TODO
 * @create 2024/7/17 下午7:19
 */
@Deprecated
public class QRInternalScrollPane extends QRPanel {
    private JComponent component;
    private QRInternalScrollbarUpdate barData;
    private QRInternalScrollBarData data;
    private JEditorPane editor;

    public QRInternalScrollPane() {
        super(false, null);
        setBorder(null);
    }

    public QRInternalScrollPane(QRInternalScrollbarUpdate content) {
        this();
        setViewportView(content);
    }

    public void setViewportView(QRInternalScrollbarUpdate content) {
        if (!(content instanceof JComponent com)) {
            throw new UnsupportedOperationException("The content must be a JComponent");
        }
        if (content instanceof QRMouseListenerAdd ma && content instanceof QRMouseMotionListenerAdd mma && content instanceof QRMouseWheelListenerAdd mwa) {
            this.component = com;
            this.barData = content;
            this.data = barData.getScrollBarData();
            add(com);
            com.setLocation(0, 0);
            scrollBarAdd(ma, mma, mwa);
            return;
        }
        throw new UnsupportedOperationException("The content must be a QRMouseListenerAdd, QRMouseMotionListenerAdd, QRMouseWheelListenerAdd");
    }

    public QRInternalScrollbarUpdate getScrollBarDate() {
        return barData;
    }

    public JComponent getContentPane() {
        return component;
    }

    private void scrollBarAdd(QRMouseListenerAdd ma, QRMouseMotionListenerAdd mma, QRMouseWheelListenerAdd mwa) {
        ma.addMouseListener();
        mma.addMouseMotionListener();
        mwa.addMouseWheelListener();
        Highlighter highlighter;
        AtomicInteger editorCaretIndex = new AtomicInteger(0);
        if (ma instanceof QRDocumentListenerAdd da && component instanceof JEditorPane editor) {
            this.editor = editor;
            highlighter = editor.getHighlighter();
            da.addDocumentListener();
            editor.getCaret().addChangeListener(e -> {
                Document document = editor.getDocument();
                int length = document.getLength();
                Rectangle2D r;
                try {
                    r = editor.modelToView2D(length);
                    if (r == null) {
                        return;
                    }
                } catch (Exception ex) {
                    return;
                }
                Dimension size = editor.getSize();
                if (r.getY() <= size.getHeight()) {
                    return;
                }
                Insets insets = editor.getInsets();
                int height = (int) (r.getY() + r.getHeight() + insets.top + insets.bottom);
                if (height != size.height) {
                    editor.setSize(size.width, height);
                }
            });
//            QRActionRegister ar = ed -> QRComponentUtils.runLater(1000, es -> {
//            da.addDocumentListenerAction(QRDocumentListener.TYPE.INSERT, ar);
//            da.addDocumentListenerAction(QRDocumentListener.TYPE.REMOVE, ar);
//            da.addDocumentListenerAction(QRDocumentListener.TYPE.CHANGED, ar);
        } else {
            highlighter = null;
        }

        mwa.addMouseWheelAction(es -> {
            MouseWheelEvent e = es;
            int amount;
            if (component instanceof QRTextPane com) {
                amount = e.getScrollAmount() * com.caret.caretHeight();
            } else {
                amount = 50;
            }
            Point location = component.getLocation();
            QRComponentUtils.runLater(10, ed -> {
                int y;
                double extent = Math.max(amount / 40d, 2);
                if (e.getWheelRotation() > 0) {
                    int heightMin = -barData.getScrollBarData().size.height + getHeight();
                    // 向上滚
                    for (int i = 0; i < amount; i += (int) extent) {
                        y = location.y - i;
                        component.setLocation(location.x, Math.max(heightMin, y));
                        QRSleepUtils.sleep(2);
                    }
                    component.setLocation(location.x, Math.max(heightMin, location.y - amount));
                } else {
                    // 向下滚
                    for (int i = 0; i < amount; i += (int) extent) {
                        y = location.y + i;
                        component.setLocation(location.x, Math.min(0, y));
                        QRSleepUtils.sleep(2);
                    }
                    component.setLocation(location.x, Math.min(0, location.y + amount));
                }
            });
        });

        mma.addMouseMotionAction(QRMouseMotionListener.TYPE.MOVE, es -> {
            MouseEvent e = es;

            // 位置是相对于文本面板的顶点
            Point p = e.getPoint();
            data.mouseEnteredHorizontal = !(p.x < data.sx) && !(p.x > data.sx + data.sw) && p.y >= data.size.height - 10;
            double sly = data.sy - data.location.y;
            data.mouseEnteredVertical = p.x >= data.size.width - 10 && !(p.y < sly) && !(p.y > sly + data.sh);
            if (data.mouseEnteredHorizontal || data.mouseEnteredVertical) {
                component.setCursor(Cursor.getDefaultCursor());
            } else {
                component.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            }
            component.repaint();
        });

        mma.addMouseMotionAction(QRMouseMotionListener.TYPE.DRAG, es -> {
            MouseEvent e = es;
            if (!data.mouseEnteredVertical) {
                if (editor != null && editor.getHighlighter() == null) {
                    editor.setHighlighter(highlighter);
                }
                return;
            }
            if (editor != null) {
                editor.setHighlighter(null);
                editor.getCaret().setVisible(false);
                editor.setCaretPosition(editorCaretIndex.get());
            }
            int diff = e.getYOnScreen() - data.pressPoint.y;
            double count = diff * data.size.height / data.maxY;
            System.out.println("diff = " + diff);
            System.out.println("max = " + data.maxY);
            System.out.println("size.height = " + data.size.height);
            System.out.println("count = " + count);
            data.dragLocationY -= count;
            System.out.println("dragLocationY = " + data.dragLocationY);
            Point point = data.location;
            point.setLocation(data.location.x, Math.min(0, Math.max(-data.size.height + data.parentSize.height, data.dragLocationY)));

            component.setLocation(point);
            System.out.println(component.getLocation().y);
            System.out.println("----------------------------");
            data.pressPoint = e.getLocationOnScreen();
        });

        ma.addMouseAction(QRMouseListener.TYPE.PRESS, es -> {
            MouseEvent e = es;
            if (e.getButton() == MouseEvent.BUTTON1) {
                data.mousePressedVertical = data.mouseEnteredVertical;
                data.mousePressedHorizontal = data.mouseEnteredHorizontal;
                data.pressPoint = e.getLocationOnScreen();
                data.dragLocationY = component.getLocation().y;
                if (editor != null) {
                    editor.getCaret().setVisible(false);
                }
                component.repaint();
            }
        });

        ma.addMouseAction(QRMouseListener.TYPE.RELEASE, es -> {
            data.mousePressedVertical = false;
            if (editor != null) {
                editorCaretIndex.set(editor.getCaretPosition());
                editor.getCaret().setVisible(true);
            }
            component.repaint();
        });
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        if (component != null && barData.getScrollBarData() != null) {
            component.setBounds(0, Math.min(0, Math.max(-barData.getScrollBarData().size.height + height, y)), width, height);
            barData.scrollBarValueUpdate();
        }
    }
}
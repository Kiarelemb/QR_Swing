package swing.qr.kiarelemb.assembly;

import swing.qr.kiarelemb.event.QRRedoUndoEvent;
import swing.qr.kiarelemb.inter.QRActionRegister;
import swing.qr.kiarelemb.listener.QRRedoUndoListener;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * QR Swing 文本组件的撤销/重做管理器。
 *
 * <p>该类基于 Swing {@link UndoManager}，在构造时自动监听目标文本组件的文档变化，
 * 并为目标组件绑定 Ctrl+Z 撤销和 Ctrl+Y 重做。QR Swing 文本组件通常通过
 * {@code addUndoManager()} 创建本类实例。</p>
 *
 * <p>使用例：
 * <pre><code>
 * QRTextPane pane = new QRTextPane();
 * pane.addUndoManager();
 * pane.undoManager.addUndoActionListener();
 * pane.undoManager.addAfterUndoAction(event -> updateStatus());
 * </code></pre>
 *
 * <p>批量程序化修改文本时，可用 {@link #pause()} 暂停记录，完成后再调用 {@link #reStart()}。
 * 如果希望一次快捷键撤销多个编辑单元，可通过 {@link #setTimes(int)} 设置每次操作执行次数。</p>
 *
 * @author Kiarelemb QR
 * @program: QR_Swing
 * @create 2022-11-30 14:38
 **/
public class QRUndoManager extends UndoManager implements UndoableEditListener {
    protected final UndoAction undoAction;
    protected final RedoAction redoAction;
    private final String UNDO = "undo";
    private final String REDO = "redo";
    private final String CAN_UNDO = "cannot undo";
    private final String CAN_REDO = "cannot redo";
    private AbstractDocument.DefaultDocumentEvent edit;
    private int times = 1;
    private final JTextComponent aComponent;
    QRRedoUndoListener redoAfterActionListener;
    QRRedoUndoListener undoAfterActionListener;

    class UndoAction extends AbstractAction {
        public UndoAction() {
            super(CAN_UNDO);
            setEnabled(false);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl Z"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            undoAction();
        }

        void updateUndoState() {
            if (canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, UNDO);
            } else {
                setEnabled(false);
                putValue(Action.NAME, CAN_UNDO);
            }
        }

    }

    class RedoAction extends AbstractAction {

        public RedoAction() {
            super(CAN_REDO);
            setEnabled(false);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl Y"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            redoAction();
        }

        void updateRedoState() {
            if (canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, REDO);
            } else {
                setEnabled(false);
                putValue(Action.NAME, CAN_REDO);
            }
        }
    }

    /**
     * 为目标文本组件安装撤销/重做管理器。
     *
     * <p>构造后会自动监听 {@code comp.getDocument()}，并把 Ctrl+Z/Ctrl+Y 写入组件的
     * InputMap/ActionMap。</p>
     *
     * @param comp 目标文本组件
     */
    public QRUndoManager(JTextComponent comp) {
        setLimit(2000);
        undoAction = new UndoAction();
        redoAction = new RedoAction();
        this.aComponent = comp;
        comp.getDocument().addUndoableEditListener(this);
        comp.getInputMap().put((KeyStroke) undoAction.getValue(Action.ACCELERATOR_KEY), UNDO);
        comp.getInputMap().put((KeyStroke) redoAction.getValue(Action.ACCELERATOR_KEY), REDO);
        comp.getActionMap().put(UNDO, undoAction);
        comp.getActionMap().put(REDO, redoAction);
    }

    /**
     * 初始化 {@code Ctrl + Y} 重做后的事件监听器。
     *
     * <p>如果要使用 {@link #addAfterRedoAction(QRActionRegister)} 注册回调，应先调用该方法。</p>
     */
    public void addRedoActionListener() {
        if (redoAfterActionListener == null) {
            redoAfterActionListener = new QRRedoUndoListener();
            redoAfterActionListener.add(this::afterRedoAction);
        }
    }

    /**
     * 初始化 {@code Ctrl + Z} 撤销后的事件监听器。
     *
     * <p>如果要使用 {@link #addAfterUndoAction(QRActionRegister)} 注册回调，应先调用该方法。</p>
     */
    public void addUndoActionListener() {
        if (undoAfterActionListener == null) {
            undoAfterActionListener = new QRRedoUndoListener();
            undoAfterActionListener.add(this::afterUndoAction);
        }
    }

    /**
     * 添加重做完成后的动作。
     *
     * <p>动作参数中的编辑列表对应本次快捷键实际重做的编辑单元。调用前需先调用
     * {@link #addRedoActionListener()}，否则动作不会被保存。</p>
     *
     * @param ar 操作
     */
    public final void addAfterRedoAction(QRActionRegister<QRRedoUndoEvent> ar) {
        if (redoAfterActionListener != null) {
            redoAfterActionListener.add(ar);
        }
    }

    /**
     * 添加撤销完成后的动作。
     *
     * <p>动作参数中的编辑列表对应本次快捷键实际撤销的编辑单元。调用前需先调用
     * {@link #addUndoActionListener()}，否则动作不会被保存。</p>
     *
     * @param ar 操作
     */
    public final void addAfterUndoAction(QRActionRegister<QRRedoUndoEvent> ar) {
        if (undoAfterActionListener != null) {
            undoAfterActionListener.add(ar);
        }
    }

    /**
     * {@code Ctrl + Y} 事件
     * 重写前请先调用 {@link #addRedoActionListener()}
     */
    protected void afterRedoAction(QRRedoUndoEvent e) {
    }

    /**
     * {@code Ctrl + Z} 事件
     * 重写前请先调用 {@link #addUndoActionListener()}
     */
    protected void afterUndoAction(QRRedoUndoEvent e) {
    }

    /**
     * 设置每次快捷键触发时连续撤销/重做的编辑单元数量。
     *
     * <p>默认是 {@code 1}。例如某些输入法或程序化插入会产生多个小编辑单元，
     * 可以把该值调大，让一次 Ctrl+Z 回退更符合业务感知。</p>
     *
     * @param times 次数
     */
    public void setTimes(int times) {
        this.times = times;
    }

    /**
     * 暂停记录新的文档编辑。
     *
     * <p>适合程序批量修改文本且不希望用户撤销这些内部修改时使用。</p>
     */
    public void pause() {
        aComponent.getDocument().removeUndoableEditListener(this);
    }

    /**
     * 恢复记录新的文档编辑。
     */
    public void reStart() {
        aComponent.getDocument().addUndoableEditListener(this);
    }

    /**
     * 清除所有撤销/重做历史。
     */
    public void clear() {
        discardAllEdits();
    }

    /**
     * 撤销
     */
    private void undoAction() {
        ArrayList<AbstractDocument.DefaultDocumentEvent> list = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            if (canUndo()) {
                try {
                    undo();
                    list.add(edit);
                } catch (CannotUndoException ignore) {
                }
            }
        }
        undoAction.updateUndoState();
        redoAction.updateRedoState();
        if (undoAfterActionListener != null) {
            undoAfterActionListener.redoUndoAction(new QRRedoUndoEvent(list));
        }
    }

    /**
     * 重做
     */
    private void redoAction() {
        ArrayList<AbstractDocument.DefaultDocumentEvent> list = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            try {
                redo();
                list.add(edit);
            } catch (CannotRedoException ignore) {
            }
        }
        redoAction.updateRedoState();
        undoAction.updateUndoState();
        if (redoAfterActionListener != null) {
            redoAfterActionListener.redoUndoAction(new QRRedoUndoEvent(list));
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        addEdit(e.getEdit());
        undoAction.updateUndoState();
        redoAction.updateRedoState();
    }

    @Override
    protected void undoTo(UndoableEdit edit) throws CannotUndoException {
        QRUndoManager.this.edit = (AbstractDocument.DefaultDocumentEvent) edit;
        super.undoTo(edit);
    }

    @Override
    protected void redoTo(UndoableEdit edit) throws CannotRedoException {
        QRUndoManager.this.edit = (AbstractDocument.DefaultDocumentEvent) edit;
        super.redoTo(edit);
    }
}

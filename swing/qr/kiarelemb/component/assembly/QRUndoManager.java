package swing.qr.kiarelemb.component.assembly;

import swing.qr.kiarelemb.component.event.QRRedoUndoEvent;
import swing.qr.kiarelemb.component.listener.QRRedoUndoListener;
import swing.qr.kiarelemb.inter.QRActionRegister;

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
 * @author Kiarelemb QR
 * @program: EnglishAnalyzer
 * @description:
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
	 * ?????? {@code Ctrl + Y} ???????????????
	 */
	public void addRedoActionListener() {
		if (redoAfterActionListener == null) {
			redoAfterActionListener = new QRRedoUndoListener();
			redoAfterActionListener.add(e -> afterRedoAction((QRRedoUndoEvent) e));
		}
	}

	/**
	 * ?????? {@code Ctrl + Z} ???????????????
	 */
	public void addUndoActionListener() {
		if (undoAfterActionListener == null) {
			undoAfterActionListener = new QRRedoUndoListener();
			undoAfterActionListener.add(e -> afterUndoAction((QRRedoUndoEvent) e));
		}
	}

	/**
	 * ?????? {@code Ctrl + Y} ??????
	 *
	 * @param ar ??????
	 */
	public final void addAfterRedoAction(QRActionRegister ar) {
		if (redoAfterActionListener != null) {
			redoAfterActionListener.add(ar);
		}
	}

	/**
	 * ?????? {@code Ctrl + Z} ??????
	 *
	 * @param ar ??????
	 */
	public final void addAfterUndoAction(QRActionRegister ar) {
		if (undoAfterActionListener != null) {
			undoAfterActionListener.add(ar);
		}
	}

	/**
	 * {@code Ctrl + Y} ??????
	 * ????????????????????? {@link #addRedoActionListener()}
	 */
	protected void afterRedoAction(QRRedoUndoEvent e) {
	}

	/**
	 * {@code Ctrl + Z} ??????
	 * ????????????????????? {@link #addUndoActionListener()}
	 */
	protected void afterUndoAction(QRRedoUndoEvent e) {
	}

	/**
	 * ????????????????????????????????????????????? {@code 1} ???
	 *
	 * @param times ??????
	 */
	public void setTimes(int times) {
		this.times = times;
	}

	/**
	 * ????????????
	 */
	public void pause() {
		aComponent.getDocument().removeUndoableEditListener(this);
	}

	/**
	 * ????????????
	 */
	public void reStart() {
		aComponent.getDocument().addUndoableEditListener(this);
	}

	/**
	 * ??????????????????
	 */
	public void clear() {
		discardAllEdits();
	}

	/**
	 * ??????
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
	 * ??????
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

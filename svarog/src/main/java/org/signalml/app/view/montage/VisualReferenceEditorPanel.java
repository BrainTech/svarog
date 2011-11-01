/* EditMontageReferencePanel.java created 2007-10-24
 *
 */
package org.signalml.app.view.montage;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.signalml.app.montage.MontageTableModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.element.AnyChangeDocumentAdapter;
import org.signalml.app.view.element.CompactButton;
import org.signalml.domain.montage.Montage;

/** EditMontageReferencePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceEditorPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private Montage montage;

	private Action previousChannelAction;
	private Action nextChannelAction;

	private Action acceptWeightAction;
	private Action rejectWeightAction;

	private Action removeReferenceAction;

	private boolean weightTextFieldChanged;
	private JTextField weightTextField;

	private JCheckBox bipolarCheckBox;

	private JButton previousChannelButton;
	private JButton nextChannelButton;

	private JButton removeReferenceButton;

	private CompactButton acceptWeightButton;
	private CompactButton rejectWeightButton;

	private VisualReferenceModel editorModel;

	private VisualReferenceEditor editor;
//	private VisualReferenceEditorScrollable editorScrollable;
	private JScrollPane editorScrollPane;

	private MontageTableModel montageTableModel;
	private MontageTable montageTable;
	private JScrollPane montageTableScrollPane;

	public  VisualReferenceEditorPanel() {
		super();
		initialize();
	}

	private void initialize() {

		previousChannelAction = new PreviousChannelAction();
		nextChannelAction = new NextChannelAction();

		acceptWeightAction = new AcceptWeightAction();
		rejectWeightAction = new RejectWeightAction();

		removeReferenceAction = new RemoveReferenceAction();

		setLayout(new BorderLayout());

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBorder(new CompoundBorder(
		                             new TitledBorder(_("Target montage")),
		                             new EmptyBorder(3,3,3,3)
		                     ));

		JPanel editorPanel = new JPanel(new BorderLayout());
		editorPanel.setBorder(new CompoundBorder(
		                              new TitledBorder(_("Edit reference")),
		                              new EmptyBorder(3,3,3,3)
		                      ));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
		controlPanel.setBorder(new EmptyBorder(3,0,0,0));

		SwingUtils.makeButtonsSameSize(new JButton[] { getPreviousChannelButton(), getNextChannelButton() });

		controlPanel.add(getBipolarCheckBox());
		controlPanel.add(Box.createHorizontalStrut(5));
		controlPanel.add(Box.createHorizontalGlue());
		controlPanel.add(getPreviousChannelButton());
		controlPanel.add(Box.createHorizontalStrut(3));
		controlPanel.add(getNextChannelButton());
		controlPanel.add(Box.createHorizontalStrut(5));
		controlPanel.add(Box.createHorizontalGlue());
		controlPanel.add(new JLabel(_("Weight")));
		controlPanel.add(Box.createHorizontalStrut(3));
		controlPanel.add(getWeightTextField());
		controlPanel.add(Box.createHorizontalStrut(3));
		controlPanel.add(getAcceptWeightButton());
		controlPanel.add(Box.createHorizontalStrut(3));
		controlPanel.add(getRejectWeightButton());
		controlPanel.add(Box.createHorizontalStrut(5));
		controlPanel.add(getRemoveReferenceButton());

		editorPanel.add(controlPanel, BorderLayout.SOUTH);
		editorPanel.add(getEditorScrollPane(), BorderLayout.CENTER);

		tablePanel.add(getMontageTableScrollPane(), BorderLayout.CENTER);

		add(editorPanel, BorderLayout.CENTER);
		add(tablePanel, BorderLayout.EAST);

		KeyStroke space = KeyStroke.getKeyStroke("SPACE");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(space, "NEXT");
		getActionMap().put("NEXT", nextChannelAction);

		KeyStroke shiftSpace = KeyStroke.getKeyStroke("shift SPACE");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(shiftSpace, "PREVIOUS");
		getActionMap().put("PREVIOUS", previousChannelAction);

		KeyStroke del = KeyStroke.getKeyStroke("DELETE");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(del, "removeReference");
		getActionMap().put("removeReference", removeReferenceAction);

	}

	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			this.montage = montage;
			getMontageTableModel().setMontage(montage);
			getEditorModel().setMontage(montage);
		}
	}

	public MontageTableModel getMontageTableModel() {
		if (montageTableModel == null) {
			montageTableModel = new MontageTableModel();
		}
		return montageTableModel;
	}

	public MontageTable getMontageTable() {
		if (montageTable == null) {
			montageTable = new MontageTable(getMontageTableModel(), true);
			montageTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			montageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					int index = montageTable.getSelectedRow();
					if (index < 0) {
						getEditorModel().setActiveChannel(null);
					} else {
						getEditorModel().selectChannelAt(index);
						Rectangle rect = montageTable.getCellRect(index, 0, true);
						montageTable.scrollRectToVisible(rect);
					}

				}

			});

		}
		return montageTable;
	}

	public JScrollPane getMontageTableScrollPane() {
		if (montageTableScrollPane == null) {
			montageTableScrollPane = new JScrollPane(getMontageTable());
			montageTableScrollPane.setPreferredSize(new Dimension(150,100));
		}
		return montageTableScrollPane;
	}

	public VisualReferenceModel getEditorModel() {
		if (editorModel == null) {
			editorModel = new VisualReferenceModel();
			editorModel.addPropertyChangeListener(this);
		}
		return editorModel;
	}

	public VisualReferenceEditor getEditor() {
		if (editor == null) {
			editor = new VisualReferenceEditor(getEditorModel());
			editor.setBackground(Color.WHITE);
		}
		return editor;
	}

	/*
	public VisualReferenceEditorScrollable getEditorScrollable() {
		if( editorScrollable == null ) {
			editorScrollable = new VisualReferenceEditorScrollable( getEditor() );
		}
		return editorScrollable;
	}
	*/

	public JScrollPane getEditorScrollPane() {
		if (editorScrollPane == null) {
			editorScrollPane = new JScrollPane(getEditor(), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getEditor().setViewport(editorScrollPane.getViewport());
		}
		return editorScrollPane;
	}

	public JTextField getWeightTextField() {
		if (weightTextField == null) {

			weightTextField = new JTextField();
			weightTextField.setPreferredSize(new Dimension(100,22));
			weightTextField.setMaximumSize(new Dimension(100,22));
			weightTextField.setMinimumSize(new Dimension(100,22));

			weightTextField.getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {
				@Override
				public void anyUpdate(DocumentEvent e) {
					if (!weightTextFieldChanged) {
						weightTextFieldChanged = true;
						acceptWeightAction.setEnabled(true);
						rejectWeightAction.setEnabled(true);
					}
				}
			});

			weightTextField.setEnabled(false);

			KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
			KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

			weightTextField.getInputMap(JComponent.WHEN_FOCUSED).put(enter, "ACCEPT");
			weightTextField.getActionMap().put("ACCEPT", acceptWeightAction);

			weightTextField.getInputMap(JComponent.WHEN_FOCUSED).put(escape, "REJECT");
			weightTextField.getActionMap().put("REJECT", rejectWeightAction);

		}
		return weightTextField;
	}

	public JCheckBox getBipolarCheckBox() {
		if (bipolarCheckBox == null) {
			bipolarCheckBox = new JCheckBox(_("Bipolar mode"));

			bipolarCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getEditorModel().setBipolarMode(bipolarCheckBox.isSelected());
				}
			});

			bipolarCheckBox.setEnabled(false);
		}
		return bipolarCheckBox;
	}

	public JButton getPreviousChannelButton() {
		if (previousChannelButton == null) {
			previousChannelButton = new JButton(previousChannelAction);
			previousChannelButton.setHorizontalAlignment(JButton.CENTER);
			previousChannelButton.setContentAreaFilled(false);
			previousChannelButton.setMargin(new Insets(0,5,0,5));
		}
		return previousChannelButton;
	}

	public JButton getNextChannelButton() {
		if (nextChannelButton == null) {
			nextChannelButton = new JButton(nextChannelAction);
			nextChannelButton.setHorizontalTextPosition(JButton.LEADING);
			nextChannelButton.setHorizontalAlignment(JButton.CENTER);
			nextChannelButton.setContentAreaFilled(false);
			nextChannelButton.setMargin(new Insets(0,5,0,5));
		}
		return nextChannelButton;
	}

	public JButton getRemoveReferenceButton() {
		if (removeReferenceButton == null) {
			removeReferenceButton = new JButton(removeReferenceAction);
			removeReferenceButton.setContentAreaFilled(false);
			removeReferenceButton.setMargin(new Insets(0,5,0,5));
		}
		return removeReferenceButton;
	}

	public CompactButton getAcceptWeightButton() {
		if (acceptWeightButton == null) {
			acceptWeightButton = new CompactButton(acceptWeightAction);
		}
		return acceptWeightButton;
	}

	public CompactButton getRejectWeightButton() {
		if (rejectWeightButton == null) {
			rejectWeightButton = new CompactButton(rejectWeightAction);
		}
		return rejectWeightButton;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		VisualReferenceModel editorModel = getEditorModel();
		if (source == editorModel) {
			String name = evt.getPropertyName();
			if (VisualReferenceModel.BIPOLAR_COMPATIBLE_PROPERTY.equals(name)) {
				boolean bipolarCompatible = editorModel.isBipolarCompatible();
				JCheckBox checkBox = getBipolarCheckBox();
				if (!bipolarCompatible && checkBox.isSelected()) {
					checkBox.setSelected(false);
				}
				checkBox.setEnabled(bipolarCompatible);
			}
			else if (VisualReferenceModel.ACTIVE_ARROW_PROPERTY.equals(name)) {
				VisualReferenceArrow arrow = (VisualReferenceArrow) evt.getNewValue();
				JTextField textField = getWeightTextField();
				if (arrow == null) {
					textField.setText("");
					textField.setEnabled(false);
				} else {
					String weight = montage.getReference(arrow.getTargetChannel(), arrow.getSourceChannel());
					if (weight != null) {
						textField.setText(weight);
					} else {
						textField.setText("");
					}
					textField.setEnabled(true);
				}
				weightTextFieldChanged = false;
				acceptWeightAction.setEnabled(false);
				rejectWeightAction.setEnabled(false);
				removeReferenceAction.setEnabled(arrow != null);
			}
			else if (VisualReferenceModel.ACTIVE_CHANNEL_PROPERTY.equals(name)) {
				VisualReferenceChannel channel = (VisualReferenceChannel) evt.getNewValue();
				if (channel == null) {
					getMontageTable().clearSelection();
				} else {
					int index = getEditorModel().indexOfChannel(channel);
					if (index < 0) {
						getMontageTable().clearSelection();
					} else {
						getMontageTable().getSelectionModel().setSelectionInterval(index, index);
					}
				}
			}
		}
	}

	protected class PreviousChannelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public PreviousChannelAction() {
			super(_("Previous"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/previous.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Previous channel (Shift-Space)"));
		}

		public void actionPerformed(ActionEvent ev) {

			getEditorModel().selectPreviousChannel();

		}

	}

	protected class NextChannelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NextChannelAction() {
			super(_("Next"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/next.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Next channel (Space)"));
		}

		public void actionPerformed(ActionEvent ev) {

			getEditorModel().selectNextChannel();

		}

	}

	protected class RemoveReferenceAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveReferenceAction() {
			super(_("Remove"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removereference.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Remove this reference (Delete)"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			VisualReferenceModel model = getEditorModel();
			VisualReferenceArrow selArrow = model.getActiveArrow();
			if (selArrow == null) {
				return;
			}

			model.removeReference(selArrow.getTargetChannel(), selArrow.getSourceChannel());
			model.setActiveArrow(null);

		}

	}

	protected class AcceptWeightAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcceptWeightAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/ok.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Accept edit"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			VisualReferenceArrow arrow = getEditorModel().getActiveArrow();
			if (arrow == null) {
				return;
			}

			try {
				montage.setReference(arrow.getTargetChannel(), arrow.getSourceChannel(), weightTextField.getText());
			} catch (NumberFormatException ex) {
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				weightTextField.setText(montage.getReference(arrow.getTargetChannel(), arrow.getSourceChannel()));
				weightTextField.selectAll();
				weightTextField.requestFocusInWindow();
			}

			weightTextFieldChanged = false;
			acceptWeightAction.setEnabled(false);
			rejectWeightAction.setEnabled(false);

		}

	}

	protected class RejectWeightAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RejectWeightAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/cancel.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Discard edit"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			VisualReferenceArrow arrow = getEditorModel().getActiveArrow();
			if (arrow == null) {
				return;
			}

			weightTextField.setText(montage.getReference(arrow.getTargetChannel(), arrow.getSourceChannel()));
			weightTextFieldChanged = false;
			acceptWeightAction.setEnabled(false);
			rejectWeightAction.setEnabled(false);

		}

	}

}

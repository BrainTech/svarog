/* EditFFTSampleFilterDialog.java created 2008-02-03
 *
 */

package org.signalml.app.view.montage.filters;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.montage.FFTSampleFilterTableModel;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.common.components.spinners.DoubleSpinner;
import org.signalml.app.view.common.components.spinners.FloatSpinner;
import org.signalml.app.view.montage.filters.charts.FFTFilterResponseChartGroupPanel;
import org.signalml.app.view.montage.filters.charts.FrequencyRangeSelection;
import org.signalml.app.view.montage.filters.charts.elements.SelectionHighlightRenderer;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

/** EditFFTSampleFilterDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditFFTSampleFilterDialog extends EditSampleFilterDialog implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private static final double FREQUENCY_SPINNER_STEP_SIZE = 0.25;

	private FFTSampleFilter currentFilter;

	/**
	 * A {@link JTextField} which can be used to edit the filter's description.
	 */
	private JTextField descriptionTextField;

	private FFTSampleFilterTableModel tableModel;
	private FFTSampleFilterTable table;
	private JScrollPane tableScrollPane;

	private JPanel newRangePanel;

	private FloatSpinner fromFrequencySpinner;
	private FloatSpinner toFrequencySpinner;
	private DoubleSpinner coefficientSpinner;
	private JCheckBox unlimitedCheckBox;
	private JCheckBox multiplyCheckBox;

	private AddNewRangeAction addNewRangeAction;
	private RemoveRangeAction removeRangeAction;

	private JButton addNewRangeButton;
	private JButton removeRangeButton;

	private FFTWindowTypePanel fftWindowTypePanel;

	/**
	 * A panel for drawing and controling the filter responses.
	 * It contains all the charts visualizing the filter and associated
	 * spinners to control the maximum value shown on the x-axis.
	 */
	protected FFTFilterResponseChartGroupPanel graphsPanel;

	public EditFFTSampleFilterDialog(Window w, boolean isModal) {
		super(SvarogApplication.getManagerOfPresetsManagers().getFftFilterPresetManager(), w, isModal);
	}

	public EditFFTSampleFilterDialog(PresetManager presetManager) {
		super(presetManager);
	}

	@Override
	protected void initialize() {

		setTitle(_("Edit FFT sample filter"));

		addNewRangeAction = new AddNewRangeAction();
		removeRangeAction = new RemoveRangeAction();
		removeRangeAction.setEnabled(false);

		super.initialize();

	}

	@Override
	public JComponent createInterface() {

		CompoundBorder border;

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel descriptionPanel = getDescriptionPanel();
		JPanel graphPanel = getChartGroupPanelWithABorder();

		JPanel addNewRangePanel = new JPanel(new BorderLayout(3, 3));

		addNewRangePanel.setBorder(new TitledBorder(_("New range parameters")));

		JPanel addNewRangeButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 3));
		addNewRangeButtonPanel.add(getAddNewRangeButton());

		addNewRangePanel.add(getNewRangePanel(), BorderLayout.CENTER);
		addNewRangePanel.add(addNewRangeButtonPanel, BorderLayout.SOUTH);

		JPanel leftPanel = new JPanel(new BorderLayout());

		leftPanel.add(graphPanel, BorderLayout.NORTH);
		leftPanel.add(descriptionPanel, BorderLayout.CENTER);
		leftPanel.add(addNewRangePanel, BorderLayout.SOUTH);

		JPanel rightPanel = new JPanel(new BorderLayout(3, 3));

		border = new CompoundBorder(
			new TitledBorder(_("Ranges")),
			new EmptyBorder(3, 3, 3, 3)
		);
		rightPanel.setBorder(border);

		JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
		rightButtonPanel.add(getRemoveRangeButton());

		rightPanel.add(getTableScrollPane(), BorderLayout.CENTER);
		rightPanel.add(rightButtonPanel, BorderLayout.SOUTH);

		interfacePanel.add(leftPanel, BorderLayout.CENTER);
		interfacePanel.add(rightPanel, BorderLayout.EAST);
		interfacePanel.add(getFFTWindowTypePanel(), BorderLayout.SOUTH);

		return interfacePanel;

	}

	/**
	 * Returns the {@link JPanel} containing a {@link JTextField} for setting
	 * the description for the currently edited filter.
	 * @return the {@link JPanel} with controls to edit the filter's
	 * description
	 */
	public JPanel getDescriptionPanel() {

		JPanel descriptionPanel = new JPanel(new BorderLayout());
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Filter description")),
			new EmptyBorder(3, 3, 3, 3));
		descriptionPanel.setBorder(border);

		descriptionPanel.add(getDescriptionTextField());

		return descriptionPanel;

	}

	/**
	 * Returns the {@link JTextField} which is shown in this dialog and
	 * can be used to edit the filter's description.
	 * @return the {@link JTextField} to edit the filter's description
	 */
	public JTextField getDescriptionTextField() {

		if (descriptionTextField == null) {
			descriptionTextField = new JTextField();
			descriptionTextField.setPreferredSize(new Dimension(200, 25));
		}
		return descriptionTextField;

	}

	public FFTSampleFilterTableModel getTableModel() {

		if (tableModel == null) {
			tableModel = new FFTSampleFilterTableModel();
		}
		return tableModel;

	}

	public FFTSampleFilterTable getTable() {

		if (table == null) {
			table = new FFTSampleFilterTable(getTableModel());
			table.setPopupMenuProvider(new RangeTablePopupProvider());

			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					FFTSampleFilterTable filterTable = getTable();
					boolean enabled = (filterTable.getModel().getRowCount() > 1 && !(filterTable.getSelectionModel().isSelectionEmpty()));
					removeRangeAction.setEnabled(enabled);

				}

			});

			table.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					FFTSampleFilterTable table = (FFTSampleFilterTable) e.getSource();
					if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0) {
						int selRow = table.rowAtPoint(e.getPoint());
						if (selRow >= 0) {

							Range range = currentFilter.getRangeAt(selRow);

							float lowFrequency = range.getLowFrequency();
							float highFrequency = range.getHighFrequency();

							getFromFrequencySpinner().setValue((double) lowFrequency);
							if (highFrequency <= lowFrequency) {
								double maximumFrequency = getCurrentSamplingFrequency() / 2;
								getToFrequencySpinner().setValue(Math.max(lowFrequency + FREQUENCY_SPINNER_STEP_SIZE, maximumFrequency));
								getUnlimitedCheckBox().setSelected(true);
							} else {
								getToFrequencySpinner().setValue((double) highFrequency);
								getUnlimitedCheckBox().setSelected(false);
							}

							getCoefficientSpinner().setValue(range.getCoefficient());
							getMultiplyCheckBox().setSelected(false);

						}
					}
				}

			});

			table.setToolTipText(_("Double click a row to copy parameters to new range parameters panel"));

			KeyStroke del = KeyStroke.getKeyStroke("DELETE");
			table.getInputMap(JComponent.WHEN_FOCUSED).put(del, "remove");
			table.getActionMap().put("remove", removeRangeAction);

		}
		return table;

	}

	public JScrollPane getTableScrollPane() {

		if (tableScrollPane == null) {
			tableScrollPane = new JScrollPane(getTable());
			tableScrollPane.setPreferredSize(new Dimension(250, 300));
		}
		return tableScrollPane;

	}

	public JPanel getNewRangePanel() {

		if (newRangePanel == null) {

			newRangePanel = new JPanel(null);

			newRangePanel.setBorder(new EmptyBorder(3, 3, 3, 3));

			GroupLayout layout = new GroupLayout(newRangePanel);
			newRangePanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel fromFrequencyLabel = new JLabel(_("From (incl.) [Hz]"));
			JLabel toFrequencyLabel = new JLabel(_("To (excl.) [Hz]"));
			JLabel coefficientLabel = new JLabel(_("Coefficient"));
			JLabel unlimitedLabel = new JLabel(_("Up to Fn"));
			JLabel multiplyLabel = new JLabel(_("Multiply"));

			Component filler1 = Box.createRigidArea(new Dimension(1, 25));
			Component filler2 = Box.createRigidArea(new Dimension(1, 25));

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(fromFrequencyLabel)
				.addComponent(coefficientLabel)
			);

			hGroup.addGroup(
				layout.createParallelGroup(Alignment.TRAILING)
				.addComponent(getFromFrequencySpinner())
				.addComponent(getCoefficientSpinner())
			);

			hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(toFrequencyLabel)
				.addComponent(filler1)
			);

			hGroup.addGroup(
				layout.createParallelGroup(Alignment.TRAILING)
				.addComponent(getToFrequencySpinner())
				.addComponent(filler2)
			);

			hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(unlimitedLabel)
				.addComponent(multiplyLabel)
			);

			hGroup.addGroup(
				layout.createParallelGroup(Alignment.TRAILING)
				.addComponent(getUnlimitedCheckBox())
				.addComponent(getMultiplyCheckBox())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(fromFrequencyLabel)
				.addComponent(getFromFrequencySpinner())
				.addComponent(toFrequencyLabel)
				.addComponent(getToFrequencySpinner())
				.addComponent(unlimitedLabel)
				.addComponent(getUnlimitedCheckBox())
			);

			vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(coefficientLabel)
				.addComponent(getCoefficientSpinner())
				.addComponent(filler1)
				.addComponent(filler2)
				.addComponent(multiplyLabel)
				.addComponent(getMultiplyCheckBox())
			);

			layout.setVerticalGroup(vGroup);


		}
		return newRangePanel;

	}

	public FloatSpinner getFromFrequencySpinner() {

		if (fromFrequencySpinner == null) {
			fromFrequencySpinner = new FloatSpinner(new SpinnerNumberModel(0.0, 0.0, 4096.0, FREQUENCY_SPINNER_STEP_SIZE));
			fromFrequencySpinner.setPreferredSize(new Dimension(80, 25));

			fromFrequencySpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					double value = fromFrequencySpinner.getValue();

					double otherValue = getToFrequencySpinner().getValue();

					if (value >= otherValue) {
						getToFrequencySpinner().setValue(value + FREQUENCY_SPINNER_STEP_SIZE);
					}

					updateHighlights();
				}
			});

			fromFrequencySpinner.setEditor(new JSpinner.NumberEditor(fromFrequencySpinner, "0.00"));
			fromFrequencySpinner.setFont(fromFrequencySpinner.getFont().deriveFont(Font.PLAIN));

		}
		return fromFrequencySpinner;

	}

	public FloatSpinner getToFrequencySpinner() {

		if (toFrequencySpinner == null) {
			toFrequencySpinner = new FloatSpinner(new SpinnerNumberModel(FREQUENCY_SPINNER_STEP_SIZE, FREQUENCY_SPINNER_STEP_SIZE, 4096.0, FREQUENCY_SPINNER_STEP_SIZE));
			toFrequencySpinner.setPreferredSize(new Dimension(80, 25));

			toFrequencySpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					double value = ((Number) toFrequencySpinner.getValue()).doubleValue();

					double otherValue = ((Number) getFromFrequencySpinner().getValue()).doubleValue();

					if (value <= otherValue) {
						getFromFrequencySpinner().setValue(value - FREQUENCY_SPINNER_STEP_SIZE);
					}

					updateHighlights();
				}
			});


			toFrequencySpinner.setEditor(new JSpinner.NumberEditor(toFrequencySpinner, "0.00"));
			toFrequencySpinner.setFont(toFrequencySpinner.getFont().deriveFont(Font.PLAIN));

		}
		return toFrequencySpinner;

	}

	public JCheckBox getUnlimitedCheckBox() {

		if (unlimitedCheckBox == null) {
			unlimitedCheckBox = new JCheckBox();

			unlimitedCheckBox.addItemListener(new ItemListener() {

				private Float previousToFrequencyValue;

				@Override
				public void itemStateChanged(ItemEvent e) {
					boolean unlimited = getUnlimitedCheckBox().isSelected();
					double maximumFrequency = getCurrentSamplingFrequency() / 2;

					double newToFrequencySpinnerValue;
					if (unlimited) {
						float toFrequencyValue = getToFrequencySpinner().getValue();
						if (toFrequencyValue != maximumFrequency)
							previousToFrequencyValue = toFrequencyValue;
						newToFrequencySpinnerValue = maximumFrequency;
					} else if (previousToFrequencyValue != null
							&& previousToFrequencyValue != maximumFrequency) {
						newToFrequencySpinnerValue = previousToFrequencyValue;
					} else {
						newToFrequencySpinnerValue = maximumFrequency - FREQUENCY_SPINNER_STEP_SIZE;
					}

					getToFrequencySpinner().setValue(newToFrequencySpinnerValue);
					getToFrequencySpinner().setEnabled(!unlimited);
					updateHighlights();
				}

			});
		}
		return unlimitedCheckBox;

	}

	public DoubleSpinner getCoefficientSpinner() {

		if (coefficientSpinner == null) {
			coefficientSpinner = new DoubleSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.1));
			coefficientSpinner.setPreferredSize(new Dimension(80, 25));

			final JTextField editor = ((JTextField)((JSpinner.NumberEditor) coefficientSpinner.getEditor()).getComponent(0));

			KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
			editor.getInputMap(JComponent.WHEN_FOCUSED).put(enter, "add");
			editor.getActionMap().put("add", addNewRangeAction);

		}
		return coefficientSpinner;

	}

	public JCheckBox getMultiplyCheckBox() {

		if (multiplyCheckBox == null) {
			multiplyCheckBox = new JCheckBox();
		}
		return multiplyCheckBox;

	}

	public JButton getAddNewRangeButton() {

		if (addNewRangeButton == null) {
			addNewRangeButton = new JButton(addNewRangeAction);
		}
		return addNewRangeButton;

	}

	public JButton getRemoveRangeButton() {

		if (removeRangeButton == null) {
			removeRangeButton = new JButton(removeRangeAction);
		}
		return removeRangeButton;
	}

	public FFTWindowTypePanel getFFTWindowTypePanel() {
		if (fftWindowTypePanel == null) {
			fftWindowTypePanel = new FFTWindowTypePanel(true);
		}
		return fftWindowTypePanel;
	}

	@Override
	protected boolean updateGraph() {
		getChartGroupPanelWithABorder().updateGraphs(currentFilter);
		return true;
	}

	/**
	 * Updates the rectangle which highlights the selected frequency range.
	 */
	@Override
	protected void updateHighlights() {

		double startFrequency = getFromFrequencySpinner().getValue();
		double endFrequency = getToFrequencySpinner().getValue();
		FrequencyRangeSelection selection = new FrequencyRangeSelection(startFrequency, endFrequency);

		getChartGroupPanelWithABorder().setHighlightedSelection(selection);

	}

	@Override
	public Preset getPreset() throws SignalMLException {
		return currentFilter.duplicate();
	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {
		fillDialogFromModel(preset);
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		currentFilter = new FFTSampleFilter((FFTSampleFilter) model);

		getTableModel().setFilter(currentFilter);
		getFFTWindowTypePanel().fillPanelFromModel(currentFilter);

		getDescriptionTextField().setText(currentFilter.getDescription());

		updateGraph();

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		getFFTWindowTypePanel().fillModelFromPanel(currentFilter);

		currentFilter.setDescription(getDescriptionTextField().getText());

		// otherwise currentFilter should be up to date

		((FFTSampleFilter) model).copyFrom(currentFilter);

	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);
		getFFTWindowTypePanel().validatePanel(errors);

		String description = getDescriptionTextField().getText();
		if (description == null || description.isEmpty()) {
			errors.addError(_("A filter must have a description"));
		} else if (Util.hasSpecialChars(description)) {
			errors.addError(_("Filter description must not contain control characters"));
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return FFTSampleFilter.class.isAssignableFrom(clazz);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if (propertyName.equals(SelectionHighlightRenderer.SELECTION_CHANGED_PROPERTY)) {
			FrequencyRangeSelection selection = (FrequencyRangeSelection)evt.getNewValue();
			double lowerFrequency = selection.getLowerFrequency();
			double higherFrequency = selection.getHigherFrequency();

			getFromFrequencySpinner().setValue(lowerFrequency);
			getToFrequencySpinner().setValue(higherFrequency);

			if (higherFrequency == getMaximumFrequency())
				getUnlimitedCheckBox().setSelected(true);
			else
				getUnlimitedCheckBox().setSelected(false);
		}

	}

	protected class AddNewRangeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddNewRangeAction() {
			super(_("Add or replace range"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addfftrange.png"));
		}

		@Override
		public void actionPerformed(ActionEvent ev) {

			if (currentFilter == null) {
				return;
			}

			DoubleSpinner coefficientSpinner = getCoefficientSpinner();
			try {
				coefficientSpinner.commitEdit();
			} catch (ParseException pe) {
				UIManager.getLookAndFeel().provideErrorFeedback(coefficientSpinner);
			}

			FloatSpinner fromFrequencySpinner = getFromFrequencySpinner();
			try {
				fromFrequencySpinner.commitEdit();
			} catch (ParseException pe) {
				UIManager.getLookAndFeel().provideErrorFeedback(fromFrequencySpinner);
			}

			FloatSpinner toFrequencySpinner = getToFrequencySpinner();
			try {
				toFrequencySpinner.commitEdit();
			} catch (ParseException pe) {
				UIManager.getLookAndFeel().provideErrorFeedback(toFrequencySpinner);
			}

			float fromFrequency = fromFrequencySpinner.getValue();
			boolean unlimited = getUnlimitedCheckBox().isSelected();
			float toFrequency;
			if (!unlimited) {
				toFrequency = ((Number) toFrequencySpinner.getValue()).floatValue();
			} else {
				toFrequency = 0F;
			}
			double coefficient = ((Number) coefficientSpinner.getValue()).doubleValue();

			Range range = currentFilter.new Range(fromFrequency, toFrequency, coefficient);

			currentFilter.setRange(range, getMultiplyCheckBox().isSelected());
			getTableModel().onUpdate();

			updateGraph();

		}

	}

	protected class RemoveRangeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveRangeAction() {
			super(_("Remove range"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removefftrange.png"));
		}

		@Override
		public void actionPerformed(ActionEvent ev) {

			if (currentFilter == null) {
				return;
			}

			int selectedRow = getTable().getSelectedRow();
			if (selectedRow < 0) {
				return;
			}

			currentFilter.removeRange(selectedRow);

			FFTSampleFilterTableModel model = getTableModel();
			model.onUpdate();

			if (model.getRowCount() > 0) {
				getTable().getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
			}

			updateGraph();

		}

	}

	protected class RangeTablePopupProvider implements TablePopupMenuProvider {

		private JPopupMenu popupMenu;

		@Override
		public JPopupMenu getPopupMenu(int col, int row) {
			return getDefaultPopupMenu();
		}

		@Override
		public JPopupMenu getPopupMenu() {
			return getPopupMenu(-1, -1);
		}

		private JPopupMenu getDefaultPopupMenu() {

			if (popupMenu == null) {

				popupMenu = new JPopupMenu();

				popupMenu.add(removeRangeAction);

			}

			return popupMenu;

		}

	}

	@Override
	public void setCurrentSamplingFrequency(float currentSamplingFrequency) {
		super.setCurrentSamplingFrequency(currentSamplingFrequency);
		getChartGroupPanelWithABorder().setSamplingFrequency(currentSamplingFrequency);

		if (getToFrequencySpinner().getValue() >= getMaximumFrequency())
			getToFrequencySpinner().setValue(FREQUENCY_SPINNER_STEP_SIZE);
		if (this.getFromFrequencySpinner().getValue() >= getMaximumFrequency())
			getFromFrequencySpinner().setValue(0.0);
	}

	@Override
	public FFTFilterResponseChartGroupPanel getChartGroupPanelWithABorder() {
		if (graphsPanel == null) {
			graphsPanel = new FFTFilterResponseChartGroupPanel(currentFilter);
			graphsPanel.setSamplingFrequency(getCurrentSamplingFrequency());

			graphsPanel.addSelectionChangedListener(this);
		}
		return graphsPanel;
	}

}

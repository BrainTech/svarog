/* EditFFTSampleFilterDialog.java created 2008-02-03
 *
 */

package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
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
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.data.xy.DefaultXYDataset;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.montage.FFTSampleFilterTableModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.element.FFTWindowTypePanel;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter.Range;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** EditFFTSampleFilterDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditFFTSampleFilterDialog extends EditSampleFilterDialog {

	private static final long serialVersionUID = 1L;

	private FFTSampleFilter currentFilter;

	private FFTSampleFilterTableModel tableModel;
	private FFTSampleFilterTable table;
	private JScrollPane tableScrollPane;

	private JPanel newRangePanel;

	//private JTextField descriptionTextField;

	private JSpinner fromFrequencySpinner;
	private JSpinner toFrequencySpinner;
	private JSpinner coefficientSpinner;
	private JCheckBox unlimitedCheckBox;
	private JCheckBox multiplyCheckBox;

	private AddNewRangeAction addNewRangeAction;
	private RemoveRangeAction removeRangeAction;

	private JButton addNewRangeButton;
	private JButton removeRangeButton;

	private FFTWindowTypePanel fftWindowTypePanel;

	public EditFFTSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

	public EditFFTSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager) {
		super(messageSource, presetManager);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("editFFTSampleFilter.title"));

		addNewRangeAction = new AddNewRangeAction();
		removeRangeAction = new RemoveRangeAction();
		removeRangeAction.setEnabled(false);

		super.initialize();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				getFrequencyResponseChartPanel().setSelectionHighlightStart(((Number) getFromFrequencySpinner().getValue()).doubleValue());
				getFrequencyResponseChartPanel().setSelectionHighlightEnd(((Number) getToFrequencySpinner().getValue()).doubleValue());
			}

		});
	}

	@Override
	public JComponent createInterface() {

		CompoundBorder border;

		JPanel interfacePanel = new JPanel( new BorderLayout() );

		JPanel descriptionPanel = getDescriptionPanel();
		JPanel graphPanel = getGraphPanel();

		JPanel addNewRangePanel = new JPanel( new BorderLayout(3,3) );

		addNewRangePanel.setBorder( new TitledBorder( messageSource.getMessage("editFFTSampleFilter.addNewRangeTitle") ) );

		JPanel addNewRangeButtonPanel = new JPanel( new FlowLayout( FlowLayout.TRAILING, 3, 3 ) );
		addNewRangeButtonPanel.add( getAddNewRangeButton() );

		addNewRangePanel.add( getNewRangePanel(), BorderLayout.CENTER );
		addNewRangePanel.add( addNewRangeButtonPanel, BorderLayout.SOUTH );

		JPanel leftPanel = new JPanel( new BorderLayout() );

		leftPanel.add( descriptionPanel, BorderLayout.NORTH );
		leftPanel.add( graphPanel, BorderLayout.CENTER );
		leftPanel.add( addNewRangePanel, BorderLayout.SOUTH );

		JPanel rightPanel = new JPanel( new BorderLayout(3,3) );

		border = new CompoundBorder(
		        new TitledBorder( messageSource.getMessage("editFFTSampleFilter.rangesTitle") ),
		        new EmptyBorder(3,3,3,3)
		);
		rightPanel.setBorder( border );

		JPanel rightButtonPanel = new JPanel( new FlowLayout( FlowLayout.TRAILING, 0, 0 ) );
		rightButtonPanel.add( getRemoveRangeButton() );

		rightPanel.add( getTableScrollPane(), BorderLayout.CENTER );
		rightPanel.add( rightButtonPanel, BorderLayout.SOUTH );

		interfacePanel.add( leftPanel, BorderLayout.CENTER );
		interfacePanel.add( rightPanel, BorderLayout.EAST );
		interfacePanel.add( getFFTWindowTypePanel(), BorderLayout.SOUTH );

		return interfacePanel;

	}

	public FFTSampleFilterTableModel getTableModel() {
		if ( tableModel == null ) {
			tableModel = new FFTSampleFilterTableModel(messageSource);
		}
		return tableModel;
	}

	public FFTSampleFilterTable getTable() {
		if ( table == null ) {
			table = new FFTSampleFilterTable(getTableModel(),messageSource);
			table.setPopupMenuProvider( new RangeTablePopupProvider() );

			table.getSelectionModel().addListSelectionListener( new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					FFTSampleFilterTable filterTable = getTable();
					boolean enabled = ( filterTable.getModel().getRowCount() > 1 && !( filterTable.getSelectionModel().isSelectionEmpty() ) );
					removeRangeAction.setEnabled( enabled );

				}

			});

			table.addMouseListener( new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					FFTSampleFilterTable table = (FFTSampleFilterTable) e.getSource();
					if ( SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0 ) {
						int selRow = table.rowAtPoint(e.getPoint());
						if ( selRow >= 0 ) {

							Range range = currentFilter.getRangeAt(selRow);

							float lowFrequency = range.getLowFrequency();
							float highFrequency = range.getHighFrequency();

							getFromFrequencySpinner().setValue( (double) lowFrequency );
							if ( highFrequency <= lowFrequency ) {
								double scaleSpinnerValue = ((Number) getGraphScaleSpinner().getValue()).doubleValue();
								getToFrequencySpinner().setValue( Math.max( (double) lowFrequency + 0.25, scaleSpinnerValue) );
								getUnlimitedCheckBox().setSelected(true);
							} else {
								getToFrequencySpinner().setValue( (double) highFrequency );
								getUnlimitedCheckBox().setSelected(false);
							}

							getCoefficientSpinner().setValue( range.getCoefficient() );
							getMultiplyCheckBox().setSelected(false);

						}
					}
				}

			});

			table.setToolTipText( messageSource.getMessage("editFFTSampleFilter.tableToolTip") );

			KeyStroke del = KeyStroke.getKeyStroke("DELETE");
			table.getInputMap( JComponent.WHEN_FOCUSED ).put(del, "remove");
			table.getActionMap().put( "remove", removeRangeAction );

		}
		return table;
	}

	public JScrollPane getTableScrollPane() {
		if ( tableScrollPane == null ) {
			tableScrollPane = new JScrollPane(getTable());
			tableScrollPane.setPreferredSize(new Dimension(250,300));
		}
		return tableScrollPane;
	}

	public JPanel getNewRangePanel() {
		if ( newRangePanel == null ) {

			newRangePanel = new JPanel(null);

			newRangePanel.setBorder(new EmptyBorder(3,3,3,3));

			GroupLayout layout = new GroupLayout(newRangePanel);
			newRangePanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel fromFrequencyLabel = new JLabel(messageSource.getMessage("editFFTSampleFilter.fromFrequency"));
			JLabel toFrequencyLabel = new JLabel(messageSource.getMessage("editFFTSampleFilter.toFrequency"));
			JLabel coefficientLabel = new JLabel(messageSource.getMessage("editFFTSampleFilter.coefficient"));
			JLabel unlimitedLabel = new JLabel(messageSource.getMessage("editFFTSampleFilter.unlimited"));
			JLabel multiplyLabel = new JLabel(messageSource.getMessage("editFFTSampleFilter.multiply"));

			Component filler1 = Box.createRigidArea( new Dimension(1,25) );
			Component filler2 = Box.createRigidArea( new Dimension(1,25) );

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

	@Override
	public JSpinner getGraphScaleSpinner() {

		if (graphScaleSpinner == null) {

			graphScaleSpinner = super.getGraphScaleSpinner();
			graphScaleSpinner.addChangeListener( new SpinnerRoundingChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);

					graphFrequencyMax = ((Number) graphScaleSpinner.getValue()).doubleValue();
					updateGraph();

					if ( getUnlimitedCheckBox().isSelected() ) {
						getFrequencyResponseChartPanel().setSelectionHighlightEnd(graphFrequencyMax);
					}

				}

			});
		}
		return graphScaleSpinner;

	}

	@Override
	public NumberAxis getGainAxis() {
		if ( gainAxis == null ) {
			gainAxis = new NumberAxis();
			gainAxis.setAutoRange(false);
			gainAxis.setTickUnit( new NumberTickUnit(1) );
		}
		return gainAxis;
	}

	public JSpinner getFromFrequencySpinner() {
		if ( fromFrequencySpinner == null ) {
			fromFrequencySpinner = new JSpinner( new SpinnerNumberModel( 0.0, 0.0, 4096.0, 0.25 ) );
			fromFrequencySpinner.setPreferredSize( new Dimension(80,25) );

			fromFrequencySpinner.addChangeListener( new SpinnerRoundingChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);

					double value = ((Number) fromFrequencySpinner.getValue()).doubleValue();

					getFrequencyResponseChartPanel().setSelectionHighlightStart(value);

					double otherValue = ((Number) getToFrequencySpinner().getValue()).doubleValue();

					if ( value >= otherValue ) {
						getToFrequencySpinner().setValue( value + 0.25 );
					}

				}

			});

			fromFrequencySpinner.setEditor( new JSpinner.NumberEditor( fromFrequencySpinner, "0.00" ) );
			fromFrequencySpinner.setFont( fromFrequencySpinner.getFont().deriveFont( Font.PLAIN ) );

		}
		return fromFrequencySpinner;
	}

	public JSpinner getToFrequencySpinner() {
		if ( toFrequencySpinner == null ) {
			toFrequencySpinner = new JSpinner( new SpinnerNumberModel( 0.25, 0.25, 4096.0, 0.25 ) );
			toFrequencySpinner.setPreferredSize( new Dimension(80,25) );

			toFrequencySpinner.addChangeListener( new SpinnerRoundingChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);

					double value = ((Number) toFrequencySpinner.getValue()).doubleValue();

					if ( !getUnlimitedCheckBox().isSelected() ) {
						getFrequencyResponseChartPanel().setSelectionHighlightEnd(value);
					}

					double otherValue = ((Number) getFromFrequencySpinner().getValue()).doubleValue();

					if ( value <= otherValue ) {
						getFromFrequencySpinner().setValue( value - 0.25 );
					}

				}

			});

			toFrequencySpinner.setEditor( new JSpinner.NumberEditor( toFrequencySpinner, "0.00" ) );
			toFrequencySpinner.setFont( toFrequencySpinner.getFont().deriveFont( Font.PLAIN ) );

		}
		return toFrequencySpinner;
	}

	public JCheckBox getUnlimitedCheckBox() {
		if ( unlimitedCheckBox == null ) {
			unlimitedCheckBox = new JCheckBox();

			unlimitedCheckBox.addItemListener( new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					JSpinner spinner = getToFrequencySpinner();
					boolean unlimited = getUnlimitedCheckBox().isSelected();
					if ( unlimited ) {
						getFrequencyResponseChartPanel().setSelectionHighlightEnd(((Number) getGraphScaleSpinner().getValue()).doubleValue());
					} else {
						getFrequencyResponseChartPanel().setSelectionHighlightEnd(((Number) spinner.getValue()).doubleValue());
					}
					spinner.setEnabled( !unlimited );

				}

			});
		}
		return unlimitedCheckBox;
	}

	public JSpinner getCoefficientSpinner() {
		if ( coefficientSpinner == null ) {
			coefficientSpinner = new JSpinner( new SpinnerNumberModel( 0.0, 0.0, 100.0, 0.1 ) );
			coefficientSpinner.setPreferredSize( new Dimension(80,25) );

			final JTextField editor = ((JTextField) ((JSpinner.NumberEditor) coefficientSpinner.getEditor()).getComponent(0));

			KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
			editor.getInputMap( JComponent.WHEN_FOCUSED ).put(enter, "add");
			editor.getActionMap().put( "add", addNewRangeAction );

		}
		return coefficientSpinner;
	}

	public JCheckBox getMultiplyCheckBox() {
		if ( multiplyCheckBox == null ) {
			multiplyCheckBox = new JCheckBox();
		}
		return multiplyCheckBox;
	}

	public JButton getAddNewRangeButton() {
		if ( addNewRangeButton == null ) {
			addNewRangeButton = new JButton( addNewRangeAction );
		}
		return addNewRangeButton;
	}

	public JButton getRemoveRangeButton() {
		if ( removeRangeButton == null ) {
			removeRangeButton = new JButton( removeRangeAction );
		}
		return removeRangeButton;
	}

	public FFTWindowTypePanel getFFTWindowTypePanel() {
		if ( fftWindowTypePanel == null ) {
			fftWindowTypePanel = new FFTWindowTypePanel(messageSource, true);
		}
		return fftWindowTypePanel;
	}

	@Override
	protected void updateGraph() {

		if ( currentFilter == null ) {
			return;
		}

		int frequencyCnt = (int) Math.ceil( getGraphFrequencyMax() / 0.25 ) + 1;
		double[] frequencies = new double[frequencyCnt];
		double[] coefficients = new double[frequencyCnt];
		int i;
		double frequency = 0;

		for ( i=0; i<frequencyCnt; i++ ) {
			frequencies[i] = frequency;
			frequency += 0.25;
		}

		Iterator<Range> it = currentFilter.getRangeIterator();
		Range range;
		double limit;
		float lowFrequency;
		float highFrequency;
		double coefficient;
		double maxCoefficient = 0;

		while ( it.hasNext() ) {

			range = it.next();

			lowFrequency = range.getLowFrequency();
			if ( lowFrequency > getGraphFrequencyMax() ) {
				break;
			}

			highFrequency = range.getHighFrequency();
			coefficient = range.getCoefficient();

			if ( highFrequency <= lowFrequency ) {
				limit = getGraphFrequencyMax();
			} else {
				limit = Math.min( highFrequency, getGraphFrequencyMax() );
			}

			int index;
			for ( frequency=lowFrequency; frequency<=limit; frequency += 0.25 ) {

				index = (int) ( frequency / 0.25 );
				coefficients[index] = coefficient;

			}

			if ( coefficient > maxCoefficient ) {
				maxCoefficient = coefficient;
			}

		}

		maxCoefficient *= 1.1;
		if ( maxCoefficient < 1 ) {
			maxCoefficient  = 1;
		}

		double unit = Math.max( 4, Math.round( getGraphFrequencyMax() / (16*4) ) * 4 );
		NumberAxis axis = getFrequencyAxis();
		axis.setRange( 0, getGraphFrequencyMax() );
		axis.setTickUnit( new NumberTickUnit(unit) );

		getGainAxis().setRange( 0, maxCoefficient );

		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("data", new double[][] { frequencies, coefficients } );
		getFrequencyResponsePlot().setDataset( dataset );

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

		getDescriptionTextField().setText( currentFilter.getDescription() );

		updateGraph();

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		getFFTWindowTypePanel().fillModelFromPanel(currentFilter);

		currentFilter.setDescription( getDescriptionTextField().getText() );

		// otherwise currentFilter should be up to date

		((FFTSampleFilter) model).copyFrom(currentFilter);

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		getFFTWindowTypePanel().validatePanel(errors);

		String description = getDescriptionTextField().getText();
		if ( description == null || description.isEmpty() ) {
			errors.rejectValue( "description", "error.editFFTSampleFilter.descriptionEmpty" );
		}
		else if ( !Util.validateString(description) ) {
			errors.rejectValue( "description", "error.editFFTSampleFilter.descriptionBadChars" );
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return FFTSampleFilter.class.isAssignableFrom(clazz);
	}

	protected class AddNewRangeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddNewRangeAction() {
			super(messageSource.getMessage("editFFTSampleFilter.addNewRange"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addfftrange.png") );
		}

		public void actionPerformed(ActionEvent ev) {

			if ( currentFilter == null ) {
				return;
			}

			JSpinner coefficientSpinner = getCoefficientSpinner();
			try {
				coefficientSpinner.commitEdit();
			} catch (ParseException pe) {
				UIManager.getLookAndFeel().provideErrorFeedback(coefficientSpinner);
			}

			JSpinner fromFrequencySpinner = getFromFrequencySpinner();
			try {
				fromFrequencySpinner.commitEdit();
			} catch (ParseException pe) {
				UIManager.getLookAndFeel().provideErrorFeedback(fromFrequencySpinner);
			}

			JSpinner toFrequencySpinner = getToFrequencySpinner();
			try {
				toFrequencySpinner.commitEdit();
			} catch (ParseException pe) {
				UIManager.getLookAndFeel().provideErrorFeedback(toFrequencySpinner);
			}

			float fromFrequency = ((Number) fromFrequencySpinner.getValue()).floatValue();
			boolean unlimited = getUnlimitedCheckBox().isSelected();
			float toFrequency;
			if ( !unlimited ) {
				toFrequency = ((Number) toFrequencySpinner.getValue()).floatValue();
			} else {
				toFrequency = 0F;
			}
			double coefficient = ((Number) coefficientSpinner.getValue()).doubleValue();

			Range range = currentFilter.new Range(fromFrequency, toFrequency, coefficient);

			currentFilter.setRange( range, getMultiplyCheckBox().isSelected() );
			getTableModel().onUpdate();

			updateGraph();

		}

	}

	protected class RemoveRangeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveRangeAction() {
			super(messageSource.getMessage("editFFTSampleFilter.removeRange"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removefftrange.png") );
		}

		public void actionPerformed(ActionEvent ev) {

			if ( currentFilter == null ) {
				return;
			}

			int selectedRow = getTable().getSelectedRow();
			if ( selectedRow < 0 ) {
				return;
			}

			currentFilter.removeRange(selectedRow);

			FFTSampleFilterTableModel model = getTableModel();
			model.onUpdate();

			if ( model.getRowCount() > 0 ) {
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
			return getPopupMenu(-1,-1);
		}

		private JPopupMenu getDefaultPopupMenu() {

			if ( popupMenu == null ) {

				popupMenu = new JPopupMenu();

				popupMenu.add(removeRangeAction);

			}

			return popupMenu;

		}

	}

	@Override
	public JFreeChart getFrequencyResponseChart() {

		if (frequencyResponseChart == null) {
			frequencyResponseChart = super.getFrequencyResponseChart();
			frequencyResponseChart.setTitle(messageSource.getMessage("editFFTSampleFilter.graphTitle"));

		}
		return frequencyResponseChart;

	}

	@Override
	public FrequencyResponseChartPanel getFrequencyResponseChartPanel() {
		if ( frequencyResponseChartPanel == null ) {

			frequencyResponseChartPanel = new FFTFrequencyResponseChartPanel(getFrequencyResponseChart());
			frequencyResponseChartPanel.setBackground(Color.WHITE);
			frequencyResponseChartPanel.setPreferredSize(new Dimension(500, 150));

		}
		return frequencyResponseChartPanel;
	}

	protected class FFTFrequencyResponseChartPanel extends FrequencyResponseChartPanel {

		public FFTFrequencyResponseChartPanel(JFreeChart chart) {
			super(chart);
		}

		@Override
		public void mouseReleased(MouseEvent ev) {
			startFrequency = null;
			hideSelectionHighlight = false;
			clearDragHighlight();
			repaint();

			JSpinner spinner = getCoefficientSpinner();
			// XXX ugly hack - this class is utterly hopeless...
			JTextField editor = ((JTextField) ((JSpinner.NumberEditor) spinner.getEditor()).getComponent(0));
			editor.selectAll();
			editor.requestFocusInWindow();
		}

		@Override
		public void mouseClicked(MouseEvent ev) {

			double frequency = getFrequency( ev.getPoint() );
			if ( frequency >= getGraphFrequencyMax() ) {
				return;
			}

			getFromFrequencySpinner().setValue( frequency );
			getToFrequencySpinner().setValue( frequency+0.25 );

		}

		@Override
		public void mouseDragged(MouseEvent ev) {

			if ( startFrequency == null ) {
				return;
			}

			double startFrequency = this.startFrequency;
			double endFrequency = getFrequency( ev.getPoint() );

			if ( startFrequency == endFrequency ) {
				clearDragHighlight();
				return;
			}

			if ( startFrequency > endFrequency ) {
				double temp = startFrequency;
				startFrequency = endFrequency;
				endFrequency = temp;
			}

			getFromFrequencySpinner().setValue( startFrequency );
			if ( endFrequency >= getGraphFrequencyMax() ) {
				getToFrequencySpinner().setValue( getGraphFrequencyMax() );
				getUnlimitedCheckBox().setSelected(true);
				setDragHighlight(startFrequency, getGraphFrequencyMax());
			} else {
				getUnlimitedCheckBox().setSelected(false);
				getToFrequencySpinner().setValue( endFrequency );
				setDragHighlight(startFrequency, endFrequency);
			}



		}

	}

}

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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
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
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.ui.RectangleInsets;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.montage.FFTSampleFilterTableModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.dialog.AbstractPresetDialog;
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
public class EditFFTSampleFilterDialog extends AbstractPresetDialog {
	
	private static final long serialVersionUID = 1L;

	private FFTSampleFilter currentFilter;
	private float currentSamplingFrequency;
	
	private FFTSampleFilterTableModel tableModel;
	private FFTSampleFilterTable table;
	private JScrollPane tableScrollPane;
	
	private JPanel newRangePanel;
	
	private JTextField descriptionTextField;
	
	private JSpinner fromFrequencySpinner;
	private JSpinner toFrequencySpinner;
	private JSpinner coefficientSpinner;
	private JCheckBox unlimitedCheckBox;
	private JCheckBox multiplyCheckBox;
	
	private AddNewRangeAction addNewRangeAction;
	private RemoveRangeAction removeRangeAction;
	
	private JButton addNewRangeButton;
	private JButton removeRangeButton;
	
	private double graphFrequencyMax;
	private JSpinner graphScaleSpinner;
	private NumberAxis frequencyAxis;
	private XYPlot coefficientPlot;
	private JFreeChart coefficientChart;	
	private CoefficientChartPanel coefficientChartPanel;
	
	private FFTWindowTypePanel fftWindowTypePanel;

	private NumberAxis coefficientAxis;
		
	public EditFFTSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

	public EditFFTSampleFilterDialog(MessageSourceAccessor messageSource, PresetManager presetManager) {
		super(messageSource, presetManager);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("editFFTSampleFilter.title"));
		setIconImage( IconUtils.loadClassPathImage("org/signalml/app/icon/editfilter.png"));		
		setResizable(false);
		
		addNewRangeAction = new AddNewRangeAction();
		removeRangeAction = new RemoveRangeAction();
		removeRangeAction.setEnabled(false);
		
		super.initialize();
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentShown(ComponentEvent e) {
	    		getCoefficientChartPanel().setSelectionHighlightStart(((Number) getFromFrequencySpinner().getValue()).doubleValue());
	    		getCoefficientChartPanel().setSelectionHighlightEnd(((Number) getToFrequencySpinner().getValue()).doubleValue());
			}
			
		});
	}
	
	@Override
	public JComponent createInterface() {
		
		JPanel interfacePanel = new JPanel( new BorderLayout() );
		
		JPanel descriptionPanel = new JPanel( new BorderLayout() );
		CompoundBorder border = new CompoundBorder(
				new TitledBorder( messageSource.getMessage("editFFTSampleFilter.descriptionTitle") ),
				new EmptyBorder(3,3,3,3)
		);
		descriptionPanel.setBorder( border );
		
		descriptionPanel.add( getDescriptionTextField() );

		JPanel graphSpinnerPanel = new JPanel();
		graphSpinnerPanel.setLayout( new BoxLayout( graphSpinnerPanel, BoxLayout.X_AXIS ) );
		
		graphSpinnerPanel.add( new JLabel( messageSource.getMessage("editFFTSampleFilter.graphSpinnerLabel") ) );
		graphSpinnerPanel.add( Box.createHorizontalStrut(5) );
		graphSpinnerPanel.add( Box.createHorizontalGlue() );
		graphSpinnerPanel.add( getGraphScaleSpinner() );
		
		JPanel graphPanel = new JPanel( new BorderLayout(6,6) );

		border = new CompoundBorder(
				new TitledBorder( messageSource.getMessage("editFFTSampleFilter.graphPanelTitle") ),
				new EmptyBorder(3,3,3,3)
		);
		graphPanel.setBorder( border );
		
		graphPanel.add( getCoefficientChartPanel(), BorderLayout.CENTER );
		graphPanel.add( graphSpinnerPanel, BorderLayout.SOUTH );
		
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
		if( tableModel == null ) {
			tableModel = new FFTSampleFilterTableModel(messageSource);
		}
		return tableModel;
	}

	public FFTSampleFilterTable getTable() {
		if( table == null ) {
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
					if( SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0 ) {
						int selRow = table.rowAtPoint(e.getPoint());
						if( selRow >= 0 ) {
							
							Range range = currentFilter.getRangeAt(selRow);
							
							float lowFrequency = range.getLowFrequency();
							float highFrequency = range.getHighFrequency();
							
							getFromFrequencySpinner().setValue( (double) lowFrequency );
							if( highFrequency <= lowFrequency ) {
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
		if( tableScrollPane == null ) {
			tableScrollPane = new JScrollPane(getTable());
			tableScrollPane.setPreferredSize(new Dimension(250,300));
		}
		return tableScrollPane;
	}
	
	public JTextField getDescriptionTextField() {
		if( descriptionTextField == null ) {
			descriptionTextField = new JTextField();
			descriptionTextField.setPreferredSize( new Dimension(200,25) );
		}
		return descriptionTextField;
	}

	public JPanel getNewRangePanel() {
		if( newRangePanel == null ) {
	
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

	public JSpinner getFromFrequencySpinner() {
		if( fromFrequencySpinner == null ) {
			fromFrequencySpinner = new JSpinner( new SpinnerNumberModel( 0.0, 0.0, 4096.0, 0.25 ) );
			fromFrequencySpinner.setPreferredSize( new Dimension(80,25) );
			
			fromFrequencySpinner.addChangeListener( new SpinnerRoundingChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);
										
					double value = ((Number) fromFrequencySpinner.getValue()).doubleValue();
					
					getCoefficientChartPanel().setSelectionHighlightStart(value);
					
					double otherValue = ((Number) getToFrequencySpinner().getValue()).doubleValue();
					
					if( value >= otherValue ) {
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
		if( toFrequencySpinner == null ) {
			toFrequencySpinner = new JSpinner( new SpinnerNumberModel( 0.25, 0.25, 4096.0, 0.25 ) );
			toFrequencySpinner.setPreferredSize( new Dimension(80,25) );
			
			toFrequencySpinner.addChangeListener( new SpinnerRoundingChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);
										
					double value = ((Number) toFrequencySpinner.getValue()).doubleValue();
					
					if( !getUnlimitedCheckBox().isSelected() ) {
						getCoefficientChartPanel().setSelectionHighlightEnd(value);
					}
					
					double otherValue = ((Number) getFromFrequencySpinner().getValue()).doubleValue();
					
					if( value <= otherValue ) {
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
		if( unlimitedCheckBox == null ) {
			unlimitedCheckBox = new JCheckBox();
			
			unlimitedCheckBox.addItemListener( new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					JSpinner spinner = getToFrequencySpinner();
					boolean unlimited = getUnlimitedCheckBox().isSelected();
					if( unlimited ) {
						getCoefficientChartPanel().setSelectionHighlightEnd(((Number) getGraphScaleSpinner().getValue()).doubleValue());
					} else {
						getCoefficientChartPanel().setSelectionHighlightEnd(((Number) spinner.getValue()).doubleValue());
					}
					spinner.setEnabled( !unlimited );
					
				}
				
			});
		}
		return unlimitedCheckBox;
	}
	
	public NumberAxis getFrequencyAxis() {
		if( frequencyAxis == null ) {
			frequencyAxis = new NumberAxis();
			frequencyAxis.setAutoRange(false);
    		frequencyAxis.setLabel( messageSource.getMessage("editFFTSampleFilter.graphFrequencyLabel") );
		}
		return frequencyAxis;
	}
	
	public NumberAxis getCoefficientAxis() {
		if( coefficientAxis == null ) {
			coefficientAxis = new NumberAxis();
			coefficientAxis.setAutoRange(false);
			coefficientAxis.setTickUnit( new NumberTickUnit(1) );			
		}
		return coefficientAxis;
	}
	
	public XYPlot getCoefficientPlot() {
		if( coefficientPlot == null ) {
						
    		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);

    		coefficientPlot = new XYPlot( null, getFrequencyAxis(), getCoefficientAxis(), renderer );			
    		
		}
		return coefficientPlot;
	}
	
	public JFreeChart getCoefficientChart() {
		if( coefficientChart == null ) {
			coefficientChart = new JFreeChart(messageSource.getMessage("editFFTSampleFilter.graphTitle"), new Font( Font.DIALOG, Font.PLAIN, 12 ), getCoefficientPlot(), false);
			coefficientChart.setBorderVisible(true);
			coefficientChart.setBackgroundPaint(Color.WHITE);
			coefficientChart.setPadding(new RectangleInsets(5,5,5,5) );
		}
		return coefficientChart;
	}
	
	public CoefficientChartPanel getCoefficientChartPanel() {
		if( coefficientChartPanel == null ) {
			    	
    		coefficientChartPanel = new CoefficientChartPanel(getCoefficientChart());
    		coefficientChartPanel.setBackground(Color.WHITE);
    		coefficientChartPanel.setPreferredSize(new Dimension(500, 150));
    		    		    		
    	}    	
		return coefficientChartPanel;
	}
	
	public JSpinner getGraphScaleSpinner() {
		if( graphScaleSpinner == null ) {
			graphScaleSpinner = new JSpinner( new SpinnerNumberModel( 0.25, 0.25, 4096.0, 0.25 ) );
			graphScaleSpinner.setPreferredSize( new Dimension(80,25) );
			
			graphScaleSpinner.addChangeListener( new SpinnerRoundingChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					super.stateChanged(e);
					
					graphFrequencyMax = ((Number) graphScaleSpinner.getValue()).doubleValue();
					updateGraph();
					
					if( getUnlimitedCheckBox().isSelected() ) {
						getCoefficientChartPanel().setSelectionHighlightEnd(graphFrequencyMax);
					}						
					
				}
								
			});
			
			graphScaleSpinner.setEditor( new JSpinner.NumberEditor( graphScaleSpinner, "0.00" ) );
			graphScaleSpinner.setFont( graphScaleSpinner.getFont().deriveFont( Font.PLAIN ) ); 			
		}		
		return graphScaleSpinner;
	}
		
	public JSpinner getCoefficientSpinner() {
		if( coefficientSpinner == null ) {
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
		if( multiplyCheckBox == null ) {
			multiplyCheckBox = new JCheckBox();
		}
		return multiplyCheckBox;
	}

	public JButton getAddNewRangeButton() {
		if( addNewRangeButton == null ) {
			addNewRangeButton = new JButton( addNewRangeAction );
		}
		return addNewRangeButton;
	}
	
	public JButton getRemoveRangeButton() {
		if( removeRangeButton == null ) {
			removeRangeButton = new JButton( removeRangeAction );
		}
		return removeRangeButton;
	}
			
	public FFTWindowTypePanel getFFTWindowTypePanel() {
		if( fftWindowTypePanel == null ) {
			fftWindowTypePanel = new FFTWindowTypePanel(messageSource, true);
		}
		return fftWindowTypePanel;
	}

	public float getCurrentSamplingFrequency() {
		return currentSamplingFrequency;
	}

	public void setCurrentSamplingFrequency(float currentSamplingFrequency) {
		this.currentSamplingFrequency = currentSamplingFrequency;
	}
	
	public double getGraphFrequencyMax() {
		return graphFrequencyMax;
	}

	public void setGraphFrequencyMax(double graphFrequencyMax) {
		if( this.graphFrequencyMax != graphFrequencyMax ) {
		
			this.graphFrequencyMax = graphFrequencyMax;
			
			getGraphScaleSpinner().setValue( graphFrequencyMax );
			
		}
	}

	private void updateGraph() {

		if( currentFilter == null ) {
			return;
		}
		
		int frequencyCnt = (int) Math.ceil( graphFrequencyMax / 0.25 ) + 1;
		double[] frequencies = new double[frequencyCnt];
		double[] coefficients = new double[frequencyCnt];
		int i;
		double frequency = 0;
		
		for( i=0; i<frequencyCnt; i++ ) {
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
		
		while( it.hasNext() ) {
			
			range = it.next();
			
			lowFrequency = range.getLowFrequency();
			if( lowFrequency > graphFrequencyMax ) {
				break;
			}
			
			highFrequency = range.getHighFrequency();
			coefficient = range.getCoefficient();
			
			if( highFrequency <= lowFrequency ) {
				limit = graphFrequencyMax;
			} else {
				limit = Math.min( highFrequency, graphFrequencyMax );
			}
			
			int index;
			for( frequency=lowFrequency; frequency<=limit; frequency += 0.25 ) {
				
				index = (int) ( frequency / 0.25 );
				coefficients[index] = coefficient;
				
			}
			
			if( coefficient > maxCoefficient ) {
				maxCoefficient = coefficient;
			}
								
		}
		
		maxCoefficient *= 1.1;
		if( maxCoefficient < 1 ) {
			maxCoefficient  = 1;
		}

		double unit = Math.max( 4, Math.round( graphFrequencyMax / (16*4) ) * 4 );
		NumberAxis axis = getFrequencyAxis();
		axis.setRange( 0, graphFrequencyMax );
		axis.setTickUnit( new NumberTickUnit(unit) );
		
		getCoefficientAxis().setRange( 0, maxCoefficient );
		
    	DefaultXYDataset dataset = new DefaultXYDataset();
    	dataset.addSeries("data", new double[][] { frequencies, coefficients } );
    	getCoefficientPlot().setDataset( dataset );
				
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
		if( description == null || description.isEmpty() ) {			
			errors.rejectValue( "description", "error.editFFTSampleFilter.descriptionEmpty" );			
		}
		else if( !Util.validateString(description) ) {
			errors.rejectValue( "description", "error.editFFTSampleFilter.descriptionBadChars" );
		}
		
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

			if( currentFilter == null ) {
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
			if( !unlimited ) {
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

			if( currentFilter == null ) {
				return;
			}
			
			int selectedRow = getTable().getSelectedRow();
			if( selectedRow < 0 ) {
				return;
			}

			currentFilter.removeRange(selectedRow);
			
			FFTSampleFilterTableModel model = getTableModel();
			model.onUpdate();
			
			if( model.getRowCount() > 0 ) {
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
			
			if( popupMenu == null ) {
			
				popupMenu = new JPopupMenu();
				
				popupMenu.add(removeRangeAction);
				
			}
		
			return popupMenu;
			
		}
		
	}
	
	protected class SpinnerRoundingChangeListener implements ChangeListener {
		
		protected boolean lock = false;
		
		@Override
		public void stateChanged(ChangeEvent e) {
			
			if( lock ) {
				return;
			}
			
			try {
				lock = true;
				
				JSpinner spinner = (JSpinner) e.getSource();
				double doubleValue = ((Number) spinner.getValue()).doubleValue();
				double newDoubleValue = ((double) Math.round( 4 * doubleValue )) / 4;
				if( newDoubleValue != doubleValue ) {
					spinner.setValue( newDoubleValue );
				}
				
			} finally {
				lock = false;
			}
			
		}		
		
	}
	
	protected class CoefficientChartPanel extends ChartPanel {

		private static final long serialVersionUID = 1L;

		private Double startFrequency = null;
		
		private int dragHighlightStart = -1;
		private int dragHighlightEnd = -1;

		private int selectionHighlightStart = -1;
		private int selectionHighlightEnd = -1;
		private boolean hideSelectionHighlight = false;
		
		public CoefficientChartPanel(JFreeChart chart) {
			super(chart);
			
    		setDomainZoomable(false);
    		setRangeZoomable(false);
    		setMouseZoomable(false);
    		setPopupMenu(null);
			
		}
		
		private double getFrequency( Point p ) {
			
			Rectangle2D area = getScreenDataArea();
			
			int xMin = (int) Math.floor( area.getX() );
			int xMax = (int) Math.ceil( area.getX() + area.getWidth() );
			
			if( p.x < xMin ) {
				return 0;
			}
			if( p.x > xMax ) {
				return graphFrequencyMax + 1;
			}
			
			double freq = graphFrequencyMax * (((double) (p.x-xMin)) / ((double) (xMax-xMin)) );
						
			return ((double) Math.round(freq * 4)) / 4.0;
			
		}

		private void setDragHighlight( double highlightStart, double highlightEnd ) {
			
			Rectangle2D area = getScreenDataArea();
			
			int xMin = (int) Math.floor( area.getX() );
			int xMax = (int) Math.ceil( area.getX() + area.getWidth() );

			double perHz = ((double) (xMax-xMin)) / graphFrequencyMax;
			
			setDragHighlight( (int) Math.round( xMin + highlightStart*perHz ), (int) Math.round( xMin + highlightEnd*perHz ) );
			
		}
		
		private void setDragHighlight( int highlightStart, int highlightEnd ) {
			if( this.dragHighlightStart != highlightStart || this.dragHighlightEnd != highlightEnd ) {
				this.dragHighlightStart = highlightStart;
				this.dragHighlightEnd = highlightEnd;
				repaint();
			}
		}
		
		private void clearDragHighlight() {
			if( dragHighlightStart >= 0 || dragHighlightEnd >= 0 ) {
				dragHighlightStart = -1;
				dragHighlightEnd = -1;
				repaint();				
			}
		}

		public void setSelectionHighlightStart( double highlightStart ) {
			
			Rectangle2D area = getScreenDataArea();
			
			int xMin = (int) Math.floor( area.getX() );
			int xMax = (int) Math.ceil( area.getX() + area.getWidth() );

			double perHz = ((double) (xMax-xMin)) / graphFrequencyMax;
			
			setSelectionHighlight( (int) Math.round( xMin + highlightStart*perHz ), selectionHighlightEnd );
			
		}

		public void setSelectionHighlightEnd( double highlightEnd ) {
			
			Rectangle2D area = getScreenDataArea();
			
			int xMin = (int) Math.floor( area.getX() );
			int xMax = (int) Math.ceil( area.getX() + area.getWidth() );

			double perHz = ((double) (xMax-xMin)) / graphFrequencyMax;
			
			setSelectionHighlight( selectionHighlightStart, (int) Math.round( xMin + highlightEnd*perHz ) );
			
		}
		
		private void setSelectionHighlight( int highlightStart, int highlightEnd ) {
			if( this.selectionHighlightStart != highlightStart || this.selectionHighlightEnd != highlightEnd ) {
				this.selectionHighlightStart = highlightStart;
				this.selectionHighlightEnd = highlightEnd;
				repaint();
			}
		}
		
		public void clearSelectionHighlight() {
			if( selectionHighlightStart >= 0 || selectionHighlightEnd >= 0 ) {
				selectionHighlightStart = -1;
				selectionHighlightEnd = -1;
				repaint();				
			}						
		}
		
		@Override
		public void mousePressed(MouseEvent ev) {
			hideSelectionHighlight = true;			
			startFrequency = getFrequency( ev.getPoint() );
			if( startFrequency >= graphFrequencyMax ) {
				startFrequency = null;
			}
			repaint();
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
			if( frequency >= graphFrequencyMax ) {
				return;
			}
			
			getFromFrequencySpinner().setValue( frequency );
			getToFrequencySpinner().setValue( frequency+0.25 );
			
		}
		
		@Override
		public void mouseDragged(MouseEvent ev) {

			if( startFrequency == null ) {
				return;
			}
			
			double startFrequency = this.startFrequency;
			double endFrequency = getFrequency( ev.getPoint() );
			
			if( startFrequency == endFrequency ) {
				clearDragHighlight();
				return;
			}

			if( startFrequency > endFrequency ) {
				double temp = startFrequency;
				startFrequency = endFrequency;
				endFrequency = temp;
			}
			
			getFromFrequencySpinner().setValue( startFrequency );
			if( endFrequency >= graphFrequencyMax ) {
				getToFrequencySpinner().setValue( graphFrequencyMax );
				getUnlimitedCheckBox().setSelected(true);
				setDragHighlight(startFrequency, graphFrequencyMax);
			} else {
				getUnlimitedCheckBox().setSelected(false);
				getToFrequencySpinner().setValue( endFrequency );
				setDragHighlight(startFrequency, endFrequency);
			}
			
						
		}
		
		@Override
		public void paintComponent(Graphics gOrig) {
			super.paintComponent(gOrig);
			
			Graphics2D g = (Graphics2D) gOrig;
			Rectangle2D area = getScreenDataArea();
			
			if( !hideSelectionHighlight ) {
			
				if( selectionHighlightStart > 0 && selectionHighlightEnd > 0 ) {
					
					g.setColor( new Color( 0.55F, 1.0F, 0.55F, 0.5F ) );			
					g.fillRect( selectionHighlightStart, (int) area.getY(), selectionHighlightEnd-selectionHighlightStart, (int) area.getHeight() );
					
				}
				
			}
			
			if( dragHighlightStart > 0 && dragHighlightEnd > 0 ) {
									
				g.setColor( new Color( 0.5F, 0.5F, 0.5F, 0.5F ) );			
				g.fillRect( dragHighlightStart, (int) area.getY(), dragHighlightEnd-dragHighlightStart, (int) area.getHeight() );
				
			}
			
		}
					
	}
	
}

/* SignalViewingConfigPanel.java created 2007-11-17
 * 
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.view.signal.SignalColor;
import org.signalml.app.view.tag.TagPaintMode;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** SignalViewingConfigPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalViewingConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JCheckBox rightClickPagesForwardCheckBox;
	private JCheckBox autoLoadDefaultMontageCheckBox;
	private JCheckBox precalculateSignalChecksumsCheckBox;
	
	private JCheckBox antialiasedCheckBox;
	private JCheckBox clampedCheckBox;
	private JCheckBox offscreenChannelsDrawnCheckBox;	
	private JCheckBox tagToolTipsVisibleCheckBox;
	
	private JCheckBox pageLinesVisibleCheckBox;
	private JCheckBox blockLinesVisibleCheckBox;
	private JCheckBox channelLinesVisibleCheckBox;

	private JComboBox tagPaintModeComboBox;
	private JComboBox signalColorComboBox;
	private JCheckBox signalXORCheckBox;
	
	private JSpinner minChannelHeightSpinner;
	private JSpinner maxChannelHeightSpinner;
	private JSpinner minValueScaleSpinner;
	private JSpinner maxValueScaleSpinner;
	private JSpinner minTimeScaleSpinner;
	private JSpinner maxTimeScaleSpinner;

	private JPanel generalPanel;
	private JPanel plotOptionsPanel;
	private JPanel scalesPanel;
			
	public SignalViewingConfigPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {
		
		setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BorderLayout());		
		
		add( getGeneralPanel(), BorderLayout.NORTH );
		add( getPlotOptionsPanel(), BorderLayout.CENTER );
		add( getScalesPanel(), BorderLayout.SOUTH );
	}

	public JComboBox getTagPaintModeComboBox() {
		if( tagPaintModeComboBox == null ) {
			tagPaintModeComboBox = new ResolvableComboBox( messageSource );
			tagPaintModeComboBox.setModel( new DefaultComboBoxModel( TagPaintMode.values() ) );			
		}
		return tagPaintModeComboBox;
	}

	public JComboBox getSignalColorComboBox() {
		if( signalColorComboBox == null ) {
			signalColorComboBox = new ResolvableComboBox( messageSource );
			signalColorComboBox.setModel( new DefaultComboBoxModel( SignalColor.values() ) );			
		}
		return signalColorComboBox;
	}

	public JCheckBox getSignalXORCheckBox() {
		if( signalXORCheckBox == null ) {
			signalXORCheckBox = new JCheckBox(messageSource.getMessage("preferences.signalViewing.signalXOR"));
			signalXORCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		}
		return signalXORCheckBox;
	}
	
	public JCheckBox getRightClickPagesForwardCheckBox() {
		if( rightClickPagesForwardCheckBox == null ) {
			rightClickPagesForwardCheckBox = new JCheckBox(messageSource.getMessage("preferences.signalViewing.rightClickPagesForward"));
		}
		return rightClickPagesForwardCheckBox;
	}
	
	public JCheckBox getAutoLoadDefaultMontageCheckBox() {
		if( autoLoadDefaultMontageCheckBox == null ) {
			autoLoadDefaultMontageCheckBox = new JCheckBox(messageSource.getMessage("preferences.signalViewing.autoLoadDefaultMontage"));
		}
		return autoLoadDefaultMontageCheckBox;
	}

	public JCheckBox getPrecalculateSignalChecksumsCheckBox() {
		if( precalculateSignalChecksumsCheckBox == null ) {
			precalculateSignalChecksumsCheckBox = new JCheckBox(messageSource.getMessage("preferences.signalViewing.precalculateSignalChecksums"));
		}
		return precalculateSignalChecksumsCheckBox;
	}
		
	public JCheckBox getAntialiasedCheckBox() {
		if( antialiasedCheckBox == null ) {
			antialiasedCheckBox = new JCheckBox( messageSource.getMessage("preferences.signalViewing.antialias") );
		}
		return antialiasedCheckBox;
	}

	public JCheckBox getClampedCheckBox() {
		if( clampedCheckBox == null ) {
			clampedCheckBox = new JCheckBox( messageSource.getMessage("preferences.signalViewing.clamp") );
		}		
		return clampedCheckBox;
	}

	public JCheckBox getOffscreenChannelsDrawnCheckBox() {
		if( offscreenChannelsDrawnCheckBox == null ) {
			offscreenChannelsDrawnCheckBox = new JCheckBox( messageSource.getMessage("preferences.signalViewing.offscreenChannelsDrawn") );
		}
		return offscreenChannelsDrawnCheckBox;
	}

	public JCheckBox getTagToolTipsVisibleCheckBox() {
		if( tagToolTipsVisibleCheckBox == null ) {
			tagToolTipsVisibleCheckBox = new JCheckBox( messageSource.getMessage("preferences.signalViewing.tagToolTipsVisible") );
		}
		return tagToolTipsVisibleCheckBox;
	}
	
	public JCheckBox getPageLinesVisibleCheckBox() {
		if( pageLinesVisibleCheckBox == null ) {
			pageLinesVisibleCheckBox = new JCheckBox( messageSource.getMessage("preferences.signalViewing.pageLinesVisible") );
		}
		return pageLinesVisibleCheckBox;
	}

	public JCheckBox getBlockLinesVisibleCheckBox() {
		if( blockLinesVisibleCheckBox == null ) {
			blockLinesVisibleCheckBox = new JCheckBox( messageSource.getMessage("preferences.signalViewing.blockLinesVisible") );
		}
		return blockLinesVisibleCheckBox;
	}

	public JCheckBox getChannelLinesVisibleCheckBox() {
		if( channelLinesVisibleCheckBox == null ) {
			channelLinesVisibleCheckBox = new JCheckBox( messageSource.getMessage("preferences.signalViewing.channelLinesVisible") );
		}
		return channelLinesVisibleCheckBox;
	}
		
	public JSpinner getMinChannelHeightSpinner() {
		if( minChannelHeightSpinner == null ) {
			minChannelHeightSpinner = new JSpinner( new SpinnerNumberModel( 20, 20, 1000, 10 ) );
			minChannelHeightSpinner.setPreferredSize( new Dimension(80,25) );
		}
		return minChannelHeightSpinner;
	}

	public JSpinner getMaxChannelHeightSpinner() {
		if( maxChannelHeightSpinner == null ) {
			maxChannelHeightSpinner = new JSpinner( new SpinnerNumberModel( 20, 20, 1000, 10 ) );
			maxChannelHeightSpinner.setPreferredSize( new Dimension(80,25) );
		}
		return maxChannelHeightSpinner;
	}

	public JSpinner getMinValueScaleSpinner() {
		if( minValueScaleSpinner == null ) {
			minValueScaleSpinner = new JSpinner( new SpinnerNumberModel( 1, 1, 2000, 10 ) );
			minValueScaleSpinner.setPreferredSize( new Dimension(80,25) );
		}
		return minValueScaleSpinner;
	}

	public JSpinner getMaxValueScaleSpinner() {
		if( maxValueScaleSpinner == null ) {
			maxValueScaleSpinner = new JSpinner( new SpinnerNumberModel( 1, 1, 2000, 10 ) );
			maxValueScaleSpinner.setPreferredSize( new Dimension(80,25) );
		}
		return maxValueScaleSpinner;
	}

	public JSpinner getMinTimeScaleSpinner() {
		if( minTimeScaleSpinner == null ) {
			minTimeScaleSpinner = new JSpinner( new SpinnerNumberModel( 0.01, 0.01, 1, 0.01 ) );
			minTimeScaleSpinner.setPreferredSize( new Dimension(80,25) );
		}
		return minTimeScaleSpinner;
	}

	public JSpinner getMaxTimeScaleSpinner() {
		if( maxTimeScaleSpinner == null ) {
			maxTimeScaleSpinner = new JSpinner( new SpinnerNumberModel( 0.01, 0.01, 1, 0.01 ) );
			maxTimeScaleSpinner.setPreferredSize( new Dimension(80,25) );
		}
		return maxTimeScaleSpinner;
	}

	public JPanel getGeneralPanel() {
		if( generalPanel == null ) {
			generalPanel = new JPanel();
			generalPanel.setLayout( new BoxLayout(generalPanel, BoxLayout.Y_AXIS) );
			generalPanel.setBorder( new CompoundBorder( 
					new TitledBorder(messageSource.getMessage("preferences.signalViewing.general")),
					new EmptyBorder(3,3,3,3)
			));
			
			generalPanel.add( getAutoLoadDefaultMontageCheckBox() );
			generalPanel.add( getRightClickPagesForwardCheckBox() );
			generalPanel.add( getPrecalculateSignalChecksumsCheckBox() );			
		}
		return generalPanel;
	}
	
	public JPanel getPlotOptionsPanel() {
		if( plotOptionsPanel == null ) {
			plotOptionsPanel = new JPanel(new BorderLayout());
			plotOptionsPanel.setBorder( new CompoundBorder( 
					new TitledBorder(messageSource.getMessage("preferences.signalViewing.plotOptions")),
					new EmptyBorder(3,3,3,3)
			));
			
			JPanel plotOptionsLeftPanel = new JPanel(new GridLayout(7, 1, 0, 0));
			
			plotOptionsLeftPanel.add( getPageLinesVisibleCheckBox() );
			plotOptionsLeftPanel.add( getBlockLinesVisibleCheckBox() );
			plotOptionsLeftPanel.add( getChannelLinesVisibleCheckBox() );
			plotOptionsLeftPanel.add( getTagToolTipsVisibleCheckBox() );
			plotOptionsLeftPanel.add( getAntialiasedCheckBox() );
			plotOptionsLeftPanel.add( getClampedCheckBox() );
			plotOptionsLeftPanel.add( getOffscreenChannelsDrawnCheckBox() );
			
			JPanel plotOptionsRightPanel = new JPanel();
			plotOptionsRightPanel.setBorder( new EmptyBorder(3,3,3,3) );

			GroupLayout layout = new GroupLayout(plotOptionsRightPanel);
			plotOptionsRightPanel.setLayout(layout);
			
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel tagPaintModeLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.tagPaintMode"));
			JLabel signalColorLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.signalColor"));
			JLabel signalXORLabel = new JLabel("");
			
			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
			
			hGroup.addGroup(
					layout.createParallelGroup(Alignment.LEADING)
					.addComponent(tagPaintModeLabel)
					.addComponent(signalColorLabel)
					.addComponent(signalXORLabel)
				);
			
			hGroup.addGroup(
					layout.createParallelGroup(Alignment.TRAILING)
					.addComponent(getTagPaintModeComboBox())
					.addComponent(getSignalColorComboBox())
					.addComponent(getSignalXORCheckBox())
				);
			
			layout.setHorizontalGroup(hGroup);
			
			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
			
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(tagPaintModeLabel)
					.addComponent(getTagPaintModeComboBox())
				);
			
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(signalColorLabel)
					.addComponent(getSignalColorComboBox())
				);

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(signalXORLabel)
					.addComponent(getSignalXORCheckBox())
				);
			
			layout.setVerticalGroup(vGroup);		
			
			plotOptionsPanel.add(plotOptionsLeftPanel, BorderLayout.WEST);
			plotOptionsPanel.add(plotOptionsRightPanel, BorderLayout.CENTER);			
		}
		return plotOptionsPanel;
	}
	
	public JPanel getScalesPanel() {
		if( scalesPanel == null ) {
			
			scalesPanel = new JPanel();
			scalesPanel.setBorder( new CompoundBorder( 
					new TitledBorder( messageSource.getMessage("preferences.signalViewing.scales") ),
					new EmptyBorder(3,6,3,6)
			));

			GroupLayout layout = new GroupLayout(scalesPanel);
			scalesPanel.setLayout(layout);
			
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel channelHeightLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.channelHeight"));
			JLabel minChannelHeightLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.min"));
			JLabel maxChannelHeightLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.max"));
			JLabel valueScaleLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.valueScale"));
			JLabel minValueScaleLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.min"));
			JLabel maxValueScaleLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.max"));
			JLabel timeScaleLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.timeScale"));
			JLabel minTimeScaleLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.min"));
			JLabel maxTimeScaleLabel = new JLabel(messageSource.getMessage("preferences.signalViewing.max"));
			
			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
			
			hGroup.addGroup(
					layout.createParallelGroup(Alignment.LEADING)
					.addComponent(channelHeightLabel)
					.addComponent(valueScaleLabel)
					.addComponent(timeScaleLabel)
				);

			hGroup.addGroup(
					layout.createParallelGroup(Alignment.LEADING)
					.addComponent(minChannelHeightLabel)
					.addComponent(minValueScaleLabel)
					.addComponent(minTimeScaleLabel)
				);
			
			hGroup.addGroup(
					layout.createParallelGroup(Alignment.TRAILING)
					.addComponent(getMinChannelHeightSpinner())
					.addComponent(getMinValueScaleSpinner())
					.addComponent(getMinTimeScaleSpinner())
				);

			hGroup.addGroup(
					layout.createParallelGroup(Alignment.LEADING)
					.addComponent(maxChannelHeightLabel)
					.addComponent(maxValueScaleLabel)
					.addComponent(maxTimeScaleLabel)
				);
			
			hGroup.addGroup(
					layout.createParallelGroup(Alignment.TRAILING)
					.addComponent(getMaxChannelHeightSpinner())
					.addComponent(getMaxValueScaleSpinner())
					.addComponent(getMaxTimeScaleSpinner())
				);
			
			layout.setHorizontalGroup(hGroup);
			
			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
			
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(channelHeightLabel)
					.addComponent(minChannelHeightLabel)
					.addComponent(getMinChannelHeightSpinner())
					.addComponent(maxChannelHeightLabel)
					.addComponent(getMaxChannelHeightSpinner())
				);
			
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(valueScaleLabel)
					.addComponent(minValueScaleLabel)
					.addComponent(getMinValueScaleSpinner())
					.addComponent(maxValueScaleLabel)
					.addComponent(getMaxValueScaleSpinner())
				);

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(timeScaleLabel)
					.addComponent(minTimeScaleLabel)
					.addComponent(getMinTimeScaleSpinner())
					.addComponent(maxTimeScaleLabel)
					.addComponent(getMaxTimeScaleSpinner())
				);
			
			layout.setVerticalGroup(vGroup);		
			
		}
		return scalesPanel;
	}

	public void fillPanelFromModel( ApplicationConfiguration applicationConfig ) {
		
		getRightClickPagesForwardCheckBox().setSelected( applicationConfig.isRightClickPagesForward() );
		getAutoLoadDefaultMontageCheckBox().setSelected( applicationConfig.isAutoLoadDefaultMontage() );
		getPrecalculateSignalChecksumsCheckBox().setSelected( applicationConfig.isPrecalculateSignalChecksums() );
		
		getPageLinesVisibleCheckBox().setSelected( applicationConfig.isPageLinesVisible() );
		getBlockLinesVisibleCheckBox().setSelected( applicationConfig.isBlockLinesVisible() );
		getChannelLinesVisibleCheckBox().setSelected( applicationConfig.isChannelLinesVisible() );
		getTagToolTipsVisibleCheckBox().setSelected( applicationConfig.isTagToolTipsVisible() );
		getAntialiasedCheckBox().setSelected( applicationConfig.isAntialiased() );
		getClampedCheckBox().setSelected( applicationConfig.isClamped() );
		getOffscreenChannelsDrawnCheckBox().setSelected( applicationConfig.isOffscreenChannelsDrawn() );
		
		getTagPaintModeComboBox().setSelectedItem( applicationConfig.getTagPaintMode() );
		getSignalColorComboBox().setSelectedItem( applicationConfig.getSignalColor() );
		getSignalXORCheckBox().setSelected( applicationConfig.isSignalXOR() );
		
		getMinChannelHeightSpinner().setValue( applicationConfig.getMinChannelHeight() );
		getMaxChannelHeightSpinner().setValue( applicationConfig.getMaxChannelHeight() );
		
		getMinValueScaleSpinner().setValue( applicationConfig.getMinValueScale() );
		getMaxValueScaleSpinner().setValue( applicationConfig.getMaxValueScale() );
		
		getMinTimeScaleSpinner().setValue( applicationConfig.getMinTimeScale() );
		getMaxTimeScaleSpinner().setValue( applicationConfig.getMaxTimeScale() );
		
	}
	
	public void fillModelFromPanel( ApplicationConfiguration applicationConfig ) {

		applicationConfig.setRightClickPagesForward( getRightClickPagesForwardCheckBox().isSelected() );
		applicationConfig.setAutoLoadDefaultMontage( getAutoLoadDefaultMontageCheckBox().isSelected() );
		applicationConfig.setPrecalculateSignalChecksums( getPrecalculateSignalChecksumsCheckBox().isSelected() );
		
		applicationConfig.setPageLinesVisible( getPageLinesVisibleCheckBox().isSelected() );
		applicationConfig.setBlockLinesVisible( getBlockLinesVisibleCheckBox().isSelected() );
		applicationConfig.setChannelLinesVisible( getChannelLinesVisibleCheckBox().isSelected() );
		applicationConfig.setTagToolTipsVisible( getTagToolTipsVisibleCheckBox().isSelected() );
		applicationConfig.setAntialiased( getAntialiasedCheckBox().isSelected() );
		applicationConfig.setClamped( getClampedCheckBox().isSelected() );
		applicationConfig.setOffscreenChannelsDrawn( getOffscreenChannelsDrawnCheckBox().isSelected() );
		
		applicationConfig.setTagPaintMode( (TagPaintMode) getTagPaintModeComboBox().getSelectedItem() );
		applicationConfig.setSignalColor( (SignalColor) getSignalColorComboBox().getSelectedItem() );
		applicationConfig.setSignalXOR( getSignalXORCheckBox().isSelected() );
		
		int min = ((Number) getMinChannelHeightSpinner().getValue()).intValue();
		int max = ((Number) getMaxChannelHeightSpinner().getValue()).intValue();
		int temp;
		if( min > max ) {
			temp = min;
			min = max;
			max = temp;
		}
		applicationConfig.setMinChannelHeight( min );
		applicationConfig.setMaxChannelHeight( max );
		
		min = ((Number) getMinValueScaleSpinner().getValue()).intValue();
		max = ((Number) getMaxValueScaleSpinner().getValue()).intValue();
		if( min > max ) {
			temp = min;
			min = max;
			max = temp;
		}
		applicationConfig.setMinValueScale( min );
		applicationConfig.setMaxValueScale( max );
		
		double minD = ((Number) getMinTimeScaleSpinner().getValue()).doubleValue();
		double maxD = ((Number) getMaxTimeScaleSpinner().getValue()).doubleValue();
		if( minD > maxD ) {
			double tempD = minD;
			minD = maxD;
			maxD = tempD;
		}
		applicationConfig.setMinTimeScale( minD );
		applicationConfig.setMaxTimeScale( maxD );
		
	}
	
	public void validate(Errors errors) {
		// do nothing
	}
		
}

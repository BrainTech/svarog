/* StagerBasicParametersPanel.java created 2008-02-14
 * 
 */
package org.signalml.app.method.stager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.action.EnableAction;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.view.element.AutoSpinnerPanel;
import org.signalml.app.view.element.CompactButton;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.method.stager.SleepStagingRules;
import org.signalml.method.stager.StagerParameters;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerBasicParametersPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerBasicParametersPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private AbstractDialog owner;
	
	private ResolvableComboBox rulesComboBox;
	
	private AutoSpinnerPanel deltaMinAmplitudePanel;
	private AutoSpinnerPanel alphaMinAmplitudePanel;
	private AutoSpinnerPanel spindleMinAmplitudePanel;
	
	private JCheckBox primaryHypnogramCheckBox;
	
	EnableAction amplitudePanelsEnable = new EnableAction() {
		public void setEnabled(boolean enabled) {
			getDeltaMinAmplitudePanel().setEnabled(enabled);
			getAlphaMinAmplitudePanel().setEnabled(enabled);
			getSpindleMinAmplitudePanel().setEnabled(enabled);						
		};
	};
				
	public StagerBasicParametersPanel(MessageSourceAccessor messageSource, AbstractDialog owner) {
		super();
		this.messageSource = messageSource;
		this.owner = owner;
		initialize();
	}

	private void initialize() {
		
		setLayout( new BorderLayout() );
		
		CompoundBorder border = new CompoundBorder(
				new TitledBorder( messageSource.getMessage("stagerMethod.dialog.basicParametersTitle") ),
				new EmptyBorder(3,3,3,3)
		);
		setBorder(border);
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel rulesLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.rules"));
		JLabel deltaMinAmplitudeLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.deltaMinAmplitude"));
		JLabel alphaMinAmplitudeLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.alphaMinAmplitude"));
		JLabel spindleMinAmplitudeLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.spindleMinAmplitude"));
		JLabel primaryHypnogramLabel = new JLabel(messageSource.getMessage("stagerMethod.dialog.primaryHypnogram"));
		primaryHypnogramLabel.setMinimumSize(new Dimension(25,35));
		primaryHypnogramLabel.setVerticalAlignment(JLabel.CENTER);
		
		Component glue1 = Box.createHorizontalGlue();
		Component glue2 = Box.createHorizontalGlue();
		Component glue3 = Box.createHorizontalGlue();
		Component glue4 = Box.createHorizontalGlue();
		Component glue5 = Box.createHorizontalGlue();
		
		CompactButton rulesHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_RULES);
		CompactButton deltaMinAmplitudeHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_DELTA_MIN_AMPLITUDE);
		CompactButton alphaMinAmplitudeHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_ALPHA_MIN_AMPLITUDE);
		CompactButton spindleMinAmplitudeHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_SPINDLE_MIN_AMPLITUDE);
		CompactButton primaryHypnogramHelpButton = SwingUtils.createFieldHelpButton(messageSource, owner, StagerMethodDialog.HELP_PRIMARY_HYPNOGRAM);
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(rulesLabel)
				.addComponent(deltaMinAmplitudeLabel)
				.addComponent(alphaMinAmplitudeLabel)
				.addComponent(spindleMinAmplitudeLabel)
				.addComponent(primaryHypnogramLabel)
			);

		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(glue1)
				.addComponent(glue2)
				.addComponent(glue3)
				.addComponent(glue4)
				.addComponent(glue5)
			);
		
		hGroup.addGroup(
				layout.createParallelGroup(Alignment.TRAILING)
				.addComponent(getRulesComboBox())
				.addComponent(getDeltaMinAmplitudePanel())
				.addComponent(getAlphaMinAmplitudePanel())
				.addComponent(getSpindleMinAmplitudePanel())
				.addComponent(getPrimaryHypnogramCheckBox())
			);

		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(rulesHelpButton)
				.addComponent(deltaMinAmplitudeHelpButton)
				.addComponent(alphaMinAmplitudeHelpButton)
				.addComponent(spindleMinAmplitudeHelpButton)
				.addComponent(primaryHypnogramHelpButton)
			);
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(rulesLabel)
				.addComponent(glue1)
				.addComponent(getRulesComboBox())
				.addComponent(rulesHelpButton)
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(deltaMinAmplitudeLabel)
				.addComponent(glue2)
				.addComponent(getDeltaMinAmplitudePanel())
				.addComponent(deltaMinAmplitudeHelpButton)
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(alphaMinAmplitudeLabel)
				.addComponent(glue3)
				.addComponent(getAlphaMinAmplitudePanel())
				.addComponent(alphaMinAmplitudeHelpButton)
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(spindleMinAmplitudeLabel)
				.addComponent(glue4)
				.addComponent(getSpindleMinAmplitudePanel())
				.addComponent(spindleMinAmplitudeHelpButton)
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.CENTER)
				.addComponent(primaryHypnogramLabel)
				.addComponent(glue5)
				.addComponent(getPrimaryHypnogramCheckBox())
				.addComponent(primaryHypnogramHelpButton)
			);
		
		layout.setVerticalGroup(vGroup);
		
		
		
	}
	
	public ResolvableComboBox getRulesComboBox() {
		if( rulesComboBox == null ) {
			rulesComboBox = new ResolvableComboBox(messageSource);
			rulesComboBox.setModel( new DefaultComboBoxModel( SleepStagingRules.values() ) );
		}
		return rulesComboBox;
	}
	
	public AutoSpinnerPanel getDeltaMinAmplitudePanel() {
		if( deltaMinAmplitudePanel == null ) {
			deltaMinAmplitudePanel = new AutoSpinnerPanel(messageSource, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MAX_AMPLITUDE, StagerParameters.INCR_AMPLITUDE, false);					
		}
		return deltaMinAmplitudePanel;
	}

	public AutoSpinnerPanel getAlphaMinAmplitudePanel() {
		if( alphaMinAmplitudePanel == null ) {
			alphaMinAmplitudePanel = new AutoSpinnerPanel(messageSource, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MAX_AMPLITUDE, StagerParameters.INCR_AMPLITUDE, false);					
		}
		return alphaMinAmplitudePanel;
	}
	
	public AutoSpinnerPanel getSpindleMinAmplitudePanel() {
		if( spindleMinAmplitudePanel == null ) {
			spindleMinAmplitudePanel = new AutoSpinnerPanel(messageSource, StagerParameters.MIN_AMPLITUDE, StagerParameters.MIN_AMPLITUDE, StagerParameters.MAX_AMPLITUDE, StagerParameters.INCR_AMPLITUDE, false);					
		}
		return spindleMinAmplitudePanel;
	}
			
	public JCheckBox getPrimaryHypnogramCheckBox() {
		if( primaryHypnogramCheckBox == null ) {
			primaryHypnogramCheckBox = new JCheckBox();
			primaryHypnogramCheckBox.setPreferredSize(new Dimension(25,25));
		}
		return primaryHypnogramCheckBox;
	}
	
	public void fillPanelFromParameters(StagerParameters parameters) {
		
		getRulesComboBox().setSelectedItem( parameters.getRules() );
		
		getDeltaMinAmplitudePanel().setValueWithAuto( parameters.getDeltaAmplitude().getMinWithUnlimited() );
		getAlphaMinAmplitudePanel().setValueWithAuto( parameters.getAlphaAmplitude().getMinWithUnlimited() );
		getSpindleMinAmplitudePanel().setValueWithAuto( parameters.getSpindleAmplitude().getMinWithUnlimited() );
		
		getPrimaryHypnogramCheckBox().setSelected( parameters.isPrimaryHypnogram() );
		
	}
	
	public void fillParametersFromPanel(StagerParameters parameters) {
		
		parameters.setRules( (SleepStagingRules) getRulesComboBox().getSelectedItem() );
		
		parameters.getDeltaAmplitude().setMinWithUnlimited( getDeltaMinAmplitudePanel().getValueWithAuto() );
		parameters.getAlphaAmplitude().setMinWithUnlimited( getAlphaMinAmplitudePanel().getValueWithAuto() );
		parameters.getSpindleAmplitude().setMinWithUnlimited( getSpindleMinAmplitudePanel().getValueWithAuto() );
				
		parameters.setPrimaryHypnogram( getPrimaryHypnogramCheckBox().isSelected() );
		
	}
	
	public void validatePanel( Errors errors ) {
		
		// nothing to do
		
	}

	/**
	 * @return the amplitudePanelsEnable
	 */
	public EnableAction getAmplitudePanelsEnable() {
		return amplitudePanelsEnable;
	}
	
}

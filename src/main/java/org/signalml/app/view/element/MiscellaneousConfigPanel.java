/* MiscellaneousConfigPanel.java created 2007-12-14
 * 
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.SignalMLOperationMode;
import org.signalml.app.config.ApplicationConfiguration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MiscellaneousConfigPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MiscellaneousConfigPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JCheckBox saveConfigOnEveryChangeCheckBox;
	private JCheckBox restoreWorkspaceCheckBox;
	private JCheckBox disableSeriousWarningsCheckBox;
	private JCheckBox dontShowCompilationWarningCheckBox;
	
	private JCheckBox viewModeHidesMainToolBarCheckBox;
	private JCheckBox viewModeHidesLeftPanelCheckBox;
	private JCheckBox viewModeHidesBottomPanelCheckBox;
	private JCheckBox viewModeCompactsPageTagBarsCheckBox;
	private JCheckBox viewModeSnapsToPageCheckBox;
	
	private JSpinner toolTipInitialSpinner;
	private JSpinner toolTipDismissSpinner;
	
	private SignalMLOperationMode mode;
	
	public MiscellaneousConfigPanel(MessageSourceAccessor messageSource, SignalMLOperationMode mode) {
		super();
		this.messageSource = messageSource;
		this.mode = mode;
		initialize();
	}

	private void initialize() {
		
		setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BorderLayout());
		
		JPanel generalPanel = new JPanel();
		generalPanel.setLayout( new BoxLayout(generalPanel, BoxLayout.Y_AXIS) );
		generalPanel.setBorder( new CompoundBorder( 
				new TitledBorder(messageSource.getMessage("preferences.miscellaneous.general")),
				new EmptyBorder(3,3,3,3)
		));
		
		if( mode == SignalMLOperationMode.APPLICATION ) {
			generalPanel.add( getSaveConfigOnEveryChangeCheckBox() );
			generalPanel.add( getRestoreWorkspaceCheckBox() );
		}
		generalPanel.add( getDisableSeriousWarningsCheckBox() );
		if( mode == SignalMLOperationMode.APPLICATION ) {
			generalPanel.add( getDontShowCompilationWarningCheckBox() );
		}
		
		JPanel toolTipPanel = new JPanel();
		toolTipPanel.setLayout( new BoxLayout(toolTipPanel, BoxLayout.X_AXIS) );
		toolTipPanel.setBorder( new CompoundBorder( 
				new TitledBorder(messageSource.getMessage("preferences.miscellaneous.toolTip")),
				new EmptyBorder(3,3,3,3)
		));
		
		toolTipPanel.add( new JLabel(messageSource.getMessage("preferences.miscellaneous.toolTipInitial")) );
		toolTipPanel.add( Box.createHorizontalStrut(5) );
		toolTipPanel.add( Box.createHorizontalGlue() );
		toolTipPanel.add( getToolTipInitialSpinner() );
		toolTipPanel.add( Box.createHorizontalStrut(5) );
		toolTipPanel.add( Box.createHorizontalGlue() );
		toolTipPanel.add( new JLabel(messageSource.getMessage("preferences.miscellaneous.toolTipDismiss")) );
		toolTipPanel.add( Box.createHorizontalStrut(5) );
		toolTipPanel.add( Box.createHorizontalGlue() );
		toolTipPanel.add( getToolTipDismissSpinner() );
		toolTipPanel.add( Box.createHorizontalStrut(5) );
		toolTipPanel.add( Box.createHorizontalGlue() );
		toolTipPanel.add( new JLabel(messageSource.getMessage("preferences.miscellaneous.toolTipMS")) );
		
		JPanel viewModePanel = new JPanel();
		if( mode == SignalMLOperationMode.APPLICATION ) {
			viewModePanel.setLayout( new GridLayout(3,2,3,3) );
		} else {
			viewModePanel.setLayout( new GridLayout(2,2,3,3) );
		}
		viewModePanel.setBorder( new CompoundBorder( 
				new TitledBorder(messageSource.getMessage("preferences.miscellaneous.viewMode")),
				new EmptyBorder(3,3,3,3)
		));
		
		viewModePanel.add( getViewModeHidesMainToolBarCheckBox() );
		if( mode == SignalMLOperationMode.APPLICATION ) {
			viewModePanel.add( getViewModeHidesLeftPanelCheckBox() );
			viewModePanel.add( getViewModeHidesBottomPanelCheckBox() );
		}
		viewModePanel.add( getViewModeCompactsPageTagBarsCheckBox() );
		viewModePanel.add( getViewModeSnapsToPageCheckBox() );
		
		add( generalPanel, BorderLayout.CENTER );
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add( toolTipPanel, BorderLayout.NORTH );
		southPanel.add( viewModePanel, BorderLayout.CENTER );
		
		add( southPanel, BorderLayout.SOUTH );
		
	}

	public JCheckBox getSaveConfigOnEveryChangeCheckBox() {
		if( saveConfigOnEveryChangeCheckBox == null ) {
			saveConfigOnEveryChangeCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.saveConfigOnEveryChange"));
		}
		return saveConfigOnEveryChangeCheckBox;
	}
	
	public JCheckBox getRestoreWorkspaceCheckBox() {
		if( restoreWorkspaceCheckBox == null ) {
			restoreWorkspaceCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.restoreWorkspace"));
		}
		return restoreWorkspaceCheckBox;
	}

	public JCheckBox getDisableSeriousWarningsCheckBox() {
		if( disableSeriousWarningsCheckBox == null ) {
			disableSeriousWarningsCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.disableSeriousWarnings"));
		}
		return disableSeriousWarningsCheckBox;
	}
		
	public JCheckBox getDontShowCompilationWarningCheckBox() {
		if( dontShowCompilationWarningCheckBox == null ) {
			dontShowCompilationWarningCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.dontShowCompilationWarning"));
		}
		return dontShowCompilationWarningCheckBox;
	}
	
	public JCheckBox getViewModeHidesMainToolBarCheckBox() {
		if( viewModeHidesMainToolBarCheckBox == null ) {
			viewModeHidesMainToolBarCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeHidesMainToolBar"));
		}
		return viewModeHidesMainToolBarCheckBox;
	}

	public JCheckBox getViewModeHidesLeftPanelCheckBox() {
		if( viewModeHidesLeftPanelCheckBox == null ) {
			viewModeHidesLeftPanelCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeHidesLeftPanel"));
		}
		return viewModeHidesLeftPanelCheckBox;
	}

	public JCheckBox getViewModeHidesBottomPanelCheckBox() {
		if( viewModeHidesBottomPanelCheckBox == null ) {
			viewModeHidesBottomPanelCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeHidesBottomPanel"));
		}
		return viewModeHidesBottomPanelCheckBox;
	}

	public JCheckBox getViewModeCompactsPageTagBarsCheckBox() {
		if( viewModeCompactsPageTagBarsCheckBox == null ) {
			viewModeCompactsPageTagBarsCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeCompactsPageTagBars"));
		}
		return viewModeCompactsPageTagBarsCheckBox;
	}

	public JCheckBox getViewModeSnapsToPageCheckBox() {
		if( viewModeSnapsToPageCheckBox == null ) {
			viewModeSnapsToPageCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeSnapsToPage"));
		}
		return viewModeSnapsToPageCheckBox;
	}
	
	public JSpinner getToolTipInitialSpinner() {
		if( toolTipInitialSpinner == null ) {
			toolTipInitialSpinner = new JSpinner( new SpinnerNumberModel(100,100,100000,100) );
			toolTipInitialSpinner.setPreferredSize(new Dimension(100,25));
		}
		return toolTipInitialSpinner;
	}

	public JSpinner getToolTipDismissSpinner() {
		if( toolTipDismissSpinner == null ) {
			toolTipDismissSpinner = new JSpinner( new SpinnerNumberModel(100,100,100000,100) );
			toolTipDismissSpinner.setPreferredSize(new Dimension(100,25));
		}
		return toolTipDismissSpinner;
	}

	public void fillPanelFromModel( ApplicationConfiguration applicationConfig ) {
		
		if( mode == SignalMLOperationMode.APPLICATION ) {
			getSaveConfigOnEveryChangeCheckBox().setSelected( applicationConfig.isSaveConfigOnEveryChange() );
			getRestoreWorkspaceCheckBox().setSelected( applicationConfig.isRestoreWorkspace() );
		}
		getDisableSeriousWarningsCheckBox().setSelected( applicationConfig.isDisableSeriousWarnings() );
		if( mode == SignalMLOperationMode.APPLICATION ) {
			getDontShowCompilationWarningCheckBox().setSelected( applicationConfig.isDontShowDynamicCompilationWarning() );
		}
		
		getViewModeHidesMainToolBarCheckBox().setSelected( applicationConfig.isViewModeHidesBottomPanel() );
		if( mode == SignalMLOperationMode.APPLICATION ) {
			getViewModeHidesLeftPanelCheckBox().setSelected( applicationConfig.isViewModeHidesLeftPanel() );
			getViewModeHidesBottomPanelCheckBox().setSelected( applicationConfig.isViewModeHidesBottomPanel() );
		}
		getViewModeCompactsPageTagBarsCheckBox().setSelected( applicationConfig.isViewModeCompactsPageTagBars() );
		getViewModeSnapsToPageCheckBox().setSelected( applicationConfig.isViewModeSnapsToPage() );
		
		getToolTipInitialSpinner().setValue( applicationConfig.getToolTipInitialDelay() );
		getToolTipDismissSpinner().setValue( applicationConfig.getToolTipDismissDelay() );
		
	}
	
	public void fillModelFromPanel( ApplicationConfiguration applicationConfig ) {
		
		if( mode == SignalMLOperationMode.APPLICATION ) {
			applicationConfig.setSaveConfigOnEveryChange( getSaveConfigOnEveryChangeCheckBox().isSelected() );
			applicationConfig.setRestoreWorkspace( getRestoreWorkspaceCheckBox().isSelected() );
		}
		applicationConfig.setDisableSeriousWarnings( getDisableSeriousWarningsCheckBox().isSelected() );
		if( mode == SignalMLOperationMode.APPLICATION ) {
			applicationConfig.setDontShowDynamicCompilationWarning( getDontShowCompilationWarningCheckBox().isSelected() );
		}
		
		applicationConfig.setViewModeHidesMainToolBar( getViewModeHidesMainToolBarCheckBox().isSelected() );
		if( mode == SignalMLOperationMode.APPLICATION ) {
			applicationConfig.setViewModeHidesLeftPanel( getViewModeHidesLeftPanelCheckBox().isSelected() );
			applicationConfig.setViewModeHidesBottomPanel( getViewModeHidesBottomPanelCheckBox().isSelected() );
		}
		applicationConfig.setViewModeCompactsPageTagBars( getViewModeCompactsPageTagBarsCheckBox().isSelected() );
		applicationConfig.setViewModeSnapsToPage( getViewModeSnapsToPageCheckBox().isSelected() );
		
		applicationConfig.setToolTipInitialDelay( (Integer) getToolTipInitialSpinner().getValue() );
		applicationConfig.setToolTipDismissDelay( (Integer) getToolTipDismissSpinner().getValue() );
				
	}
	
	public void validate(Errors errors) {
		// do nothing
	}
		
}

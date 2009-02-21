/* EditMontageReferencePanel.java created 2007-10-24
 * 
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.SeriousWarningDescriptor;
import org.signalml.app.montage.MontageGeneratorListModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.SeriousWarningDialog;
import org.signalml.app.view.element.CompactButton;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.MontageGenerator;
import org.signalml.domain.montage.SourceMontageEvent;
import org.signalml.domain.montage.SourceMontageListener;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/** EditMontageReferencePanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageGeneratorPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageGeneratorPanel.class);
	
	private MessageSourceAccessor messageSource;
	
	private Montage montage;
	
	private JComboBox generatorComboBox;
	private MontageGeneratorListModel montageGeneratorListModel;
	private ErrorsDialog errorsDialog;
	private SeriousWarningDialog seriousWarningDialog;
	
	private ShowErrorsAction showErrorsAction;
	private ReloadAction reloadAction;
	
	private CompactButton showErrorsButton;
	private CompactButton reloadButton;
	
	private MontagePropertyListener montagePropertyListener;
	private SourceMontageChangeListener sourceMontageChangeListener;
	
	private boolean lockComboEvents = false;
			
	public MontageGeneratorPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}
	
	private void initialize() {

		montagePropertyListener = new MontagePropertyListener();
		sourceMontageChangeListener = new SourceMontageChangeListener();
		
		setLayout(new BorderLayout());
		
		JPanel choicePanel = new JPanel();
		choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.X_AXIS));
		
		CompoundBorder border = new CompoundBorder(
				new TitledBorder( messageSource.getMessage("signalMontage.chooseGenerator") ),
				new EmptyBorder(3,3,3,3)
		);				
		choicePanel.setBorder( border );
		
		choicePanel.add(new JLabel( messageSource.getMessage("signalMontage.generator")) );
		choicePanel.add( Box.createHorizontalStrut(5) );
		choicePanel.add( Box.createHorizontalGlue() );
		choicePanel.add(getGeneratorComboBox());
		choicePanel.add( Box.createHorizontalStrut(10) );
		choicePanel.add(getReloadButton());
		choicePanel.add( Box.createHorizontalStrut(5) );
		choicePanel.add(getShowErrorsButton());
				
		add(choicePanel, BorderLayout.CENTER);
						
	}
		
	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		if( this.montage != montage ) {
			if( this.montage != null ) {
				this.montage.removePropertyChangeListener(Montage.MONTAGE_GENERATOR_PROPERTY, montagePropertyListener);
				this.montage.removeSourceMontageListener(sourceMontageChangeListener);
			}
			this.montage = montage;
			MontageGenerator generator = null; 
			if( montage != null ) {
				montage.addPropertyChangeListener(Montage.MONTAGE_GENERATOR_PROPERTY, montagePropertyListener);
				montage.addSourceMontageListener(sourceMontageChangeListener);
				getMontageGeneratorListModel().setConfigurer( montage.getSignalTypeConfigurer() );
				generator = montage.getMontageGenerator();
			} else {
				getMontageGeneratorListModel().setConfigurer(null);
			}
			quietSetSelectedGenerator(generator);
		}
	}
	
	public ErrorsDialog getErrorsDialog() {
		return errorsDialog;
	}

	public void setErrorsDialog(ErrorsDialog errorsDialog) {
		this.errorsDialog = errorsDialog;
	}
		
	public SeriousWarningDialog getSeriousWarningDialog() {
		return seriousWarningDialog;
	}

	public void setSeriousWarningDialog(SeriousWarningDialog seriousWarningDialog) {
		this.seriousWarningDialog = seriousWarningDialog;
	}

	public MontageGeneratorListModel getMontageGeneratorListModel() {
		if( montageGeneratorListModel == null ) {
			montageGeneratorListModel = new MontageGeneratorListModel();
		}
		return montageGeneratorListModel;
	}

	public JComboBox getGeneratorComboBox() {
		if( generatorComboBox == null ) {
			generatorComboBox = new ResolvableComboBox(messageSource);
			generatorComboBox.setModel(getMontageGeneratorListModel());
			generatorComboBox.setPreferredSize( new Dimension(300,25) );
			
			generatorComboBox.addActionListener( new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					if( lockComboEvents ) {
						return;
					}
					
					Object item = getGeneratorComboBox().getSelectedItem();
					if( montage == null ) {
						return;
					}
					if( !(item instanceof MontageGenerator) ) {
						montage.setMontageGenerator(null);
						setEnableds();
						return;
					}
					
					MontageGenerator generator = (MontageGenerator) item;
					setEnableds();
					tryGenerate(generator);
										
				}
				
			});
			
		}
		return generatorComboBox;
	}
	
	public ShowErrorsAction getShowErrorsAction() {
		if( showErrorsAction == null ) {
			showErrorsAction = new ShowErrorsAction();
		}
		return showErrorsAction;
	}

	public ReloadAction getReloadAction() {
		if( reloadAction == null ) {
			reloadAction = new ReloadAction();
		}
		return reloadAction;
	}

	public CompactButton getShowErrorsButton() {
		if( showErrorsButton == null ) {
			showErrorsButton = new CompactButton(getShowErrorsAction());
		}
		return showErrorsButton;
	}

	public CompactButton getReloadButton() {
		if( reloadButton == null ) {
			reloadButton = new CompactButton(getReloadAction());
		}
		return reloadButton;
	}
	
	private void quietSetSelectedGenerator( MontageGenerator generator ) {
		
		try {
			lockComboEvents = true;
			if( generator == null ) {
				getGeneratorComboBox().setSelectedIndex(0);
			} else {
				getGeneratorComboBox().setSelectedItem(generator);
			}
		} finally {
			lockComboEvents = false;
		}
		getGeneratorComboBox().repaint();
		setEnableds();		
		
	}

	public void tryGenerate( MontageGenerator generator ) {
		
		Errors errors = new BindException(montage, "montage");
		generator.validateSourceMontage(montage, errors);
		
		if( errors.hasErrors() ) {			
			errorsDialog.showErrors(errors);
			return;			
		}
		
		if( montage.isChanged() ) {
			
			String warning =  messageSource.getMessage( "montageTable.onGenerate" );
			SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);
			
			boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
			if( !ok ) {
				quietSetSelectedGenerator(null);
				return;
			}
			
		}
		
		try {
			generator.createMontage(montage);
		} catch (MontageException ex) {
			logger.error( "Montage generation failed", ex );
			errorsDialog.showException(ex);
			quietSetSelectedGenerator(null);
			return;
		}
		
	}
	
	public void setEnableds() {
		
		Object item = getGeneratorComboBox().getSelectedItem();
		if( !(item instanceof MontageGenerator) ) {
			getShowErrorsAction().setEnabled(false);
			getReloadAction().setEnabled(false);
			return;
		}
		
		MontageGenerator generator = (MontageGenerator) item;
		
		Errors errors = new BindException(montage, "montage");
		generator.validateSourceMontage(montage, errors);
		boolean hasErrors = errors.hasErrors();
		
		getShowErrorsAction().setEnabled( hasErrors );
		getReloadAction().setEnabled( !hasErrors );
		
	}
	
	protected class ShowErrorsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ShowErrorsAction() {
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/errormedium.png") );
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage("signalMontage.generatorErrorLabelToolTip"));
		}
		
		public void actionPerformed(ActionEvent ev) {			

			Object item = getGeneratorComboBox().getSelectedItem();
			if( !(item instanceof MontageGenerator) ) {
				return;
			}
			
			MontageGenerator generator = (MontageGenerator) item;
			
			Errors errors = new BindException(montage, "montage");
			generator.validateSourceMontage(montage, errors);
			
			if( errors.hasErrors() ) {
				
				errorsDialog.showErrors(errors);
				
			}			
			
		}
	
	}

	protected class ReloadAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ReloadAction() {
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/reloadmedium.png") );
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage("signalMontage.reloadToolTip"));
		}
		
		public void actionPerformed(ActionEvent ev) {			

			Object item = getGeneratorComboBox().getSelectedItem();
			if( !(item instanceof MontageGenerator) ) {
				return;
			}
			
			MontageGenerator generator = (MontageGenerator) item;
			tryGenerate(generator);
			
		}
	
	}
	
	protected class MontagePropertyListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			quietSetSelectedGenerator((MontageGenerator) evt.getNewValue());
		}
		
	}
	
	protected class SourceMontageChangeListener implements SourceMontageListener {

		private void onChange() {
			if( getGeneratorComboBox().getSelectedItem() instanceof MontageGenerator ) {
				setEnableds();
			}
		}
		
		@Override
		public void sourceMontageChannelAdded(SourceMontageEvent ev) {
			onChange();
		}

		@Override
		public void sourceMontageChannelChanged(SourceMontageEvent ev) {
			onChange();
		}

		@Override
		public void sourceMontageChannelRemoved(SourceMontageEvent ev) {
			onChange();
		}
		
	}
	
}

package org.signalml.app.view.dialog;

import java.awt.Color;
import java.awt.Window;
import java.io.IOException;
import java.net.URL;

import javax.swing.JComponent;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.FileSelectPanel;
import org.signalml.app.view.element.MonitorChannelSelectPanel;
import org.signalml.app.view.element.MultiplexerConnectionPanel;
import org.signalml.app.view.element.MonitorParamsPanel;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.ClassPathResource;

/** 
 * 
 */
public class OpenMonitorDialog extends AbstractWizardDialog {

	private static final long serialVersionUID = 1L;

	public static final int TIMEOUT_MILIS = 500;
	public static final int TRYOUT_COUNT = 10;

	public static final Color SUCCESS_COLOR = Color.GREEN;
	public static final Color FAILURE_COLOR = Color.RED;

	private ApplicationConfiguration applicationConfig;

	private URL contextHelpURL = null;

	private ViewerElementManager elementManager;

	public OpenMonitorDialog( MessageSourceAccessor messageSource, ViewerElementManager elementManager) {
		super( messageSource);
		this.elementManager = elementManager;
	}

	public OpenMonitorDialog( MessageSourceAccessor messageSource, 
							  ViewerElementManager elementManager, 
							  Window f, 
							  boolean isModal) {
		super( messageSource, f, isModal);
		this.elementManager = elementManager;
	}

	protected JComponent createMonitorStepOnePanel() {
		return new MultiplexerConnectionPanel( elementManager);
	}

	protected MultiplexerConnectionPanel getMultiplexerConnectionPanel() {
		return (MultiplexerConnectionPanel) getInterfaceForStep( 0);
	}

	protected JComponent createMonitorStepTwoPanel() {
		return new MonitorParamsPanel( messageSource);
	}

	public MonitorParamsPanel getMonitorParamsPanel() {
		return (MonitorParamsPanel) getInterfaceForStep( 1);
	}

	protected JComponent createMonitorStepThreePanel() {
		return new MonitorChannelSelectPanel( messageSource);
	}

	public MonitorChannelSelectPanel getChannelSelectPanel() {
		return (MonitorChannelSelectPanel) getInterfaceForStep( 2);
	}

	protected JComponent createMonitorStepFourPanel() {
		return new FileSelectPanel(messageSource, messageSource.getMessage( "openMonitor.saveDataLabel"));
	}

	public FileSelectPanel getFileSelectPanel() {
		return (FileSelectPanel) getInterfaceForStep( 3);
	}

	@Override
	protected JComponent createInterfaceForStep(int step) {
		switch( step ) {
		case 0 :
			return createMonitorStepOnePanel();
		case 1 :
			return createMonitorStepTwoPanel();
		case 2 :
			return createMonitorStepThreePanel();
		case 3 :
			return createMonitorStepFourPanel();
		default :
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public int getStepCount() {
		return 4;
	}

	@Override
	public boolean isFinishAllowedOnStep(int step) {
		return (step == 3);
	}

	@Override
	protected boolean onStepChange(int toStep, int fromStep, Object model)
			throws SignalMLException {
		if (fromStep == 0) {
			if (elementManager.getJmxClient() != null) {
				OpenMonitorDescriptor m = (OpenMonitorDescriptor) model;
				fillParamsPanel( m);
				fillChannelListPanel( m);
				return true;
			}
			else
				return false;
		}
		else if (fromStep == 1) {
			if (toStep == 0)
				return true;
			MonitorParamsPanel p = getMonitorParamsPanel();
			String t = p.getPageSizeField().getText();
			if (t != null && !"".equals( t))
				return true;
			else
				return false;
		}
		else if (fromStep == 2) {
			if (toStep == 1)
				return true;
			MonitorChannelSelectPanel p = getChannelSelectPanel();
			int[] i = p.getChannelList().getSelectedIndices();
			if (i != null && i.length > 0)
				return true;
			else
				return false;
		}
		else if (fromStep == 3) {
			if (toStep == 2)
				return true;
			FileSelectPanel p = getFileSelectPanel();
			return p.isFileSelected();
		}
		return super.onStepChange(toStep, fromStep, model);
	}

	public void cancelConnection() {
		getMultiplexerConnectionPanel().cancel();
	}

	@Override
	protected boolean onCancel() {
		cancelConnection();
		return super.onCancel();
	}

	@Override
	protected void initialize() {
		
		setTitle(messageSource.getMessage("openMonitor.title"));
		
		super.initialize();

	}

	private void fillParamsPanel( OpenMonitorDescriptor  m) {
		Float freq = m.getSamplingFrequency();
		if (freq != null)
			getMonitorParamsPanel().getSamplingField().setText( freq.toString());
		Integer channelCount = m.getChannelCount();
		if (channelCount == null)
			channelCount = 1;
		getMonitorParamsPanel().getChannelCountField().setText( channelCount.toString());
	}

	private void fillChannelListPanel( OpenMonitorDescriptor  m) {
		String[] channelLabels = m.getChannelLabels();
		if (channelLabels == null) {
			Integer channelCount = m.getChannelCount();
			if (channelCount == null)
				channelCount = 1;
			channelLabels = new String[channelCount];
			for (int i=0; i<channelCount; i++)
				channelLabels[i] = Integer.toBinaryString( i);
		}
		getChannelSelectPanel().getChannelList().setListData( channelLabels);
	}

	@Override
	public void fillDialogFromModel(Object model) {

		OpenMonitorDescriptor m = (OpenMonitorDescriptor)  model;

		getMultiplexerConnectionPanel().setOpenMonitorDescriptor( m);

		String address = m.getMultiplexerAddress();
		if (address == null)
			address = applicationConfig.getMultiplexerAddress();
		getMultiplexerConnectionPanel().getMultiplexerAddressField().setText( address);

		int port = m.getMultiplexerPort();
		if (port == -1)
			port = applicationConfig.getMultiplexerPort();
		getMultiplexerConnectionPanel().getMultiplexerPortField().setText( Integer.toString( port));

		Double pageSize = m.getPageSize();
		if (pageSize == null)
			pageSize = applicationConfig.getMonitorPageSize();
		getMonitorParamsPanel().getPageSizeField().setText( Double.toString( pageSize));

		String fileName = m.getFileName();
		if (fileName == null)
			fileName = applicationConfig.getSignalRecorderFileName();
		getFileSelectPanel().setFileName(fileName);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		OpenMonitorDescriptor m = (OpenMonitorDescriptor) model;

		String fileName = getFileSelectPanel().getFileName();
		if (fileName.endsWith( ".raw") || fileName.endsWith( ".xml"))
			fileName = fileName.substring( 0, fileName.length() - 4);
		m.setFileName( fileName);

		String adres = getMultiplexerConnectionPanel().getMultiplexerAddressField().getText();
		m.setMultiplexerAddress( adres);
		applicationConfig.setMultiplexerAddress( adres);
		int port = Integer.parseInt( getMultiplexerConnectionPanel().getMultiplexerPortField().getText());
		m.setMultiplexerPort( port);
		applicationConfig.setMultiplexerPort( port);
		m.setJmxClient( elementManager.getJmxClient());
		m.setTagClient( elementManager.getTagClient());

		m.setPageSize( Double.parseDouble( getMonitorParamsPanel().getPageSizeField().getText()));

		try {
			m.setSelectedChannelList( getChannelSelectPanel().getChannelList().getSelectedValues());
		} 
		catch (Exception e) {
			throw new SignalMLException( e);
		}
		
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return OpenMonitorDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	protected URL getContextHelpURL() {
		if( contextHelpURL == null ) {
			 try {
				 contextHelpURL = (new ClassPathResource("org/signalml/help/contents.html")).getURL();
				 contextHelpURL = new URL( contextHelpURL.toExternalForm() + "#opendoc" );
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}				
		}
		return contextHelpURL;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

}


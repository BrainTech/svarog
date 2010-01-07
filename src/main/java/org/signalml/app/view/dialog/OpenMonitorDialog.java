package org.signalml.app.view.dialog;

import java.awt.Color;
import java.awt.Window;
import java.io.IOException;
import java.net.URL;

import javax.swing.JComponent;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.element.MultiplexerConnectionPanel;
import org.signalml.app.view.element.MonitorParamsPanel;
import org.signalml.exception.SignalMLException;
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

    @Override
    protected JComponent createInterfaceForStep(int step) {
        switch( step ) {
        case 0 :
            return createMonitorStepOnePanel();
        case 1 :
            return createMonitorStepTwoPanel();
        default :
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public int getStepCount() {
        return 2;
    }

    @Override
    public boolean isFinishAllowedOnStep(int step) {
        return (step >= 1);
    }

	@Override
    protected boolean onStepChange(int toStep, int fromStep, Object model)
            throws SignalMLException {
	    if (fromStep == 0) {
	        return elementManager.getJmxClient() != null;
	    }
        return super.onStepChange(toStep, fromStep, model);
    }

    @Override
	protected void initialize() {
		
		setTitle(messageSource.getMessage("openMonitor.title"));
		
		super.initialize();

	}

	@Override
	public void fillDialogFromModel(Object model) {

		OpenMonitorDescriptor m = (OpenMonitorDescriptor)  model;

		String address = m.getMultiplexerAddress();
		if (address == null)
		    address = applicationConfig.getMultiplexerAddress();
		getMultiplexerConnectionPanel().getMultiplexerAddressField().setText( address);

        int port = m.getMultiplexerPort();
        if (port == -1)
            port = applicationConfig.getMultiplexerPort();
		getMultiplexerConnectionPanel().getMultiplexerPortField().setText( Integer.toString( port));

        Float freq = m.getSamplingFrequency();
        if (freq == null)
            freq = applicationConfig.getMonitorSamplingFrequency();
		getMonitorParamsPanel().getSamplingField().setText( freq.toString());
        Integer channelCount = m.getChannelCount();
        if (channelCount == null)
            channelCount = 1; // TODO nie wiadomo jeszcze jak będzie ustalane, które kanały mają byc wyświetlane
		getMonitorParamsPanel().getChannelCountField().setText( channelCount.toString());
        Float gain = m.getCalibrationGain();
        if (gain == null)
            gain = applicationConfig.getMonitorCalibrationGain();
		getMonitorParamsPanel().getCalibrationGainField().setText( Float.toString( gain));
        Float offset = m.getCalibrationOffset();
        if (offset == null)
            offset = applicationConfig.getMonitorCalibrationOffset();
        getMonitorParamsPanel().getCalibrationOffsetField().setText( Float.toString( offset));
        Float pageSize = m.getPageSize();
        if (pageSize == null)
            pageSize = applicationConfig.getMonitorPageSize();
		getMonitorParamsPanel().getPageSizeField().setText( Float.toString( pageSize));

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

	    OpenMonitorDescriptor m = (OpenMonitorDescriptor) model;

	    m.setMultiplexerAddress( getMultiplexerConnectionPanel().getMultiplexerAddressField().getText());
		m.setMultiplexerPort( Integer.parseInt( getMultiplexerConnectionPanel().getMultiplexerPortField().getText()));
		m.setJmxClient( elementManager.getJmxClient());

		m.setSamplingFrequency( Float.parseFloat( getMonitorParamsPanel().getSamplingField().getText()));
		m.setChannelCount( Integer.parseInt( getMonitorParamsPanel().getChannelCountField().getText()));
		m.setCalibrationGain( Float.parseFloat( getMonitorParamsPanel().getCalibrationGainField().getText()));
        m.setCalibrationOffset( Float.parseFloat( getMonitorParamsPanel().getCalibrationOffsetField().getText()));

		m.setPageSize( Float.parseFloat( getMonitorParamsPanel().getPageSizeField().getText()));

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


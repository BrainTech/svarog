package org.signalml.app.video;

import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.video.components.OnlineMediaComponent;
import org.signalml.app.video.components.OnlineMediaPlayerPanel;
import org.signalml.app.video.components.VideoStreamSelectionPanel;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;

/**
 *
 * @author Marian Dovgialo
 */
public class VideoStreamSelectedWorker extends SwingWorkerWithBusyDialog<Void, Void> {
        OnlineMediaPlayerPanel previewPanel;
        VideoStreamManager manager;
        OnlineMediaComponent component;
        VideoStreamSelectionPanel streamSelectionPanel;
        VideoStreamSpecification stream;
        protected static final Logger logger = Logger.getLogger(VideoStreamSelectedWorker.class);
   
    	public VideoStreamSelectedWorker(OnlineMediaPlayerPanel previewPanel, VideoStreamManager manager, OnlineMediaComponent component, VideoStreamSelectionPanel streamSelectionPanel, VideoStreamSpecification stream) {
		super(previewPanel);
                this.previewPanel = previewPanel;
                this.manager = manager;
                this.component = component;
                this.streamSelectionPanel = streamSelectionPanel;
                this.stream = stream;
		getBusyDialog().setText(_("Starting video preview."));
		getBusyDialog().setCancellable(false);
	}
        
    	@Override
	protected Void doInBackground() throws Exception {

            showBusyDialog();
            component.release();
            try {
                    String rtspURL = manager.replace(stream);
                    previewPanel.setCameraFeatures(stream.features);
                    component.open(rtspURL);
            } catch (OpenbciCommunicationException ex) {
                    streamSelectionPanel.clearSelection();
                    ex.showErrorDialog(_("Error initializing video preview"));
            }
			

            return null;
        }
}

package org.signalml.app.video;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.Timer;
import org.signalml.app.video.components.OnlineMediaPlayerComponent;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import uk.co.caprica.vlcj.player.MediaPlayer;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Simple modal dialog for displaying a preview of a single RTSP stream.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class PreviewVideoDialog extends JDialog {

	private static final int START_DELAY_MILLIS = 500;

	private final MediaPlayer player;
	private final VideoStreamManager manager;
	private final String rtspURL;

	public PreviewVideoDialog(Window parentWindow, VideoStreamSpecification stream) throws OpenbciCommunicationException {
		super(parentWindow, _("video preview (close to continue)"));
		OnlineMediaPlayerComponent component = new OnlineMediaPlayerComponent();
		player = component.getMediaPlayer();
		manager = component.getManager();
		rtspURL = manager.replace(stream);

		setLayout(new BorderLayout());
		add(component, BorderLayout.CENTER);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		setPreferredSize(new Dimension(400, 400));
		pack();
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			// we have to delay start of the player until this dialog is visible
			Timer delayedStart = new Timer(START_DELAY_MILLIS, (ActionEvent e) -> {
				player.playMedia(rtspURL);
			});
			delayedStart.setRepeats(false);
			delayedStart.start();
		}
		super.setVisible(b);
	}

	@Override
	public void dispose() {
		player.stop();
		manager.free();
		super.dispose();
	}

}

package org.signalml.app.video.components;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.video.VideoSourceSpecification;
import org.signalml.app.video.VideoStreamSpecification;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Panel for selecting video stream from a list of available streams.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class VideoStreamSelectionCompactPanel extends JPanel {

	private final DefaultComboBoxModel streamModel;
	private final JComboBox streamCombo;
	private final JButton previewButton;
	private final JButton refreshButton;

	/**
	 * Create a new panel with a predefined listener.
	 *
	 * @param listener  must NOT be null
	 */
	public VideoStreamSelectionCompactPanel(VideoStreamSelectionListener listener) {
		super(new BorderLayout());
		streamModel = new DefaultComboBoxModel<>();

		streamCombo = new JComboBox(streamModel);
		previewButton = new JButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/camera.png"));
		previewButton.addActionListener((ActionEvent e) -> {
			VideoStreamSpecification stream = getSelectedVideoStream();
			if (stream == null) {
				JOptionPane.showMessageDialog(this, _("You have to select a valid video stream."), _("Preview not available"), JOptionPane.ERROR_MESSAGE);
			} else {
				listener.previewRequested(stream);
			}
		});
		refreshButton = new JButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/reload.png"));
		refreshButton.addActionListener((ActionEvent e) -> {
			listener.refreshRequested();
		});
		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
		buttonsPanel.add(previewButton);
		buttonsPanel.add(refreshButton);
		add(streamCombo, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.EAST);
		setAvailableSources(Collections.EMPTY_LIST);
	}

	public VideoStreamSpecification getSelectedVideoStream() {
		Object item = streamModel.getSelectedItem();
		if (item instanceof VideoStreamSpecification) {
			return (VideoStreamSpecification) item;
		} else {
			return null;
		}
	}

	public void setSelectedVideoStream(VideoStreamSpecification stream) {
		streamModel.setSelectedItem(stream);
	}

	/**
	 * Update displayed list of available sources.
	 *
	 * @param cameras  list of video source capabilities
	 */
	public void setAvailableSources(List<VideoSourceSpecification> cameras) {
		streamModel.removeAllElements();
		boolean hasSomeStreams = false;
		for (VideoSourceSpecification camera : cameras) {
			streamModel.addElement("["+camera.description+"]");
			for (VideoStreamSpecification stream : camera.streams) {
				streamModel.addElement(stream);
				hasSomeStreams = true;
			}
		}
		String dummyItemLabel = hasSomeStreams ? _("(select video stream)") : _("(no streams available)");
		streamModel.insertElementAt(dummyItemLabel, 0);
		streamModel.setSelectedItem(dummyItemLabel);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		streamCombo.setEnabled(enabled);
		refreshButton.setEnabled(enabled);
		previewButton.setEnabled(enabled);
	}

}

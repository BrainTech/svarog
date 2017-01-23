package org.signalml.app.video.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.util.IconUtils;
import org.signalml.app.video.VideoSourceSpecification;
import org.signalml.app.video.VideoStreamSpecification;

/**
 * Panel for selecting video source and stream from a list of available sources.
 * Panel consists of two parts: on the top of the panel, a combo-box displaying
 * the list of available cameras. The main part of the panel is a list of video
 * streams provided by a currently selected camera.
 *
 * @author piotr.rozanski@onet.pl
 */
public final class VideoStreamSelectionPanel extends JPanel {

	protected static final Logger logger = Logger.getLogger(VideoStreamSelectionPanel.class);

	private final VideoStreamSelectionListener listener;
	private final DefaultComboBoxModel<VideoSourceSpecification> cameraModel;
	private final DefaultListModel<VideoStreamSpecification> streamModel;

	private final JList streamList;
	private final JScrollPane streamListContainer;

	/**
	 * Create a new panel with a predefined listener.
	 *
	 * @param listener  must NOT be null
	 */
	public VideoStreamSelectionPanel(VideoStreamSelectionListener listener) {
		super(new BorderLayout());
		this.listener = listener;
		cameraModel = new DefaultComboBoxModel<>();
		streamModel = new DefaultListModel<>();
		streamList = new JList(streamModel);
		streamList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		streamListContainer = new JScrollPane(streamList);
		Dimension size = new Dimension(200, 200);
		setMinimumSize(size);
		setPreferredSize(size);
		add(createSourcePanel(), BorderLayout.NORTH);
		add(createStreamPanel(), BorderLayout.CENTER);
	}

	/**
	 * Update displayed list of available sources.
	 * Given list of specifications should not be modified
	 * after being passed to this method.
	 *
	 * @param cameras  list of video source capabilities
	 */
	public void setAvailableSources(List<VideoSourceSpecification> cameras) {
		cameraModel.removeAllElements();
		cameraModel.addElement(new VideoSourceSpecification(
			"(select source)",
			Collections.EMPTY_LIST
		));
		for (VideoSourceSpecification camera : cameras) {
			cameraModel.addElement(camera);
		}
		streamModel.clear();
	}

	public void clearSelection() {
		streamList.clearSelection();
	}

	private void setAvailableStreams(List<VideoStreamSpecification> streams) {
		streamModel.clear();
		for (VideoStreamSpecification stream : streams) {
			streamModel.addElement(stream);
		}
	}

	private JPanel createSourcePanel() {
		JComboBox cameraCombo = new JComboBox(cameraModel);
		cameraCombo.addActionListener((ActionEvent e) -> {
			VideoSourceSpecification source = (VideoSourceSpecification) cameraModel.getSelectedItem();
			if (source != null) {
				setAvailableStreams(source.streams);
			}
		});
		JButton refreshButton = new JButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/reload.png"));
		refreshButton.addActionListener((ActionEvent e) -> {
			listener.refreshRequested();
		});
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(new JLabel("Camera:"), BorderLayout.NORTH);
		panel.add(cameraCombo, BorderLayout.CENTER);
		panel.add(refreshButton, BorderLayout.EAST);
		return panel;
	}

	private JPanel createStreamPanel() {
		streamList.addListSelectionListener((ListSelectionEvent e) -> {
			VideoStreamSpecification stream = (VideoStreamSpecification) streamList.getSelectedValue();
			if (stream != null) {
				listener.videoStreamSelected(stream);
			}
		});
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(new JLabel("Stream:"), BorderLayout.NORTH);
		panel.add(streamListContainer, BorderLayout.CENTER);
		return panel;
	}

}

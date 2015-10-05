package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.signalml.app.view.montage.visualreference.VisualReferenceModel;
import org.signalml.domain.montage.Montage;

/**
 * @author ptr@mimuw.edu.pl
 */
public class DescribeComponentsPanel extends JPanel {

	private final int componentCount;

	private final VisualReferenceModel intensityModel;
	private final VisualIntensityDisplay intensityDisplay;
	private final JScrollPane intensityPane;
	private final JList<String> channelDisplay;
	
	public DescribeComponentsPanel(final Montage montage) {
		super(new BorderLayout());

		intensityModel = new VisualReferenceModel();
		intensityModel.setMontage(montage);
		intensityDisplay = new VisualIntensityDisplay(intensityModel);

		intensityPane = new JScrollPane(intensityDisplay, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		intensityPane.setPreferredSize(new Dimension(500, 400));
		intensityDisplay.setViewport(intensityPane.getViewport());

		componentCount = montage.getMontageChannelCount();
		final String[] componentNames = new String[componentCount];
		final float[][] components = new float[componentCount][];
		for (int i=0; i<componentCount; ++i) {
			componentNames[i] = montage.getMontageChannelLabelAt(i);
			components[i] = montage.getReferenceAsFloat(i);
		}

		channelDisplay = new JList<String>(componentNames);
		channelDisplay.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		channelDisplay.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int component = channelDisplay.getSelectedIndex();
				if (component >= 0) {
					float[] intensities = components[component];
					if (intensities != null) {
						intensityDisplay.setIntensities(intensities);
					}
				}
			}
		});
		channelDisplay.setPreferredSize(new Dimension(50, 400));

		add(intensityPane, BorderLayout.CENTER);
		add(channelDisplay, BorderLayout.EAST);
	}

}

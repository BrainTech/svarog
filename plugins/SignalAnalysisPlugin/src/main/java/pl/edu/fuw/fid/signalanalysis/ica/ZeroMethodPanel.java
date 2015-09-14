package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.signalml.app.view.montage.visualreference.VisualReferenceModel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.space.SignalSpaceConstraints;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ZeroMethodPanel extends JPanel {

	private final int componentCount;

	private final VisualReferenceModel intensityModel;
	private final VisualIntensityDisplay intensityDisplay;
	private final JScrollPane intensityPane;

	private final DefaultTableModel channelModel;
	private final JTable channelDisplay;
	
	public ZeroMethodPanel(final Montage montage, SignalSpaceConstraints signalSpaceConstraints) {
		super(new BorderLayout());

		intensityModel = new VisualReferenceModel();
		intensityModel.setMontage(montage);
		intensityDisplay = new VisualIntensityDisplay(intensityModel);

		intensityPane = new JScrollPane(intensityDisplay, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		intensityPane.setPreferredSize(new Dimension(500, 400));
		intensityDisplay.setViewport(intensityPane.getViewport());

		componentCount = montage.getMontageChannelCount();
		Object[][] componentData = new Object[componentCount][2];
		for (int i=0; i<componentCount; ++i) {
			componentData[i][0] = Boolean.FALSE;
			componentData[i][1] = montage.getMontageChannelLabelAt(i);
		}

        channelModel = new DefaultTableModel(
            componentData, new String[] {"Remove?", "Component"}
        ) {
			@Override
            public Class getColumnClass(int columnIndex) {
				return columnIndex == 0 ? Boolean.class : String.class;
            }

			@Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 0;
            }
        };

		channelDisplay = new JTable(channelModel);
		channelDisplay.getTableHeader().setReorderingAllowed(false);
		channelDisplay.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		channelDisplay.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int component = channelDisplay.getSelectedRow();
				if (component >= 0) {
					float[] intensities = montage.getReferenceAsFloat(component);
					if (intensities != null) {
						intensityDisplay.setIntensities(intensities);
					}
				}
			}
		});

		add(intensityPane, BorderLayout.CENTER);
		add(channelDisplay, BorderLayout.EAST);
	}

	public int[] getSelectedChannels() {
		int[] selected = new int[componentCount];
		int count = 0;
		for (int i=0; i<componentCount; ++i) {
			if (channelModel.getValueAt(i, 0).equals(Boolean.TRUE)) {
				selected[count++] = i;
			}
		}
		return Arrays.copyOf(selected, count);
	}

}

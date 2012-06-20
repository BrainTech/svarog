/**
 *
 */
package org.signalml.plugin.exampleplugin;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.signalml.plugin.export.signal.ChannelSamples;

/**
 * The panel that contains a table with first 100 samples of every channel.
 *
 * @author Marcin Szumski
 */
public class SamplesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the table in which the samples of the signal are displayed
	 */
	private JTable table;

	/**
	 * Constructor.
	 * Creates a table and for every channel puts in it first 100 samples
	 * from it.
	 * Adds the table (within a scroll pane) to this panel.
	 * Sets the layout of this channel to {@code BorderLayout}.
	 * @param name the name of this panel (used as the title of the tab)
	 * @param samples the channels with samples to be displayed
	 */
	public SamplesPanel(String name, Collection<ChannelSamples> samples) {
		setName(name);
		String[] names = new String[] {"channel index", "first 100 samples"};
		int numberOfChannels = samples.size();
		String[][] data = new String[numberOfChannels][2];
		int i = 0;
		for (ChannelSamples channelSamples : samples) {
			data[i][0] = Integer.toString(i);
			String samplesString = new String();
			for (int j = 0; j < channelSamples.getSamples().length && j < 100; ++j) {
				samplesString+= (channelSamples.getSamples())[j];
				samplesString += ";";
			}
			data[i][1] = samplesString;
			++i;
		}
		setLayout(new BorderLayout());
		table = new JTable(data, names);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		add(scrollPane);
		setVisible(true);

	}


}

/**
 * 
 */
package org.signalml.plugin.exampleplugin;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ChannelSamples;
import org.signalml.plugin.export.signal.SignalSamples;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;

/**
 * When this action is performed the samples (first 100) from the active signal
 * are displayed in a new property tab.
 * If the action is performed second (actually even) time the tab is removed.
 * 
 * @author Marcin Szumski
 */
public class SamplesPanelAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link SvarogAccessSignal access} to signal options 
	 */
	private SvarogAccessSignal signalAccess;
	
	/**
	 * the {@link SvarogAccessGUI access} to GUI functions
	 */
	private SvarogAccessGUI guiAccess;
	
	/**
	 * the {@link SamplesPanel panel} that is added as a property
	 * tab (the tab in the bottom).
	 */
	private SamplesPanel samplesPanel = null;
	
	/**
	 * Constructor. Sets {@link SvarogAccessSignal signal access} and
	 * {@link SvarogAccessGUI GUI access}.
	 * @param signalAccess signal access to set
	 * @param guiAccess GUI access to set
	 */
	public SamplesPanelAction(SvarogAccessSignal signalAccess, SvarogAccessGUI guiAccess) {
		super("Show output of samples");
		this.signalAccess = signalAccess;
		this.guiAccess = guiAccess;
	}
	

	/**
	 * Gets the collection of {@link ChannelSamples} from the given
	 * {@link SignalSamples} object
	 * @param signalSamples the signal samples from which the channels samples
	 * are to be extracted
	 * @return the collection of {@link ChannelSamples}
	 */
	private Collection<ChannelSamples> toChannelsCollection(SignalSamples signalSamples){
		Collection<ChannelSamples> samples = Arrays.asList(signalSamples.getChannels());
		return samples;
	}
	
	/**
	 * Gets the (processed) samples from the active signal and displays them
	 * in the {@link SamplesPanel panel} which is added as a property tab
	 * (the tab in the bottom).
	 * If there no active signal appropriate communicate is shown.
	 * If the panel was already added as tab it is removed and disposed.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (samplesPanel == null){
				SignalSamples signalSamples;
				signalSamples = signalAccess.getActiveProcessedSignalSamples();
				Collection<ChannelSamples> samples = toChannelsCollection(signalSamples);
				samplesPanel = new SamplesPanel("Processed samples for active signal", samples);
				guiAccess.addPropertyTab(samplesPanel);
			} else {
				guiAccess.removePropertyTab(samplesPanel);
				samplesPanel = null;
			}

		} catch (NoActiveObjectException e1) {
			JOptionPane.showMessageDialog(null, "There is no active signal");
		} catch (IllegalArgumentException e1){
		}
	}

}

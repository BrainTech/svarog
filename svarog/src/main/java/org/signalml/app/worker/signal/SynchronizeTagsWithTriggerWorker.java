package org.signalml.app.worker.signal;

import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.worker.SwingWorkerWithBusyDialog;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.Tag;

/**
 * A worker which synchronizes tags with a trigger channel for a given document.
 *
 * @author Piotr Szachewicz
 */
public class SynchronizeTagsWithTriggerWorker extends SwingWorkerWithBusyDialog<Void, Object> {

	private SynchronizeTagsWithTriggerParameters parameters;
	private Integer[] positions;

	private StyledTagSet tagSet;

	public SynchronizeTagsWithTriggerWorker(SynchronizeTagsWithTriggerParameters parameters, Integer[] slopes) {
		super(null);
		this.parameters = parameters;
		this.positions = slopes;
		tagSet = parameters.getTagSet();

		getBusyDialog().setText(_("Synchronizing tags with trigger."));
		getBusyDialog().setCancellable(false);
	}

	@Override
	protected Void doInBackground() throws Exception {
		showBusyDialog();

		removeExcessiveTags();

		for (int i = 0; i < tagSet.getChannelTagCount(); i++) {
			Tag tag = tagSet.getChannelTagAt(i);

			tag.setPosition(positions[i] / parameters.getSampleSource().getSamplingFrequency());
			tagSet.editTag(tag); //fire an event informing that tag has changed
		}

		return null;
	}

	protected void removeExcessiveTags() {
		for (int i = tagSet.getChannelTagCount()-1; i >= positions.length ; i--) {
			Tag tag = tagSet.getChannelTagAt(i);
			tagSet.removeTag(tag);
		}
	}

}

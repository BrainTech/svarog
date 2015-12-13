package pl.edu.fuw.fid.signalanalysis.waveform;

import java.util.List;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;

/**
 * @author ptr@mimuw.edu.pl
 */
public final class AveragedBaseModel<P> {

	public final SignalDocument signalDocument;

	public PreferencesWithAxes<P> preferences;
	public int[] selectedChannels;
	public List<TagStyleGroup> selectedTags;

	public AveragedBaseModel(SignalDocument signalDocument) {
		this.signalDocument = signalDocument;
	}
}

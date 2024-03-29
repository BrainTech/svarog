package pl.edu.fuw.fid.signalanalysis.waveform;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.signalml.app.method.bookaverage.BookAverageMethodPanel;
import org.signalml.app.method.ep.view.tags.TagSelectionPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.signalselection.ChannelSpacePanel;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.SignalMLException;

/**
 * Base class for dialogs allowing to select options
 * for averaged time-frequency map.
 *
 * @author ptr@mimuw.edu.pl
 */
public abstract class AveragedBaseDialog<P> extends AbstractDialog {

	private TagSelectionPanel tagPanel;
	private ChannelSpacePanel channelPanel;
	private BookAverageMethodPanel averagePanel;

	public AveragedBaseDialog(Window w, Boolean isModal) {
		super(w, isModal);
	}

	protected abstract JPanel createCustomPanel();
	
	@Override
	protected JComponent createInterface() {
		JPanel panel = new JPanel(new GridLayout(1, 2));

		tagPanel = new TagSelectionPanel(_("Tags"));

		averagePanel = new BookAverageMethodPanel();
		averagePanel.setPageSelectionEnabled(false);

		JPanel fftPanel = createCustomPanel();

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(fftPanel, BorderLayout.NORTH);
		leftPanel.add(tagPanel, BorderLayout.CENTER);
		leftPanel.add(averagePanel, BorderLayout.SOUTH);

		channelPanel = new ChannelSpacePanel();

		panel.add(leftPanel);
		panel.add(channelPanel);
		return panel;
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		AveragedBaseModel m = (AveragedBaseModel) model;
		SignalView signalView = (SignalView) m.signalDocument.getDocumentView();
		SignalSpaceConstraints signalSpaceConstraints = signalView.createSignalSpaceConstraints();

		tagPanel.setTagDocument(m.signalDocument.getActiveTag());
		averagePanel.setFrequencyEnd(signalSpaceConstraints.getSamplingFrequency() / 2);
		channelPanel.setConstraints(signalSpaceConstraints);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		AveragedBaseModel m = (AveragedBaseModel) model;
		m.preferences = getPreferences();
		m.selectedChannels = channelPanel.getChannelList().getSelectedIndices();
		m.selectedTags = tagPanel.getSelectedTagStyles();
	}

	protected abstract P getCustomPreferences();

	protected PreferencesWithAxes<P> getPreferences() {
		P prefs = getCustomPreferences();
		int width = averagePanel.getSelectedWidth();
		int height = averagePanel.getSelectedHeight();
		double tMin = averagePanel.getTimeStart();
		double tMax = averagePanel.getTimeEnd();
		double fMin = averagePanel.getFrequencyStart();
		double fMax = averagePanel.getFrequencyEnd();
		return new PreferencesWithAxes<>(prefs, width, height, tMin, tMax, fMin, fMax);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return clazz == AveragedBaseModel.class;
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		channelPanel.validatePanel(errors);
		tagPanel.validatePanel(errors);
		if (tagPanel.getSelectedTagStyles().isEmpty()) {
			errors.addError(_("At least one tag style must be selected"));
		}
	}

}

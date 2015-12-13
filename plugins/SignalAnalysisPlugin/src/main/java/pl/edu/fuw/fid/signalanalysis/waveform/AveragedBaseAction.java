package pl.edu.fuw.fid.signalanalysis.waveform;

import java.awt.Window;
import pl.edu.fuw.fid.signalanalysis.stft.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.method.bookaverage.TimeFrequencyMapPresenter;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import pl.edu.fuw.fid.signalanalysis.AsyncStatus;
import pl.edu.fuw.fid.signalanalysis.SimpleSingleSignal;
import pl.edu.fuw.fid.signalanalysis.SingleSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class AveragedBaseAction<P> extends AbstractSignalMLAction {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AveragedBaseAction.class);

	private final SvarogAccessGUI guiAccess;
	private final SvarogAccessSignal signalAccess;
	private final AveragedBaseDialog dialog;
	private final Class<? extends ImageRenderer<P>> clazz;

	public AveragedBaseAction(SvarogAccessGUI guiAccess, SvarogAccessSignal signalAccess, Class<? extends AveragedBaseDialog> dialogClass, Class<? extends ImageRenderer<P>> rendererClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		super();
		this.guiAccess = guiAccess;
		this.signalAccess = signalAccess;
		this.dialog = dialogClass.getConstructor(Window.class, Boolean.class).newInstance(guiAccess.getDialogParent(), true);
		this.clazz = rendererClass;
		setText("averaged");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			// This method is invoked on Swing thread
			SignalDocument signalDocument = (SignalDocument) signalAccess.getActiveSignalDocument();
			AveragedBaseModel<P> model = new AveragedBaseModel<P>(signalDocument);

			if (dialog.showDialog(model)) {
				final List<SingleSignal> signals = new LinkedList<SingleSignal>();
				final int[] channels = model.selectedChannels;
				final SingleSignal[] samples = new SimpleSingleSignal[channels.length];
				for (int i=0; i<channels.length; ++i) {
					samples[i] = new SimpleSingleSignal(signalAccess.getActiveProcessedSignalSamples(channels[i]));
				}

				Set<String> tagStyleNames = new HashSet<String>();
				for (TagStyleGroup tagStyleGroup : model.selectedTags) {
					tagStyleNames.addAll(tagStyleGroup.getTagStyleNames());
				}

				for (Tag tag : signalDocument.getActiveTag().getTagSet().getTags()) {
					if (tagStyleNames.contains(tag.getStyle().getName())) {
						for (int i=0; i<channels.length; ++i) {
							final int fi = i;
							int channel = channels[i];
							if (tag.getChannel() == ExportedSignalSelection.CHANNEL_NULL || tag.getChannel() == channel) {
								final int offset = (int) Math.round(tag.getPosition() * samples[fi].getSamplingFrequency());
								signals.add(new SingleSignal() {

									@Override
									public void getSamples(int start, int length, double[] buffer) {
										samples[fi].getSamples(start + offset, length, buffer);
									}

									@Override
									public double getSamplingFrequency() {
										return samples[fi].getSamplingFrequency();
									}

								});
							}
						}
					}
				}

				final PreferencesWithAxes<P> preferences = model.preferences;
				final ProgressMonitor progressMonitor = new ProgressMonitor(guiAccess.getDialogParent(), "Computing...", null, 0, signals.size());
				progressMonitor.setMillisToDecideToPopup(100);
				progressMonitor.setMillisToPopup(500);
				final AsyncStatus status = new AsyncStatus() {
					@Override
					public boolean isCancelled() {
						return progressMonitor.isCanceled();
					}
					@Override
					public void setProgress(double progress) {
						// nothing here
					}
				};

				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							final double[][] values = new double[preferences.width][preferences.height];
							int progress = 0;
							for (SingleSignal signal : signals) {
								final ImageRenderer<P> renderer = clazz.getConstructor(SingleSignal.class).newInstance(signal);
								final ImageResult result = renderer.compute(preferences, status);
								if (status.isCancelled()) {
									return;
								}
								for (int ix=0; ix<preferences.width; ++ix) {
									for (int iy=0; iy<preferences.height; ++iy) {
										values[ix][iy] += result.values[ix][iy].abs();
									}
								}
								progressMonitor.setProgress(progress++);
							}
							double max = 0.0;
							for (int ix=0; ix<preferences.width; ++ix) {
								for (int iy=0; iy<preferences.height; ++iy) {
									max = Math.max(max, values[ix][iy]);
								}
							}
							if (max > 0) {
								for (int ix=0; ix<preferences.width; ++ix) {
									for (int iy=0; iy<preferences.height; ++iy) {
										values[ix][iy] /= max;
									}
								}
							}
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									progressMonitor.close();
									TimeFrequencyMapPresenter presenter = new TimeFrequencyMapPresenter(guiAccess.getDialogParent());
									presenter.showResults(values, preferences.yMin, preferences.yMax, preferences.xMax - preferences.xMin);
								}
							});
						} catch (final Exception ex) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									progressMonitor.close();
									JOptionPane.showMessageDialog(guiAccess.getDialogParent(), ex, "Error", JOptionPane.ERROR_MESSAGE);
								}
							});
						}
					}
				}).start();
			}
		} catch (NoActiveObjectException ex) {
			JOptionPane.showMessageDialog(null, "Select valid single-channel signal fragment.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}

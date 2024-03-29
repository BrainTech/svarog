package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.SingularMatrixException;
import org.signalml.app.document.signal.SignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;

/**
 * Manages DTF method computation. Gathers data and presents results.
 *
 * @author ptr@mimuw.edu.pl
 */
public class DtfMethodAction extends AbstractSignalMLAction {

	private static final int SPECTRUM_SIZE = 100;
	public static final String TITLE = _("Directed Transfer Function");

	private final SvarogAccessGUI guiAccess;
	private final SvarogAccessSignal signalAccess;

	public DtfMethodAction(SvarogAccessGUI guiAccess, SvarogAccessSignal signalAccess) {
		super();
		this.guiAccess = guiAccess;
		this.signalAccess = signalAccess;
		this.setText(TITLE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			SignalDocument signalDocument = (SignalDocument) signalAccess.getActiveSignalDocument();
			DtfSettingsPanel settingsPanel = createSettingsPanel(signalDocument);
			int result = settingsPanel.showAsConfirmDialog(guiAccess.getDialogParent());
			if (result == JOptionPane.OK_OPTION) {
				int maxModelOrder = settingsPanel.getMaxModelOrder();
				int[] selectedChannels = settingsPanel.getSelectedChannels();
				if (selectedChannels.length == 0) {
					JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Select at least one channel."), _("Try again"), JOptionPane.WARNING_MESSAGE);
					return;
				}
				proceedToComputation(signalDocument, selectedChannels, maxModelOrder);
			}
		} catch (SingularMatrixException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Cannot compute DTF. Lag autocorrelation matrix is singular."), _("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (MontageMismatchException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Montage mismatch."), _("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (NoActiveObjectException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Choose an active signal first."), _("Error"), JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * 
	 * @param models  list of AR models for order 1, 2, 3, ...
	 * @param N  sample count in data used to compute AR models
	 * @return
	 */
	private XYSeriesWithLegend[] computeCriteria(ArModel[] models, int N) {
		XYSeriesWithLegend serieAIC = new XYSeriesWithLegend(_("AIC"), _("AIC(order) = log(det(V)) + 2 × order × channels² / samples"));
		XYSeriesWithLegend serieHQ = new XYSeriesWithLegend(_("Hannan-Quin"), _("HQ(order) = log(det(V)) + 2 × log(log(samples)) × order × channels² / samples"));
		XYSeriesWithLegend serieSch = new XYSeriesWithLegend(_("Schwartz"), _("SC(order) = log(det(V)) + log(samples) × order × channels² / samples"));
		int order = 1;
		for (ArModel model : models) {
			double det = model.getErrorDeterminant();
			if (det > 0) {
				double logdet = Math.log(det);
				double logN = Math.log(N);
				double k = model.getChannelCount();
				double pk2_N = order*k*k/N;
				serieAIC.add(order, logdet + 2*pk2_N);
				serieHQ.add(order, logdet + 2*Math.log(logN)*pk2_N);
				serieSch.add(order, logdet + logN*pk2_N);
			}
			order++;
		}
		return new XYSeriesWithLegend[] {
			serieAIC,
			serieHQ,
			serieSch
		};
	}

	private DtfSettingsPanel createSettingsPanel(SignalDocument signalDocument) throws NoActiveObjectException {
		SignalView signalView = (SignalView) signalDocument.getDocumentView();
		SignalSpaceConstraints signalSpaceConstraints = signalView.createSignalSpaceConstraints();
		return new DtfSettingsPanel(signalSpaceConstraints);
	}

	private String[] getChannelNames(MultichannelSampleSource sampleSource, int[] selectedChannels) {
		final String[] channels = new String[selectedChannels.length];
		for (int c=0; c<selectedChannels.length; ++c) {
			channels[c] = sampleSource.getLabel(selectedChannels[c]);
		}
		return channels;
	}

	private MultichannelSampleSource getSampleSource(SignalDocument signalDocument) throws MontageMismatchException {
		SignalProcessingChain signalChain = SignalProcessingChain.createFilteredChain(signalDocument.getSampleSource());
		Montage oldMontage = signalDocument.getMontage();
		if (oldMontage != null) {
			signalChain.applyMontageDefinition(oldMontage);
		}
		return signalChain.getOutput();
	}

	private void proceedToComputation(SignalDocument signalDocument, final int[] selectedChannels, int maxOrder) throws MontageMismatchException {
		// extract data samples of selected channels
		final MultichannelSampleSource sampleSource = getSampleSource(signalDocument);
		final int N = sampleSource.getSampleCount(0);
		RealMatrix data = SignalAnalysisTools.extractDataFromSignal(sampleSource, null, selectedChannels);

		// compute AR models for all orders from 1 up to maxOrder
		final ArModel[] models = new ArModel[maxOrder];
		for (int order=1; order<=maxOrder; ++order) {
			models[order-1] = ArModel.compute(data, sampleSource.getSamplingFrequency(), order);
		}

		// compute criteria facilitating order selection
		XYSeriesWithLegend[] criteria = computeCriteria(models, N);

		// make list of channel labels
		final String[] channels = getChannelNames(sampleSource, selectedChannels);

		// create tabbed pane
		Montage montage = new Montage(signalDocument);
		for (int i=0; i<selectedChannels.length; ++i) {
			montage.addMontageChannel(selectedChannels[i]);
		}

		// display dialog with DTF results
		DtfDialog dialog = new DtfDialog(guiAccess.getDialogParent(), criteria, channels, models, SPECTRUM_SIZE, montage);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(false);
		dialog.showDialog(null);
	}

}

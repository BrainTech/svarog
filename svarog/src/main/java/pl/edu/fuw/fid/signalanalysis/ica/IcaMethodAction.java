package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import org.apache.commons.math.linear.RealMatrix;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.signal.SignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.space.ChannelSpaceType;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.impl.PluginAccessClass;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;

/**
 * Action triggered when user decides to compute ICA
 * (Independent Component Analysis).
 *
 * @author ptr@mimuw.edu.pl
 */
public class IcaMethodAction extends AbstractSignalMLAction {

	private static final String TITLE = _("Compute ICA");

	private final SvarogAccessGUI guiAccess;
	private final SvarogAccessSignal signalAccess;

	public IcaMethodAction(SvarogAccessGUI guiAccess, SvarogAccessSignal signalAccess) {
		super();
		this.guiAccess = guiAccess;
		this.signalAccess = signalAccess;
		this.setText(TITLE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			SignalDocument signalDocument = (SignalDocument) signalAccess.getActiveSignalDocument();
			SignalView signalView = (SignalView) signalDocument.getDocumentView();
			SignalSpaceConstraints signalSpaceConstraints = signalView.createSignalSpaceConstraints();

			ExportedSignalSelection selection = null;
			try {
				selection = signalAccess.getActiveSelection();
			} catch (NoActiveObjectException ex) {
				// all right, no problem
			}

			SignalSpace signalSpace = new SignalSpace();
			IcaDialog dialog = new IcaDialog(guiAccess.getDialogParent(), signalSpaceConstraints, selection);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			if (dialog.showDialog(signalSpace)) {

				int[] outputChannels;
				if (signalSpace.getChannelSpaceType() == ChannelSpaceType.WHOLE_SIGNAL) {
					int channelCount = signalSpaceConstraints.getChannels().length;
					outputChannels = new int[channelCount];
					for (int i=0; i<channelCount; ++i) {
						outputChannels[i] = i;
					}
				} else {
					outputChannels = signalSpace.getChannelSpace().getSelectedChannels();
				}

				SignalProcessingChain signalChain = SignalProcessingChain.createFilteredChain(signalDocument.getSampleSource());
				Montage montage = signalDocument.getMontage();
				if (montage != null) {
					signalChain.applyMontageDefinition(montage);
				}

				RealMatrix output = SignalAnalysisTools.extractDataFromSignal(signalChain.getOutput(), signalSpace.getSelectionTimeSpace(), outputChannels);

				IcaMethodComputer computer = new IcaMethodComputer();

				// transfer matrix from transformed signal to ICA components
				RealMatrix icaFromOutput = computer.compute(output);

				// ICA component data
				RealMatrix ica = icaFromOutput.multiply(output);

				// transfer matrix from raw signal to ICA components
				if (montage == null) {
					montage = new Montage(new SourceMontage(signalDocument));
				} else {
					montage = montage.clone();
				}
				RealMatrix outputFromRaw = SignalAnalysisTools.extractMatrixFromMontage(montage, outputChannels);

				// prepare montage with only selected signals
				for (int i=montage.getMontageChannelCount()-1; i>=0; --i) {
					if (!isIndexInArray(i, outputChannels)) {
						montage.removeMontageChannel(i);
					}
				}

				File newFile = SignalAnalysisTools.createRawTemporaryFileFromData(signalAccess, ica);

				// open generated data in a new tab
				int channelCount = ica.getRowDimension();
				RawSignalDescriptor newDescriptor = new RawSignalDescriptor();
				newDescriptor.setBlocksPerPage(signalDocument.getBlocksPerPage());
				newDescriptor.setByteOrder(RawSignalByteOrder.BIG_ENDIAN);
				newDescriptor.setChannelLabels(SignalAnalysisTools.generateIcaComponentNames(channelCount));
				newDescriptor.setChannelCount(channelCount);
				newDescriptor.setPageSize(signalDocument.getPageSize());
				newDescriptor.setSampleCount(ica.getColumnDimension());
				newDescriptor.setSampleType(RawSignalSampleType.DOUBLE);
				newDescriptor.setSamplingFrequency(signalDocument.getSamplingFrequency());
				newDescriptor.setSourceFileName(newFile.getName());

				newDescriptor.getSignalParameters().setCalibrationGain(new float[channelCount]);
				newDescriptor.getSignalParameters().setCalibrationOffset(new float[channelCount]);
				newDescriptor.setCalibrationGain(1.0f);

				IcaSignalDocument newDocument = new IcaSignalDocument(newDescriptor, icaFromOutput, outputFromRaw, montage);
				newDocument.setBackingFile(newFile);
				newDocument.setBlocksPerPage(newDescriptor.getBlocksPerPage());
				newDocument.setPageSize(newDescriptor.getPageSize());
				newDocument.openDocument();

				DocumentFlowIntegrator dfi = PluginAccessClass.getManager().getDocumentFlowIntegrator();
				dfi.getDocumentManager().addDocument(newDocument);
				dfi.getActionFocusManager().setActiveDocument(newDocument);
			}
		} catch (IcaMethodException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("ICA method failed: ")+ex.getMessage(), _("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (MontageMismatchException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Montage mismatch."), _("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (NoActiveObjectException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Choose an active signal first."), _("Error"), JOptionPane.WARNING_MESSAGE);
		} catch (SignalMLException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Error: ")+ex, _("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Error: ")+ex, _("Error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private static boolean isIndexInArray(int index, int[] array) {
		for (int i : array) {
			if (i == index) {
				return true;
			}
		}
		return false;
	}
}

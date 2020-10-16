package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.signal.RawSignalDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.signalselection.ChannelSpacePanel;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.impl.PluginAccessClass;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;

/**
 * Action triggered whenever user chooses to zero some ICA components
 * and revert to the original channel space. It can be only performed
 * on a IcaSignalDocument.
 *
 * @author ptr@mimuw.edu.pl
 */
public class ZeroMethodAction extends AbstractSignalMLAction {

	private static final String TITLE = _("Zero selected components");

	private final SvarogAccessGUI guiAccess;
	private final SvarogAccessSignal signalAccess;

	public ZeroMethodAction(SvarogAccessGUI guiAccess, SvarogAccessSignal signalAccess) {
		super();
		this.guiAccess = guiAccess;
		this.signalAccess = signalAccess;
		this.setText(TITLE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Document document = signalAccess.getActiveDocument();
			if (!(document instanceof IcaSignalDocument)) {
				throw new NoActiveObjectException();
			}
			IcaSignalDocument icaDocument = (IcaSignalDocument) document;
			RealMatrix icaFromOutput = icaDocument.getIcaMatrix(false);

			SignalView signalView = (SignalView) icaDocument.getDocumentView();
			SignalSpaceConstraints signalSpaceConstraints = signalView.createSignalSpaceConstraints();

			ChannelSpacePanel panel = new ChannelSpacePanel();
			panel.setConstraints(signalSpaceConstraints);

			int result = JOptionPane.showConfirmDialog(guiAccess.getDialogParent(), panel, TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {

				int[] channelsToZero = panel.getChannelList().getSelectedIndices();
				RealMatrix D = MatrixUtils.createRealIdentityMatrix(icaFromOutput.getRowDimension());
				for (int c : channelsToZero) {
					D.setEntry(c, c, 0.0);
				}

				RealMatrix ica = SignalAnalysisTools.extractDataFromSignal(icaDocument.getSampleSource(), null, null);
				RealMatrix outputFromIca = new LUDecompositionImpl(icaFromOutput).getSolver().getInverse();
				RealMatrix output = outputFromIca.multiply(D).multiply(ica);

				File newFile = SignalAnalysisTools.createRawTemporaryFileFromData(signalAccess, output);

				// open generated data in a new tab
				int channelCount = ica.getRowDimension();
				String[] channelLabels = new String[channelCount];
				for (int index=0; index<channelCount; ++index) {
					channelLabels[index] = icaDocument.getSourceMontage().getMontageChannelLabelAt(index);
				}
				RawSignalDescriptor newDescriptor = new RawSignalDescriptor();
				newDescriptor.setBlocksPerPage(icaDocument.getBlocksPerPage());
				newDescriptor.setByteOrder(RawSignalByteOrder.BIG_ENDIAN);
				newDescriptor.setChannelCount(channelCount);
				newDescriptor.setChannelLabels(channelLabels);
				newDescriptor.setPageSize(icaDocument.getPageSize());
				newDescriptor.setSampleCount(ica.getColumnDimension());
				newDescriptor.setSampleType(RawSignalSampleType.DOUBLE);
				newDescriptor.setSamplingFrequency(icaDocument.getSamplingFrequency());
				newDescriptor.setSourceFileName(newFile.getName());

				newDescriptor.getSignalParameters().setCalibrationGain(new float[channelCount]);
				newDescriptor.getSignalParameters().setCalibrationOffset(new float[channelCount]);
				newDescriptor.setCalibrationGain(1.0f);

				RawSignalDocument newDocument = new RawSignalDocument(newDescriptor);
				newDocument.setBackingFile(newFile);
				newDocument.setBlocksPerPage(newDescriptor.getBlocksPerPage());
				newDocument.setPageSize(newDescriptor.getPageSize());
				newDocument.openDocument();

				DocumentFlowIntegrator dfi = PluginAccessClass.getManager().getDocumentFlowIntegrator();
				dfi.getDocumentManager().addDocument(newDocument);
				dfi.getActionFocusManager().setActiveDocument(newDocument);
			}
		} catch (MontageException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Montage error."), _("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (MontageMismatchException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Montage mismatch."), _("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (NoActiveObjectException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Choose a signal with an existing ICA analysis."), _("Error"), JOptionPane.WARNING_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Error: ")+ex, _("Error"), JOptionPane.ERROR_MESSAGE);
		} catch (SignalMLException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), _("Error: ")+ex, _("Error"), JOptionPane.ERROR_MESSAGE);
		}
	}
}

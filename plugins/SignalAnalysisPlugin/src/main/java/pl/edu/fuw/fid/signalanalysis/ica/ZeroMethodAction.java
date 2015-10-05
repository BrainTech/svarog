package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.signalselection.ChannelSpacePanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;

/**
 * @author ptr@mimuw.edu.pl
 */
public class ZeroMethodAction extends AbstractSignalMLAction {

	private static final String TITLE = "Zero selected components";

	public static IcaComputationTrace trace;

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
			if (trace == null || trace.document != signalAccess.getActiveDocument()) {
				throw new NoActiveObjectException("ICA has to be computed first");
			}
			SignalDocument signalDocument = trace.document;
			SignalView signalView = (SignalView) signalDocument.getDocumentView();
			SignalSpaceConstraints signalSpaceConstraints = signalView.createSignalSpaceConstraints();

			RealMatrix A = trace.icaMatrix;
			RealMatrix M = SignalAnalysisTools.extractMatrixFromMontage(trace.montage, trace.selectedChannels);
			int inputChannelCount = M.getColumnDimension();
			int outputChannelCount = M.getRowDimension();
			Montage icaMontage = signalDocument.getMontage();
			if (icaMontage == null || A.getRowDimension() != outputChannelCount || A.getColumnDimension() != outputChannelCount || signalDocument.getChannelCount() != inputChannelCount) {
				throw new MontageMismatchException();
			}

			ChannelSpacePanel panel = new ChannelSpacePanel();
			panel.setConstraints(signalSpaceConstraints);

			int result = JOptionPane.showConfirmDialog(guiAccess.getDialogParent(), panel, TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {

				int[] channelsToZero = panel.getChannelList().getSelectedIndices();

				Montage newMontage;
				if (trace.montage == null) {
					newMontage = new Montage(new SourceMontage(trace.document));
				} else {
					newMontage = trace.montage.clone();
				}

				RealMatrix D = MatrixUtils.createRealIdentityMatrix(outputChannelCount);
				for (int c : channelsToZero) {
					D.setEntry(c, c, 0.0);
				}

				RealMatrix invA = new LUDecompositionImpl(A).getSolver().getInverse();
				RealMatrix newM = invA.multiply(D).multiply(A).multiply(M);

				double[][] components = newM.getData();
				for (int c=0; c<components.length; ++c) {
					double[] weights = components[c];
					int ch = trace.selectedChannels[c];
					int mainChannel = trace.montage.getMontagePrimaryChannelAt(ch);

					double mainWeight = weights[mainChannel];
					if (mainWeight == 0) {
						throw new MontageException("zeroing channels failed");
					}
					for (int i=0; i<weights.length; ++i) {
						if (i != mainChannel) {
							double weight = weights[i] / mainWeight;
							if (Math.abs(weight) > SignalAnalysisTools.THRESHOLD) {
								newMontage.setReference(ch, i, Double.toString(weight));
							} else {
								newMontage.setReference(ch, i, "");
							}
						}
					}
				}
				signalDocument.setMontage(newMontage);
			}
		} catch (MontageException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Montage error.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (MontageMismatchException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Montage mismatch.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (NoActiveObjectException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Choose a signal with an existing ICA analysis.", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}
}

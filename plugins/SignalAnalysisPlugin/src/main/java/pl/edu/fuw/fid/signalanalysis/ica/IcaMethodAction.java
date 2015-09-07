package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.signalselection.ChannelSpacePanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;
import pl.edu.fuw.fid.signalanalysis.SimpleSignal;
import pl.edu.fuw.fid.signalanalysis.zero.ZeroMethodAction;

/**
 * @author ptr@mimuw.edu.pl
 */
public class IcaMethodAction extends AbstractSignalMLAction {

	private static final String TITLE = "Compute ICA";

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

			ChannelSpacePanel panel = new ChannelSpacePanel();
			panel.setConstraints(signalSpaceConstraints);
			int result = JOptionPane.showConfirmDialog(guiAccess.getDialogParent(), panel, TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {

				int[] outputChannels = panel.getChannelList().getSelectedIndices();

				SignalProcessingChain signalChain = SignalProcessingChain.createFilteredChain(signalDocument.getSampleSource());
				Montage oldMontage = signalDocument.getMontage();
				Montage newMontage;
				if (oldMontage == null) {
					newMontage = new Montage(new SourceMontage(signalDocument));
				} else {
					signalChain.applyMontageDefinition(oldMontage);
					newMontage = oldMontage.clone();
					while (newMontage.getMontageChannelCount() > 0) {
						newMontage.removeMontageChannel(0);
					}
				}

				int inputChannelCount = signalDocument.getChannelCount();
				int outputChannelCount = outputChannels.length;
				SimpleSignal[] data = new SimpleSignal[outputChannelCount];
				for (int i=0; i<outputChannelCount; ++i) {
					data[i] = new StoredSignal(signalChain.getOutput(), outputChannels[i]);
				}

				IcaMethodComputer computer = new IcaMethodComputer();
				RealMatrix ica = new Array2DRowRealMatrix(computer.compute(data));

				RealMatrix M;
				if (oldMontage == null) {
					M = new Array2DRowRealMatrix(outputChannelCount, inputChannelCount);
					for (int i=0; i<outputChannelCount; ++i) {
						M.setEntry(i, outputChannels[i], 1.0);
					}
				} else {
					M = SignalAnalysisTools.extractMatrixFromMontage(oldMontage, outputChannels);
				}
				double[][] components = ica.multiply(M).getData();

				for (double[] weights : components) {
					int mainChannel = 0;
					double mainWeight = weights[0];
					for (int c=1; c<weights.length; ++c) {
						if (Math.abs(weights[c]) > Math.abs(mainWeight)) {
							mainChannel = c;
							mainWeight = weights[c];
						}
					}
					if (mainWeight != 0) {
						int index = newMontage.addMontageChannel(mainChannel);
						newMontage.setMontageChannelLabelAt(index, "ICA-"+(index+1));
						for (int c=0; c<weights.length; ++c) {
							if (c != mainChannel) {
								double weight = weights[c] / mainWeight;
								if (Math.abs(weight) > SignalAnalysisTools.THRESHOLD) {
									newMontage.setReference(index, c, Double.toString(weight));
								}
							}
						}
					}
				}
				if (newMontage.getMontageChannelCount() > 0) {
					ZeroMethodAction.trace = new IcaComputationTrace(signalDocument, ica, oldMontage, outputChannels);
					signalDocument.setMontage(newMontage);
				} else {
					JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "No valid components found.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (IcaMethodException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "ICA method failed: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (MontageException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Montage error.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (MontageMismatchException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Montage mismatch.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (NoActiveObjectException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Choose an active signal first.", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}

}

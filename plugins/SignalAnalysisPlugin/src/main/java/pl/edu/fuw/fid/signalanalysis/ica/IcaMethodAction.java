package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.commons.math.linear.RealMatrix;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.signalselection.ChannelSpacePanel;
import org.signalml.app.view.signal.signalselection.TimeSpacePanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.impl.PluginAccessClass;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;

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

			ExportedSignalSelection selection = null;
			try {
				selection = signalAccess.getActiveSelection();
			} catch (NoActiveObjectException ex) {
				// all right, no problem
			}

			ChannelSpacePanel channelPanel = new ChannelSpacePanel();
			channelPanel.setConstraints(signalSpaceConstraints);

			TimeSpacePanel timePanel = new TimeSpacePanel();
			timePanel.setConstraints(signalSpaceConstraints);

			SignalSpace signalSpace = new SignalSpace();
			if (selection != null) {
				signalSpace.configureFromSelections(new SignalSelection(selection), null);
			}
			timePanel.fillPanelFromModel(signalSpace);

			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(channelPanel);
			panel.add(timePanel);

			int result = JOptionPane.showConfirmDialog(guiAccess.getDialogParent(), panel, TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {

				int[] outputChannels = channelPanel.getChannelList().getSelectedIndices();
				if (outputChannels.length == 0) {
					JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Select at least one channel.", "Try again", JOptionPane.WARNING_MESSAGE);
					return;
				}

				SignalProcessingChain signalChain = SignalProcessingChain.createFilteredChain(signalDocument.getSampleSource());
				Montage montage = signalDocument.getMontage();
				if (montage != null) {
					signalChain.applyMontageDefinition(montage);
				}

				timePanel.fillModelFromPanel(signalSpace);
				RealMatrix output = SignalAnalysisTools.extractDataFromSignal(signalChain.getOutput(), signalSpace.getSelectionTimeSpace(), outputChannels);

				IcaMethodComputer computer = new IcaMethodComputer();

				// transfer matrix from transformed signal to ICA components
				RealMatrix icaFromOutput = computer.compute(output);

				// ICA component data
				RealMatrix ica = icaFromOutput.multiply(output);

				// transfer matrix from raw signal to ICA components
				if (montage == null) {
					montage = new Montage(new SourceMontage(signalDocument));
				}
				RealMatrix outputFromRaw = SignalAnalysisTools.extractMatrixFromMontage(montage, outputChannels);

				File newFile = SignalAnalysisTools.createRawTemporaryFileFromData(signalAccess, ica);

				// open generated data in a new tab
				RawSignalDescriptor newDescriptor = new RawSignalDescriptor();
				newDescriptor.setBlocksPerPage(signalDocument.getBlocksPerPage());
				newDescriptor.setByteOrder(RawSignalByteOrder.BIG_ENDIAN);
				newDescriptor.setChannelLabels(SignalAnalysisTools.generateIcaComponentNames(ica.getRowDimension()));
				newDescriptor.setChannelCount(ica.getRowDimension());
				newDescriptor.setSampleCount(ica.getColumnDimension());
				newDescriptor.setSampleType(RawSignalSampleType.DOUBLE);
				newDescriptor.setSamplingFrequency(signalDocument.getSamplingFrequency());
				newDescriptor.setSourceFileName(newFile.getName());

				IcaSignalDocument newDocument = new IcaSignalDocument(newDescriptor, icaFromOutput, outputFromRaw, montage);
				newDocument.setBackingFile(newFile);
				newDocument.openDocument();

				DocumentFlowIntegrator dfi = PluginAccessClass.getManager().getDocumentFlowIntegrator();
				dfi.getDocumentManager().addDocument(newDocument);
				dfi.getActionFocusManager().setActiveDocument(newDocument);
			}
		} catch (IcaMethodException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "ICA method failed: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (MontageMismatchException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Montage mismatch.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (NoActiveObjectException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Choose an active signal first.", "Error", JOptionPane.WARNING_MESSAGE);
		} catch (SignalMLException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Error: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Error: "+ex, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}

/* EditSignalParametersAction.java created 2007-09-28
 *
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.SignalDocumentFocusSelector;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.app.view.dialog.SignalParametersDialog;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.signal.OriginalMultichannelSampleSource;
import org.springframework.context.support.MessageSourceAccessor;

/** EditSignalParametersAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditSignalParametersAction extends AbstractFocusableSignalMLAction<SignalDocumentFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditSignalParametersAction.class);

	private SignalParametersDialog signalParametersDialog;

	public EditSignalParametersAction(MessageSourceAccessor messageSource, SignalDocumentFocusSelector signalDocumentFocusSelector) {
		super(messageSource, signalDocumentFocusSelector);
		setText("action.signalParameters");
		setIconPath("org/signalml/app/icon/signalparameters.png");
		setToolTip("action.signalParametersToolTip");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		SignalDocument signalDocument = getActionFocusSelector().getActiveSignalDocument();
		if (signalDocument == null) {
			logger.warn("Target document doesn't exist or is not a signal");
			return;
		}

		SignalView signalView = (SignalView) signalDocument.getDocumentView();

		SignalParameterDescriptor spd = new SignalParameterDescriptor();

		spd.setPageSize(signalDocument.getPageSize());
		spd.setBlocksPerPage(signalDocument.getBlocksPerPage());

		if (signalDocument.getTagDocuments().isEmpty()) {
			spd.setPageSizeEditable(true);
			spd.setBlocksPerPageEditable(true);
		} else {
			spd.setPageSizeEditable(false);
			spd.setBlocksPerPageEditable(false);
		}

		OriginalMultichannelSampleSource mss = signalDocument.getSampleSource();

		spd.setSamplingFrequency(mss.getSamplingFrequency());
		if (mss.isSamplingFrequencyCapable()) {
			spd.setSamplingFrequency(mss.getSamplingFrequency());
			spd.setSamplingFrequencyEditable(false);
		} else {
			spd.setSamplingFrequency(null);
			spd.setSamplingFrequencyEditable(true);
		}

		spd.setChannelCount(mss.getChannelCount());
		if (mss.isChannelCountCapable()) {
			spd.setChannelCount(mss.getChannelCount());
			spd.setChannelCountEditable(false);
		} else {
			spd.setChannelCount(null);
			spd.setChannelCountEditable(true);
		}

		if (mss.isCalibrationCapable()) {
			spd.setCalibrationGain(mss.getSingleCalibrationGain());
			spd.setCalibrationOffset(mss.getSingleCalibrationOffset());
			spd.setCalibrationEditable(true);
		} else {
			spd.setCalibrationGain(null);
			spd.setCalibrationOffset(null);
			spd.setCalibrationEditable(false);
		}

		boolean ok = signalParametersDialog.showDialog(spd,true);
		if (!ok) {
			return;
		}

		if (signalDocument.getTagDocuments().isEmpty()) {
			signalDocument.setPageSize(spd.getPageSize());
			signalDocument.setBlocksPerPage(spd.getBlocksPerPage());
		}

		if (mss.isSamplingFrequencyCapable()) {
			mss.setSamplingFrequency(spd.getSamplingFrequency());
		}
		if (mss.isChannelCountCapable() && spd.isChannelCountEditable()) {
			mss.setChannelCount(spd.getChannelCount());
		}

		if (mss.isCalibrationCapable()) {
			mss.setCalibrationGain(spd.getCalibrationGain());
			mss.setCalibrationOffset(spd.getCalibrationOffset());
		}

		signalView.clearSignalSelection();
		LinkedList<SignalPlot> plots = signalView.getPlots();
		for (SignalPlot plot : plots) {
			if (!plot.isVisible()) {
				plot.setVisible(true);
			}
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveSignalDocument() != null);
	}

	public SignalParametersDialog getSignalParametersDialog() {
		return signalParametersDialog;
	}

	public void setSignalParametersDialog(SignalParametersDialog signalParametersDialog) {
		this.signalParametersDialog = signalParametersDialog;
	}

}

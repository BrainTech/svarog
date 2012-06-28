/* WorkspaceSignal.java created 2007-12-15
 *
 */

package org.signalml.app.config.workspace;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import multiplexer.jmx.client.ConnectException;

import org.apache.log4j.Logger;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.MRUDEntry;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.RawSignalDocument;
import org.signalml.app.document.RawSignalMRUDEntry;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.SignalMLDocument;
import org.signalml.app.document.SignalMLMRUDEntry;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.document.opensignal.SignalMLDescriptor;
import org.signalml.app.util.SnapToPageRunnable;
import org.signalml.app.view.common.components.LockableJSplitPane;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.codec.SignalMLCodec;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.signalml.app.config.ZoomSignalSettings;
import org.signalml.app.config.preset.managers.EegSystemsPresetManager;
import org.signalml.domain.montage.system.EegSystem;

/** WorkspaceSignal
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("workspacesignal")
public class WorkspaceSignal extends WorkspaceDocument {

	protected static final Logger logger = Logger.getLogger(WorkspaceSignal.class);

	private double timeZoomFactor;
	private double voltageZoomFactor;
	private int pixelPerChannel;

	private Montage montage;

	private LinkedList<WorkspaceTag> tags = new LinkedList<WorkspaceTag>();
	private WorkspaceTag activeTag;

	private WorkspaceTag[] comparedTags;

	private WorkspaceSignalPlot masterPlot;

	private LinkedList<WorkspaceSignalPlot> clones = new LinkedList<WorkspaceSignalPlot>();
	private WorkspaceSignalPlot activePlot;

	private int splitPaneLocation;
	private boolean snapToPageMode;

	private ZoomSignalSettings zoomSignalSettings = new ZoomSignalSettings();

	protected WorkspaceSignal() {
		super();
	}

	public WorkspaceSignal(SignalDocument document) {

		if (document instanceof SignalMLDocument) {

			SignalMLDocument signalMLDocument = (SignalMLDocument) document;

			SignalMLDescriptor descriptor = new SignalMLDescriptor(signalMLDocument);
			SignalMLMRUDEntry mrud = new SignalMLMRUDEntry(ManagedDocumentType.SIGNAL, signalMLDocument.getClass(), ((FileBackedDocument) document).getBackingFile().getAbsolutePath(), descriptor);
			mrud.setLastTimeOpened(new Date());

			mrudEntry = mrud;

		}
		else if (document instanceof RawSignalDocument) {

			RawSignalDocument rawSignalDocument = (RawSignalDocument) document;

			RawSignalMRUDEntry mrud = new RawSignalMRUDEntry(ManagedDocumentType.SIGNAL, rawSignalDocument.getClass(), ((FileBackedDocument) document).getBackingFile().getAbsolutePath(), rawSignalDocument.getDescriptor());
			mrud.setLastTimeOpened(new Date());

			mrudEntry = mrud;

		} else {
			File backingFile = ((FileBackedDocument) document).getBackingFile();
			if (backingFile != null)
				mrudEntry = new MRUDEntry(ManagedDocumentType.SIGNAL, document.getClass(), backingFile.getAbsolutePath());

		}

		SignalView view = (SignalView) document.getDocumentView();
		SignalPlot masterSignalPlot = view.getMasterPlot();

		zoomSignalSettings = view.getZoomSignalTool().getSettings();

		montage = document.getMontage();

		timeZoomFactor = masterSignalPlot.getTimeZoomFactor();
		voltageZoomFactor = masterSignalPlot.getVoltageZoomFactor();
		pixelPerChannel = masterSignalPlot.getPixelPerChannel();

		List<TagDocument> tagDocuments = document.getTagDocuments();

		TagDocument[] comparedSignalTags = null;
		if (view.isComparingTags()) {
			comparedSignalTags = view.getComparedTags();
			comparedTags = new WorkspaceTag[2];
		}

		for (TagDocument tagDocument : tagDocuments) {

			File file = tagDocument.getBackingFile();
			if (file == null) {
				// no file, cannot save with workspace
				continue;
			}

			WorkspaceTag tag = new WorkspaceTag(tagDocument);

			tags.add(tag);
			if (tagDocument == document.getActiveTag()) {
				activeTag = tag;
			}
			if (comparedSignalTags != null) {
				if (tagDocument == comparedSignalTags[0]) {
					comparedTags[0] = tag;
				}
				else if (tagDocument == comparedSignalTags[1]) {
					comparedTags[1] = tag;
				}
			}

		}

		masterPlot = new WorkspaceSignalPlot(masterSignalPlot);
		SignalPlot activeSignalPlot = view.getActiveSignalPlot();
		if (activeSignalPlot == masterSignalPlot) {
			activePlot = masterPlot;
		}

		LinkedList<SignalPlot> plots = view.getPlots();
		if (plots.size() > 1) {
			Iterator<SignalPlot> it = plots.iterator();
			SignalPlot slaveSignalPlot;
			WorkspaceSignalPlot slavePlot;
			it.next(); // skip the master
			while (it.hasNext()) {
				slaveSignalPlot = it.next();
				slavePlot = new WorkspaceSignalPlot(slaveSignalPlot);
				clones.add(slavePlot);
				if (activeSignalPlot == slaveSignalPlot) {
					activePlot = slavePlot;
				}
			}
		}

		LockableJSplitPane plotSplitPane = view.getPlotSplitPane();
		splitPaneLocation = plotSplitPane.getDividerLocation();

		snapToPageMode = view.isSnapToPageMode();

	}

	public void configureSignal(SignalDocument document, DocumentFlowIntegrator integrator, EegSystemsPresetManager eegSystemsPresetManager) throws IOException, SignalMLException, ConnectException {

		SignalView view = (SignalView) document.getDocumentView();
		SignalPlot masterSignalPlot = view.getMasterPlot();

		if (zoomSignalSettings != null) {
			view.getZoomSignalTool().setSettings(zoomSignalSettings);
		}

		if (eegSystemsPresetManager != null) {
			String eegSystemName = montage.getEegSystemFullName();
			EegSystem eegSystem = (EegSystem) eegSystemsPresetManager.getPresetByName(eegSystemName);
			montage.setEegSystem(eegSystem);
		}

		if (montage != null) {
			document.setMontage(montage);
		}

		masterSignalPlot.setTimeZoomFactor(timeZoomFactor);
		masterSignalPlot.setVoltageZoomFactor(voltageZoomFactor);
		masterSignalPlot.setPixelPerChannel(pixelPerChannel);

		SignalPlot activeSignalPlot = null;

		if (masterPlot != null) {
			if (activePlot == masterPlot) {
				activeSignalPlot = masterSignalPlot;
			}
			masterPlot.configurePlot(masterSignalPlot);
		}

		if (!clones.isEmpty()) {

			boolean splitPaneConfigured = false;
			SignalPlot slaveSignalPlot;
			for (WorkspaceSignalPlot slavePlot : clones) {

				slaveSignalPlot = view.addSlavePlot(masterSignalPlot);
				if (!splitPaneConfigured) {
					view.getPlotSplitPane().setDividerLocation(splitPaneLocation);
				}
				if (activePlot == slavePlot) {
					activeSignalPlot = slaveSignalPlot;
				}
				slavePlot.configurePlot(slaveSignalPlot);

			}

		}

		if (activeSignalPlot != null) {
			view.setActivePlot(activeSignalPlot);
		}

		if (!tags.isEmpty()) {

			integrator.getActionFocusManager().setActiveDocument(document);
			TagDocument activeTagDocument = null;
			TagDocument[] comparedTagDocuments = null;

			for (WorkspaceTag tag : tags) {

				Document tagDocument = integrator.openMRUDEntry(tag.getMrudEntry());
				if (tagDocument == null || !(tagDocument instanceof TagDocument)) {
					logger.warn("WARNING: not a tag");
					return;
				}
				tag.configureTag((TagDocument) tagDocument);

				if (tag == activeTag) {
					activeTagDocument = (TagDocument) tagDocument;
				}
				if (comparedTags != null) {
					if (comparedTagDocuments == null) {
						comparedTagDocuments = new TagDocument[2];
					}
					if (tag == comparedTags[0]) {
						comparedTagDocuments[0] = (TagDocument) tagDocument;
					}
					else if (tag == comparedTags[1]) {
						comparedTagDocuments[1] = (TagDocument) tagDocument;
					}
				}

			}

			if (activeTagDocument != null) {
				document.setActiveTag(activeTagDocument);
			}

			if (comparedTagDocuments != null) {
				view.setComparedTags(comparedTagDocuments[0], comparedTagDocuments[1]);
			}

		}

		// XXX this must be re-queued onto the event thread so that Swing has a chance to
		// compose itself after the new view was created
		// otherwise viewports will have no extent size and snap will fail
		// change if you know a better solution
		SwingUtilities.invokeLater(new SnapToPageRunnable(view, snapToPageMode));

	}

}

/* ActionFocusManager.java created 2007-10-15
 *
 */

package org.signalml.app.action.selector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.config.preset.PresetManagerAdapter;
import org.signalml.app.config.preset.PresetManagerEvent;
import org.signalml.app.config.preset.PresetManagerListener;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.Document;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.montage.MontagePresetManager;
import org.signalml.app.view.ViewerDocumentTabbedPane;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.tag.TagStyle;

/** ActionFocusManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ActionFocusManager implements ChangeListener, DocumentFocusSelector, TagFocusSelector, TagStyleFocusSelector, SignalPlotFocusSelector, MontageFocusSelector, BookDocumentFocusSelector, PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(ActionFocusManager.class);

	private ActionFocusSupport afSupport = new ActionFocusSupport(this);

	private Document activeDocument = null;

	private MontagePresetManager montagePresetManager;
	private PresetManagerListener presetManagerListener;

	public MontagePresetManager getMontagePresetManager() {
		return montagePresetManager;
	}

	public void setMontagePresetManager(MontagePresetManager montagePresetManager) {
		if (this.montagePresetManager != montagePresetManager) {
			if (this.montagePresetManager != null) {
				this.montagePresetManager.removePresetManagerListener(presetManagerListener);
			}
			this.montagePresetManager = montagePresetManager;
			if (montagePresetManager != null) {
				if (presetManagerListener == null) {
					presetManagerListener = new PresetManagerAdapter() {
						@Override
						public void defaultPresetChanged(PresetManagerEvent ev) {
							afSupport.fireActionFocusChanged();
						}
					};
				}
				montagePresetManager.addPresetManagerListener(presetManagerListener);
			}
		}
	}

	@Override
	public Document getActiveDocument() {
		return activeDocument;
	}

	public void setActiveDocument(Document document) {
		if (document != activeDocument) {
			if (activeDocument != null) {
				activeDocument.removePropertyChangeListener(this);
			}
			activeDocument = document;
			if (document != null) {
				document.addPropertyChangeListener(this);
			}
			afSupport.fireActionFocusChanged();
		}
	}

	@Override
	public PositionedTag getActiveTag() {
		logger.warn("WARNING: active tag not updated");
		/*
		if( activeDocument instanceof SignalDocument ) {
			SignalView signalView = (SignalView) ( activeDocument.getDocumentView() );
			return signalView.getTagSelection();
		}
		*/
		return null;
	}

	@Override
	public TagStyle getActiveTagStyle() {
		logger.warn("WARNING: active tag style not updated");
		return null;
	}

	@Override
	public TagDocument getActiveTagDocument() {
		if (activeDocument instanceof SignalDocument) {
			return ((SignalDocument) activeDocument).getActiveTag();
		}
		return null;
	}

	@Override
	public SignalDocument getActiveSignalDocument() {
		if (activeDocument instanceof SignalDocument) {
			return (SignalDocument) activeDocument;
		}
		return null;
	}



	@Override
	public BookDocument getActiveBookDocument() {
		if (activeDocument instanceof BookDocument) {
			return (BookDocument) activeDocument;
		}
		return null;
	}

	@Override
	public SignalPlot getActiveSignalPlot() {
		if (activeDocument instanceof SignalDocument) {
			SignalView signalView = (SignalView)(activeDocument.getDocumentView());
			return signalView.getActiveSignalPlot();
		}
		return null;
	}

	@Override
	public Montage getActiveMontage() {
		if (montagePresetManager == null) {
			return null;
		}
		return (Montage) montagePresetManager.getDefaultPreset();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object src = e.getSource();
		if (src instanceof ViewerDocumentTabbedPane) {
			ViewerDocumentTabbedPane documentTabbedPane = (ViewerDocumentTabbedPane) src;
			int index = documentTabbedPane.getSelectedIndex();
			if (index >= 0) {
				setActiveDocument(documentTabbedPane.getDocumentInTab(index));
			} else {
				setActiveDocument(null);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == activeDocument) {
			if (activeDocument instanceof SignalDocument) {
				if (SignalDocument.ACTIVE_TAG_PROPERTY.equals(evt.getPropertyName())) {
					afSupport.fireActionFocusChanged();
				}
			}
		}
	}

	@Override
	public void addActionFocusListener(ActionFocusListener listener) {
		afSupport.addActionFocusListener(listener);
	}

	@Override
	public void removeActionFocusListener(ActionFocusListener listener) {
		afSupport.removeActionFocusListener(listener);
	}



}

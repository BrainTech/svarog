package org.signalml.app.method.ep.view.tags;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.method.ep.EvokedPotentialParameters;

/**
 * This a panel for selecting which tag styles represent artifacts.
 *
 * @author Piotr Szachewicz
 */
public class ArtifactTagsSelectionPanel extends TagSelectionPanel {

	public ArtifactTagsSelectionPanel() {
		super(_("Artifact tags"));

		getTable().setCellSelectionEnabled(false);
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setArtifactTagStyles(getSelectedTagStyles());
	}

}

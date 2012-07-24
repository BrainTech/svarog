package org.signalml.app.method.ep.view.tags;

import static org.signalml.app.util.i18n.SvarogI18n._;

public class ArtifactTagsSelectionPanel extends TagSelectionPanel {

	public ArtifactTagsSelectionPanel() {
		super(_("Artifact tags"));

		getTable().setCellSelectionEnabled(false);
	}

}

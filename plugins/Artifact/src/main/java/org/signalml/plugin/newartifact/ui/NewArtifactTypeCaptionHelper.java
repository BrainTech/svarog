package org.signalml.plugin.newartifact.ui;

import static org.signalml.plugin.i18n.PluginI18n._;

import org.signalml.plugin.newartifact.data.NewArtifactType;

public class NewArtifactTypeCaptionHelper {

	public static String GetCaption(NewArtifactType artifactType) {
		switch (artifactType) {
		case BREATHING:
			return _("Breathing");
		case ECG:
			return _("ECG");
		case EYE_MOVEMENT:
			return _("Eye movement");
		case MUSCLE_ACTIVITY:
			return _("Muscle activity");
		case EYEBLINKS:
			return _("Eyeblinks");
		case TECHNICAL:
			return _("Technical");
		case POWER_SUPPLY:
			return _("Power supply");
		case UNKNOWN:
			return _("Unknown");
		default:
			return ""; //can't happen
		}
	}

}

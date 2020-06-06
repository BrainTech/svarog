package org.signalml.app.view.tag.synchronize;

import org.signalml.app.model.tag.SynchronizeTagsWithTriggerParameters;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.panels.PanelWithEnablingCheckbox;

public class LengthThresholdPanel extends PanelWithEnablingCheckbox<LengthThresholdValuePanel>{

	private static final long serialVersionUID = -923590891624359993L;

	private LengthThresholdValuePanel lengthThresholdValuePanel;

	public LengthThresholdPanel() {
		super(_("Length threshold"));
	}

	@Override
	protected String getEnableCheckboxText() {
		return _("Enable length threshold");
	}

	@Override
	protected LengthThresholdValuePanel getPanel() {
		if (lengthThresholdValuePanel == null)
			lengthThresholdValuePanel = new LengthThresholdValuePanel();
		return lengthThresholdValuePanel;
	}

	public void fillPanelFromModel(SynchronizeTagsWithTriggerParameters model) {
		//setCheckboxSelected(model.isLengthThresholdEnabled());
		//getPanel().getLengthThresholdValueSpinner().setValue(model.getLengthThresholdValue());
	}

	public void fillModelFromPanel(SynchronizeTagsWithTriggerParameters model) {
		model.setLengthThresholdEnabled(isCheckboxSelected());
		model.setLengthThresholdValue(getPanel().getLengthThresholdValueSpinner().getValue());
	}

}

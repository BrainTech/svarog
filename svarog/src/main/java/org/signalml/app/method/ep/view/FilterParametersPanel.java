package org.signalml.app.method.ep.view;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.view.montage.filters.EditTimeDomainSampleFilterDialog;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.math.iirdesigner.BadFilterParametersException;
import org.signalml.math.iirdesigner.IIRDesigner;
import org.signalml.method.ep.EvokedPotentialParameters;

/**
 * This is a panel for selecting the cut-off frequency of a low pass filter
 * that will be used to filter the result of evoked potentials
 * averaging.
 *
 * @author Piotr Szachewicz
 */
public class FilterParametersPanel extends AbstractPanel {

	private JButton editFilterButton;
	/**
	 * the dialog that is used when editing {@link TimeDomainSampleFilter}
	 * parameters.
	 */
	private EditTimeDomainSampleFilterDialog editTimeDomainSampleFilterDialog;
	private TimeDomainSampleFilter filter;

	public FilterParametersPanel() {
		super("");
		this.add(getEditFilterButton());
	}

	public JButton getEditFilterButton() {
		if (editFilterButton == null) {
			editFilterButton = new JButton(new AbstractAction(_("Edit filter")) {
				{
					putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/editfilter.png"));
				}

				@Override
				public void actionPerformed(ActionEvent event) {
					getEditTimeDomainSampleFilterDialog().showDialog(filter);
				}
			});

		}
		return editFilterButton;
	}

	public EditTimeDomainSampleFilterDialog getEditTimeDomainSampleFilterDialog() {
		if (editTimeDomainSampleFilterDialog == null) {
			editTimeDomainSampleFilterDialog = new EditTimeDomainSampleFilterDialog(null, true);
		}
		return editTimeDomainSampleFilterDialog;
	}

	public void fillModelFromPanel(EvokedPotentialParameters parameters) {
		parameters.setTimeDomainSampleFilter(filter);
	}

	public void fillPanelFromModel(EvokedPotentialParameters parameters) {
		filter = parameters.getTimeDomainSampleFilter();
	}

	public void setSamplingFrequency(float samplingFrequency) {
		getEditTimeDomainSampleFilterDialog().setCurrentSamplingFrequency(samplingFrequency);
	}

	@Override
	public void validatePanel(ValidationErrors errors) {
		filter.setSamplingFrequency(getEditTimeDomainSampleFilterDialog().getCurrentSamplingFrequency());
		try {
			IIRDesigner.designDigitalFilter(filter);
		} catch (BadFilterParametersException e) {
			e.printStackTrace();
			errors.addError(_("Cannot design the EP filter - please correct its parameters."));
		}
	}

}

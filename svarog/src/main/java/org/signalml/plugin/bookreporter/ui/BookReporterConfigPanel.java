package org.signalml.plugin.bookreporter.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.common.components.CompactButton;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.plugin.bookreporter.chart.preset.BookReporterChartPreset;
import org.signalml.plugin.bookreporter.data.BookReporterParameters;
import org.signalml.plugin.bookreporter.ui.components.BookReporterChartRow;
import org.signalml.plugin.bookreporter.ui.components.BookReporterTitleLabel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._H;

/**
 * @author piotr@develancer.pl
 * (based on Michal Dobaczewski's NewStagerAdvancedConfigPanel)
 */
public class BookReporterConfigPanel extends JPanel {

	private final AbstractDialog owner;
	private final List<BookReporterChartRow> chartRows;

	private JPanel parametersPanel;
	
	protected class AddChartAction extends AbstractAction {
		
		private final Dialog dialogParent;

		public AddChartAction(Dialog dialogParent) {
			super(_("Add new chart"));
			this.dialogParent = dialogParent;
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/add.png"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			BookReporterNewChartDialog dialog = new BookReporterNewChartDialog(
				this.dialogParent,
				true
			);
			dialog.setLocationRelativeTo(this.dialogParent);

			// showing modal dialog
			dialog.setVisible(true);
			// modal dialog closed

			BookReporterChartPreset preset = dialog.getChartPreset();
			dialog.dispose();

			if (preset != null) {
				addPanelForNewChartPreset(preset);
			}
		}
	}
	
	public BookReporterConfigPanel(AbstractDialog owner) {
		super();
		this.owner = owner;
		this.chartRows = new ArrayList<BookReporterChartRow>();
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		CompoundBorder border = new CompoundBorder(new TitledBorder(
				_("Parameters")), new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		add(getParametersPanel(), BorderLayout.NORTH);
		add(Box.createVerticalGlue(), BorderLayout.CENTER);
	}

	private void addPanelForNewChartPreset(BookReporterChartPreset chartPreset) {
		final BookReporterChartRow newRow = new BookReporterChartRow(chartPreset);
		
		final JPanel parent = getParametersPanel();
		final AbstractAction removeAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chartRows.remove(newRow);
				newRow.removeFromPanel(parent);
				owner.pack();
			}
		};
		removeAction.putValue(
			AbstractAction.SMALL_ICON,
			IconUtils.loadClassPathIcon("org/signalml/app/icon/remove.png")
		);
		newRow.setRemoveAction(removeAction);

		chartRows.add(newRow);
		newRow.addToPanel(parent);
		owner.pack();
	}

	public JPanel getParametersPanel() {
		if (parametersPanel == null) {

			parametersPanel = new JPanel(new GridLayout(1, 6, 6, 10));

			BookReporterTitleLabel deltaLabel = new BookReporterTitleLabel(_("Delta waves"));
			BookReporterTitleLabel thetaLabel = new BookReporterTitleLabel(_("Theta waves"));
			BookReporterTitleLabel alphaLabel = new BookReporterTitleLabel(_("Alpha waves"));
			BookReporterTitleLabel spindleLabel = new BookReporterTitleLabel(_("Sleep spindles"));
			BookReporterTitleLabel kComplexLabel = new BookReporterTitleLabel(_("K-Complex"));

			BookReporterTitleLabel amplitudeLabel = new BookReporterTitleLabel(
					"<html><body><div align=\"center\">" + _("Amplitude [µV]")
							+ "<br />" + _("min/max") + "</div></body></html>");
			BookReporterTitleLabel frequencyLabel = new BookReporterTitleLabel(
					"<html><body><div align=\"center\">" + _("Frequency [Hz]")
							+ "<br />" + _("min/max") + "</div></body></html>");
			BookReporterTitleLabel scaleLabel = new BookReporterTitleLabel(
					"<html><body><div align=\"center\">" + _("Time width [s]")
							+ "<br />" + _("min/max") + "</div></body></html>");
			BookReporterTitleLabel phaseLabel = new BookReporterTitleLabel(
					"<html><body><div align=\"center\">" + _("Phase [rad]")
							+ "<br />" + _("min/max") + "</div></body></html>");

			CompactButton parametersHelpButton = SwingUtils
					.createFieldHelpButton(owner, _H("profilesEEG.html"));

			CompactButton addChartButton = new CompactButton(
				new AddChartAction(this.owner)
			);
			
			parametersPanel.add(addChartButton);
			parametersPanel.add(parametersHelpButton);
			parametersPanel.add(amplitudeLabel);
			parametersPanel.add(frequencyLabel);
			parametersPanel.add(scaleLabel);
			parametersPanel.add(phaseLabel);
		}
		return parametersPanel;
	}

	public void fillPanelFromParameters(BookReporterParameters parameters) {
		for (BookReporterChartRow chartRow : chartRows) {
			chartRow.removeFromPanel(parametersPanel);
		}
		chartRows.clear();
		owner.pack();
		
		for (BookReporterChartPreset chartPreset : parameters.chartPresets) {
			addPanelForNewChartPreset(chartPreset);
		}
	}

	public void fillParametersFromPanel(BookReporterParameters parameters) {
		int presetCount = chartRows.size();
		BookReporterChartPreset[] chartPresets = new BookReporterChartPreset[presetCount];
		for (int i=0; i<presetCount; i++) {
			chartPresets[i] = chartRows.get(i).getPreset();
		}
		parameters.chartPresets = chartPresets;
	}

//	private void normalize(BookReporterFASPThreshold threshold) {
//		threshold.amplitude.normalize();
//		threshold.frequency.normalize();
//		threshold.scale.normalize();
//	}

	public void validatePanel(ValidationErrors errors) {
		// nothing to do
	}

}

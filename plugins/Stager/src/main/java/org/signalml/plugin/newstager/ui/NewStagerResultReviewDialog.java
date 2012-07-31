/* StagerResultReviewDialog.java created 2008-02-21
 *
 */

package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.document.OpenDocumentDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.common.components.AnyChangeDocumentAdapter;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.SleepTagName;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.view.AbstractPluginDialog;
import org.signalml.plugin.export.view.FileChooser;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerSleepComparison;
import org.signalml.plugin.newstager.data.NewStagerSleepStatistic;

/**
 * StagerResultReviewDialog
 * 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerResultReviewDialog extends AbstractPluginDialog {

	private static final long serialVersionUID = 1L;

	private FileChooser fileChooser;

	private NewStagerExpertTagPanel expertStageTagPanel;
	private NewStagerExpertTagPanel expertArtifactTagPanel;

	private CompareAction compareAction;
	private CompareExcludingArtifactsAction compareExcludingArtifactsAction;

	private JButton compareButton;
	private JButton compareExcludingArtifactsButton;

	private NewStagerTotalStatisticsTableModel totalsTableModel;
	private NewStagerTotalStatisticsTable totalsTable;
	private JScrollPane totalsScrollPane;

	private NewStagerSleepStatisticTableModel sleepStatisticTableModel;
	private NewStagerSleepStatisticTable sleepStatisticTable;
	private JScrollPane sleepStatisticScrollPane;

	private JTabbedPane tabbedPane;

	private NewStagerResultTargetDescriptor currentDescriptor;

	private NewStagerSleepStatistic currentStatistic;

	protected NewSleepComparisonDialog sleepComparisonDialog;

	public NewStagerResultReviewDialog() {
		super();
	}

	public NewStagerResultReviewDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Stager result review"));
		setIconImage(IconUtils.loadClassPathImage(NewStagerPlugin.iconPath));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel expertPanel = new JPanel(new BorderLayout(3, 10));

		CompoundBorder border = new CompoundBorder(new TitledBorder(
				_("Compare with expert tag")), new EmptyBorder(3, 3, 3, 3));
		expertPanel.setBorder(border);

		JPanel expertButtonPanel = new JPanel(new FlowLayout(
				FlowLayout.TRAILING, 0, 0));
		expertButtonPanel.add(getCompareExcludingArtifactsButton());
		expertButtonPanel.add(Box.createHorizontalStrut(6));
		expertButtonPanel.add(getCompareButton());

		SwingUtils.makeButtonsSameSize(new JButton[] { getCompareButton(),
				getExpertArtifactTagPanel().getChooseTagButton(),
				getExpertStageTagPanel().getChooseTagButton() });

		expertPanel.add(getExpertStageTagPanel(), BorderLayout.NORTH);
		expertPanel.add(getExpertArtifactTagPanel(), BorderLayout.CENTER);
		expertPanel.add(expertButtonPanel, BorderLayout.SOUTH);

		JPanel statisticsPanel = new JPanel(new BorderLayout());
		border = new CompoundBorder(new TitledBorder(_("Result statistics")),
				new EmptyBorder(3, 3, 3, 3));
		statisticsPanel.setBorder(border);

		statisticsPanel.add(getTabbedPane(), BorderLayout.CENTER);

		interfacePanel.add(expertPanel, BorderLayout.CENTER);
		interfacePanel.add(statisticsPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	public NewStagerExpertTagPanel getExpertStageTagPanel() {
		if (expertStageTagPanel == null) {
			expertStageTagPanel = new NewStagerExpertTagPanel(fileChooser);
			expertStageTagPanel.initialize(_("Expert stages tag"),
					_("Choose refrerence tag with stages"));

			expertStageTagPanel.getTagTextField().getDocument()
					.addDocumentListener(new AnyChangeDocumentAdapter() {

						@Override
						public void anyUpdate(DocumentEvent e) {
							updateActionEnabled();
						}

					});

		}
		return expertStageTagPanel;
	}

	public NewStagerExpertTagPanel getExpertArtifactTagPanel() {
		if (expertArtifactTagPanel == null) {
			expertArtifactTagPanel = new NewStagerExpertTagPanel(fileChooser);

			expertArtifactTagPanel.initialize(_("Expert artifacts tag"),
					_("Choose refrerence tag with artifacts"));

			expertArtifactTagPanel.getTagTextField().getDocument()
					.addDocumentListener(new AnyChangeDocumentAdapter() {

						@Override
						public void anyUpdate(DocumentEvent e) {
							updateActionEnabled();
						}

					});

		}
		return expertArtifactTagPanel;
	}

	public CompareAction getCompareAction() {
		if (compareAction == null) {
			compareAction = new CompareAction();
		}
		return compareAction;
	}

	public CompareExcludingArtifactsAction getCompareExcludingArtifactsAction() {
		if (compareExcludingArtifactsAction == null) {
			compareExcludingArtifactsAction = new CompareExcludingArtifactsAction();
		}
		return compareExcludingArtifactsAction;
	}

	public JButton getCompareButton() {
		if (compareButton == null) {
			compareButton = new JButton(getCompareAction());
		}
		return compareButton;
	}

	public JButton getCompareExcludingArtifactsButton() {
		if (compareExcludingArtifactsButton == null) {
			compareExcludingArtifactsButton = new JButton(
					getCompareExcludingArtifactsAction());
		}
		return compareExcludingArtifactsButton;
	}

	public NewStagerTotalStatisticsTableModel getTotalsTableModel() {
		if (totalsTableModel == null) {
			totalsTableModel = new NewStagerTotalStatisticsTableModel();
		}
		return totalsTableModel;
	}

	public NewStagerTotalStatisticsTable getTotalsTable() {
		if (totalsTable == null) {
			totalsTable = new NewStagerTotalStatisticsTable(getTotalsTableModel());
		}
		return totalsTable;
	}

	public JScrollPane getTotalsScrollPane() {
		if (totalsScrollPane == null) {
			totalsScrollPane = new JScrollPane(getTotalsTable());
			totalsScrollPane.setPreferredSize(new Dimension(400, 200));
		}
		return totalsScrollPane;
	}

	public NewStagerSleepStatisticTableModel getSleepStatisticTableModel() {
		if (sleepStatisticTableModel == null) {
			sleepStatisticTableModel = new NewStagerSleepStatisticTableModel();
		}
		return sleepStatisticTableModel;
	}

	public NewStagerSleepStatisticTable getSleepStatisticTable() {
		if (sleepStatisticTable == null) {
			sleepStatisticTable = new NewStagerSleepStatisticTable(
					getSleepStatisticTableModel());
		}
		return sleepStatisticTable;
	}

	public JScrollPane getSleepStatisticScrollPane() {
		if (sleepStatisticScrollPane == null) {
			sleepStatisticScrollPane = new JScrollPane(getSleepStatisticTable());

			sleepStatisticScrollPane.setPreferredSize(new Dimension(400, 200));
		}
		return sleepStatisticScrollPane;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP,
					JTabbedPane.SCROLL_TAB_LAYOUT);

			tabbedPane.addTab(_("Totals"), getTotalsScrollPane());
			tabbedPane.addTab(_("Stages"), getSleepStatisticScrollPane());
		}
		return tabbedPane;
	}

	private void updateActionEnabled() {

		boolean hasStageTag = (getExpertStageTagPanel().getTagFile() != null);
		boolean hasArtifactTag = (getExpertArtifactTagPanel().getTagFile() != null);

		getCompareAction().setEnabled(hasStageTag);
		getCompareExcludingArtifactsAction().setEnabled(
				hasArtifactTag && hasStageTag);

	}

	@Override
	public void fillDialogFromModel(Object model) {

		NewStagerResultTargetDescriptor descriptor = (NewStagerResultTargetDescriptor) model;

		getExpertStageTagPanel().setTagFile(descriptor.getExpertStageTagFile());
		getExpertArtifactTagPanel().setTagFile(
				descriptor.getExpertArtifactTagFile());

		NewStagerSleepStatistic sleepStatistic = new NewStagerSleepStatistic(
				descriptor.getStagerResult(), descriptor.getPrimaryTag(),
				descriptor.getSegmentCount(), descriptor.getSegmentLength());
		getTotalsTableModel().setStatistic(sleepStatistic);
		getSleepStatisticTableModel().setStatistic(sleepStatistic);

		updateActionEnabled();

		currentDescriptor = descriptor;
		currentStatistic = sleepStatistic;

	}

	@Override
	public void fillModelFromDialog(Object model) {

		NewStagerResultTargetDescriptor descriptor = (NewStagerResultTargetDescriptor) model;

		descriptor.setExpertStageTagFile(getExpertStageTagPanel().getTagFile());
		descriptor.setExpertArtifactTagFile(getExpertArtifactTagPanel()
				.getTagFile());

	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors)
			throws SignalMLException {
		super.validateDialog(model, errors);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return NewStagerResultTargetDescriptor.class.isAssignableFrom(clazz);
	}

	public FileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(FileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	protected TagDocument getTagDocument(File tagFile) {

		TagDocument tag = null;

		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		ofd.setType(ManagedDocumentType.TAG);
		ofd.setMakeActive(true);

		boolean legTag = true;
		LegacyTagImporter importer = new LegacyTagImporter();
		StyledTagSet tagSet = null;
		try {
			// TODO!
			SignalDocument signalDocument = null;
			tagSet = importer.importLegacyTags(tagFile,
					signalDocument.getSamplingFrequency());
			tag = new TagDocument(tagSet);
		} catch (SignalMLException ex) {
			legTag = false;
			logger.info("Failed to import tags, not a legacy tag");
		}

		if (!legTag) {
			try {
				tag = new TagDocument(tagFile);
			} catch (SignalMLException ex) {
				logger.error("Failed to read tag file ["
						+ tagFile.getAbsolutePath() + "]");
				Dialogs.showExceptionDialog(this, ex);
				return null;
			} catch (IOException ex) {
				logger.error("Failed to read tag file ["
						+ tagFile.getAbsolutePath() + "] - i/o exception");
				Dialogs.showExceptionDialog(this, ex);
				return null;
			}
		}

		return tag;
	}

	protected class CompareAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CompareAction() {
			super(_("Compare"));
			putValue(
					AbstractAction.SMALL_ICON,
					IconUtils
							.loadClassPathIcon("org/signalml/app/icon/analyze.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,
					_("Compare staging result with expert tag"));
		}

		public void actionPerformed(ActionEvent ev) {

			boolean excludingArtifacts = ("compareExcludingArtifacts".equals(ev
					.getActionCommand()));

			File expertStageTagFile = getExpertStageTagPanel().getTagFile();
			if (expertStageTagFile == null) {
				return;
			}
			if (!expertStageTagFile.exists() || !expertStageTagFile.canRead()) {
				logger.error("File [" + expertStageTagFile.getAbsolutePath()
						+ "] doesn't exist");
				// TODO!
				// getErrorsDialog().showException(new FileNotFoundException());
				return;
			}

			TagDocument expertStageTag = getTagDocument(expertStageTagFile);

			if (expertStageTag == null) {
				logger.error("Failed to read tag file ["
						+ expertStageTagFile.getAbsolutePath()
						+ "] - i/o exception");
				// TODO!
				// getErrorsDialog().showException(new IOException());
				return;
			}

			ExportedTagDocument tag = currentDescriptor.getPrimaryTag();
			if (SleepTagName.isValidRKSleepTag(tag)) {

				if (!SleepTagName.isValidRKSleepTag(expertStageTag)) {
					logger.error("Expert tag file not RK");
					// TODO!
					// getErrorsDialog().showException(
					// new SignalMLException(
					// "error.stager.result.expertTagNotCompatibleRK"));
					return;
				}

			} else if (SleepTagName.isValidAASMSleepTag(tag)) {

				if (!SleepTagName.isValidAASMSleepTag(expertStageTag)) {
					logger.error("Expert tag file not AASM");
					// TODO!
					// getErrorsDialog()
					// .showException(
					// new SignalMLException(
					// "error.stager.result.expertTagNotCompatibleAASM"));
					return;
				}

			} else {
				logger.error("Primary tag is neither RK nor AASM");
				// TODO!
				// getErrorsDialog().showException(
				// new SignalMLException(
				// "error.stager.result.resultTagUnknownRules"));
				return;
			}

			TagDocument expertArtifactTag = null;

			if (excludingArtifacts) {

				File expertArtifactTagFile = getExpertArtifactTagPanel()
						.getTagFile();
				if (expertArtifactTagFile == null) {
					return;
				}
				if (!expertArtifactTagFile.exists()
						|| !expertArtifactTagFile.canRead()) {
					logger.error("File ["
							+ expertArtifactTagFile.getAbsolutePath()
							+ "] doesn't exist");
					// TODO!
					// getErrorsDialog()
					// .showException(new FileNotFoundException());
					return;
				}

				expertArtifactTag = getTagDocument(expertArtifactTagFile);

				if (expertStageTag == null) {
					logger.error("Failed to read tag file ["
							+ expertArtifactTagFile.getAbsolutePath()
							+ "] - i/o exception");
					// TODO!
					// getErrorsDialog().showException(new IOException());
					return;
				}
			}

			NewStagerSleepComparison comparison = new NewStagerSleepComparison(
					currentStatistic, tag, expertStageTag, expertArtifactTag);

			if (sleepComparisonDialog == null) {
				sleepComparisonDialog = new NewSleepComparisonDialog(
						NewStagerResultReviewDialog.this, true);
			}

			sleepComparisonDialog.showDialog(comparison, true);

		}

	}

	protected class CompareExcludingArtifactsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CompareExcludingArtifactsAction() {
			super(_("Compare no artifacts"));
			putValue(
					AbstractAction.SMALL_ICON,
					IconUtils
							.loadClassPathIcon("org/signalml/app/icon/analyze.png"));
			putValue(
					AbstractAction.SHORT_DESCRIPTION,
					_("Compare staging result with expert tag (pages without artifacts only)"));
		}

		public void actionPerformed(ActionEvent ev) {

			compareAction.actionPerformed(new ActionEvent(ev.getSource(), 0,
					"compareExcludingArtifacts"));

		}

	}

}

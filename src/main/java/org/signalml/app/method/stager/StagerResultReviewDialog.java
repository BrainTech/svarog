/* StagerResultReviewDialog.java created 2008-02-21
 *
 */

package org.signalml.app.method.stager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
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
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.PropertySheetModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.ViewerPropertySheet;
import org.signalml.app.view.element.AnyChangeDocumentAdapter;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.SleepTagName;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerResultReviewDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerResultReviewDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;

	private StagerExpertTagPanel expertStageTagPanel;
	private StagerExpertTagPanel expertArtifactTagPanel;

	private CompareAction compareAction;
	private CompareExcludingArtifactsAction compareExcludingArtifactsAction;

	private JButton compareButton;
	private JButton compareExcludingArtifactsButton;

	private PropertySheetModel propertySheetModel;
	private ViewerPropertySheet propertySheet;
	private JScrollPane propertyScrollPane;

	private SleepStatisticTableModel sleepStatisticTableModel;
	private SleepStatisticTable sleepStatisticTable;
	private JScrollPane sleepStatisticScrollPane;

	private JTabbedPane tabbedPane;

	private StagerResultTargetDescriptor currentDescriptor;

	private SleepStatistic currentStatistic;

	protected SleepComparisonDialog sleepComparisonDialog;

	protected SignalDocument signalDocument;

	public StagerResultReviewDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public StagerResultReviewDialog(MessageSourceAccessor messageSource, Window w, 	SignalDocument signalDocument, boolean isModal) {
		super(messageSource, w, isModal);
		this.signalDocument = signalDocument;
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("stagerMethod.dialog.resultReview.title"));
		setIconImage(IconUtils.loadClassPathImage(StagerMethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel expertPanel = new JPanel(new BorderLayout(3,10));

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("stagerMethod.dialog.resultReview.expertTitle")),
		        new EmptyBorder(3,3,3,3)
		);
		expertPanel.setBorder(border);

		JPanel expertButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 0, 0));
		expertButtonPanel.add(getCompareExcludingArtifactsButton());
		expertButtonPanel.add(Box.createHorizontalStrut(6));
		expertButtonPanel.add(getCompareButton());

		SwingUtils.makeButtonsSameSize(new JButton[] { getCompareButton(), getExpertArtifactTagPanel().getChooseTagButton(), getExpertStageTagPanel().getChooseTagButton() });

		expertPanel.add(getExpertStageTagPanel(), BorderLayout.NORTH);
		expertPanel.add(getExpertArtifactTagPanel(), BorderLayout.CENTER);
		expertPanel.add(expertButtonPanel, BorderLayout.SOUTH);

		JPanel statisticsPanel = new JPanel(new BorderLayout());
		border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("stagerMethod.dialog.resultReview.statisticsTitle")),
		        new EmptyBorder(3,3,3,3)
		);
		statisticsPanel.setBorder(border);

		statisticsPanel.add(getTabbedPane(), BorderLayout.CENTER);

		interfacePanel.add(expertPanel, BorderLayout.CENTER);
		interfacePanel.add(statisticsPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	public StagerExpertTagPanel getExpertStageTagPanel() {
		if (expertStageTagPanel == null) {
			expertStageTagPanel = new StagerExpertTagPanel(messageSource,fileChooser);
			expertStageTagPanel.setLabelCode("stagerMethod.dialog.resultReview.expertStageTag");
			expertStageTagPanel.setChooseButtonToolTipCode("stagerMethod.dialog.resultReview.expertStageTagToolTip");
			expertStageTagPanel.initialize();

			expertStageTagPanel.getTagTextField().getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {

				@Override
				public void anyUpdate(DocumentEvent e) {
					updateActionEnabled();
				}

			});

		}
		return expertStageTagPanel;
	}

	public StagerExpertTagPanel getExpertArtifactTagPanel() {
		if (expertArtifactTagPanel == null) {
			expertArtifactTagPanel = new StagerExpertTagPanel(messageSource,fileChooser);
			expertArtifactTagPanel.setLabelCode("stagerMethod.dialog.resultReview.expertArtifactTag");
			expertArtifactTagPanel.setChooseButtonToolTipCode("stagerMethod.dialog.resultReview.expertArtifactTagToolTip");
			expertArtifactTagPanel.initialize();

			expertArtifactTagPanel.getTagTextField().getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {

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
			compareExcludingArtifactsButton = new JButton(getCompareExcludingArtifactsAction());
		}
		return compareExcludingArtifactsButton;
	}

	public PropertySheetModel getPropertySheetModel() {
		if (propertySheetModel == null) {
			propertySheetModel = new PropertySheetModel();
			propertySheetModel.setMessageSource(messageSource);
		}
		return propertySheetModel;
	}

	public ViewerPropertySheet getPropertySheet() {
		if (propertySheet == null) {
			propertySheet = new ViewerPropertySheet(getPropertySheetModel());
		}
		return propertySheet;
	}

	public JScrollPane getPropertyScrollPane() {
		if (propertyScrollPane == null) {
			propertyScrollPane = new JScrollPane(getPropertySheet());
			propertyScrollPane.setPreferredSize(new Dimension(400,200));
		}
		return propertyScrollPane;
	}

	public SleepStatisticTableModel getSleepStatisticTableModel() {
		if (sleepStatisticTableModel == null) {
			sleepStatisticTableModel = new SleepStatisticTableModel(messageSource);
		}
		return sleepStatisticTableModel;
	}

	public SleepStatisticTable getSleepStatisticTable() {
		if (sleepStatisticTable == null) {
			sleepStatisticTable = new SleepStatisticTable(getSleepStatisticTableModel(), messageSource);
		}
		return sleepStatisticTable;
	}

	public JScrollPane getSleepStatisticScrollPane() {
		if (sleepStatisticScrollPane == null) {
			sleepStatisticScrollPane = new JScrollPane(getSleepStatisticTable());

			sleepStatisticScrollPane.setPreferredSize(new Dimension(400,200));
		}
		return sleepStatisticScrollPane;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

			tabbedPane.addTab(messageSource.getMessage("stagerMethod.dialog.resultReview.totalTab"), getPropertyScrollPane());
			tabbedPane.addTab(messageSource.getMessage("stagerMethod.dialog.resultReview.stageTab"), getSleepStatisticScrollPane());
		}
		return tabbedPane;
	}

	private void updateActionEnabled() {

		boolean hasStageTag = (getExpertStageTagPanel().getTagFile() != null);
		boolean hasArtifactTag = (getExpertArtifactTagPanel().getTagFile() != null);

		getCompareAction().setEnabled(hasStageTag);
		getCompareExcludingArtifactsAction().setEnabled(hasArtifactTag && hasStageTag);

	}

	@Override
	public void fillDialogFromModel(Object model) {

		StagerResultTargetDescriptor descriptor = (StagerResultTargetDescriptor) model;

		getExpertStageTagPanel().setTagFile(descriptor.getExpertStageTagFile());
		getExpertArtifactTagPanel().setTagFile(descriptor.getExpertArtifactTagFile());

		SleepStatistic sleepStatistic = new SleepStatistic(descriptor.getStagerResult(), descriptor.getPrimaryTag(), descriptor.getSegmentCount(), descriptor.getSegmentLength());
		getPropertySheetModel().setSubject(sleepStatistic);
		getSleepStatisticTableModel().setStatistic(sleepStatistic);

		updateActionEnabled();

		currentDescriptor = descriptor;
		currentStatistic = sleepStatistic;

	}

	@Override
	public void fillModelFromDialog(Object model) {

		StagerResultTargetDescriptor descriptor = (StagerResultTargetDescriptor) model;

		descriptor.setExpertStageTagFile(getExpertStageTagPanel().getTagFile());
		descriptor.setExpertArtifactTagFile(getExpertArtifactTagPanel().getTagFile());

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);


	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return StagerResultTargetDescriptor.class.isAssignableFrom(clazz);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
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
			tagSet = importer.importLegacyTags(tagFile , signalDocument.getSamplingFrequency());
			tag = new TagDocument(tagSet);
		} catch (SignalMLException ex) {
			legTag = false;
			logger.info("Failed to import tags, not a legacy tag");
		}

		if (! legTag) {
			try {
				tag = new TagDocument(tagFile);
			} catch (SignalMLException ex) {
				logger.error("Failed to read tag file [" + tagFile.getAbsolutePath() + "]");
				getErrorsDialog().showException(ex);
				return null;
			} catch (IOException ex) {
				logger.error("Failed to read tag file [" + tagFile.getAbsolutePath() + "] - i/o exception");
				getErrorsDialog().showException(ex);
				return null;
			}
		}

		return tag;
	}

	protected class CompareAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CompareAction() {
			super(messageSource.getMessage("stagerMethod.dialog.resultReview.compare"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/analyze.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("stagerMethod.dialog.resultReview.compareToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			boolean excludingArtifacts = ("compareExcludingArtifacts".equals(ev.getActionCommand()));

			File expertStageTagFile = getExpertStageTagPanel().getTagFile();
			if (expertStageTagFile == null) {
				return;
			}
			if (!expertStageTagFile.exists() || !expertStageTagFile.canRead()) {
				logger.error("File [" + expertStageTagFile.getAbsolutePath() + "] doesn't exist");
				getErrorsDialog().showException(new FileNotFoundException());
				return;
			}

			TagDocument expertStageTag = getTagDocument(expertStageTagFile);

			if (expertStageTag == null) {
				logger.error("Failed to read tag file [" + expertStageTagFile.getAbsolutePath() + "] - i/o exception");
				getErrorsDialog().showException(new IOException());
				return;
			}

			TagDocument tag = currentDescriptor.getPrimaryTag();
			if (SleepTagName.isValidRKSleepTag(tag)) {

				if (!SleepTagName.isValidRKSleepTag(expertStageTag)) {
					logger.error("Expert tag file not RK");
					getErrorsDialog().showException(new SignalMLException("error.stager.result.expertTagNotCompatibleRK"));
					return;
				}

			}
			else if (SleepTagName.isValidAASMSleepTag(tag)) {

				if (!SleepTagName.isValidAASMSleepTag(expertStageTag)) {
					logger.error("Expert tag file not AASM");
					getErrorsDialog().showException(new SignalMLException("error.stager.result.expertTagNotCompatibleAASM"));
					return;
				}

			} else {
				logger.error("Primary tag is neither RK nor AASM");
				getErrorsDialog().showException(new SignalMLException("error.stager.result.resultTagUnknownRules"));
				return;
			}

			TagDocument expertArtifactTag = null;

			if (excludingArtifacts) {

				File expertArtifactTagFile = getExpertArtifactTagPanel().getTagFile();
				if (expertArtifactTagFile == null) {
					return;
				}
				if (!expertArtifactTagFile.exists() || !expertArtifactTagFile.canRead()) {
					logger.error("File [" + expertArtifactTagFile.getAbsolutePath() + "] doesn't exist");
					getErrorsDialog().showException(new FileNotFoundException());
					return;
				}

				expertArtifactTag = getTagDocument(expertArtifactTagFile);

				if (expertStageTag == null) {
					logger.error("Failed to read tag file [" + expertArtifactTagFile.getAbsolutePath() + "] - i/o exception");
					getErrorsDialog().showException(new IOException());
					return;
				}
			}

			SleepComparison comparison = new SleepComparison(currentStatistic, tag, expertStageTag, expertArtifactTag);

			if (sleepComparisonDialog == null) {
				sleepComparisonDialog = new SleepComparisonDialog(messageSource,StagerResultReviewDialog.this,true);
			}

			sleepComparisonDialog.showDialog(comparison, true);

		}

	}

	protected class CompareExcludingArtifactsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CompareExcludingArtifactsAction() {
			super(messageSource.getMessage("stagerMethod.dialog.resultReview.compareExcludingArtifacts"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/analyze.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("stagerMethod.dialog.resultReview.compareExcludingArtifactsToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			compareAction.actionPerformed(new ActionEvent(ev.getSource(), 0, "compareExcludingArtifacts"));

		}

	}

}

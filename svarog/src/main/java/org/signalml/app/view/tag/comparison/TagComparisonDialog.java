/* TagComparisonDialog.java created 2007-11-14
 *
 */

package org.signalml.app.view.tag.comparison;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.model.TagComparisonDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.domain.tag.TagComparisonResults;
import org.signalml.domain.tag.TagDifferenceDetector;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractDialog;
import org.signalml.util.SvarogConstants;
import org.springframework.context.support.MessageSourceAccessor;

/** TagComparisonDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagComparisonDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private SignalDocument currentSignalDocument;

	private TagIconProducer tagIconProducer;

	private TagDocument currentTopDocument;
	private TagDocument currentBottomDocument;

	private ResolvableComboBox topDocumentComboBox;
	private ResolvableComboBox bottomDocumentComboBox;

	private TagComparisonResultPanel resultPanel;

	private TagDifferenceDetector detector;
	private TableToTextExporter tableToTextExporter;
	private ViewerFileChooser fileChooser;

	public TagComparisonDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public TagComparisonDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("tagComparison.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/comparetags.png"));
		setPreferredSize(SvarogConstants.MIN_ASSUMED_DESKTOP_SIZE);
		super.initialize();

		topDocumentComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TagDocument document = (TagDocument) topDocumentComboBox.getSelectedItem();
				if (document != null) {
					currentTopDocument = document;
					updateResult();
				}

			}

		});

		bottomDocumentComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TagDocument document = (TagDocument) bottomDocumentComboBox.getSelectedItem();
				if (document != null) {
					currentBottomDocument = document;
					updateResult();
				}

			}

		});

	}

	@Override
	public JComponent createInterface() {

		topDocumentComboBox = new ResolvableComboBox(messageSource);
		bottomDocumentComboBox = new ResolvableComboBox(messageSource);

		JPanel topDocumentPanel = new JPanel(new BorderLayout());
		topDocumentPanel.setBorder(new TitledBorder(messageSource.getMessage("tagComparison.topDocument")));
		topDocumentPanel.add(topDocumentComboBox, BorderLayout.CENTER);

		JPanel bottomDocumentPanel = new JPanel(new BorderLayout());
		bottomDocumentPanel.setBorder(new TitledBorder(messageSource.getMessage("tagComparison.bottomDocument")));
		bottomDocumentPanel.add(bottomDocumentComboBox, BorderLayout.CENTER);

		JPanel topPanel = new JPanel(new GridLayout(1,2,3,3));
		topPanel.add(topDocumentPanel);
		topPanel.add(bottomDocumentPanel);

		resultPanel = new TagComparisonResultPanel(messageSource, tableToTextExporter, fileChooser);

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(topPanel, BorderLayout.NORTH);
		interfacePanel.add(resultPanel, BorderLayout.CENTER);

		return interfacePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		TagComparisonDescriptor descriptor = (TagComparisonDescriptor) model;

		resultPanel.setTagIconProducer(descriptor.getTagIconProducer());

		currentSignalDocument = descriptor.getSignalDocument();
		if (currentSignalDocument == null) {
			throw new NullPointerException("No signal");
		}

		List<TagDocument> tags = currentSignalDocument.getTagDocuments();
		if (tags.size() < 2) {
			throw new SanityCheckException("Not enough tags on the signal");
		}
		TagDocument[] tagArr = new TagDocument[tags.size()];
		tags.toArray(tagArr);

		currentTopDocument = descriptor.getTopTagDocument();
		currentBottomDocument = descriptor.getBottomTagDocument();

		if (currentTopDocument == null) {
			currentTopDocument = tagArr[0];
			if (currentTopDocument == currentBottomDocument) {
				currentTopDocument = tagArr[1];
			}
		} else {
			if (!tags.contains(currentTopDocument)) {
				throw new SanityCheckException("Top tag not in the document");
			}
		}

		if (currentBottomDocument == null) {
			currentBottomDocument = tagArr[1];
			if (currentBottomDocument == currentTopDocument) {
				currentBottomDocument = tagArr[0];
			}
		} else {
			if (!tags.contains(currentBottomDocument)) {
				throw new SanityCheckException("Top tag not in the document");
			}
		}

		topDocumentComboBox.setModel(new DefaultComboBoxModel(tagArr));
		topDocumentComboBox.setSelectedItem(currentTopDocument);

		bottomDocumentComboBox.setModel(new DefaultComboBoxModel(tagArr));
		bottomDocumentComboBox.setSelectedItem(currentBottomDocument);

		updateResult();

	}

	private void updateResult() {

		if (detector == null) {
			detector = new TagDifferenceDetector();
		}

		// TODO maybe needs worker - if so the detector needs progress reporting

		TagComparisonResults results = detector.compare(currentTopDocument, currentBottomDocument);
		results.setMessageSourceAccessor(messageSource);
		results.getParametersFromSampleSource(currentSignalDocument.getSampleSource(), currentSignalDocument.getMontage());
		resultPanel.setResults(results);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		TagComparisonDescriptor descriptor = (TagComparisonDescriptor) model;

		descriptor.setTopTagDocument(currentTopDocument);
		descriptor.setBottomTagDocument(currentBottomDocument);

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return TagComparisonDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		this.tagIconProducer = tagIconProducer;
	}

	public TableToTextExporter getTableToTextExporter() {
		return tableToTextExporter;
	}

	public void setTableToTextExporter(TableToTextExporter tableToTextExporter) {
		this.tableToTextExporter = tableToTextExporter;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}

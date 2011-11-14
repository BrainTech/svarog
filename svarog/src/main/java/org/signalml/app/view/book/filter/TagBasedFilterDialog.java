/* TagBasedFilterDialog.java created 2008-03-04
 *
 */

package org.signalml.app.view.book.filter;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;

import org.signalml.app.action.util.ListSelectAllAction;
import org.signalml.app.action.util.ListSelectInvertAction;
import org.signalml.app.action.util.ListSelectNoneAction;
import org.signalml.app.document.TagDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.element.AnyChangeDocumentAdapter;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.app.view.tag.TagStyleListCellRenderer;
import org.signalml.domain.book.filter.TagBasedAtomFilter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.TagStyle;

import org.springframework.validation.Errors;

/** TagBasedFilterDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagBasedFilterDialog extends AbstractFilterDialog {

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;

	private BookFilterChooseTagPanel chooseTagPanel;

	private DefaultListModel styleListModel;
	private JList styleList;
	private JScrollPane styleScrollPane;

	private JButton channelSelectAllButton;
	private JButton channelSelectInvertButton;
	private JButton channelSelectNoneButton;

	private JPanel settingsPanel;

	private JSpinner secondsBeforeSpinner;
	private JSpinner secondsAfterSpinner;

	private TagStyleListCellRenderer markerStyleCellRenderer;

	private TagDocument currentTagDocument;

	public TagBasedFilterDialog( Window w, boolean isModal) {
		super( w, isModal);
	}

	@Override
	protected void initialize() {

		setTitle(_("Tag based atom filter"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/filter.png"));
		super.initialize();
		setResizable(false);

		getChooseTagPanel().getTagTextField().getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {

			@Override
			public void anyUpdate(DocumentEvent e) {

				File tagFile = getChooseTagPanel().getTagFile();
				if (tagFile == null) {
					setCurrentTagDocument(null);
				} else {
					if (currentTagDocument == null || !tagFile.equals(currentTagDocument.getBackingFile())) {

						// load tag
						TagDocument document;
						try {
							document = new TagDocument(tagFile);
						} catch (SignalMLException ex) {
							logger.error("Failed to open tag file [" + tagFile.getAbsolutePath() + "]", ex);
							getErrorsDialog().showException(ex);
							setCurrentTagDocument(null);
							return;
						} catch (IOException ex) {
							logger.error("Failed to open tag file [" + tagFile.getAbsolutePath() + "]", ex);
							getErrorsDialog().showException(ex);
							setCurrentTagDocument(null);
							return;
						}

						setCurrentTagDocument(document);

					}
				}

			}

		});

	}

	@Override
	public JComponent createInterface() {

		JPanel stylePanel = new JPanel(new BorderLayout());
		stylePanel.setBorder(new CompoundBorder(
		                             new TitledBorder(_("Choose styles")),
		                             new EmptyBorder(3,3,3,3)
		                     ));

		JPanel styleButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 3));
		styleButtonPanel.add(getChannelSelectAllButton());
		styleButtonPanel.add(getChannelSelectNoneButton());
		styleButtonPanel.add(getChannelSelectInvertButton());

		stylePanel.add(getStyleScrollPane(), BorderLayout.CENTER);
		stylePanel.add(styleButtonPanel, BorderLayout.SOUTH);

		JPanel bottomPanel = new JPanel(new BorderLayout());

		bottomPanel.add(getChooseTagPanel(), BorderLayout.NORTH);
		bottomPanel.add(stylePanel, BorderLayout.CENTER);
		bottomPanel.add(getSettingsPanel(), BorderLayout.SOUTH);

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(getNamePanel(), BorderLayout.NORTH);
		interfacePanel.add(bottomPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	public BookFilterChooseTagPanel getChooseTagPanel() {
		if (chooseTagPanel == null) {
			chooseTagPanel = new BookFilterChooseTagPanel( fileChooser);
		}
		return chooseTagPanel;
	}

	public TagStyleListCellRenderer getMarkerStyleCellRenderer() {
		if (markerStyleCellRenderer == null) {
			markerStyleCellRenderer = new TagStyleListCellRenderer();
		}
		return markerStyleCellRenderer;
	}

	public DefaultListModel getStyleListModel() {
		if (styleListModel == null) {
			styleListModel = new DefaultListModel();
		}
		return styleListModel;
	}

	public JList getStyleList() {
		if (styleList == null) {
			styleList = new JList(getStyleListModel());
			styleList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			styleList.setCellRenderer(getMarkerStyleCellRenderer());
		}
		return styleList;
	}

	public JScrollPane getStyleScrollPane() {
		if (styleScrollPane == null) {
			styleScrollPane = new JScrollPane(getStyleList());

			styleScrollPane.setPreferredSize(new Dimension(400,300));
		}
		return styleScrollPane;
	}

	public JButton getChannelSelectAllButton() {
		if (channelSelectAllButton == null) {
			channelSelectAllButton = new JButton(new ListSelectAllAction( getStyleList()));
		}
		return channelSelectAllButton;
	}

	public JButton getChannelSelectNoneButton() {
		if (channelSelectNoneButton == null) {
			channelSelectNoneButton = new JButton(new ListSelectNoneAction( getStyleList()));
		}
		return channelSelectNoneButton;
	}

	public JButton getChannelSelectInvertButton() {
		if (channelSelectInvertButton == null) {
			channelSelectInvertButton = new JButton(new ListSelectInvertAction( getStyleList()));
		}
		return channelSelectInvertButton;
	}

	public JPanel getSettingsPanel() {
		if (settingsPanel == null) {

			settingsPanel = new JPanel();

			settingsPanel.setBorder(new CompoundBorder(
			                                new TitledBorder(_("Settings")),
			                                new EmptyBorder(3,3,3,3)
			                        ));

			GroupLayout layout = new GroupLayout(settingsPanel);
			settingsPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel secondsBeforeLabel = new JLabel(_("Seconds before"));
			JLabel secondsAfterLabel = new JLabel(_("Seconds after"));

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(secondsBeforeLabel)
			        .addComponent(secondsAfterLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.TRAILING)
			        .addComponent(getSecondsBeforeSpinner())
			        .addComponent(getSecondsAfterSpinner())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(secondsBeforeLabel)
					.addComponent(getSecondsBeforeSpinner())
				);
			
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(secondsAfterLabel)
					.addComponent(getSecondsAfterSpinner())
				);

			layout.setVerticalGroup(vGroup);

		}
		return settingsPanel;
	}

	public JSpinner getSecondsBeforeSpinner() {
		if (secondsBeforeSpinner == null) {
			secondsBeforeSpinner = new JSpinner(new SpinnerNumberModel(1.0,0.0,3600,0.1));
			Dimension fixedSize = new Dimension(200,25);
			secondsBeforeSpinner.setPreferredSize(fixedSize);
		}
		return secondsBeforeSpinner;
	}

	public JSpinner getSecondsAfterSpinner() {
		if (secondsAfterSpinner == null) {
			secondsAfterSpinner = new JSpinner(new SpinnerNumberModel(1.0,0.0,3600,0.1));
			Dimension fixedSize = new Dimension(200,25);
			secondsAfterSpinner.setPreferredSize(fixedSize);
		}
		return secondsAfterSpinner;
	}


	protected TagDocument getCurrentTagDocument() {
		return currentTagDocument;
	}

	protected void setCurrentTagDocument(TagDocument currentTagDocument) {
		if (this.currentTagDocument != currentTagDocument) {
			if (this.currentTagDocument != null) {
				try {
					this.currentTagDocument.closeDocument();
					this.currentTagDocument = null;
				} catch (SignalMLException ex) {
					logger.error("Failed to close old tag document", ex);
					// ignore any exceptions
				}
			}
			this.currentTagDocument = currentTagDocument;

			DefaultListModel listModel = getStyleListModel();
			listModel.clear();
			getStyleList().clearSelection();
			setTagIconProducer(new TagIconProducer());

			if (currentTagDocument != null) {

				StyledTagSet tagSet = currentTagDocument.getTagSet();
				LinkedHashSet<TagStyle> styles = tagSet.getStyles();

				for (TagStyle style : styles) {
					listModel.addElement(style);
				}

			}

		}

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		TagBasedAtomFilter filter = (TagBasedAtomFilter) model;

		super.fillDialogFromFilter(filter);

		getChooseTagPanel().fillPanelFromModel(filter);

		LinkedHashSet<String> styleNames = filter.getStyleNames();

		JList list = getStyleList();
		list.clearSelection();

		if (styleNames != null) {

			DefaultListModel listModel = getStyleListModel();
			int size = listModel.getSize();
			TagStyle style;

			for (int i=0; i<size; i++) {
				style = (TagStyle) listModel.getElementAt(i);
				if (styleNames.contains(style.getName())) {
					list.addSelectionInterval(i, i);
				}
			}

		}

		getSecondsBeforeSpinner().setValue(filter.getSecondsBefore());
		getSecondsAfterSpinner().setValue(filter.getSecondsAfter());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		TagBasedAtomFilter filter = (TagBasedAtomFilter) model;

		super.fillFilterFromDialog(filter);

		fillFilterFromDialog(filter);

		filter.initialize();

	}

	protected void fillFilterFromDialog(TagBasedAtomFilter filter) {

		getChooseTagPanel().fillModelFromPanel(filter);

		DefaultListModel listModel = getStyleListModel();
		int size = listModel.getSize();

		JList list = getStyleList();
		LinkedHashSet<String> styleNames = new LinkedHashSet<String>();

		for (int i=0; i<size; i++) {

			if (list.isSelectedIndex(i)) {
				styleNames.add(((TagStyle) listModel.getElementAt(i)).getName());
			}

		}

		filter.setStyleNames(styleNames);

		filter.setSecondsBefore(((Double) getSecondsBeforeSpinner().getValue()).doubleValue());
		filter.setSecondsAfter(((Double) getSecondsAfterSpinner().getValue()).doubleValue());

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {

		super.validateDialog(model, errors);

		getChooseTagPanel().validatePanel(errors);

		if (!errors.hasErrors()) {
			TagBasedAtomFilter filter = new TagBasedAtomFilter();
			fillFilterFromDialog(filter);

			try {
				filter.initialize();
			} catch (Throwable t) {
				logger.error("Filter failed to initialize", t);
				errors.reject("error.tagBasedAtomFilter.failedToInitialize");
			}
		}


	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return TagBasedAtomFilter.class.isAssignableFrom(clazz);
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		getMarkerStyleCellRenderer().setTagIconProducer(tagIconProducer);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}

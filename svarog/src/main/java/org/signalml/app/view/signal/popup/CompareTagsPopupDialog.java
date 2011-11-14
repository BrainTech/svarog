/* CompareTagsPopupDialog.java created 2007-11-13
 *
 */

package org.signalml.app.view.signal.popup;

import static org.signalml.app.SvarogApplication._;
import static org.signalml.app.SvarogApplication._R;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.document.TagDocument;
import org.signalml.app.model.TagComparisonDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.TitledCrossBorder;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.tag.comparison.TagComparisonDialog;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;

/** CompareTagsPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CompareTagsPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;

	private SignalView signalView;

	private JRadioButton compareOffRadio;
	private JRadioButton compareOnRadio;

	private JCheckBox[] checkBoxes;
	private TagDocument[] tagDocuments;

	private AnalyzeAction analyzeAction;
	private JButton analyzeButton;

	private TagComparisonDialog tagComparisonDialog;

	public CompareTagsPopupDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	public SignalView getSignalView() {
		return signalView;
	}

	public void setSignalView(SignalView signalView) {
		this.signalView = signalView;
	}

	public TagComparisonDialog getTagComparisonDialog() {
		return tagComparisonDialog;
	}

	public void setTagComparisonDialog(TagComparisonDialog tagComparisonDialog) {
		this.tagComparisonDialog = tagComparisonDialog;
	}

	@Override
	public JComponent createInterface() {

		List<TagDocument> tags = signalView.getDocument().getTagDocuments();
		int cnt = tags.size();
		if (cnt < 2) {
			throw new SanityCheckException("Too few tag documents");
		}
		tagDocuments = new TagDocument[cnt];
		tags.toArray(tagDocuments);

		analyzeAction = new AnalyzeAction();

		JPanel graphicalComparePanel = new JPanel();
		graphicalComparePanel.setLayout(new BoxLayout(graphicalComparePanel, BoxLayout.Y_AXIS));
		graphicalComparePanel.setBorder(new CompoundBorder(
		                                        new TitledCrossBorder(_("Graphical comparison"), true),
		                                        new EmptyBorder(3,3,3,3)
		                                ));

		compareOnRadio = new JRadioButton(_("Comparison mode enabled"));
		compareOffRadio = new JRadioButton(_("Comparison mode disabled"));

		ButtonGroup compareGroup = new ButtonGroup();
		compareGroup.add(compareOffRadio);
		compareGroup.add(compareOnRadio);

		compareOnRadio.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				fillModelFromDialog(getCurrentModel());
			}

		});

		graphicalComparePanel.add(compareOffRadio);
		graphicalComparePanel.add(compareOnRadio);

		JPanel chooseTagsPanel = new JPanel();

		chooseTagsPanel.setLayout(new GridLayout(cnt, 1, 3, 3));

		chooseTagsPanel.setBorder(new CompoundBorder(
		                                  new TitledBorder(_("Choose tag documents (2 required)")),
		                                  new EmptyBorder(3,3,3,3)
		                          ));

		checkBoxes = new JCheckBox[cnt];
		CheckBoxCoordinator checkBoxCoordinator = new CheckBoxCoordinator();

		for (int i=0; i<cnt; i++) {
			final String message;
			if (tagDocuments[i].getBackingFile() == null) {
				message = _R("New tag {0}", tagDocuments[i].getName());
			} else {
				message = tagDocuments[i].getName();
			}
			checkBoxes[i] = new JCheckBox(message);
			chooseTagsPanel.add(checkBoxes[i]);
			checkBoxes[i].addItemListener(checkBoxCoordinator);
		}

		checkBoxes[0].setSelected(true);
		checkBoxes[1].setSelected(true);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBorder(new EmptyBorder(3,0,0,0));

		analyzeButton = new JButton(analyzeAction);
		buttonPanel.add(analyzeButton, BorderLayout.CENTER);

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(graphicalComparePanel, BorderLayout.NORTH);
		interfacePanel.add(chooseTagsPanel, BorderLayout.CENTER);
		interfacePanel.add(buttonPanel, BorderLayout.SOUTH);

		Dimension size = chooseTagsPanel.getPreferredSize();
		if (size.width < 270) {
			size.width = 270;
		}
		chooseTagsPanel.setPreferredSize(size);

		return interfacePanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		TagDocument[] comparedDocuments = signalView.getComparedTags();

		if (comparedDocuments == null) {

			compareOffRadio.setSelected(true);

		} else {

			for (int i=0; i<tagDocuments.length; i++) {
				if (tagDocuments[i] == comparedDocuments[0] || tagDocuments[i] == comparedDocuments[1]) {
					checkBoxes[i].setSelected(true); // any deselection is handled by coordinator
				}
			}

			compareOnRadio.setSelected(true);

		}

	}

	@Override
	public void fillModelFromDialog(Object model) {

		if (compareOnRadio.isSelected()) {

			TagDocument[] tags = new TagDocument[2];
			int cnt = 0;

			for (int i=0; i<checkBoxes.length; i++) {
				if (checkBoxes[i].isSelected()) {
					if (cnt > 1) {
						throw new SanityCheckException("More than 2 tags selected");
					}
					tags[cnt] = tagDocuments[i];
					cnt++;
				}
			}

			if (cnt == 2) {
				signalView.setComparedTags(tags[0], tags[1]);
			} else {
				signalView.setComparedTags(null, null);
			}

		} else {
			signalView.setComparedTags(null, null);
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return true;
	}

	@Override
	public boolean isFormClickApproving() {
		return true;
	}

	private class CheckBoxCoordinator implements ItemListener {

		private JCheckBox penultimateSelection;
		private JCheckBox ultimateSelection;

		private boolean lock = false;

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (penultimateSelection != null) {
					try {
						lock = true;
						penultimateSelection.setSelected(false); // deselect the tag selected first
					} finally {
						lock = false;
					}
				}
				penultimateSelection = ultimateSelection;
				ultimateSelection = (JCheckBox) e.getSource();
			} else { // compensate for manual deselection
				if (!lock) {
					Object source = e.getSource();
					if (source == penultimateSelection) {
						penultimateSelection = null;
					} else if (source == ultimateSelection) {
						ultimateSelection = penultimateSelection;
						penultimateSelection = null;
					}
				}
			}

			if (!lock) {

				int selCnt = 0;
				for (JCheckBox checkBox : checkBoxes) {
					if (checkBox.isSelected()) {
						selCnt++;
					}
				}

				boolean canCompare = (selCnt == 2);   // enable if two tags selected

				analyzeAction.setEnabled(canCompare);
				if (!canCompare) {
					compareOffRadio.setSelected(true);
				}
				compareOffRadio.setEnabled(canCompare);
				compareOnRadio.setEnabled(canCompare);

				if (canCompare && compareOnRadio.isSelected()) {
					fillModelFromDialog(getCurrentModel());
				}

			}


		}

	}

	protected class AnalyzeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AnalyzeAction() {
			super(_("Analyze tag differences"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/analyze.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Show analytical comparison summary"));
		}

		public void actionPerformed(ActionEvent ev) {

			TagComparisonDescriptor descriptor = new TagComparisonDescriptor(signalView.getDocument());
			descriptor.setTagIconProducer(signalView.getTagIconProducer());

			int selCnt = 0;
			int i;
			for (i=0; i<checkBoxes.length; i++) {
				if (checkBoxes[i].isSelected()) {
					if (selCnt == 0) {
						descriptor.setTopTagDocument(tagDocuments[i]);
					} else if (selCnt == 1) {
						descriptor.setBottomTagDocument(tagDocuments[i]);
						break;
					}
					selCnt++;
				}
			}

			// temporarily hide the popup
			setVisible(false);

			tagComparisonDialog.showDialog(descriptor, true);

			TagDocument topDocument = descriptor.getTopTagDocument();
			TagDocument bottomDocument = descriptor.getBottomTagDocument();

			for (i=0; i<tagDocuments.length; i++) {
				if (tagDocuments[i] == topDocument || tagDocuments[i] == bottomDocument) {
					checkBoxes[i].setSelected(true);
				}
			}

			// restore the popup
			setVisible(true);

		}

	}

}

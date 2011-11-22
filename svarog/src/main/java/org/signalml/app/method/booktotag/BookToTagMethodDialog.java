/* BookToTagMethodDialog.java created 2007-10-22
 *
 */

package org.signalml.app.method.booktotag;

import static org.signalml.app.SvarogI18n._;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.LinkedHashSet;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.action.util.ListSelectAllAction;
import org.signalml.app.action.util.ListSelectInvertAction;
import org.signalml.app.action.util.ListSelectNoneAction;
import org.signalml.app.util.IconUtils;
import org.signalml.domain.book.StandardBook;
import org.signalml.method.booktotag.BookToTagData;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.validation.Errors;

/** BookToTagMethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookToTagMethodDialog extends org.signalml.app.view.dialog.AbstractSvarogDialog  {

	private static final long serialVersionUID = 1L;

	private JList channelList;
	private JScrollPane channelScrollPane;

	private JButton channelSelectAllButton;
	private JButton channelSelectInvertButton;
	private JButton channelSelectNoneButton;

	private String[] channels;

	private JCheckBox makePageTagsCheckBox;
	private JCheckBox makeBlockTagsCheckBox;
	private JCheckBox makeChannelTagsCheckBox;

	public BookToTagMethodDialog(Window window) {
		super(window,true);
	}

	@Override
	protected void initialize() {
		setTitle(_("Configure book to tag"));
		setIconImage(IconUtils.loadClassPathImage(BookToTagMethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel channelPanel = new JPanel(new BorderLayout());
		channelPanel.setBorder(new CompoundBorder(
		                               new TitledBorder(_("Choose channels to include")),
		                               new EmptyBorder(3,3,3,3)
		                       ));

		JPanel channelButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 3));
		channelButtonPanel.add(getChannelSelectAllButton());
		channelButtonPanel.add(getChannelSelectNoneButton());
		channelButtonPanel.add(getChannelSelectInvertButton());

		channelPanel.add(getChannelScrollPane(), BorderLayout.CENTER);
		channelPanel.add(channelButtonPanel, BorderLayout.SOUTH);

		JPanel tagTypesPanel = new JPanel();
		tagTypesPanel.setLayout(new BoxLayout(tagTypesPanel, BoxLayout.Y_AXIS));
		tagTypesPanel.setBorder(new CompoundBorder(
		                                new TitledBorder(_("Choose tag types")),
		                                new EmptyBorder(3,3,3,3)
		                        ));

		tagTypesPanel.add(getMakePageTagsCheckBox());
		tagTypesPanel.add(getMakeBlockTagsCheckBox());
		tagTypesPanel.add(getMakeChannelTagsCheckBox());

		interfacePanel.add(channelPanel, BorderLayout.CENTER);
		interfacePanel.add(tagTypesPanel, BorderLayout.SOUTH);

		return interfacePanel;

	}

	public JList getChannelList() {
		if (channelList == null) {

			channelList = new JList();

			channelList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		}
		return channelList;
	}

	public JScrollPane getChannelScrollPane() {
		if (channelScrollPane == null) {
			channelScrollPane = new JScrollPane(getChannelList());
			channelScrollPane.setPreferredSize(new Dimension(300,200));
		}
		return channelScrollPane;
	}

	public JButton getChannelSelectAllButton() {
		if (channelSelectAllButton == null) {
			channelSelectAllButton = new JButton(new ListSelectAllAction( getChannelList()));
		}
		return channelSelectAllButton;
	}

	public JButton getChannelSelectNoneButton() {
		if (channelSelectNoneButton == null) {
			channelSelectNoneButton = new JButton(new ListSelectNoneAction( getChannelList()));
		}
		return channelSelectNoneButton;
	}

	public JButton getChannelSelectInvertButton() {
		if (channelSelectInvertButton == null) {
			channelSelectInvertButton = new JButton(new ListSelectInvertAction( getChannelList()));
		}
		return channelSelectInvertButton;
	}

	public String[] getChannels() {
		return channels;
	}

	public void setChannels(String[] channels) {
		if (this.channels != channels) {

			this.channels = channels;

			DefaultListModel listModel = new DefaultListModel();
			for (int i=0; i<channels.length; i++) {
				listModel.addElement(channels[i]);
			}

			JList list = getChannelList();
			list.setModel(listModel);
			list.clearSelection();

		}
	}

	public JCheckBox getMakePageTagsCheckBox() {
		if (makePageTagsCheckBox == null) {
			makePageTagsCheckBox = new JCheckBox(_("Create page tags"));
		}
		return makePageTagsCheckBox;
	}

	public JCheckBox getMakeBlockTagsCheckBox() {
		if (makeBlockTagsCheckBox == null) {
			makeBlockTagsCheckBox = new JCheckBox(_("Create block tags"));
		}
		return makeBlockTagsCheckBox;
	}

	public JCheckBox getMakeChannelTagsCheckBox() {
		if (makeChannelTagsCheckBox == null) {
			makeChannelTagsCheckBox = new JCheckBox(_("Create channel tags"));
		}
		return makeChannelTagsCheckBox;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		BookToTagData data = (BookToTagData) model;

		StandardBook book = data.getBook();
		int channelCount = book.getChannelCount();
		String[] labels = new String[channelCount];
		int i;
		String label;
		for (i=0; i<channelCount; i++) {
			label = book.getChannelLabel(i);
			if (label == null || label.isEmpty()) {
				label = "L" + (i+1);
			}
			labels[i] = label;
		}

		setChannels(labels);

		JList list = getChannelList();
		list.clearSelection();

		LinkedHashSet<Integer> channelSet = data.getChannels();

		if (channelSet != null) {

			for (i=0; i<channelCount; i++) {
				if (channelSet.contains(i)) {
					list.addSelectionInterval(i, i);
				}
			}

		}

		getMakePageTagsCheckBox().setSelected(data.isMakePageTags());
		getMakeBlockTagsCheckBox().setSelected(data.isMakeBlockTags());
		getMakeChannelTagsCheckBox().setSelected(data.isMakeChannelTags());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		BookToTagData data = (BookToTagData) model;

		JList list = getChannelList();

		LinkedHashSet<Integer> channelSet = data.getChannels();
		if (channelSet != null) {
			channelSet.clear();
		} else {
			channelSet = new LinkedHashSet<Integer>();
		}

		for (int i=0; i<channels.length; i++) {

			if (list.isSelectedIndex(i)) {
				channelSet.add(i);
			}

		}

		data.setMakePageTags(getMakePageTagsCheckBox().isSelected());
		data.setMakeBlockTags(getMakeBlockTagsCheckBox().isSelected());
		data.setMakeChannelTags(getMakeChannelTagsCheckBox().isSelected());

	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		if (getChannelList().isSelectionEmpty()) {
			errors.rejectValue("channels", _("Select at least one channel"));
		}

		if (!getMakePageTagsCheckBox().isSelected() && !getMakeBlockTagsCheckBox().isSelected() && !getMakeChannelTagsCheckBox().isSelected()) {
			errors.reject(_("Select at least one tag type"));
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return BookToTagData.class.isAssignableFrom(clazz);
	}

}

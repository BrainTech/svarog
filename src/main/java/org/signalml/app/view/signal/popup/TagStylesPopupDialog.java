/* TagStylesPopupDialog.java created 2007-10-13
 *
 */

package org.signalml.app.view.signal.popup;

import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.app.view.tag.TagStyleToggleButton;
import org.signalml.app.view.tag.TagStyleToolBar;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.view.AbstractPopupDialog;
import org.springframework.context.support.MessageSourceAccessor;

/** TagStylesPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStylesPopupDialog extends AbstractPopupDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private ButtonGroup buttonGroup;
	private Map<TagStyle,TagStyleToggleButton> styleToButtonMap;
	private Map<ButtonModel,TagStyle> buttonToStyleMap;

	private TagStyleToolBar tagStyleToolBar;

	public TagStylesPopupDialog(MessageSourceAccessor messageSource, TagStyleToolBar tagStyleToolBar, Window w, boolean isModal) {
		super(messageSource, w, isModal);
		this.tagStyleToolBar = tagStyleToolBar;
	}

	@Override
	public JComponent createInterface() {

		buttonGroup = new ButtonGroup();
		styleToButtonMap = new HashMap<TagStyle, TagStyleToggleButton>();
		buttonToStyleMap = new HashMap<ButtonModel, TagStyle>();

		JPanel interfacePanel = new JPanel();

		StyledTagSet tagSet = tagStyleToolBar.getTagSet();
		SignalSelectionType type = tagStyleToolBar.getType();
		TagIconProducer tagIconProducer = tagStyleToolBar.getTagIconProducer();

		Collection<TagStyle> styles;
		if (type == null) {
			styles = tagSet.getStyles();
		} else {
			styles = tagSet.getStyles(type);
		}

		int cnt = styles.size();
		int cols = (int) Math.ceil(Math.sqrt(cnt));
		int rows = (int) Math.ceil(((double) cnt) / cols);

		interfacePanel.setLayout(new GridLayout(rows, cols, 1, 1));
		interfacePanel.setBorder(new EmptyBorder(3,3,3,3));

		TagStyleToggleButton toolButton;
		for (TagStyle style : styles) {
			toolButton = new TagStyleToggleButton(style,tagIconProducer);
			buttonGroup.add(toolButton);
			styleToButtonMap.put(style, toolButton);
			buttonToStyleMap.put(toolButton.getModel(), style);
			interfacePanel.add(toolButton);
			toolButton.addActionListener(this);
		}

		return interfacePanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		if (tagStyleToolBar.isTagSelectionOnButtonClick()) {
			buttonGroup.clearSelection();
		} else {
			TagStyle style = tagStyleToolBar.getSelectedStyle();
			if (style != null) {
				TagStyleToggleButton toolButton = styleToButtonMap.get(style);
				if (toolButton != null) {
					buttonGroup.setSelected(toolButton.getModel(), true);
				}
			}
		}
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		if (!tagStyleToolBar.isTagSelectionOnButtonClick()) {
			ButtonModel buttonModel = buttonGroup.getSelection();
			if (buttonModel != null) {
				TagStyle style = buttonToStyleMap.get(buttonModel);
				if (style != null) {
					tagStyleToolBar.setSelectedStyle(style);
				}
			}
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
	public void actionPerformed(ActionEvent e) {
		if (tagStyleToolBar.isTagSelectionOnButtonClick()) {
			tagStyleToolBar.getTagSelectionAction().actionPerformed(new ActionEvent(e.getSource(), 0, "tagSelection"));
			getCancelAction().actionPerformed(new ActionEvent(this, 0, "cancel"));
		}
		getOkAction().actionPerformed(new ActionEvent(this, 0, "ok"));
	}

}

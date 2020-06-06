/* TagStyleToolBar.java created 2007-10-13
 *
 */
package org.signalml.app.view.tag;

import com.alee.laf.toolbar.WebToolBar;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import org.apache.log4j.Logger;
import org.signalml.app.action.tag.TagSelectionAction;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagStyleEvent;
import org.signalml.domain.tag.TagStyleListener;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * TagStyleToolBar
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 * Sp. z o.o.
 */
public class TagStyleToolBar extends WebToolBar implements TagStyleListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(TagStyleToolBar.class);

	private StyledTagSet tagSet;
	private SignalSelectionType type;

	private ButtonGroup buttonGroup;

	private List<TagStyleToggleButton> buttonList;
	private Map<TagStyle, TagStyleToggleButton> styleToButtonMap;
	private Map<ButtonModel, TagStyle> buttonToStyleMap;

	private TagEraserToggleButton tagEraserToggleButton;

	private TagIconProducer tagIconProducer;

	private TagSelectionAction tagSelectionAction;
	private boolean tagSelectionOnButtonClick = false;
	private ActionListener buttonClickListener;

	public TagStyleToolBar(StyledTagSet tagSet, SignalSelectionType type, TagIconProducer tagIconProducer, TagSelectionAction tagSelectionAction) {

		super();
		setOrientation(WebToolBar.VERTICAL);
		setFloatable(false);
		setBorder(null);
		this.tagSelectionAction = tagSelectionAction;
		this.tagSet = tagSet;
		this.type = type;

		this.tagIconProducer = tagIconProducer;

		buttonClickListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tagSelectionOnButtonClick) {
					TagStyleToolBar.this.tagSelectionAction.actionPerformed(e);
					buttonGroup.clearSelection();
				}
			}
		};

		buttonGroup = new ButtonGroup();

		buttonList = new LinkedList<>();

		styleToButtonMap = new HashMap<>();
		buttonToStyleMap = new HashMap<>();

		tagEraserToggleButton = new TagEraserToggleButton();
		tagEraserToggleButton.addActionListener(buttonClickListener);
		buttonGroup.add(tagEraserToggleButton);
		add(tagEraserToggleButton);
		add(Box.createVerticalStrut(3));

		Collection<TagStyle> styles;
		if (type == null) {
			styles = tagSet.getListOfStyles();
		} else {
			styles = tagSet.getStyles(type);
		}
		for (TagStyle style : styles) {
			addTagStyle(style);
		}

		tagSet.addTagStyleListener(this);

		if (styles.size() > 0) {
			buttonList.get(0).setSelected(true);
		}

	}

	public void close() {
		tagSet.removeTagStyleListener(this);
		removeAll();
		tagSet = null;
	}

	public void clearSelection() {
		buttonGroup.clearSelection();
	}

	public void assertAnySelection() {
		if (buttonGroup.getSelection() == null) {
			if (!buttonList.isEmpty()) {
				buttonGroup.setSelected(buttonList.get(0).getModel(), true);
			} else {
				buttonGroup.setSelected(tagEraserToggleButton.getModel(), true);
			}
		}
	}

	public StyledTagSet getTagSet() {
		return tagSet;
	}

	public SignalSelectionType getType() {
		return type;
	}

	public boolean isTagSelectionOnButtonClick() {
		return tagSelectionOnButtonClick;
	}

	public void setTagSelectionOnButtonClick(boolean tagSelectionOnButtonClick) {
		this.tagSelectionOnButtonClick = tagSelectionOnButtonClick;
	}

	public TagSelectionAction getTagSelectionAction() {
		return tagSelectionAction;
	}

	public TagStyle getSelectedStyle() {
		ButtonModel model = buttonGroup.getSelection();
		if (model != null) {
			return buttonToStyleMap.get(model);
		}
		return null;
	}

	public void setSelectedStyle(TagStyle style) {
		if (style != null) {
			TagStyleToggleButton toolButton = styleToButtonMap.get(style);
			if (toolButton != null) {
				buttonGroup.setSelected(toolButton.getModel(), true);
			}
		} else {
			buttonGroup.setSelected(tagEraserToggleButton.getModel(), true);
		}
	}

	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	private void addTagStyle(TagStyle style) {

		TagStyleToggleButton toolButton = new TagStyleToggleButton(style, tagIconProducer);
		toolButton.addActionListener(buttonClickListener);
		buttonGroup.add(toolButton);
		buttonList.add(toolButton);
		styleToButtonMap.put(style, toolButton);
		buttonToStyleMap.put(toolButton.getModel(), style);

		this.add(toolButton);
	}

	private void removeTagStyle(TagStyle style) {

		TagStyleToggleButton toolButton = styleToButtonMap.get(style);
		if (toolButton != null) {
			buttonGroup.remove(toolButton);
			buttonList.remove(toolButton);
			styleToButtonMap.remove(style);
			buttonToStyleMap.remove(toolButton.getModel());

			this.remove(toolButton);
		}

	}

	@Override
	public void tagStyleAdded(TagStyleEvent e) {
		TagStyle style = e.getTagStyle();
		if (type == null || type.equals(style.getType())) {
			addTagStyle(style);
		}
	}

	@Override
	public void tagStyleChanged(TagStyleEvent e) {
		TagStyle style = e.getTagStyle();
		if (type == null || type.equals(style.getType())) {
			TagStyleToggleButton toolButton = styleToButtonMap.get(style);
			if (toolButton != null) {
				toolButton.reset();
			}
			tagIconProducer.reset(style);
		}
	}

	@Override
	public void tagStyleRemoved(TagStyleEvent e) {
		TagStyle style = e.getTagStyle();
		if (type == null || type.equals(style.getType())) {
			removeTagStyle(style);
			tagIconProducer.reset(style);
		}
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(28, tagEraserToggleButton.getSize().height + 3);
	}
}

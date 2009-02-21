/* TagStyleToolBar.java created 2007-10-13
 * 
 */

package org.signalml.app.view.tag;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractSignalMLAction;
import org.signalml.app.action.TagSelectionAction;
import org.signalml.app.view.signal.popup.TagStylesPopupDialog;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagStyle;
import org.signalml.domain.tag.TagStyleEvent;
import org.signalml.domain.tag.TagStyleListener;
import org.springframework.context.support.MessageSourceAccessor;

/** TagStyleToolBar
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleToolBar extends JToolBar implements TagStyleListener {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(TagStyleToolBar.class);
	
	private StyledTagSet tagSet;
	private SignalSelectionType type;
	
	private ButtonGroup buttonGroup;
	
	private List<TagStyleToggleButton> buttonList;
	private Map<TagStyle,TagStyleToggleButton> styleToButtonMap;
	private Map<ButtonModel,TagStyle> buttonToStyleMap;
	
	private TagEraserToggleButton tagEraserToggleButton;
	
	private JButton showAllButton;
	
	private MessageSourceAccessor messageSource;
	
	private TagStylesPopupDialog tagStylesPopupDialog = null;
	private TagIconProducer tagIconProducer;

	private TagSelectionAction tagSelectionAction;
	private boolean tagSelectionOnButtonClick = false;
	private ActionListener buttonClickListener;
	
	public TagStyleToolBar( StyledTagSet tagSet, SignalSelectionType type, MessageSourceAccessor messageSource, TagIconProducer tagIconProducer, TagSelectionAction tagSelectionAction ) {
		
		super(JToolBar.VERTICAL);
		setFloatable(false);
		setBorder(null);
		
		this.messageSource = messageSource;
		this.tagSelectionAction = tagSelectionAction;
		this.tagSet = tagSet;
		this.type = type;
		
		this.tagIconProducer = tagIconProducer;
		
		buttonClickListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if( tagSelectionOnButtonClick ) {
					TagStyleToolBar.this.tagSelectionAction.actionPerformed(e);
					buttonGroup.clearSelection();
				}
			}			
		};
		
		buttonGroup = new ButtonGroup();
		
		buttonList = new LinkedList<TagStyleToggleButton>();
		
		styleToButtonMap = new HashMap<TagStyle, TagStyleToggleButton>();
		buttonToStyleMap = new HashMap<ButtonModel, TagStyle>();
		
		tagEraserToggleButton = new TagEraserToggleButton(messageSource);
		tagEraserToggleButton.addActionListener(buttonClickListener);
		buttonGroup.add(tagEraserToggleButton);
		add(tagEraserToggleButton);				
		add( Box.createVerticalStrut(3) );
		
		showAllButton = new JButton( new ShowAllAction() ); 
		add( Box.createVerticalStrut(5) );
		add( Box.createVerticalGlue() );
		add( showAllButton );
		
		Collection<TagStyle> styles;
		if( type == null ) {
			styles = tagSet.getStyles();
		} else {
			styles = tagSet.getStyles(type);
		}
		for( TagStyle style : styles ) {
			addTagStyle(style);
		}
		
		addComponentListener(new ToolBarListener());
		tagSet.addTagStyleListener(this);
		
		if( styles.size() > 0 ) {
			buttonList.get(0).setSelected(true);
		}
		
	}
	
	public void close() {
		tagSet.removeTagStyleListener(this);
		tagStylesPopupDialog = null;
		removeAll();
		tagSet = null;
	}
	
	public void clearSelection() {
		buttonGroup.clearSelection();
	}
	
	public void assertAnySelection() {
		if( buttonGroup.getSelection() == null ) {
			if( !buttonList.isEmpty() ) {
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
		if( model != null ) {
			return buttonToStyleMap.get(model);
		}
		return null;
	}
		
	public void setSelectedStyle(TagStyle style) {
		if( style != null ) {
			TagStyleToggleButton toolButton = styleToButtonMap.get(style);
			if( toolButton != null ) {
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

		this.add(toolButton,getComponentCount()-3); // insert before the spacer, glue and show all button 
		
		resetVisibility();
		
		tagStylesPopupDialog = null;
				
	}

	private void removeTagStyle(TagStyle style) {

		TagStyleToggleButton toolButton = styleToButtonMap.get(style);
		if( toolButton != null ) {
			buttonGroup.remove(toolButton);
			buttonList.remove(toolButton);
			styleToButtonMap.remove(style);
			buttonToStyleMap.remove(toolButton.getModel());
			
			this.remove(toolButton);
			
			resetVisibility();
			
			tagStylesPopupDialog = null;			
		}		
		
	}
	
	private void resetVisibility() {
		if( isDisplayable() ) {
			int minHeight = getMinimumSize().height;
			
			Dimension size = getSize();
			int usedHeight = minHeight;
			int reqHeight;
			Iterator<TagStyleToggleButton> it = buttonList.iterator();
			TagStyleToggleButton toolButton;
			boolean somethingHidden = false;
			while( it.hasNext() ) {
				toolButton = it.next();
				reqHeight = toolButton.getPreferredSize().height + 1;
				if( (usedHeight + reqHeight) > size.height ) {
					toolButton.setVisible(false);
					somethingHidden = true;
					break;
				}
				toolButton.setVisible(true);
				usedHeight += reqHeight;
			}
			showAllButton.setEnabled(somethingHidden);
			while( it.hasNext() ) {
				it.next().setVisible(false);
			}			
		}
	}

	@Override
	public void tagStyleAdded(TagStyleEvent e) {
		TagStyle style = e.getTagStyle();
		if( type == null || type.equals(style.getType()) ) {
			addTagStyle(style);
		}
	}

	@Override
	public void tagStyleChanged(TagStyleEvent e) {
		TagStyle style = e.getTagStyle();
		if( type == null || type.equals(style.getType()) ) {
			TagStyleToggleButton toolButton = styleToButtonMap.get(style);
			if( toolButton != null ) {
				tagStylesPopupDialog = null;
				toolButton.reset();
			}
			tagIconProducer.reset(style);
		}
	}

	@Override
	public void tagStyleRemoved(TagStyleEvent e) {
		TagStyle style = e.getTagStyle();
		if( type == null || type.equals(style.getType()) ) {
			removeTagStyle(style);
			tagIconProducer.reset(style);
		}
	}	
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(28, tagEraserToggleButton.getSize().height + 3 + 5 + 1 + showAllButton.getSize().height);
	}
	
	private class ToolBarListener extends ComponentAdapter {
		
		@Override
		public void componentResized(ComponentEvent e) {
			resetVisibility();
		}
		
	}
	
	private class ShowAllAction extends AbstractSignalMLAction {

		private static final long serialVersionUID = 1L;

		public ShowAllAction() {
			super(TagStyleToolBar.this.messageSource);
			setIconPath("org/signalml/app/icon/showallstyles.png");
			setToolTip("action.showAllTagStylesToolTip");
		}
				
		@Override
		public void actionPerformed(ActionEvent e) {
			
			Container ancestor = getTopLevelAncestor();
			
			if( tagStylesPopupDialog == null ) {
				tagStylesPopupDialog = new TagStylesPopupDialog(messageSource, TagStyleToolBar.this, (Window) ancestor, true);
				tagStylesPopupDialog.initializeNow();
			}

			Point containerLocation = ancestor.getLocation();
			Point location = SwingUtilities.convertPoint(showAllButton, new Point(0,0), ancestor);
			Dimension size = tagStylesPopupDialog.getSize();
			location.translate(containerLocation.x, containerLocation.y+showAllButton.getSize().height-size.height);
			tagStylesPopupDialog.setLocation(location);
			
			tagStylesPopupDialog.showDialog(null);
			
		}
				
	}

}

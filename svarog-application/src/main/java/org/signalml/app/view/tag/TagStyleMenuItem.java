/* TagStyleMenuItem.java created 2007-10-16
 * 
 */

package org.signalml.app.view.tag;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

import org.signalml.domain.tag.TagStyle;

/** TagStyleMenuItem
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleMenuItem extends JMenuItem implements TagStyleSelector {

	private static final long serialVersionUID = 1L;

	private TagStyle tagStyle;
	private TagIconProducer tagIconProducer; 
	
	public TagStyleMenuItem(Action action, TagStyle tagStyle, TagIconProducer tagIconProducer) {
		super(action);
		this.tagStyle = tagStyle;
		this.tagIconProducer = tagIconProducer;
		setText(tagStyle.getDescriptionOrName());
	}
		
	@Override
	public Icon getIcon() {
		return tagIconProducer.getIcon(tagStyle);			
	}
			
	@Override
	public TagStyle getTagStyle() {
		return tagStyle;
	}
				
}

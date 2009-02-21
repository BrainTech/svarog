/* AbstractViewerTree.java created 2007-10-15
 * 
 */

package org.signalml.app.view;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** AbstractViewerTree
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractViewerTree extends JTree {

	private static final long serialVersionUID = 1L;

	protected MessageSourceAccessor messageSource;

	public AbstractViewerTree(TreeModel model, MessageSourceAccessor messageSource) {
		super((TreeModel) null);
		this.messageSource = messageSource;
		setModel(model);
		setRootVisible(false);
		setShowsRootHandles(true);
		setEditable(false);
		setExpandsSelectedPaths(true);
	}

	@Override
	public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if( messageSource != null ) {
			if( value instanceof MessageSourceResolvable ) {
				return messageSource.getMessage((MessageSourceResolvable) value);
			}
			String s = value.toString();
			if( s != null && s.length() > 0 ) {
				return messageSource.getMessage(s);
			}
		}
		return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
	}
				
}

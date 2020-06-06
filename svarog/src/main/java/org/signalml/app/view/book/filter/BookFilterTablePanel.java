/* BookFilterListPanel.java created 2008-03-04
 *
 */
package org.signalml.app.view.book.filter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.common.dialogs.AbstractPresetDialog;
import org.signalml.domain.book.filter.AbstractAtomFilter;
import org.signalml.domain.book.filter.AtomFilterChain;
import org.signalml.domain.book.filter.DelegatingAtomFilter;
import org.signalml.domain.book.filter.ParameterRangeAtomFilter;
import org.signalml.domain.book.filter.TagBasedAtomFilter;
import org.signalml.exception.SanityCheckException;

/** BookFilterListPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookFilterTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(BookFilterTablePanel.class);

	private ParameterRangeFilterDialog parameterFilterDialog;
	private TagBasedFilterDialog tagBasedFilterDialog;
	private DelegatingFilterDialog delegatingFilterDialog;

	private AtomFilterChainTableModel tableModel;
	private AtomFilterChainTable table;
	private JScrollPane scrollPane;

	private AddParameterFilterAction addParameterFilterAction;
	private AddTagBasedFilterAction addTagBasedFilterAction;
	private AddDelegatingFilterAction addDelegatingFilterAction;

	private EditFilterAction editFilterAction;
	private RemoveFilterAction removeFilterAction;

	private JButton editFilterButton;
	private JButton removeFilterButton;

	private JButton addParameterFilterButton;
	private JButton addTagBasedFilterButton;
	private JButton addDelegatingFilterButton;

	private AtomFilterChain chain;
	private AbstractPresetDialog container;

	public BookFilterTablePanel(AbstractPresetDialog container) {
		super();
		this.container = container;

		addParameterFilterAction = new AddParameterFilterAction();
		addTagBasedFilterAction = new AddTagBasedFilterAction();
		addDelegatingFilterAction = new AddDelegatingFilterAction();

		editFilterAction = new EditFilterAction();
		removeFilterAction = new RemoveFilterAction();

		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout(5,5));

		setBorder(new CompoundBorder(
					  new TitledBorder(_("Filter list")),
					  new EmptyBorder(3,3,3,3)
				  ));

		JPanel rightPanel = new JPanel(new BorderLayout());

		JPanel topButtonPanel = new JPanel(new GridLayout(2,1,3,3));

		topButtonPanel.add(getEditFilterButton());
		topButtonPanel.add(getRemoveFilterButton());

		rightPanel.add(topButtonPanel, BorderLayout.NORTH);
		rightPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new GridLayout(1,3,3,3));

		bottomPanel.add(getAddParameterFilterButton());
		bottomPanel.add(getAddTagBasedFilterButton());
		bottomPanel.add(getAddDelegatingFilterButton());

		JPanel leftPanel = new JPanel(new BorderLayout(5,5));

		leftPanel.add(getScrollPane(), BorderLayout.CENTER);
		leftPanel.add(bottomPanel, BorderLayout.SOUTH);

		add(leftPanel, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.EAST);

	}

	public AtomFilterChainTableModel getTableModel() {
		if (tableModel == null) {
			tableModel = new AtomFilterChainTableModel();
		}
		return tableModel;
	}

	public AtomFilterChainTable getTable() {
		if (table == null) {
			table = new AtomFilterChainTable(getTableModel());
			table.addMouseListener(new FiltersTableMouseHandler());
			table.setPopupMenuProvider(new FiltersTablePopupProvider());

			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					boolean selected = (table.getSelectedRow() >= 0);

					editFilterAction.setEnabled(selected);
					removeFilterAction.setEnabled(selected);

				}

			});

		}
		return table;
	}

	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getTable());
			scrollPane.setPreferredSize(new Dimension(600,300));
		}
		return scrollPane;
	}

	public JButton getAddParameterFilterButton() {
		if (addParameterFilterButton == null) {
			addParameterFilterButton = new JButton(addParameterFilterAction);
		}
		return addParameterFilterButton;
	}

	public JButton getAddTagBasedFilterButton() {
		if (addTagBasedFilterButton == null) {
			addTagBasedFilterButton = new JButton(addTagBasedFilterAction);
		}
		return addTagBasedFilterButton;
	}

	public JButton getAddDelegatingFilterButton() {
		if (addDelegatingFilterButton == null) {
			addDelegatingFilterButton = new JButton(addDelegatingFilterAction);
		}
		return addDelegatingFilterButton;
	}

	public JButton getEditFilterButton() {
		if (editFilterButton == null) {
			editFilterButton = new JButton(editFilterAction);
		}
		return editFilterButton;
	}

	public JButton getRemoveFilterButton() {
		if (removeFilterButton == null) {
			removeFilterButton = new JButton(removeFilterAction);
		}
		return removeFilterButton;
	}

	public void fillPanelFromModel(AtomFilterChain chain) {

		this.chain = chain;

		getTableModel().setChain(chain);
		getTable().clearSelection();

	}

	public void fillModelFromPanel(AtomFilterChain chain) {

		// nothing to do

	}

	public void validatePanel(ValidationErrors errors) {

		// nothing to do

	}

	public ParameterRangeFilterDialog getParameterFilterDialog() {
		return parameterFilterDialog;
	}

	public void setParameterFilterDialog(ParameterRangeFilterDialog parameterFilterDialog) {
		this.parameterFilterDialog = parameterFilterDialog;
	}

	public TagBasedFilterDialog getTagBasedFilterDialog() {
		return tagBasedFilterDialog;
	}

	public void setTagBasedFilterDialog(TagBasedFilterDialog tagBasedFilterDialog) {
		this.tagBasedFilterDialog = tagBasedFilterDialog;
	}

	public DelegatingFilterDialog getDelegatingFilterDialog() {
		return delegatingFilterDialog;
	}

	public void setDelegatingFilterDialog(DelegatingFilterDialog delegatingFilterDialog) {
		this.delegatingFilterDialog = delegatingFilterDialog;
	}

	protected class AddParameterFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddParameterFilterAction() {
			super(_("Add parameter filter"));
			putValue(AbstractAction.SHORT_DESCRIPTION, _("Add a filter that filters based on parameter ranges"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addparameterfilter.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (chain == null) {
				return;
			}

			ParameterRangeAtomFilter filter = new ParameterRangeAtomFilter();
			filter.setName(_("New parameter range filter"));

			boolean ok = getParameterFilterDialog().showDialog(filter, true);
			if (!ok) {
				return;
			}

			filter.setEnabled(true);

			int index = chain.addFilter(filter);
			getTableModel().onInsert(index);

		}

	}

	protected class AddTagBasedFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddTagBasedFilterAction() {
			super(_("Add tag based filter"));
			putValue(AbstractAction.SHORT_DESCRIPTION, _("Add a filter that filters based on a signal tag"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addtagbasedfilter.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (chain == null) {
				return;
			}

			TagBasedAtomFilter filter = new TagBasedAtomFilter();
			filter.setName(_("New tag based filter"));

			boolean ok = getTagBasedFilterDialog().showDialog(filter, true);
			if (!ok) {
				return;
			}

			filter.setEnabled(true);

			int index = chain.addFilter(filter);
			getTableModel().onInsert(index);

		}

	}

	protected class AddDelegatingFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddDelegatingFilterAction() {
			super(_("Add custom filter"));
			putValue(AbstractAction.SHORT_DESCRIPTION, _("Add a filter that uses a custom class to determine matching atoms"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/adddelegatingfilter.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (chain == null) {
				return;
			}

			DelegatingAtomFilter filter = new DelegatingAtomFilter();
			filter.setName(_("New custom filter"));

			boolean ok = getDelegatingFilterDialog().showDialog(filter, true);
			if (!ok) {
				return;
			}

			filter.setEnabled(true);

			int index = chain.addFilter(filter);
			getTableModel().onInsert(index);

		}

	}

	protected class EditFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditFilterAction() {
			super(_("Configure"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/editfilter.png"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			if (chain == null) {
				return;
			}

			int selectedRow = getTable().getSelectedRow();
			if (selectedRow < 0) {
				return;
			}

			AbstractAtomFilter filter = chain.getFilterAt(selectedRow);

			if (filter instanceof ParameterRangeAtomFilter) {

				boolean ok = getParameterFilterDialog().showDialog(filter, true);
				if (!ok) {
					return;
				}

			} else if (filter instanceof TagBasedAtomFilter) {

				boolean ok = getTagBasedFilterDialog().showDialog(filter, true);
				if (!ok) {
					return;
				}

			} else if (filter instanceof DelegatingAtomFilter) {

				boolean ok = getDelegatingFilterDialog().showDialog(filter, true);
				if (!ok) {
					return;
				}

			} else {
				throw new SanityCheckException("Unsupported filter type [" + filter.getClass().getName() + "]");
			}
			getTableModel().onUpdate(selectedRow);

		}

	}

	protected class RemoveFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveFilterAction() {
			super(_("Remove"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removefilter.png"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			if (chain == null) {
				return;
			}

			int selectedRow = getTable().getSelectedRow();
			if (selectedRow < 0) {
				return;
			}

			chain.removeFilterAt(selectedRow);
			getTableModel().onDelete(selectedRow);

		}

	}

	protected class FiltersTablePopupProvider implements TablePopupMenuProvider {

		private JPopupMenu popupMenu;

		@Override
		public JPopupMenu getPopupMenu(int col, int row) {
			return getDefaultPopupMenu();
		}

		@Override
		public JPopupMenu getPopupMenu() {
			return getPopupMenu(-1,-1);
		}

		private JPopupMenu getDefaultPopupMenu() {

			if (popupMenu == null) {

				popupMenu = new JPopupMenu();

				popupMenu.add(editFilterAction);
				popupMenu.addSeparator();
				popupMenu.add(removeFilterAction);

			}

			return popupMenu;

		}

	}

	protected class FiltersTableMouseHandler extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			AtomFilterChainTable table = (AtomFilterChainTable) e.getSource();
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0) {
				int selRow = table.rowAtPoint(e.getPoint());
				if (selRow >= 0) {
					editFilterAction.actionPerformed(new ActionEvent(table,0,"edit"));
				}
			}
		}

	}

}

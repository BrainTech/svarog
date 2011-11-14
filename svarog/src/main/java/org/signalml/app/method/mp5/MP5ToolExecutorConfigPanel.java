/* ArtifactToolWorkingDirectoryConfigPanel.java created 2008-02-08
 *
 */
package org.signalml.app.method.mp5;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.exception.SanityCheckException;
import org.signalml.method.mp5.MP5Executor;
import org.signalml.method.mp5.MP5LocalProcessExecutor;

/** ArtifactToolWorkingDirectoryConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ToolExecutorConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MP5ExecutorManager executorManager;

	private MP5LocalExecutorDialog localExecutorDialog;

	private AddLocalExecutorAction addLocalExecutorAction;
	private ConfigureExecutorAction configureExecutorAction;
	private RemoveExecutorAction removeExecutorAction;
	private MakeDefaultAction makeDefaultAction;

	private JButton addLocalExecutorButton;
	private JButton configureExecutorButton;
	private JButton removeExecutorButton;
	private JButton makeDefaultButton;

	private MP5ExecutorListCellRenderer executorListCellRenderer;
	private JList executorList;
	private JScrollPane executorScrollPane;

	public MP5ToolExecutorConfigPanel(MP5ExecutorManager executorManager) {
		super();
		this.executorManager = executorManager;

		getExecutorListCellRenderer().setDefaultExecutor(executorManager.getDefaultExecutor());

		executorManager.addMP5ExecutorManagerListener(new MP5ExecutorManagerAdapter() {

			@Override
			public void defaultExecutorChanged(MP5ExecutorManagerEvent ev) {
				getExecutorListCellRenderer().setDefaultExecutor(MP5ToolExecutorConfigPanel.this.executorManager.getDefaultExecutor());
				getExecutorList().repaint();
			}

		});

		initialize();

	}

	private void initialize() {

		addLocalExecutorAction = new AddLocalExecutorAction();
		configureExecutorAction = new ConfigureExecutorAction();
		removeExecutorAction = new RemoveExecutorAction();
		makeDefaultAction = new MakeDefaultAction();

		setLayout(new BorderLayout(3,3));

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(_("MP5 executors")),
		        new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		SwingUtils.makeButtonsSameSize(new JButton[] { getMakeDefaultButton(), getConfigureExecutorButton(), getRemoveExecutorButton(), getAddLocalExecutorButton() });

		buttonPanel.add(getMakeDefaultButton());
		buttonPanel.add(Box.createVerticalStrut(3));
		buttonPanel.add(getConfigureExecutorButton());
		buttonPanel.add(Box.createVerticalStrut(3));
		buttonPanel.add(getRemoveExecutorButton());
		buttonPanel.add(Box.createVerticalStrut(10));
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(getAddLocalExecutorButton());
		buttonPanel.add(Box.createVerticalStrut(3));

		add(getExecutorScrollPane(), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.EAST);

	}

	public MP5ExecutorListCellRenderer getExecutorListCellRenderer() {
		if (executorListCellRenderer == null) {
			executorListCellRenderer = new MP5ExecutorListCellRenderer();
		}
		return executorListCellRenderer;
	}

	public JList getExecutorList() {
		if (executorList == null) {
			executorList = new JList(new MP5ExecutorListModel(executorManager));
			executorList.setCellRenderer(getExecutorListCellRenderer());
			executorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			executorList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					boolean selected = !executorList.isSelectionEmpty();

					makeDefaultAction.setEnabled(selected);
					configureExecutorAction.setEnabled(selected);
					removeExecutorAction.setEnabled(selected);

				}

			});

			executorList.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0) {

						int index = executorList.getSelectedIndex();
						if (index < 0) {
							return;
						}

						configureExecutorAction.actionPerformed(new ActionEvent(this, 0, "configure"));

					}
				}

			});

		}
		return executorList;
	}

	public JScrollPane getExecutorScrollPane() {
		if (executorScrollPane == null) {
			executorScrollPane = new JScrollPane(getExecutorList());
			executorScrollPane.setPreferredSize(new Dimension(300,200));
		}
		return executorScrollPane;
	}

	public JButton getAddLocalExecutorButton() {
		if (addLocalExecutorButton == null) {
			addLocalExecutorButton = new JButton(addLocalExecutorAction);
		}
		return addLocalExecutorButton;
	}

	public JButton getConfigureExecutorButton() {
		if (configureExecutorButton == null) {
			configureExecutorButton = new JButton(configureExecutorAction);
		}
		return configureExecutorButton;
	}

	public JButton getRemoveExecutorButton() {
		if (removeExecutorButton == null) {
			removeExecutorButton = new JButton(removeExecutorAction);
		}
		return removeExecutorButton;
	}


	public JButton getMakeDefaultButton() {
		if (makeDefaultButton == null) {
			makeDefaultButton = new JButton(makeDefaultAction);
		}
		return makeDefaultButton;
	}

	public MP5LocalExecutorDialog getLocalExecutorDialog() {
		return localExecutorDialog;
	}

	public void setLocalExecutorDialog(MP5LocalExecutorDialog localExecutorDialog) {
		this.localExecutorDialog = localExecutorDialog;
	}

	protected class AddLocalExecutorAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddLocalExecutorAction() {
			super(_("Add local"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addlocal.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Add local process MP5 executor"));
		}

		public void actionPerformed(ActionEvent ev) {

			MP5LocalProcessExecutor executor = new MP5LocalProcessExecutor();

			boolean ok = localExecutorDialog.showDialog(executor, true);
			if (!ok) {
				return;
			}

			executorManager.addExecutor(executor);

		}

	}

	protected class RemoveExecutorAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveExecutorAction() {
			super(_("Remove"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/remove.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Remove selected MP5 executor"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			int index = getExecutorList().getSelectedIndex();
			if (index < 0) {
				return;
			}

			executorManager.removeExecutor(index);

		}

	}

	protected class ConfigureExecutorAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ConfigureExecutorAction() {
			super(_("Configure"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/configure.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Configure selected MP5 executor"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			int index = getExecutorList().getSelectedIndex();
			if (index < 0) {
				return;
			}

			MP5Executor executor = executorManager.getExecutorAt(index);
			if (executor == null) {
				return;
			}

			boolean ok;
			if (executor instanceof MP5LocalProcessExecutor) {
				ok = localExecutorDialog.showDialog(executor, true);
			} else {
				throw new SanityCheckException("Unsupported executor type [" + executor.getClass() + "]");
			}

			if (!ok) {
				return;
			}

			executorManager.setExecutorAt(index, executor);

		}

	}

	protected class MakeDefaultAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public MakeDefaultAction() {
			super(_("Make default"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/makedefault.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Make selecte executor default"));
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {

			int index = getExecutorList().getSelectedIndex();
			if (index < 0) {
				return;
			}

			MP5Executor executor = executorManager.getExecutorAt(index);
			if (executor == null) {
				return;
			}

			executorManager.setDefaultExecutor(executor);

		}

	}

}

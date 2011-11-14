/* EditMontagePanel.java created 2007-10-24
 *
 */
package org.signalml.app.view.montage;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.model.SeriousWarningDescriptor;
import org.signalml.app.montage.MontageTableModel;
import org.signalml.app.montage.SourceMontageTableModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.SeriousWarningDialog;
import org.signalml.app.view.montage.dnd.MontageWasteBasket;
import org.signalml.app.view.montage.dnd.MontageWasteBasketTransferHandler;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageChannel;
import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceChannel;

/**
 * The panel which allows to:
 * <ul>
 * <li>select which {@link SourceChannel source channels} should be included
 * in the Montage,</li>
 * <li>change the label of both source and {@link MontageChannel montage}
 * channels,</li>
 * <li>change the order of montage channels.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageChannelsPanel extends JPanel {

	/**
	 * the default serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the logger
	 */
	protected static final Logger logger = Logger.getLogger(MontageChannelsPanel.class);

	/**
	 * the dialog which is shown when the user tries to
	 * {@link ClearMontageAction clear} the {@link Montage montage} or
	 * {@link RemoveSourceChannelAction remove} a
	 * {@link SourceChannel source channel}
	 */
	private SeriousWarningDialog seriousWarningDialog;

	/**
	 * the {@link Montage montage} that is the model for this panel
	 */
	private Montage montage;
	
	/**
	 * {@code true} if the {@link SignalDocument signal document} exists
	 * {@code false} otherwise 
	 */
	private boolean signalBound;

	/**
	 * the {@link SourceMontageTableModel model} for
	 * {@link #getSourceMontageTable()}
	 */
	private SourceMontageTableModel sourceMontageTableModel;
	
	/**
	 * the {@link MontageTableModel model} for {@link #montageTable}
	 */
	private MontageTableModel montageTableModel;

	/**
	 * the {@link SourceMontageTable table} which allows to edit the labels and
	 * functions of {@link SourceChannel source channels}
	 */
	private SourceMontageTable sourceMontageTable;
	
	/**
	 * the {@link MontageTable table} which allows to edit the labels and
	 * the order (the indexes) of {@link MontageChannel montage channels}
	 */
	private MontageTable montageTable;

	/**
	 * the scroll pane with the {@link #getSourceMontageTable() source montage
	 * table}
	 */
	private JScrollPane sourceScrollPane;
	
	/**
	 * the scroll pane with the {@link #getMontageTable() montage table}
	 */
	private JScrollPane scrollPane;

	/**
	 * the panel with the {@link SourceMontageTable} enclosed in the
	 * {@link #getSourceScrollPane() scroll pane} and the panel
	 * with two buttons ({@link #addSourceChannelButton} and
	 * {@link #removeSourceChannelButton}) below the table
	 */
	private JPanel sourceTablePanel;
	
	/**
	 * the panel with the {@link MontageTable} enclosed in the
	 * {@link #getScrollPane() scroll pane} and the panel
	 * with one button ({@link #clearMontageButton}) below the table
	 */
	private JPanel montageTablePanel;

	/**
	 * the {@link MoveUpAction action} which moves the selected
	 * {@link MontageChannel montage channel} up in the
	 * {@link #getMontageTable() montage table} (changes its index from
	 * {@code i} to {@code i - 1} and changes the index of the above channel
	 * from {@code i - 1} to {@code i})
	 */
	private Action moveUpAction;
	
	/**
	 * the {@link MoveDownAction action} which moves the selected
	 * {@link MontageChannel montage channel} down in the
	 * {@link #getMontageTable() montage table} (changes its index
	 * from {@code i} to {@code i + 1} and changes the index of the
	 * channel below from {@code i + 1} to {@code i})
	 */
	private Action moveDownAction;
	
	/**
	 * the {@link AddChannelsAction action} which adds the selected
	 * {@link SourceChannel source channel} to the target {@link Montage
	 * montage}
	 */
	private Action addChannelsAction;
	
	/**
	 * the {@link RemoveChannelsAction action} which removes the selected
	 * {@link MontageChannel montage channel} from the {@link Montage
	 * montage}
	 */
	private Action removeChannelsAction;

	private Action addZeroChannelAction;
	private Action addOneChannelAction;
	
	/**
	 * the {@link RemoveSourceChannelAction action} which
	 * {@link Montage#removeSourceChannel() removes} the {@link SourceChannel
	 * source channel} from the {@link Montage montage}
	 */
	private Action removeSourceChannelAction;
	
	/**
	 * the {@link ClearMontageAction action} which {@link Montage#reset()
	 * removes} all {@link MontageChannel montage channels} from the
	 * {@link Montage montage}
	 */
	private Action clearMontageAction;

	/**
	 * the button for the {@link #moveUpAction}
	 */
	private JButton moveUpButton;
	
	/**
	 * the button for the {@link #moveDownAction}
	 */
	private JButton moveDownButton;

	/**
	 * the button for the {@link #addChannelsAction}
	 */
	private JButton addChannelsButton;
	
	/**
	 * the button for the {@link #removeChannelsAction}
	 */
	private JButton removeChannelsButton;

	private JButton addZeroChannelButton;
	private JButton addOneChannelButton;
	
	/**
	 * the button for the {@link #removeSourceChannelAction}
	 */
	private JButton removeSourceChannelButton;

	/**
	 * the button for the {@link #clearMontageAction}
	 */
	private JButton clearMontageButton;

	/**
	 * Constructor. Sets the source of messages and {@link #initialize()
	 * initializes} this panel.
	 */
	public MontageChannelsPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel:
	 * <ul>
	 * <li>creates all actions and assigns them to buttons,</li>
	 * <li>creates the center panel with buttons - {@link #moveUpButton},
	 * {@link #moveDownButton}, {@link #addChannelsButton} and
	 * {@link #removeChannelsButton} - and the waste basked,</li>
	 * <li>adds subpanels to this panel (from left to right):
	 * <ul>
	 * <li>the {@link #sourceTablePanel panel} with the
	 * {@link SourceMontageTable},</li>
	 * <li>the center panel as described above,</li>
	 * <li>the {@link #montageTablePanel panel} with the
	 * {@link MontageTable}.</li></ul></li></ul>
	 */
	private void initialize() {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		moveUpAction = new MoveUpAction();
		moveDownAction = new MoveDownAction();

		addChannelsAction = new AddChannelsAction();
		removeChannelsAction = new RemoveChannelsAction();

		addZeroChannelAction = new AddSourceChannelAction(0, _("ZEROs channel"));
		addOneChannelAction = new AddSourceChannelAction(1, _("ONEs channel"));
		removeSourceChannelAction = new RemoveSourceChannelAction();

		clearMontageAction = new ClearMontageAction();

		moveUpButton = new JButton(moveUpAction);
		moveUpButton.setHorizontalAlignment(SwingConstants.LEFT);

		moveDownButton = new JButton(moveDownAction);
		moveDownButton.setHorizontalAlignment(SwingConstants.LEFT);

		addChannelsButton = new JButton(addChannelsAction);
		addChannelsButton.setHorizontalAlignment(SwingConstants.LEFT);

		removeChannelsButton = new JButton(removeChannelsAction);
		removeChannelsButton.setHorizontalAlignment(SwingConstants.LEFT);

		SwingUtils.makeButtonsSameSize(new JButton[] { moveUpButton, moveDownButton, addChannelsButton, removeChannelsButton });

		addZeroChannelButton = new JButton(addZeroChannelAction);
		addOneChannelButton = new JButton(addOneChannelAction);
		removeSourceChannelButton = new JButton(removeSourceChannelAction);

		clearMontageButton = new JButton(clearMontageAction);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setBorder(new EmptyBorder(getSourceTablePanel().getInsets().top,3,0,3));

		buttonPanel.add(moveUpButton);
		buttonPanel.add(Box.createVerticalStrut(3));
		buttonPanel.add(moveDownButton);
		buttonPanel.add(Box.createVerticalStrut(6));
		buttonPanel.add(addChannelsButton);
		buttonPanel.add(Box.createVerticalStrut(3));
		buttonPanel.add(removeChannelsButton);
		buttonPanel.add(Box.createVerticalStrut(6));
		buttonPanel.add(Box.createVerticalGlue());

		MontageWasteBasket montageWasteBasket = new MontageWasteBasket();
		MontageWasteBasketTransferHandler montageWasteBasketTransferHandler = new MontageWasteBasketTransferHandler(getMontageTable());
		montageWasteBasket.setTransferHandler(montageWasteBasketTransferHandler);
		montageWasteBasket.setAlignmentX(Component.CENTER_ALIGNMENT);
		montageWasteBasket.setToolTipText(_("Drop target channels here to delete them"));

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(buttonPanel, BorderLayout.CENTER);
		centerPanel.add(montageWasteBasket, BorderLayout.SOUTH);

		add(getSourceTablePanel());
		add(centerPanel);
		add(getMontageTablePanel());

	}

	/**
	 * Gets the dialog which is shown when the user tries to.
	 * 
	 * @return the dialog which is shown when the user tries to
	 */
	public SeriousWarningDialog getSeriousWarningDialog() {
		return seriousWarningDialog;
	}

	/**
	 * Sets the dialog which is shown when the user tries to.
	 * 
	 * @param seriousWarningDialog
	 *            the new dialog which is shown when the user tries to
	 */
	public void setSeriousWarningDialog(SeriousWarningDialog seriousWarningDialog) {
		this.seriousWarningDialog = seriousWarningDialog;
	}

	/**
	 * Enables or disables buttons (actions). The buttons are enabled if:
	 * <ul>
	 * <li>{@link #addChannelsButton} - if there are some selected rows in
	 * {@link #getSourceMontageTable() source montage table},</li>
	 * <li>{@link #removeChannelsButton} - if there are some selected rows in
	 * {@link #getMontageTable() montage table},</li>
	 * <li>{@link #moveUpButton} and {@link #moveDownButton} - if the selection
	 * in the montage table is continuous.</li>
	 * </ul>
	 */
	private void setEnableds() {

		boolean sourceSelection = !getSourceMontageTable().getSelectionModel().isSelectionEmpty();
		boolean montageSelection = !getMontageTable().getSelectionModel().isSelectionEmpty();
		boolean montageSelectionContiguous = false;
		if (montageSelection) {

			montageSelectionContiguous = true;

			ListSelectionModel selectionModel = getMontageTable().getSelectionModel();

			int firstRow = selectionModel.getMinSelectionIndex();
			int lastRow = selectionModel.getMaxSelectionIndex();

			for (int i=firstRow+1; i<lastRow; i++) {
				if (!selectionModel.isSelectedIndex(i)) {
					montageSelectionContiguous = false;
					break;
				}
			}

		}

		moveUpAction.setEnabled(montageSelectionContiguous);
		moveDownAction.setEnabled(montageSelectionContiguous);

		addChannelsAction.setEnabled(sourceSelection);
		removeChannelsAction.setEnabled(montageSelection);

	}

	/**
	 * Gets the panel with the {@link SourceMontageTable} enclosed in the.
	 * 
	 * @return the panel with the {@link SourceMontageTable} enclosed in the
	 */
	public JPanel getSourceTablePanel() {
		if (sourceTablePanel == null) {

			sourceTablePanel = new JPanel(new BorderLayout());
			CompoundBorder border = new CompoundBorder(
			        new TitledBorder(_("Source montage")),
			        new EmptyBorder(3,3,3,3)
			);
			sourceTablePanel.setBorder(border);

			JPanel sourceButtonPanel = new JPanel();
			sourceButtonPanel.setLayout(new BoxLayout(sourceButtonPanel, BoxLayout.X_AXIS));
			sourceButtonPanel.setBorder(new EmptyBorder(5,0,0,0));

			sourceButtonPanel.add(addZeroChannelButton);
			sourceButtonPanel.add(Box.createHorizontalStrut(5));
			sourceButtonPanel.add(addOneChannelButton);
			sourceButtonPanel.add(Box.createHorizontalStrut(5));
			sourceButtonPanel.add(removeSourceChannelButton);
			sourceButtonPanel.add(Box.createHorizontalGlue());

			sourceTablePanel.add(getSourceScrollPane(), BorderLayout.CENTER);
			sourceTablePanel.add(sourceButtonPanel, BorderLayout.SOUTH);

		}
		return sourceTablePanel;
	}

	/**
	 * Gets the panel with the {@link MontageTable} enclosed in the.
	 * 
	 * @return the panel with the {@link MontageTable} enclosed in the
	 */
	public JPanel getMontageTablePanel() {
		if (montageTablePanel == null) {

			montageTablePanel = new JPanel();
			montageTablePanel.setLayout(new BorderLayout());
			CompoundBorder border = new CompoundBorder(
			        new TitledBorder(_("Target montage")),
			        new EmptyBorder(3,3,3,3)
			);
			montageTablePanel.setBorder(border);

			JPanel montageButtonPanel = new JPanel();
			montageButtonPanel.setLayout(new BoxLayout(montageButtonPanel, BoxLayout.X_AXIS));
			montageButtonPanel.setBorder(new EmptyBorder(5,0,0,0));

			montageButtonPanel.add(Box.createHorizontalGlue());
			montageButtonPanel.add(clearMontageButton);

			montageTablePanel.add(getScrollPane(), BorderLayout.CENTER);
			montageTablePanel.add(montageButtonPanel, BorderLayout.SOUTH);

		}
		return montageTablePanel;
	}

	/**
	 * Gets the {@link SourceMontageTableModel model} for.
	 * 
	 * @return the {@link SourceMontageTableModel model} for
	 */
	public SourceMontageTableModel getSourceMontageTableModel() {
		if (sourceMontageTableModel == null) {
			sourceMontageTableModel = new SourceMontageTableModel();
		}
		return sourceMontageTableModel;
	}

	/**
	 * Gets the {@link MontageTableModel model} for {@link #montageTable}.
	 * 
	 * @return the {@link MontageTableModel model} for {@link #montageTable}
	 */
	public MontageTableModel getMontageTableModel() {
		if (montageTableModel == null) {
			montageTableModel = new MontageTableModel();
		}
		return montageTableModel;
	}

	/**
	 * Gets the {@link MontageTable table} which allows to edit the labels and
	 * the order (the indexes) of {@link MontageChannel montage channels}.
	 * 
	 * @return the {@link MontageTable table} which allows to edit the labels
	 *         and the order (the indexes) of {@link MontageChannel montage
	 *         channels}
	 */
	public MontageTable getMontageTable() {
		if (montageTable == null) {
			montageTable = new MontageTable(getMontageTableModel(), false);
			montageTable.setPopupMenuProvider(new MontageTablePopupProvider());

			montageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (!getMontageTable().getSelectionModel().isSelectionEmpty()) {
						getSourceMontageTable().clearSelection();
					}
					setEnableds();
				}

			});

		}
		return montageTable;
	}

	/**
	 * Gets the {@link SourceMontageTable table} which allows to edit the labels
	 * and functions of {@link SourceChannel source channels}.
	 * 
	 * @return the {@link SourceMontageTable table} which allows to edit the
	 *         labels and functions of {@link SourceChannel source channels}
	 */
	public SourceMontageTable getSourceMontageTable() {
		if (sourceMontageTable == null) {
			sourceMontageTable = new SourceMontageTable(getSourceMontageTableModel());
			sourceMontageTable.setPopupMenuProvider(new SourceMontageTablePopupProvider());

			sourceMontageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (!getSourceMontageTable().getSelectionModel().isSelectionEmpty()) {
						getMontageTable().clearSelection();
					}
					setEnableds();
				}

			});

		}
		return sourceMontageTable;
	}

	/**
	 * Gets the scroll pane with the {@link #getMontageTable() montage table}.
	 * 
	 * @return the scroll pane with the {@link #getMontageTable() montage table}
	 */
	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getMontageTable());
		}
		return scrollPane;
	}

	/**
	 * Gets the scroll pane with the {@link #getSourceMontageTable() source
	 * montage table}.
	 * 
	 * @return the scroll pane with the {@link #getSourceMontageTable() source
	 *         montage table}
	 */
	public JScrollPane getSourceScrollPane() {
		if (sourceScrollPane == null) {
			sourceScrollPane = new JScrollPane(getSourceMontageTable());
		}
		return sourceScrollPane;
	}

	/**
	 * Gets the {@link Montage montage} that is the model for this panel.
	 * 
	 * @return the {@link Montage montage} that is the model for this panel
	 */
	public Montage getMontage() {
		return montage;
	}

	/**
	 * Sets the {@link Montage montage} that is the model for this panel.
	 * 
	 * @param montage
	 *            the new {@link Montage montage} that is the model for this
	 *            panel
	 */
	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			this.montage = montage;
			getSourceMontageTableModel().setMontage(montage);
			getMontageTableModel().setMontage(montage);
			getSourceMontageTable().clearSelection();
			getMontageTable().clearSelection();
			setEnableds();
		}
	}

	/**
	 * Checks if is signal bound.
	 * 
	 * @return true, if is signal bound
	 */
	public boolean isSignalBound() {
		return signalBound;
	}

	/**
	 * Sets the signal bound.
	 * 
	 * @param signalBound
	 *            the new signal bound
	 */
	public void setSignalBound(boolean signalBound) {
		if (this.signalBound != signalBound) {
			this.signalBound = signalBound;
		}
	}

	/**
	 * The action which that which rows in the
	 * {@link MontageChannelsPanel#getMontageTable() montage table} are
	 * selected and {@link Montage#moveMontageChannelRange(int, int, int)
	 * moves} them one position backward.
	 */
	protected class MoveUpAction extends AbstractAction {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the text and the icon of this button.
		 */
		public MoveUpAction() {
			super(_("Move up"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/moveup.png"));
		}

		/**
		 * When the action is performed checks which rows in the
		 * {@link MontageChannelsPanel#getMontageTable() montage table} are
		 * selected and {@link Montage#moveMontageChannelRange(int, int, int)
		 * moves} them one position backward.
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			ListSelectionModel selectionModel = getMontageTable().getSelectionModel();

			int firstRow = selectionModel.getMinSelectionIndex();
			if (firstRow <= 0) {
				return;
			}
			int lastRow = selectionModel.getMaxSelectionIndex();

			for (int i=firstRow+1; i<lastRow; i++) {
				if (!selectionModel.isSelectedIndex(i)) {
					// non-contiguous
					return;
				}
			}

			montage.moveMontageChannelRange(firstRow, 1+lastRow-firstRow, -1);
			selectionModel.setSelectionInterval(firstRow-1, lastRow-1);

		}

	}

	/**
	 * The action which that which rows in the
	 * {@link MontageChannelsPanel#getMontageTable() montage table} are
	 * selected and {@link Montage#moveMontageChannelRange(int, int, int)
	 * moves} them one position forward.
	 */
	protected class MoveDownAction extends AbstractAction {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the text and the icon of this button.
		 */
		public MoveDownAction() {
			super(_("Move down"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/movedown.png"));
		}

		/**
		 * When the action is performed checks which rows in the
		 * {@link MontageChannelsPanel#getMontageTable() montage table} are
		 * selected and {@link Montage#moveMontageChannelRange(int, int, int)
		 * moves} them one position forward.
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			ListSelectionModel selectionModel = getMontageTable().getSelectionModel();
			int firstRow = selectionModel.getMinSelectionIndex();
			if (firstRow < 0) {
				return;
			}
			int lastRow = selectionModel.getMaxSelectionIndex();
			if (lastRow >= getMontageTable().getRowCount()-1) {
				return;
			}

			for (int i=firstRow+1; i<lastRow; i++) {
				if (!selectionModel.isSelectedIndex(i)) {
					// non-contiguous
					return;
				}
			}

			montage.moveMontageChannelRange(firstRow, 1+lastRow-firstRow, 1);
			selectionModel.setSelectionInterval(firstRow+1, lastRow+1);

		}

	}

	/**
	 * Action that checks which rows in the
	 * {@link MontageChannelsPanel#getSourceMontageTable() source montage
	 * table} are selected and {@link Montage#addMontageChannels(int[], int)
	 * adds} the new {@link MontageChannel montage channels} (created on
	 * the basis of the {@link SourceChannel source channels} of the
	 * selected indexes) to the {@link Montage montage}.
	 */
	protected class AddChannelsAction extends AbstractAction {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the text and the icon of this button.
		 */
		public AddChannelsAction() {
			super(_("Add channels"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addchannels.png"));
		}

		/**
		 * When the action is performed checks which rows in the
		 * {@link MontageChannelsPanel#getSourceMontageTable() source montage
		 * table} are selected and {@link Montage#addMontageChannels(int[], int)
		 * adds} the new {@link MontageChannel montage channels} (created on
		 * the basis of the {@link SourceChannel source channels} of the
		 * selected indexes) to the {@link Montage montage}.
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			ListSelectionModel selectionModel = getSourceMontageTable().getSelectionModel();

			int firstRow = selectionModel.getMinSelectionIndex();
			if (firstRow < 0) {
				return;
			}
			int lastRow = selectionModel.getMaxSelectionIndex();

			int[] selection = new int[getSourceMontageTable().getRowCount()];
			int cnt = 0;

			for (int i=firstRow; i<=lastRow; i++) {
				if (selectionModel.isSelectedIndex(i)) {
					selection[cnt] = i;
					cnt++;
				}
			}

			selection = Arrays.copyOf(selection, cnt);

			montage.addMontageChannels(selection);

		}

	}

	/**
	 * Action that checks which rows in the
	 * {@link MontageChannelsPanel#getMontageTable() montage table} are
	 * selected and {@link Montage#removeMontageChannels(int[])
	 * removes} them from the {@link Montage montage}.
	 */
	protected class RemoveChannelsAction extends AbstractAction {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the text and the icon of this button.
		 */
		public RemoveChannelsAction() {
			super(_("Remove channels"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removechannels.png"));
		}

		/**
		 * When the action is performed checks which rows in the
		 * {@link MontageChannelsPanel#getMontageTable() montage table} are
		 * selected and {@link Montage#removeMontageChannels(int[])
		 * removes} them from the {@link Montage montage}.
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			ListSelectionModel selectionModel = getMontageTable().getSelectionModel();

			int firstRow = selectionModel.getMinSelectionIndex();
			if (firstRow < 0) {
				return;
			}
			int lastRow = selectionModel.getMaxSelectionIndex();

			int[] selection = new int[getMontageTable().getRowCount()];
			int cnt = 0;

			for (int i=firstRow; i<=lastRow; i++) {
				if (selectionModel.isSelectedIndex(i)) {
					selection[cnt] = i;
					cnt++;
				}
			}

			selection = Arrays.copyOf(selection, cnt);

			montage.removeMontageChannels(selection);

		}

	}

	/**
	 * Action which:<ul>
	 * <li>if the document with the signal exists does nothing,</li>
	 * <li>otherwise
	 * {@link Montage#addSourceChannel(String, org.signalml.domain.montage.Channel)
	 * adds} a new {@link SourceChannel source channel} to the {@link Montage
	 * montage}.</li>
	 * </ul>
	 */
	protected class AddSourceChannelAction extends AbstractAction {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;
		private int channelType;
		/**
		 * Constructor. Sets the text and the icon of this button.
		 */
		public AddSourceChannelAction(int t, String msg) {
			super(msg);
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addsourcechannel.png"));
			this.channelType = t;
		}

		/**
		 * When the action is performed:
		 * <ul>
		 * <li>if the document with the signal exists does nothing,</li>
		 * <li>otherwise
		 * {@link Montage#addSourceChannel(String, org.signalml.domain.montage.Channel)
		 * adds} a new {@link SourceChannel source channel} to the {@link Montage
		 * montage}.</li>
		 * </ul>
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			/*if (signalBound) {
				return;
			}*/

			try {
				if (this.channelType == 0) {
					String lb = montage.getNewSourceChannelLabel(_("ZERO"));
					montage.addSourceChannel(lb, EegChannel.ZERO);
				} else {//assumed ONE
					String lb = montage.getNewSourceChannelLabel(_("ONE"));
					montage.addSourceChannel(lb, EegChannel.ONE);
				}
				
			} catch (MontageException ex) {
				logger.error("Failed to add source channel", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return;
			}

		}

	}

	/**
	 * Action that
	 * <ul>
	 * <li>displays the {@link MontageChannelsPanel#getSeriousWarningDialog()
	 * serious warning dialog},</li>
	 * <li>if the user accepts the action {@link Montage#removeSourceChannel()
	 * removes} the last {@link SourceChannel source channel}
	 * from the {@link MontageChannelsPanel#montage montage}.</li></ul>
	 */
	protected class RemoveSourceChannelAction extends AbstractAction {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the text and the icon of this button.
		 */
		public RemoveSourceChannelAction() {
			super(_("Remove"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removesourcechannel.png"));
		}

		/**
		 * When the action is performed:
		 * <ul>
		 * <li>the {@link MontageChannelsPanel#getSeriousWarningDialog()
		 * serious warning dialog} is displayed,</li>
		 * <li>if the user accepts the action the last {@link SourceChannel
		 * source channel} is {@link Montage#removeSourceChannel() removed}
		 * from the {@link MontageChannelsPanel#montage montage}.</li></ul>
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			int cnt = montage.getSourceChannelCount();
			if (cnt == 0) {
				return;
			}

			if (montage.isSourceChannelInUse(cnt -  1)) {

				String warning =  _("The removed source channel is used either as a primary channel or as a reference. Montage will be altered.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>remove</b> a source channel?");
				SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);

				boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
				if (!ok) {
					return;
				}

			}

			montage.removeSourceChannel();

		}

	}

	/**
	 * 
	 * @author Marcin Szumski
	 *
	 */
	protected class ClearMontageAction extends AbstractAction {

		/**
		 * the default serialization constant
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the text and the icon of this button.
		 */
		public ClearMontageAction() {
			super(_("Clear montage"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/clearmontage.png"));
		}

		/**
		 * When the action is performed:
		 * <ul>
		 * <li>the {@link MontageChannelsPanel#getSeriousWarningDialog()
		 * serious warning dialog} is displayed,</li>
		 * <li>if the user accepts this action all parameters of the
		 * {@link MontageChannelsPanel#montage montage} are
		 * {@link Montage#reset() reseted} (for example all
		 * {@link MontageChannel montage channels} are removed).</li>
		 * </ul>
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			String warning =  _("The montage will be irreversibly lost.<br>&nbsp;<br>There is no undo.<br>&nbsp;<br>Are you sure you wish to <b>clear</b> the montage?");
			SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);

			boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
			if (!ok) {
				return;
			}

			montage.reset();

		}

	}

	/**
	 * The popup menu provider for the {@link SourceMontageTable}.
	 * Returns the popup menu with one element -
	 * {@link MontageChannelsPanel#addChannelsAction}.
	 */
	protected class SourceMontageTablePopupProvider implements TablePopupMenuProvider {

		/**
		 * the popup menu with one element -
		 * {@link MontageChannelsPanel#addChannelsAction}
		 */
		private JPopupMenu popupMenu;

		/* (non-Javadoc)
		 * @see org.signalml.app.view.TablePopupMenuProvider#getPopupMenu(int, int)
		 */
		@Override
		public JPopupMenu getPopupMenu(int col, int row) {
			return getDefaultPopupMenu();
		}

		/* (non-Javadoc)
		 * @see org.signalml.app.view.PopupMenuProvider#getPopupMenu()
		 */
		@Override
		public JPopupMenu getPopupMenu() {
			return getPopupMenu(-1,-1);
		}

		/**
		 * Returns the popup menu with one element -
		 * {@link MontageChannelsPanel#addChannelsAction}.
		 * If the menu doesn't exist it is created.
		 * @return the popup menu with one element -
		 * {@link MontageChannelsPanel#addChannelsAction}
		 */
		private JPopupMenu getDefaultPopupMenu() {

			if (popupMenu == null) {

				popupMenu = new JPopupMenu();

				popupMenu.add(addChannelsAction);

			}

			return popupMenu;

		}

	}

	/**
	 * The popup menu provider for the {@link MontageTable}.
	 * Returns the popup menu with 3 elements:
	 * <ul>
	 * <li>{@link MontageChannelsPanel#moveUpAction},</li>
	 * <li>{@link MontageChannelsPanel#moveDownAction},</li>
	 * <li>{@link MontageChannelsPanel#removeChannelsAction}.</li>
	 * </ul>
	 */
	protected class MontageTablePopupProvider implements TablePopupMenuProvider {

		/**
		 * the popup menu with 3 elements:
		 * <ul>
		 * <li>{@link MontageChannelsPanel#moveUpAction},</li>
		 * <li>{@link MontageChannelsPanel#moveDownAction},</li>
		 * <li>{@link MontageChannelsPanel#removeChannelsAction}.</li>
		 * </ul>
		 */
		private JPopupMenu popupMenu;

		/* (non-Javadoc)
		 * @see org.signalml.app.view.TablePopupMenuProvider#getPopupMenu(int, int)
		 */
		@Override
		public JPopupMenu getPopupMenu(int col, int row) {
			return getDefaultPopupMenu();
		}

		/* (non-Javadoc)
		 * @see org.signalml.app.view.PopupMenuProvider#getPopupMenu()
		 */
		@Override
		public JPopupMenu getPopupMenu() {
			return getPopupMenu(-1,-1);
		}

		/**
		 * Returns the popup menu with 3 elements:
		 * <ul>
		 * <li>{@link MontageChannelsPanel#moveUpAction},</li>
		 * <li>{@link MontageChannelsPanel#moveDownAction},</li>
		 * <li>{@link MontageChannelsPanel#removeChannelsAction}.</li>
		 * </ul>
		 * @return the popup menu with 3 elements:
		 * <ul>
		 * <li>{@link MontageChannelsPanel#moveUpAction},</li>
		 * <li>{@link MontageChannelsPanel#moveDownAction},</li>
		 * <li>{@link MontageChannelsPanel#removeChannelsAction}</li>
		 * </ul>
		 */
		private JPopupMenu getDefaultPopupMenu() {

			if (popupMenu == null) {

				popupMenu = new JPopupMenu();

				popupMenu.add(moveUpAction);
				popupMenu.add(moveDownAction);
				popupMenu.addSeparator();
				popupMenu.add(removeChannelsAction);

			}

			return popupMenu;

		}

	}

}

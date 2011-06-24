/* EditMontagePanel.java created 2007-10-24
 *
 */
package org.signalml.app.view.montage;

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
import org.signalml.domain.montage.ChannelType;
import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.domain.montage.MontageException;
import org.springframework.context.support.MessageSourceAccessor;

/** EditMontagePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageChannelsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageChannelsPanel.class);

	private MessageSourceAccessor messageSource;
	private SeriousWarningDialog seriousWarningDialog;

	private Montage montage;
	private boolean signalBound;

	private SourceMontageTableModel sourceMontageTableModel;
	private MontageTableModel montageTableModel;

	private SourceMontageTable sourceMontageTable;
	private MontageTable montageTable;

	private JScrollPane sourceScrollPane;
	private JScrollPane scrollPane;

	private JPanel sourceTablePanel;
	private JPanel montageTablePanel;

	private Action moveUpAction;
	private Action moveDownAction;
	private Action addChannelsAction;
	private Action addEmptyChannelAction;
	private Action removeChannelsAction;

	private Action addSourceChannelAction;
	private Action removeSourceChannelAction;
	private Action clearMontageAction;

	private JButton moveUpButton;
	private JButton moveDownButton;

	private JButton addChannelsButton;
	private JButton addEmptyChannelButton;
	private JButton removeChannelsButton;

	private JButton addSourceChannelButton;
	private JButton removeSourceChannelButton;

	private JButton clearMontageButton;

	public MontageChannelsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		moveUpAction = new MoveUpAction();
		moveDownAction = new MoveDownAction();

		addChannelsAction = new AddChannelsAction();
		addEmptyChannelAction = new AddEmptyChannelAction();
		removeChannelsAction = new RemoveChannelsAction();

		addSourceChannelAction = new AddSourceChannelAction();
		removeSourceChannelAction = new RemoveSourceChannelAction();

		clearMontageAction = new ClearMontageAction();

		moveUpButton = new JButton(moveUpAction);
		moveUpButton.setHorizontalAlignment(SwingConstants.LEFT);

		moveDownButton = new JButton(moveDownAction);
		moveDownButton.setHorizontalAlignment(SwingConstants.LEFT);

		addChannelsButton = new JButton(addChannelsAction);
		addChannelsButton.setHorizontalAlignment(SwingConstants.LEFT);

		addEmptyChannelButton = new JButton(addEmptyChannelAction);
		addEmptyChannelButton.setHorizontalAlignment(SwingConstants.LEFT);

		removeChannelsButton = new JButton(removeChannelsAction);
		removeChannelsButton.setHorizontalAlignment(SwingConstants.LEFT);

		SwingUtils.makeButtonsSameSize(new JButton[] { moveUpButton, moveDownButton, addChannelsButton, addEmptyChannelButton, removeChannelsButton });

		addSourceChannelButton = new JButton(addSourceChannelAction);
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
		buttonPanel.add(addEmptyChannelButton);
		buttonPanel.add(Box.createVerticalStrut(3));
		buttonPanel.add(removeChannelsButton);
		buttonPanel.add(Box.createVerticalStrut(6));
		buttonPanel.add(Box.createVerticalGlue());

		MontageWasteBasket montageWasteBasket = new MontageWasteBasket();
		MontageWasteBasketTransferHandler montageWasteBasketTransferHandler = new MontageWasteBasketTransferHandler(getMontageTable());
		montageWasteBasket.setTransferHandler(montageWasteBasketTransferHandler);
		montageWasteBasket.setAlignmentX(Component.CENTER_ALIGNMENT);
		montageWasteBasket.setToolTipText(messageSource.getMessage("montageTable.wasteBasketToolTip"));

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(buttonPanel, BorderLayout.CENTER);
		centerPanel.add(montageWasteBasket, BorderLayout.SOUTH);

		add(getSourceTablePanel());
		add(centerPanel);
		add(getMontageTablePanel());

	}

	public SeriousWarningDialog getSeriousWarningDialog() {
		return seriousWarningDialog;
	}

	public void setSeriousWarningDialog(SeriousWarningDialog seriousWarningDialog) {
		this.seriousWarningDialog = seriousWarningDialog;
	}

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
		addEmptyChannelAction.setEnabled(true);
		removeChannelsAction.setEnabled(montageSelection);

	}

	public JPanel getSourceTablePanel() {
		if (sourceTablePanel == null) {

			sourceTablePanel = new JPanel(new BorderLayout());
			CompoundBorder border = new CompoundBorder(
			        new TitledBorder(messageSource.getMessage("sourceMontageTable.title")),
			        new EmptyBorder(3,3,3,3)
			);
			sourceTablePanel.setBorder(border);

			JPanel sourceButtonPanel = new JPanel();
			sourceButtonPanel.setLayout(new BoxLayout(sourceButtonPanel, BoxLayout.X_AXIS));
			sourceButtonPanel.setBorder(new EmptyBorder(5,0,0,0));

			sourceButtonPanel.add(addSourceChannelButton);
			sourceButtonPanel.add(Box.createHorizontalStrut(5));
			sourceButtonPanel.add(removeSourceChannelButton);
			sourceButtonPanel.add(Box.createHorizontalGlue());

			sourceTablePanel.add(getSourceScrollPane(), BorderLayout.CENTER);
			sourceTablePanel.add(sourceButtonPanel, BorderLayout.SOUTH);

		}
		return sourceTablePanel;
	}

	public JPanel getMontageTablePanel() {
		if (montageTablePanel == null) {

			montageTablePanel = new JPanel();
			montageTablePanel.setLayout(new BorderLayout());
			CompoundBorder border = new CompoundBorder(
			        new TitledBorder(messageSource.getMessage("montageTable.title")),
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

	public SourceMontageTableModel getSourceMontageTableModel() {
		if (sourceMontageTableModel == null) {
			sourceMontageTableModel = new SourceMontageTableModel();
			sourceMontageTableModel.setMessageSource(messageSource);
		}
		return sourceMontageTableModel;
	}

	public MontageTableModel getMontageTableModel() {
		if (montageTableModel == null) {
			montageTableModel = new MontageTableModel();
			montageTableModel.setMessageSource(messageSource);
		}
		return montageTableModel;
	}

	public MontageTable getMontageTable() {
		if (montageTable == null) {
			montageTable = new MontageTable(getMontageTableModel(), messageSource, false);
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

	public SourceMontageTable getSourceMontageTable() {
		if (sourceMontageTable == null) {
			sourceMontageTable = new SourceMontageTable(getSourceMontageTableModel(), messageSource);
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

	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getMontageTable());
		}
		return scrollPane;
	}

	public JScrollPane getSourceScrollPane() {
		if (sourceScrollPane == null) {
			sourceScrollPane = new JScrollPane(getSourceMontageTable());
		}
		return sourceScrollPane;
	}

	public Montage getMontage() {
		return montage;
	}

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

	public boolean isSignalBound() {
		return signalBound;
	}

	public void setSignalBound(boolean signalBound) {
		if (this.signalBound != signalBound) {
			this.signalBound = signalBound;
			addSourceChannelAction.setEnabled(!signalBound);
			removeSourceChannelAction.setEnabled(!signalBound);
		}
	}

	protected class MoveUpAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public MoveUpAction() {
			super(messageSource.getMessage("montageTable.moveUp"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/moveup.png"));
		}

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

	protected class MoveDownAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public MoveDownAction() {
			super(messageSource.getMessage("montageTable.moveDown"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/movedown.png"));
		}

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

	protected class AddChannelsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddChannelsAction() {
			super(messageSource.getMessage("montageTable.addChannels"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addchannels.png"));
		}

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

	protected class AddEmptyChannelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddEmptyChannelAction() {
			super(messageSource.getMessage("montageTable.addEmptyChannel"));
			//putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addchannels.png"));
		}

		public void actionPerformed(ActionEvent ev) {
			String lb = montage.getNewMontageChannelLabel("EMPTY");
			try {
				if (montage.getSourceChannelFunctionAt(montage.getSourceChannelCount()-1).getType() != ChannelType.EMPTY)
					montage.addSourceChannel(lb, EegChannel.EMPTY);
				int[] inds = montage.addMontageChannels(montage.getSourceChannelCount()-1, 1);				
			} catch (MontageException ex) {
				logger.error("Failed to add empty channel", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return;
			}

		}

	}

	protected class RemoveChannelsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveChannelsAction() {
			super(messageSource.getMessage("montageTable.removeChannels"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removechannels.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			ListSelectionModel selectionModel = getMontageTable().getSelectionModel();

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

			montage.removeMontageChannels(selection);

		}

	}

	protected class AddSourceChannelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddSourceChannelAction() {
			super(messageSource.getMessage("sourceMontageTable.addChannel"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addsourcechannel.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (signalBound) {
				return;
			}

			try {
				montage.addSourceChannel(montage.getNewSourceChannelLabel(messageSource.getMessage("new")), montage.getSignalTypeConfigurer().genericChannel());
			} catch (MontageException ex) {
				logger.error("Failed to add source channel", ex);
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
				return;
			}

		}

	}

	protected class RemoveSourceChannelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveSourceChannelAction() {
			super(messageSource.getMessage("sourceMontageTable.removeChannel"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removesourcechannel.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (signalBound) {
				return;
			}
			int cnt = montage.getSourceChannelCount();
			if (cnt == 0) {
				return;
			}

			if (montage.isSourceChannelInUse(cnt -  1)) {

				String warning =  messageSource.getMessage("montageTable.onDeleteUsed");
				SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);

				boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
				if (!ok) {
					return;
				}

			}

			montage.removeSourceChannel();

		}

	}

	protected class ClearMontageAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ClearMontageAction() {
			super(messageSource.getMessage("montageTable.clear"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/clearmontage.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			String warning =  messageSource.getMessage("montageTable.onClear");
			SeriousWarningDescriptor descriptor = new SeriousWarningDescriptor(warning, 5);

			boolean ok = getSeriousWarningDialog().showDialog(descriptor, true);
			if (!ok) {
				return;
			}

			montage.reset();

		}

	}

	protected class SourceMontageTablePopupProvider implements TablePopupMenuProvider {

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

				popupMenu.add(addChannelsAction);

			}

			return popupMenu;

		}

	}

	protected class MontageTablePopupProvider implements TablePopupMenuProvider {

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

				popupMenu.add(moveUpAction);
				popupMenu.add(moveDownAction);
				popupMenu.addSeparator();
				popupMenu.add(removeChannelsAction);

			}

			return popupMenu;

		}

	}

}

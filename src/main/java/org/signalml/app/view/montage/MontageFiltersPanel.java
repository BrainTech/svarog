/* MontageFiltersPanel.java created 2008-02-03
 *
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
import org.signalml.app.config.preset.PredefinedTimeDomainSampleFilterPresetManager;
import org.signalml.app.montage.MontageFilterExclusionTableModel;
import org.signalml.app.montage.MontageFiltersTableModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.dialog.SeriousWarningDialog;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.SampleFilterType;
import org.signalml.exception.SanityCheckException;
import org.springframework.context.support.MessageSourceAccessor;

/** MontageFiltersPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageFiltersPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageFiltersPanel.class);

	private MessageSourceAccessor messageSource;
	private SeriousWarningDialog seriousWarningDialog;
	private EditFFTSampleFilterDialog editFFTSampleFilterDialog;
	private EditTimeDomainSampleFilterDialog editTimeDomainSampleFilterDialog;

	private Montage montage;
	private boolean signalBound;
	private float currentSamplingFrequency;

	private JCheckBox filteringEnabledCheckBox;

	private MontageFiltersTableModel filtersTableModel;
	private MontageFiltersTable filtersTable;
	private JScrollPane filtersScrollPane;

	private MontageFilterExclusionTableModel filterExclusionTableModel;
	private MontageFilterExclusionTable filterExclusionTable;
	private JScrollPane filterExclusionScrollPane;

	private ResolvableComboBox timeDomainFilterTypeComboBox;
	private JComboBox fftFilterTypeComboBox;

	private AddTimeDomainFilterAction addTimeDomainFilterAction;
	private AddCustomTimeDomainFilterAction addCustomTimeDomainFilterAction;
	private AddFFTFilterAction addFFTFilterAction;
	private EditFilterAction editFilterAction;
	private RemoveFilterAction removeFilterAction;
	private ClearFilterExclusionAction clearFilterExclusionAction;

	private JButton addTimeDomainFilterButton;
	private JButton addCustomTimeDomainFilterButton;
	private JButton addFFTFilterButton;
	private JButton editFilterButton;
	private JButton removeFilterButton;
	private JButton clearFilterExclusionButton;

	private PredefinedTimeDomainSampleFilterPresetManager predefinedTimeDomainSampleFilterPresetManager;

	public MontageFiltersPanel(MessageSourceAccessor messageSource, PredefinedTimeDomainSampleFilterPresetManager predefinedTimeDomainSampleFilterPresetManager) {
		super();
		this.messageSource = messageSource;
		this.predefinedTimeDomainSampleFilterPresetManager = predefinedTimeDomainSampleFilterPresetManager;
		initialize();
	}

	private void initialize() {

		addTimeDomainFilterAction = new AddTimeDomainFilterAction();
		addCustomTimeDomainFilterAction = new AddCustomTimeDomainFilterAction();
		addFFTFilterAction = new AddFFTFilterAction();
		editFilterAction = new EditFilterAction();
		removeFilterAction = new RemoveFilterAction();
		clearFilterExclusionAction = new ClearFilterExclusionAction();

		editFilterAction.setEnabled(false);
		removeFilterAction.setEnabled(false);

		setLayout(new GridLayout(1, 2, 3, 3));

		JPanel masterSwitchPanel = new JPanel(new BorderLayout(3, 3));

		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("montageFilters.masterSwitchTitle")),
		        new EmptyBorder(3, 3, 3, 3)
		);
		masterSwitchPanel.setBorder(border);

		JLabel filteringEnabledLabel = new JLabel(messageSource.getMessage("montageFilters.filteringEnabled"));

		masterSwitchPanel.add(filteringEnabledLabel, BorderLayout.CENTER);
		masterSwitchPanel.add(getFilteringEnabledCheckBox(), BorderLayout.EAST);

		JPanel filtersTablePanel = new JPanel(new BorderLayout(3, 3));
		border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("montageFilters.filtersTableTitle")),
		        new EmptyBorder(3, 3, 3, 3)
		);
		filtersTablePanel.setBorder(border);

		JPanel filtersTableButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 3));

		filtersTableButtonPanel.add(getEditFilterButton());
		filtersTableButtonPanel.add(getRemoveFilterButton());

		filtersTablePanel.add(getFiltersScrollPane(), BorderLayout.CENTER);
		filtersTablePanel.add(filtersTableButtonPanel, BorderLayout.SOUTH);

		JPanel addTimeDomainFilterPanel = new JPanel(new BorderLayout(3, 3));
		border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("montageFilters.addTimeDomainFilterTitle")),
		        new EmptyBorder(3, 3, 3, 3)
		);
		addTimeDomainFilterPanel.setBorder(border);

		SwingUtils.makeButtonsSameSize(new JButton[] {getAddTimeDomainFilterButton(), getAddFFTFilterButton(), getAddCustomTimeDomainFilterButton()});

		addTimeDomainFilterPanel.add(getTimeDomainFilterTypeComboBox(), BorderLayout.CENTER);
		addTimeDomainFilterPanel.add(getAddTimeDomainFilterButton(), BorderLayout.EAST);

		JPanel addCustomTimeDomainFilterPanel = new JPanel(new BorderLayout(3, 3));
		addTimeDomainFilterPanel.add(addCustomTimeDomainFilterPanel, BorderLayout.SOUTH);
		addCustomTimeDomainFilterPanel.add(getAddCustomTimeDomainFilterButton(), BorderLayout.EAST);

		JPanel addFftFilterPanel = new JPanel(new BorderLayout(3, 3));
		border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("montageFilters.addFFTFilterTitle")),
		        new EmptyBorder(3, 3, 3, 3)
		);
		addFftFilterPanel.setBorder(border);

		addFftFilterPanel.add(getFftFilterTypeComboBox(), BorderLayout.CENTER);
		addFftFilterPanel.add(getAddFFTFilterButton(), BorderLayout.EAST);

		JPanel bottomLeftPanel = new JPanel(new BorderLayout());

		bottomLeftPanel.add(addTimeDomainFilterPanel, BorderLayout.CENTER);

		bottomLeftPanel.add(addFftFilterPanel, BorderLayout.SOUTH);

		JPanel leftPanel = new JPanel(new BorderLayout());

		leftPanel.add(masterSwitchPanel, BorderLayout.NORTH);
		leftPanel.add(filtersTablePanel, BorderLayout.CENTER);
		leftPanel.add(bottomLeftPanel, BorderLayout.SOUTH);

		JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 3, 3));

		rightButtonPanel.add(getClearFilterExclusionButton());

		JPanel rightPanel = new JPanel(new BorderLayout(3, 3));
		border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("montageFilters.filterChannelExclusionTitle")),
		        new EmptyBorder(3, 3, 3, 3)
		);
		rightPanel.setBorder(border);

		rightPanel.add(getFilterExclusionScrollPane(), BorderLayout.CENTER);
		rightPanel.add(rightButtonPanel, BorderLayout.SOUTH);

		add(leftPanel);
		add(rightPanel);

	}

	public JCheckBox getFilteringEnabledCheckBox() {
		if (filteringEnabledCheckBox == null) {
			filteringEnabledCheckBox = new JCheckBox();

			filteringEnabledCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (montage != null) {
						montage.setFilteringEnabled(getFilteringEnabledCheckBox().isSelected());
					}
				}

			});
		}
		return filteringEnabledCheckBox;
	}

	public MontageFiltersTableModel getFiltersTableModel() {
		if (filtersTableModel == null) {
			filtersTableModel = new MontageFiltersTableModel(messageSource);
		}
		return filtersTableModel;
	}

	public MontageFiltersTable getFiltersTable() {
		if (filtersTable == null) {
			filtersTable = new MontageFiltersTable(getFiltersTableModel(), messageSource);

			filtersTable.setPopupMenuProvider(new FiltersTablePopupProvider());
			filtersTable.addMouseListener(new FiltersTableMouseHandler());

			filtersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					boolean enableActions = !(getFiltersTable().getSelectionModel().isSelectionEmpty());

					editFilterAction.setEnabled(enableActions);
					removeFilterAction.setEnabled(enableActions);


				}

			});

		}
		return filtersTable;
	}

	public JScrollPane getFiltersScrollPane() {
		if (filtersScrollPane == null) {
			filtersScrollPane = new JScrollPane(getFiltersTable());
			filtersScrollPane.setPreferredSize(new Dimension(200, 400));
		}
		return filtersScrollPane;
	}

	public MontageFilterExclusionTableModel getFilterExclusionTableModel() {
		if (filterExclusionTableModel == null) {
			filterExclusionTableModel = new MontageFilterExclusionTableModel(messageSource);
		}
		return filterExclusionTableModel;
	}

	public MontageFilterExclusionTable getFilterExclusionTable() {
		if (filterExclusionTable == null) {
			filterExclusionTable = new MontageFilterExclusionTable(getFilterExclusionTableModel());
		}
		return filterExclusionTable;
	}

	public JScrollPane getFilterExclusionScrollPane() {
		if (filterExclusionScrollPane == null) {
			filterExclusionScrollPane = new JScrollPane(getFilterExclusionTable());
			filterExclusionScrollPane.setPreferredSize(new Dimension(200, 400));
		}
		return filterExclusionScrollPane;
	}

	public ResolvableComboBox getTimeDomainFilterTypeComboBox() {
		if (timeDomainFilterTypeComboBox == null) {
			timeDomainFilterTypeComboBox = new ResolvableComboBox(messageSource);
			timeDomainFilterTypeComboBox.setPreferredSize(new Dimension(200, 25));
		}
		return timeDomainFilterTypeComboBox;
	}

	public JComboBox getFftFilterTypeComboBox() {
		if (fftFilterTypeComboBox == null) {
			DefaultComboBoxModel model = new DefaultComboBoxModel(new Object[] {
			                        messageSource.getMessage("montageFilters.passingFFTFilter"),
			                        messageSource.getMessage("montageFilters.stoppingFFTFilter")
			                });
			fftFilterTypeComboBox = new JComboBox(model);
			fftFilterTypeComboBox.setSelectedIndex(0);
			fftFilterTypeComboBox.setPreferredSize(new Dimension(200, 25));
		}
		return fftFilterTypeComboBox;
	}

	public JButton getAddTimeDomainFilterButton() {
		if (addTimeDomainFilterButton == null) {
			addTimeDomainFilterButton = new JButton(addTimeDomainFilterAction);
		}
		return addTimeDomainFilterButton;
	}

	public JButton getAddCustomTimeDomainFilterButton() {
		if (addCustomTimeDomainFilterButton == null) {
			addCustomTimeDomainFilterButton = new JButton(addCustomTimeDomainFilterAction);
		}
		return addCustomTimeDomainFilterButton;
	}

	public JButton getAddFFTFilterButton() {
		if (addFFTFilterButton == null) {
			addFFTFilterButton = new JButton(addFFTFilterAction);
		}
		return addFFTFilterButton;
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

	public JButton getClearFilterExclusionButton() {
		if (clearFilterExclusionButton == null) {
			clearFilterExclusionButton = new JButton(clearFilterExclusionAction);
		}
		return clearFilterExclusionButton;
	}

	public SeriousWarningDialog getSeriousWarningDialog() {
		return seriousWarningDialog;
	}

	public void setSeriousWarningDialog(SeriousWarningDialog seriousWarningDialog) {
		this.seriousWarningDialog = seriousWarningDialog;
	}

	public EditFFTSampleFilterDialog getEditFFTSampleFilterDialog() {
		return editFFTSampleFilterDialog;
	}

	public void setEditFFTSampleFilterDialog(EditFFTSampleFilterDialog editFFTSampleFilterDialog) {
		this.editFFTSampleFilterDialog = editFFTSampleFilterDialog;
	}

	public EditTimeDomainSampleFilterDialog getEditTimeDomainSampleFilterDialog() {
		return editTimeDomainSampleFilterDialog;
	}

	public void setTimeDomainSampleFilterDialog(EditTimeDomainSampleFilterDialog editTimeDomainSampleFilterDialog) {
		this.editTimeDomainSampleFilterDialog = editTimeDomainSampleFilterDialog;
	}

	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			this.montage = montage;
			getFiltersTableModel().setMontage(montage);
			getFilterExclusionTableModel().setMontage(montage);

			if (montage != null) {
				Collection<TimeDomainSampleFilter> predefinedFilters = predefinedTimeDomainSampleFilterPresetManager.getPredefinedFilters(getCurrentSamplingFrequency());
				SampleFilterDefinition[] arr = new SampleFilterDefinition[predefinedFilters.size()];
				predefinedFilters.toArray(arr);
				DefaultComboBoxModel model = new DefaultComboBoxModel(arr);
				ResolvableComboBox comboBox = getTimeDomainFilterTypeComboBox();
				comboBox.setModel(model);
				comboBox.setSelectedIndex(0);
				comboBox.repaint();

				getFilteringEnabledCheckBox().setSelected(montage.isFilteringEnabled());
			} else {
				getTimeDomainFilterTypeComboBox().setModel(new DefaultComboBoxModel(new Object[0]));
				getFilteringEnabledCheckBox().setSelected(false);
			}
		}
	}

	public boolean isSignalBound() {
		return signalBound;
	}

	public void setSignalBound(boolean signalBound) {
		if (this.signalBound != signalBound) {
			this.signalBound = signalBound;
		}
	}

	public float getCurrentSamplingFrequency() {
		return currentSamplingFrequency;
	}

	public void setCurrentSamplingFrequency(float currentSamplingFrequency) {
		this.currentSamplingFrequency = currentSamplingFrequency;
		if (currentSamplingFrequency >= 0) {
			editFFTSampleFilterDialog.setGraphFrequencyMax(currentSamplingFrequency / 2);
			editTimeDomainSampleFilterDialog.setGraphFrequencyMax(currentSamplingFrequency / 2);
		} else {
			double frequencyMax = editFFTSampleFilterDialog.getGraphFrequencyMax();
			if (frequencyMax < 0.25) {
				editFFTSampleFilterDialog.setGraphFrequencyMax(64.0);
				editTimeDomainSampleFilterDialog.setGraphFrequencyMax(currentSamplingFrequency / 2);
			}
		}
	}

	protected class AddTimeDomainFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddTimeDomainFilterAction() {
			super(messageSource.getMessage("montageFilters.addTimeDomainFilter"));
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage("montageFilters.addTimeDomainFilterToolTip"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addtimedomainfilter.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			int index = getTimeDomainFilterTypeComboBox().getSelectedIndex();
			TimeDomainSampleFilter filter = predefinedTimeDomainSampleFilterPresetManager.getPredefinedFilterAt(currentSamplingFrequency, index);
			filter.setDescription(messageSource.getMessage("montageFilters.newTimeDomainFilter"));
			filter.setSamplingFrequency(currentSamplingFrequency);
			montage.addSampleFilter(filter);
		}

	}

	protected class AddCustomTimeDomainFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddCustomTimeDomainFilterAction() {
			super(messageSource.getMessage("montageFilters.addCustomTimeDomainFilter"));
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage("montageFilters.addCustomTimeDomainFilterToolTip"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addcustomtimedomainfilter.png"));
		}

		@Override
		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			TimeDomainSampleFilter filter = predefinedTimeDomainSampleFilterPresetManager.getCustomFilterStartingPoint(getCurrentSamplingFrequency());

			if (filter == null) {
				filter = predefinedTimeDomainSampleFilterPresetManager.getCustomStartingPoint();
			}

			editTimeDomainSampleFilterDialog.setCurrentSamplingFrequency(currentSamplingFrequency);
			boolean ok = editTimeDomainSampleFilterDialog.showDialog(filter, true);
			if (!ok) {
				return;
			}

			filter.setDescription(messageSource.getMessage("montageFilters.newTimeDomainFilter"));
			filter.setSamplingFrequency(getCurrentSamplingFrequency());
			montage.addSampleFilter(filter);

		}

	}

	protected class AddFFTFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddFFTFilterAction() {
			super(messageSource.getMessage("montageFilters.addFFTFilter"));
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage("montageFilters.addFFTFilterToolTip"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addfftfilter.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			int index = getFftFilterTypeComboBox().getSelectedIndex();
			boolean initiallyPassing = (index == 0);

			FFTSampleFilter filter = new FFTSampleFilter(initiallyPassing);
			filter.setDescription(messageSource.getMessage("montageFilters.newFFT"));

			editFFTSampleFilterDialog.setCurrentSamplingFrequency(currentSamplingFrequency);
			boolean ok = editFFTSampleFilterDialog.showDialog(filter, true);
			if (!ok) {
				return;
			}

			montage.addSampleFilter(filter);

		}

	}

	protected class EditFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EditFilterAction() {
			super(messageSource.getMessage("montageFilters.editFilter"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/editfilter.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			int selectedRow = getFiltersTable().getSelectedRow();
			if (selectedRow < 0) {
				return;
			}

			SampleFilterDefinition filter = montage.getSampleFilterAt(selectedRow);
			SampleFilterType type = filter.getType();
			boolean ok;
			switch (type) {

			case TIME_DOMAIN :
				editTimeDomainSampleFilterDialog.setCurrentSamplingFrequency(currentSamplingFrequency);
				ok = editTimeDomainSampleFilterDialog.showDialog(filter, true);
				if (!ok) {
					return;
				}

				montage.updateSampleFilter(selectedRow, filter);

				break;

			case FFT :

				editFFTSampleFilterDialog.setCurrentSamplingFrequency(currentSamplingFrequency);
				ok = editFFTSampleFilterDialog.showDialog(filter, true);
				if (!ok) {
					return;
				}

				montage.updateSampleFilter(selectedRow, filter);

				break;

			default :
				throw new SanityCheckException("Unsupported filter type [" + type + "]");
			}


		}

	}

	protected class RemoveFilterAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveFilterAction() {
			super(messageSource.getMessage("montageFilters.removeFilter"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removefilter.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			int selectedRow = getFiltersTable().getSelectedRow();
			if (selectedRow < 0) {
				return;
			}

			montage.removeSampleFilter(selectedRow);

		}

	}

	protected class ClearFilterExclusionAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ClearFilterExclusionAction() {
			super(messageSource.getMessage("montageFilters.clearFilterExclusion"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/clearfilterexclusion.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			int count = montage.getSampleFilterCount();
			int i;
			for (i = 0; i < count; i++) {
				montage.setFilterEnabled(i, true);
				montage.clearFilterExclusion(i);
			}

			count = montage.getMontageChannelCount();
			for (i = 0; i < count; i++) {
				montage.setExcludeAllFilters(i, false);
			}

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
			MontageFiltersTable table = (MontageFiltersTable) e.getSource();
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() % 2) == 0) {
				int selRow = table.rowAtPoint(e.getPoint());
				if (selRow >= 0) {
					editFilterAction.actionPerformed(new ActionEvent(table, 0, "edit"));
				}
			}
		}

	}

}

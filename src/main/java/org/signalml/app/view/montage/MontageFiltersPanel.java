/* MontageFiltersPanel.java created 2008-02-03
 *
 */
package org.signalml.app.view.montage;

import org.signalml.app.view.montage.filters.EditTimeDomainSampleFilterDialog;
import org.signalml.app.view.montage.filters.EditFFTSampleFilterDialog;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;

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
import org.signalml.app.config.preset.PredefinedTimeDomainFiltersPresetManager;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.montage.MontageFilterExclusionTableModel;
import org.signalml.app.montage.MontageFiltersTableModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.TablePopupMenuProvider;
import org.signalml.app.view.dialog.SeriousWarningDialog;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageChannel;
import org.signalml.domain.montage.MontageSampleFilter;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.SampleFilterType;
import org.signalml.exception.SanityCheckException;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * Panel which allows to add, remove and edit filters associated with the.
 * 
 * {@link Montage montage}. There filters can be either
 * {@link TimeDomainSampleFilter}s or {@link FFTSampleFilter}s.
 * <p>
 * This panel contains five subpanels:
 * <ul>
 * <li>the panel with the {@link #getFilteringEnabledCheckBox() check box} to
 * enable {@link Montage#isFiltered() signal filtering},</li>
 * <li>the panel with the {@link #getFiltersTable() table} with the list of
 * filters and two buttons:
 * <ul>
 * <li>the button to {@link #getEditFilterButton() edit} a filter,</li>
 * <li>the button to {@link #getRemoveFilterButton() remove} a filter,</li>
 * </ul>
 * </li>
 * <li>the panel which contains two buttons allowing to add a
 * {@link TimeDomainSampleFilter} to the list of filters:
 * <ul>
 * <li>the {@link #getAddTimeDomainFilterButton() button} and
 * {@link #getTimeDomainFilterTypeComboBox() combo-box} which adds the
 * predefined filter,</li>
 * <li>the {@link #getAddCustomTimeDomainFilterButton() button} which opens a
 * {@link #getEditTimeDomainSampleFilterDialog() dialog} to add a custom filter,
 * </li>
 * </ul>
 * </li>
 * <li>the panel which allows to add a {@link FFTSampleFilter} and contains a
 * {@link #getFftFilterTypeComboBox() combo-box} and a
 * {@link #getAddFFTFilterButton() button} do to it,</li>
 * <li>the panel with the {@link #getFilterExclusionTable() exclusion table}.</li>
 * </ul>
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class MontageFiltersPanel extends JPanel {

	/** the default serialization constant. */
	private static final long serialVersionUID = 1L;

	/** the logger. */
	protected static final Logger logger = Logger.getLogger(MontageFiltersPanel.class);

	/** the source of messages (labels). */
	private MessageSourceAccessor messageSource;
	
	/** the {@link SeriousWarningDialog dialog window} with a serious warning. */
	private SeriousWarningDialog seriousWarningDialog;

	/**
	 * the dialog that is used when editing. {@link FFTSampleFilter
	 * FFTSampleFilter's} parameters
	 */
	private EditFFTSampleFilterDialog editFFTSampleFilterDialog;

	/**
	 * the dialog that is used when editing {@link TimeDomainSampleFilter}
	 * parameters.
	 */
	private EditTimeDomainSampleFilterDialog editTimeDomainSampleFilterDialog;

	/** the {@link Montage montage} that is edited. */
	private Montage montage;
	
	/**
	 * <code>true</code> if there exists a {@link SignalDocument document}
	 * associated with the {@link Montage montage}, <code>false</code> otherwise.
	 */
	private boolean signalBound;
	
	/** the sampling frequency of the signal (the number of samples per second). */
	private float currentSamplingFrequency;

	/**
	 * check box which states if the signal should be
	 * {@link Montage#isFiltered() filtered}.
	 */
	private JCheckBox filteringEnabledCheckBox;
	private JCheckBox filtfiltEnabledCheckBox;

	/**
	 * the {@link MontageFiltersTableModel model} for the {@link #filtersTable}.
	 */
	private MontageFiltersTableModel filtersTableModel;
	
	/**
	 * the {@link MontageFiltersTable table} with the list of filters associated
	 * with the {@link #getMontage() montage}.
	 */
	private MontageFiltersTable filtersTable;
	
	/** the scroll pane for the {@link #getFiltersTable() filters table}. */
	private JScrollPane filtersScrollPane;

	/**
	 * the {@link MontageFilterExclusionTableModel model} for the.
	 * {@link #getFilterExclusionTable() filter exclusion table}
	 */
	private MontageFilterExclusionTableModel filterExclusionTableModel;
	
	/**
	 * the table which allows to select which {@link MontageChannel montage
	 * channels} should not be {@link MontageSampleFilter filtered} by which
	 * filter.
	 */
	private MontageFilterExclusionTable filterExclusionTable;
	
	/**
	 * the scroll pane for the {@link #getFilterExclusionTable() filter
	 * exclusion table}.
	 */
	private JScrollPane filterExclusionScrollPane;

	/**
	 * a {@link ResolvableComboBox} allowing to select a predefined.
	 * {@link TimeDomainSampleFilter} to be added to the signal chain
	 */
	private ResolvableComboBox timeDomainFilterTypeComboBox;

	/**
	 * a {@link JComboBox} allowing to select the initial parameters of the
	 * {@link FFTSampleFilter} shown in the {@link EditFFTSampleFilterDialog}.
	 */
	private JComboBox fftFilterTypeComboBox;

	/**
	 * action which adds a {@link TimeDomainSampleFilter} and is invoked after
	 * pressing the {@link #getAddTimeDomainFilterButton()
	 * addTimeDomainFilterButton}.
	 */
	private AddTimeDomainFilterAction addTimeDomainFilterAction;

	/**
	 * action which adds a {@link TimeDomainSampleFilter}, displays a.
	 * {@link EditTimeDomainSampleFilterDialog} and is invoked after pressing
	 * the {@link #getAddCustomTimeDomainFilterButton()
	 * addCustomTimeDomainFilterButton}
	 */
	private AddCustomTimeDomainFilterAction addCustomTimeDomainFilterAction;
	
	/**
	 * action which adds a {@link FFTSampleFilter} and is invoked after pressing
	 * the {@link #getAddFFTFilterButton() addFFTFilterButton}.
	 */
	private AddFFTFilterAction addFFTFilterAction;
	
	/**
	 * action which displays a {@link EditTimeDomainSampleFilterDialog} and is
	 * invoked after pressing the {@link #getEditFilterButton()
	 * editFilterButton}.
	 */
	private EditFilterAction editFilterAction;
	
	/**
	 * action which removes a selected {@link MontageSampleFilter filter} from
	 * the list of filters and is invoked after pressing the
	 * {@link #getRemoveFilterButton() removeFilterButton}.
	 */
	private RemoveFilterAction removeFilterAction;
	
	/**
	 * action which removes the exclusions of the filters and is invoked after
	 * pressing the {@link #getClearFilterExclusionButton()
	 * clearFilterExclusionButton}.
	 */
	private ClearFilterExclusionAction clearFilterExclusionAction;

	/**
	 * a button which can be used to add a predefined
	 * {@link TimeDomainSampleFilter} to the list of filters.
	 */
	private JButton addTimeDomainFilterButton;

	/**
	 * a button allowing to add a custom-designed filter to the list of filters.
	 */
	private JButton addCustomTimeDomainFilterButton;

	/**
	 * a button which can be used to add a predefined {@link FFTSampleFilter} to
	 * the list of filters.
	 */
	private JButton addFFTFilterButton;

	/** a button allowing to edit the selected filter's parameters. */
	private JButton editFilterButton;

	/**
	 * a button allowing to delete currently selected filter from the list of
	 * filters.
	 */
	private JButton removeFilterButton;

	/**
	 * a button allowing to clear all selections in the.
	 * {@link #getFilterExclusionTable() filterExclusionTable}
	 */
	private JButton clearFilterExclusionButton;

	/**
	 * the manager at which predefined {@link TimeDomainSampleFilter
	 * TimeDomainSampleFilters} are stored.
	 */
	private PredefinedTimeDomainFiltersPresetManager predefinedTimeDomainSampleFilterPresetManager;

	/**
	 * Creates this panel and {@link #initialize() initializes} it.
	 * @param messageSource the source of messages (labels)
	 * @param predefinedTimeDomainSampleFilterPresetManager the manager at which
	 * predefined {@link TimeDomainSampleFilter TimeDomainSampleFilters} are
	 * stored
	 */
	public MontageFiltersPanel(MessageSourceAccessor messageSource, PredefinedTimeDomainFiltersPresetManager predefinedTimeDomainSampleFilterPresetManager) {
		super();
		this.messageSource = messageSource;
		this.predefinedTimeDomainSampleFilterPresetManager = predefinedTimeDomainSampleFilterPresetManager;
		initialize();
	}

	/**
	 * Initializes this panel with five subpanels:
	 * <ul>
	 * <li>the panel with the {@link #getFilteringEnabledCheckBox() check box}
	 * to enable {@link Montage#isFiltered() signal filtering},</li>
	 * <li>the panel with the {@link #getFiltersTable() table} with the list
	 * of filters and two buttons:
	 * <ul><li>the button to {@link #getEditFilterButton() edit} a filter,</li>
	 * <li>the button to {@link #getRemoveFilterButton() remove} a filter,</li>
	 * </ul></li>
	 * <li>the panel which contains two buttons allowing to add a
	 * {@link TimeDomainSampleFilter} to the list of filters:
	 * <ul><li>the {@link #getAddTimeDomainFilterButton() button} and
	 * {@link #getTimeDomainFilterTypeComboBox() combo-box} which adds the
	 * predefined filter,</li>
	 * <li>the {@link #getAddCustomTimeDomainFilterButton() button} which
	 * opens a {@link #getEditTimeDomainSampleFilterDialog() dialog} to add
	 * a custom filter,</li></ul></li>
	 * <li>the panel which allows to add a {@link FFTSampleFilter} and contains
	 * a {@link #getFftFilterTypeComboBox() combo-box} and a {@link
	 * #getAddFFTFilterButton() button} do to it,</li>
	 * <li>the panel with the {@link #getFilterExclusionTable() exclusion table}.
	 * </li></ul>
	 */
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

		JPanel masterSwitchPanel = createMasterSwitchPanel();
		JPanel enableFiltfiltPanel = createEnableFiltfiltPanel();

		JPanel filteringOptionsPanel = new JPanel(new BorderLayout(3, 3));
		filteringOptionsPanel.add(masterSwitchPanel, BorderLayout.NORTH);
		filteringOptionsPanel.add(enableFiltfiltPanel, BorderLayout.SOUTH);

                //filters table panel
		JPanel filtersTablePanel = new JPanel(new BorderLayout(3, 3));
		CompoundBorder border = new CompoundBorder(
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

		leftPanel.add(filteringOptionsPanel, BorderLayout.NORTH);
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

	private JPanel createMasterSwitchPanel() {
		JPanel masterSwitchPanel = new JPanel(new BorderLayout(3, 3));

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("montageFilters.masterSwitchTitle")),
			new EmptyBorder(3, 3, 3, 3));
		masterSwitchPanel.setBorder(border);

		JLabel filteringEnabledLabel = new JLabel(messageSource.getMessage("montageFilters.filteringEnabled"));

		masterSwitchPanel.add(filteringEnabledLabel, BorderLayout.CENTER);
		masterSwitchPanel.add(getFilteringEnabledCheckBox(), BorderLayout.EAST);

		return masterSwitchPanel;
	}

	private JPanel createEnableFiltfiltPanel() {
		JPanel enableFiltfiltPanel = new JPanel(new BorderLayout(3, 3));

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("montageFilters.enableFiltfiltTitle")),
			new EmptyBorder(3, 3, 3, 3));
		enableFiltfiltPanel.setBorder(border);

		JLabel filtfiltEnabledLabel = new JLabel(messageSource.getMessage("montageFilters.enableFiltfiltLabel"));

		enableFiltfiltPanel.add(filtfiltEnabledLabel, BorderLayout.CENTER);
		enableFiltfiltPanel.add(getFiltfiltEnabledCheckBox(), BorderLayout.EAST);

		return enableFiltfiltPanel;
	}

	/**
	 * Gets the check box which states if the signal should be
	 * {@link Montage#isFiltered() filtered}.
	 * 
	 * @return the check box which states if the signal should be
	 *         {@link Montage#isFiltered() filtered}
	 */
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

	public JCheckBox getFiltfiltEnabledCheckBox() {
		if (filtfiltEnabledCheckBox == null) {
			filtfiltEnabledCheckBox = new JCheckBox();

			filtfiltEnabledCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (montage != null) {
						montage.setFiltfiltEnabled(getFiltfiltEnabledCheckBox().isSelected());
					}
				}
			});
		}
		return filtfiltEnabledCheckBox;
	}

	/**
	 * Gets the {@link MontageFiltersTableModel model} for the
	 * {@link #filtersTable}.
	 * 
	 * @return the {@link MontageFiltersTableModel model} for the
	 *         {@link #filtersTable}
	 */
	public MontageFiltersTableModel getFiltersTableModel() {
		if (filtersTableModel == null) {
			filtersTableModel = new MontageFiltersTableModel(messageSource);
		}
		return filtersTableModel;
	}

	/**
	 * Gets the {@link MontageFiltersTable table} with the list of filters
	 * associated with the {@link #getMontage() montage}.
	 * 
	 * @return the {@link MontageFiltersTable table} with the list of filters
	 *         associated with the {@link #getMontage() montage}
	 */
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

	/**
	 * Gets the scroll pane for the {@link #getFiltersTable() filters table}.
	 * 
	 * @return the scroll pane for the {@link #getFiltersTable() filters table}
	 */
	public JScrollPane getFiltersScrollPane() {
		if (filtersScrollPane == null) {
			filtersScrollPane = new JScrollPane(getFiltersTable());
			filtersScrollPane.setPreferredSize(new Dimension(200, 400));
		}
		return filtersScrollPane;
	}

	/**
	 * Gets the {@link MontageFilterExclusionTableModel model} for the.
	 * 
	 * @return the {@link MontageFilterExclusionTableModel model} for the
	 */
	public MontageFilterExclusionTableModel getFilterExclusionTableModel() {
		if (filterExclusionTableModel == null) {
			filterExclusionTableModel = new MontageFilterExclusionTableModel(messageSource);
		}
		return filterExclusionTableModel;
	}

	/**
	 * Gets the table which allows to select which {@link MontageChannel montage
	 * channels} should not be {@link MontageSampleFilter filtered} by which
	 * filter.
	 * 
	 * @return the table which allows to select which {@link MontageChannel
	 *         montage channels} should not be {@link MontageSampleFilter
	 *         filtered} by which filter
	 */
	public MontageFilterExclusionTable getFilterExclusionTable() {
		if (filterExclusionTable == null) {
			filterExclusionTable = new MontageFilterExclusionTable(getFilterExclusionTableModel());
		}
		return filterExclusionTable;
	}

	/**
	 * Gets the scroll pane for the {@link #getFilterExclusionTable() filter
	 * exclusion table}.
	 * 
	 * @return the scroll pane for the {@link #getFilterExclusionTable() filter
	 *         exclusion table}
	 */
	public JScrollPane getFilterExclusionScrollPane() {
		if (filterExclusionScrollPane == null) {
			filterExclusionScrollPane = new JScrollPane(getFilterExclusionTable());
			filterExclusionScrollPane.setPreferredSize(new Dimension(200, 400));
		}
		return filterExclusionScrollPane;
	}

	/**
	 * Returns the {@link ResolvableComboBox} allowing to select
	 * a predefined {@link TimeDomainSampleFilter} to be added to the
	 * signal chain.
	 * @return the {@link ResolvableComboBox} to select a predefined filter
	 */
	public ResolvableComboBox getTimeDomainFilterTypeComboBox() {
		if (timeDomainFilterTypeComboBox == null) {
			timeDomainFilterTypeComboBox = new ResolvableComboBox(messageSource);
			timeDomainFilterTypeComboBox.setPreferredSize(new Dimension(200, 25));
		}
		return timeDomainFilterTypeComboBox;
	}

	/**
	 * Returns the {@link JComboBox} allowing to select whether the
	 * 
	 * @return a {@link JComboBox} allowing to select initial parameters of the
	 *         {@link FFTSampleFilter} to be edited {@link FFTSampleFilter}
	 *         which will be shown in
	 *         {@link MontageFiltersPanel#editFFTSampleFilterDialog} will be
	 *         initially-passing or initially-stopping.
	 */
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

	/**
	 * Returns a {@link JButton} allowing to add a predefined
	 * 
	 * @return a button allowing to add a new predefined TimeDomainSampleFilter
	 *         to the signal chain {@link TimeDomainSampleFilter} to the signal
	 *         chain.
	 */
	public JButton getAddTimeDomainFilterButton() {
		if (addTimeDomainFilterButton == null) {
			addTimeDomainFilterButton = new JButton(addTimeDomainFilterAction);
		}
		return addTimeDomainFilterButton;
	}

	/**
	 * Returns a button which opens a {@link EditTimeDomainSampleFilterDialog}
	 * which allows to add a new custom designed
	 * 
	 * @return a button allowing to open a
	 *         {@link EditTimeDomainSampleFilterDialog}
	 *         {@link TimeDomainSampleFilter} to the signal chain
	 */
	public JButton getAddCustomTimeDomainFilterButton() {
		if (addCustomTimeDomainFilterButton == null) {
			addCustomTimeDomainFilterButton = new JButton(addCustomTimeDomainFilterAction);
		}
		return addCustomTimeDomainFilterButton;
	}

	/**
	 * Returns a button which opens a {@link EditFFTSampleFilterDialog}
	 * which allows to add a new {@link FFTSampleFilter} to the signal chain.
	 * @return a button allowing to open a {@link EditFFTSampleFilterDialog}
	 */
	public JButton getAddFFTFilterButton() {
		if (addFFTFilterButton == null) {
			addFFTFilterButton = new JButton(addFFTFilterAction);
		}
		return addFFTFilterButton;
	}

	/**
	 * Gets the a button allowing to edit the selected filter's parameters.
	 * 
	 * @return the a button allowing to edit the selected filter's parameters
	 */
	public JButton getEditFilterButton() {
		if (editFilterButton == null) {
			editFilterButton = new JButton(editFilterAction);
		}
		return editFilterButton;
	}

	/**
	 * Gets the a button allowing to delete currently selected filter from the
	 * list of filters.
	 * 
	 * @return the a button allowing to delete currently selected filter from
	 *         the list of filters
	 */
	public JButton getRemoveFilterButton() {
		if (removeFilterButton == null) {
			removeFilterButton = new JButton(removeFilterAction);
		}
		return removeFilterButton;
	}

	/**
	 * Gets the a button allowing to clear all selections in the.
	 * 
	 * @return the a button allowing to clear all selections in the
	 */
	public JButton getClearFilterExclusionButton() {
		if (clearFilterExclusionButton == null) {
			clearFilterExclusionButton = new JButton(clearFilterExclusionAction);
		}
		return clearFilterExclusionButton;
	}

	/**
	 * Gets the {@link SeriousWarningDialog dialog window} with a serious
	 * warning.
	 * 
	 * @return the {@link SeriousWarningDialog dialog window} with a serious
	 *         warning
	 */
	public SeriousWarningDialog getSeriousWarningDialog() {
		return seriousWarningDialog;
	}

	/**
	 * Sets the {@link SeriousWarningDialog dialog window} with a serious
	 * warning.
	 * 
	 * @param seriousWarningDialog
	 *            the new {@link SeriousWarningDialog dialog window} with a
	 *            serious warning
	 */
	public void setSeriousWarningDialog(SeriousWarningDialog seriousWarningDialog) {
		this.seriousWarningDialog = seriousWarningDialog;
	}

	/**
	 * Gets the dialog that is used when editing.
	 * 
	 * @return the dialog that is used when editing
	 */
	public EditFFTSampleFilterDialog getEditFFTSampleFilterDialog() {
		return editFFTSampleFilterDialog;
	}

	/**
	 * Sets the dialog that is used when editing.
	 * 
	 * @param editFFTSampleFilterDialog
	 *            the new dialog that is used when editing
	 */
	public void setEditFFTSampleFilterDialog(EditFFTSampleFilterDialog editFFTSampleFilterDialog) {
		this.editFFTSampleFilterDialog = editFFTSampleFilterDialog;
	}

	/**
	 * Returns a {@link EditTimeDomainSampleFilterDialog} which can be opened
	 * using this {@link MontageFiltersPanel}.
	 * @return a {@link EditTimeDomainSampleFilterDialog} set for this panel
	 */
	public EditTimeDomainSampleFilterDialog getEditTimeDomainSampleFilterDialog() {
		return editTimeDomainSampleFilterDialog;
	}

	/**
	 * Sets an {@link EditTimeDomainSampleFilterDialog} to be opened in this panel
	 * when needed.
	 * @param editTimeDomainSampleFilterDialog an EditTimeDomainSampleFilterDialog
	 * to be opened in this panel
	 */
	public void setTimeDomainSampleFilterDialog(EditTimeDomainSampleFilterDialog editTimeDomainSampleFilterDialog) {
		this.editTimeDomainSampleFilterDialog = editTimeDomainSampleFilterDialog;
	}

	/**
	 * Gets the {@link Montage montage} that is edited.
	 * 
	 * @return the {@link Montage montage} that is edited
	 */
	public Montage getMontage() {
		return montage;
	}

	/**
	 * Sets the {@link Montage montage} that is edited.
	 * 
	 * @param montage
	 *            the new {@link Montage montage} that is edited
	 */
	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			this.montage = montage;
			getFiltersTableModel().setMontage(montage);
			getFilterExclusionTableModel().setMontage(montage);

			if (montage != null) {
				updatePredefinedTimeDomainFiltersComboBox();

				getFilteringEnabledCheckBox().setSelected(montage.isFilteringEnabled());
                                getFiltfiltEnabledCheckBox().setSelected(montage.isFiltfiltEnabled());
			} else {
				getTimeDomainFilterTypeComboBox().setModel(new DefaultComboBoxModel(new Object[0]));
				getFilteringEnabledCheckBox().setSelected(false);
                                getFiltfiltEnabledCheckBox().setSelected(false);
			}
		}
	}

	/**
	 * Updates the combo box used for the selection of predefined filters
	 * regarding the current sampling frequency.
	 */
	protected void updatePredefinedTimeDomainFiltersComboBox() {
		List<SampleFilterDefinition> predefinedFilters = predefinedTimeDomainSampleFilterPresetManager.getPredefinedFilters(getCurrentSamplingFrequency());

		if (predefinedFilters == null) {
			//ErrorsDialog.showImmediateExceptionDialog(this, new ResolvableException("error.noPredefinedFiltersForThisSamplingFrequency"));
			logger.debug("No predefined filters for the current sampling frequency " + getCurrentSamplingFrequency());
			getTimeDomainFilterTypeComboBox().setModel(new DefaultComboBoxModel(new Object[0]));
			return;
		}

		SampleFilterDefinition[] arr = new SampleFilterDefinition[predefinedFilters.size()];
		predefinedFilters.toArray(arr);
		DefaultComboBoxModel model = new DefaultComboBoxModel(arr);
		ResolvableComboBox comboBox = getTimeDomainFilterTypeComboBox();
		comboBox.setModel(model);
		comboBox.setSelectedIndex(0);
		comboBox.repaint();
	}

	/**
	 * Checks if is <code>true</code> if there exists a {@link SignalDocument
	 * document} associated with the {@link Montage montage}, <code>false</code>
	 * otherwise.
	 * 
	 * @return the <code>true</code> if there exists a {@link SignalDocument
	 *         document} associated with the {@link Montage montage},
	 *         <code>false</code> otherwise
	 */
	public boolean isSignalBound() {
		return signalBound;
	}

	/**
	 * Sets the <code>true</code> if there exists a {@link SignalDocument
	 * document} associated with the {@link Montage montage}, <code>false</code>
	 * otherwise.
	 * 
	 * @param signalBound
	 *            the new <code>true</code> if there exists a
	 *            {@link SignalDocument document} associated with the
	 *            {@link Montage montage}, <code>false</code> otherwise
	 */
	public void setSignalBound(boolean signalBound) {
		if (this.signalBound != signalBound) {
			this.signalBound = signalBound;
		}
	}

	/**
	 * Gets the sampling frequency of the signal (the number of samples per
	 * second).
	 * 
	 * @return the sampling frequency of the signal (the number of samples per
	 *         second)
	 */
	public float getCurrentSamplingFrequency() {
		return currentSamplingFrequency;
	}

	/**
	 * Sets the sampling frequency of the signal (the number of samples per
	 * second).
	 * 
	 * @param currentSamplingFrequency
	 *            the new sampling frequency of the signal (the number of
	 *            samples per second)
	 */
	public void setCurrentSamplingFrequency(float currentSamplingFrequency) {
		this.currentSamplingFrequency = currentSamplingFrequency;
		editFFTSampleFilterDialog.setCurrentSamplingFrequency(currentSamplingFrequency);
		editTimeDomainSampleFilterDialog.setCurrentSamplingFrequency(currentSamplingFrequency);
		updatePredefinedTimeDomainFiltersComboBox();
	}

	/**
	 * Action which creates a {@link TimeDomainSampleFilter} of a.
	 * 
	 * {@link MontageFiltersPanel#getTimeDomainFilterTypeComboBox() selected}
	 * type and adds it to the {@link MontageFiltersPanel#montage montage}.
	 */
	protected class AddTimeDomainFilterAction extends AbstractAction {

		/** the default serialization constant. */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Creates a new action and sets a label, tooltip
		 * and an icon for the button associated with this action.
		 */
		public AddTimeDomainFilterAction() {
			super(messageSource.getMessage("montageFilters.addTimeDomainFilter"));
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage("montageFilters.addTimeDomainFilterToolTip"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addtimedomainfilter.png"));
		}

		/**
		 * When the action is performed creates a {@link TimeDomainSampleFilter}
		 * of a {@link MontageFiltersPanel#getTimeDomainFilterTypeComboBox()
		 * selected} type and adds it to the {@link MontageFiltersPanel#montage
		 * montage}.
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			int index = getTimeDomainFilterTypeComboBox().getSelectedIndex();
			TimeDomainSampleFilter filter = predefinedTimeDomainSampleFilterPresetManager.getPredefinedFilterAt(currentSamplingFrequency, index);
			if (filter == null)
				return;
			filter.setDescription(messageSource.getMessage("montageFilters.newTimeDomainFilter"));
			filter.setSamplingFrequency(currentSamplingFrequency);
			montage.addSampleFilter(filter);
		}

	}

	/**
	 * Action which creates a {@link TimeDomainSampleFilter} and displays a.
	 * 
	 * {@link MontageFiltersPanel#editTimeDomainSampleFilterDialog dialog} to
	 * edit it.
	 */
	protected class AddCustomTimeDomainFilterAction extends AbstractAction {

		/** the default serialization constant. */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Creates a new action and sets a label, tooltip
		 * and an icon for the button associated with this action.
		 */
		public AddCustomTimeDomainFilterAction() {
			super(messageSource.getMessage("montageFilters.addCustomTimeDomainFilter"));
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage("montageFilters.addCustomTimeDomainFilterToolTip"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addcustomtimedomainfilter.png"));
		}

		/**
		 * When the action is performed creates a {@link TimeDomainSampleFilter}
		 * and displays a {@link
		 * MontageFiltersPanel#editTimeDomainSampleFilterDialog dialog} to
		 * edit it.
		 */
		@Override
		public void actionPerformed(ActionEvent ev) {

			if (montage == null) {
				return;
			}

			TimeDomainSampleFilter filter = predefinedTimeDomainSampleFilterPresetManager.getCustomFilterStartingPoint(getCurrentSamplingFrequency());

			if (filter == null) {
				filter = predefinedTimeDomainSampleFilterPresetManager.getCustomStartingPoint();
			}

			filter.setDescription(messageSource.getMessage("montageFilters.newTimeDomainFilter"));
			filter.setSamplingFrequency(getCurrentSamplingFrequency());

			editTimeDomainSampleFilterDialog.setCurrentSamplingFrequency(currentSamplingFrequency);
			boolean ok = editTimeDomainSampleFilterDialog.showDialog(filter, true);
			if (!ok) {
				return;
			}

			montage.addSampleFilter(filter);

		}

	}

	/**
	 * Action which creates a {@link FFTSampleFilter} of a.
	 * 
	 * {@link MontageFiltersPanel#getFftFilterTypeComboBox() selected} type and
	 * adds it to the {@link MontageFiltersPanel#montage montage}.
	 */
	protected class AddFFTFilterAction extends AbstractAction {

		/** the default serialization constant. */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Creates a new action and sets a label, tooltip
		 * and an icon for the button associated with this action.
		 */
		public AddFFTFilterAction() {
			super(messageSource.getMessage("montageFilters.addFFTFilter"));
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage("montageFilters.addFFTFilterToolTip"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addfftfilter.png"));
		}

		/**
		 * When the action is performed creates a {@link FFTSampleFilter} of a
		 * {@link MontageFiltersPanel#getFftFilterTypeComboBox() selected}
		 * type and adds it to the {@link MontageFiltersPanel#montage montage}.
		 */
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

	/**
	 * Action which displays the dialog to edit a selected
	 * {@link MontageSampleFilter sample filter}:
	 * <ul>
	 * <li>{@link EditFFTSampleFilterDialog} if it is a.
	 * 
	 * {@link FFTSampleFilter FFT filter},</li>
	 * <li>{@link EditTimeDomainSampleFilterDialog} if it is a
	 * {@link TimeDomainSampleFilter time domain filter}.</li>
	 * </ul>
	 */
	protected class EditFilterAction extends AbstractAction {

		/** the default serialization constant. */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Creates a new action and sets a label
		 * and an icon for the button associated with this action.
		 */
		public EditFilterAction() {
			super(messageSource.getMessage("montageFilters.editFilter"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/editfilter.png"));
		}

		/**
		 * When the action is performed checks which {@link MontageSampleFilter
		 * sample filter} in the {@link MontageFiltersPanel#getFiltersTable()
		 * filters table} is selected and displays the dialog to edit it:
		 * <ul>
		 * <li>{@link EditFFTSampleFilterDialog} if it is a 
		 * {@link FFTSampleFilter FFT filter},</li>
		 * <li>{@link EditTimeDomainSampleFilterDialog} if it is a
		 * {@link TimeDomainSampleFilter time domain filter}.</li></ul>
		 * After the dialog is closed with OK button updates the filter in
		 * the {@link MontageFiltersPanel#montage montage}.
		 */
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

	/**
	 * Action which removes a selected {@link MontageSampleFilter sample
	 * filter} from the {@link MontageFiltersPanel#montage montage}.
	 */
	protected class RemoveFilterAction extends AbstractAction {

		/** the default serialization constant. */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Creates a new action and sets a label
		 * and an icon for the button associated with this action.
		 */
		public RemoveFilterAction() {
			super(messageSource.getMessage("montageFilters.removeFilter"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removefilter.png"));
		}

		/**
		 * When this action is performed checks which {@link
		 * MontageSampleFilter sample filter} in the
		 * {@link MontageFiltersPanel#getFiltersTable() filters table} is
		 * selected and {@link Montage#removeSampleFilter(int) removes} it from
		 * the {@link MontageFiltersPanel#montage montage}.
		 */
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

	/**
	 * Action which removes all {@link Montage#clearFilterExclusion(int)
	 * exclusions} of the channels from the {@link MontageSampleFilter
	 * sample filters}.
	 */
	protected class ClearFilterExclusionAction extends AbstractAction {

		/** the default serialization constant. */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Creates a new action and sets a label
		 * and an icon for the button associated with this action.
		 */
		public ClearFilterExclusionAction() {
			super(messageSource.getMessage("montageFilters.clearFilterExclusion"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/clearfilterexclusion.png"));
		}

		/**
		 * When this action is performed {@link
		 * Montage#setFilterEnabled(int, boolean) enables} all {@link
		 * MontageSampleFilter sample filters} and {@link
		 * Montage#clearFilterExclusion(int) clears} their exclusions.
		 * Also sets that no channel in the {@link Montage montage}
		 * {@link Montage#setExcludeAllFilters(int, boolean) excludes all
		 * filters}.
		 */
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

	/**
	 * Provider of popup menus with two buttons:
	 * <ul>
	 * <li>button to {@link EditFilterAction edit} a filter,</li>
	 * <li>button to {@link RemoveFilterAction remove} a filter.</li></ul>
	 */
	protected class FiltersTablePopupProvider implements TablePopupMenuProvider {

		/**
		 * a popup menu with two buttons:
		 * <ul>
		 * <li>button to {@link EditFilterAction edit} a filter,</li>
		 * <li>button to {@link RemoveFilterAction remove} a filter</li>
		 * </ul>
		 * .
		 */
		private JPopupMenu popupMenu;

		/**
		 * Returns a {@link #getDefaultPopupMenu() default popup menu}.
		 */
		@Override
		public JPopupMenu getPopupMenu(int col, int row) {
			return getDefaultPopupMenu();
		}

		/**
		 * Returns a {@link #getDefaultPopupMenu() default popup menu}.
		 * 
		 * @return the a popup menu with two buttons:
		 *         <ul>
		 *         <li>button to {@link EditFilterAction edit} a filter,</li>
		 *         <li>button to {@link RemoveFilterAction remove} a filter</li>
		 *         </ul>
		 */
		@Override
		public JPopupMenu getPopupMenu() {
			return getPopupMenu(-1,-1);
		}

		/**
		 * Provides a popup menu with two buttons:
		 * <ul>
		 * <li>button to {@link EditFilterAction edit} a filter,</li>
		 * <li>button to {@link RemoveFilterAction remove} a filter.</li></ul>
		 * @return a popup menu with two buttons:
		 * <ul>
		 * <li>button to {@link EditFilterAction edit} a filter,</li>
		 * <li>button to {@link RemoveFilterAction remove} a filter.</li></ul>
		 */
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

	/**
	 * Mouse handler which {@link EditFilterAction edits} a filter
	 * when it is double clicked.
	 */
	protected class FiltersTableMouseHandler extends MouseAdapter {

		/**
		 * When the double click with the left mouse button occurs {@link
		 * EditFilterAction} is performed.
		 */
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

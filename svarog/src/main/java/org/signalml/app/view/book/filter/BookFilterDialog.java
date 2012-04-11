/* BookFilterDialog.java created 2008-03-04
 *
 */

package org.signalml.app.view.book.filter;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.config.preset.BookFilterPresetManager;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.model.book.BookFilterDescriptor;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.dialogs.AbstractPresetDialog;
import org.signalml.domain.book.filter.AtomFilterChain;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/** BookFilterDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookFilterDialog extends AbstractPresetDialog {

	private static final long serialVersionUID = 1L;

	private ParameterRangeFilterDialog parameterFilterDialog;
	private TagBasedFilterDialog tagBasedFilterDialog;
	private DelegatingFilterDialog delegatingFilterDialog;

	private JCheckBox filteringEnabledCheckBox;

	private ButtonGroup conditionButtonGroup;
	private JRadioButton alternativeRadioButton;
	private JRadioButton conjunctionRadioButton;

	private BookFilterTablePanel tablePanel;

	private AtomFilterChain currentChain;

	private URL contextHelpURL = null;

	public BookFilterDialog(BookFilterPresetManager presetManager, Window w, boolean isModal) {
		super(presetManager, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Edit book filter"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/editbookfilter.png"));
		super.initialize();
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			try {
				contextHelpURL = (new ClassPathResource("org/signalml/help/contents.html")).getURL();
				contextHelpURL = new URL(contextHelpURL.toExternalForm() + "#bookFilter");
			} catch (IOException ex) {
				logger.error("Failed to get help URL", ex);
			}
		}
		return contextHelpURL;
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel masterSwitchPanel = new JPanel(new BorderLayout(3,3));

		masterSwitchPanel.setBorder(new CompoundBorder(
		                                    new TitledBorder(_("Filtering master switch")),
		                                    new EmptyBorder(3,3,3,3)
		                            ));

		JLabel filteringEnabledLabel = new JLabel(_("Enable book filtering"));

		masterSwitchPanel.add(filteringEnabledLabel, BorderLayout.CENTER);
		masterSwitchPanel.add(getFilteringEnabledCheckBox(), BorderLayout.EAST);

		JPanel conditionPanel = new JPanel(new GridLayout(1,2,3,3));

		conditionPanel.setBorder(new CompoundBorder(
		                                 new TitledBorder(_("Condition type")),
		                                 new EmptyBorder(3,3,3,3)
		                         ));

		conditionPanel.add(getConjunctionRadioButton());
		conditionPanel.add(getAlternativeRadioButton());

		JPanel topPanel = new JPanel(new GridLayout(1,2,3,0));

		topPanel.add(masterSwitchPanel);
		topPanel.add(conditionPanel);

		interfacePanel.add(topPanel, BorderLayout.NORTH);
		interfacePanel.add(getTablePanel(), BorderLayout.CENTER);

		return interfacePanel;

	}

	public BookFilterTablePanel getTablePanel() {
		if (tablePanel == null) {
			tablePanel = new BookFilterTablePanel(this);
			tablePanel.setParameterFilterDialog(getParameterFilterDialog());
			tablePanel.setTagBasedFilterDialog(getTagBasedFilterDialog());
			tablePanel.setDelegatingFilterDialog(getDelegatingFilterDialog());
		}
		return tablePanel;
	}

	public JCheckBox getFilteringEnabledCheckBox() {
		if (filteringEnabledCheckBox == null) {
			filteringEnabledCheckBox = new JCheckBox();

			filteringEnabledCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (currentChain != null) {
						boolean selected = getFilteringEnabledCheckBox().isSelected();
						if (selected != currentChain.isFilteringEnabled()) {
							currentChain.setFilteringEnabled(selected);
						}
					}
				}

			});
		}
		return filteringEnabledCheckBox;
	}

	public ButtonGroup getConditionButtonGroup() {
		if (conditionButtonGroup == null) {
			conditionButtonGroup = new ButtonGroup();
		}
		return conditionButtonGroup;
	}

	public JRadioButton getConjunctionRadioButton() {
		if (conjunctionRadioButton == null) {

			conjunctionRadioButton = new JRadioButton(_("Satisfy all (AND)"));
			conjunctionRadioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			getConditionButtonGroup().add(conjunctionRadioButton);

			conjunctionRadioButton.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					if (e.getStateChange() == ItemEvent.SELECTED) {

						if (currentChain.isAlternative()) {
							currentChain.setAlternative(false);
						}

					}

				}

			});

		}
		return conjunctionRadioButton;
	}

	public JRadioButton getAlternativeRadioButton() {
		if (alternativeRadioButton == null) {

			alternativeRadioButton = new JRadioButton(_("Satisfy any (OR)"));
			alternativeRadioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			getConditionButtonGroup().add(alternativeRadioButton);

			alternativeRadioButton.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					if (e.getStateChange() == ItemEvent.SELECTED) {

						if (!currentChain.isAlternative()) {
							currentChain.setAlternative(true);
						}

					}

				}

			});

		}
		return alternativeRadioButton;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		if (model instanceof AtomFilterChain) {

			// for presets
			currentChain = new AtomFilterChain((AtomFilterChain) model);

		} else {

			BookFilterDescriptor descriptor = (BookFilterDescriptor) model;

			currentChain = descriptor.getChain().duplicate();

		}

		getTablePanel().fillPanelFromModel(currentChain);

		getFilteringEnabledCheckBox().setSelected(currentChain.isFilteringEnabled());

		if (currentChain.isAlternative()) {
			getAlternativeRadioButton().setSelected(true);
		} else {
			getConjunctionRadioButton().setSelected(true);
		}

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		BookFilterDescriptor descriptor = (BookFilterDescriptor) model;

		// current chain was being edited in place

		getTablePanel().fillModelFromPanel(currentChain);

		descriptor.setChain(currentChain);

	}

	@Override
	public Preset getPreset() throws SignalMLException {

		AtomFilterChain preset = currentChain.duplicate();

		ValidationErrors errors = new ValidationErrors();
		validateDialog(preset, errors);

		if (errors.hasErrors()) {
			showValidationErrors(errors);
			return null;
		}

		return preset;

	}

	@Override
	public void setPreset(Preset preset) throws SignalMLException {

		fillDialogFromModel(preset);

	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {

		super.validateDialog(model, errors);

		getTablePanel().validatePanel(errors);

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return BookFilterDescriptor.class.isAssignableFrom(clazz);
	}

	public ParameterRangeFilterDialog getParameterFilterDialog() {
		if (parameterFilterDialog == null) {
			parameterFilterDialog = new ParameterRangeFilterDialog(this,true);
		}
		return parameterFilterDialog;
	}

	public TagBasedFilterDialog getTagBasedFilterDialog() {
		if (tagBasedFilterDialog == null) {
			tagBasedFilterDialog = new TagBasedFilterDialog(this,true);
			tagBasedFilterDialog.setFileChooser(getFileChooser());
		}
		return tagBasedFilterDialog;
	}

	public DelegatingFilterDialog getDelegatingFilterDialog() {
		if (delegatingFilterDialog == null) {
			delegatingFilterDialog = new DelegatingFilterDialog(this,true);
			delegatingFilterDialog.setFileChooser(getFileChooser());
		}
		return delegatingFilterDialog;
	}

}

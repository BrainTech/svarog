/* TaggingConfigPanel.java created 2007-12-15
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.PagingParameterDescriptor;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.validation.Errors;

/**
 * The panel with options for tags.
 * Contains two sub-panels:
 * <ul>
 * <li>the panel with a {@link #getSaveFullMontageWithTagCheckBox()
 * check-box} to select if the montage should be saved in the tag file,</li>
 * <li>the {@link PagingParametersPanel panel} which allows to select the
 * sizes of a block and a page.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaggingConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the panel which allows to set the size of a block and a page
	 */
	private PagingParametersPanel pagingParametersPanel;
	/**
	 * the check-box which tells if the full {@link Montage montage} should
	 * be saved in the tag file 
	 */
	private JCheckBox saveFullMontageWithTagCheckBox;

	/**
	 * Constructor. Initializes the panel.
	 */
	public TaggingConfigPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with BorderLayout and two sub-panels:
	 * <ul>
	 * <li>the panel with a {@link #getSaveFullMontageWithTagCheckBox()
	 * check-box} to select if the montage should be saved in the tag file,</li>
	 * <li>the {@link PagingParametersPanel panel} which allows to select the
	 * sizes of a block and a page.</li>
	 * </ul>
	 */
	private void initialize() {

		setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BorderLayout());

		JPanel generalPanel = new JPanel();
		generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
		generalPanel.setBorder(new CompoundBorder(
		                               new TitledBorder(_("General")),
		                               new EmptyBorder(3,3,3,3)
		                       ));

		generalPanel.add(getSaveFullMontageWithTagCheckBox());

		pagingParametersPanel = new PagingParametersPanel();

		add(generalPanel, BorderLayout.CENTER);
		add(pagingParametersPanel, BorderLayout.SOUTH);

	}

	/**
	 * Returns the check-box which tells if the full {@link Montage montage}
	 * should be saved in the tag file.
	 * If the check-box doesn't exist it is created.  
	 * @return the check-box which tells if the full montage should
	 * be saved in the tag file 
	 */
	public JCheckBox getSaveFullMontageWithTagCheckBox() {
		if (saveFullMontageWithTagCheckBox == null) {
			saveFullMontageWithTagCheckBox = new JCheckBox(_("Save full montage with tag file"));
		}
		return saveFullMontageWithTagCheckBox;
	}

	/**
	 * Fills the fields of this panel from the given
	 * {@link ApplicationConfiguration configuration}:
	 * <ul>
	 * <li>the check-box if the {@link Montage} should be saved in the tag file,</li>
	 * <li>the {@link PagingParametersPanel panel} with the size of a block and
	 * a page of the signal.</li>
	 * </ul>
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) {

		getSaveFullMontageWithTagCheckBox().setSelected(applicationConfig.isSaveFullMontageWithTag());

		PagingParameterDescriptor descriptor = new PagingParameterDescriptor();
		descriptor.setPageSize(applicationConfig.getPageSize());
		descriptor.setBlocksPerPage(applicationConfig.getBlocksPerPage());
		descriptor.setPageSizeEditable(true);
		descriptor.setBlocksPerPageEditable(true);

		pagingParametersPanel.fillPanelFromModel(descriptor);

	}

	/**
	 * Writes the values of the fields from this panel to the
	 * {@link ApplicationConfiguration configuration} of Svarog:
	 * <ul>
	 * <li>the information if the {@link Montage} should be saved in the tag
	 * file,</li>
	 * <li> the size of a block and a page of the signal.</li></ul>
	 * @param applicationConfig the configuration of Svarog
	 * @throws SignalMLException if the numbers in the fields in {@link
	 * PagingParametersPanel} have invalid format
	 */
	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) throws SignalMLException {

		applicationConfig.setSaveFullMontageWithTag(getSaveFullMontageWithTagCheckBox().isSelected());

		PagingParameterDescriptor descriptor = new PagingParameterDescriptor();

		pagingParametersPanel.fillModelFromPanel(descriptor);

		applicationConfig.setPageSize(descriptor.getPageSize());
		applicationConfig.setBlocksPerPage(descriptor.getBlocksPerPage());

	}

	/**
	 * Validates this panel.
	 * This panel is valid if {@link PagingParametersPanel} is
	 * {@link PagingParametersPanel#validatePanel(Errors) valid}.
	 * @param errors the variable in which errors are stored.
	 */
	public void validate(Errors errors) {

		pagingParametersPanel.validatePanel(errors);

	}

}

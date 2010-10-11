/* TaggingConfigPanel.java created 2007-12-15
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.PagingParameterDescriptor;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** TaggingConfigPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaggingConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private PagingParametersPanel pagingParametersPanel;
	private JCheckBox saveFullMontageWithTagCheckBox;

	public TaggingConfigPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BorderLayout());

		JPanel generalPanel = new JPanel();
		generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
		generalPanel.setBorder(new CompoundBorder(
		                               new TitledBorder(messageSource.getMessage("preferences.tagging.general")),
		                               new EmptyBorder(3,3,3,3)
		                       ));

		generalPanel.add(getSaveFullMontageWithTagCheckBox());

		pagingParametersPanel = new PagingParametersPanel(messageSource);

		add(generalPanel, BorderLayout.CENTER);
		add(pagingParametersPanel, BorderLayout.SOUTH);

	}

	public JCheckBox getSaveFullMontageWithTagCheckBox() {
		if (saveFullMontageWithTagCheckBox == null) {
			saveFullMontageWithTagCheckBox = new JCheckBox(messageSource.getMessage("preferences.tagging.saveFullMontageWithTag"));
		}
		return saveFullMontageWithTagCheckBox;
	}

	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) {

		getSaveFullMontageWithTagCheckBox().setSelected(applicationConfig.isSaveFullMontageWithTag());

		PagingParameterDescriptor descriptor = new PagingParameterDescriptor();
		descriptor.setPageSize(applicationConfig.getPageSize());
		descriptor.setBlocksPerPage(applicationConfig.getBlocksPerPage());
		descriptor.setPageSizeEditable(true);
		descriptor.setBlocksPerPageEditable(true);

		pagingParametersPanel.fillPanelFromModel(descriptor);

	}

	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) throws SignalMLException {

		applicationConfig.setSaveFullMontageWithTag(getSaveFullMontageWithTagCheckBox().isSelected());

		PagingParameterDescriptor descriptor = new PagingParameterDescriptor();

		pagingParametersPanel.fillModelFromPanel(descriptor);

		applicationConfig.setPageSize(descriptor.getPageSize());
		applicationConfig.setBlocksPerPage(descriptor.getBlocksPerPage());

	}

	public void validate(Errors errors) {

		pagingParametersPanel.validatePanel(errors);

	}

}

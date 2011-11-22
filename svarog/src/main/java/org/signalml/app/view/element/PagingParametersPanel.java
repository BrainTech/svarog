/* PagingParametersPanel.java created 2007-09-17
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogI18n._;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.PagingParameterDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.plugin.export.SignalMLException;

import org.springframework.validation.Errors;


/**
 * Panel with two text fields (with labels):
 * <ul>
 * <li>the text field with the size of the page of signal in seconds,</li>
 * <li>the text field with the number of blocks that fit into one page of
 * the signal.</li>
 * </ul>
 * Depending on the model these fields may be or may be not editable.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PagingParametersPanel extends AbstractSignalMLPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(PagingParametersPanel.class);

	/**
	 * the text field with the size of the page of signal in seconds
	 */
	private JTextField pageSizeField;
	
	/**
	 * the text field with the number of blocks that fit into one page of
	 * the signal
	 */
	private JTextField blocksPerPageField;

	/**
	 * Constructor. Initializes the panel.
	 */
	public PagingParametersPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel.
	 * This panel contains two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for text fields. This group positions the elements in two columns.</li>
	 * <li>vertical group which has 2 sub-groups - one for every row:
	 * <ul>
	 * <li>the label and the text field with the size of the page of signal in
	 * seconds,</li>
	 * <li>the label and the text field with the number of blocks that fit into
	 * one page of the signal.</li>
	 * </ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 */
	private void initialize() {

		CompoundBorder cb = new CompoundBorder(
		        new TitledBorder(_("Page & block parameters")),
		        new EmptyBorder(3,3,3,3)
		);

		setBorder(cb);

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel pageSizeLabel = new JLabel(_("Page size (s)"));
		JLabel blocksPerPageLabel = new JLabel(_("Number of blocks per page"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(pageSizeLabel)
		        .addComponent(blocksPerPageLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getPageSizeField())
		        .addComponent(getBlocksPerPageField())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(pageSizeLabel)
				.addComponent(getPageSizeField())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(blocksPerPageLabel)
				.addComponent(getBlocksPerPageField())
			);

		layout.setVerticalGroup(vGroup);

	}

	/**
	 * Returns the text field with the size of the page of signal in seconds.
	 * If it doesn't exist it is created.
	 * @return the text field with the size of the page of signal in seconds
	 */
	public JTextField getPageSizeField() {
		if (pageSizeField == null) {
			pageSizeField = new JTextField();
			pageSizeField.setPreferredSize(new Dimension(200,25));
		}
		return pageSizeField;
	}

	/**
	 * Returns the text field with the number of blocks that fit into one page
	 * of the signal.
	 * If it doesn't exist it is created.
	 * @return the text field with the number of blocks that fit into one page
	 * of the signal
	 */
	public JTextField getBlocksPerPageField() {
		if (blocksPerPageField == null) {
			blocksPerPageField = new JTextField();
			blocksPerPageField.setPreferredSize(new Dimension(200,25));
		}
		return blocksPerPageField;
	}

	/**
	 * Fills the fields of this dialog using the given
	 * {@link PagingParameterDescriptor descriptor}:
	 * <ul>
	 * <li>the {@link PagingParameterDescriptor#getPageSize() page size},</li>
	 * <li>the {@link PagingParameterDescriptor#getBlocksPerPage() number}
	 * of blocks in a single page of signal,</li>
	 * <li>if the {@link PagingParameterDescriptor#isPageSizeEditable() page
	 * size} and {@link PagingParameterDescriptor#isBlocksPerPageEditable()
	 * blocks per page} text fields should be editable,</li>
	 * </ul>
	 * @param spd the descriptor
	 */
	public void fillPanelFromModel(PagingParameterDescriptor spd) {

		Float pageSize = spd.getPageSize();
		if (pageSize != null) {
			getPageSizeField().setText(pageSize.toString());
		} else {
			getPageSizeField().setText("");
		}

		Integer blocksPerPage = spd.getBlocksPerPage();
		if (blocksPerPage != null) {
			getBlocksPerPageField().setText(blocksPerPage.toString());
		} else {
			getBlocksPerPageField().setText("");
		}

		if (spd.isPageSizeEditable()) {
			getPageSizeField().setEditable(true);
			getPageSizeField().setToolTipText(null);
		} else {
			getPageSizeField().setEditable(false);
			getPageSizeField().setToolTipText(_("This parameter is inherited from the active tag and may not be changed as long as any tags are open"));
		}

		if (spd.isBlocksPerPageEditable()) {
			getBlocksPerPageField().setEditable(true);
			getBlocksPerPageField().setToolTipText(null);
		} else {
			getBlocksPerPageField().setEditable(false);
			getBlocksPerPageField().setToolTipText(_("This parameter is inherited from the active tag and may not be changed as long as any tags are open"));
		}

	}

	/**
	 * Fills the {@link PagingParameterDescriptor descriptor} with the user
	 * input from this panel:
	 * <ul><li>the {@link PagingParameterDescriptor#setPageSize(Float) page
	 * size},</li>
	 * <li>the {@link PagingParameterDescriptor#setBlocksPerPage(Integer)
	 * number} of block in a page of the signal.</li>
	 * </ul>
	 * @param spd the descriptor
	 * @throws SignalMLException if the numbers in fields have invalid format
	 */
	public void fillModelFromPanel(PagingParameterDescriptor spd) throws SignalMLException {
		try {
			if (spd.isPageSizeEditable()) {
				spd.setPageSize(new Float(getPageSizeField().getText()));
			}
			if (spd.isBlocksPerPageEditable()) {
				spd.setBlocksPerPage(new Integer(getBlocksPerPageField().getText()));
			}
		} catch (NumberFormatException ex) {
			throw new SignalMLException(ex);
		}
	}

	/**
	 * Fills the fields of this panel using the given
	 * {@link RawSignalDescriptor descriptor}:
	 * <ul>
	 * <li>the {@link RawSignalDescriptor#getPageSize() page size},</li>
	 * <li>the {@link RawSignalDescriptor#getBlocksPerPage() number}
	 * of blocks in a single page of signal,</li>
	 * </ul>
	 * @param descriptor the descriptor
	 */
	public void fillPanelFromModel(RawSignalDescriptor descriptor) {

		getPageSizeField().setText(Float.toString(descriptor.getPageSize()));
		getBlocksPerPageField().setText(Integer.toString(descriptor.getBlocksPerPage()));

	}

	/**
	 * Fills the {@link RawSignalDescriptor descriptor} with the user
	 * input from this panel:
	 * <ul><li>the {@link RawSignalDescriptor#setPageSize(float) page
	 * size},</li>
	 * <li>the {@link RawSignalDescriptor#setBlocksPerPage(int)
	 * number} of block in a page of the signal.</li>
	 * </ul>
	 * @param descriptor the descriptor
	 * @throws NumberFormatException if the numbers in fields have invalid
	 * format
	 * TODO method {@link #fillModelFromPanel(PagingParameterDescriptor)}
	 * catches this exception, maybe this also should?
	 */
	public void fillModelFromPanel(RawSignalDescriptor descriptor) {

		descriptor.setPageSize(Float.parseFloat(getPageSizeField().getText()));
		descriptor.setBlocksPerPage(Integer.parseInt(getBlocksPerPageField().getText()));

	}

	/**
	 * Validates this panel.
	 * Dialog is valid if the numbers in the text fields have valid format and
	 * are positive.
	 * @param errors the object in which the errors are stored
	 */
	public void validatePanel(Errors errors) {
		try {
			float pageSize = Float.parseFloat(getPageSizeField().getText());
			if (pageSize <= 0) {
				errors.rejectValue("pageSize", "error.pageSizeNegative", _("Page size must be positive"));
			}
		} catch (NumberFormatException ex) {
			errors.rejectValue("pageSize", "error.invalidNumber", _("Invalid numeric value"));
		}
		try {
			int blocksPerPage = Integer.parseInt(getBlocksPerPageField().getText());
			if (blocksPerPage <= 0) {
				errors.rejectValue("blocksPerPage", "error.blocksPerPageNegative", _("Block count must be positive"));
			}
		} catch (NumberFormatException ex) {
			errors.rejectValue("blocksPerPage", "error.invalidNumber", _("Invalid numeric value"));
		}
	}

}

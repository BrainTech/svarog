/* EditMontageReferencePanel.java created 2007-10-24
 *
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.montage.ReferenceTableModel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageChannel;

/**
 * The panel which displays the reference between {@link MontageChannel montage
 * channels} and original channels.
 * This panel contains the {@link ReferenceTable} within a scroll pane.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MatrixReferenceEditorPanel extends JPanel {

	/**
	 * the default serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the {@link Montage montage} which is the model for this panel
	 */
	private Montage montage;

	/**
	 * the {@link ReferenceTableModel model} for the {@link #referenceTable}
	 */
	private ReferenceTableModel referenceTableModel;

	/**
	 * the {@link ReferenceTable table} which displays the reference between
	 * {@link MontageChannel montage channels} and original channels
	 */
	private ReferenceTable referenceTable;

	/**
	 * the scroll pane with the {@link #referenceTable}
	 */
	private JScrollPane scrollPane;

	/**
	 * Constructor. Sets the source of messages (labels) and
	 * {@link #initialize() initializes} this panel.
	 */
	public MatrixReferenceEditorPanel() {
		super();
		initialize();
	}

	/**
	 * Sets the border with the title and adds the {@link #getScrollPane()
	 * scroll pane} with the {@link #getReferenceTable() reference table}.
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Edit reference")),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		add(getScrollPane(), BorderLayout.CENTER);

	}

	/**
	 * Returns the {@link Montage montage} which is the model for this panel.
	 * @return the montage which is the model for this panel
	 */
	public Montage getMontage() {
		return montage;
	}

	/**
	 * Sets the {@link Montage montage} to be the model for this panel.
	 * Also updates the montage in the {@link #getReferenceTableModel()
	 * ReferenceTableModel}.
	 * @param montage the montage
	 */
	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			this.montage = montage;
			getReferenceTableModel().setMontage(montage);
		}
	}

	/**
	 * Returns the {@link ReferenceTable table} which displays the reference
	 * between {@link MontageChannel montage channels} and original channels.
	 * If the table doesn't exist it is created
	 * @return the ReferenceTable
	 */
	public ReferenceTable getReferenceTable() {
		if (referenceTable == null) {
			referenceTable = new ReferenceTable(getReferenceTableModel());
		}
		return referenceTable;
	}

	/**
	 * Returns the {@link ReferenceTableModel model} for the {@link
	 * #getReferenceTable() reference table}.
	 * @return the model for the ReferenceTable
	 */
	public ReferenceTableModel getReferenceTableModel() {
		if (referenceTableModel == null) {
			referenceTableModel = new ReferenceTableModel();
		}
		return referenceTableModel;
	}

	/**
	 * Returns the scroll pane with the {@link #getReferenceTable() reference
	 * table}.
	 * @return the scroll pane with the reference table
	 */
	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getReferenceTable());
		}
		return scrollPane;
	}

}

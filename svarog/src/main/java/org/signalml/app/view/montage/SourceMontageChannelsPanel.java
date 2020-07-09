/* EditFixedMontagePanel.java created 2007-11-02
 *
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.model.montage.SourceMontageTableModel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.montage.SourceMontage;

/** EditFixedMontagePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageChannelsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private SourceMontage montage;

	private SourceMontageTableModel sourceMontageTableModel;

	private SourceMontageTable sourceMontageTable;
	private JScrollPane sourceScrollPane;

	public SourceMontageChannelsPanel() {
		super();
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Source montage")),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		add(getSourceScrollPane(), BorderLayout.CENTER);

	}

	public SourceMontage getMontage() {
		return montage;
	}

	public void setMontage(SourceMontage montage) {
		if (this.montage != montage) {
			this.montage = montage;
			getSourceMontageTableModel().setMontage(montage);
		}
	}

	public SourceMontageTableModel getSourceMontageTableModel() {
		if (sourceMontageTableModel == null) {
			sourceMontageTableModel = new SourceMontageTableModel();
		}
		return sourceMontageTableModel;
	}

	public SourceMontageTable getSourceMontageTable() {
		if (sourceMontageTable == null) {
			sourceMontageTable = new SourceMontageTable(getSourceMontageTableModel());
		}
		return sourceMontageTable;
	}

	public JScrollPane getSourceScrollPane() {
		if (sourceScrollPane == null) {
			sourceScrollPane = new JScrollPane(getSourceMontageTable());
		}
		return sourceScrollPane;
	}

}

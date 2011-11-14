/* ArtifactExclusionPanel.java created 2007-11-02
 *
 */
package org.signalml.plugin.newartifact.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import static org.signalml.plugin.newartifact.NewArtifactPlugin._;

/** ArtifactExclusionPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewArtifactExclusionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private NewArtifactExclusionTable artifactExclusionTable;
	private JScrollPane scrollPane;

	public  NewArtifactExclusionPanel() {
		super();
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(_("Exclusion")),
			new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		add(getScrollPane(), BorderLayout.CENTER);

	}

	public NewArtifactExclusionTable getArtifactExclusionTable() {
		if (artifactExclusionTable == null) {
			NewArtifactExclusionTableModel artifactExclusionTableModel = new NewArtifactExclusionTableModel();
			artifactExclusionTable = new NewArtifactExclusionTable(artifactExclusionTableModel);
		}
		return artifactExclusionTable;
	}

	public JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(getArtifactExclusionTable());
		}
		return scrollPane;
	}

}
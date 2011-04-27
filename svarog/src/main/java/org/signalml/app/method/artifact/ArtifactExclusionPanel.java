/* ArtifactExclusionPanel.java created 2007-11-02
 *
 */
package org.signalml.app.method.artifact;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.springframework.context.support.MessageSourceAccessor;

/** ArtifactExclusionPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactExclusionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private ArtifactExclusionTable artifactExclusionTable;
	private JScrollPane scrollPane;

	public ArtifactExclusionPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());
		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(messageSource.getMessage("artifactMethod.dialog.exclusionTable")),
		        new EmptyBorder(3,3,3,3)
		);
		setBorder(border);

		add(getScrollPane(), BorderLayout.CENTER);

	}

	public ArtifactExclusionTable getArtifactExclusionTable() {
		if (artifactExclusionTable == null) {
			ArtifactExclusionTableModel artifactExclusionTableModel = new ArtifactExclusionTableModel(messageSource);
			artifactExclusionTable = new ArtifactExclusionTable(artifactExclusionTableModel);
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

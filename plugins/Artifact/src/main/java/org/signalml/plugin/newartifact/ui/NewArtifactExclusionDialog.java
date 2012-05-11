/* ArtifactExclusionDialog.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.ui;

import static org.signalml.plugin.newartifact.NewArtifactPlugin._;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.newartifact.data.NewArtifactExclusionDescriptor;
import org.signalml.plugin.newartifact.data.NewArtifactType;

/**
 * ArtifactExclusionDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o. (dialog design based on work by Hubert Klekowicz)
 */
public class NewArtifactExclusionDialog extends org.signalml.plugin.export.view.AbstractPluginDialog  {

	private static final long serialVersionUID = 1L;

	private NewArtifactExclusionPanel artifactExclusionPanel;

	private int[][] currentExclusion;
	private NewArtifactType[] artifactTypes = NewArtifactType.values();

	public NewArtifactExclusionDialog() {
		super();
	}

	public NewArtifactExclusionDialog(
		Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Exclusion"));
		setIconImage(IconUtils
					 .loadClassPathImage("org/signalml/app/icon/editexclusion.png"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		artifactExclusionPanel = new NewArtifactExclusionPanel();

		return artifactExclusionPanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		NewArtifactExclusionDescriptor descriptor = (NewArtifactExclusionDescriptor) model;
		SourceMontage montage = descriptor.getMontage();

		int channelCount = montage.getSourceChannelCount();
		int[][] exclusion = descriptor.getExclusion();
		currentExclusion = new int[artifactTypes.length][channelCount];
		for (int i = 0; i < Math.min(artifactTypes.length, exclusion.length); i++) {
			for (int e = 0; e < Math.min(channelCount, exclusion[i].length); e++) {
				currentExclusion[i][e] = exclusion[i][e];
			}
		}

		artifactExclusionPanel.getArtifactExclusionTable().getModel()
		.setExcludedChannelsAndMontage(currentExclusion, montage);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		NewArtifactExclusionDescriptor descriptor = (NewArtifactExclusionDescriptor) model;
		SourceMontage montage = descriptor.getMontage();

		int channelCount = montage.getSourceChannelCount();
		int[][] exclusion = descriptor.getExclusion();
		for (int i = 0; i < Math.min(artifactTypes.length, exclusion.length); i++) {
			for (int e = 0; e < Math.min(channelCount, exclusion[i].length); e++) {
				exclusion[i][e] = currentExclusion[i][e];
			}
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return NewArtifactExclusionDescriptor.class.isAssignableFrom(clazz);
	}

}
/* ArtifactExclusionDialog.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.ui;

import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.view.AbstractDialog;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.method.artifact.ArtifactType;
import org.signalml.plugin.newartifact.data.NewArtifactExclusionDescriptor;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * ArtifactExclusionDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o. (dialog design based on work by Hubert Klekowicz)
 */
public class NewArtifactExclusionDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private NewArtifactExclusionPanel artifactExclusionPanel;

	private int[][] currentExclusion;
	private ArtifactType[] artifactTypes = ArtifactType.values();

	public NewArtifactExclusionDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public NewArtifactExclusionDialog(MessageSourceAccessor messageSource,
					  Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource
			 .getMessage("newArtifactMethod.dialog.exclusionTable"));
		setIconImage(IconUtils
			     .loadClassPathImage("org/signalml/app/icon/editexclusion.png"));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		artifactExclusionPanel = new NewArtifactExclusionPanel(messageSource);

		return artifactExclusionPanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		NewArtifactExclusionDescriptor descriptor = (NewArtifactExclusionDescriptor) model;
		SourceMontage montage = descriptor.getMontage();

		int channelCount = montage.getSourceChannelCount();
		int i, e;
		int[][] exclusion = descriptor.getExclusion();
		currentExclusion = new int[artifactTypes.length][channelCount];
		for (i = 0; i < artifactTypes.length; i++) {
			for (e = 0; e < channelCount; e++) {
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
		int i, e;
		int[][] exclusion = descriptor.getExclusion();
		for (i = 0; i < artifactTypes.length; i++) {
			for (e = 0; e < channelCount; e++) {
				exclusion[i][e] = currentExclusion[i][e];
			}
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return NewArtifactExclusionDescriptor.class.isAssignableFrom(clazz);
	}

}
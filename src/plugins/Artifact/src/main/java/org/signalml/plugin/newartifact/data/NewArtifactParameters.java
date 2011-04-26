/* ArtifactParameters.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.data;

import java.io.Serializable;
import java.util.Arrays;

import org.signalml.app.config.preset.Preset;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** ArtifactParameters
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("artifactparameters")
public class NewArtifactParameters implements Serializable, Preset {

	private static final long serialVersionUID = 1L;

	private String name;

	private float powerGridFrequency;

	private int[] chosenArtifactTypes;
	private float[] sensitivities;

	public NewArtifactParameters() {
		int cnt = NewArtifactType.values().length;
		chosenArtifactTypes = new int[cnt];
		sensitivities = new float[cnt];

		Arrays.fill(chosenArtifactTypes, 1);
		Arrays.fill(sensitivities, 50F);

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public float getPowerGridFrequency() {
		return powerGridFrequency;
	}

	public void setPowerGridFrequency(float powerGridFrequency) {
		this.powerGridFrequency = powerGridFrequency;
	}

	public int[] getChosenArtifactTypes() {
		return chosenArtifactTypes;
	}

	public void setChosenArtifactTypes(int[] chosenArtifactTypes) {
		this.chosenArtifactTypes = chosenArtifactTypes;
	}

	public float[] getSensitivities() {
		return sensitivities;
	}

	public void setSensitivities(float[] sensitivities) {
		this.sensitivities = sensitivities;
	}

	public float getSensitivity(int index) {
		return sensitivities[index];
	}

	public void setSensitivity(int index, float value) {
		sensitivities[index] = value;
	}

	public float getSensitivity(NewArtifactType artifactType) {
		return sensitivities[artifactType.ordinal()];
	}

	public void setSensitivity(NewArtifactType artifactType, float value) {
		sensitivities[artifactType.ordinal()] = value;
	}

	@Override
	public String toString() {
		return name;
	}

}
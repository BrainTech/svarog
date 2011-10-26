package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.ArrayList;
import java.util.List;
import org.signalml.app.config.preset.Preset;
import org.signalml.domain.montage.generators.CommonAverageMontageGenerator;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.generators.MontageGeneratorsConverter;
import org.signalml.domain.montage.generators.RawMontageGenerator;

/**
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegSystem")
public class EegSystem implements Preset {

	private String name;

	private List<EegElectrode> electrodes = new ArrayList<EegElectrode>();

	@XStreamAlias("montageGenerators")
	@XStreamConverter(MontageGeneratorsConverter.class)
	private List<IMontageGenerator> montageGenerators = new ArrayList<IMontageGenerator>();

	public EegSystem() {
		MontageGeneratorsConverter.addDefaultMontageGenerators(montageGenerators);
	}

	public EegSystem(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfElectrodes() {
		return electrodes.size();
	}

	public EegElectrode getElectrodeAt(int index) {
		return electrodes.get(index);
	}

	public void addElectrode(EegElectrode electrode) {
		electrodes.add(electrode);
	}

	public EegElectrode getElectrode(String electrodeName) {
		for (EegElectrode electrode: electrodes) {
			if (electrode.getLabel().equals(electrodeName)) {
				return electrode;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		if ( !(obj instanceof EegSystem) ) return false;

		EegSystem other = (EegSystem) obj;
		if (other.getName().compareTo(this.name) == 0)
			return true;
		return false;

	}

	public IMontageGenerator getMontageGeneratorAt(int i) {
		return montageGenerators.get(i);
	}

	public int getNumberOfMontageGenerators() {
		return montageGenerators.size();
	}

}

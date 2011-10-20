package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;
import org.signalml.app.config.preset.Preset;

/**
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegSystem")
public class EegSystem implements Preset {

	private String name;
	private List<EegElectrode> electrodes = new ArrayList<EegElectrode>();

	public EegSystem() {
	}

	public EegSystem(String name) {
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

	public void addElectrode(EegElectrode electrode) {
		electrodes.add(electrode);
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

}

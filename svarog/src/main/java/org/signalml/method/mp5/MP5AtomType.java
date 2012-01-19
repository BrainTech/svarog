/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.signalml.method.mp5;

import java.io.Serializable;
import static org.signalml.app.SvarogI18n._;

/**
 *
 * @author Piotr Szachewicz
 */
public enum MP5AtomType implements Serializable {

	DIRAC(_("Dirac"), "diracInDictionary"),
	GAUSS(_("Gauss"), "gaussInDictionary"),
	SINCOS(_("Sin/cos"), "sinCosInDictionary"),
	GABOR(_("Gabor"), "gaborInDictionary");

	private String name;
	private String configName;

	private MP5AtomType(String name, String configName) {
		this.name = name;
		this.configName = configName;
	}

	public String getName() {
		return name;
	}

	public String getConfigName() {
		return configName;
	}

	@Override
	public String toString() {
		return name;
	}
}

package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * The unique identifier of this EEG system - consists of the EEG system symbol
 * (e.g. 'EEG 10_20 Cap33') and the type of this EEG system (e.g. 'children'/'adults').
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegSystemName")
public class EegSystemName {

	/**
	 * The symbol identifying this EEG system.
	 */
	@XStreamAlias("eegSystemSymbol")
	private String symbol;
	/**
	 * The type of this EEG system.
	 */
	@XStreamAlias("eegSystemType")
	private String type;

	public EegSystemName() {
	}

	/**
	 * Constructor.
	 * @param symbol the symbol used to identify this EEG system
	 * @param type the type of the EEG system (children/adults)
	 */
	public EegSystemName(String symbol, String type) {
		this.symbol = symbol;
		this.type = type;
	}

	/**
	 * Returns the symbol of this EEG system (e.g. 'EEG 10_20 Cap25')
	 * @return the symbol of this EEG system
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Sets the symbol for this EEG system.
	 * @param symbol the new symbol of this EEG system
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Returns the type of this EEG system ('adults'/'children')
	 * @return the type of this EEG system
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of this EEG system.
	 * @param type the new type of this EEG system
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the full name of this EEG system. The full name consists
	 * of the symbol of this EEG system and the type of the EEG system
	 * in brackets (but only if the type is defined).
	 * For example: if the systems's symbol is 'EEG 10_20 Cap25' and the type
	 * is 'children' then this method returns 'EEG 10_20 Cap25 (children)'.
	 * @return
	 */
	public String getFullName() {

		StringBuffer str = new StringBuffer();
		str.append(symbol);
		if (type != null && !type.isEmpty()) {
			str.append(" (");
			str.append(type);
			str.append(")");
		}
		return str.toString();
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof EegSystemName) || obj == null) {
			return false;
		}

		EegSystemName otherId = (EegSystemName) obj;

		if (this.symbol.compareTo(otherId.symbol) != 0) {
			return false;
		}
		if (this.type.compareTo(otherId.type) != 0) {
			return false;
		}

		return true;
	}
}

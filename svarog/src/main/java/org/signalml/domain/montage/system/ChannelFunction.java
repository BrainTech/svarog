package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents a function of a channel - that is what kind of signal
 * it 'transfers', what is the unit of measurement of the signal and its
 * minimum and maximum values.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * @author Piotr Szachewicz
 */
@XStreamAlias("eegchannel")
public enum ChannelFunction implements IChannelFunction {

	UNKNOWN("Unknown", false, true, "", 20, 800),
	EEG("EEG", false, true, "uV", 20, 800),
	ECG("ECG", false, true, "uV", 20, 40),
	EMG("EMG", false, true, "e", 20, 800),
	RESP("RESP", false, true, "", 20, 800),
	SAO2("SaO2", false, true, "", 20, 800),
	ZERO("ZERO", true, true, "bit", 20, 800),
	ONE("ONE", true, true, "bit", 20, 800);
	/**
	 * a name of this channel
	 */
	private String name;
	/**
	 * a variable telling if this channel is unique
	 */
	private boolean unique;
	/**
	 * a variable telling if this channel is mutable
	 */
	private boolean mutable;

	/**
	 * The String representing a unit of measurement for the channel
	 * (e.g. 'uV' or 'mV').
	 */
	private String unitOfMeasurementSymbol;
	/**
	 * The minimum value that should be set on the value scale for the signal.
	 */
	private int minValueScale;
	/**
	 * The maximum value that should be set on the value scale for the signal.
	 */
	private int maxValueScale;


	/**
	 * Constructor.
	 * @param name the name of the channel
	 * @param type the type of the channel
	 * @param unique is the channel unique?
	 * @param mutable is the channel mutable?
	 */
	private ChannelFunction(String name, boolean unique, boolean mutable, String unitOfMeasurementSymbol, int minValueScale, int maxValueScale) {
		this.name = name;
		this.unique = unique;
		this.mutable = mutable;
		this.unitOfMeasurementSymbol = unitOfMeasurementSymbol;
		this.minValueScale = minValueScale;
		this.maxValueScale = maxValueScale;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isUnique() {
		return unique;
	}

	@Override
	public Object[] getArguments() {
		return new Object[0];
	}

	@Override
	public String[] getCodes() {
		return new String[]{"eegChannel." + this.toString()};
	}

	@Override
	public String getDefaultMessage() {
		return name;
	}

	@Override
	public boolean isMutable() {
		return this.mutable;
	}

	@Override
	public int getMinValueScale() {
		return minValueScale;
	}

	@Override
	public int getMaxValueScale() {
		return maxValueScale;
	}

	@Override
	public String getUnitOfMeasurementSymbol() {
		return unitOfMeasurementSymbol;
	}

}

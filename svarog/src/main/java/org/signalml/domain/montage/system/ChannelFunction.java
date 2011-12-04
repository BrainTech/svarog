package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;

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

	UNKNOWN("Unknown", false, true, "", -800, 800),
	TRIGGER("Trigger", false, true, "", 0, 10),
	EEG("EEG", false, true, "uV", -100, 100),
	ECG("ECG", false, true, "uV", -5000, 5000),
	EMG("EMG", false, true, "e", -20000, 20000),
	RESP("RESP", false, true, "", -200, 200),
	SAO2("SaO2", false, true, "", -100, 100),
	ZERO("ZERO", true, false, "bit", -100, 100),
	ONE("ONE", true, false, "bit", -100, 100);
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
	 * The minimum expected value of the signal.
	 */
	private int minValue;
	/**
	 * The maximum expected value value of the signal.
	 */
	private int maxValue;


	/**
	 * Constructor.
	 * @param name the name of the channel
	 * @param type the type of the channel
	 * @param unique is the channel unique?
	 * @param mutable is the channel mutable?
	 */
	private ChannelFunction(String name, boolean unique, boolean mutable, String unitOfMeasurementSymbol, int minValue, int maxValue) {
		this.name = name;
		this.unique = unique;
		this.mutable = mutable;
		this.unitOfMeasurementSymbol = unitOfMeasurementSymbol;
		this.minValue = minValue;
		this.maxValue = maxValue;
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
	public int getMinValue() {
		return minValue;
	}

	@Override
	public int getMaxValue() {
		return maxValue;
	}

	@Override
	public String getUnitOfMeasurementSymbol() {
		return unitOfMeasurementSymbol;
	}

	/**
	 * Returns the list of all available mutable channel functions.
	 * @return the list of channel functions that can be changed
	 */
	public static List<IChannelFunction> getMutableChannelFunctions() {
		ChannelFunction[] allFunctions = values();
		List<IChannelFunction> mutableChannelFunctions = new ArrayList<IChannelFunction>();

		for (IChannelFunction function: allFunctions) {
			if (function.isMutable())
				mutableChannelFunctions.add(function);
		}
		return mutableChannelFunctions;
	}

}

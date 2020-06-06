package org.signalml.domain.montage.system;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;
import static org.signalml.app.util.i18n.SvarogI18n._;

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

	UNKNOWN(_("Unknown"), false, true, "uV", 800),
	EOG_LEFT(_("EOG (left)"), false ,true, "uV", 1000), //1-few mV
	EOG_RIGHT(_("EOG (right)"), false ,true, "uV", 1000),
	TRIGGER(_("Trigger"), false, true, "", 1),
	EEG(_("EEG"), false, true, "uV", 20), // 100uV
	ECG(_("ECG"), false, true, "uV", 400), //5mV
	EMG(_("EMG"), false, true, "uV", 1000), //few mV
	RESP(_("RESP"), false, true, "", 20),
	SAO2(_("SaO2"), false, true, "%", 100),
	SC(_("Skin conductance"), false, true, "uS", 20), //20uS
	SP(_("Skin potential"), false, true, "mV", 60000), //0 -60 mV
	ZERO(_("ZERO"), true, false, "bit", 100),
	ONE(_("ONE"), true, false, "bit", 100);

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
	private ChannelFunction(String name, boolean unique, boolean mutable, String unitOfMeasurementSymbol, int maxValue) {
		this.name = name;
		this.unique = unique;
		this.mutable = mutable;
		this.unitOfMeasurementSymbol = unitOfMeasurementSymbol;
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
		return new String[] {"eegChannel." + this.toString()};
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
		List<IChannelFunction> mutableChannelFunctions = new ArrayList<>();

		for (IChannelFunction function: values()) {
			if (function.isMutable())
				mutableChannelFunctions.add(function);
		}
		return mutableChannelFunctions;
	}

}

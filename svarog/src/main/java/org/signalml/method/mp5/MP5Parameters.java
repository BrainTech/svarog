/* MP5Parameters.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;
import org.signalml.domain.signal.space.SignalSpace;
import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MP5Parameters
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mp5parameters")
public class MP5Parameters implements Serializable, Preset {

	private static final long serialVersionUID = 1L;

	public static final float MIN_SCALE_TO_PERIOD_FACTOR = 0F;
	public static final float MAX_SCALE_TO_PERIOD_FACTOR = 20000F;

	public static final float MIN_ENERGY_ERROR = 0F;
	public static final float MAX_ENERGY_ERROR = 1F;

	public static final float MIN_ENERGY_ERROR_PERCENTAGE = 0.0F;
	public static final float MAX_ENERGY_ERROR_PERCENTAGE = 100F;

	public static final int MIN_ITERATION_COUNT = 1;
	public static final int MAX_ITERATION_COUNT = 20000;

	public static final float MIN_ENERGY_PERCENT = 0F;
	public static final float MAX_ENERGY_PERCENT = 100F;

	public static final float MIN_POINTS_PER_MICROVOLT = 0F;
	public static final float MAX_POINTS_PER_MICROVOLT = 2000000;

	public static final float MIN_DOT_EPS = 0;
	public static final float MAX_DOT_EPS = 1;

	private String name;

	private SignalSpace signalSpace = new SignalSpace();

	// dictionary parameters
	private MP5DictionaryType dictionaryType = MP5DictionaryType.OCTAVE_FIXED;
	private MP5DictionaryReinitType dictionaryReinitType = MP5DictionaryReinitType.NO_REINIT_AT_ALL;
	private float scaleToPeriodFactor = 1F;
	private float energyError = 0.1F;
	private float energyErrorPercentage = 90.0F;

	// decomposition parameters
	private MP5Algorithm algorithm = MP5Algorithm.SMP;
	private int maxIterationCount = 50;
	private float energyPercent = 99F;

	private String bookComment;

	private String customConfigText;
	private String rawConfigText;

	private AtomsInDictionary atomsInDictionary = new AtomsInDictionary();

//	private float dotEps = 1E-16F;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public SignalSpace getSignalSpace() {
		return signalSpace;
	}

	public void setSignalSpace(SignalSpace signalSpace) {
		this.signalSpace = signalSpace;
	}

	public void validate(Errors errors) {
		if (scaleToPeriodFactor < MIN_SCALE_TO_PERIOD_FACTOR || scaleToPeriodFactor > MAX_SCALE_TO_PERIOD_FACTOR) {
			errors.rejectValue("scaleToPeriodFactor", "error.mp5.badScaleToPeriodFactor", _("Bad scale to period factor"));
		}
		if (!(energyError > MIN_ENERGY_ERROR && energyError < MAX_ENERGY_ERROR)) {
			errors.rejectValue("energyError", "error.mp5.badEnergyError", _("Bad energy error"));
		}
		if (!(energyErrorPercentage > MIN_ENERGY_ERROR_PERCENTAGE && energyErrorPercentage < MAX_ENERGY_ERROR_PERCENTAGE)) {
			errors.rejectValue("energyErrorPercentage", "error.mp5.badEnergyError", _("Bad energy error percentage"));
		}
		if (maxIterationCount < MIN_ITERATION_COUNT || maxIterationCount > MAX_ITERATION_COUNT) {
			errors.rejectValue("maxIterationCount", "error.mp5.badMaxIterationCount", _("Bad max iteration count"));
		}
		if (energyPercent < MIN_ENERGY_PERCENT || energyPercent > MAX_ENERGY_PERCENT) {
			errors.rejectValue("energyPercent", "error.mp5.badEnergyPercent", _("Bad energy percent"));
		}
	}

	public MP5DictionaryType getDictionaryType() {
		return dictionaryType;
	}

	public void setDictionaryType(MP5DictionaryType dictionaryType) {
		this.dictionaryType = dictionaryType;
	}

	public float getEnergyError() {
		return energyError;
	}

	public void setEnergyError(float energyError) {
		this.energyError = energyError;
	}

	public float getEnergyErrorPercentage() {
		return energyErrorPercentage;
	}

	public void setEnergyErrorPercentage(float energyErrorPercentage) {
		this.energyErrorPercentage = energyErrorPercentage;
	}

	public MP5DictionaryReinitType getDictionaryReinitType() {
		return dictionaryReinitType;
	}

	public void setDictionaryReinitType(MP5DictionaryReinitType dictionaryReinitType) {
		this.dictionaryReinitType = dictionaryReinitType;
	}

	public float getScaleToPeriodFactor() {
		return scaleToPeriodFactor;
	}

	public void setScaleToPeriodFactor(float scaleToPeriodFactor) {
		this.scaleToPeriodFactor = scaleToPeriodFactor;
	}

	public int getMaxIterationCount() {
		return maxIterationCount;
	}

	public void setMaxIterationCount(int maxIterationCount) {
		this.maxIterationCount = maxIterationCount;
	}

	public float getEnergyPercent() {
		return energyPercent;
	}

	public void setEnergyPercent(float energyPercent) {
		this.energyPercent = energyPercent;
	}

	public MP5Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(MP5Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public String getBookComment() {
		return bookComment;
	}

	public void setBookComment(String bookComment) {
		this.bookComment = bookComment;
	}

	public String getCustomConfigText() {
		return customConfigText;
	}

	public void setCustomConfigText(String customConfigText) {
		this.customConfigText = customConfigText;
	}

	public String getRawConfigText() {
		return rawConfigText;
	}

	public void setRawConfigText(String rawConfigText) {
		this.rawConfigText = rawConfigText;
	}

	public AtomsInDictionary getAtomsInDictionary() {
		return atomsInDictionary;
	}

	@Override
	public String toString() {
		return name;
	}

}

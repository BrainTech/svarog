/* MP5Parameters.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import java.io.Serializable;

import static org.signalml.app.SvarogI18n._;
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

	public static final float MIN_DILATION_FACTOR = 1F + Float.MIN_NORMAL;
	public static final float MAX_DILATION_FACTOR = 20000F;

	public static final int MIN_PERIOD_DENSITY = 0;
	public static final int MAX_PERIOD_DENSITY = 20000;

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
	private float scaleToPeriodFactor = 0.1F;
	private float dilationFactor = 2F;
	private int periodDensity = 1;

	// decomposition parameters
	private MP5Algorithm algorithm = MP5Algorithm.SMP;
	private int maxIterationCount = 50;
	private float energyPercent = 99F;

	private boolean analyticalDotProduct;
	private boolean bookWithSignal;

	private String bookComment;

	private String customConfigText;
	private String rawConfigText;

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
		if (dilationFactor < MIN_DILATION_FACTOR || dilationFactor > MAX_DILATION_FACTOR) {
			errors.rejectValue("dilationFactor", "error.mp5.badDilationFactor", _("Bad dilation factor"));
		}
		if (periodDensity < MIN_PERIOD_DENSITY || periodDensity > MAX_PERIOD_DENSITY) {
			errors.rejectValue("periodDensity", "error.mp5.badPerionDensity", _("Bad period density"));
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

	public float getDilationFactor() {
		return dilationFactor;
	}

	public void setDilationFactor(float dilationFactor) {
		this.dilationFactor = dilationFactor;
	}

	public int getPeriodDensity() {
		return periodDensity;
	}

	public void setPeriodDensity(int periodDensity) {
		this.periodDensity = periodDensity;
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

	public boolean isAnalyticalDotProduct() {
		return analyticalDotProduct;
	}

	public void setAnalyticalDotProduct(boolean analyticalDotProduct) {
		this.analyticalDotProduct = analyticalDotProduct;
	}

	public boolean isBookWithSignal() {
		return bookWithSignal;
	}

	public void setBookWithSignal(boolean bookWithSignal) {
		this.bookWithSignal = bookWithSignal;
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

	@Override
	public String toString() {
		return name;
	}

}

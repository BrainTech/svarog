/* StagerParameters.java created 2008-02-08
 *
 */

package org.signalml.method.stager;

import org.signalml.app.config.preset.Preset;
import org.signalml.util.MinMaxRange;
import org.springframework.validation.Errors;

/** StagerParameters
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerParameters implements Preset {

	private static final long serialVersionUID = 1L;

	public static final double MIN_AMPLITUDE = 0;
	public static final double MAX_AMPLITUDE = 1000000;
	public static final double INCR_AMPLITUDE = 1;

	public static final double MIN_FREQUENCY = 0;
	public static final double MAX_FREQUENCY = 4096;
	public static final double INCR_FREQUENCY = 0.01;

	public static final double MIN_SCALE = 0;
	public static final double MAX_SCALE = 1000000;
	public static final double INCR_SCALE = 0.1;

	public static final double MIN_PHASE = -3.14;
	public static final double MAX_PHASE = 3.14;
	public static final double INCR_PHASE = 0.01;

	public static final double MIN_EMG_TONE_THRESHOLD = 5;
	public static final double MAX_EMG_TONE_THRESHOLD = 100;

	public static final double MIN_MT_EEG_THRESHOLD = 10;
	public static final double MAX_MT_EEG_THRESHOLD = 150;

	public static final double MIN_MT_EMG_THRESHOLD = 100;
	public static final double MAX_MT_EMG_THRESHOLD = 1000;

	public static final double MIN_MT_TONE_EMG_THRESHOLD = 10;
	public static final double MAX_MT_TONE_EMG_THRESHOLD = 150;

	public static final double MIN_REM_EOG_DEFLECTION_THRESHOLD = 10;
	public static final double MAX_REM_EOG_DEFLECTION_THRESHOLD = 1000;

	public static final double MIN_SEM_EOG_DEFLECTION_THRESHOLD = 5;
	public static final double MAX_SEM_EOG_DEFLECTION_THRESHOLD = 100;

	private String name;

	private String signalPath;

	private String bookFilePath;
	private boolean primaryHypnogram;

	private boolean advancedConfig = false;

	private SleepStagingRules rules = SleepStagingRules.RK;

	private MinMaxRange deltaAmplitude = new MinMaxRange(MinMaxRange.AUTO);
	private MinMaxRange deltaFrequency = new MinMaxRange(MinMaxRange.UNLIMITED);
	private MinMaxRange deltaScale = new MinMaxRange(MinMaxRange.UNLIMITED);

	private MinMaxRange thetaAmplitude = new MinMaxRange(MinMaxRange.UNLIMITED);
	private MinMaxRange thetaFrequency = new MinMaxRange(MinMaxRange.UNLIMITED);
	private MinMaxRange thetaScale = new MinMaxRange(MinMaxRange.UNLIMITED);

	private MinMaxRange alphaAmplitude = new MinMaxRange(MinMaxRange.AUTO);
	private MinMaxRange alphaFrequency = new MinMaxRange(MinMaxRange.UNLIMITED);
	private MinMaxRange alphaScale = new MinMaxRange(MinMaxRange.UNLIMITED);

	private MinMaxRange spindleAmplitude = new MinMaxRange(MinMaxRange.AUTO);
	private MinMaxRange spindleFrequency = new MinMaxRange(MinMaxRange.UNLIMITED);
	private MinMaxRange spindleScale = new MinMaxRange(MinMaxRange.UNLIMITED);

	private MinMaxRange kComplexAmplitude = new MinMaxRange(MinMaxRange.UNLIMITED);
	private MinMaxRange kComplexFrequency = new MinMaxRange(MinMaxRange.UNLIMITED);
	private MinMaxRange kComplexScale = new MinMaxRange(MinMaxRange.UNLIMITED);
	private MinMaxRange kComplexPhase = new MinMaxRange(MinMaxRange.UNLIMITED);

	private double emgToneThreshold = MinMaxRange.AUTO;
	private double mtEegThreshold;
	private boolean mtEegThresholdEnabled;
	private boolean mtEmgThresholdEnabled;
	private boolean mtArtifactsThresholdEnabled;
	private double mtEmgThreshold;
	private double mtToneEmgThreshold;
	private double remEogDeflectionThreshold;
	private double semEogDeflectionThreshold;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public String getBookFilePath() {
		return bookFilePath;
	}

	public void setBookFilePath(String bookFilePath) {
		this.bookFilePath = bookFilePath;
	}

	public boolean isPrimaryHypnogram() {
		return primaryHypnogram;
	}

	public void setPrimaryHypnogram(boolean primaryHypnogram) {
		this.primaryHypnogram = primaryHypnogram;
	}

	public boolean isAdvancedConfig() {
		return advancedConfig;
	}

	public void setAdvancedConfig(boolean advancedConfig) {
		this.advancedConfig = advancedConfig;
	}

	public SleepStagingRules getRules() {
		return rules;
	}

	public void setRules(SleepStagingRules rules) {
		this.rules = rules;
	}

	public double getEmgToneThreshold() {
		return emgToneThreshold;
	}

	public void setEmgToneThreshold(double emgToneThreshold) {
		this.emgToneThreshold = emgToneThreshold;
	}

	public double getMtEegThreshold() {
		return mtEegThreshold;
	}

	public void setMtEegThreshold(double mtEegThreshold) {
		this.mtEegThreshold = mtEegThreshold;
	}

	public boolean isMtEegThresholdEnabled() {
		return mtEegThresholdEnabled;
	}

	public void setMtEegThresholdEnabled(boolean mtEegThresholdEnabled) {
		this.mtEegThresholdEnabled = mtEegThresholdEnabled;
	}

	public boolean isMtEmgThresholdEnabled() {
		return mtEmgThresholdEnabled;
	}

	public void setMtEmgThresholdEnabled(boolean mtEmgThresholdEnabled) {
		this.mtEmgThresholdEnabled = mtEmgThresholdEnabled;
	}

	public double getMtEmgThreshold() {
		return mtEmgThreshold;
	}

	public void setMtEmgThreshold(double mtEmgThreshold) {
		this.mtEmgThreshold = mtEmgThreshold;
	}

	public double getMtToneEmgThreshold() {
		return mtToneEmgThreshold;
	}

	public void setMtToneEmgThreshold(double mtToneEmgThreshold) {
		this.mtToneEmgThreshold = mtToneEmgThreshold;
	}

	public double getRemEogDeflectionThreshold() {
		return remEogDeflectionThreshold;
	}

	public void setRemEogDeflectionThreshold(double remEogDeflectionThreshold) {
		this.remEogDeflectionThreshold = remEogDeflectionThreshold;
	}

	public double getSemEogDeflectionThreshold() {
		return semEogDeflectionThreshold;
	}

	public void setSemEogDeflectionThreshold(double semEogDeflectionThreshold) {
		this.semEogDeflectionThreshold = semEogDeflectionThreshold;
	}

	public MinMaxRange getDeltaAmplitude() {
		return deltaAmplitude;
	}

	public MinMaxRange getDeltaFrequency() {
		return deltaFrequency;
	}

	public MinMaxRange getDeltaScale() {
		return deltaScale;
	}

	public MinMaxRange getThetaAmplitude() {
		return thetaAmplitude;
	}

	public MinMaxRange getThetaFrequency() {
		return thetaFrequency;
	}

	public MinMaxRange getThetaScale() {
		return thetaScale;
	}

	public MinMaxRange getAlphaAmplitude() {
		return alphaAmplitude;
	}

	public MinMaxRange getAlphaFrequency() {
		return alphaFrequency;
	}

	public MinMaxRange getAlphaScale() {
		return alphaScale;
	}

	public MinMaxRange getSpindleAmplitude() {
		return spindleAmplitude;
	}

	public MinMaxRange getSpindleFrequency() {
		return spindleFrequency;
	}

	public MinMaxRange getSpindleScale() {
		return spindleScale;
	}

	public MinMaxRange getKComplexAmplitude() {
		return kComplexAmplitude;
	}

	public MinMaxRange getKComplexFrequency() {
		return kComplexFrequency;
	}

	public MinMaxRange getKComplexScale() {
		return kComplexScale;
	}

	public MinMaxRange getKComplexPhase() {
		return kComplexPhase;
	}

	public double[] getDeltaParameterArray() {
		return new double[] {
		               deltaFrequency.getMinWithUnlimited(),
		               deltaFrequency.getMaxWithUnlimited(),
		               deltaAmplitude.getMinWithUnlimited(),
		               deltaAmplitude.getMaxWithUnlimited(),
		               deltaScale.getMinWithUnlimited(),
		               deltaScale.getMaxWithUnlimited()
		       };
	}

	public double[] getThetaParameterArray() {
		return new double[] {
		               thetaFrequency.getMinWithUnlimited(),
		               thetaFrequency.getMaxWithUnlimited(),
		               thetaAmplitude.getMinWithUnlimited(),
		               thetaAmplitude.getMaxWithUnlimited(),
		               thetaScale.getMinWithUnlimited(),
		               thetaScale.getMaxWithUnlimited()
		       };
	}

	public double[] getAlphaParameterArray() {
		return new double[] {
		               alphaFrequency.getMinWithUnlimited(),
		               alphaFrequency.getMaxWithUnlimited(),
		               alphaAmplitude.getMinWithUnlimited(),
		               alphaAmplitude.getMaxWithUnlimited(),
		               alphaScale.getMinWithUnlimited(),
		               alphaScale.getMaxWithUnlimited()
		       };
	}

	public double[] getSpindleParameterArray() {
		return new double[] {
		               spindleFrequency.getMinWithUnlimited(),
		               spindleFrequency.getMaxWithUnlimited(),
		               spindleAmplitude.getMinWithUnlimited(),
		               spindleAmplitude.getMaxWithUnlimited(),
		               spindleScale.getMinWithUnlimited(),
		               spindleScale.getMaxWithUnlimited()
		       };
	}

	public double[] getKComplexParameterArray() {
		return new double[] {
		               kComplexFrequency.getMinWithUnlimited(),
		               kComplexFrequency.getMaxWithUnlimited(),
		               kComplexAmplitude.getMinWithUnlimited(),
		               kComplexAmplitude.getMaxWithUnlimited(),
		               kComplexScale.getMinWithUnlimited(),
		               kComplexScale.getMaxWithUnlimited(),
		               kComplexPhase.getMinWithUnlimited(),
		               kComplexPhase.getMaxWithUnlimited()
		       };
	}

	public void validate(Errors errors) {

		// TODO

	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * @return the mtArtifactsThresholdEnabled
	 */
	public boolean isMtArtifactsThresholdEnabled() {
		return mtArtifactsThresholdEnabled;
	}

	/**
	 * @param mtArtifactsThresholdEnabled the mtArtifactsThresholdEnabled to set
	 */
	public void setMtArtifactsThresholdEnabled(boolean mtArtifactsThresholdEnabled) {
		this.mtArtifactsThresholdEnabled = mtArtifactsThresholdEnabled;
	}

	public String getSignalPath() {
		return signalPath;
	}

	public void setSignalPath(String signalPath) {
		this.signalPath = signalPath;
	}

}

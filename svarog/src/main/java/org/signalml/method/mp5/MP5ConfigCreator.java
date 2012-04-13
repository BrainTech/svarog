/* MP5ConfigWriter.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.signalml.util.SvarogConstants;
import org.signalml.util.Util;

/** MP5ConfigWriter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ConfigCreator {

	public static final String NONE = "NONE";

	public static final String NAME_OF_DATA_FILE = "nameOfDataFile";

	public static final String NAME_OF_OUTPUT_DIRECTORY = "nameOfOutputDirectory";

	public static final String WRITING_MODE = "writingMode";

	public static final String SAMPLING_FREQUENCY = "samplingFrequency";

	public static final String NUMBER_OF_CHANNELS = "numberOfChannels";
	public static final String SELECTED_CHANNELS = "selectedChannels";
	public static final String NUMBER_OF_SAMPLES_IN_EPOCH = "numberOfSamplesInEpoch";
	public static final String SELECTED_EPOCHS = "selectedEpochs";

	public static final String TYPE_OF_DICTIONARY = "typeOfDictionary";

	public static final String DILATION_FACTOR = "dilationFactor";

	public static final String RANDOM_SEED = "randomSeed";

	public static final String REINIT_DICTIONARY = "reinitDictionary";

	public static final String SCALE_TO_PERIOD_FACTOR = "scaleToPeriodFactor";
	public static final String MAX_NUMBER_OF_ITERATIONS = "maximalNumberOfIterations";
	public static final String ENERGY_PERCENT = "energyPercent";

	public static final String MP = "MP";
	public static final String MP_SMP = "SMP";
	public static final String MP_MMP1 = "MMP1";
	public static final String MP_MMP2 = "MMP2";
	public static final String MP_MMP3 = "MMP3";

	public static final String POINTS_PER_MICROVOLT = "pointsPerMicrovolt";

	public static final String ON = "ON";
	public static final String OFF = "OFF";

	public static final String YES = "YES";
	public static final String NO = "NO";

	private SecureRandom random;

	private HashSet<String> runtimeParameters;

	public MP5ConfigCreator() {
		random = new SecureRandom();
	}

	public Formatter createConfigFormatter() {
		return createConfigFormatter(true);
	}

	public Formatter createConfigFormatter(boolean addHeader) {

		Formatter formatter = new Formatter(new Locale("en_EN"));

		if (addHeader) {
			formatter.format("# signalml mp5 configuration%n");
			formatter.format("# auto created by SignalML version [%s] on [%s]%n", SvarogConstants.VERSION, (new Date()).toString());
			formatter.format("%n");
		}

		return formatter;

	}

	public void writeRuntimeInvariantConfig(MP5Parameters parameters, Formatter formatter) {

		String bookComment = parameters.getBookComment();
		if (bookComment != null && !bookComment.isEmpty()) {

			String[] lines = Util.splitTextIntoLines(bookComment, 70);
			for (int i=0; i<lines.length; i++) {
				formatter.format("##%s%n", lines[i]);
			}

			formatter.format("%n");

		}

		formatter.format("%s %s%n", TYPE_OF_DICTIONARY, parameters.getDictionaryType().toString());
		formatter.format("%s %f %f%n", DILATION_FACTOR, parameters.getDilationFactor(), parameters.getDilationFactorPercentage());
		formatter.format("%s %s%n", REINIT_DICTIONARY, parameters.getDictionaryReinitType().toString());
		formatter.format("%s %f%n", SCALE_TO_PERIOD_FACTOR, parameters.getScaleToPeriodFactor());

		formatter.format("%n");

		formatter.format("%s %d%n", MAX_NUMBER_OF_ITERATIONS, parameters.getMaxIterationCount());
		formatter.format("%s %.6f%n", ENERGY_PERCENT, parameters.getEnergyPercent());
		formatter.format("%n");

		formatter.format("%s %s%n", MP, parameters.getAlgorithm().toString());
		formatter.format("%n");

		for (MP5AtomType atomType: MP5AtomType.values()) {
			boolean isAtomIncluded = parameters.getAtomsInDictionary().isAtomIncluded(atomType);
			String parameterName = atomType.getConfigName();
			String yesNoString =  isAtomIncluded ? "YES" : "NO";
			formatter.format("%s %s%n", parameterName, yesNoString);
		}

		formatter.format("%n");

		String customConfigText = parameters.getCustomConfigText();
		if (customConfigText != null && !customConfigText.isEmpty()) {
			formatter.format("%s%n", customConfigText);
			formatter.format("%n");
		}

	}

	public void writeRawConfig(String rawConfig, Formatter formatter) {

		// config is processed line by line and all runtime parameters are stripped from it
		if (runtimeParameters == null) {
			runtimeParameters = createRuntimeParameters();
		}

		Pattern cfgPattern = Pattern.compile("^\\s*([a-zA-Z0-9_]+)\\s.*$");

		String line;
		Matcher matcher;

		StringTokenizer st = new StringTokenizer(rawConfig, "\n", false);

		while (st.hasMoreTokens()) {

			line = st.nextToken();

			matcher = cfgPattern.matcher(line);
			if (matcher.matches()) {
				if (runtimeParameters.contains(matcher.group(1))) {
					// strip this line
					continue;
				}
			}

			line = line.trim();

			formatter.format("%s%n", line);

		}

	}

	private HashSet<String> createRuntimeParameters() {

		HashSet<String> set = new HashSet<String>();

		set.add(NAME_OF_DATA_FILE);
		set.add(POINTS_PER_MICROVOLT);
		set.add(NAME_OF_OUTPUT_DIRECTORY);
		set.add(WRITING_MODE);
		set.add(SAMPLING_FREQUENCY);
		set.add(NUMBER_OF_CHANNELS);
		set.add(SELECTED_CHANNELS);
		set.add(NUMBER_OF_SAMPLES_IN_EPOCH);
		set.add(SELECTED_EPOCHS);
		set.add(RANDOM_SEED);

		return set;

	}

	public void writeRuntimeConfig(MP5RuntimeParameters parameters, Formatter formatter) {

		formatter.format("%s %f%n", SAMPLING_FREQUENCY, parameters.getSamplingFrequency());
		formatter.format("%n");

		formatter.format("%s %s%n", NAME_OF_DATA_FILE, parameters.getSignalFile().getName());

		formatter.format("%s %.6f%n", POINTS_PER_MICROVOLT, parameters.getPointsPerMicrovolt());
		formatter.format("%n");

		File outputDirectory = parameters.getOutputDirectory();
		if (outputDirectory == null) {
			formatter.format("%s %s%n", NAME_OF_OUTPUT_DIRECTORY, "./");
		} else {
			formatter.format("%s %s%n", NAME_OF_OUTPUT_DIRECTORY, outputDirectory.getAbsolutePath());
		}
		formatter.format("%s %s%n", WRITING_MODE, parameters.getWritingMode().toString());
		formatter.format("%n");

		formatter.format("%s %f%n", SAMPLING_FREQUENCY, parameters.getSamplingFrequency());
		formatter.format("%n");

		formatter.format("%s %d%n", NUMBER_OF_SAMPLES_IN_EPOCH, parameters.getSegementSize());

		int minOffset = parameters.getMinOffset();
		int maxOffset = parameters.getMaxOffset();

		if (minOffset < 0 || maxOffset < 0) {
			formatter.format("%s %d%n", SELECTED_EPOCHS, 1);
		} else {
			if (minOffset == maxOffset) {
				formatter.format("%s %d%n", SELECTED_EPOCHS, minOffset);
			} else {
				formatter.format("%s %d-%d%n", SELECTED_EPOCHS, minOffset, maxOffset);
			}
		}
		formatter.format("%n");

		formatter.format("%s %d%n", NUMBER_OF_CHANNELS, parameters.getChannelCount());

		int[] chosenChannels = parameters.getChosenChannels();
		if (chosenChannels == null) {

			formatter.format("%s %d-%d%n", SELECTED_CHANNELS, 1, parameters.getChannelCount());

		} else {

			int i;
			StringBuilder sb = new StringBuilder();
			boolean inRange = false;
			for (i=0; i<chosenChannels.length; i++) {
				if (i>0 && i<(chosenChannels.length-1) && chosenChannels[i] == (chosenChannels[i-1]+1)) {
					if (!inRange) {
						sb.append('-');
						inRange = true;
					}
				} else {
					if (inRange) {
						sb.append(chosenChannels[i]+1);
						inRange = false;
					} else {
						if (i != 0) {
							sb.append(' ');
						}
						sb.append(chosenChannels[i]+1);
					}
				}
			}
			formatter.format("%s %s%n", SELECTED_CHANNELS, sb.toString());

		}
		formatter.format("%n");

		formatter.format("%s %d%n", RANDOM_SEED, random.nextInt(Integer.MAX_VALUE));
		formatter.format("%n");

	}

	public void writeMp5Config(Formatter config, File configFile) throws IOException {

		Writer writer = null;
		try {

			writer = new BufferedWriter(new FileWriter(configFile));

			writer.append(config.toString());

		} finally {
			if (writer != null) {
				writer.close();
			}
		}

	}

}

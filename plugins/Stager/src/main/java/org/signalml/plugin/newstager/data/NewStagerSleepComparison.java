/* SleepComparison.java created 2008-02-27
 * 
 */

package org.signalml.plugin.newstager.data;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import org.signalml.domain.tag.SleepTagName;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * SleepComparison
 * 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerSleepComparison implements PropertyProvider {

	protected static final Logger logger = Logger
			.getLogger(NewStagerSleepComparison.class);

	private NewStagerSleepStatistic statistic;
	private ExportedTagDocument tag;
	private TagDocument expertTag;
	private TagDocument artifactTag;

	// styleOverlay[i][j] = stager said i, expert said j
	// i goes from 0 to count-1
	// j goes from 0 to count and j == count => unrecognized expert tag
	private int[][] styleOverlay;
	private int[] rowSums; // T(i,:)
	private int[] columnSums; // T(:,i)
	private ArrayList<ExportedTagStyle> styles;
	private HashMap<String, Integer> styleNameMap;

	private int styleCount;

	private double segmentLength;

	private int segmentCount;

	private int total;

	public NewStagerSleepComparison(NewStagerSleepStatistic statistic,
			ExportedTagDocument tagDocument, TagDocument expertTag,
			TagDocument artifactTag) {
		this.statistic = statistic;
		this.tag = tagDocument;
		this.expertTag = expertTag;
		this.artifactTag = artifactTag;

		Set<ExportedTagStyle> tagStylesSet = tagDocument.getTagStyles();
		StyledTagSet expertTagSet = expertTag.getTagSet();
		StyledTagSet artifactTagSet = null;
		if (artifactTag != null) {
			artifactTagSet = artifactTag.getTagSet();
		}

		styles = new ArrayList<ExportedTagStyle>();

		if (SleepTagName.isValidRKSleepTag(tagDocument)) {
			styles.addAll(this.getSleepStyles(tagStylesSet,
					SleepTagName.RK_WAKE, SleepTagName.RK_1, SleepTagName.RK_2,
					SleepTagName.RK_3, SleepTagName.RK_4, SleepTagName.RK_REM,
					SleepTagName.RK_MT));

		} else if (SleepTagName.isValidAASMSleepTag(tagDocument)) {
			styles.addAll(this.getSleepStyles(tagStylesSet,
					SleepTagName.AASM_WAKE, SleepTagName.AASM_N1,
					SleepTagName.AASM_N2, SleepTagName.AASM_N3,
					SleepTagName.AASM_REM));

		} else {
			throw new SanityCheckException("Unsupported stage tag type");
		}

		ExportedTag tag = null;
		int index = 0;
		ExportedTagStyle style = null;
		double position = 0;
		int i;

		SortedSet<ExportedTag> tags = tagDocument.getSetOfTags();
		Iterator<ExportedTag> it = tags.iterator();
		SortedSet<Tag> expertTags;
		Iterator<Tag> expertIt;
		Tag expert = null;
		TagStyle expertStyle = null;
		SortedSet<Tag> artifactTags;
		Iterator<Tag> artifactIt;
		Tag artifact = null;

		styleCount = styles.size();
		styleNameMap = new HashMap<String, Integer>(styleCount);
		for (i = 0; i < styleCount; i++) {
			style = styles.get(i);
			styleNameMap.put(style.getName(), i);
		}

		styleOverlay = new int[styleCount][styleCount + 1];

		double expectedTime;
		Integer idx;

		segmentLength = statistic.getSegmentLength();
		segmentCount = statistic.getSegmentCount();

		for (i = 0; i < segmentCount; i++) {

			expectedTime = i * segmentLength;

			while (tag == null) {

				// acquire next valid page tag
				if (!it.hasNext()) {
					logger.warn("No more tags at [" + expectedTime + "]");
					break;
				}
				tag = it.next();
				style = tag.getStyle();
				position = tag.getPosition();
				if (!style.getType().isPage()) {
					tag = null;
				} else if (position < expectedTime) {
					logger.warn("Extra tag [" + style.getName()
							+ "] found at [" + position + "]");
					tag = null;
				} else if (tag.getLength() != segmentLength) {
					logger.warn("Bad tag length [" + tag.getLength() + "]");
					tag = null;
				} else {
					index = styles.indexOf(style);
					if (index < 0) {
						logger.warn("Unknown page style [" + style.getName()
								+ "]");
						tag = null;
					}
				}

			}

			if (tag != null) {

				if (position > expectedTime) {
					logger.warn("Missing tag at [" + expectedTime + "]");
					continue;
				}

				// so at this time we know that we have a tag, starting as
				// expected
				// at expectedTime, lasting segmentTime, with style @ index in
				// styles

				// check artifacts if applicable
				if (artifactTagSet != null) {

					artifactTags = artifactTagSet.getTagsBetween(expectedTime,
							expectedTime + segmentLength);
					artifactIt = artifactTags.iterator();
					while (artifactIt.hasNext()) {

						artifact = artifactIt.next();

						if (artifact.overlaps(tag)) {
							logger.info("Artifacts at position ["
									+ expectedTime + "]");
							tag = null;
							break;
						}

					}

					if (tag == null) {
						continue;
					}

				}

				expertTags = expertTagSet.getTagsBetween(expectedTime,
						expectedTime + segmentLength);
				expert = null;
				expertIt = expertTags.iterator();
				while (expert == null && expertIt.hasNext()) {
					expert = expertIt.next();
					expertStyle = expert.getStyle();
					if (!expertStyle.getType().isPage()) {
						expert = null;
					} else if (expert.getPosition() != expectedTime) {
						expert = null;
					} else if (expert.getLength() != segmentLength) {
						logger.warn("Bad expert tag length ["
								+ expert.getLength() + "]");
						expert = null;
					}
				}

				if (expert == null) {
					logger.warn("No corresponding expert tag found for location ["
							+ expectedTime + "]");
					styleOverlay[index][styleCount]++;
					tag = null;
					continue;
				}

				idx = styleNameMap.get(expertStyle.getName());
				int expertIndex = (idx != null ? idx.intValue() : -1);
				if (expertIndex < 0) {
					logger.warn("Unrecognized expert style ["
							+ expertStyle.getName() + "] at position ["
							+ expectedTime + "]");
					styleOverlay[index][styleCount]++;
					tag = null;
					continue;
				}

				styleOverlay[index][expertIndex]++;

				tag = null;

			}

		}

		// overlay matrix has been computed

		int e;
		total = 0;
		rowSums = new int[styleCount];
		columnSums = new int[styleCount + 1];
		for (i = 0; i < styleCount; i++) {
			for (e = 0; e <= styleCount; e++) {
				rowSums[i] += styleOverlay[i][e];
				columnSums[e] += styleOverlay[i][e];
			}
			total += rowSums[i];
		}

	}

	public NewStagerSleepStatistic getStatistic() {
		return statistic;
	}

	/*
	 * public ExportedTagDocument getTag() { return tag; }
	 * 
	 * public TagDocument getExpertTag() { return expertTag; }
	 * 
	 * public TagDocument getArtifactTag() { return artifactTag; }
	 */

	public int getStyleCount() {
		return styleCount;
	}

	public ExportedTagStyle getStyleAt(int index) {
		return styles.get(index);
	}

	public int getSegmentCount() {
		return segmentCount;
	}

	public float getSegmentLength() {
		return (float) segmentLength;
	}

	public int getStyleOverlay(int stager, int expert) {
		return styleOverlay[stager][expert];
	}

	public int getUncategorizeStyleOverlay(int stager) {
		return styleOverlay[stager][styleCount];
	}

	public int getStagerTotal(int stager) {
		return rowSums[stager];
	}

	public int getExpertTotal(int expert) {
		return columnSums[expert];
	}

	public int getUncategorizedTotal() {
		return columnSums[styleCount];
	}

	public int getTotal() {
		return total;
	}

	public double getConcordance(int index) {
		int low = (rowSums[index] + columnSums[index] - styleOverlay[index][index]);
		if (low == 0) {
			return 0;
		} else {
			return (((double) styleOverlay[index][index]) / ((double) low));
		}
	}

	public double getSensitivity(int index) {
		if (rowSums[index] == 0) {
			return 0;
		} else {
			return (((double) styleOverlay[index][index]) / ((double) rowSums[index]));
		}
	}

	public double getSelectivity(int index) {
		if (columnSums[index] == 0) {
			return 0;
		} else {
			return (((double) styleOverlay[index][index]) / ((double) columnSums[index]));
		}
	}

	public double getCohensKappa() {

		if (total == 0) {
			return -1;
		}

		long diagonal = 0;
		long marginal = 0;
		for (int i = 0; i < styleCount; i++) {
			diagonal += styleOverlay[i][i];
			marginal += rowSums[i] * columnSums[i];
		}

		double q = ((double) marginal) / ((double) total);

		double low = (((double) total) - q);

		if (low == 0) {
			return -1;
		} else {
			return ((((double) diagonal) - q) / low);
		}

	}

	public double getTotalConcordance() {

		if (total == 0) {
			return -1;
		}

		long diagonal = 0;

		for (int i = 0; i < styleCount; i++) {
			diagonal += styleOverlay[i][i];
		}

		return (((double) diagonal) / ((double) total));

	}

	public String getTotalConcordancePretty() {
		double totalConcordance = getTotalConcordance();
		if (totalConcordance < 0) {
			return "-";
		} else {
			return (new Double(
					((double) Math.round(totalConcordance * 100)) / 100))
					.toString();
		}
	}

	public String getCohensKappaPretty() {
		double cohensKappa = getCohensKappa();
		if (cohensKappa < 0) {
			return "-";
		} else {
			return (new Double(((double) Math.round(cohensKappa * 100)) / 100))
					.toString();
		}
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList()
			throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor(
				"property.sleepComparison.totalConcordance",
				"totalConcordancePretty", NewStagerSleepComparison.class,
				"getTotalConcordancePretty", null));
		list.add(new LabelledPropertyDescriptor(
				"property.sleepComparison.cohensKappa", "cohensKappaPretty",
				NewStagerSleepComparison.class, "getCohensKappaPretty", null));

		return list;

	}

	private Collection<ExportedTagStyle> getSleepStyles(
			Set<ExportedTagStyle> tagStylesSet, String... styleNames) {
		Set<ExportedTagStyle> result = new HashSet<ExportedTagStyle>();

		Set<String> styleNamesSet = new HashSet<String>(
				Arrays.asList(styleNames));
		for (ExportedTagStyle style : tagStylesSet) {
			String styleName = style.getName();
			if (styleNamesSet.contains(styleName)) {
				result.add(style);
			}
		}

		return result;
	}

}

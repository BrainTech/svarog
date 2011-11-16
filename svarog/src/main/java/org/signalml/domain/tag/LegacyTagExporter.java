/* LegacyTagExporter.java created 2007-11-18
 *
 */

package org.signalml.domain.tag;

import java.awt.Color;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.signalml.exception.ResolvableException;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

import eega.util.tag.TTagHDRRec;
import eega.util.tag.TagDataSet;
import eega.util.tag.TagException;

/**
 * This class allows to convert a {@link StyledTagSet StyledTagSet} to a
 * {@link TagDataSet TagDataSet} (which is a form that can be written to file)
 * and write it to a file.
 * To write to file uses {@link TagDataSet#write(String)}.
 *
 * @see LegacyTagConstants
 * @see LegacyTagImporter
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LegacyTagExporter {

	protected static final Logger logger = Logger.getLogger(LegacyTagExporter.class);

        /**
         * Writes a {@link StyledTagSet StyledTagSet} to file.
         * It is done by creating a {@link TagDataSet TagDataSet} form the given
         * StyledTagSet and writing it to file.
         * @param tagSet the StyledTagSet to be written to file
         * @param f the file to which set will be written
         * @param channelCount the number of channels of signal
         * @param samplingFrequency the sampling frequency of a signal
         * @throws SignalMLException if the page size is not an integer
         * @throws SanityCheckException if the type of a selection improper for
         * any tag/tag style in the set
         */
	public void exportLegacyTags(StyledTagSet tagSet, File f, int channelCount, float samplingFrequency) throws SignalMLException {

		if (Math.floor(tagSet.getPageSize()) != tagSet.getPageSize()) {
			throw new SignalMLException("error.tagExport.secPPNotInteger");
		}

		TagDataSet tds = exportLegacyTags(tagSet, channelCount, samplingFrequency);;
		try {
			tds.write(f.getAbsolutePath());
		} catch (TagException ex) {
			logger.error("Exception when writing legacy tags", ex);
			throw new ResolvableException(ex);
		}

	}

        /**
         * Creates a {@link TagDataSet TagDataSet} form the given
         * {@link StyledTagSet StyledTagSet} assuming there are a given number
         * of channels in the signal and given sampling frequency of a signal.
         * @param tagSet the StyledTagSet to be converted
         * @param channelCount the number of channels of signal
         * @param samplingFrequency the sampling frequency of a signal
         * @return the created TagDataSet object
         * @throws SanityCheckException if the type of a selection improper for
         * any tag/tag style in the set
         */
	public TagDataSet exportLegacyTags(StyledTagSet tagSet, int channelCount, float samplingFrequency) {

		TagStyle style;
		Tag tag;

		TTagHDRRec hdrRec;
		SignalSelectionType type;

		int secPP = (int) tagSet.getPageSize();
		int blkPP = tagSet.getBlocksPerPage();
		float blockSize = tagSet.getBlockSize();

		TagDataSet tds = new TagDataSet(secPP, blkPP);
		tds.setNoOfChannels(channelCount);

		Collection<TagStyle> styles = tagSet.getListOfStyles();
		Iterator<TagStyle> it = styles.iterator();
		while (it.hasNext()) {
			style = it.next();
			hdrRec = exportStyle(style);
			type = style.getType();
			if (type.isPage()) {
				tds.addPageRec(hdrRec);
			}
			else if (type.isBlock()) {
				tds.addBlockRec(hdrRec);
			}
			else if (type.isChannel()) {
				tds.addChannelRec(hdrRec);
			} else {
				throw new SanityCheckException("Bad selection type [" + type + "]");
			}
		}

		SortedSet<Tag> tags = tagSet.getTags();
		Iterator<Tag> tIt = tags.iterator();
		while (tIt.hasNext()) {
			tag = tIt.next();
			type = tag.getType();
			if (type.isPage()) {

				tds.addPageTag(
				        tag.getStyle().getName().getBytes()[0],
				        tag.getStartSegment(secPP) + 1
				);

			}
			else if (type.isBlock()) {

				tds.addBlockTag(
				        tag.getStyle().getName().getBytes()[0],
				        tag.getStartSegment(secPP) + 1,
				        (byte)((tag.getStartSegment(blockSize) % blkPP) + 1)
				);

			}
			else if (type.isChannel()) {

				tds.addChannelTag(
				        tag.getChannel(),
				        tag.getStyle().getName().getBytes()[0],
				        (long)(tag.getPosition() * samplingFrequency),
				        (int)(tag.getLength() * samplingFrequency)
				);

			} else {
				throw new SanityCheckException("Bad selection type [" + type + "]");
			}
		}

		return tds;

	}

        /**
         * Converts the {@link TagStyle style of a tag} to {@link TTagHDRRec
         * object} that can be written to file.
         * @param style the style of a tag
         * @return the created object
         */
	private TTagHDRRec exportStyle(TagStyle style) {

		// note - marker attribute cannot be exported and is not exported

		return new TTagHDRRec(
		               style.getName().getBytes()[0],
		               style.getDescription(),
		               exportColor(style.getFillColor()),
		               LegacyTagConstants.FILL_SOLID,
		               exportColor(style.getOutlineColor()),
		               LegacyTagConstants.DRAW_MODE_COPY,
		               exportDash(style.getOutlineDash()),
		               (short) style.getOutlineWidth()
		       );
	}

        /**
         * Creates an integer representation of RGB colour.
         * @param color the colour to be converted
         * @return an integer representation of RGB colour in the form of
         * Blue|Green|Red
         */
	private int exportColor(Color color) {
		return (color.getRed() + (color.getGreen() << 8) + (color.getBlue() << 16));
	}

        /**
         * Converts the array representing the dashing pattern for the outline
         * to a constant byte representation of the outline.
         * @param dash the array representing the dashing pattern for the
         * outline
         * @return the constant byte representation of the outline
         */
	private byte exportDash(float[] dash) {
		if (dash == null || dash.length < 2) {
			return LegacyTagConstants.OUTLINE_MODE_SOLID;
		} else {
			if (dash.length == 2 || dash.length == 3) {
				if (dash[0] > 4) {
					return LegacyTagConstants.OUTLINE_MODE_DASH;
				} else {
					return LegacyTagConstants.OUTLINE_MODE_DOT;
				}
			}
			else if (dash.length == 4 || dash.length == 5) {
				return LegacyTagConstants.OUTLINE_MODE_DASHDOT;
			} else {
				return LegacyTagConstants.OUTLINE_MODE_DASHDOTDOT;
			}
		}
	}

}

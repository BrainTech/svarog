/* LegacyTagImporter.java created 2007-10-03
 *
 */

package org.signalml.domain.tag;

import java.awt.Color;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.TreeSet;

import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.exception.ResolvableException;
import org.signalml.exception.SignalMLException;

import eega.util.tag.BlockTag;
import eega.util.tag.ChannelTag;
import eega.util.tag.PageTag;
import eega.util.tag.TTagHDRRec;
import eega.util.tag.TagDataSet;
import eega.util.tag.TagException;

/**
 * This class allows to create a {@link StyledTagSet StyledTagSet} from file
 * and to convert a {@link TagDataSet TagDataSet} to a StyledTagSet.
 * Reading from file is done by reading TagDataSet (using method
 * {@link TagDataSet#loadTagDataSet(String)}) from file and converting it
 * to StyledTagSet. 
 *
 * @see LegacyTagConstants
 * @see LegacyTagExporter
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LegacyTagImporter {

	protected static final Logger logger = Logger.getLogger(LegacyTagImporter.class);

        /**
         * Reads a {@link StyledTagSet StyledTagSet} from file.
         * It is done by reading a {@link TagDataSet TagDataSet} form the file
         * and converting it to StyledTagSet.
         * @param f the file from which set will be read
         * @param samplingFrequency the sampling frequency of a signal
         * @return the created StyledTagSet
         * @throws SignalMLException if file contains no legacy tag or
         * some other error while reading legacy tags
         */
	public StyledTagSet importLegacyTags(File f, float samplingFrequency) throws SignalMLException {

		TagDataSet tds = null;
		try {
			tds = TagDataSet.loadTagDataSet(f.getAbsolutePath());
		} catch (TagException ex) {
			logger.info("Exception when reading legacy tags, or not legacy tag");
			throw new ResolvableException(ex);
		}

		return importLegacyTags(tds, samplingFrequency);

	}

        /**
         * Converts a {@link TagDataSet TagDataSet} to the given
         * {@link StyledTagSet StyledTagSet} assuming given sampling frequency
         * of a signal.
         * @param tds the TagDataSet to be converted
         * @param samplingFrequency the sampling frequency of a signal
         * @return the created StyledTagSet object
         */
	public StyledTagSet importLegacyTags(TagDataSet tds, float samplingFrequency) {

		TagStyle style;
		Tag tag;

		LinkedHashMap<String,TagStyle> styles = new LinkedHashMap<String, TagStyle>();
		TreeSet<Tag> tags = new TreeSet<Tag>();

		int i;
		int cnt;

		cnt = tds.getPageRecCount();
		for (i=0; i<cnt; i++) {
			style = importStyle(tds.getPageRec(i), SignalSelectionType.PAGE);
			styles.put(style.getName(), style);
		}

		cnt = tds.getBlockRecCount();
		for (i=0; i<cnt; i++) {
			style = importStyle(tds.getBlockRec(i), SignalSelectionType.BLOCK);
			styles.put(style.getName(), style);
		}

		cnt = tds.getChannelRecCount();
		for (i=0; i<cnt; i++) {
			style = importStyle(tds.getChannelRec(i), SignalSelectionType.CHANNEL);
			styles.put(style.getName(), style);
		}

		float pageSize = ((float) tds.getSecPP());
		int blocksPerPage = tds.getBlkPP();
		float blockSize = pageSize / blocksPerPage;

		cnt = tds.getPageTagCount();
		PageTag pTag;
		for (i=0; i<cnt; i++) {
			pTag = tds.getPageTag(i);
			style = styles.get(new String(new byte[] {pTag.getTag()}));
			if (style == null) {
				style = TagStyle.getDefaultPage();
			}
			tag = new Tag(
			        style,
			        (pTag.getPage()-1)*tds.getSecPP(),
			        tds.getSecPP(),
			        Tag.CHANNEL_NULL,
			        null
			);
			tags.add(tag);
		}

		cnt = tds.getBlockTagCount();
		BlockTag bTag;
		for (i=0; i<cnt; i++) {
			bTag = tds.getBlockTag(i);
			style = styles.get(new String(new byte[] {bTag.getTag()}));
			if (style == null) {
				style = TagStyle.getDefaultBlock();
			}
			tag = new Tag(
			        style,
			        (bTag.getPage()-1)*tds.getSecPP() + (bTag.getBlock()-1)*blockSize,
			        blockSize,
			        Tag.CHANNEL_NULL,
			        null
			);
			tags.add(tag);
		}

		int channelCnt = tds.getNoOfChannels();
		ChannelTag cTag;

		for (int channel = 0; channel<channelCnt; channel++) {
			cnt = tds.getChannelTagCount(channel);
			for (i=0; i<cnt; i++) {
				cTag = tds.getChannelTag(channel,i);
				style = styles.get(new String(new byte[] {cTag.getTag()}));
				if (style == null) {
					style = TagStyle.getDefaultChannel();
				}
				tag = new Tag(
				        style,
				        ((float) cTag.getOffset())/samplingFrequency,
				        ((float) cTag.getLength())/samplingFrequency,
				        channel,
				        null
				);
				tags.add(tag);
			}
		}

		return new StyledTagSet(styles,tags,pageSize,blocksPerPage);

	}

        /**
         * Converts the {@link TTagHDRRec TTagHDRRec object} to a
         * {@link TagStyle TagStyle}.
         * @param rec the object to be converted
         * @param type the type of signal selection associated with the style
         * @return the created object
         */
	private TagStyle importStyle(TTagHDRRec rec, SignalSelectionType type) {

		// note - fill style is not supported in new tags and ignored

		String name = new String(new byte[] {rec.getTag()});
		return new TagStyle(
		               type,
		               name,
		               new String(rec.getHint()),
		               importColor(rec.getBackColor()),
		               rec.getPenStyle() == LegacyTagConstants.OUTLINE_MODE_CLEAR ? importColor(rec.getBackColor()) : importColor(rec.getPenColor()),
		               rec.getPenWidth(),
		               importDash(rec.getPenStyle()),
		               KeyStroke.getKeyStroke("typed " + name),
		               false // always false
		       );

	}

        /**
         * Converts an integer representation of a RGB colour to
         * {@link Color Color} object.
         * @param rgb an integer with colour in the form of
         * Blue|Green|Red (8 bits every base colour)
         * @return the created Colour object
         */
	private Color importColor(int rgb) {
		return new Color((rgb & 0x0000FF), (rgb & 0x00FF00) >> 8, (rgb & 0xFF0000) >> 16);
	}

        /**
         * Converts the constant byte representation of the outline to the array
         * representing the dashing pattern for the outline.
         * @param id the constant byte representation of the outline
         * @return the array representing the dashing pattern for the outline
         */
	private float[] importDash(byte id) {
		switch (id) {

		case LegacyTagConstants.OUTLINE_MODE_DASH :
			return new float[] { 8F, 8F };

		case LegacyTagConstants.OUTLINE_MODE_DOT :
			return new float[] { 2F, 2F };

		case LegacyTagConstants.OUTLINE_MODE_DASHDOT :
			return new float[] { 8F, 2F, 2F, 2F };

		case LegacyTagConstants.OUTLINE_MODE_DASHDOTDOT :
			return new float[] { 8F, 2F, 2F, 2F, 2F, 2F };

		case LegacyTagConstants.OUTLINE_MODE_SOLID :
		case LegacyTagConstants.OUTLINE_MODE_CLEAR :
		case LegacyTagConstants.OUTLINE_MODE_INSIDEFRAME :
		default :
			return null; // those are all treated as solid

		}
	}

}

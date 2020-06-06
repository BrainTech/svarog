package org.signalml.domain.tag;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.exception.ResolvableException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Tag;

/**
 * This class allows to export a {@link StyledTagSet StyledTagSet} to
 * an ASCII file, which can be imported into EEGLab
 *
 * @author Maciej Pawlisz, Titanis
 */
public class EEGLabTagExporter {
	protected static final Logger logger = Logger.getLogger(EEGLabTagExporter.class);
	/**
	 * Writes tags to a file. Header of the file is an instruction telling how to use that file.
	 * @param tagSet
	 * @param f
	 * @throws SignalMLException
	 */
	public void exportEEGLabTags(StyledTagSet tagSet, File f) throws SignalMLException
	{
		logger.info("Exporting tags to file"+f.getName());
		String contents = convertToString(tagSet);
		try {
			FileWriter fstream = new FileWriter(f);
			fstream.write(contents);
			fstream.close();
		} catch (IOException ex) {
			logger.error("Exception when writing EEGLab tags", ex);
			throw new ResolvableException(ex);
		}

	}

	private String convertToString(StyledTagSet tagSet) {
		StringBuffer str=new StringBuffer();
		str.append("#"+_("This is ASCII representation of tags from svarog")+"\n");
		str.append("#"+_("To import it to EEGLab, run File->Import event info->From Matlab array or ASCII File")+"\n");
		str.append("#"+_("Set the folowing parameters in the import dialog:")+"\n");
		str.append("#"+_("Append events: unchecked")+"\n");
		str.append("#"+_("Input field (column) names: 'type latency duration tag_type channel'")+"\n");
		str.append("#"+_("Number of file header lines: 10")+"\n");
		str.append("#"+_("Time unit: 1")+"\n");
		str.append("#"+_("Align event latencies to data events: NaN")+"\n");
		str.append("#"+_("Auto adjust new events sampling rate: unchecked")+"\n");
		str.append("#type\tlatency\tduration\ttag_type\tchannel\n");
		for (Iterator<Tag> it=tagSet.getTags().iterator(); it.hasNext();)
		{
			Tag tag = it.next();
			str.append(tag.getStyle().getName()+"\t");
			str.append(tag.getPosition());
			str.append("\t");
			str.append(tag.getLength());
			str.append("\t");
			str.append(tag.getType().getName()+"\t");
			str.append(tag.getChannel());
			str.append("\n");
		}
		return str.toString();
	}
}

/* TagBasedAtomFilter.java created 2008-02-25
 *
 */

package org.signalml.domain.book.filter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.SortedSet;
import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.util.Util;

/** TagBasedAtomFilter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("tagbookfilter")
public class TagBasedAtomFilter extends AbstractAtomFilter {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(TagBasedAtomFilter.class);

	private static final String[] CODES = new String[] { "tagBasedAtomFilter" };
	private static final Object[] ARGUMENTS = new Object[0];

	private String tagFilePath;
	private LinkedHashSet<String> styleNames;

	private double secondsBefore;
	private double secondsAfter;

	private transient TagDocument tagDocument;

	public TagBasedAtomFilter() {
		styleNames = new LinkedHashSet<String>();
	}

	public TagBasedAtomFilter(TagBasedAtomFilter filter) {
		super(filter);

		tagFilePath = filter.tagFilePath;
		styleNames = new LinkedHashSet<String>();
		for (String name : filter.styleNames) {
			styleNames.add(name);
		}
		secondsBefore = filter.secondsBefore;
		secondsAfter = filter.secondsAfter;

	}

	@Override
	public AbstractAtomFilter duplicate() {
		return new TagBasedAtomFilter(this);
	}

	public TagDocument getTagDocument() throws SignalMLException, IOException {
		if (tagDocument == null) {
			tagDocument = new TagDocument(new File(tagFilePath));
		}
		return tagDocument;
	}

	@Override
	public boolean matches(StandardBookSegment segment, StandardBookAtom atom) {

		if (tagDocument == null) {
			try {
				getTagDocument();
			} catch (SignalMLException ex) {
				logger.error("Failed to instantiate delegate", ex);
				throw new SanityCheckException("Failed to instantiate verified filter", ex);
			} catch (IOException ex) {
				logger.error("Failed to instantiate delegate", ex);
				throw new SanityCheckException("Failed to instantiate verified filter", ex);
			}
		}

		float position = segment.getSegmentTime() + atom.getTimePosition();
		SortedSet<Tag> tags = tagDocument.getTagSet().getTagsBetween((float)(position-secondsBefore), (float)(position+secondsAfter));
		TagStyle style;
		double markerPosition;
		for (Tag tag : tags) {
			style = tag.getStyle();
			if (styleNames.contains(style.getName())) {
				if (style.isMarker()) {
					markerPosition = tag.getPosition();
					if (position >= (markerPosition-secondsBefore) && position <= (markerPosition+secondsAfter)) {
						logger.debug("Atom @ [" + segment.getSegmentTime() + ":" + atom.getTimePosition() + "] accepted by marker [" + tag.toString() + "]");
						return true;
					}
				} else {
					if (position >= (tag.getPosition()-secondsBefore) && position < (tag.getEndPosition()+secondsAfter)) {
						logger.debug("Atom @ [" + segment.getSegmentTime() + ":" + atom.getTimePosition() + "] accepted by tag [" + tag.toString() + "]");
						return true;
					}
				}
			}
		}

		return false;

	}

	public String getTagFilePath() {
		return tagFilePath;
	}

	public void setTagFilePath(String tagFilePath) {
		if (!Util.equalsWithNulls(this.tagFilePath, tagFilePath)) {
			this.tagFilePath = tagFilePath;
			tagDocument = null;
		}
	}

	public LinkedHashSet<String> getStyleNames() {
		return styleNames;
	}

	public void setStyleNames(LinkedHashSet<String> styleNames) {
		this.styleNames = styleNames;
	}

	public double getSecondsBefore() {
		return secondsBefore;
	}

	public void setSecondsBefore(double secondsBefore) {
		this.secondsBefore = secondsBefore;
	}

	public double getSecondsAfter() {
		return secondsAfter;
	}

	public void setSecondsAfter(double secondsAfter) {
		this.secondsAfter = secondsAfter;
	}

	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return CODES;
	}

	@Override
	public String getDefaultMessage() {
		return _("Tag based atom filter");
	}

	@Override
	public void initialize() throws SignalMLException {
		try {
			getTagDocument();
		} catch (SignalMLException ex) {
			throw new SignalMLException("error.tagBasedAtomFilter.initializationFailed", ex);
		} catch (IOException ex) {
			throw new SignalMLException("error.tagBasedAtomFilter.initializationFailed", ex);
		}
	}

}

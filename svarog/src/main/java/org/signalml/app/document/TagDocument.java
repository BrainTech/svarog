/* TagDocument.java created 2007-09-12
 *
 */
package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;

import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.NativeFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import org.signalml.domain.tag.TagStyles;

/**
 * The document with {@link Tag tags} and {@link TagStyle tag styles}.
 * Contains two static methods, which create documents with:
 * <ul>
 * <li>the styles from the given file,</li>
 * <li>the default styles of sleeping stages.</li>
 * </ul>
 * Tags and styles are stored in the {@link StyledTagSet set}, which can be
 * obtained from this document.
 * Moreover every instance has methods that allow to:
 * <ul>
 * <li>get the name,</li>
 * <li>get the size (in seconds) of the block and the page,</li>
 * <li>get the {@link SignalDocument document} with a signal with which the
 * tags in this document are associated,</li>
 * <li>get the number of tags and tag styles.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDocument extends AbstractMutableFileDocument implements ExportedTagDocument {

	/**
         * Logger to save history of execution at
         */
	protected static final Logger logger = Logger.getLogger(TagDocument.class);

        /**
         * Charset used to save this document. Probably shouldn't be changed.
         */
        public static final String CHAR_SET = "UTF-8";

	/**
	 * the {@link StyledTagSet set} in which the {@link Tag tags} and
	 * {@link TagStyle styles} are stored
	 */
	private StyledTagSet tagSet;
	/**
	 * the {@link SignalDocument document} with a signal with which the tags
	 * in this document are associated
	 */
	private SignalDocument parent;
	/**
	 * the fallback name
	 */
	private String fallbackName = "";

	/**
	 * Empty constructor.
	 * @throws SignalMLException never thrown (???)
	 */
	public TagDocument() throws SignalMLException {
	}

	/**
	 * Constructor. Sets the size of the page and the block.
	 * Creates a new {@link StyledTagSet set} with these parameters.
	 * @param pageSize the size of the page
	 * @param blocksPerPage the number of blocks in the single page
	 * @throws SignalMLException never thrown
	 */
	public TagDocument(float pageSize, int blocksPerPage) throws SignalMLException {
		tagSet = new StyledTagSet(pageSize, blocksPerPage);
		saved = true;
	}

	/**
	 * Constructor. Reads the {@link Tag tags} and their {@link TagStyle
	 * styles} from file.
	 * @param file the file to be read
	 * @throws SignalMLException if there is no backing file or
	 * the document stored in the file has invalid format or other
	 * non I/O error occurs while reading a file
	 * @throws IOException if I/O error occurs while reading the file
	 */
	public TagDocument(File file) throws SignalMLException, IOException {
		super(file);
	}

	/**
	 * Constructor. Sets the {@link StyledTagSet set}.
	 * @param tagSet the set in which the {@link Tag tags} and {@link TagStyle
	 * styles} are stored
	 * @throws SignalMLException never thrown (???)
	 */
	public TagDocument(StyledTagSet tagSet) throws SignalMLException {
		this.tagSet = tagSet;
		saved = true;
	}

	@Override
	public void newDocument() throws SignalMLException {
		if (tagSet == null) {
			tagSet = new StyledTagSet();
		} else {
			tagSet = new StyledTagSet(tagSet.getPageSize(), tagSet.getBlocksPerPage());
		}
		saved = true;
	}

	/**
	 * Creates the new tag document with the {@link TagStyle styles} to mark
	 * sleep stages.
	 * @param pageSize the size of the page
	 * @param blocksPerPage the number of blocks in the page
	 * @return the created tag document
	 * @throws SignalMLException never thrown (??)
	 * @throws IOException if the stream to the file with styles could not
	 * be opened
	 */
	public static TagDocument getNewSleepDefaultDocument(float pageSize, int blocksPerPage) throws SignalMLException, IOException {

		Resource r = new ClassPathResource("org/signalml/domain/tag/sample/default_sleep_styles.xml");

		TagDocument templateDocument = new TagDocument();
		templateDocument.readDocument(r.getInputStream());

		TagStyles styles = templateDocument.getTagSet().getTagStyles().clone();
		StyledTagSet tagSet = new StyledTagSet(styles, pageSize, blocksPerPage);

		TagDocument tagDocument = new TagDocument(tagSet);
		return tagDocument;

	}

	/**
	 * Creates the new {@link TagDocument tag document} with the
	 * {@link TagStyle styles} taken from another tag document stored
	 * in the given file.
	 * @param file the file with the tag document from which the styles are
	 * to be taken
	 * @param pageSize the size of the page
	 * @param blocksPerPage the number of blocks in the page
	 * @return the created tag document
	 * @throws SignalMLException if the document stored in the file has
	 * invalid format or other non I/O error occurs while reading a file
	 * @throws IOException if I/O error occurs while reading the file
	 */
	public static TagDocument getStylesFromFileDocument(File file, float pageSize, int blocksPerPage) throws SignalMLException, IOException {
		TagDocument templateDocument = new TagDocument(file);

		TagStyles styles = templateDocument.getTagSet().getTagStyles().clone();
		StyledTagSet tagSet = new StyledTagSet(styles, pageSize, blocksPerPage);
		TagDocument tagDocument = new TagDocument(tagSet);
		return tagDocument;
	}

	@Override
	public void closeDocument() throws SignalMLException {
		tagSet = null;
		super.closeDocument();
	}

	@Override
	public void readDocument(InputStream is) {
		XStream streamer = getTagStreamer();
		tagSet = (StyledTagSet) streamer.fromXML(is);
	}

	@Override
	protected void writeDocument(OutputStream os) {

		XStream streamer = getTagStreamer();
		try {
			XMLUtils.writeXMLHeader(os);
		} catch (IOException ex) {
			logger.error("Failed to save tag - i/o exception", ex);
		}
		streamer.toXML(tagSet, os);

	}

	/**
	 * Returns the {@link StyledTagSet set} in which the {@link Tag tags} and
	 * {@link TagStyle styles} are stored.
	 * @return the set in which the tags and styles are stored
	 */
	public StyledTagSet getTagSet() {
		return tagSet;
	}

	@Override
	public float getBlockSize() {
		return tagSet.getBlockSize();
	}

	@Override
	public int getBlocksPerPage() {
		return tagSet.getBlocksPerPage();
	}

	@Override
	public float getPageSize() {
		return tagSet.getPageSize();
	}

	@Override
	public SignalDocument getParent() {
		return parent;
	}

	/**
	 * Sets the {@link SignalDocument document} with a signal with which the
	 * {@link Tag tags} in this document are associated.
	 * @param parent the document with a signal with which the tags
	 * in this document are associated
	 */
	public void setParent(SignalDocument parent) {
		if (this.parent != parent) {
			if (this.parent != null) {
				this.parent.removeTagDocument(this);
			}
			this.parent = parent;
			if (parent != null) {
				parent.addTagDocument(this);
			}
		}
	}

	/**
	 * Returns the streamer which reads {@link Tag tags} and {@link TagStyle
	 * tag styles} from file.
	 * @return the streamer which reads tags and styles from file
	 */
	private XStream getTagStreamer() {

		XStream streamer = new XStream(
		        new PureJavaReflectionProvider(new FieldDictionary(new NativeFieldKeySorter())),
		new DomDriver(CHAR_SET, new XmlFriendlyReplacer() {

			// the classes in question don't have $'s in their names and the
			// format specifies single underscores, so disregard replacing
			@Override
			public String escapeName(String name) {
				return name;
			}
			@Override
			public String unescapeName(String name) {
				return name;
			}

		}

		                     ));
		Annotations.configureAliases(streamer,
		                             StyledTagSet.class
		                            );
		XMLUtils.configureStreamerForMontage(streamer);

		return streamer;

	}

	@Override
	public String getName() {
		return (backingFile != null ? backingFile.getName() : fallbackName);
	}

	@Override
	public String getFallbackName() {
		return fallbackName;
	}

	/**
	 * Sets the fallback name.
	 * @param fallbackName the fallback name to set
	 */
	public void setFallbackName(String fallbackName) {
		this.fallbackName = fallbackName;
	}

	@Override
	public Object[] getArguments() {
		SignalDocument document = getParent();
		String parentName = document.getName();
		return new Object[] {
		               getName(),
		               parentName
		       };
	}

	@Override
	public String[] getCodes() {
		if (getBackingFile() == null) {
			return new String[] { "newTagDocument" };
		} else {
			return new String[] { "tagDocument" };
		}
	}

	@Override
	public String getDefaultMessage() {
		return toString();
	}

	/**
	 * Returns the number of {@link TagStyle tag styles} in this document.
	 * @return the number of tag styles in this document
	 */
	public int getTagStyleCount() {
		return tagSet.getTagStyleCount();
	}

	@Override
	public int getTagCount() {
		return tagSet.getTagCount();
	}

	/**
	 * Sets the {@link TagStyle#isMarker() marker} styles from this document
	 * in the {@link SignalSpaceConstraints signal space constraints}.
	 * @param constraints the constraints in which the marker styles are
	 * to be set
	 */
	public void updateSignalSpaceConstraints(SignalSpaceConstraints constraints) {

		Collection<TagStyle> channelStyles = getTagSet().getChannelStyles();
		Iterator<TagStyle> it = channelStyles.iterator();
		while (it.hasNext()) {
			if (!it.next().isMarker()) {
				it.remove();
			}
		}

		TagStyle[] markerStyles = new TagStyle[channelStyles.size()];
		channelStyles.toArray(markerStyles);

		constraints.setMarkerStyles(markerStyles);

	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(new LabelledPropertyDescriptor("property.tagDocument.tagStyleCount", "tagStyleCount", TagDocument.class, "getTagStyleCount", null));
		list.add(new LabelledPropertyDescriptor("property.tagDocument.tagCount", "tagCount", TagDocument.class, "getTagCount", null));

		return list;

	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.ExportedTagDocument#getSetOfTags()
	 */
	@Override
	public Set<ExportedTag> getSetOfTags() {
		Set<Tag> tagSet = getTagSet().getTags();

		Set<ExportedTag> exportedTagSet = new TreeSet<ExportedTag>();
		for (Tag tag : tagSet){
			exportedTagSet.add(tag);
		}
		return exportedTagSet;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.ExportedTagDocument#getTagStyles()
	 */
	@Override
	public Set<ExportedTagStyle> getTagStyles() {
		StyledTagSet tagSet = getTagSet();
		List<TagStyle> styles = tagSet.getListOfStyles();
		Set<ExportedTagStyle> exportedStyles = new LinkedHashSet<ExportedTagStyle>();
		for(TagStyle style : styles){
			exportedStyles.add(style);
		}
		return exportedStyles;
	}

}

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

/** TagDocument
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDocument extends AbstractMutableFileDocument implements ExportedTagDocument {

	private StyledTagSet tagSet;
	private SignalDocument parent;
	private String fallbackName = "";

	private TagDocument() throws SignalMLException {
	}

	public TagDocument(float pageSize, int blocksPerPage) throws SignalMLException {
		tagSet = new StyledTagSet(pageSize, blocksPerPage);
		saved = true;
	}

	public TagDocument(File file) throws SignalMLException, IOException {
		super(file);
	}

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

	public static TagDocument getNewSleepDefaultDocument(float pageSize, int blocksPerPage) throws SignalMLException, IOException {

		Resource r = new ClassPathResource("org/signalml/domain/tag/sample/default_sleep_styles.xml");

		TagDocument templateDocument = new TagDocument();
		templateDocument.readDocument(r.getInputStream());

		LinkedHashMap<String,TagStyle> styles = new LinkedHashMap<String, TagStyle>();
		Collection<TagStyle> templateStyles = templateDocument.getTagSet().getStyles();
		for (TagStyle style : templateStyles) {
			styles.put(style.getName(), style);
		}
		StyledTagSet tagSet = new StyledTagSet(styles, pageSize, blocksPerPage);

		TagDocument tagDocument = new TagDocument(tagSet);
		return tagDocument;

	}

	public static TagDocument getStylesFromFileDocument(File file, float pageSize, int blocksPerPage) throws SignalMLException, IOException {
		TagDocument templateDocument = new TagDocument(file);
		LinkedHashMap<String,TagStyle> styles = new LinkedHashMap<String, TagStyle>();
		Collection<TagStyle> templateStyles = templateDocument.getTagSet().getStyles();
		for (TagStyle style : templateStyles) {
			styles.put(style.getName(), style);
		}
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
	protected void readDocument(InputStream is) {
		XStream streamer = getTagStreamer();
		tagSet = (StyledTagSet) streamer.fromXML(is);
	}

	@Override
	protected void writeDocument(OutputStream os) {

		XStream streamer = getTagStreamer();
		streamer.toXML(tagSet, os);

	}

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

	private XStream getTagStreamer() {

		XStream streamer = new XStream(
		        new PureJavaReflectionProvider(new FieldDictionary(new NativeFieldKeySorter())),
		new DomDriver("UTF-8", new XmlFriendlyReplacer() {

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

	public int getTagStyleCount() {
		return tagSet.getTagStyleCount();
	}

	@Override
	public int getTagCount() {
		return tagSet.getTagCount();
	}

	public void updateSignalSpaceConstraints(SignalSpaceConstraints constraints) {

		LinkedHashSet<TagStyle> channelStyles = getTagSet().getChannelStyles();
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
		Set<TagStyle> styles = tagSet.getStyles();
		Set<ExportedTagStyle> exportedStyles = new LinkedHashSet<ExportedTagStyle>();
		for(TagStyle style : styles){
			exportedStyles.add(style);
		}
		return exportedStyles;
	}

}

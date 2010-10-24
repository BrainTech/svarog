package org.signalml.domain.tag;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class TagStylesGenerator {
	protected static final Logger logger = Logger.getLogger(TagStylesGenerator.class);
	protected static String STYLES_PATH = "org/signalml/domain/tag/sample/default_sleep_styles.xml";
	protected HashMap<String,TagStyle> styles;
	protected HashMap<String,TagStyle> tempStyles;

	public TagStylesGenerator() {
		this.tempStyles = new HashMap<String, TagStyle>();
		
		this.styles = new HashMap<String, TagStyle>();
		Collection<TagStyle> templateStyles = this.getStylesFromDataBase();
		for (TagStyle style : templateStyles) {
			styles.put(style.getName(), style);
		}
		
	}
	protected Collection<TagStyle> getStylesFromDataBase(){
		
		//Create for a moment TagDocument so that it'll read-in database styles
		Resource r = new ClassPathResource(STYLES_PATH);
		TagDocument templateDocument;
		try {
			templateDocument = new TagDocument();
		} catch (SignalMLException e) {
			logger.error("Couldn't create TagDocument to read-in database styles!");
			return new HashSet<TagStyle>();
		}
		try {
			templateDocument.readDocument(r.getInputStream());
		} catch (IOException e) {
			logger.error("An IO error occured while trying to read-in database styles!");
			return new HashSet<TagStyle>();
		}

		return templateDocument.getTagSet().getStyles();
	}
	protected TagStyle generateNewStyleFor(String name, double tagLength) {
		TagStyle style = new TagStyle(SignalSelectionType.CHANNEL, name, "", 
				Color.BLUE, Color.RED, 5);
		if (tagLength < 0.01)
			style.setMarker(true);
		else
			style.setMarker(false);
		this.tempStyles.put(name, style);
		return style;
	}
	public TagStyle getTempOrRealStyleFor(String name) {
		TagStyle style = this.styles.get(name);
		if (style != null)
			return style;
		else 
			return this.tempStyles.get(name);
	}

	public TagStyle getStyleFor(String name) {
		return this.styles.get(name);
	}
	
	
	public TagStyle getSmartStyleFor(String name, double tagLength) {
		TagStyle style = this.getTempOrRealStyleFor(name);
		if (style != null)
			return style;
		else 
			return generateNewStyleFor(name,tagLength);
	}
}

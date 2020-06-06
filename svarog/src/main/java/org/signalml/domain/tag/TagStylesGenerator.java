package org.signalml.domain.tag;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;
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
	protected Stack<Color> colors;

	private double pageSize;
	private double blockSize;

	private TagStylesGenerator() {

		this.tempStyles = new HashMap<String, TagStyle>();

		this.styles = new HashMap<String, TagStyle>();
		Collection<TagStyle> templateStyles = this.getStylesFromDataBase();
		for (TagStyle style : templateStyles) {
			styles.put(style.getName(), style);
		}

		this.colors = new Stack<Color>();

		colors.push(Color.GRAY);
		colors.push(Color.PINK);
		colors.push(Color.YELLOW);
		colors.push(Color.DARK_GRAY);
		colors.push(Color.ORANGE);
		colors.push(Color.MAGENTA);
		colors.push(Color.LIGHT_GRAY);
		colors.push(Color.RED);
		colors.push(Color.CYAN);
		colors.push(Color.GREEN);
		colors.push(Color.BLUE);
	}

	/**
	 * Constructor. Creates a new tag style generator for the given page size and the given
	 * number of blocks per page. Page size and number of blocks per page is needed to help to
	 * determine the type of tag (page/block/channel) for which styles would be generated.
	 * @param pageSize the length of the page (in seconds)
	 * @param blocksPerPage number of blocks per page
	 */
	public TagStylesGenerator(double pageSize, int blocksPerPage) {
		this();
		this.pageSize = pageSize;
		this.blockSize = pageSize / blocksPerPage;
	}

	protected Collection<TagStyle> getStylesFromDataBase() {

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

		return templateDocument.getTagSet().getListOfStyles();

	}

	protected TagStyle generateNewStyleFor(String name, double tagLength, int channel) {

		//determining the type of tag
		SignalSelectionType signalSelectionType;
		if (channel != -1)
			signalSelectionType = SignalSelectionType.CHANNEL;
		else if (tagLength == pageSize)
			signalSelectionType = SignalSelectionType.PAGE;
		else if (tagLength == blockSize)
			signalSelectionType = SignalSelectionType.BLOCK;
		else
			signalSelectionType = SignalSelectionType.CHANNEL;

		//generating new style for the tag
		Color c = this.getNextColor();
		TagStyle style = new TagStyle(signalSelectionType, name, "",
									  c, Color.RED, 1);
		logger.info("Generated color for:"+name+" = "+c+" with type: "+signalSelectionType);
		if (tagLength < 0.001)
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
	public Color getNextColor() {
		if (this.colors.empty()) {
			Random r = new Random();
			Color c = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
			return c;
		} else
			return this.colors.pop();
	}

	public TagStyle getStyleFor(String name) {
		return this.styles.get(name);
	}

	public TagStyle getSmartStyleFor(String name, double tagLength, int channel) {

		TagStyle style = this.getTempOrRealStyleFor(name);
		if (style != null)
			return style;
		else
			return generateNewStyleFor(name, tagLength, channel);

	}

}

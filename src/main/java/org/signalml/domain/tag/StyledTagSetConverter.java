/* StyledTagSetConverter.java created 2007-09-28
 *
 */

package org.signalml.domain.tag;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.TreeSet;

import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.signalml.app.model.PagingParameterDescriptor;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.util.ColorConverter;
import org.signalml.util.FloatArrayConverter;
import org.signalml.util.KeyStrokeConverter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.basic.FloatConverter;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * This class is responsible for marshaling/unmarshaling {@link StyledTagSet}
 * objects to/from textual data.
 * Doesn't seem to be used.
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StyledTagSetConverter implements Converter {

	protected static final Logger logger = Logger.getLogger(StyledTagSetConverter.class);

	private FloatConverter floatConverter = new FloatConverter();
	private IntConverter intConverter = new IntConverter();
	private ColorConverter colorConverter = new ColorConverter();
	private KeyStrokeConverter keyStrokeConverter = new KeyStrokeConverter();
	private FloatArrayConverter floatArrayConverter = new FloatArrayConverter();
	private BooleanConverter booleanConverter = new BooleanConverter("1", "0", false);

        /**
         * Converts a {@link StyledTagSet StyledTagSet} to textual data.
         * @param value the object to be marshaled
         * @param writer a stream to write to
         * @param context a context that allows nested objects to be processed
         * by XStream
         */
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

		StyledTagSet sts = (StyledTagSet) value;
		TagSignalIdentification ident = sts.getTagSignalIdentification();
		String info = sts.getInfo();
		String montageInfo = sts.getMontageInfo();
		Montage montage = sts.getMontage();
		Collection<TagStyle> styles;
		String annotation;

		if (ident != null) {
			writer.startNode("datafile_identification");
			if (ident.getFormatId() != null) {
				writer.startNode("format");
				writer.addAttribute("id", ident.getFormatId());
				writer.endNode();
			}
			if (ident.getFileName() != null) {
				writer.startNode("name");
				writer.setValue(ident.getFileName());
				writer.endNode();
			}
			SignalChecksum signalChecksum = ident.getChecksum();
			if (signalChecksum != null) {
				writer.startNode("signature");
				writer.addAttribute("method", signalChecksum.getMethod());
				writer.addAttribute("offset", intConverter.toString(signalChecksum.getOffset()));
				writer.addAttribute("length", intConverter.toString(signalChecksum.getLength()));
				writer.addAttribute("value", signalChecksum.getValue());
				writer.endNode();
			}
			writer.endNode();
		}

		writer.startNode("paging");
		writer.addAttribute("page_size", floatConverter.toString(sts.getPageSize()));
		writer.addAttribute("blocks_per_page", intConverter.toString(sts.getBlocksPerPage()));
		writer.endNode();

		writer.startNode("tag_definitions");

		writer.startNode("def_group");
		writer.addAttribute("name", "pageTags");
		styles = sts.getPageStyles();
		for (TagStyle style : styles) {
			marshalStyle(writer,style);
		}
		writer.endNode();

		writer.startNode("def_group");
		writer.addAttribute("name", "blockTags");
		styles = sts.getBlockStyles();
		for (TagStyle style : styles) {
			marshalStyle(writer,style);
		}
		writer.endNode();

		writer.startNode("def_group");
		writer.addAttribute("name", "channelTags");
		styles = sts.getChannelStyles();
		for (TagStyle style : styles) {
			marshalStyle(writer,style);
		}
		writer.endNode();

		writer.endNode(); // tag_definitions

		writer.startNode("tag_data");

		if (info != null) {
			writer.startNode("text_info");
			writer.setValue(info);
			writer.endNode();
		}

		if (montage != null) {
			writer.startNode("montage");
			context.convertAnother(montage);
			writer.endNode();
		}
		else if (montageInfo != null && !montageInfo.isEmpty()) {
			writer.startNode("montage_info");
			writer.setValue(montageInfo);
			writer.endNode();
		}

		writer.startNode("tags");
		for (Tag tag : sts.getTags()) {
			writer.startNode("tag");
			writer.addAttribute("name", tag.getStyle().getName());
			writer.addAttribute("position", floatConverter.toString(tag.getPosition()));
			writer.addAttribute("length", floatConverter.toString(tag.getLength()));
			writer.addAttribute("channel_number", intConverter.toString(tag.getChannel()));
			annotation = tag.getAnnotation();
			if (annotation != null) {
				writer.startNode("annotation");
				writer.setValue(annotation);
				writer.endNode();
			}
			writer.endNode();
		}
		writer.endNode();

		writer.endNode(); // tag_data

	}

        /**
         * Converts a {@link TagStyle TagStyle} to textual data.
         * @param style The TagStyle to be marshaled
         * @param writer A stream to write to.
         */
	private void marshalStyle(HierarchicalStreamWriter writer, TagStyle style) {
		writer.startNode("tag_item");
		writer.addAttribute("name", style.getName());
		String description = style.getDescription();
		writer.addAttribute("description", description != null ? description : "");
		writer.addAttribute("fill_color", colorConverter.toString(style.getFillColor()));
		writer.addAttribute("outline_color", colorConverter.toString(style.getOutlineColor()));
		writer.addAttribute("outline_width", floatConverter.toString(style.getOutlineWidth()));
		writer.addAttribute("outline_dash", floatArrayConverter.toString(style.getOutlineDash()));
		writer.addAttribute("key_shortcut", keyStrokeConverter.toString(style.getKeyStroke()));
		writer.addAttribute("marker", booleanConverter.toString(style.isMarker()));
		writer.endNode();
	}

        /**
         * Convert textual data back into a {@link StyledTagSet StyledTagSet}.
         * @param reader the stream to read the text from.
         * @param context a context that allows nested objects to be processed by XStream.
         * @return the created StyledTagSet
         */
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

		TagSignalIdentification ident = null;
		String info = null;
		String montageInfo = null;

		TagStyle style;
		Tag tag;
		float position;
		float length;
		int channel;
		String annotation;

		LinkedHashMap<String,TagStyle> styles = new LinkedHashMap<String, TagStyle>();
		TreeSet<Tag> tags = new TreeSet<Tag>();

		float pageSize = PagingParameterDescriptor.DEFAULT_PAGE_SIZE;
		int blocksPerPage = PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE;
		boolean pagingOk = false;

		Montage montage = null;

		while (reader.hasMoreChildren()) {
			reader.moveDown();
			if ("datafile_identification".equals(reader.getNodeName())) {
				ident = new TagSignalIdentification();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if ("format".equals(reader.getNodeName())) {
						ident.setFormatId(reader.getAttribute("id"));
					}
					else if ("name".equals(reader.getNodeName())) {
						ident.setFileName(reader.getValue());
					}
					else if ("signature".equals(reader.getNodeName())) {
						SignalChecksum signalChecksum = new SignalChecksum();
						signalChecksum.setMethod(reader.getAttribute("method"));
						signalChecksum.setOffset((Integer) intConverter.fromString(reader.getAttribute("offset")));
						signalChecksum.setLength((Integer) intConverter.fromString(reader.getAttribute("length")));
						signalChecksum.setValue(reader.getAttribute("value"));
						ident.setChecksum(signalChecksum);
					}
					reader.moveUp();
				}
			} else if ("paging".equals(reader.getNodeName())) {
				boolean pageSizeOk = false;
				boolean blocksPerPageOk = false;
				String attr = reader.getAttribute("page_size");
				if (attr != null && !attr.isEmpty()) {
					pageSize = (Float) floatConverter.fromString(attr);
					pageSizeOk = true;
				}

				attr = reader.getAttribute("blocks_per_page");
				if (attr != null && !attr.isEmpty()) {
					blocksPerPage = (Integer) intConverter.fromString(attr);
					blocksPerPageOk = true;
				}

				if (pageSizeOk && blocksPerPageOk) {
					pagingOk = true;
				}
			} else if ("tag_definitions".equals(reader.getNodeName())) {
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					String type = reader.getAttribute("name");
					if ("pageTags".equals(type)) {
						while (reader.hasMoreChildren()) {
							reader.moveDown();
							if ("tag_item".equals(reader.getNodeName())) {
								style = new TagStyle(SignalSelectionType.PAGE);
								unmarshalStyle(reader, style);
								styles.put(style.getName(),style);
							}
							reader.moveUp();
						}
					} else if ("blockTags".equals(type)) {
						while (reader.hasMoreChildren()) {
							reader.moveDown();
							if ("tag_item".equals(reader.getNodeName())) {
								style = new TagStyle(SignalSelectionType.BLOCK);
								unmarshalStyle(reader, style);
								styles.put(style.getName(),style);
							}
							reader.moveUp();
						}
					} else if ("channelTags".equals(type)) {
						while (reader.hasMoreChildren()) {
							reader.moveDown();
							if ("tag_item".equals(reader.getNodeName())) {
								style = new TagStyle(SignalSelectionType.CHANNEL);
								unmarshalStyle(reader, style);
								styles.put(style.getName(),style);
							}
							reader.moveUp();
						}
					}
					reader.moveUp();
				}
			} else if ("tag_data".equals(reader.getNodeName())) {
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if ("text_info".equals(reader.getNodeName())) {
						info = reader.getValue();
					} else if ("montage".equals(reader.getNodeName())) {
						montage = (Montage) context.convertAnother(null, Montage.class);
					} else if ("montage_info".equals(reader.getNodeName())) {
						montageInfo = reader.getValue();
					} else if ("tags".equals(reader.getNodeName())) {
						while (reader.hasMoreChildren()) {
							reader.moveDown();
							if ("tag".equals(reader.getNodeName())) {
								style = styles.get(reader.getAttribute("name"));
								if (style == null) {
									style = TagStyle.getDefault();
								}
								position = (Float) floatConverter.fromString(reader.getAttribute("position"));
								length = (Float) floatConverter.fromString(reader.getAttribute("length"));
								channel = (Integer) intConverter.fromString(reader.getAttribute("channel_number"));
								annotation = null;
								while (reader.hasMoreChildren()) {
									reader.moveDown();
									if ("annotation".equals(reader.getNodeName())) {
										annotation = reader.getValue();
									}
									reader.moveUp();
								}
								tag = new Tag(style,position,length,channel,annotation);
								tags.add(tag);
							}
							reader.moveUp();
						}
					}
					reader.moveUp();
				}
			}
			reader.moveUp();
		}

		if (!pagingOk) {
			logger.warn("WARNING: tag doesn't contain paging information, assuming defaults");
			pageSize = PagingParameterDescriptor.DEFAULT_PAGE_SIZE;
			blocksPerPage = PagingParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE;
		}

		StyledTagSet sts = new StyledTagSet(styles,tags,pageSize,blocksPerPage);
		sts.setTagSignalIdentification(ident);
		sts.setInfo(info);
		if (montage != null) {
			sts.setMontage(montage);
		} else {
			sts.setMontageInfo(montageInfo);
		}

		return sts;
	}

        /**
         * Convert textual data back into a {@link TagStyle TagStyle}.
         * @param reader the stream to read the text from.
         * @param style the TagStyle object on which read data will be written
         */
	private void unmarshalStyle(HierarchicalStreamReader reader, TagStyle style) {

		String name = reader.getAttribute("name");
		String description = reader.getAttribute("description");
		Color fillColor = (Color) colorConverter.fromString(reader.getAttribute("fill_color"));
		Color outlineColor = (Color) colorConverter.fromString(reader.getAttribute("outline_color"));
		float outlineWidth = (Float) floatConverter.fromString(reader.getAttribute("outline_width"));
		float[] outlineDash = (float[]) floatArrayConverter.fromString(reader.getAttribute("outline_dash"));
		KeyStroke keyStroke = (KeyStroke) keyStrokeConverter.fromString(reader.getAttribute("key_shortcut"));
		boolean marker = false;
		String markerAttr = reader.getAttribute("marker");
		if (markerAttr != null && !markerAttr.isEmpty()) {
			marker = ((Boolean) booleanConverter.fromString(markerAttr)).booleanValue();
		}

		if (name == null || fillColor == null || outlineColor == null) {
			throw new NullPointerException("Bad tag file format");
		}

		style.setParameters(name, description, fillColor, outlineColor, outlineWidth, outlineDash, keyStroke, marker);

	}

        /**
         * Determines whether this converter can marshal a particular type.
         * @param clazz the Class representing the object type to be converted
         * @return true if this converter can marshal a particular type,
         * false otherwise
         */
	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class clazz) {
		return (StyledTagSet.class.equals(clazz));
	}

}

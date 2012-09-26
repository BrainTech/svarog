/* TagDocumentTest.java created 2007-10-01
 *
 */

package org.signalml.app.document;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;
import javax.swing.KeyStroke;
import org.junit.Before;
import org.junit.Test;
import org.signalml.domain.tag.StyledTagSet;
import static org.junit.Assert.*;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.tagStyle.TagAttributeValue;
import org.signalml.plugin.export.signal.tagStyle.TagStyleAttributeDefinition;
import org.signalml.plugin.export.signal.tagStyle.TagStyleAttributes;

/** StyledTagSetConverterTest
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagDocumentTest {

	private File defaultFile = new File("src/main/resources/org/signalml/domain/tag/sample/default_sleep_styles.xml");
	private TagDocument tagDocument;
	private StyledTagSet sts;

	/**
	 * sets everything up for the test
	 */
	@Before
	public void setUp() throws SignalMLException, IOException {
		tagDocument = new TagDocument(defaultFile);
		sts = tagDocument.getTagSet();
	}

	private void testDefaults() {
		assertEquals(12, sts.getTagStyleCount());
		assertEquals(7, sts.getPageStyleCount());
		assertEquals(1, sts.getBlockStyleCount());
		assertEquals(4, sts.getChannelStyleCount());
		assertEquals(0, sts.getTagCount());

		Collection<TagStyle> pageStyles = sts.getPageStyles();
		Iterator<TagStyle> it = pageStyles.iterator();
		TagStyle style = it.next();

		assertNotNull(style);
		assertEquals("1", style.getName());
		assertEquals("Stage 1", style.getDescription());
		assertEquals(new Color(0x00, 0xCC, 0xCC), style.getOutlineColor());
		assertEquals(new Color(0x00, 0xCC, 0xCC), style.getFillColor());
		assertEquals(1.0F, style.getOutlineWidth(), 0);
		assertNull(style.getOutlineDash());
		assertEquals(KeyStroke.getKeyStroke("pressed 1"), style.getKeyStroke());
	}

	/**
	 * Tests if the data read from the default tag file is appropriate.
	 */
	@Test
	public void testDefaultLoad() {
		testDefaults();
	}

	/**
	 * Tests if the data read from a tag file, written to a different tag file
	 * and then read from this different tag file is the same data.
	 * @throws Exception thrown when the IO exception occurs
	 */
	@Test
	public void testDefaultSaveLoad() throws Exception {

		File tempFile = File.createTempFile("smltest", ".xml");
		tempFile.deleteOnExit();

		TagDocument defaultTagDocument = new TagDocument();
		defaultTagDocument.readDocument(new FileInputStream(defaultFile));

		defaultTagDocument.writeDocument(new FileOutputStream(tempFile));

		TagDocument writtenTagDocument = new TagDocument(tempFile);

		sts = writtenTagDocument.getTagSet();
		testDefaults();

	}

	/**
	 * Tests if the data read from the default tag file, changed and then written
	 * to a tag file is correct.
	 * @throws Exception thrown when the IO exception occurs
	 */
	@Test
	public void testAddTagsAndSave() throws Exception {

		Tag tag = new Tag(sts.getStyle(SignalSelectionType.PAGE, "1"), 120F, 20F, Tag.CHANNEL_NULL, null);
		sts.addTag(tag);

		tag = new Tag(sts.getStyle(SignalSelectionType.CHANNEL, "Y"), 300F, 400F, 5, "test");
		TagStyleAttributeDefinition attributeDefinition = new TagStyleAttributeDefinition("testAttributeKey", "ble", true);
		sts.getStyle(SignalSelectionType.CHANNEL, "Y").getAttributesDefinitions().addAttributeDefinition(attributeDefinition);
		tag.setAttribute(new TagAttributeValue(attributeDefinition, "testAttributeValue"));
		sts.addTag(tag);

		assertEquals(12, sts.getTagStyleCount());
		assertEquals(7, sts.getPageStyleCount());
		assertEquals(1, sts.getBlockStyleCount());
		assertEquals(4, sts.getChannelStyleCount());
		assertEquals(2, sts.getTagCount());

		File f = File.createTempFile("smltest2", ".xml");
		f.deleteOnExit();

		//writing the data to XML
		TagDocument writeTagDocument = new TagDocument(sts);
		FileOutputStream os = new FileOutputStream(f);
		writeTagDocument.writeDocument(os);
		os.close();

		//reading the data from an XML file
		writeTagDocument = new TagDocument();
		writeTagDocument.readDocument(new FileInputStream(f));
		sts = writeTagDocument.getTagSet();

		//asserts
		assertEquals(12, sts.getTagStyleCount());
		assertEquals(7, sts.getPageStyleCount());
		assertEquals(1, sts.getBlockStyleCount());
		assertEquals(4, sts.getChannelStyleCount());
		assertEquals(2, sts.getTagCount());

		SortedSet<Tag> tags = sts.getTags();
		assertEquals(2, tags.size());

		tag = tags.first();
		assertEquals("1", tag.getStyle().getName());
		assertEquals(120F, tag.getPosition(), 0);
		assertEquals(20F, tag.getLength(), 0);
		assertEquals(Tag.CHANNEL_NULL, tag.getChannel());
		assertNull(tag.getAnnotation());

		tag = tags.last();
		assertEquals("Y", tag.getStyle().getName());
		assertEquals(300F, tag.getPosition(), 0);
		assertEquals(400F, tag.getLength(), 0);
		assertEquals(5, tag.getChannel());
		assertEquals("test", tag.getAnnotation());
		assertEquals(1, tag.getAttributes().getAttributesList().size());
		assertEquals("testAttributeValue", tag.getAttributes().getAttribute("testAttributeKey").getAttributeValue());

	}

}
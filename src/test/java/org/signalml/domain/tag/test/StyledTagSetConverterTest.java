/* StyledTagSetConverterTest.java created 2007-10-01
 *
 */

package org.signalml.domain.tag.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import javax.swing.KeyStroke;

import org.junit.Before;
import org.junit.Test;
import org.signalml.app.util.XMLUtils;
import org.signalml.domain.tag.StyledTagSet;
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

/** StyledTagSetConverterTest
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StyledTagSetConverterTest {

	private StyledTagSet sts = null;
	private XStream stream;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		Resource r = new ClassPathResource("org/signalml/domain/tag/sample/default_sleep_styles.xml");
		InputStream is = r.getInputStream();
		stream = new XStream(new PureJavaReflectionProvider(new FieldDictionary(new NativeFieldKeySorter())), new DomDriver("UTF-8", new XmlFriendlyReplacer() {
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
		Annotations.configureAliases(stream,
		                             StyledTagSet.class
		                            );
		XMLUtils.configureStreamerForMontage(stream);

		sts = (StyledTagSet) stream.fromXML(is);

	}

	private void testDefaults() throws Exception {
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

	@Test
	public void testDefaultLoad() throws Exception {
		testDefaults();
	}

	@Test
	public void testDefaultSaveLoad() throws Exception {

		File f = File.createTempFile("smltest", ".xml");
		f.deleteOnExit();

//		System.out.println(f.getAbsolutePath());

		OutputStream os = new FileOutputStream(f);

		stream.toXML(sts, os);
		os.close();

		InputStream is = new FileInputStream(f);

		sts = (StyledTagSet) stream.fromXML(is);
		testDefaults();

	}

	@Test
	public void testAddTagsAndSave() throws Exception {

		sts.addTag(new Tag(sts.getStyle("1"), 120F, 20F, Tag.CHANNEL_NULL, null));
		sts.addTag(new Tag(sts.getStyle("Y"), 300F, 400F, 5, "test"));

		assertEquals(12, sts.getTagStyleCount());
		assertEquals(7, sts.getPageStyleCount());
		assertEquals(1, sts.getBlockStyleCount());
		assertEquals(4, sts.getChannelStyleCount());
		assertEquals(2, sts.getTagCount());

		File f = File.createTempFile("smltest", ".xml");
		f.deleteOnExit();

//		System.out.println(f.getAbsolutePath());

		OutputStream os = new FileOutputStream(f);

		stream.toXML(sts, os);
		os.close();

		InputStream is = new FileInputStream(f);

		sts = (StyledTagSet) stream.fromXML(is);

		assertEquals(12, sts.getTagStyleCount());
		assertEquals(7, sts.getPageStyleCount());
		assertEquals(1, sts.getBlockStyleCount());
		assertEquals(4, sts.getChannelStyleCount());
		assertEquals(2, sts.getTagCount());

		SortedSet<Tag> tags = sts.getTags();
		assertEquals(2, tags.size());

		Tag tag = tags.first();
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



	}


}

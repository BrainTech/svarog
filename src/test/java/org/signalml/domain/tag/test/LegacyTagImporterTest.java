/* LegacyTagImporterTest.java created 2007-10-03
 * 
 */

package org.signalml.domain.tag.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.Tag;
import org.signalml.domain.tag.TagStyle;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/** LegacyTagImporterTest
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class LegacyTagImporterTest {

	private File legacyTagFile;
	private LegacyTagImporter lti; 
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Resource r = new ClassPathResource("org/signalml/domain/tag/test/ZCB02A.TAG");
		legacyTagFile = r.getFile();
		
		lti = new LegacyTagImporter();
	}

	/**
	 * Test method for {@link org.signalml.domain.tag.LegacyTagImporter#importLegacyTags(java.io.File, float)}.
	 */
	@Test
	public void testImportLegacyTags() throws Exception {
		
		StyledTagSet sts = lti.importLegacyTags(legacyTagFile, 128F);

		assertEquals(7, sts.getPageStyleCount());
		assertEquals(1, sts.getBlockStyleCount());
		assertEquals(0, sts.getChannelStyleCount());
		
		TagStyle style = sts.getPageStyleAt(0);
		assertEquals(SignalSelectionType.PAGE, style.getType());
		assertEquals("1", style.getName());
		assertEquals(Color.WHITE, style.getFillColor());
		assertEquals(Color.WHITE, style.getOutlineColor());
		
		assertEquals(1582, sts.getPageTagCount());
		assertEquals(1401, sts.getBlockTagCount());
		assertEquals(0, sts.getChannelTagCount());
		
		Tag tag = sts.getPageTagAt(0);
		assertEquals("w", tag.getStyle().getName());
		assertEquals(0F, tag.getPosition(), 0);
		assertEquals(20F, tag.getLength(), 0);
		assertEquals(Tag.CHANNEL_NULL, tag.getChannel());
				
		tag = sts.getPageTagAt(724);
		assertEquals("r", tag.getStyle().getName());
		assertEquals(724*20F, tag.getPosition(), 0);
		assertEquals(20F, tag.getLength(), 0);
		assertEquals(Tag.CHANNEL_NULL, tag.getChannel());
		
		style = sts.getStyle("r");
		assertNotNull(style);
		assertEquals(SignalSelectionType.PAGE, style.getType());
		assertEquals("r", style.getName());
		assertEquals(Color.WHITE, style.getFillColor());
		assertEquals(Color.WHITE, style.getOutlineColor());

		style = sts.getStyle("a");
		assertNotNull(style);
		assertEquals(SignalSelectionType.BLOCK, style.getType());
		assertEquals("a", style.getName());
		assertEquals(new Color(128,128,128), style.getFillColor());
		assertEquals(new Color(128,128,128), style.getOutlineColor());
		
		tag = sts.getBlockTagAt(0);
		assertNotNull(tag);
		assertEquals("a", tag.getStyle().getName());
				
	}

}

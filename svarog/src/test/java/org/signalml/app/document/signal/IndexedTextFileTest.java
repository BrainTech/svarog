package org.signalml.app.document.signal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author piotr.rozanski@braintech.pl
 */
public class IndexedTextFileTest {
	
	private static IndexedTextFile prepareTemporaryTestFile(String contents) throws IOException {
		File temp = File.createTempFile("ascii", ".txt");
		temp.deleteOnExit();
		try (FileWriter writer = new FileWriter(temp)) {
			writer.write(contents);
		}
		return new IndexedTextFile(temp, 5);
	}
	
	@Test
	public void testEmptyFile() throws IOException {
		IndexedTextFile t1 = prepareTemporaryTestFile("");
		Assert.assertEquals(0, t1.getLineCount());
	}
	
	@Test
	public void testOneLineFile() throws IOException {
		IndexedTextFile temp = prepareTemporaryTestFile("abc");
		Assert.assertEquals(1, temp.getLineCount());
		Assert.assertEquals("abc", temp.getLine(0));
	}
	
	@Test
	public void testOneLineFileWithEndline() throws IOException {
		IndexedTextFile temp = prepareTemporaryTestFile("abc\n");
		Assert.assertEquals(1, temp.getLineCount());
		Assert.assertEquals("abc", temp.getLine(0));
	}
	
	@Test
	public void testFourLineFileWithEndline() throws IOException {
		IndexedTextFile temp = prepareTemporaryTestFile("a\nb\nc\nd\n");
		Assert.assertEquals(4, temp.getLineCount());
		Assert.assertEquals("a", temp.getLine(0));
		Assert.assertEquals("b", temp.getLine(1));
		Assert.assertEquals("c", temp.getLine(2));
		Assert.assertEquals("d", temp.getLine(3));
	}

	@Test
	public void testFiveLineFile() throws IOException {
		IndexedTextFile temp = prepareTemporaryTestFile("a\nbb\nc\nddd\ne");
		Assert.assertEquals(5, temp.getLineCount());
		Assert.assertEquals("a", temp.getLine(0));
		Assert.assertEquals("bb", temp.getLine(1));
		Assert.assertEquals("c", temp.getLine(2));
		Assert.assertEquals("ddd", temp.getLine(3));
		Assert.assertEquals("e", temp.getLine(4));
	}

	@Test
	public void testFiveLineFileWithNewline() throws IOException {
		IndexedTextFile temp = prepareTemporaryTestFile("a\nbb\nc\nddd\ne\n");
		Assert.assertEquals(5, temp.getLineCount());
		Assert.assertEquals("a", temp.getLine(0));
		Assert.assertEquals("bb", temp.getLine(1));
		Assert.assertEquals("c", temp.getLine(2));
		Assert.assertEquals("ddd", temp.getLine(3));
		Assert.assertEquals("e", temp.getLine(4));
	}

	@Test
	public void testSixLineFile() throws IOException {
		IndexedTextFile temp = prepareTemporaryTestFile("a\nbb\nc\nddd\ne\nffff");
		Assert.assertEquals(6, temp.getLineCount());
		Assert.assertEquals("a", temp.getLine(0));
		Assert.assertEquals("bb", temp.getLine(1));
		Assert.assertEquals("c", temp.getLine(2));
		Assert.assertEquals("ddd", temp.getLine(3));
		Assert.assertEquals("e", temp.getLine(4));
		Assert.assertEquals("ffff", temp.getLine(5));
	}

	@Test
	public void testSixLineFileWithNewline() throws IOException {
		IndexedTextFile temp = prepareTemporaryTestFile("a\nbb\nc\nddd\ne\nffff\n");
		Assert.assertEquals(6, temp.getLineCount());
		Assert.assertEquals("a", temp.getLine(0));
		Assert.assertEquals("bb", temp.getLine(1));
		Assert.assertEquals("c", temp.getLine(2));
		Assert.assertEquals("ddd", temp.getLine(3));
		Assert.assertEquals("e", temp.getLine(4));
		Assert.assertEquals("ffff", temp.getLine(5));
	}

}

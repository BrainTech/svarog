package org.signalml.util;

import static org.junit.Assert.*;
import org.junit.Test;
import org.signalml.BaseTestCase;
import static org.signalml.util.Util.hasSpecialChars;

public class TestUtil_hasSpecialChars extends BaseTestCase {
	@Test public void test_text_with_spaces() {
		assertFalse(hasSpecialChars("goo goo goo"));
	}

	@Test public void test_text_with_lf() {
		assertTrue(hasSpecialChars("goo\ngoo"));
	}

	final static String[] opisy_jarka = {
		"Low-pass 30Hz; Fs=1024Hz; 12dB/octave",
		"Low-pass 40Hz; Fs=1024Hz; 12dB/octave",
		"high-pass 0.1 Hz; Fs=1024Hz; 6dB/octave",
		"high-pass 0.2 Hz; Fs=1024Hz; 6dB/octave",
	};

	@Test public void test_opisy_jarka() {
		for (String s: opisy_jarka)
			assertFalse(s, hasSpecialChars(s));
		// TODO: convert to parametric tests when junit is changed to something better
	}

	@Test public void test_combining_in_front() {
		assertTrue("combining grave accent", hasSpecialChars("\u0300"));
	}

	@Test public void test_combining_at_end() {
		assertFalse("a + combining grave accent", hasSpecialChars("a\u0300"));
	}

	@Test public void test_surrogate() {
		assertTrue("surrogate", hasSpecialChars("\uD800"));
	}
}

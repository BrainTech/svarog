package org.signalml.util;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.signalml.util.Util.validateString;

public class TestUtil_validateString {
	@Test public void test_text_with_spaces() {
		assertTrue(validateString("goo goo goo"));
	}

	@Test public void test_text_with_lf() {
		assertFalse(validateString("goo\ngoo"));
	}

	final static String[] opisy_jarka = {
		"Low-pass 30Hz; Fs=1024Hz; 12dB/octave",
		"Low-pass 40Hz; Fs=1024Hz; 12dB/octave",
		"high-pass 0.1 Hz; Fs=1024Hz; 6dB/octave",
		"high-pass 0.2 Hz; Fs=1024Hz; 6dB/octave",
	};

	@Test public void test_opisy_jarka() {
		for(String s: opisy_jarka)
			assertTrue(s, validateString(s));
		// TODO: convert to parametric tests when junit is changed to something better
	}

	@Test public void test_combining_in_front() {
		assertFalse("combining grave accent", validateString("\u0300"));
	}

	@Test public void test_combining_at_end() {
		assertTrue("a + combining grave accent", validateString("a\u0300"));
	}

	@Test public void test_surrogate() {
		assertFalse("surrogate", validateString("\uD800"));
	}
}

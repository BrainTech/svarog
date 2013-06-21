package org.signalml.plugin.newstager.mock;

import java.io.File;
import java.io.IOException;

import org.signalml.app.document.TagDocument;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

public class NewStagerTagImporter {

	//private static String path = "../../../../book_20sec_a1.3_smp_delta.tag";
	private static String path = "E:/book_20sec_a1.3_smp_spindles.tag";
	//private static String path = "C:/sv/stager_files/book_20sec_a1.3_smp_PrimHypnos_a65.00.tag";
	//private static String path = "E:/book_20sec_a1.3_smp_PrimHypnos_a65.00.tag";
	//private static String path = "C:/Users/kdr/book_20sec_a1.3_smp_PrimHypnos_a65.00.tag";
	//private static String path = "E:/book_20sec_a1.3_smp_hypnos_a65.00.tag";
	private static float samplingFrequency = 128.0f;

	public static void main(String[] args) {
		LegacyTagImporter importer = new LegacyTagImporter();
		StyledTagSet tags;

		File sourceFile = new File(path);
		try {
			tags = importer.importLegacyTags(sourceFile, samplingFrequency);
		} catch (SignalMLException e) {
			e.printStackTrace();
			return;
		}

		File outputFile = new File(sourceFile.getParent(), Util.getFileNameWithoutExtension(sourceFile) + "conv.xml");

		TagDocument d;
		try {
			d = new TagDocument(tags);
		} catch (SignalMLException e) {
			e.printStackTrace();
			return;
		}

		d.setBackingFile(outputFile);
		try {
			d.saveDocument();
			d.closeDocument();
		} catch (SignalMLException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}

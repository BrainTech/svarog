/* XMLUtils.java created 2007-09-14
 *
 */
package org.signalml.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.signalml.app.config.preset.BookFilterPresetManager;
import org.signalml.app.config.preset.FFTSampleFilterPresetManager;
import org.signalml.app.config.preset.TimeDomainSampleFilterPresetManager;
import org.signalml.app.config.preset.SignalExportPresetManager;
import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.app.montage.MontagePresetManager;
import org.signalml.domain.book.filter.AbstractAtomFilter;
import org.signalml.domain.book.filter.AtomFilterChain;
import org.signalml.domain.book.filter.DelegatingAtomFilter;
import org.signalml.domain.book.filter.ParameterRangeAtomFilter;
import org.signalml.domain.book.filter.TagBasedAtomFilter;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageChannel;
import org.signalml.domain.montage.MontageSampleFilter;
import org.signalml.domain.montage.RawMontageGenerator;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.domain.montage.eeg.LeftEarMontageGenerator;
import org.signalml.domain.montage.eeg.LinkedEarsMontageGenerator;
import org.signalml.domain.montage.eeg.RightEarMontageGenerator;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.util.Util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.NativeFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.signalml.app.config.preset.PredefinedFiltersConfiguration;
import org.signalml.app.config.preset.PredefinedTimeDomainFiltersPresetManager;

/** XMLUtils
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class XMLUtils {

	public static XStream getDefaultStreamer() {
		return new XStream(new PureJavaReflectionProvider(new FieldDictionary(new NativeFieldKeySorter())), new DomDriver("UTF-8"));
	}

	public static void configureStreamerForMontage(XStream streamer) {
		Annotations.configureAliases(
				streamer,
				EegChannel.class,
				MontagePresetManager.class,
				SourceChannel.class,
				MontageChannel.class,
				SourceMontage.class,
				Montage.class,
				RawMontageGenerator.class,
				LeftEarMontageGenerator.class,
				RightEarMontageGenerator.class,
				LinkedEarsMontageGenerator.class,
				MontageSampleFilter.class,
				SampleFilterDefinition.class,
				FFTSampleFilter.class,
				TimeDomainSampleFilter.class
		);		
	}

	public static void configureStreamerForBookFilter(XStream streamer) {
		Annotations.configureAliases(
		        streamer,
		        BookFilterPresetManager.class,
		        AtomFilterChain.class,
		        AbstractAtomFilter.class,
		        ParameterRangeAtomFilter.class,
		        TagBasedAtomFilter.class,
		        DelegatingAtomFilter.class
		);
	}

	public static void configureStreamerForSignalExport(XStream streamer) {
		Annotations.configureAliases(
		        streamer,
		        SignalExportPresetManager.class,
		        SignalExportDescriptor.class
		);
	}

	public static void configureStreamerForFFTSampleFilter(XStream streamer) {
		Annotations.configureAliases(
		        streamer,
		        FFTSampleFilterPresetManager.class,
		        FFTSampleFilter.class
		);
	}

	/**
	 * Configures the given {@link XStream XStreamer} for {@link TimeDomainSampleFilter}
	 * streaming.
	 * @param streamer a streamer to be configured
	 */
	public static void configureStreamerForTimeDomainSampleFilter(XStream streamer) {
		Annotations.configureAliases(
				streamer,
				TimeDomainSampleFilterPresetManager.class,
				TimeDomainSampleFilter.class
		);
	}

	/**
	 * Configures the given {@link XStream XStreamer} for
	 * {@link PredefinedTimeDomainFiltersPresetManager}.
	 * @param streamer a streamer to be configured
	 */
	public static void configureStreamerForPredefinedTimeDomainSampleFilter(XStream streamer) {
		Annotations.configureAliases(
				streamer,
				PredefinedTimeDomainFiltersPresetManager.class,
				PredefinedFiltersConfiguration.class,
				TimeDomainSampleFilter.class
		);
	}

	public static OutputStream getInitializedXMLOutputStream(File f) throws IOException {
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(f));
		try {
			writeXMLHeader(outputStream);
		} catch (IOException ex) {
			outputStream.close();
			throw ex;
		}
		return outputStream;
	}

	public static InputStream getInitializedXMLInputStream(File f) throws IOException {
		return new BufferedInputStream(new FileInputStream(f));
	}

	public static void objectToFile(Object o, File f, XStream streamer) throws IOException {

		OutputStream outputStream = XMLUtils.getInitializedXMLOutputStream(f);

		try {
			streamer.toXML(o,outputStream);
		} finally {
			outputStream.close();
		}

	}

	public static void objectFromFile(Object o, File f, XStream streamer) throws IOException {

		InputStream inputStream = XMLUtils.getInitializedXMLInputStream(f);

		try {
			streamer.fromXML(inputStream,o);
		} finally {
			inputStream.close();
		}

	}

	/**
	 * Reads an object from an input stream.
	 * @param o the read object (after calling this method, this object
	 * should be equal to the one read from the input stream)
	 * @param inputStream an input stream from which the object should be read.
	 * It is closed after all data is read.
	 * @param streamer the streamer which should be used to read the object
	 * @throws IOException if an I/O error occurs while closing the input stream
	 */
	public static void objectFromInputStream(Object o, InputStream inputStream, XStream streamer) throws IOException {
		try {
			streamer.fromXML(inputStream, o);
		} finally {
			inputStream.close();
		}
	}

	public static Object newObjectFromFile(File f, XStream streamer) throws IOException {

		InputStream inputStream = XMLUtils.getInitializedXMLInputStream(f);

		try {
			return streamer.fromXML(inputStream);
		} finally {
			inputStream.close();
		}

	}

	/**
	 * Writes a standard XML header to the given output stream.
	 * @param outputStream the stream to which the XML header will be written
	 * @throws IOException thrown if an IO error occurs
	 */
	public static void writeXMLHeader(OutputStream outputStream) throws IOException {
		outputStream.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+Util.LINE_SEP).getBytes());
	}

}

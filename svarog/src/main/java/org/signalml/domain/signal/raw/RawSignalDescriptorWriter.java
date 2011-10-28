/* RawSignalDescriptorWriter.java created 2008-01-18
 *
 */

package org.signalml.domain.signal.raw;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.signalml.domain.signal.raw.RawSignalDescriptor.SourceSignalType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is responsible for writing the
 * {@link RawSignalDescriptor description} of a raw signal to the XML file.
 * TODO add link to example file (need to put it somewhere it the web).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalDescriptorWriter {

        /**
         * Creates the document with the description of the signal based on the
         * given {@link RawSignalDescriptor description}
         * @param descriptor the description of the raw signal
         * @return the created document
         */
	public Document getDocument(RawSignalDescriptor descriptor) {

		Document document = RawSignalDocumentBuilder.getInstance().newDocument();

		Element root = document.createElementNS(RawSignalDocumentBuilder.NAMESPACE_URI, RawSignalDocumentBuilder.RAW_SIGNAL);
		document.appendChild(root);

		Element element;

		element = document.createElement(RawSignalDocumentBuilder.EXPORT_FILE_NAME);
		element.setTextContent(descriptor.getExportFileName());
		root.appendChild(element);

		String sourceFileName = descriptor.getSourceFileName();
		if (sourceFileName != null && !sourceFileName.isEmpty()) {
			element = document.createElement(RawSignalDocumentBuilder.SOURCE_FILE_NAME);
			element.setTextContent(sourceFileName);
			root.appendChild(element);
		}

		Element sourceFileFormatEl = document.createElement(RawSignalDocumentBuilder.SOURCE_FILE_FORMAT);

		SourceSignalType sourceSignalType = descriptor.getSourceSignalType();
		switch (sourceSignalType) {

		case SIGNALML :

			Element signalMLSignalEl = document.createElement(RawSignalDocumentBuilder.SIGNALML_SIGNAL_INFO);

			element = document.createElement(RawSignalDocumentBuilder.FORMAT);
			element.setTextContent(descriptor.getSourceSignalMLFormat());
			signalMLSignalEl.appendChild(element);

			element = document.createElement(RawSignalDocumentBuilder.SOURCE_UID);
			element.setTextContent(descriptor.getSourceSignalMLSourceUID());
			signalMLSignalEl.appendChild(element);

			sourceFileFormatEl.appendChild(signalMLSignalEl);

			break;

		case RAW :
		default :

			sourceFileFormatEl.appendChild(document.createElement(RawSignalDocumentBuilder.RAW_SIGNAL_INFO));

			break;

		}

		root.appendChild(sourceFileFormatEl);

		element = document.createElement(RawSignalDocumentBuilder.SAMPLING_FREQUENCY);
		element.setTextContent(Float.toString(descriptor.getSamplingFrequency()));
		root.appendChild(element);

		element = document.createElement(RawSignalDocumentBuilder.CHANNEL_COUNT);
		element.setTextContent(Integer.toString(descriptor.getChannelCount()));
		root.appendChild(element);

		element = document.createElement(RawSignalDocumentBuilder.SAMPLE_COUNT);
		element.setTextContent(Integer.toString(descriptor.getSampleCount()));
		root.appendChild(element);

		element = document.createElement(RawSignalDocumentBuilder.SAMPLE_TYPE);
		element.setTextContent(descriptor.getSampleType().name());
		root.appendChild(element);

		element = document.createElement(RawSignalDocumentBuilder.BYTE_ORDER);
		element.setTextContent(descriptor.getByteOrder().name());
		root.appendChild(element);

		float pageSize = descriptor.getPageSize();
		if (pageSize > 0) {

			element = document.createElement(RawSignalDocumentBuilder.PAGE_SIZE);
			element.setTextContent(Float.toString(pageSize));
			root.appendChild(element);

		}

		int blocksPerPage = descriptor.getBlocksPerPage();
		if (blocksPerPage > 0) {

			element = document.createElement(RawSignalDocumentBuilder.BLOCKS_PER_PAGE);
			element.setTextContent(Integer.toString(blocksPerPage));
			root.appendChild(element);

		}

		String eegSystemName = descriptor.getEegSystemName();
		if (eegSystemName != null) {
			element = document.createElement(RawSignalDocumentBuilder.EEG_SYSTEM_NAME);
			element.setTextContent(eegSystemName);
			root.appendChild(element);
		}

		String[] channelLabels = descriptor.getChannelLabels();
		if (channelLabels != null && channelLabels.length > 0) {

			Element channelLabelsEl = document.createElement(RawSignalDocumentBuilder.CHANNEL_LABELS);

			for (int i=0; i<channelLabels.length; i++) {

				element = document.createElement(RawSignalDocumentBuilder.LABEL);
				element.setTextContent(channelLabels[i]);
				channelLabelsEl.appendChild(element);

			}

			root.appendChild(channelLabelsEl);

		}

		double markerOffset = descriptor.getMarkerOffset();
		if (markerOffset > 0) {

			element = document.createElement(RawSignalDocumentBuilder.MARKER_OFFSET);
			element.setTextContent(Double.toString(markerOffset));
			root.appendChild(element);

		}

		Date exportDate = descriptor.getExportDate();
		if (exportDate != null) {

			Calendar cal = Calendar.getInstance();
			cal.setTime(exportDate);

			Formatter formatter = new Formatter();

			formatter.format("%04d-%02d-%02dT%02d:%02d:%02d",
			                 cal.get(Calendar.YEAR),
			                 cal.get(Calendar.MONTH) + 1,
			                 cal.get(Calendar.DAY_OF_MONTH),
			                 cal.get(Calendar.HOUR_OF_DAY),
			                 cal.get(Calendar.MINUTE),
			                 cal.get(Calendar.SECOND)
			                );

			element = document.createElement(RawSignalDocumentBuilder.EXPORT_DATE);
			element.setTextContent(formatter.toString());
			root.appendChild(element);

		}

		float[] gain = descriptor.getCalibrationGain();
		if (gain != null && gain.length > 0) {

			Element gainElems = document.createElement(RawSignalDocumentBuilder.CALIBRATION_GAIN);

			for (int i=0; i<gain.length; i++) {

				element = document.createElement(RawSignalDocumentBuilder.CALIBRATION_PARAM);
				element.setTextContent(Float.toString(gain[i]));
				gainElems.appendChild(element);

			}

			root.appendChild(gainElems);

		}

		float[] offset = descriptor.getCalibrationOffset();
		if (offset != null && offset.length > 0) {

			Element offsetElems = document.createElement(RawSignalDocumentBuilder.CALIBRATION_OFFSET);

			for (int i=0; i<offset.length; i++) {

				element = document.createElement(RawSignalDocumentBuilder.CALIBRATION_PARAM);
				element.setTextContent(Float.toString( offset[i]));
				offsetElems.appendChild(element);

			}

			root.appendChild(offsetElems);

		}

		element = document.createElement(RawSignalDocumentBuilder.FIRST_SAMPLE_TIMESTAMP);
		double firstSampleTimestamp = descriptor.getFirstSampleTimestamp();
		if (!Double.isNaN(firstSampleTimestamp)) {
			element.setTextContent(Double.toString(descriptor.getFirstSampleTimestamp()));
			root.appendChild(element);
		}

                element = document.createElement(RawSignalDocumentBuilder.IS_BACKUP);
                element.setTextContent(descriptor.isBackup() ? "1" : "0");
                root.appendChild(element);

		return document;

	}

        /**
         * Writes the given {@link RawSignalDescriptor description} to
         * the given file
         * @param descriptor the description of the raw signal
         * @param file the file to which the description will be written
         * @throws IOException if error while creating the file or while
         * writing the xml to it
         */
	public void writeDocument(RawSignalDescriptor descriptor, File file) throws IOException {

		Document document = getDocument(descriptor);

		FileOutputStream fileOutputStream = null;
		try {

			Source source = new DOMSource(document);
			fileOutputStream = new FileOutputStream(file);
			Result result = new StreamResult(new OutputStreamWriter(fileOutputStream, "utf-8"));
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setAttribute("indent-number", new Integer(2));
			Transformer xformer = factory.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, result);

		} catch (TransformerConfigurationException ex) {
			throw new IOException("Failed to write xml", ex);
		} catch (TransformerException ex) {
			throw new IOException("Failed to write xml", ex);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException ex) {
					// ignore
				}
			}
		}

	}

}

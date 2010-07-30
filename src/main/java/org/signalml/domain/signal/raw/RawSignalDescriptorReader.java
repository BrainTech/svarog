/* RawSignalDescriptorReader.java created 2008-01-28
 *
 */

package org.signalml.domain.signal.raw;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.signalml.app.util.SingleNameSpaceContext;
import org.signalml.domain.signal.raw.RawSignalDescriptor.SourceSignalType;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class is responsible for reading the
 * {@link RawSignalDescriptor description} of a raw signal from file.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalDescriptorReader {

	protected static final Logger logger = Logger.getLogger(RawSignalDescriptorReader.class);

        /**
         * Converts the given document element to the
         * {@link RawSignalDescriptor description} of a raw signal
         * @param rawSignalEl the document element to be converted
         * @return the created description of a raw signal
         * @throws SignalMLException if the element doesn't contain a valid
         * description of a raw signal
         */
	public RawSignalDescriptor getDescriptor(Element rawSignalEl) throws SignalMLException {

		RawSignalDescriptor descriptor = new RawSignalDescriptor();

		XPathFactory pathFactory = XPathFactory.newInstance();
		XPath path = pathFactory.newXPath();
		path.setNamespaceContext(new SingleNameSpaceContext("rs", RawSignalDocumentBuilder.NAMESPACE_URI));

		try {

			String exportFileName = path.evaluate(RawSignalDocumentBuilder.EXPORT_FILE_NAME, rawSignalEl);
			descriptor.setExportFileName(exportFileName);

			String sourceFileName = path.evaluate(RawSignalDocumentBuilder.SOURCE_FILE_NAME, rawSignalEl);
			if (sourceFileName != null && !sourceFileName.isEmpty()) {
				descriptor.setSourceFileName(sourceFileName);
			}

			boolean formatEstablished = false;

			Element sourceFileFormatEl = (Element) path.evaluate(RawSignalDocumentBuilder.SOURCE_FILE_FORMAT, rawSignalEl, XPathConstants.NODE);
			if (sourceFileFormatEl != null) {

				Element signalMLSignalInfoEl = (Element) path.evaluate(RawSignalDocumentBuilder.SIGNALML_SIGNAL_INFO, sourceFileFormatEl, XPathConstants.NODE);
				if (signalMLSignalInfoEl != null) {

					descriptor.setSourceSignalType(SourceSignalType.SIGNALML);
					descriptor.setSourceSignalMLFormat(path.evaluate(RawSignalDocumentBuilder.FORMAT, signalMLSignalInfoEl));
					descriptor.setSourceSignalMLSourceUID(path.evaluate(RawSignalDocumentBuilder.SOURCE_UID, signalMLSignalInfoEl));
					formatEstablished = true;

				}

			}

			if (!formatEstablished) {
				descriptor.setSourceSignalType(SourceSignalType.RAW);
			}

			descriptor.setSamplingFrequency(Float.parseFloat(path.evaluate(RawSignalDocumentBuilder.SAMPLING_FREQUENCY, rawSignalEl)));

			int channelCount = Integer.parseInt(path.evaluate(RawSignalDocumentBuilder.CHANNEL_COUNT, rawSignalEl));
			descriptor.setChannelCount(channelCount);

			descriptor.setSampleCount(Integer.parseInt(path.evaluate(RawSignalDocumentBuilder.SAMPLE_COUNT, rawSignalEl)));

			String calibration = path.evaluate(RawSignalDocumentBuilder.CALIBRATION, rawSignalEl);
			if (calibration != null && !calibration.isEmpty()) {
				descriptor.setCalibration(Float.parseFloat(calibration));
			} else {
				descriptor.setCalibration(1F);
			}

			descriptor.setSampleType(RawSignalSampleType.valueOf(path.evaluate(RawSignalDocumentBuilder.SAMPLE_TYPE, rawSignalEl)));
			descriptor.setByteOrder(RawSignalByteOrder.valueOf(path.evaluate(RawSignalDocumentBuilder.BYTE_ORDER, rawSignalEl)));

			String pageSize = path.evaluate(RawSignalDocumentBuilder.PAGE_SIZE, rawSignalEl);
			if (pageSize != null && !pageSize.isEmpty()) {
				descriptor.setPageSize(Float.parseFloat(pageSize));
			}

			String blocksPerPage = path.evaluate(RawSignalDocumentBuilder.BLOCKS_PER_PAGE, rawSignalEl);
			if (blocksPerPage != null && !blocksPerPage.isEmpty()) {
				descriptor.setBlocksPerPage(Integer.parseInt(blocksPerPage));
			}

			Element channelLabelsEl = (Element) path.evaluate(RawSignalDocumentBuilder.CHANNEL_LABELS, rawSignalEl, XPathConstants.NODE);
			if (channelLabelsEl != null && channelLabelsEl.hasChildNodes()) {

				NodeList labelList = (NodeList) path.evaluate(RawSignalDocumentBuilder.LABEL, channelLabelsEl, XPathConstants.NODESET);
				if (labelList != null) {

					int labelCnt = labelList.getLength();
					if (labelCnt > 0) {

						String[] labels = new String[channelCount];

						for (int i=0; i<channelCount; i++) {

							if (i < labelCnt) {
								labels[i] = labelList.item(i).getTextContent();
							} else {
								labels[i] = "L" + (i+1);
							}

						}

						descriptor.setChannelLabels(labels);

					}
				}

			}

			String markerOffset = path.evaluate(RawSignalDocumentBuilder.MARKER_OFFSET, rawSignalEl);
			if (markerOffset != null && !markerOffset.isEmpty()) {
				descriptor.setMarkerOffset(Double.parseDouble(markerOffset));
			}

			String exportDate = path.evaluate(RawSignalDocumentBuilder.EXPORT_DATE, rawSignalEl);
			if (exportDate != null && !exportDate.isEmpty()) {

				try {
					descriptor.setExportDate(Util.parseTime(exportDate));
				} catch (ParseException ex) {
					logger.error("Bad export date", ex);
					throw new SignalMLException("error.invalidRawSignalXML");
				}

			}

		} catch (XPathExpressionException ex) {
			throw new SignalMLException("error.invalidRawSignalXML", ex);
		}

		return descriptor;

	}

        /**
         * Reads the {@link RawSignalDescriptor description} of a raw signal
         * from the given file
         * @param file the file with the description of a raw signal
         * @return the created description
         * @throws IOException if the file is not a valid xml file or some
         * other error while reading file
         * @throws SignalMLException if the file doesn't contain a valid
         * description of a raw signal
         */
	public RawSignalDescriptor readDocument(File file) throws IOException, SignalMLException {

		try {
			Document document = RawSignalDocumentBuilder.getInstance().parse(file);

			return getDescriptor(document.getDocumentElement());

		} catch (SAXException ex) {
			throw new SignalMLException("error.invalidRawSignalXML", ex);
		}

	}

}

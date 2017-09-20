/* RawSignalDocument.java created 2008-01-28
 *
 */

package org.signalml.app.document.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.List;

import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.video.OfflineVideoFrame;
import org.signalml.codec.SignalMLCodec;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleSource;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.plugin.export.SignalMLException;

/**
 * The document with the signal stored in the raw form (without
 * a {@link SignalMLCodec}).
 * This is a simple subclass derived from BaseSignalDocument,
 * designed to be used with {@link RawSignalSampleSource}.
 * It also includes some additional data (e.g. video) specific for RAW signals.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalDocument extends BaseSignalDocument {

	/**
	 * optional video preview for this signal
	 */
	private OfflineVideoFrame videoFrame;

	/**
	 * time offset (in seconds) of video relative to the signal's start
	 */
	private float videoOffset;

	public RawSignalDocument(RawSignalDescriptor descriptor) {
		super(descriptor);
	}

	@Override
	public void closeDocument() throws SignalMLException {
		if (videoFrame != null) {
			videoFrame.dispose();
		}
		super.closeDocument();
	}

	@Override
	protected BaseSignalSampleSource createSampleSource() throws IOException {
		return new RawSignalSampleSource(
			getBackingFile().getAbsoluteFile(),
			getDescriptor().getChannelCount(),
			getDescriptor().getSamplingFrequency(),
			getDescriptor().getSampleType(),
			getDescriptor().getByteOrder()
		);
	}

	@Override
	public String getFormatName() {
		return "InternalInterleavedRAW";
	}

	/**
	 * Returns the {@link RawSignalSampleType type} of samples.
	 *
	 * @return the type of samples
	 */
	public RawSignalSampleType getSampleType() {
		return getDescriptor().getSampleType();
	}

	/**
	 * Returns the {@link RawSignalByteOrder order} of bytes in the file with
	 * the signal.
	 *
	 * @return the order of bytes in the file with the signal.
	 */
	public RawSignalByteOrder getByteOrder() {
		return getDescriptor().getByteOrder();
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(new LabelledPropertyDescriptor(_("sample type"), "sampleType", RawSignalDocument.class, "getSampleType", null));
		list.add(new LabelledPropertyDescriptor(_("byte order"), "byteOrder", RawSignalDocument.class, "getByteOrder", null));

		return list;

	}

	/**
	 * Get video preview window assigned to this signal.
	 * If no such window exists, return null.
	 *
	 * @return  currently assigned video window
	 */
	public OfflineVideoFrame getVideoFrame() {
		return videoFrame;
	}

	/**
	 * Get time offset of video file, relative to the signal's start.
	 *
	 * @return  time offset (in seconds) of video file
	 */
	public float getVideoOffset() {
		return videoOffset;
	}

	/**
	 * Assign given video frame to be managed by this signal document.
	 *
	 * @param frame  created video frame
	 * @param offset  time offset (in seconds) of video relative to the signal's start
	 */
	public void setVideoFrame(OfflineVideoFrame frame, float offset) {
		this.videoFrame = frame;
		this.videoOffset = offset;
	}

}

package org.signalml.codec.precompiled;

import org.signalml.codec.SMLCodec;
import org.signalml.codec.generator.xml.XMLCodecException;

public class EDF extends SMLCodec {

	// ---- BEGIN USER CODE ---



	// ---- END USER CODE   ---

	public String getFormatID() {
		return "EDF";
	}

	public String getFormatDescription() {
		return "EDF data format";
	}

	public EDF() throws XMLCodecException {
		super();
	}

	public void open(String name) throws XMLCodecException {
		super.open(name);
		if (!getConstraints()) {
			throw new XMLCodecException("CONSTRAINTS ERROR !");
		}
	}
	public boolean is_number_of_channels() {
		return true;
	}

	public int get_number_of_channels() throws XMLCodecException {
		String theResult=read_String(252, 4);
		return to_int(theResult);
	}

	public boolean is_sampling_frequency() {
		return true;
	}

	public float[] get_sampling_frequency() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		float theResult[]=new float[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=get_nr_of_samples()[index-1]/get_duration_of_data_record();
		}
		return theResult;
	}

	public boolean is_calibration() {
		return false;
	}

	public boolean is_channel_names() {
		return false;
	}


	public String get_version() throws XMLCodecException {
		String theResult=read_String(0, 8);
		return theResult;
	}

	public String get_patient_ident() throws XMLCodecException {
		String theResult=read_String(8, 80);
		return theResult;
	}

	public String get_record_ident() throws XMLCodecException {
		String theResult=read_String(88, 80);
		return theResult;
	}

	public String get_start_date() throws XMLCodecException {
		String theResult=read_String(168, 8);
		return theResult;
	}

	public String get_start_time() throws XMLCodecException {
		String theResult=read_String(176, 8);
		return theResult;
	}

	public int get_header_size() throws XMLCodecException {
		String theResult=read_String(184, 8);
		return to_int(theResult);
	}

	public String get_reserved() throws XMLCodecException {
		String theResult=read_String(192, 44);
		return theResult;
	}

	public int get_number_of_data_records() throws XMLCodecException {
		String theResult=read_String(236, 8);
		return to_int(theResult);
	}

	public float get_duration_of_data_record() throws XMLCodecException {
		String theResult=read_String(244, 8);
		return to_float(theResult);
	}

	public String[] get_label() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+16*index, 16);
		}
		return theResult;
	}

	
	public String[] get_transducer_type() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+16*get_number_of_channels()+80*(index-1), 80);
		}
		return theResult;
	}

	public String[] get_physical_dimension() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+96*get_number_of_channels()+8*(index-1), 8);
		}
		return theResult;
	}

	public String[] get_physical_minimum() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+104*get_number_of_channels()+8*(index-1), 8);
		}
		return theResult;
	}

	public String[] get_physical_maximum() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+112*get_number_of_channels()+8*(index-1), 8);
		}
		return theResult;
	}

	public String[] get_digital_minimum() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+120*get_number_of_channels()+8*(index-1), 8);
		}
		return theResult;
	}

	public String[] get_digital_maximum() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+128*get_number_of_channels()+8*(index-1), 8);
		}
		return theResult;
	}

	public String[] get_prefiltering() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+136*get_number_of_channels()+80*(index-1), 80);
		}
		return theResult;
	}

	public int[] get_nr_of_samples() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+216*get_number_of_channels()+8*(index-1), 8);
		}
		return to_int(theResult);
	}

	public String[] get_reserved2() throws XMLCodecException {
		int length=(get_number_of_channels())-(1)+1;
		String theResult[]=new String[length];
		for (int j=0, index=(1) ; index<=length ; index++, j++) {
			theResult[j]=read_String(256+224*get_number_of_channels()+32*(index-1), 32);
		}
		return theResult;
	}


	private boolean getConstraints() throws XMLCodecException {
		return true;
	}

	public float[] getSample(long offset) throws XMLCodecException {
		return null;
	}

	private boolean m_edf_init=false;
	private int m_max_offset;
	public void init() throws XMLCodecException {
		if (!m_edf_init) {
			init_edf(get_number_of_channels(), get_nr_of_samples());
			m_edf_init=true;
		}
		m_max_offset=get_number_of_data_records()*getMaxRecordSamples();
		if (m_max_offset<0) m_max_offset=4*1024;
	}

	public int get_max_offset() throws XMLCodecException {
		return m_max_offset;
	}


	public float getChannelSample(long offset, int chn) throws XMLCodecException {
		return getEDFChannelSample(offset, chn);
	}
}

package org.signalml.domain.signal.export.edf.writer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/*
*****************************************************************************
*
* Copyright (c) 2020 Teunis van Beelen
* All rights reserved.
*
* Email: teuniz@protonmail.com
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the copyright holder nor the names of its
*       contributors may be used to endorse or promote products derived from
*       this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ''AS IS'' AND ANY
* EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*****************************************************************************
 */
/**
 * EDF header. <br>
 * <br>
 * <pre>
 * offset (hex, dec) length
 * ---------------------------------------------------------------------
 * 0x00      0     8 ascii : version of this data format (0)
 * 0x08      8    80 ascii : local patient identification
 * 0x58     88    80 ascii : local recording identification
 * 0xA8    168     8 ascii : startdate of recording (dd.mm.yy)
 * 0xB0    176     8 ascii : starttime of recording (hh.mm.ss)
 * 0xB8    184     8 ascii : number of bytes in header record
 * 0xC0    192    44 ascii : reserved
 * 0xEC    236     8 ascii : number of data records (-1 if unknown)
 * 0xF4    244     8 ascii : duration of a data record, in seconds
 * 0xFC    252     4 ascii : number of signals
 *
 *
 *
 *      0x00           0     ns * 16 ascii : ns * label (e.g. EEG Fpz-Cz or Body temp)
 * ns * 0x10    ns *  16     ns * 80 ascii : ns * transducer type (e.g. AgAgCl electrode)
 * ns * 0x60    ns *  96     ns *  8 ascii : ns * physical dimension (e.g. uV or degreeC)
 * ns * 0x68    ns * 104     ns *  8 ascii : ns * physical minimum (e.g. -500 or 34)
 * ns * 0x70    ns * 112     ns *  8 ascii : ns * physical maximum (e.g. 500 or 40)
 * ns * 0x78    ns * 120     ns *  8 ascii : ns * digital minimum (e.g. -2048)
 * ns * 0x80    ns * 128     ns *  8 ascii : ns * digital maximum (e.g. 2047)
 * ns * 0x88    ns * 136     ns * 80 ascii : ns * prefiltering (e.g. HP:0.1Hz LP:75Hz N:60)
 * ns * 0xD8    ns * 216     ns *  8 ascii : ns * nr of samples in each data record
 * ns * 0xE0    ns * 224     ns * 32 ascii : ns * reserved
 * </pre>
 * <br>
 * ns: number of signals<br>
 * <br>
 * All fields are left aligned and filled up with spaces, no NULL's.<br>
 * <br>
 * Only printable ASCII characters are allowed.<br>
 * <br>
 * Decimal separator (if any) must be a dot. No grouping characters in
 * numbers.<br>
 * <br>
 * <br>
 * For more info about the EDF and EDF+ format, visit:
 * <a href="https://edfplus.info/specs/">https://edfplus.info/specs/</a><br>
 * <br>
 * For more info about the BDF and BDF+ format, visit:
 * <a href="https://www.teuniz.net/edfbrowser/bdfplus%20format%20description.html">https://www.teuniz.net/edfbrowser/bdfplus%20format%20description.html</a><br>
 * <br>
 * <br>
 * note: In EDF, the sensitivity (e.g. uV/bit) and offset are stored using four
 * parameters:<br>
 * digital maximum and minimum, and physical maximum and minimum.<br>
 * Here, digital means the raw data coming from a sensor or ADC. Physical means
 * the units like uV.<br>
 * The sensitivity in units/bit is calculated as follows:<br>
 * <br>
 * units per bit = (physical max - physical min) / (digital max - digital
 * min)<br>
 * <br>
 * The digital offset is calculated as follows:<br>
 * <br>
 * offset = (physical max / units per bit) - digital max<br>
 * <br>
 * For a better explanation about the relation between digital data and physical
 * data,<br>
 * read the document "Coding Schemes Used with Data Converters" (PDF):<br>
 * <br>
 * <a href="https://www.ti.com/general/docs/lit/getliterature.tsp?baseLiteratureNumber=sbaa042">https://www.ti.com/general/docs/lit/getliterature.tsp?baseLiteratureNumber=sbaa042</a><br>
 * <br>
 * note: An EDF file usually contains multiple so-called datarecords. One
 * datarecord usually has a duration of one second (this is the default but it
 * is not mandatory!).<br>
 * In that case a file with a duration of five minutes contains 300 datarecords.
 * The duration of a datarecord can be freely choosen but, if possible, use
 * values from<br>
 * 0.1 to 1 second for easier handling. Just make sure that the total size of
 * one datarecord, expressed in bytes, does not exceed 10MByte (15MBytes for
 * BDF(+)).<br>
 * <br>
 * The RECOMMENDATION of a maximum datarecordsize of 61440 bytes in the EDF and
 * EDF+ specification was usefull in the time people were still using DOS as
 * their main operating system.<br>
 * Using DOS and fast (near) pointers (16-bit pointers), the maximum allocatable
 * block of memory was 64KByte.<br>
 * This is not a concern anymore so the maximum datarecord size now is limited
 * to 10MByte for EDF(+) and 15MByte for BDF(+). This helps to accommodate for
 * higher samplingrates<br>
 * used by modern Analog to Digital Converters.<br>
 * <br>
 * EDF header character encoding: The EDF specification says that only
 * (printable) ASCII characters are allowed.<br>
 * When writing the header info, EDFlib will assume you are using Latin1
 * encoding and it will automatically convert<br>
 * characters with accents, umlauts, tilde, etc. to their "normal" equivalent
 * without the accent/umlaut/tilde/etc.<br>
 * in order to create a valid EDF file.<br>
 * <br>
 * The description/name of an EDF+ annotation on the other hand, is encoded in
 * UTF-8.<br>
 * <br>
 *
 * @author Teunis van Beelen
 */
public class EDFwriter {

	public static final long EDFLIB_TIME_DIMENSION = 10000000L;
	public static final int EDFLIB_MAXSIGNALS = 640;

	/* the following defines are used in the member "filetype" of the edf_hdr_struct
	   and as return value for the function edfopen_file_readonly() */
	public static final int EDFLIB_FILETYPE_EDFPLUS = 0;
	public static final int EDFLIB_FILETYPE_BDFPLUS = 1;
	public static final int EDFLIB_NO_SUCH_FILE_OR_DIRECTORY = -2;

	/* when this error occurs, try to open the file with EDFbrowser,
	   it will give you full details about the cause of the error. */
	public static final int EDFLIB_FILE_CONTAINS_FORMAT_ERRORS = -3;

	public static final int EDFLIB_FILE_WRITE_ERROR = -8;
	public static final int EDFLIB_NUMBER_OF_SIGNALS_INVALID = -9;
	public static final int EDFLIB_INVALID_ARGUMENT = -12;
	public static final int EDFLIB_TOO_MANY_DATARECORDS = -13;

	/* the following defines are possible errors returned by the first sample write action */
	public static final int EDFLIB_NO_SIGNALS = -20;
	public static final int EDFLIB_TOO_MANY_SIGNALS = -21;
	public static final int EDFLIB_NO_SAMPLES_IN_RECORD = -22;
	public static final int EDFLIB_DIGMIN_IS_DIGMAX = -23;
	public static final int EDFLIB_DIGMAX_LOWER_THAN_DIGMIN = -24;
	public static final int EDFLIB_PHYSMIN_IS_PHYSMAX = -25;
	public static final int EDFLIB_DATARECORD_SIZE_TOO_BIG = -26;

	private final int EDFLIB_VERSION = 101;

	/* max size of annotationtext */
	private final int EDFLIB_WRITE_MAX_ANNOTATION_LEN = 40;

	/* bytes in datarecord for EDF annotations, must be an integer multiple of three and two */
	private final int EDFLIB_ANNOTATION_BYTES = 114;

	/* for writing only */
	private final int EDFLIB_MAX_ANNOTATION_CHANNELS = 64;

	private final int EDFLIB_ANNOT_MEMBLOCKSZ = 1000;

	/* signal parameters */
	private String[] param_label;
	private String[] param_transducer;
	private String[] param_physdimension;
	private double[] param_phys_min;
	private double[] param_phys_max;
	private int[] param_dig_min;
	private int[] param_dig_max;
	private String[] param_prefilter;
	private int[] param_smp_per_record;
	private String[] param_reserved;
	private double[] param_offset;
	private int[] param_buf_offset;
	private double[] param_bitvalue;

	private String path;
	private int filetype;
	private String version;
	private String plus_patientcode;
	private int plus_gender;
	private int plus_birthdate_year;
	private int plus_birthdate_month;
	private int plus_birthdate_day;
	private String plus_patient_name;
	private String plus_patient_additional;
	private String plus_admincode;
	private String plus_technician;
	private String plus_equipment;
	private String plus_recording_additional;
	private long l_starttime;
	private int startdate_day;
	private int startdate_month;
	private int startdate_year;
	private int starttime_second;
	private int starttime_minute;
	private int starttime_hour;
	private long starttime_offset;
	private int edfsignals;
	private long datarecords;
	private int recordsize;
	private int[] annot_ch;
	private int nr_annot_chns;
	private int edf;
	private int bdf;
	private int signal_write_sequence_pos;
	private long long_data_record_duration;
	private int annots_in_file;
	private int annotlist_sz;
	private int total_annot_bytes;
	private int eq_sf;
	private byte[] wrbuf;
	private int wrbufsz;
	private byte[] hdr;
	private RandomAccessFile file_out;
	private int status_ok;

	/**
	 * This list contains the annotations (if any).
	 */
	public ArrayList<EDFAnnotationStruct> annotationslist;

	/**
	 * Creates an EDFwriter object that writes to an EDF+/BDF+ file. <br>
	 * Warning: an already existing file with the same name will be silently
	 * overwritten without advance warning.<br>
	 *
	 * @param p_path The path to the file.
	 *
	 * @param f_filetype Must be EDFLIB_FILETYPE_EDFPLUS (0) or
	 * EDFLIB_FILETYPE_BDFPLUS (1).
	 *
	 * @param number_of_signals The number of signals you want to write into the
	 * file.
	 *
	 * @throws IOException, EDFException
	 */
	public EDFwriter(String p_path, int f_filetype, int number_of_signals) throws IOException, EDFException {
		int i, err;

		annotationslist = new ArrayList<EDFAnnotationStruct>(0);

		path = p_path;

		nr_annot_chns = 1;

		long_data_record_duration = EDFLIB_TIME_DIMENSION;

		annotlist_sz = 0;

		annots_in_file = 0;

		plus_gender = 2;

		edfsignals = number_of_signals;

		filetype = f_filetype;

		if ((edfsignals < 1) || (edfsignals > EDFLIB_MAXSIGNALS)) {
			throw new EDFException(EDFLIB_NUMBER_OF_SIGNALS_INVALID, "Invalid number of signals.\n");
		}

		if ((filetype != EDFLIB_FILETYPE_EDFPLUS) && (filetype != EDFLIB_FILETYPE_BDFPLUS)) {
			throw new EDFException(EDFLIB_NUMBER_OF_SIGNALS_INVALID, "Invalid filetype.\n");
		}

		if (filetype == EDFLIB_FILETYPE_EDFPLUS) {
			edf = 1;
		} else {
			bdf = 1;
		}

		file_out = new RandomAccessFile(path, "rw");

		file_out.setLength(0L);

		annotationslist = new ArrayList<EDFAnnotationStruct>(0);

		param_label = new String[edfsignals];
		param_transducer = new String[edfsignals];
		param_physdimension = new String[edfsignals];
		param_phys_min = new double[edfsignals];
		param_phys_max = new double[edfsignals];
		param_dig_min = new int[edfsignals];
		param_dig_max = new int[edfsignals];
		param_prefilter = new String[edfsignals];
		param_smp_per_record = new int[edfsignals];
		param_offset = new double[edfsignals];
		param_buf_offset = new int[edfsignals];
		param_bitvalue = new double[edfsignals];

		status_ok = 1;
	}

	/**
	 * If version is "1.00" than it will return 100.<br>
	 *
	 * @return version number of this library, multiplied by hundred.
	 */
	public int version() {
		return EDFLIB_VERSION;
	}

	/**
	 * Sets the samplefrequency of signal edfsignal. (In reallity, it sets the
	 * number of samples in a datarecord.)<br>
	 * The samplefrequency of a signal is determined as: fs = number of samples
	 * in a datarecord / datarecord duration.<br>
	 * The samplefrequency equals the number of samples in a datarecord only
	 * when the datarecord duration is set to the default of one second.<br>
	 * This function is required for every signal and can be called only before
	 * the first sample write action.<br>
	 *
	 * @param edfsignal signal number, zero based
	 *
	 * @param samplefrequency
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setSampleFrequency(int edfsignal, int samplefrequency) {
		if ((edfsignal < 0) || (edfsignal >= edfsignals) || (samplefrequency < 1) || (datarecords != 0)) {
			return -1;
		}

		param_smp_per_record[edfsignal] = samplefrequency;

		return 0;
	}

	/**
	 * Sets the maximum physical value of signal edfsignal. <br>
	 * This is the value of the input of the ADC when the output equals the
	 * value of "digital maximum".<br>
	 * This function is required for every signal and can be called only before
	 * the first sample write action.<br>
	 *
	 * note: In EDF, the sensitivity (e.g. uV/bit) and offset are stored using
	 * four parameters:<br>
	 * digital maximum and minimum, and physical maximum and minimum.<br>
	 * Here, digital means the raw data coming from a sensor or ADC. Physical
	 * means the units like uV.<br>
	 * The sensitivity in units/bit is calculated as follows:<br>
	 * <br>
	 * units per bit = (physical max - physical min) / (digital max - digital
	 * min)<br>
	 * <br>
	 * The digital offset is calculated as follows:<br>
	 * <br>
	 * offset = (physical max / units per bit) - digital max<br>
	 *
	 * @param edfsignal signal number, zero based
	 *
	 * @param phys_max
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setPhysicalMaximum(int edfsignal, double phys_max) {
		if ((edfsignal < 0) || (edfsignal >= edfsignals) || (datarecords != 0)) {
			return -1;
		}

		param_phys_max[edfsignal] = phys_max;

		return 0;
	}

	/**
	 * Sets the minimum physical value of signal edfsignal. <br>
	 * This is the value of the input of the ADC when the output equals the
	 * value of "digital minimum".<br>
	 * This function is required for every signal and can be called only before
	 * the first sample write action.<br>
	 *
	 * note: In EDF, the sensitivity (e.g. uV/bit) and offset are stored using
	 * four parameters:<br>
	 * digital maximum and minimum, and physical maximum and minimum.<br>
	 * Here, digital means the raw data coming from a sensor or ADC. Physical
	 * means the units like uV.<br>
	 * The sensitivity in units/bit is calculated as follows:<br>
	 * <br>
	 * units per bit = (physical max - physical min) / (digital max - digital
	 * min)<br>
	 * <br>
	 * The digital offset is calculated as follows:<br>
	 * <br>
	 * offset = (physical max / units per bit) - digital max<br>
	 *
	 * @param edfsignal signal number, zero based
	 *
	 * @param phys_min
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setPhysicalMinimum(int edfsignal, double phys_min) {
		if ((edfsignal < 0) || (edfsignal >= edfsignals) || (datarecords != 0)) {
			return -1;
		}

		param_phys_min[edfsignal] = phys_min;

		return 0;
	}

	/**
	 * Sets the maximum digital value of signal edfsignal. The maximum value is
	 * 32767 for EDF and 8388607 for BDF.<br>
	 * Usually it's the extreme output of the ADC.<br>
	 * This function is required for every signal and can be called only before
	 * the first sample write action.<br>
	 *
	 * note: In EDF, the sensitivity (e.g. uV/bit) and offset are stored using
	 * four parameters:<br>
	 * digital maximum and minimum, and physical maximum and minimum.<br>
	 * Here, digital means the raw data coming from a sensor or ADC. Physical
	 * means the units like uV.<br>
	 * The sensitivity in units/bit is calculated as follows:<br>
	 * <br>
	 * units per bit = (physical max - physical min) / (digital max - digital
	 * min)<br>
	 * <br>
	 * The digital offset is calculated as follows:<br>
	 * <br>
	 * offset = (physical max / units per bit) - digital max<br>
	 *
	 * @param edfsignal signal number, zero based
	 *
	 * @param dig_max
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setDigitalMaximum(int edfsignal, int dig_max) {
		if ((edfsignal < 0) || (edfsignal >= edfsignals) || (datarecords != 0)) {
			return -1;
		}

		if (edf != 0) {
			if (dig_max > 32767) {
				return -1;
			}
		} else {
			if (dig_max > 8388607) {
				return -1;
			}
		}

		param_dig_max[edfsignal] = dig_max;

		return 0;
	}

	/**
	 * Sets the minimum digital value of signal edfsignal. The minimum value is
	 * -32768 for EDF and -8388608 for BDF.<br>
	 * Usually it's the extreme output of the ADC.<br>
	 * This function is required for every signal and can be called only before
	 * the first sample write action.<br>
	 *
	 * note: In EDF, the sensitivity (e.g. uV/bit) and offset are stored using
	 * four parameters:<br>
	 * digital maximum and minimum, and physical maximum and minimum.<br>
	 * Here, digital means the raw data coming from a sensor or ADC. Physical
	 * means the units like uV.<br>
	 * The sensitivity in units/bit is calculated as follows:<br>
	 * <br>
	 * units per bit = (physical max - physical min) / (digital max - digital
	 * min)<br>
	 * <br>
	 * The digital offset is calculated as follows:<br>
	 * <br>
	 * offset = (physical max / units per bit) - digital max<br>
	 *
	 * @param edfsignal signal number, zero based
	 *
	 * @param dig_min
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setDigitalMinimum(int edfsignal, int dig_min) {
		if ((edfsignal < 0) || (edfsignal >= edfsignals) || (datarecords != 0)) {
			return -1;
		}

		if (edf != 0) {
			if (dig_min < -32768) {
				return -1;
			}
		} else {
			if (dig_min < -8388608) {
				return -1;
			}
		}

		param_dig_min[edfsignal] = dig_min;

		return 0;
	}

	/**
	 * Sets the label (name) of a signal. ("FP1", "SaO2", etc.)<br>
	 * This function is recommended for every signal and can be called only
	 * before the first sample write action.<br>
	 *
	 * @param edfsignal signal number, zero based
	 *
	 * @param label
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setSignalLabel(int edfsignal, String label) {
		if ((edfsignal < 0) || (edfsignal >= edfsignals) || (datarecords != 0)) {
			return -1;
		}

		param_label[edfsignal] = label;

		return 0;
	}

	/**
	 * Sets the prefilter description of a signal. ("HP:0.05Hz", "LP:250Hz",
	 * "N:60Hz", etc.)<br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param edfsignal signal number, zero based
	 *
	 * @param prefilter
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setPreFilter(int edfsignal, String prefilter) {
		if ((edfsignal < 0) || (edfsignal >= edfsignals) || (datarecords != 0)) {
			return -1;
		}

		param_prefilter[edfsignal] = prefilter;

		return 0;
	}

	/**
	 * Sets the transducer description of a signal. ("AgAgCl cup electrodes",
	 * etc.)<br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param edfsignal signal number, zero based
	 *
	 * @param transducer
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setTransducer(int edfsignal, String transducer) {
		if ((edfsignal < 0) || (edfsignal >= edfsignals) || (datarecords != 0)) {
			return -1;
		}

		param_transducer[edfsignal] = transducer;

		return 0;
	}

	/**
	 * Sets the physical_dimension (unit) of signal. ("uV", "BPM", "mA",
	 * "Degr.", etc.)<br>
	 * This function is recommended for every signal and can be called only
	 * before the first sample write action.<br>
	 *
	 * @param edfsignal signal number, zero based
	 *
	 * @param physical_dimension
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setPhysicalDimension(int edfsignal, String physical_dimension) {
		if ((edfsignal < 0) || (edfsignal >= edfsignals) || (datarecords != 0)) {
			return -1;
		}

		param_physdimension[edfsignal] = physical_dimension;

		return 0;
	}

	/**
	 * Sets the startdate and starttime. <br>
	 * If not called, the system date and time at runtime will be used.<br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 * <br>
	 * If subsecond precision is not needed or not applicable, leave it at
	 * zero.<br>
	 *
	 * @param year 1970 - 3000
	 *
	 * @param month 1 - 12
	 *
	 * @param day 1 - 31
	 *
	 * @param hour 0 - 23
	 *
	 * @param minute 0 - 59
	 *
	 * @param second 0 - 59
	 *
	 * @param subsecond 0 - 9999 expressed in units of 100 microSeconds
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setStartDateTime(int year, int month, int day,
			int hour, int minute, int second, int subsecond) {
		if (datarecords != 0) {
			return -1;
		}

		if ((year < 1970) || (year > 3000)
				|| (month < 1) || (month > 12)
				|| (day < 1) || (day > 31)
				|| (hour < 0) || (hour > 23)
				|| (minute < 0) || (minute > 59)
				|| (second < 0) || (second > 59)
				|| (subsecond < 0) || (subsecond > 9999)) {
			return -1;
		}

		startdate_year = year;
		startdate_month = month;
		startdate_day = day;
		starttime_hour = hour;
		starttime_minute = minute;
		starttime_second = second;
		starttime_offset = subsecond * 1000L;

		return 0;
	}

	/**
	 * Sets the patientname. <br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param name
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setPatientName(String name) {
		if (datarecords != 0) {
			return -1;
		}

		plus_patient_name = name;

		return 0;
	}

	/**
	 * Sets the patientcode. <br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param code
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setPatientCode(String code) {
		if (datarecords != 0) {
			return -1;
		}

		plus_patientcode = code;

		return 0;
	}

	/**
	 * Sets the patients' gender. <br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param gender 0 = female, 1 = male, 2 = unknown or not applicable (this
	 * is the default)
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setPatientGender(int gender) {
		if ((gender < 0) || (gender > 2) || (datarecords != 0)) {
			return -1;
		}

		plus_gender = gender;

		return 0;
	}

	/**
	 * Sets the patients' birthdate. <br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param year 1800 - 3000
	 *
	 * @param month 1 - 12
	 *
	 * @param day 1 - 31
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setPatientBirthDate(int year, int month, int day) {
		if (datarecords != 0) {
			return -1;
		}

		if ((year < 1800) || (year > 3000)
				|| (month < 1) || (month > 12)
				|| (day < 1) || (day > 31)) {
			return -1;
		}

		plus_birthdate_year = year;
		plus_birthdate_month = month;
		plus_birthdate_day = day;

		return 0;
	}

	/**
	 * Sets the additional information related to the patient. <br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param additional
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setPatientAdditional(String additional) {
		if (datarecords != 0) {
			return -1;
		}

		plus_patient_additional = additional;

		return 0;
	}

	/**
	 * Sets the administration code. <br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param admin_code
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setAdministrationCode(String admin_code) {
		if (datarecords != 0) {
			return -1;
		}

		plus_admincode = admin_code;

		return 0;
	}

	/**
	 * Sets the name or id of the technician. <br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param technician
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setTechnician(String technician) {
		if (datarecords != 0) {
			return -1;
		}

		plus_technician = technician;

		return 0;
	}

	/**
	 * Sets the description of the equipment used for the recording. <br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param equipment
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setEquipment(String equipment) {
		if (datarecords != 0) {
			return -1;
		}

		plus_equipment = equipment;

		return 0;
	}

	/**
	 * Sets the additional info of the recording. <br>
	 * This function is optional and can be called only before the first sample
	 * write action.<br>
	 *
	 * @param additional
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setAdditionalRecordingInfo(String additional) {
		if (datarecords != 0) {
			return -1;
		}

		plus_recording_additional = additional;

		return 0;
	}

	/**
	 * Finalizes and closes the file. <br>
	 * This function is required after writing. Failing to do so will cause a
	 * corrupted and incomplete file.<br>
	 *
	 * @throws IOException, EDFException
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int close() throws IOException, EDFException {
		if (status_ok == 0) {
			return -1;
		}

		if (datarecords < 100000000L) {
			file_out.seek(236L);
			if (fprint_int_number_nonlocalized(file_out, (int) datarecords, 0, 0) < 2) {
				file_out.write(' ');
			}
		} else {
			throw new EDFException(EDFLIB_TOO_MANY_DATARECORDS, "Too many datarecords written.\n");
		}

		write_annotations();

		file_out.close();

		status_ok = 0;

		return 0;
	}

	private int write_annotations() throws IOException, UnsupportedEncodingException {
		int i, j, k, n, p, err,
				datrecsize,
				nmemb;

		long offset,
				datrecs = 0L,
				file_sz;

		byte[] str = new byte[EDFLIB_ANNOTATION_BYTES * 2];

		byte[] str2 = new byte[EDFLIB_ANNOTATION_BYTES];

		EDFAnnotationStruct annot2;

		offset = (edfsignals + nr_annot_chns + 1L) * 256L;

		file_sz = offset + (datarecords * (long) recordsize);

		datrecsize = total_annot_bytes;

		for (i = 0; i < edfsignals; i++) {
			if (edf != 0) {
				offset += param_smp_per_record[i] * 2L;

				datrecsize += param_smp_per_record[i] * 2L;
			} else {
				offset += param_smp_per_record[i] * 3L;

				datrecsize += param_smp_per_record[i] * 3L;
			}
		}

		for (k = 0, j = 0; k < annots_in_file; k++) {
			annot2 = annotationslist.get(k);

			annot2.onset += starttime_offset / 1000L;

			p = 0;

			if (j == 0) // first annotation signal
			{
				if ((offset + total_annot_bytes) > file_sz) {
					break;
				}

				file_out.seek(offset);

				p += snprint_ll_number_nonlocalized(str, 0, (datrecs * long_data_record_duration + starttime_offset) / EDFLIB_TIME_DIMENSION, 0, 1);

				if (((long_data_record_duration % EDFLIB_TIME_DIMENSION) != 0) || (starttime_offset != 0)) {
					str[p++] = '.';
					n = snprint_ll_number_nonlocalized(str, p, (datrecs * long_data_record_duration + starttime_offset) % EDFLIB_TIME_DIMENSION, 7, 0);
					p += n;
				}
				str[p++] = 20;
				str[p++] = 20;
				str[p++] = 0;
			}

			n = snprint_ll_number_nonlocalized(str, p, annot2.onset / 10000L, 0, 1);
			p += n;
			if ((annot2.onset % 10000L) != 0) {
				str[p++] = '.';
				n = snprint_ll_number_nonlocalized(str, p, annot2.onset % 10000L, 4, 0);
				p += n;
			}
			if (annot2.duration >= 0L) {
				str[p++] = 21;
				n = snprint_ll_number_nonlocalized(str, p, annot2.duration / 10000L, 0, 0);
				p += n;
				if ((annot2.duration % 10000L) != 0) {
					str[p++] = '.';
					n = snprint_ll_number_nonlocalized(str, p, annot2.duration % 10000L, 4, 0);
					p += n;
				}
			}
			str[p++] = 20;
			strcpy(str2, annot2.description.getBytes("UTF-8"));
			for (i = 0; i < EDFLIB_WRITE_MAX_ANNOTATION_LEN; i++) {
				if (str2[i] == 0) {
					break;
				}

				str[p++] = str2[i];
			}
			str[p++] = 20;

			for (; p < EDFLIB_ANNOTATION_BYTES; p++) {
				str[p] = 0;
			}

			file_out.write(str, 0, EDFLIB_ANNOTATION_BYTES);

			if (++j >= nr_annot_chns) {
				j = 0;

				offset += datrecsize;

				datrecs++;

				if (datrecs >= datarecords) {
					break;
				}
			}
		}

		return 0;
	}

	/**
	 * Writes n "raw" digital samples from buf belonging to one signal. <br>
	 * where n is the samplefrequency of that signal.<br>
	 * The 16 (or 24 in case of BDF) least significant bits of the sample will
	 * be written to the<br>
	 * file without any conversion.<br>
	 * The number of samples written is equal to the samplefrequency of the
	 * signal<br>
	 * (actually, it's the value that is set with setSampleFrequency()).<br>
	 * Size of buf should be equal to or bigger than the samplefrequency<br>
	 * Call this function for every signal in the file. The order is
	 * important!<br>
	 * When there are 4 signals in the file, the order of calling this
	 * function<br>
	 * must be: signal 0, signal 1, signal 2, signal 3, signal 0, signal 1,
	 * signal 2, etc.<br>
	 * The end of a recording must always be at the end of a complete cycle.<br>
	 *
	 * @param buf
	 *
	 * @throws IOException
	 *
	 * @return 0 on success, otherwise non-zero
	 */
	public int writeDigitalSamples(int[] buf) throws IOException {
		int i,
				error,
				sf,
				digmax,
				digmin,
				edfsignal,
				value;

		if (status_ok == 0) {
			return -1;
		}

		edfsignal = signal_write_sequence_pos;

		if (datarecords == 0) {
			if (edfsignal == 0) {
				error = write_edf_header();

				if (error != 0) {
					return error;
				}
			}
		}

		sf = param_smp_per_record[edfsignal];

		digmax = param_dig_max[edfsignal];

		digmin = param_dig_min[edfsignal];

		if (sf > buf.length) {
			return -1;
		}

		if (edf != 0) {
			if (wrbufsz < (sf * 2)) {
				wrbuf = new byte[sf * 2];

				wrbufsz = sf * 2;
			}

			for (i = 0; i < sf; i++) {
				value = buf[i];

				if (value > digmax) {
					value = digmax;
				}

				if (value < digmin) {
					value = digmin;
				}

				wrbuf[i * 2] = (byte) (value & 0xff);

				wrbuf[i * 2 + 1] = (byte) ((value >> 8) & 0xff);
			}

			file_out.write(wrbuf, 0, sf * 2);
		} else {
			if (wrbufsz < (sf * 3)) {
				wrbuf = new byte[sf * 3];

				wrbufsz = sf * 3;
			}

			for (i = 0; i < sf; i++) {
				value = buf[i];

				if (value > digmax) {
					value = digmax;
				}

				if (value < digmin) {
					value = digmin;
				}

				wrbuf[i * 3] = (byte) (value & 0xff);

				wrbuf[i * 3 + 1] = (byte) ((value >> 8) & 0xff);

				wrbuf[i * 3 + 2] = (byte) ((value >> 16) & 0xff);
			}

			file_out.write(wrbuf, 0, sf * 3);
		}

		signal_write_sequence_pos++;

		if (signal_write_sequence_pos == edfsignals) {
			signal_write_sequence_pos = 0;

			if (write_tal(file_out) != 0) {
				return -1;
			}

			datarecords++;
		}

		return 0;
	}

	/**
	 * Writes "raw" digital samples of all signals from buf into the file. <br>
	 * buf must be filled with samples from all signals, starting with n samples
	 * of signal 0, n samples of signal 1, n samples of signal 2, etc.<br>
	 * where n is the samplefrequency of that signal.<br>
	 * The 16 (or 24 in case of BDF) least significant bits of the sample will
	 * be written to the file without any conversion.<br>
	 * The number of samples written is equal to the sum of the
	 * samplefrequencies of all signals.<br>
	 * Size of buf should be equal to or bigger than the sum of the
	 * samplefrequencies of all signals.<br>
	 *
	 * @param buf
	 *
	 * @throws IOException
	 *
	 * @return 0 on success, otherwise non-zero
	 */
	public int blockWriteDigitalSamples(int[] buf) throws IOException {
		int i, j,
				error,
				sf,
				digmax,
				digmin,
				edfsignal,
				value,
				buf_offset = 0;

		if (status_ok == 0) {
			return -1;
		}

		if (signal_write_sequence_pos != 0) {
			return -1;
		}

		if (datarecords == 0) {
			error = write_edf_header();

			if (error != 0) {
				return error;
			}
		}

		for (edfsignal = 0; edfsignal < edfsignals; edfsignal++) {
			sf = param_smp_per_record[edfsignal];

			digmax = param_dig_max[edfsignal];

			digmin = param_dig_min[edfsignal];

			if (sf > buf.length) {
				return -1;
			}

			if (edf != 0) {
				if (wrbufsz < (sf * 2)) {
					wrbuf = new byte[sf * 2];

					wrbufsz = sf * 2;
				}

				for (i = 0; i < sf; i++) {
					value = buf[i + buf_offset];

					if (value > digmax) {
						value = digmax;
					}

					if (value < digmin) {
						value = digmin;
					}

					wrbuf[i * 2] = (byte) (value & 0xff);

					wrbuf[i * 2 + 1] = (byte) ((value >> 8) & 0xff);
				}

				file_out.write(wrbuf, 0, sf * 2);
			} else {
				if (wrbufsz < (sf * 3)) {
					wrbuf = new byte[sf * 3];

					wrbufsz = sf * 3;
				}

				for (i = 0; i < sf; i++) {
					value = buf[i + buf_offset];

					if (value > digmax) {
						value = digmax;
					}

					if (value < digmin) {
						value = digmin;
					}

					wrbuf[i * 3] = (byte) (value & 0xff);

					wrbuf[i * 3 + 1] = (byte) ((value >> 8) & 0xff);

					wrbuf[i * 3 + 2] = (byte) ((value >> 16) & 0xff);
				}

				file_out.write(wrbuf, 0, sf * 3);
			}

			buf_offset += sf;
		}

		if (write_tal(file_out) != 0) {
			return -1;
		}

		datarecords++;

		return 0;
	}

	/**
	 * For use with BDF only.&nbsp;Writes "raw" digital samples of all signals
	 * from buf into the file. <br>
	 * buf must be filled with samples from all signals, starting with n samples
	 * of signal 0, n samples of signal 1, n samples of signal 2, etc.<br>
	 * where n is the samplefrequency of that signal.<br>
	 * A sample consists of three consecutive bytes (24 bits, little endian,
	 * seconds' complement) and will be written to the file without any
	 * conversion.<br>
	 * The number of samples written is equal to the sum of the
	 * samplefrequencies of all signals.<br>
	 * Size of buf should be equal to or bigger than the sum of the
	 * samplefrequencies of all signals * 3.<br>
	 *
	 * @param buf
	 *
	 * @throws IOException
	 *
	 * @return 0 on success, otherwise non-zero
	 */
	public int blockWriteDigital3ByteSamples(byte[] buf) throws IOException {
		int j, error, total_samples = 0;

		if (status_ok == 0) {
			return -1;
		}

		if (signal_write_sequence_pos != 0) {
			return -1;
		}

		if (bdf != 1) {
			return -1;
		}

		for (j = 0; j < edfsignals; j++) {
			total_samples += param_smp_per_record[j];
		}

		if (datarecords == 0) {
			error = write_edf_header();

			if (error != 0) {
				return error;
			}
		}

		file_out.write(buf, 0, total_samples * 3);

		if (write_tal(file_out) != 0) {
			return -1;
		}

		datarecords++;

		return 0;
	}

	/**
	 * Writes "raw" digital samples of all signals from buf into the file. <br>
	 * buf must be filled with samples from all signals, starting with n samples
	 * of signal 0, n samples of signal 1, n samples of signal 2, etc.<br>
	 * where n is the samplefrequency of that signal.<br>
	 * The 16 (or 24 in case of BDF) least significant bits of the sample will
	 * be written to the file without any conversion.<br>
	 * The number of samples written is equal to the sum of the
	 * samplefrequencies of all signals.<br>
	 * Size of buf should be equal to or bigger than the sum of the
	 * samplefrequencies of all signals.<br>
	 *
	 * @param buf
	 *
	 * @throws IOException
	 *
	 * @return 0 on success, otherwise non-zero
	 */
	public int blockWriteDigitalShortSamples(short[] buf) throws IOException {
		int i, j,
				error,
				sf,
				digmax,
				digmin,
				edfsignal,
				value,
				buf_offset = 0;

		if (status_ok == 0) {
			return -1;
		}

		if (signal_write_sequence_pos != 0) {
			return -1;
		}

		if (datarecords == 0) {
			error = write_edf_header();

			if (error != 0) {
				return error;
			}
		}

		for (edfsignal = 0; edfsignal < edfsignals; edfsignal++) {
			sf = param_smp_per_record[edfsignal];

			digmax = param_dig_max[edfsignal];

			digmin = param_dig_min[edfsignal];

			if (sf > buf.length) {
				return -1;
			}

			if (edf != 0) {
				if (wrbufsz < (sf * 2)) {
					wrbuf = new byte[sf * 2];

					wrbufsz = sf * 2;
				}

				for (i = 0; i < sf; i++) {
					value = buf[i + buf_offset];

					if (value > digmax) {
						value = digmax;
					}

					if (value < digmin) {
						value = digmin;
					}

					wrbuf[i * 2] = (byte) (value & 0xff);

					wrbuf[i * 2 + 1] = (byte) ((value >> 8) & 0xff);
				}

				file_out.write(wrbuf, 0, sf * 2);
			} else {
				if (wrbufsz < (sf * 3)) {
					wrbuf = new byte[sf * 3];

					wrbufsz = sf * 3;
				}

				for (i = 0; i < sf; i++) {
					value = buf[i + buf_offset];

					if (value > digmax) {
						value = digmax;
					}

					if (value < digmin) {
						value = digmin;
					}

					wrbuf[i * 3] = (byte) (value & 0xff);

					wrbuf[i * 3 + 1] = (byte) ((value >> 8) & 0xff);

					wrbuf[i * 3 + 2] = (byte) ((value >> 16) & 0xff);
				}

				file_out.write(wrbuf, 0, sf * 3);
			}

			buf_offset += sf;
		}

		if (write_tal(file_out) != 0) {
			return -1;
		}

		datarecords++;

		return 0;
	}

	/**
	 * Writes n "raw" digital samples from buf belonging to one signal. <br>
	 * where n is the samplefrequency of that signal.<br>
	 * The 16 (or 24 in case of BDF) least significant bits of the sample will
	 * be written to the<br>
	 * file without any conversion.<br>
	 * The number of samples written is equal to the samplefrequency of the
	 * signal<br>
	 * (actually, it's the value that is set with setSampleFrequency()).<br>
	 * Size of buf should be equal to or bigger than the samplefrequency<br>
	 * Call this function for every signal in the file. The order is
	 * important!<br>
	 * When there are 4 signals in the file, the order of calling this
	 * function<br>
	 * must be: signal 0, signal 1, signal 2, signal 3, signal 0, signal 1,
	 * signal 2, etc.<br>
	 * The end of a recording must always be at the end of a complete cycle.<br>
	 *
	 * @param buf
	 *
	 * @throws IOException
	 *
	 * @return 0 on success, otherwise non-zero
	 */
	public int writeDigitalShortSamples(short[] buf) throws IOException {
		int i,
				error,
				sf,
				digmax,
				digmin,
				edfsignal,
				value;

		if (status_ok == 0) {
			return -1;
		}

		edfsignal = signal_write_sequence_pos;

		if (datarecords == 0) {
			if (edfsignal == 0) {
				error = write_edf_header();

				if (error != 0) {
					return error;
				}
			}
		}

		sf = param_smp_per_record[edfsignal];

		digmax = param_dig_max[edfsignal];

		digmin = param_dig_min[edfsignal];

		if (sf > buf.length) {
			return -1;
		}

		if (edf != 0) {
			if (wrbufsz < (sf * 2)) {
				wrbuf = new byte[sf * 2];

				wrbufsz = sf * 2;
			}

			for (i = 0; i < sf; i++) {
				value = buf[i];

				if (value > digmax) {
					value = digmax;
				}

				if (value < digmin) {
					value = digmin;
				}

				wrbuf[i * 2] = (byte) (value & 0xff);

				wrbuf[i * 2 + 1] = (byte) ((value >> 8) & 0xff);
			}

			file_out.write(wrbuf, 0, sf * 2);
		} else {
			if (wrbufsz < (sf * 3)) {
				wrbuf = new byte[sf * 3];

				wrbufsz = sf * 3;
			}

			for (i = 0; i < sf; i++) {
				value = buf[i];

				if (value > digmax) {
					value = digmax;
				}

				if (value < digmin) {
					value = digmin;
				}

				wrbuf[i * 3] = (byte) (value & 0xff);

				wrbuf[i * 3 + 1] = (byte) ((value >> 8) & 0xff);

				wrbuf[i * 3 + 2] = (byte) ((value >> 16) & 0xff);
			}

			file_out.write(wrbuf, 0, sf * 3);
		}

		signal_write_sequence_pos++;

		if (signal_write_sequence_pos == edfsignals) {
			signal_write_sequence_pos = 0;

			if (write_tal(file_out) != 0) {
				return -1;
			}

			datarecords++;
		}

		return 0;
	}

	/**
	 * Writes n "physical" samples (uV, mmHg, Ohm, etc.) from buf belonging to
	 * one signal. <br>
	 * where n is the samplefrequency of that signal.<br>
	 * The physical samples will be converted to digital samples using the<br>
	 * values of physical maximum, physical minimum, digital maximum and digital
	 * minimum.<br>
	 * The number of samples written is equal to the samplefrequency of the
	 * signal<br>
	 * (actually, it's the value that is set with setSampleFrequency()).<br>
	 * Size of buf should be equal to or bigger than the samplefrequency<br>
	 * Call this function for every signal in the file. The order is
	 * important!<br>
	 * When there are 4 signals in the file, the order of calling this
	 * function<br>
	 * must be: signal 0, signal 1, signal 2, signal 3, signal 0, signal 1,
	 * signal 2, etc.<br>
	 * The end of a recording must always be at the end of a complete cycle.<br>
	 *
	 * @param buf
	 *
	 * @throws IOException
	 *
	 * @return 0 on success, otherwise non-zero
	 */
	public int writePhysicalSamples(double[] buf) throws IOException {
		int i,
				error,
				sf,
				digmax,
				digmin,
				edfsignal,
				value;

		if (status_ok == 0) {
			return -1;
		}

		edfsignal = signal_write_sequence_pos;

		if (datarecords == 0) {
			if (edfsignal == 0) {
				error = write_edf_header();

				if (error != 0) {
					return error;
				}
			}
		}

		sf = param_smp_per_record[edfsignal];

		digmax = param_dig_max[edfsignal];

		digmin = param_dig_min[edfsignal];

		if (sf > buf.length) {
			return -1;
		}

		if (edf != 0) {
			if (wrbufsz < (sf * 2)) {
				wrbuf = new byte[sf * 2];

				wrbufsz = sf * 2;
			}

			for (i = 0; i < sf; i++) {
				value = (int) ((buf[i] / param_bitvalue[edfsignal]) - param_offset[edfsignal]);

				if (value > digmax) {
					value = digmax;
				}

				if (value < digmin) {
					value = digmin;
				}

				wrbuf[i * 2] = (byte) (value & 0xff);

				wrbuf[i * 2 + 1] = (byte) ((value >> 8) & 0xff);
			}

			file_out.write(wrbuf, 0, sf * 2);
		} else {
			if (wrbufsz < (sf * 3)) {
				wrbuf = new byte[sf * 3];

				wrbufsz = sf * 3;
			}

			for (i = 0; i < sf; i++) {
				value = (int) ((buf[i] / param_bitvalue[edfsignal]) - param_offset[edfsignal]);

				if (value > digmax) {
					value = digmax;
				}

				if (value < digmin) {
					value = digmin;
				}

				wrbuf[i * 3] = (byte) (value & 0xff);

				wrbuf[i * 3 + 1] = (byte) ((value >> 8) & 0xff);

				wrbuf[i * 3 + 2] = (byte) ((value >> 16) & 0xff);
			}

			file_out.write(wrbuf, 0, sf * 3);
		}

		signal_write_sequence_pos++;

		if (signal_write_sequence_pos == edfsignals) {
			signal_write_sequence_pos = 0;

			if (write_tal(file_out) != 0) {
				return -1;
			}

			datarecords++;
		}

		return 0;
	}

	/**
	 * Writes "physical" samples (uV, mmHg, Ohm, etc.) of all signals from buf
	 * into the file. <br>
	 * buf must be filled with samples from all signals, starting with n samples
	 * of signal 0, n samples of signal 1, n samples of signal 2, etc.<br>
	 * where n is the samplefrequency of that signal.<br>
	 * The physical samples will be converted to digital samples using the<br>
	 * values of physical maximum, physical minimum, digital maximum and digital
	 * minimum.<br>
	 * The number of samples written is equal to the sum of the
	 * samplefrequencies of all signals.<br>
	 * Size of buf should be equal to or bigger than the sum of the
	 * samplefrequencies of all signals.<br>
	 *
	 * @param buf
	 *
	 * @throws IOException
	 *
	 * @return 0 on success, otherwise non-zero
	 */
	public int blockWritePhysicalSamples(double[] buf) throws IOException {
		int i, j,
				error,
				sf,
				digmax,
				digmin,
				edfsignal,
				value,
				buf_offset = 0;

		if (status_ok == 0) {
			return -1;
		}

		if (signal_write_sequence_pos != 0) {
			return -1;
		}

		if (datarecords == 0) {
			error = write_edf_header();

			if (error != 0) {
				return error;
			}
		}

		for (edfsignal = 0; edfsignal < edfsignals; edfsignal++) {
			sf = param_smp_per_record[edfsignal];

			digmax = param_dig_max[edfsignal];

			digmin = param_dig_min[edfsignal];

			if (sf > buf.length) {
				return -1;
			}

			if (edf != 0) {
				if (wrbufsz < (sf * 2)) {
					wrbuf = new byte[sf * 2];

					wrbufsz = sf * 2;
				}

				for (i = 0; i < sf; i++) {
					value = (int) ((buf[i + buf_offset] / param_bitvalue[edfsignal]) - param_offset[edfsignal]);

					if (value > digmax) {
						value = digmax;
					}

					if (value < digmin) {
						value = digmin;
					}

					wrbuf[i * 2] = (byte) (value & 0xff);

					wrbuf[i * 2 + 1] = (byte) ((value >> 8) & 0xff);
				}

				file_out.write(wrbuf, 0, sf * 2);
			} else {
				if (wrbufsz < (sf * 3)) {
					wrbuf = new byte[sf * 3];

					wrbufsz = sf * 3;
				}

				for (i = 0; i < sf; i++) {
					value = (int) ((buf[i + buf_offset] / param_bitvalue[edfsignal]) - param_offset[edfsignal]);

					if (value > digmax) {
						value = digmax;
					}

					if (value < digmin) {
						value = digmin;
					}

					wrbuf[i * 3] = (byte) (value & 0xff);

					wrbuf[i * 3 + 1] = (byte) ((value >> 8) & 0xff);

					wrbuf[i * 3 + 2] = (byte) ((value >> 16) & 0xff);
				}

				file_out.write(wrbuf, 0, sf * 3);
			}

			buf_offset += sf;
		}

		if (write_tal(file_out) != 0) {
			return -1;
		}

		datarecords++;

		return 0;
	}

	/**
	 * Writes an annotation/event to the file. <br>
	 * onset is relative to the starttime of the recording.<br>
	 * onset and duration are in units of 100 microSeconds. Resolution is 0.0001
	 * second.<br>
	 * E.g. 34.071 seconds must be written as 340710.<br>
	 * If duration is unknown or not applicable: set a negative number (-1).<br>
	 * Description is a string containing the text that describes the event.<br>
	 * This function is optional.<br>
	 *
	 * @param onset onset time of the event expressed in units of 100
	 * microSeconds, must be >= 0
	 *
	 * @param duration duration time of the event expressed in units of 100
	 * microSeconds
	 *
	 * @param description description of the event
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int writeAnnotation(long onset, long duration, String description) {
		EDFAnnotationStruct new_annotation;

		if (status_ok == 0) {
			return -1;
		}

		if (onset < 0L) {
			return -1;
		}

		if (annots_in_file >= annotlist_sz) {
			annotlist_sz += EDFLIB_ANNOT_MEMBLOCKSZ;

			annotationslist.ensureCapacity(annotlist_sz);
		}

		new_annotation = new EDFAnnotationStruct();

		new_annotation.onset = onset;

		new_annotation.duration = duration;

		new_annotation.description = description;

		annots_in_file++;

		annotationslist.add(new_annotation);

		return 0;
	}

	/**
	 * Sets the datarecord duration. <br>
	 * This function is optional, normally you don't need to change the default
	 * value of one second.<br>
	 * This function is NOT REQUIRED but can be called only before the first
	 * sample write action.<br>
	 * <br>
	 * This function can be used when you want to use a non-integer
	 * samplerate.<br>
	 * For example, if you want to use a samplerate of 0.5 Hz, set the
	 * samplefrequency to 5 Hz and<br>
	 * the datarecord duration to 10 seconds, or alternatively, set the
	 * samplefrequency to 1 Hz and<br>
	 * the datarecord duration to 2 seconds.<br>
	 * This function can also be used when you want to use a very high
	 * samplerate.<br>
	 * For example, if you want to use a samplerate of 5 GHz,<br>
	 * set the samplefrequency to 5000 Hz and the datarecord duration to 1
	 * microSecond.<br>
	 * Do not use this function if not necessary.<br>
	 *
	 * @param duration expressed in microSeconds, range: 1 - 60000000 (1uSec. -
	 * 60 sec.)
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setDataRecordDuration(int duration) {
		if ((duration < 1) || (duration > 60000000) || (datarecords != 0)) {
			return -1;
		}

		long_data_record_duration = (long) duration * 10L;

		return 0;
	}

	/**
	 * Sets the number of annotation signals. The default value is 1<br>
	 * This function is optional and, if used, must be called before the first
	 * sample write action.<br>
	 * Normally you don't need to change the default value. Only when the number
	 * of annotations<br>
	 * you want to write is higher than the number of datarecords in the
	 * recording, you can use<br>
	 * this function to increase the storage space for annotations.<br>
	 *
	 * @param annot_signals minimum is 1, maximum is 64
	 *
	 * @return 0 on success, otherwise -1
	 */
	public int setNumberOfAnnotationSignals(int annot_signals) {
		if ((annot_signals < 1) || (annot_signals >= EDFLIB_MAX_ANNOTATION_CHANNELS) || (datarecords != 0)) {
			return -1;
		}

		nr_annot_chns = annot_signals;

		return 0;
	}

	private int write_edf_header() throws IOException, UnsupportedEncodingException {
		int i, j, p, q,
				len,
				rest;

		byte[] str = new byte[128];

		if (status_ok == 0) {
			return -1;
		}

		eq_sf = 1;

		recordsize = 0;

		total_annot_bytes = EDFLIB_ANNOTATION_BYTES * nr_annot_chns;

		for (i = 0; i < edfsignals; i++) {
			if (param_smp_per_record[i] < 1) {
				return EDFLIB_NO_SAMPLES_IN_RECORD;
			}

			if (param_dig_max[i] == param_dig_min[i]) {
				return EDFLIB_DIGMIN_IS_DIGMAX;
			}

			if (param_dig_max[i] < param_dig_min[i]) {
				return EDFLIB_DIGMAX_LOWER_THAN_DIGMIN;
			}

			if (param_phys_max[i] == param_phys_min[i]) {
				return EDFLIB_PHYSMIN_IS_PHYSMAX;
			}

			recordsize += param_smp_per_record[i];

			if (i > 0) {
				if (param_smp_per_record[i] != param_smp_per_record[i - 1]) {
					eq_sf = 0;
				}
			}
		}

		if (edf != 0) {
			recordsize *= 2;

			recordsize += total_annot_bytes;

			if (recordsize > (10 * 1024 * 1024)) /* datarecord size should not exceed 10MB for EDF */ {
				return EDFLIB_DATARECORD_SIZE_TOO_BIG;
			}
			/* if your application gets hit by this limitation, lower the value for the datarecord duration */
 /* using the function edf_set_datarecord_duration() */
		} else {
			recordsize *= 3;

			recordsize += total_annot_bytes;

			if (recordsize > (15 * 1024 * 1024)) /* datarecord size should not exceed 15MB for BDF */ {
				return EDFLIB_DATARECORD_SIZE_TOO_BIG;
			}
			/* if your application gets hit by this limitation, lower the value for the datarecord duration */
 /* using the function edf_set_datarecord_duration() */
		}

		for (i = 0; i < edfsignals; i++) {
			param_bitvalue[i] = (param_phys_max[i] - param_phys_min[i]) / (param_dig_max[i] - param_dig_min[i]);
			param_offset[i] = param_phys_max[i] / param_bitvalue[i] - param_dig_max[i];
		}

		file_out.seek(0L);

		if (edf != 0) {
			file_out.writeBytes(String.format("0       "));
		} else {
			file_out.write((byte) -1);
			file_out.writeBytes(String.format("BIOSEMI"));
		}

		p = 0;

		if (plus_birthdate_year == 0) {
			rest = 72;
		} else {
			rest = 62;
		}

		if (plus_patientcode != null) {
			len = plus_patientcode.length();
		} else {
			len = 0;
		}
		if ((len != 0) && (rest != 0)) {
			if (len > rest) {
				len = rest;
				rest = 0;
			} else {
				rest -= len;
			}
			strcpy(str, plus_patientcode.getBytes("ISO-8859-1"));
			latin1_to_ascii(str, len);
			for (i = 0; i < len; i++) {
				if (str[i] == ' ') {
					str[i] = '_';
				}
			}
			file_out.write(str, 0, len);
			p += len;
			file_out.write(' ');
			p++;
		} else {
			file_out.writeBytes(String.format("X "));
			p += 2;
		}

		if (plus_gender == 1) {
			file_out.write('M');
		} else {
			if (plus_gender == 0) {
				file_out.write('F');
			} else {
				file_out.write('X');
			}
		}
		file_out.write(' ');
		p += 2;

		if (plus_birthdate_year == 0) {
			file_out.writeBytes(String.format("X "));
			p += 2;
		} else {
			file_out.writeBytes(String.format("%02d-", plus_birthdate_day));
			switch (plus_birthdate_month) {
				case 1:
					file_out.writeBytes(String.format("JAN"));
					break;
				case 2:
					file_out.writeBytes(String.format("FEB"));
					break;
				case 3:
					file_out.writeBytes(String.format("MAR"));
					break;
				case 4:
					file_out.writeBytes(String.format("APR"));
					break;
				case 5:
					file_out.writeBytes(String.format("MAY"));
					break;
				case 6:
					file_out.writeBytes(String.format("JUN"));
					break;
				case 7:
					file_out.writeBytes(String.format("JUL"));
					break;
				case 8:
					file_out.writeBytes(String.format("AUG"));
					break;
				case 9:
					file_out.writeBytes(String.format("SEP"));
					break;
				case 10:
					file_out.writeBytes(String.format("OCT"));
					break;
				case 11:
					file_out.writeBytes(String.format("NOV"));
					break;
				case 12:
					file_out.writeBytes(String.format("DEC"));
					break;
				default:
					file_out.writeBytes(String.format("ERR"));
					break;
			}
			file_out.writeBytes(String.format("-%04d ", plus_birthdate_year));
			p += 12;
		}

		if (plus_patient_name != null) {
			len = plus_patient_name.length();
		} else {
			len = 0;
		}
		if ((len != 0) && (rest != 0)) {
			if (len > rest) {
				len = rest;
				rest = 0;
			} else {
				rest -= len;
			}
			strcpy(str, plus_patient_name.getBytes("ISO-8859-1"));
			latin1_to_ascii(str, len);
			for (i = 0; i < len; i++) {
				if (str[i] == ' ') {
					str[i] = '_';
				}
			}
			file_out.write(str, 0, len);
			p += len;
		} else {
			file_out.write('X');
			p++;
		}

		if (rest != 0) {
			file_out.write(' ');

			p++;

			rest--;
		}

		if (plus_patient_additional != null) {
			len = plus_patient_additional.length();
		} else {
			len = 0;
		}
		if ((len != 0) && (rest != 0)) {
			if (len > rest) {
				len = rest;
			}
			strcpy(str, plus_patient_additional.getBytes("ISO-8859-1"));
			latin1_to_ascii(str, len);
			file_out.write(str, 0, len);
			p += len;
		}

		for (; p < 80; p++) {
			file_out.write(' ');
		}

		if (startdate_year == 0) {
			LocalDateTime date_time = LocalDateTime.now();

			startdate_year = date_time.getYear();
			startdate_month = date_time.getMonthValue();
			startdate_day = date_time.getDayOfMonth();
			starttime_hour = date_time.getHour();
			starttime_minute = date_time.getMinute();
			starttime_second = date_time.getSecond();
		}

		file_out.writeBytes(String.format("Startdate %02d-", startdate_day));
		switch (startdate_month) {
			case 1:
				file_out.writeBytes(String.format("JAN"));
				break;
			case 2:
				file_out.writeBytes(String.format("FEB"));
				break;
			case 3:
				file_out.writeBytes(String.format("MAR"));
				break;
			case 4:
				file_out.writeBytes(String.format("APR"));
				break;
			case 5:
				file_out.writeBytes(String.format("MAY"));
				break;
			case 6:
				file_out.writeBytes(String.format("JUN"));
				break;
			case 7:
				file_out.writeBytes(String.format("JUL"));
				break;
			case 8:
				file_out.writeBytes(String.format("AUG"));
				break;
			case 9:
				file_out.writeBytes(String.format("SEP"));
				break;
			case 10:
				file_out.writeBytes(String.format("OCT"));
				break;
			case 11:
				file_out.writeBytes(String.format("NOV"));
				break;
			case 12:
				file_out.writeBytes(String.format("DEC"));
				break;
			default:
				file_out.writeBytes(String.format("ERR"));
				break;
		}
		file_out.writeBytes(String.format("-%04d ", startdate_year));
		p = 22;

		rest = 42;

		if (plus_admincode != null) {
			len = plus_admincode.length();
		} else {
			len = 0;
		}
		if ((len != 0) && (rest != 0)) {
			if (len > rest) {
				len = rest;
				rest = 0;
			} else {
				rest -= len;
			}
			strcpy(str, plus_admincode.getBytes("ISO-8859-1"));
			latin1_to_ascii(str, len);
			for (i = 0; i < len; i++) {
				if (str[i] == ' ') {
					str[i] = '_';
				}
			}
			file_out.write(str, 0, len);
			p += len;
		} else {
			file_out.write('X');
			p++;
		}

		if (rest != 0) {
			file_out.write(' ');
			p++;
			rest--;
		}

		if (plus_technician != null) {
			len = plus_technician.length();
		} else {
			len = 0;
		}
		if ((len != 0) && (rest != 0)) {
			if (len > rest) {
				len = rest;
				rest = 0;
			} else {
				rest -= len;
			}
			strcpy(str, plus_technician.getBytes("ISO-8859-1"));
			latin1_to_ascii(str, len);
			for (i = 0; i < len; i++) {
				if (str[i] == ' ') {
					str[i] = '_';
				}
			}
			file_out.write(str, 0, len);
			p += len;
		} else {
			file_out.write('X');
			p++;
		}

		if (rest != 0) {
			file_out.write(' ');
			p++;
			rest--;
		}

		if (plus_equipment != null) {
			len = plus_equipment.length();
		} else {
			len = 0;
		}
		if ((len != 0) && (rest != 0)) {
			if (len > rest) {
				len = rest;
				rest = 0;
			} else {
				rest -= len;
			}
			strcpy(str, plus_equipment.getBytes("ISO-8859-1"));
			latin1_to_ascii(str, len);
			for (i = 0; i < len; i++) {
				if (str[i] == ' ') {
					str[i] = '_';
				}
			}
			file_out.write(str, 0, len);
			p += len;
		} else {
			file_out.write('X');
			p++;
		}

		if (rest != 0) {
			file_out.write(' ');
			p++;
			rest--;
		}

		if (plus_recording_additional != null) {
			len = plus_recording_additional.length();
		} else {
			len = 0;
		}
		if ((len != 0) && (rest != 0)) {
			if (len > rest) {
				len = rest;
				rest = 0;
			} else {
				rest -= len;
			}
			strcpy(str, plus_recording_additional.getBytes("ISO-8859-1"));
			latin1_to_ascii(str, len);
			for (i = 0; i < len; i++) {
				if (str[i] == ' ') {
					str[i] = '_';
				}
			}
			file_out.write(str, 0, len);
			p += len;
		}

		for (; p < 80; p++) {
			file_out.write(' ');
		}

		file_out.writeBytes(String.format("%02d.%02d.%02d", startdate_day, startdate_month, (startdate_year % 100)));
		file_out.writeBytes(String.format("%02d.%02d.%02d", starttime_hour, starttime_minute, starttime_second));

		p = fprint_int_number_nonlocalized(file_out, (edfsignals + nr_annot_chns + 1) * 256, 0, 0);
		for (; p < 8; p++) {
			file_out.write(' ');
		}
		if (edf != 0) {
			file_out.writeBytes(String.format("EDF+C"));
		} else {
			file_out.writeBytes(String.format("BDF+C"));
		}
		for (i = 0; i < 39; i++) {
			file_out.write(' ');
		}
		file_out.writeBytes(String.format("-1      "));
		if (long_data_record_duration == EDFLIB_TIME_DIMENSION) {
			file_out.writeBytes(String.format("1       "));
		} else {
			p = sprint_number_nonlocalized(str, ((double) long_data_record_duration) / EDFLIB_TIME_DIMENSION);
			for (; p < 8; p++) {
				str[p] = ' ';
			}
//    strcat(str, new byte[]{' ',' ',' ',' ',' ',' ',' ',' '});
			file_out.write(str, 0, 8);
		}
		p = fprint_int_number_nonlocalized(file_out, edfsignals + nr_annot_chns, 0, 0);
		for (; p < 4; p++) {
			file_out.write(' ');
		}

		for (i = 0; i < edfsignals; i++) {
			if (param_label[i] != null) {
				len = param_label[i].length();
			} else {
				len = 0;
			}
			if (len != 0) {
				if (len > 16) {
					len = 16;
				}
				strcpy(str, param_label[i].getBytes("ISO-8859-1"));
				latin1_to_ascii(str, len);
				file_out.write(str, 0, len);
			}
			for (j = len; j < 16; j++) {
				file_out.write(' ');
			}
		}
		for (j = 0; j < nr_annot_chns; j++) {
			if (edf != 0) {
				file_out.writeBytes(String.format("EDF Annotations "));
			} else {
				file_out.writeBytes(String.format("BDF Annotations "));
			}
		}
		for (i = 0; i < edfsignals; i++) {
			if (param_transducer[i] != null) {
				len = param_transducer[i].length();
			} else {
				len = 0;
			}
			if (len != 0) {
				if (len > 80) {
					len = 80;
				}
				strcpy(str, param_transducer[i].getBytes("ISO-8859-1"));
				latin1_to_ascii(str, len);
				file_out.write(str, 0, len);
			}
			for (j = len; j < 80; j++) {
				file_out.write(' ');
			}
		}
		for (j = 0; j < nr_annot_chns; j++) {
			for (i = 0; i < 80; i++) {
				file_out.write(' ');
			}
		}
		for (i = 0; i < edfsignals; i++) {
			if (param_physdimension[i] != null) {
				len = param_physdimension[i].length();
			} else {
				len = 0;
			}
			if (len != 0) {
				if (len > 8) {
					len = 8;
				}
				strcpy(str, param_physdimension[i].getBytes("ISO-8859-1"));
				latin1_to_ascii(str, len);
				file_out.write(str, 0, len);
			}
			for (j = len; j < 8; j++) {
				file_out.write(' ');
			}
		}
		for (j = 0; j < nr_annot_chns; j++) {
			for (i = 0; i < 8; i++) {
				file_out.write(' ');
			}
		}
		for (i = 0; i < edfsignals; i++) {
			p = sprint_number_nonlocalized(str, param_phys_min[i]);
			for (; p < 8; p++) {
				str[p] = ' ';
			}
			file_out.write(str, 0, 8);
		}
		for (j = 0; j < nr_annot_chns; j++) {
			file_out.writeBytes(String.format("-1      "));
		}
		for (i = 0; i < edfsignals; i++) {
			p = sprint_number_nonlocalized(str, param_phys_max[i]);
			for (; p < 8; p++) {
				str[p] = ' ';
			}
			file_out.write(str, 0, 8);
		}
		for (j = 0; j < nr_annot_chns; j++) {
			file_out.writeBytes(String.format("1       "));
		}
		for (i = 0; i < edfsignals; i++) {
			p = fprint_int_number_nonlocalized(file_out, param_dig_min[i], 0, 0);
			for (; p < 8; p++) {
				file_out.write(' ');
			}
		}
		for (j = 0; j < nr_annot_chns; j++) {
			if (edf != 0) {
				file_out.writeBytes(String.format("-32768  "));
			} else {
				file_out.writeBytes(String.format("-8388608"));
			}
		}
		for (i = 0; i < edfsignals; i++) {
			p = fprint_int_number_nonlocalized(file_out, param_dig_max[i], 0, 0);
			for (; p < 8; p++) {
				file_out.write(' ');
			}
		}
		for (j = 0; j < nr_annot_chns; j++) {
			if (edf != 0) {
				file_out.writeBytes(String.format("32767   "));
			} else {
				file_out.writeBytes(String.format("8388607 "));
			}
		}
		for (i = 0; i < edfsignals; i++) {
			if (param_prefilter[i] != null) {
				len = param_prefilter[i].length();
			} else {
				len = 0;
			}
			if (len != 0) {
				if (len > 80) {
					len = 80;
				}
				strcpy(str, param_prefilter[i].getBytes("ISO-8859-1"));
				latin1_to_ascii(str, len);
				file_out.write(str, 0, len);
			}
			for (j = len; j < 80; j++) {
				file_out.write(' ');
			}
		}
		for (j = 0; j < nr_annot_chns; j++) {
			for (i = 0; i < 80; i++) {
				file_out.write(' ');
			}
		}
		for (i = 0; i < edfsignals; i++) {
			p = fprint_int_number_nonlocalized(file_out, param_smp_per_record[i], 0, 0);
			for (; p < 8; p++) {
				file_out.write(' ');
			}
		}
		for (j = 0; j < nr_annot_chns; j++) {
			if (edf != 0) {
				p = fprint_int_number_nonlocalized(file_out, EDFLIB_ANNOTATION_BYTES / 2, 0, 0);
			} else {
				p = fprint_int_number_nonlocalized(file_out, EDFLIB_ANNOTATION_BYTES / 3, 0, 0);
			}
			for (; p < 8; p++) {
				file_out.write(' ');
			}
		}
		for (i = 0; i < ((edfsignals + nr_annot_chns) * 32); i++) {
			file_out.write(' ');
		}

		return 0;
	}

	private int sprint_number_nonlocalized(byte[] dest, double val) {
		int flag = 0, z, i, j = 0, q, base = 1000000000, sz;

		double var;

		sz = dest.length;

		if (sz < 1) {
			return 0;
		}

		q = (int) val;

		var = val - q;

		if (val < 0.0) {
			dest[j++] = '-';

			if (q < 0) {
				q = -q;
			}
		}

		if (j == sz) {
			dest[--j] = 0;

			return j;
		}

		for (i = 10; i != 0; i--) {
			z = q / base;

			q %= base;

			if ((z != 0) || (flag != 0)) {
				dest[j++] = (byte) ('0' + z);

				if (j == sz) {
					dest[--j] = 0;

					return j;
				}

				flag = 1;
			}

			base /= 10;
		}

		if (flag == 0) {
			dest[j++] = '0';
		}

		if (j == sz) {
			dest[--j] = 0;

			return j;
		}

		base = 100000000;

		var *= (base * 10);

		q = (int) var;

		if (q < 0) {
			q = -q;
		}

		if (q == 0) {
			dest[j] = 0;

			return j;
		}

		dest[j++] = '.';

		if (j == sz) {
			dest[--j] = 0;

			return j;
		}

		for (i = 9; i != 0; i--) {
			z = q / base;

			q %= base;

			dest[j++] = (byte) ('0' + z);

			if (j == sz) {
				dest[--j] = 0;

				return j;
			}

			base /= 10;
		}

		dest[j] = 0;

		j--;

		for (; j > 0; j--) {
			if (dest[j] == '0') {
				dest[j] = 0;
			} else {
				j++;

				break;
			}
		}

		return j;
	}

	/* minimum is the minimum digits that will be printed (minus sign not included), leading zero's will be added if necessary */
 /* if sign is zero, only negative numbers will have the sign '-' character */
 /* if sign is one, the sign '+' or '-' character will always be printed */
 /* returns the number of characters printed */
	private int snprint_ll_number_nonlocalized(byte[] dest, int offset, long q, int minimum, int sign) {
		int flag = 0, z, i, j = offset, sz;

		long base = 1000000000000000000L;

		sz = dest.length;

		if ((sz - offset) < 1) {
			return 0;
		}

		if (minimum < 0) {
			minimum = 0;
		}

		if (minimum > 18) {
			flag = 1;
		}

		if (q < 0L) {
			dest[j++] = '-';

			q = -q;
		} else {
			if (sign != 0) {
				dest[j++] = '+';
			}
		}

		if (j == sz) {
			dest[--j] = 0;

			return (j - offset);
		}

		for (i = 19; i != 0; i--) {
			if (minimum == i) {
				flag = 1;
			}

			z = (int) (q / base);

			q %= base;

			if ((z != 0) || (flag != 0)) {
				dest[j++] = (byte) ('0' + z);

				if (j == sz) {
					dest[--j] = 0;

					return (j - offset);
				}

				flag = 1;
			}

			base /= 10L;
		}

		if (flag == 0) {
			dest[j++] = '0';
		}

		if (j == sz) {
			dest[--j] = 0;

			return (j - offset);
		}

		dest[j] = 0;

		return (j - offset);
	}

	/* minimum is the minimum digits that will be printed (minus sign not included), leading zero's will be added if necessary */
 /* if sign is zero, only negative numbers will have the sign '-' character */
 /* if sign is one, the sign '+' or '-' character will always be printed */
 /* returns the amount of characters printed */
	private int fprint_int_number_nonlocalized(RandomAccessFile file, int q, int minimum, int sign) throws IOException {
		int flag = 0, z, i, j = 0, base = 1000000000;

		if (minimum < 0) {
			minimum = 0;
		}

		if (minimum > 9) {
			flag = 1;
		}

		if (q < 0) {
			file.write('-');

			j++;

			q = -q;
		} else {
			if (sign != 0) {
				file.write('+');

				j++;
			}
		}

		for (i = 10; i != 0; i--) {
			if (minimum == i) {
				flag = 1;
			}

			z = q / base;

			q %= base;

			if ((z != 0) || (flag != 0)) {
				file.write('0' + z);

				j++;

				flag = 1;
			}

			base /= 10;
		}

		if (flag == 0) {
			file.write('0');

			j++;
		}

		return j;
	}

	/* minimum is the minimum digits that will be printed (minus sign not included), leading zero's will be added if necessary */
 /* if sign is zero, only negative numbers will have the sign '-' character */
 /* if sign is one, the sign '+' or '-' character will always be printed */
 /* returns the amount of characters printed */
	private int fprint_ll_number_nonlocalized(RandomAccessFile file, long q, int minimum, int sign) throws IOException {
		int flag = 0, z, i, j = 0;

		long base = 1000000000000000000L;

		if (minimum < 0) {
			minimum = 0;
		}

		if (minimum > 18) {
			flag = 1;
		}

		if (q < 0L) {
			file.write('-');

			j++;

			q = -q;
		} else {
			if (sign != 0) {
				file.write('+');

				j++;
			}
		}

		for (i = 19; i != 0; i--) {
			if (minimum == i) {
				flag = 1;
			}

			z = (int) (q / base);

			q %= base;

			if ((z != 0) || (flag != 0)) {
				file.write('0' + z);

				j++;

				flag = 1;
			}

			base /= 10L;
		}

		if (flag == 0) {
			file.write('0');

			j++;
		}

		return j;
	}

	private int write_tal(RandomAccessFile file) throws IOException {
		int p;

		byte[] str = new byte[total_annot_bytes];

		p = snprint_ll_number_nonlocalized(str, 0, (datarecords * long_data_record_duration + starttime_offset) / EDFLIB_TIME_DIMENSION, 0, 1);
		if (((long_data_record_duration % EDFLIB_TIME_DIMENSION) != 0) || (starttime_offset != 0)) {
			str[p++] = '.';
			p += snprint_ll_number_nonlocalized(str, p, (datarecords * long_data_record_duration + starttime_offset) % EDFLIB_TIME_DIMENSION, 7, 0);
		}
		str[p++] = 20;
		str[p++] = 20;
		for (; p < total_annot_bytes; p++) {
			str[p] = 0;
		}
		file.write(str);

		return 0;
	}

	private int strlen(byte[] str) {
		int i;

		for (i = 0; i < str.length; i++) {
			if (str[i] == 0) {
				return i;
			}
		}

		return i;
	}

	private int strcpy(byte[] dest, byte[] src) {
		int i, sz, srclen;

		sz = dest.length - 1;

		srclen = strlen(src);

		if (srclen > sz) {
			srclen = sz;
		}

		if (srclen < 0) {
			return 0;
		}

		for (i = 0; i < srclen; i++) {
			dest[i] = src[i];
		}

		dest[srclen] = 0;

		return srclen;
	}

	private int strcat(byte[] dst, byte[] src) {
		int i, sz, srclen, dstlen;

		dstlen = strlen(dst);

		sz = dst.length;

		sz -= dstlen + 1;

		if (sz <= 0) {
			return dstlen;
		}

		srclen = strlen(src);

		if (srclen > sz) {
			srclen = sz;
		}

		for (i = 0; i < srclen; i++) {
			dst[i + dstlen] = src[i];
		}

		dst[dstlen + srclen] = 0;

		return (dstlen + srclen);
	}

	private void latin1_to_ascii(byte[] str, int len) {
		int i, value;

		if (len > str.length) {
			len = str.length;
		}

		for (i = 0; i < len; i++) {
			value = str[i];

			if (value < 0) {
				value += 256;
			}

			if ((value > 31) && (value < 127)) {
				continue;
			}

			switch (value) {
				case 128:
					str[i] = 'E';
					break;

				case 130:
					str[i] = ',';
					break;

				case 131:
					str[i] = 'F';
					break;

				case 132:
					str[i] = '\"';
					break;

				case 133:
					str[i] = '.';
					break;

				case 134:
					str[i] = '+';
					break;

				case 135:
					str[i] = '+';
					break;

				case 136:
					str[i] = '^';
					break;

				case 137:
					str[i] = 'm';
					break;

				case 138:
					str[i] = 'S';
					break;

				case 139:
					str[i] = '<';
					break;

				case 140:
					str[i] = 'E';
					break;

				case 142:
					str[i] = 'Z';
					break;

				case 145:
					str[i] = '`';
					break;

				case 146:
					str[i] = '\'';
					break;

				case 147:
					str[i] = '\"';
					break;

				case 148:
					str[i] = '\"';
					break;

				case 149:
					str[i] = '.';
					break;

				case 150:
					str[i] = '-';
					break;

				case 151:
					str[i] = '-';
					break;

				case 152:
					str[i] = '~';
					break;

				case 154:
					str[i] = 's';
					break;

				case 155:
					str[i] = '>';
					break;

				case 156:
					str[i] = 'e';
					break;

				case 158:
					str[i] = 'z';
					break;

				case 159:
					str[i] = 'Y';
					break;

				case 171:
					str[i] = '<';
					break;

				case 180:
					str[i] = '\'';
					break;

				case 181:
					str[i] = 'u';
					break;

				case 187:
					str[i] = '>';
					break;

				case 191:
					str[i] = '?';
					break;

				case 192:
					str[i] = 'A';
					break;

				case 193:
					str[i] = 'A';
					break;

				case 194:
					str[i] = 'A';
					break;

				case 195:
					str[i] = 'A';
					break;

				case 196:
					str[i] = 'A';
					break;

				case 197:
					str[i] = 'A';
					break;

				case 198:
					str[i] = 'E';
					break;

				case 199:
					str[i] = 'C';
					break;

				case 200:
					str[i] = 'E';
					break;

				case 201:
					str[i] = 'E';
					break;

				case 202:
					str[i] = 'E';
					break;

				case 203:
					str[i] = 'E';
					break;

				case 204:
					str[i] = 'I';
					break;

				case 205:
					str[i] = 'I';
					break;

				case 206:
					str[i] = 'I';
					break;

				case 207:
					str[i] = 'I';
					break;

				case 208:
					str[i] = 'D';
					break;

				case 209:
					str[i] = 'N';
					break;

				case 210:
					str[i] = 'O';
					break;

				case 211:
					str[i] = 'O';
					break;

				case 212:
					str[i] = 'O';
					break;

				case 213:
					str[i] = 'O';
					break;

				case 214:
					str[i] = 'O';
					break;

				case 215:
					str[i] = 'x';
					break;

				case 216:
					str[i] = 'O';
					break;

				case 217:
					str[i] = 'U';
					break;

				case 218:
					str[i] = 'U';
					break;

				case 219:
					str[i] = 'U';
					break;

				case 220:
					str[i] = 'U';
					break;

				case 221:
					str[i] = 'Y';
					break;

				case 222:
					str[i] = 'I';
					break;

				case 223:
					str[i] = 's';
					break;

				case 224:
					str[i] = 'a';
					break;

				case 225:
					str[i] = 'a';
					break;

				case 226:
					str[i] = 'a';
					break;

				case 227:
					str[i] = 'a';
					break;

				case 228:
					str[i] = 'a';
					break;

				case 229:
					str[i] = 'a';
					break;

				case 230:
					str[i] = 'e';
					break;

				case 231:
					str[i] = 'c';
					break;

				case 232:
					str[i] = 'e';
					break;

				case 233:
					str[i] = 'e';
					break;

				case 234:
					str[i] = 'e';
					break;

				case 235:
					str[i] = 'e';
					break;

				case 236:
					str[i] = 'i';
					break;

				case 237:
					str[i] = 'i';
					break;

				case 238:
					str[i] = 'i';
					break;

				case 239:
					str[i] = 'i';
					break;

				case 240:
					str[i] = 'd';
					break;

				case 241:
					str[i] = 'n';
					break;

				case 242:
					str[i] = 'o';
					break;

				case 243:
					str[i] = 'o';
					break;

				case 244:
					str[i] = 'o';
					break;

				case 245:
					str[i] = 'o';
					break;

				case 246:
					str[i] = 'o';
					break;

				case 247:
					str[i] = '-';
					break;

				case 248:
					str[i] = '0';
					break;

				case 249:
					str[i] = 'u';
					break;

				case 250:
					str[i] = 'u';
					break;

				case 251:
					str[i] = 'u';
					break;

				case 252:
					str[i] = 'u';
					break;

				case 253:
					str[i] = 'y';
					break;

				case 254:
					str[i] = 't';
					break;

				case 255:
					str[i] = 'y';
					break;

				default:
					str[i] = ' ';
					break;
			}
		}
	}

}

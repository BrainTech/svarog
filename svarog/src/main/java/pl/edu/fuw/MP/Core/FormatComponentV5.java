package pl.edu.fuw.MP.Core;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class FormatComponentV5 {
	public static final int COMMENT_SEGMENT_IDENTITY=1,
							TEXT_INFO=1,
							FILE_HEADER=2,
							WEB_SITE_INFO=3,
							DATE_INFO=4;
	public static final int SIGNAL_INFO=5,
							DECOMP_INFO=6;
	public static final int OFFSET_SEGMENT_IDENTITY=7,
							SIGNAL_SEGMENT_IDENTITY=8,
							ATOMS_SEGMENT_IDENTITY =9;
	public int  type;

	public void Read(DataArrayInputStream stream)
	throws IOException {
		;
	}

	public void Read(DataArrayInputStream stream,int size)
	throws IOException {
		;
	}

	public abstract void Write(DataOutputStream stream) throws IOException;

	public String toString() {
		return "";
	}

	public void writeHeader(DataOutputStream stream) throws IOException {
		stream.writeByte(type);
		stream.writeByte(getSize());
	}

	public abstract int getSize();

	public static String toName(int code) {
		switch (code) {
		case TEXT_INFO:
			return "TEXT_INFO/COMMENT_SEGMENT_IDENTITY";
		case FILE_HEADER:
			return "FILE_HEADER";
		case WEB_SITE_INFO:
			return "WEB_SITE_INFO";
		case DATE_INFO:
			return "DATE_INFO";
		case SIGNAL_INFO:
			return "SIGNAL_INFO";
		case DECOMP_INFO:
			return "DECOMP_INFO";
		case OFFSET_SEGMENT_IDENTITY:
			return "OFFSET_SEGMENT_IDENTITY";
		case SIGNAL_SEGMENT_IDENTITY:
			return "SIGNAL_SEGMENT_IDENTITY";
		case ATOMS_SEGMENT_IDENTITY:
			return "ATOMS_SEGMENT_IDENTITY";
		}
		return "";
	}
}

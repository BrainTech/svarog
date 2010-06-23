package pl.edu.fuw.MP;

import java.util.Enumeration;
import java.util.Vector;

import org.signalml.domain.book.BookFormatException;
import org.signalml.domain.book.StandardBook;
import org.signalml.domain.book.StandardBookSegment;

import pl.edu.fuw.MP.Core.*;

public class MPBookStore implements StandardBook {
	private BookLibraryInterface library=null;

	public boolean Open(String filename) throws BookFormatException {
		if(library!=null) {
			library.Close();
			library=null;
		}

		switch(NewBookLibrary.checkFormat(filename)) {
		case NewBookLibrary.VERSION_III:
			Utils.log("Book version: III");
			library=new BookLibrary();
			break;
		case NewBookLibrary.VERSION_IV:
			Utils.log("Book version: IV");
			library=new BookLibrary();
			break;
		case NewBookLibrary.VERSION_V:
			Utils.log("Book version V");
			library=new BookLibraryV5();
			break;
		default:
			Utils.log("Book version: II");
		library=new BookLibrary();
		break;
		}

		return library.Open(filename, 0);
	}

	public void close() {
		library.Close();
	}

	public boolean Next() {
		return library.NextBook();
	}

	/*
	public BookAtom []getAtoms() {
		return library.getAtoms();
	}
	 */


	public float[] getSignal() {
		return library.getSignal();
	}

	public String getString() {
		return library.getString();
	}

	public float getSamplingFrequency() {
		return library.getSamplingFreq();
	}

	public float getCalibration(){
		return library.getConvFactor();
	}

	public int getChannel() {
		return library.getChannel();
	}

	public String getBookComment() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getString();
		}
		return null;
	}

	public int getChannelCount() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getMaxChannel();
		}
		return 0;
	}

	public String getChannelLabel(int channelIndex) {
		return null;
	}

	public String getDate() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getDate();
		} else if(library instanceof BookLibrary) {
			return ((BookLibrary)library).getDate();
		}
		return null;
	}

	public int getDictionarySize() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getDictionarySize();
		} else if(library instanceof BookLibrary) {
			return ((BookLibrary)library).getDictionarySize();
		}
		return 0;
	}

	public char getDictionaryType() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getDictionaryType();
		} else if(library instanceof BookLibrary) {
			return ((BookLibrary)library).getDictionaryType();
		}
		return '\0';
	}

	public float getEnergyPercent() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getEnergyPercent();
		} else if(library instanceof BookLibrary) {
			return ((BookLibrary)library).getEnergyPercent();
		}
		return 0;
	}

	public int getMaxIterationCount() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getMaxNumberOfIteration();
		} else if(library instanceof BookLibrary) {
			return ((BookLibrary)library).getMaxNumberOfIteration();
		}
		return 0;
	}

	public Object getProperty(String name) throws IllegalArgumentException {
		throw new IllegalArgumentException("No properties"); 
	}

	public Enumeration<String> getPropertyNames() {
		Vector<String> names = new Vector<String>();
		return names.elements(); 
	}

	public StandardBookSegment[] getSegmentAt(int segmentIndex) {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getCurrentSegment(segmentIndex+1);
		} else if(library instanceof  BookLibrary) {
			return ((BookLibrary)library).getCurrentSegment(segmentIndex+1);
		}
		return null;
	}

	public StandardBookSegment getSegmentAt(int segmentIndex, int channelIndex) {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getCurrentSegment(segmentIndex+1, channelIndex+1);
		}
		return null;
	}

	public int getSegmentCount() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getSegmentCount();
		}
		return 0;
	}

	public int getSignalChannelCount() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getMaxChannel();
		}
		return 0;
	}

	public String getTextInfo() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getText();
		} else if(library instanceof BookLibrary) {
			return ((BookLibrary)library).getText();
		}
		return null;
	}

	public String getVersion() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getVersion();
		} 
		return null;
	}

	public String getWebSiteInfo() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getWebSize();
		}
		return null;
	}

	public Integer getDimBase() {
		if(library instanceof BookLibraryV5) {
			return ((BookLibraryV5)library).getDimBase();
		} else if(library instanceof BookLibrary) {
			return null;
		}
		return null;
	}

}

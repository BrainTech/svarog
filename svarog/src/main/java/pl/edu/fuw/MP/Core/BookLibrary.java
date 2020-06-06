/** 1998 10 07/09/14/20/22 1998 11 30
 *  1999 06 29 - JDK 1.1
 *  1999 07 23 - nowy format ksiazek
 *  1999 07 30
 *  1999 10 03
 *  1999 11 22
 *  2000 02 18
 */

package pl.edu.fuw.MP.Core;

import java.io.*;
import org.signalml.domain.book.StandardBookSegment;

public class BookLibrary implements BookLibraryInterface {
	protected NewBookLibrary   newLib=new NewBookLibrary();
	protected BookHeader       head=new BookHeader();
	protected BookAtom         atoms[]=null;
	protected RandomAccessFile file=null;
	protected float            SecPP=20.0F;
	protected int              BookNo=0;
	protected boolean          isNewMode;
	private   String           fileString;
	protected int              MaxBookNumber;

	private void convertPhase() {
		float df=(float)(2.0*Math.PI/head.signal_size);
		for (BookAtom atom : atoms) {
			if (atom.scale != 0) {
				float freq = df * atom.frequency;
				atom.phase = Utils.HmppPhase(freq, atom.position, atom.phase);
				atom.truePhase = Utils.RawPhase(freq, atom.position, atom.phase);
			}
		}
	}

	public String getDate() {
		return isNewMode ? newLib.getDate() : null;
	}

	public int getDictionarySize() {
		return isNewMode ? newLib.getDictionarySize() : -1;
	}

	public int getSignalSize() {
		return isNewMode ? newLib.getDimBase() : head.signal_size;
	}

	public float getSamplingFreq() {
		return isNewMode ? newLib.getSamplingFreq() : head.FREQUENCY;
	}

	public float getConvFactor() {
		return isNewMode ? newLib.getConvFactor() : head.points_per_micro_V;
	}

	public String getString() {
		return isNewMode ? newLib.getString() : head.getString();
	}

	public int getMaxBookNumber() {
		return isNewMode ? newLib.getMaxBookNumber() : MaxBookNumber;
	}

	public char getDictionaryType() {
		return isNewMode ? newLib.getDictionaryType() : '\0';
	}

	public float getEnergyPercent() {
		return isNewMode ? newLib.getEnergyPercent() : -1.0F;
	}

	public int getMaxNumberOfIteration() {
		return isNewMode ? newLib.getMaxNumberOfIteration() : -1;
	}

	public String getText() {
		return isNewMode ? newLib.getText() : null;
	}

	public int getNumOfAtoms() {
		return getMaxBookNumber();
	}

	public BookAtom []getAtoms() {
		return atoms;
	}

	public int getChannel() {
		return 1;
	}

	private void importAtoms() {
		newLib.export(head);
		float df=(float)(2.0*Math.PI/head.signal_size);
		int size;

		atoms=new BookAtom[size=newLib.getNumOfAtoms()];
		for (int i=0 ; i<size ; i++) {
			atoms[i]=new BookAtom();
			newLib.export(atoms[i],i);
			atoms[i].truePhase=Utils.MppPhase(df*atoms[i].frequency,
											  atoms[i].position,
											  atoms[i].phase);
			atoms[i].number_of_atom_in_book=(short)(i+1);
		}
	}

	private boolean SetOffset(int Offset) {
		if (Offset==BookNo)
			return true;

		if (isNewMode) {
			if (!newLib.SetOffset(fileString,Offset)) {
				return false;
			}
		} else {
			return SetOldOffset(Offset);
		}
		BookNo=Offset;
		return true;
	}

	private int countBook() {
		if (isNewMode) {
			return newLib.countBook(fileString);
		} else {
			RandomAccessFile file=null;
			int k=0;

			try {
				file=new RandomAccessFile(fileString, "r");
			} catch (IOException e) {
				// [MD] automatic NPE
				// try { file.close(); } catch(Exception ee) { ; }
				return 0;
			}

			try {
				for (k=0 ; ; k++) {
					head.Read(file);
					file.skipBytes(head.book_size*20);
				}
			} catch (IOException e) {
				try {
					file.close();
				} catch (Exception ee) {
					;
				}
				return k;
			}
		}
	}

	private boolean SetOldOffset(int Offset) {
		try {
			Close();
			file=new RandomAccessFile(fileString, "r");
			for (int i=0 ; i<Offset ; i++) {
				head.Read(file);
				file.skipBytes(head.book_size*20);
			}
		} catch (IOException e) {
			return false;
		}
		BookNo=Offset;
		return true;
	}

	public boolean GoTo(int Offset) {
		if (Offset==BookNo) {
			return true;
		}

		if (isNewMode) {
			if (!newLib.loadBook(fileString,Offset)) {
				return false;
			}
		} else {
			return OldGoTo(Offset);
		}

		importAtoms();
		BookNo=Offset;
		return true;
	}

	public boolean OldGoTo(int Offset) {
		if (!SetOffset(Offset)) {
			return false;
		}
		try {
			head.Read(file);
			atoms=new BookAtom[head.book_size];
			byte buffor[]=new byte[head.book_size*20];
			DataArrayInputStream bfile=new DataArrayInputStream(file,buffor);

			for (int i=0 ; i<head.book_size ; i++) {
				(atoms[i]=new BookAtom()).Read(bfile);
				atoms[i].index=i;
			}
			convertPhase();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public void SetSecPP(float secPP_) {
		SecPP=secPP_;
	}

	public boolean NextBook() {
		if (isNewMode) {
			if (!newLib.readNextBook())
				return false;
		} else {
			return OldNextBook();
		}

		importAtoms();
		BookNo++;
		return true;
	}

	public boolean OldNextBook() {
		try {
			head.Read(file);
			atoms=new BookAtom[head.book_size];
			byte buffor[]=new byte[head.book_size*20];
			DataArrayInputStream bfile=new DataArrayInputStream(file,buffor);

			head.reset();
			for (int i=0 ; i<head.book_size ; i++) {
				(atoms[i]=new BookAtom()).Read(bfile);
				atoms[i].index=i;
				head.addAtom(atoms[i]);
			}
			convertPhase();
			BookNo++;
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private void readFirst() throws IOException {
		head.Read(file);
		atoms=new BookAtom[head.book_size];
		byte buffor[]=new byte[head.book_size*20];
		DataArrayInputStream bfile=new DataArrayInputStream(file,buffor);

		head.reset();
		for (int i=0 ; i<head.book_size ; i++) {
			(atoms[i]=new BookAtom()).Read(bfile);
			atoms[i].index=i;
			atoms[i].number_of_atom_in_book=(short)(i+1);
			head.addAtom(atoms[i]);
		}
		convertPhase();
	}

	public boolean Open(String filename,int Offset) {
		int rc;
		if ((rc=NewBookLibrary.checkFormat(filename))!=NewBookLibrary.VERSION_NONE) {
			Utils.log("VERSION: "+rc);
			isNewMode=true;
			fileString=filename;
			if (newLib.Open(filename,Offset)) {
				importAtoms();
				return true;
			}
		} else {
			isNewMode=false;
			return OldOpen(filename,Offset);
		}
		return false;
	}

	public boolean OldOpen(String filename,int Offset) {
		try {
			fileString=filename;

			Utils.log("OldOpen");
			file=new RandomAccessFile(filename, "r");

			if (!SetOffset(Offset)) {
				file.close();
				file=null;
				Utils.log("SetOffset failed!");
				return false;
			}

			readFirst();
			MaxBookNumber=countBook();
		} catch (IOException e) {
			Utils.log(e.toString());
			return false;
		}
		return true;
	}

	public void Close() {
		try {
			if (file!=null) {
				file.close();
			}
		} catch (IOException e) {
			;
		}
	}

	/** Nie wspierane (zachowane ze wzgledu na interface) */
	public float []getSignal() {
		return null;
	}

	public StandardBookSegment[] getCurrentSegment(int segmentIndex) {
		if (GoTo(segmentIndex)) {
			StandardBookSegment arr[]=new StandardBookSegment[1];
			arr[0]=head;
			return arr;
		} else {
			return null;
		}
	}
}


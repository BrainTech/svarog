package pl.edu.fuw.MP.Core;

import org.signalml.domain.book.BookFormatException;

public interface BookLibraryInterface {
        public void Close();
        public boolean Open(String filename, int off) throws BookFormatException;
        public boolean NextBook();
        public int getChannel();
       // public BookAtom []getAtoms();
        public float []getSignal();
        public String getString();
        public float getSamplingFreq();
        public float getConvFactor();
        public int getSignalSize();
}

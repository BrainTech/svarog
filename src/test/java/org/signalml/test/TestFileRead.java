/* TestFileRead.java created 2007-10-13
 *
 */
package org.signalml.test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

/** TestFileRead
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TestFileRead {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File f = new File("d:\\temp\\test");

		final Thread thread = Thread.currentThread();

		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				thread.interrupt();
			}
		};

		Timer t = new Timer(true);
		t.schedule(tt, 10*1000);

		try {

			RandomAccessFile rf = new RandomAccessFile(f,"r");
			String line;

			long filePointer = 0;
			boolean end = false;
			do {

				long fileLength = f.length();

				if (fileLength > filePointer) {

					do {

						line = rf.readLine();
						if (line != null) {
							System.out.println(line);
							if (line.equals("end")) {
								end = true;
							}
						}

					} while (line != null);

					filePointer = rf.getFilePointer();
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
					end = true;
				}

			} while (!end);

		} catch (IOException ex) {
			ex.printStackTrace(System.err);
			System.exit(1);
		}

	}

}

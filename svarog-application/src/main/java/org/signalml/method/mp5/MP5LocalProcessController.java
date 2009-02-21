/* MP5LocalProcessController.java created 2008-02-18
 * 
 */

package org.signalml.method.mp5;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.util.ResolvableString;

/** MP5LocalProcessController
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5LocalProcessController {

	protected static final Logger logger = Logger.getLogger(MP5LocalProcessController.class);
	
	private static final String ERROR = "ERROR";
	private static final Pattern TESTED_MESSAGE_PATTERN = Pattern.compile("^TESTED\\s*([0-9]+)$$");
	private static final Pattern ATOM_MESSAGE_PATTERN = Pattern.compile("^ATOM\\s*([0-9]+)\\s*([0-9]+)\\s*([0-9.]+)\\s*([0-9.]+)$" );
	private static final Pattern CHANNEL_MESSAGE_PATTERN = Pattern.compile("^CHANNEL\\s*([0-9]+)$");
	private static final Pattern OFFSET_MESSAGE_PATTERN = Pattern.compile("^OFFSET\\s*([0-9]+)$");
	private static final Pattern START_MESSAGE_PATTERN = Pattern.compile("^START\\s*([0-9]+)\\s*([0-9]+)\\s*(-?[0-9]+)\\s*(-?[0-9.]+)$");
	private static final Pattern END_MESSAGE_PATTERN = Pattern.compile("^END$");

	public boolean executeProcess( File workingDirectory, String mp5ExecutablePath, File configFile, MethodExecutionTracker tracker ) throws ComputationException {
		
		ProcessBuilder pb = new ProcessBuilder();
		pb.command(
				mp5ExecutablePath,
				"-x",
				configFile.getAbsolutePath()
		);
		pb.directory(workingDirectory);
		pb.redirectErrorStream(true);
		
		logger.debug( "Using mp5 working directory [" + pb.directory().getAbsolutePath() + "]" );
		List<String> command = pb.command();
		for( String s : command ) {
			logger.debug( "Using mp5 command [" + s + "]" );
		}
		
		Process process;
		try {
			process = pb.start();
			logger.debug("Process started");
		} catch (IOException ex) {
			throw new ComputationException(ex);
		}
				
		tracker.setMessage( new ResolvableString("mp5Method.message.calculating") );
		
		BufferedReader feedbackReader = null;
		try {
			
			feedbackReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 1 );
			String line;
			boolean msgUnderstood;
			
			do {
				line = feedbackReader.readLine(); // XXX this may prevent prompt abort, rethink
				logger.debug("Process returned line [" + (line != null ? line : "(null)") + "]" );
				if( line != null && !line.isEmpty() ) {
					line = line.trim();
					if( !line.isEmpty() ) {
						try {
							msgUnderstood = processMessage(line, tracker);
						} catch( ComputationException ex ) {
							logger.debug( "Execution exception" );
							process.destroy();
							try {
								process.waitFor();
							} catch (InterruptedException ex2) {
								// ignore
							}
							logger.debug( "Process terminated" );
							throw ex;
						}
						if( !msgUnderstood ) {
							logger.warn("Message line not understood [" + line + "]" );
						}
					}
				}
				if( tracker.isRequestingAbort() || tracker.isRequestingSuspend() ) {
					logger.debug("Termination request received");
					process.destroy();
					try {
						process.waitFor();
					} catch (InterruptedException ex2) {
						// ignore
					}
					logger.debug( "Process terminated" );
					return false;
				}	
			} while( line != null );
			
		} catch(IOException ex) {
			logger.error("IOException reading from report stream", ex);
			throw new ComputationException(ex);
		} finally { 
			if( feedbackReader != null ) {
				try {
					feedbackReader.close();
				} catch (IOException ex) {
				}
			}
		}
		
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			// ignore
		}
		
		// end of the process (should we ignore the last message)
		int exitValue = process.exitValue();
		logger.debug("Process may have finished, exit code is [" + exitValue + "]" );
		if( exitValue != 0 ) {
			logger.warn("MP5 process exited with an error [" + exitValue + "]");
			throw new ComputationException("error.mp5.exitedWithErrorCode", new Object[] { exitValue } );
		}
		
		return true;
		
	}
	
	private boolean processMessage(String line, MethodExecutionTracker tracker) throws ComputationException {
		
		if( line.startsWith( ERROR ) ) {
			
			String[] parts = line.split("\\s+");
			if( parts.length < 2 ) {
				return false;
			}
			
			String[] args = Arrays.copyOfRange(parts, 2, parts.length);
			
			throw new ComputationException( parts[1], args );
			
		}

		Matcher matcher;
		
		matcher = TESTED_MESSAGE_PATTERN.matcher(line);
		if( matcher.matches() ) {
			
			int done = Integer.parseInt( matcher.group(1) );
			tracker.setTicker(3, done);
			
			return true;
		}
		
		matcher = ATOM_MESSAGE_PATTERN.matcher(line);
		if( matcher.matches() ) {
			
			double done;
			done = Double.parseDouble( matcher.group(4) );
			int dictionaryCnt = Integer.parseInt( matcher.group(2) );

			synchronized( tracker ) {
				tracker.setTicker(2, (int) Math.round( done * 100) );
				tracker.setTickerLimit(3, dictionaryCnt);
				tracker.setTicker(3, 0);
			}
			
			return true;

		}
		
		matcher = OFFSET_MESSAGE_PATTERN.matcher(line);
		if( matcher.matches() ) {
			// do nothing
			return true;
		}

		matcher = CHANNEL_MESSAGE_PATTERN.matcher(line);
		if( matcher.matches() ) {
			int channel = Integer.parseInt( matcher.group(1) );
			tracker.setTicker(1, channel);
			return true;
		}
		
		matcher = START_MESSAGE_PATTERN.matcher(line);
		if( matcher.matches() ) {
			
			int channelCount = Integer.parseInt( matcher.group(2) );

			synchronized( tracker ) {
				int[] limits = tracker.getTickerLimits();
				tracker.setTickerLimits(new int[] { limits[0], channelCount, 100 * 100, 1 } );
				int[] tickers = tracker.getTickers();
				tracker.setTickers(new int[] { tickers[0], 0, 0, 0 } );
			}
						
			return true;
		
		}

		matcher = END_MESSAGE_PATTERN.matcher(line);
		if( matcher.matches() ) {

			synchronized( tracker ) {
				int[] tickerLimits = tracker.getTickerLimits();
				int[] tickers = tracker.getTickers();
				tracker.setTickers(new int[] { tickers[0], tickerLimits[1], tickerLimits[2], tickerLimits[3] } );
			}
			
			return true;
		}
				
		return false;
		
	}
		
}

// 1999 10 25

package pl.edu.fuw.MP.Core;

import org.apache.log4j.Logger;

public class Utils {
	private static final Logger logger = Logger.getLogger(Utils.class);
	public static boolean loggingFlag = true;

	public static float HmppPhase(float freq,float position,float phase) {
		double pi2=2.0*Math.PI;
		double RawPhase=(phase<0.0) ? pi2+phase : phase;

		RawPhase+=freq*position;
		return (float)(RawPhase-pi2*Math.floor(RawPhase/pi2));
	}

	public static float MppPhase(float freq,float position,float phase) {
		phase=HmppPhase(freq,position,phase);
		double RawPhase=phase-freq*position,pi2=2.0*Math.PI;
		double NewPhase=RawPhase-pi2*Math.floor(RawPhase/pi2);

		NewPhase=(NewPhase>=Math.PI) ? NewPhase-pi2 : NewPhase;
		NewPhase-=freq*position;
		return (float)(NewPhase-pi2*Math.floor(NewPhase/pi2));
	}

	public static float RawPhase(float freq,float position,float phase) {
		double NewPhase=phase-freq*position,pi2=2.0*Math.PI;
		return (float)(NewPhase-pi2*Math.floor(NewPhase/pi2));
	}

	public static void log(String text) {
		if (Utils.loggingFlag) {
			if (text.contains("Exception")) {
				logger.error(text);
				throw new RuntimeException();
			} else {
				logger.debug(text);
			}
		}
	}
}

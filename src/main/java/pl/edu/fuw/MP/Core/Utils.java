// 1999 10 25

package pl.edu.fuw.MP.Core;

public class Utils {
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
	  System.err.println(text);
	  if(text.indexOf("Exception")!=-1) {
		 throw new RuntimeException();
	  }

  }
}

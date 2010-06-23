/* 1999 08 01 */

package pl.edu.fuw.MP.Core;

public class MPParameters {
	public static final char PSEUDOMALLAT='M',DYADIC='D',STOCHASTIC='S';
	public int   MaxNumberOfIteration;
	public float EnergyEps;
	public int   DictionarySize;
	public char  DictionaryType;
	public float SamplingFrequency;
	public float ConvFactor;
	public boolean newSeed;
	public int   DimBase;

	public MPParameters() {
		MaxNumberOfIteration=32;
		EnergyEps=95.0F;
		DictionarySize=70000;
		SamplingFrequency=1.0F;
		ConvFactor=1.0F;
		DictionaryType=DYADIC;
		newSeed=false;
		DimBase=512;
	}
}



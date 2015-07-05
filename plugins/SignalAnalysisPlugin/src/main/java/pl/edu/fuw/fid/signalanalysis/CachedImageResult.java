package pl.edu.fuw.fid.signalanalysis;

/**
 * @author ptr@mimuw.edu.pl
 */
public class CachedImageResult<P> {
	public final P preferences;
	public final double[][] result;

	public CachedImageResult(P preferences, double[][] result) {
		this.preferences = preferences;
		this.result = result;
	}
}

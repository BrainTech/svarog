package pl.edu.fuw.fid.signalanalysis.waveform;

/**
 * Time-frequency map with a set of preferences used to compute it.
 *
 * @author ptr@mimuw.edu.pl
 */
public class CachedImageResult<P> {
	public final P preferences;
	public final ImageResult result;

	public CachedImageResult(P preferences, ImageResult result) {
		this.preferences = preferences;
		this.result = result;
	}
}

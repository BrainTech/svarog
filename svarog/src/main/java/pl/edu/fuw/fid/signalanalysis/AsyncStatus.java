package pl.edu.fuw.fid.signalanalysis;

/**
 * Interface for managing asynchronous requests.
 *
 * @author ptr@mimuw.edu.pl
 */
public interface AsyncStatus {

	public boolean isCancelled();

	public void setProgress(double progress);

}

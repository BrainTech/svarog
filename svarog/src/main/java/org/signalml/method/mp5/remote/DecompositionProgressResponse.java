/* DecompositionProgressResponse.java created 2008-02-18
 *
 */

package org.signalml.method.mp5.remote;

import java.util.List;

import org.signalml.task.TaskStatus;

/** DecompositionProgressResponse
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DecompositionProgressResponse {

	private String status;

	private String messageCode;
	private List<String> messageArguments;

	private List<Integer> tickerLimits;
	private List<Integer> tickers;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public TaskStatus getTaskStatus() {
		if (status == null) {
			return null;
		}
		return TaskStatus.valueOf(getStatus());
	}

	public void setTaskStatus(TaskStatus status) {
		setStatus(status != null ? status.name() : null);
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public List<String> getMessageArguments() {
		return messageArguments;
	}

	public String[] getMessageArgumentsArray() {
		if (messageArguments == null) {
			return new String[0];
		}
		String[] arr = new String[messageArguments.size()];
		messageArguments.toArray(arr);
		return arr;
	}

	public void setMessageArguments(List<String> messageArguments) {
		this.messageArguments = messageArguments;
	}

	public List<Integer> getTickerLimits() {
		return tickerLimits;
	}

	public int[] getTickerLimitsArray() {
		return getIntArray(tickerLimits);
	}

	public void setTickerLimits(List<Integer> tickerLimits) {
		this.tickerLimits = tickerLimits;
	}

	public List<Integer> getTickers() {
		return tickers;
	}

	public int[] getTickersArray() {
		return getIntArray(tickers);
	}

	public void setTickers(List<Integer> tickers) {
		this.tickers = tickers;
	}

	private int[] getIntArray(List<Integer> list) {
		if (list == null) {
			return new int[0];
		}
		int[] arr = new int[list.size()];
		int cnt = 0;
		for (Integer i : list) {
			arr[cnt] = i.intValue();
			cnt++;
		}
		return arr;
	}

}

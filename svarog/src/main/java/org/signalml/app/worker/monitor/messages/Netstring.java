package org.signalml.app.worker.monitor.messages;

import java.util.StringTokenizer;

public class Netstring {

	private int length;
	private String data;
	private boolean correct;

	public Netstring() {
	}

	public Netstring(Message message) {
		this.data = message.toString();
		this.length = data.length();
		this.correct = true;
	}

	public void parseNetstring(String netstring) {
		StringTokenizer tokenizer = new StringTokenizer(netstring, ":");
		String lengthToken = tokenizer.nextToken();

		try {
			length = Integer.parseInt(lengthToken);
			data = netstring.substring(lengthToken.length() + 1,
									   lengthToken.length() + 1 + length);
			String lastCharacter = netstring.substring(netstring.length() - 1,
								   netstring.length());
			if (lastCharacter.equals(","))
				correct = true;
		} catch (Exception e) {
			correct = false;
		}
	}

	public String getData() {
		return data;
	}

	public boolean isCorrect() {
		return correct;
	}

	@Override
	public String toString() {
		return length + ":" + data + ",";
	}
}

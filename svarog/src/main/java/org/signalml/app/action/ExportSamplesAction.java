/* ExportSamplesAction.java created 2008-01-15
 *
 */

package org.signalml.app.action;

import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.util.Util;

/** ExportSamplesAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportSamplesAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	public ExportSamplesAction() {
		super();
	}

	protected abstract int getSampleCount();

	protected abstract double[][] getSamples();

	protected boolean isWithLabels() {
		return false;
	}

	protected String getLabel(int index) {
		return "";
	}

	protected String getLineSeparator() {
		return Util.LINE_SEP;
	}

	protected String getFieldSeparator() {
		return "\t";
	}

	protected String getSamplesAsString() {

		double[][] samples = getSamples();
		if (samples == null) {
			return null;
		}

		int sampleCount = getSampleCount();

		String lineSeparator = getLineSeparator();
		String fieldSeparator = getFieldSeparator();
		StringBuilder sb = new StringBuilder();
		int i;
		int e;

		if (isWithLabels()) {
			for (i=0; i<samples.length; i++) {
				if (i > 0) {
					sb.append(fieldSeparator);
				}
				sb.append(getLabel(i));
			}
			sb.append(lineSeparator);
		}

		for (e=0; e<sampleCount; e++) {
			for (i=0; i<samples.length; i++) {
				if (i > 0) {
					sb.append(fieldSeparator);
				}
				sb.append(Double.toString(samples[i][e]));
			}
			sb.append(lineSeparator);
		}

		return sb.toString();

	}

}

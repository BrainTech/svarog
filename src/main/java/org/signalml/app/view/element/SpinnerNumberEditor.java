/* SpinnerNumberEditor.java created 2007-10-04
 *
 */

package org.signalml.app.view.element;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.signalml.app.model.BoundedSpinnerModel;

/** SpinnerNumberEditor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SpinnerNumberEditor extends JSpinner.DefaultEditor {

	private static final long serialVersionUID = 1L;

	private static String getDefaultPattern(Locale locale) {
		// XXX this causes problems and is ugly
		/*
		ResourceBundle rb = LocaleData.getNumberFormatData(locale);
		String[] all = rb.getStringArray("NumberPatterns");
		return all[0];
		*/
		return "#,##0.###;-#,##0.###";
	}

	public SpinnerNumberEditor(JSpinner spinner) {
		this(spinner, getDefaultPattern(spinner.getLocale()));
	}

	public SpinnerNumberEditor(JSpinner spinner, String defaultPattern) {
		this(spinner, new DecimalFormat(defaultPattern));
	}

	public SpinnerNumberEditor(JSpinner spinner, DecimalFormat format) {
		super(spinner);
		if (!(spinner.getModel() instanceof BoundedSpinnerModel)) {
			throw new IllegalArgumentException(
			        "model not a BoundedSpinnerModel");
		}

		BoundedSpinnerModel model = (BoundedSpinnerModel) spinner.getModel();
		NumberFormatter formatter = new NumberEditorFormatter(model, format);
		DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);

		JFormattedTextField ftf = getTextField();
		ftf.setEditable(true);
		ftf.setFormatterFactory(factory);
		ftf.setHorizontalAlignment(JTextField.RIGHT);

		try {
			String maxString = formatter.valueToString(model.getMinimum());
			String minString = formatter.valueToString(model.getMaximum());
			ftf.setColumns(Math.max(maxString.length(),minString.length()));
		}
		catch (ParseException e) {

		}
	}

	private static class NumberEditorFormatter extends NumberFormatter {
		private static final long serialVersionUID = 1L;
		private final BoundedSpinnerModel model;

		NumberEditorFormatter(BoundedSpinnerModel model, NumberFormat format) {
			super(format);
			this.model = model;
			setValueClass(model.getValue().getClass());
		}

		public Comparable<? extends Number> getMinimum() {
			return  model.getMinimum();
		}

		public Comparable<? extends Number> getMaximum() {
			return model.getMaximum();
		}
	}


}

/* SpinnerNumberEditor.java created 2007-10-04
 *
 */

package org.signalml.app.view.common.components.spinners;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.signalml.app.model.components.BoundedSpinnerModel;

/**
 * Editor of numbers in the spinner, which has
 * <ul>
 * <li>the decimal format with custom or default pattern,</li>
 * <li>the width created based on the bounds of the spinner.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SpinnerNumberEditor extends JSpinner.DefaultEditor {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns the pattern used to create {@link DecimalFormat}:
	 * "#,##0.###;-#,##0.###".
	 * The format is independent from the Locale.
	 * @param locale the locale
	 * @return the pattern used to create DecimalFormat
	 */
	private static String getDefaultPattern(Locale locale) {
		// XXX this causes problems and is ugly
		/*
		ResourceBundle rb = LocaleData.getNumberFormatData(locale);
		String[] all = rb.getStringArray("NumberPatterns");
		return all[0];
		*/
		return "#,##0.###;-#,##0.###";
	}

	/**
	 * Constructor.
	 * Sets the DefaultFormatterFactory created based on {@link
	 * NumberEditorFormatter} in the text field.
	 * Uses the {@link #getDefaultPattern(Locale) default pattern}.
	 * @param spinner the spinner for which this editor is to be used
	 */
	public SpinnerNumberEditor(JSpinner spinner) {
		this(spinner, getDefaultPattern(spinner.getLocale()));
	}

	/**
	 * Constructor.
	 * Sets the DefaultFormatterFactory created based on {@link
	 * NumberEditorFormatter} in the text field.
	 * @param spinner the spinner for which this is used
	 * @param defaultPattern the pattern used to created the format of the
	 * numbers shown in the spinner
	 */
	public SpinnerNumberEditor(JSpinner spinner, String defaultPattern) {
		this(spinner, new DecimalFormat(defaultPattern));
	}

	/**
	 * Constructor.
	 * Sets the DefaultFormatterFactory created based on {@link
	 * NumberEditorFormatter} in the text field.
	 * @param spinner the spinner for which this editor is used
	 * @param format the format of the numbers shown in the spinner
	 */
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
		} catch (ParseException e) {

		}
	}

	/**
	 * Number formatter with two parameters:
	 * <ul>
	 * <li>the format of numbers,</li>
	 * <li>the {@link BoundedSpinnerModel bounded model}, which is used to
	 * obtain limits - maximum and minimum.</li></ul>
	 *
	 */
	private static class NumberEditorFormatter extends NumberFormatter {
		private static final long serialVersionUID = 1L;

		/**
		 * the {@link BoundedSpinnerModel model} for the spinner
		 */
		private final BoundedSpinnerModel model;

		/**
		 * Constructor. Sets the format of numbers and the model from which
		 * limits (minimum and maximum) are taken.
		 * @param model the model
		 * @param format the format of numbers
		 */
		NumberEditorFormatter(BoundedSpinnerModel model, NumberFormat format) {
			super(format);
			this.model = model;
			setValueClass(model.getValue().getClass());
		}

		@Override
		public Comparable<? extends Number> getMinimum() {
			return  model.getMinimum();
		}

		@Override
		public Comparable<? extends Number> getMaximum() {
			return model.getMaximum();
		}
	}


}

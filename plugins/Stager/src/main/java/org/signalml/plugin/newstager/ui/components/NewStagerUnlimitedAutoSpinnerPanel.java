package org.signalml.plugin.newstager.ui.components;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.common.components.panels.UnlimitedSpinnerPanel;

public class NewStagerUnlimitedAutoSpinnerPanel extends UnlimitedSpinnerPanel {

	public static final Object AUTO_VALUE = new Serializable() {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public String toString() {
			return _("auto");
		}

	};

	private static final long serialVersionUID = -8263779708483751580L;

	private class _SpinnerAutoModel extends SpinnerNumberModel {

		private static final long serialVersionUID = 1L;

		private Number autoValue;
		private boolean isAuto;

		public _SpinnerAutoModel(SpinnerNumberModel instrumentedModel, Number autoValue, boolean isAuto) {
			super((Number) instrumentedModel.getValue(),
				  instrumentedModel.getMinimum(), instrumentedModel.getMaximum(),
				  instrumentedModel.getStepSize());
			this.autoValue = autoValue;
			this.isAuto = isAuto;
		}

		@Override
		public Object getValue() {
			if (this.isAuto) {
				return NewStagerUnlimitedAutoSpinnerPanel.AUTO_VALUE;
			}
			return super.getValue();
		}

		@Override
		public void setValue(Object value) {
			if (value == NewStagerUnlimitedAutoSpinnerPanel.AUTO_VALUE) {
				if (!this.isAuto) {
					this.setAuto();
				}
				return;
			}
			boolean needsUpdate = false;
			if (this.isAuto) {
				this.isAuto = false;
				needsUpdate = true;
			}
			super.setValue(value);
			if (needsUpdate) {
				this.fireStateChanged();
			}
		}

		@Override
		public Object getNextValue() {
			if (this.isAuto) {
				this.setValue(this.autoValue);
				this.isAuto = false;
			}
			return super.getNextValue();
		}

		public void setAuto() {
			this.isAuto = true;
			this.fireStateChanged();
		}

		public void setAutoValue(Number autoValue) {
			this.autoValue = autoValue;
		}

		public Number getAutoValue() {
			return this.autoValue;
		}

		public boolean isAuto() {
			return this.isAuto;
		}

	}

	private class _AutoNumberFormatter extends NumberFormatter {

		private static final long serialVersionUID = 1L;

		private SpinnerNumberModel model;

		public _AutoNumberFormatter(SpinnerNumberModel model) {
			super();
			this.model = model;
            setValueClass(model.getValue().getClass());
        }

        @Override
		public void setMinimum(Comparable min) {
            model.setMinimum(min);
        }

        @Override
		public Comparable getMinimum() {
            return  model.getMinimum();
        }

        @Override
		public void setMaximum(Comparable max) {
            model.setMaximum(max);
        }

        @Override
		public Comparable getMaximum() {
            return model.getMaximum();
        }

		@Override
		public String valueToString(Object value) throws ParseException {
			if (value == NewStagerUnlimitedAutoSpinnerPanel.AUTO_VALUE) {
				return value.toString();
			}
			return super.valueToString(value);
		}

		@Override
		public Object stringToValue(String text) throws ParseException {
			if (text.equals(NewStagerUnlimitedAutoSpinnerPanel.AUTO_VALUE.toString())) {
				return NewStagerUnlimitedAutoSpinnerPanel.AUTO_VALUE;
			}
			return super.stringToValue(text);
		}
	}

	private class _SpinnerAutoEditor extends JSpinner.NumberEditor {

		private static final long serialVersionUID = 1L;

		private SpinnerNumberModel model;

		public _SpinnerAutoEditor(JSpinner spinner) {
			super(spinner);

			this.model = (SpinnerNumberModel) spinner.getModel();

			this.setupFormatter();
		}

		@Override
		public void commitEdit() throws ParseException {
			if (this.model.getValue() != NewStagerUnlimitedAutoSpinnerPanel.AUTO_VALUE) {
				super.commitEdit();
			}
		}

		private void setupFormatter() {
			_AutoNumberFormatter formatter = new _AutoNumberFormatter(this.model);

			JFormattedTextField textField = this.getTextField();
			textField.setFormatterFactory(new DefaultFormatterFactory(formatter));
		}

	}

	private _SpinnerAutoModel proxyModel;

	protected NewStagerUnlimitedAutoSpinnerPanel(boolean compact) {
		super(compact);
	}

	public NewStagerUnlimitedAutoSpinnerPanel(double value, double min, double max, double step, double autoValue, boolean compact, boolean isAuto) {
		super(value, min, max, step, compact);
		this.instrumentSpinner(Double.valueOf(autoValue), isAuto);
	}

	public NewStagerUnlimitedAutoSpinnerPanel(float value, float min, float max, float step, float autoValue, boolean compact, boolean isAuto) {
		super(value, min, max, step, compact);
		this.instrumentSpinner(Float.valueOf(autoValue), isAuto);
	}

	public NewStagerUnlimitedAutoSpinnerPanel(int value, int min, int max, int step, int autoValue, boolean compact, boolean isAuto) {
		super(value, min, max, step, compact);
		this.instrumentSpinner(Long.valueOf(autoValue), isAuto);
	}

	@Override
	public double getValue() {
		Object value = this.getSpinner().getValue();
		if (value == NewStagerUnlimitedAutoSpinnerPanel.AUTO_VALUE) {
			return this.proxyModel.getNumber().doubleValue();
		}

		return super.getValue();
	}

	public void setAuto() {
		this.getButtonGroup().clearSelection();

		this.getSpinner().setValue(NewStagerUnlimitedAutoSpinnerPanel.AUTO_VALUE);
	}

	public void setAutoValue(double autoValue) {
		this.proxyModel.setAutoValue(Double.valueOf(autoValue));
	}

	public void setAutoValue(float autoValue) {
		this.proxyModel.setAutoValue(Float.valueOf(autoValue));
	}

	public void setAutoValue(int autoValue) {
		this.proxyModel.setAutoValue(Long.valueOf(autoValue));
	}

	public boolean isAuto() {
		return this.proxyModel.isAuto();
	}

	private void instrumentSpinner(Number autoValue, boolean isAuto) {
		JSpinner spinner = this.getSpinner();

		this.proxyModel = new _SpinnerAutoModel((SpinnerNumberModel) spinner.getModel(), autoValue, isAuto);
		spinner.setEditor(new _SpinnerAutoEditor(spinner));
		SwingUtils.replaceSpinnerModel(spinner, this.proxyModel);

		this.addListeners();

		if (isAuto) {
			this.setAuto();
		}
	}

	private void addListeners() {
		this.getSpinnerRadio().addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					if (proxyModel.isAuto()) {
						proxyModel.setValue(proxyModel.getAutoValue());
					}
				}
			}
		});

	}

}

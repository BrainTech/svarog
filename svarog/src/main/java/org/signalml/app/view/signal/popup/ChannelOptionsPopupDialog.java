package org.signalml.app.view.signal.popup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.model.ChannelPlotOptionsModel;
import org.signalml.app.model.MontageDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SwingUtils;
import org.signalml.app.view.element.TitledCrossBorder;
import org.signalml.app.view.element.TitledSliderPanel;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.popup.SlavePlotSettingsPopupDialog.EditMontageAction;
import org.signalml.app.view.signal.popup.SlavePlotSettingsPopupDialog.SynchronizeNowAction;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;
import org.springframework.context.support.MessageSourceAccessor;

public class ChannelOptionsPopupDialog extends AbstractPopupDialog implements ChangeListener {

	public ChannelOptionsPopupDialog(MessageSourceAccessor messageSource,
			Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	private static final long serialVersionUID = 1L;

	private SignalPlot currentPlot;
	private DefaultBoundedRangeModel valueScaleModel;
	private JCheckBox ignoreGlobalScale;
	private int channel;
	private ChannelPlotOptionsModel model;

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel synchronizationPanel = new JPanel();
		synchronizationPanel.setLayout(new BoxLayout(synchronizationPanel, BoxLayout.Y_AXIS));

		CompoundBorder border = new CompoundBorder(
		        new TitledCrossBorder(messageSource.getMessage("signalView.channelLocalScale"), true),
		        new EmptyBorder(3,3,3,3)
		);
		synchronizationPanel.setBorder(border);

		synchronizationPanel.add(getHorizontalLockCheckBox());
		synchronizationPanel.add(Box.createVerticalStrut(3));
		synchronizationPanel.add(getValueScaleSlider());


		interfacePanel.add(synchronizationPanel, BorderLayout.NORTH);

		return interfacePanel;

	}
	
	public void setCurrentPlot(SignalPlot plot) {
		currentPlot = plot;
	}
	
	public JPanel getHorizontalLockCheckBox() {
		ignoreGlobalScale =  new JCheckBox(messageSource.getMessage("signalView.ignoreGlobalScale"));
		JPanel p = new JPanel(new BorderLayout());
		p.add(ignoreGlobalScale, BorderLayout.NORTH);
		return p;
	}
	
	private JPanel getValueScaleSlider() {
		
		JSlider valueScaleSlider = new JSlider(new DefaultBoundedRangeModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent ev) {
				return messageSource.getMessage("signalView.valueScaleToolTip", new Object[] { getValue() });
			}
		};
		
		DefaultBoundedRangeModel m = currentPlot.getValueScaleRangeModel();
		this.valueScaleModel =  (DefaultBoundedRangeModel) valueScaleSlider.getModel();
		this.valueScaleModel.setRangeProperties(m.getValue(), m.getExtent(), m.getMinimum(), m.getMaximum(), m.getValueIsAdjusting());

		Dimension d = valueScaleSlider.getPreferredSize();
		d.width = 100;
		valueScaleSlider.setToolTipText("");
		valueScaleSlider.setPreferredSize(d);
		valueScaleSlider.setMinimumSize(d);
		valueScaleSlider.setMaximumSize(d);
		
		this.valueScaleModel.addChangeListener(this);

		TitledSliderPanel retPanel = new TitledSliderPanel(messageSource.getMessage("signalView.valueScale"), valueScaleSlider);
		JPanel p = new JPanel(new BorderLayout());
		p.add(retPanel, BorderLayout.NORTH);
		return p;
	}

	private void setInitialVoltageScale(int scale) {
		this.valueScaleModel.setValue(scale);
	}
	
	public void setChannel(int ch) {
		this.channel = ch;
	}
	
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		SignalPlot plot = (SignalPlot) model; 
		this.model = plot.getChannelsPlotOptionsModel().getChannelPlotOptionsModelAt(this.channel);
		this.setInitialVoltageScale(this.model.getVoltageScale());
		this.ignoreGlobalScale.getModel().setSelected(this.model.getIgnoreGlobalScale());
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		this.model.setIgnoreGlobalScale(this.ignoreGlobalScale.getModel().isSelected());
		
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return SignalPlot.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public boolean isFormClickApproving() {
		return true;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.valueScaleModel) {
			this.model.setVoltageScale(this.valueScaleModel.getValue());

		}
		
	}

}

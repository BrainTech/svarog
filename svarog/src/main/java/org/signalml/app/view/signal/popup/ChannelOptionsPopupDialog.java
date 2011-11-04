package org.signalml.app.view.signal.popup;

import static org.signalml.app.SvarogApplication._;
import static org.signalml.app.SvarogApplication._R;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

/*
 * A pop-up that handles single channel`s display options, eg.visibility, value scale.
 */
public class ChannelOptionsPopupDialog extends AbstractPopupDialog implements ChangeListener, ActionListener {

	public  ChannelOptionsPopupDialog(
			Window w, boolean isModal) {
		super( w, isModal);
	}

	private static final long serialVersionUID = 1L;

	/*
	 * parent`s plot (self`s model)
	 */
	private SignalPlot currentPlot;
	/*
	 * value scale model for current channel
	 */
	private DefaultBoundedRangeModel valueScaleModel;
	/*
	 * ignore-global-scale value for current channel
	 */
	private JCheckBox ignoreGlobalScale;
	/*
	 * current channel index
	 */
	private int channel;
	/*
	 * a model for all channels`es display options (eg. visibility, value scale etc.)
	 */
	private ChannelPlotOptionsModel model;

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());

		JPanel valueScalePanel = new JPanel();
		
		//value scale
		valueScalePanel.setLayout(new BoxLayout(valueScalePanel, BoxLayout.Y_AXIS));
		CompoundBorder border = new CompoundBorder(
		        new TitledBorder(_("Value scale")),
		        new EmptyBorder(3,3,3,3)
		);
		valueScalePanel.setBorder(border);
		valueScalePanel.add(getIgnoreGlobalPanel());
		valueScalePanel.add(Box.createVerticalStrut(3));
		valueScalePanel.add(getValueScalePanel());
		
		//visibility
		JPanel visibilityPanel = new JPanel();
		visibilityPanel.setLayout(new BoxLayout(visibilityPanel, BoxLayout.Y_AXIS));
		border = new CompoundBorder(
		        new TitledCrossBorder(_("Visibility"), true),
		        new EmptyBorder(3,3,3,3)
		);
		visibilityPanel.setBorder(border);
		visibilityPanel.add(getVisibilityPanel());

		
		interfacePanel.add(visibilityPanel, BorderLayout.NORTH);
		interfacePanel.add(valueScalePanel, BorderLayout.SOUTH);
		return interfacePanel;

	}
	
	public void setCurrentPlot(SignalPlot plot) {
		currentPlot = plot;
	}
	
	/*
	 * Creates and returns a component for ignoreGlobalScale checkbox.
	 * @returns JPanel with checkbox
	 */
	private JPanel getIgnoreGlobalPanel() {
		ignoreGlobalScale =  new JCheckBox(_("Ignore global scale"));
		JPanel p = new JPanel(new BorderLayout());
		p.add(ignoreGlobalScale, BorderLayout.NORTH);
		return p;
	}
	
	/*
	 * Creates and returns a component for HideChannel button.
	 * @returns JPanel with HideChannel button.
	 */
	private JPanel getVisibilityPanel() {
		JButton hideChannel =  new JButton(_("Hide"));
		hideChannel.addActionListener(this);
		JPanel p = new JPanel(new BorderLayout());
		p.add(hideChannel, BorderLayout.NORTH);
		return p;
	}
	
	/*
	 * Creates and returns an component for ValueScale scrollBar.
	 * @returns JPanel with ValueScale scrollBar.
	 */
	private JPanel getValueScalePanel() {
		
		JSlider valueScaleSlider = new JSlider(new DefaultBoundedRangeModel()) {
			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent ev) {
				return _R(("{0} %"), new Object[] { getValue() });
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

		TitledSliderPanel retPanel = new TitledSliderPanel(_("Value scale"), valueScaleSlider);
		JPanel p = new JPanel(new BorderLayout());
		p.add(retPanel, BorderLayout.NORTH);
		return p;
	}

	/*
	 * Sets scrollBar`s initial value.
	 * @param scale initial value to be set
	 */
	private void setInitialVoltageScale(int scale) {
		this.valueScaleModel.setValue(scale);
	}
	
	/*
	 * Sets channel number for which the panel will be shown.
	 * @param ch channel number for which the panel will be shown
	 */
	public void setChannel(int ch) {
		this.channel = ch;
	}
	
	/*
	 * Fills the panel with data from its parent plot. Fired on panel`s appearance.
	 * @see org.signalml.plugin.export.view.AbstractDialog#fillDialogFromModel(java.lang.Object)
	 * @param model parent`s plot
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		SignalPlot plot = (SignalPlot) model; 
		this.model = plot.getChannelsPlotOptionsModel().getModelAt(this.channel);
		if (!this.model.getVisible())
			this.model.setVisible(true);
		
		this.setInitialVoltageScale(this.model.getVoltageScale());
		this.ignoreGlobalScale.getModel().setSelected(this.model.getIgnoreGlobalScale());
	}

	/*
	 * Fills model with data from the panel. Fired on panel`s disappearance.
	 * @see org.signalml.plugin.export.view.AbstractDialog#fillModelFromDialog(java.lang.Object)
	 * @param model parent`s plot
	 */
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

	/*
	 * Fired on valueScale changed. Changes model`s value scale.
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == this.valueScaleModel) {
			this.model.setVoltageScale(this.valueScaleModel.getValue());

		}
		
	}

	/*
	 * Fired on 'hide' button pressed. Sets channel`s visibility and hides the panel.
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		//assumed 'hide' performed
		this.model.setVisible(false);
		this.getOkAction().actionPerformed(null);
		
	}

}

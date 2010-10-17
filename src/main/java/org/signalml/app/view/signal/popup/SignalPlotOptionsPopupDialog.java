/* SignalPlotOptionsPopupDialog.java created 2007-11-22
 *
 */

package org.signalml.app.view.signal.popup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.ResolvableComboBox;
import org.signalml.app.view.element.TitledCrossBorder;
import org.signalml.app.view.signal.SignalColor;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.tag.TagPaintMode;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;
import org.springframework.context.support.MessageSourceAccessor;

/** SignalPlotOptionsPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalPlotOptionsPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;

	private SignalView signalView;

	private List<Component> buttonPanelComponents = new LinkedList<Component>();

	private JComboBox tagPaintModeComboBox;
	private JComboBox signalColorComboBox;
	private JCheckBox signalXORCheckBox;

	public SignalPlotOptionsPopupDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	public SignalView getSignalView() {
		return signalView;
	}

	public void setSignalView(SignalView view) {
		this.signalView = view;
	}

	@Override
	public JComponent createInterface() {

		createButtons();

		JPanel interfacePanel = new JPanel(new BorderLayout());
		interfacePanel.setBorder(new TitledCrossBorder(messageSource.getMessage("signalView.plotOptions"), true));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(3,3,3,3));

		buttonPanel.setLayout(new GridLayout(buttonPanelComponents.size(), 1, 3, 3));

		for (Component c : buttonPanelComponents) {
			if (c instanceof JComponent) {
				((JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);
			}
			buttonPanel.add(c);
		}

		interfacePanel.add(buttonPanel, BorderLayout.WEST);

		interfacePanel.add(createSettingPanel(), BorderLayout.CENTER);

		return interfacePanel;

	}

	private JPanel createSettingPanel() {

		JPanel settingPanel = new JPanel();
		settingPanel.setBorder(new EmptyBorder(3,3,3,3));

		GroupLayout layout = new GroupLayout(settingPanel);
		settingPanel.setLayout(layout);

		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel tagPaintModeLabel = new JLabel(messageSource.getMessage("signalView.tagPaintMode"));
		JLabel signalColorLabel = new JLabel(messageSource.getMessage("signalView.signalColor"));
		JLabel signalXORLabel = new JLabel("");

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup(Alignment.LEADING)
		        .addComponent(tagPaintModeLabel)
		        .addComponent(signalColorLabel)
		        .addComponent(signalXORLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup(Alignment.TRAILING)
		        .addComponent(getTagPaintModeComboBox())
		        .addComponent(getSignalColorComboBox())
		        .addComponent(getSignalXORCheckBox())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(tagPaintModeLabel)
				.addComponent(getTagPaintModeComboBox())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(signalColorLabel)
				.addComponent(getSignalColorComboBox())
			);

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(signalXORLabel)
				.addComponent(getSignalXORCheckBox())
			);
		
		layout.setVerticalGroup(vGroup);		
		
		
		
		return settingPanel;

	}

	public JComboBox getTagPaintModeComboBox() {
		if (tagPaintModeComboBox == null) {
			tagPaintModeComboBox = new ResolvableComboBox(messageSource);
			tagPaintModeComboBox.setModel(new DefaultComboBoxModel(TagPaintMode.values()));
			tagPaintModeComboBox.setSelectedItem(signalView.getMasterPlot().getTagPaintMode());

			tagPaintModeComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for (SignalPlot plot : signalView.getPlots()) {
						plot.setTagPaintMode((TagPaintMode) tagPaintModeComboBox.getSelectedItem());
					}
				}

			});

		}
		return tagPaintModeComboBox;
	}

	public JComboBox getSignalColorComboBox() {
		if (signalColorComboBox == null) {
			signalColorComboBox = new ResolvableComboBox(messageSource);
			signalColorComboBox.setModel(new DefaultComboBoxModel(SignalColor.values()));
			signalColorComboBox.setSelectedItem(signalView.getMasterPlot().getSignalColor());

			signalColorComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for (SignalPlot plot : signalView.getPlots()) {
						plot.setSignalColor((SignalColor) signalColorComboBox.getSelectedItem());
					}
				}

			});

		}
		return signalColorComboBox;
	}

	public JCheckBox getSignalXORCheckBox() {
		if (signalXORCheckBox == null) {
			signalXORCheckBox = new JCheckBox(messageSource.getMessage("signalView.signalXOR"));
			signalXORCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
			signalXORCheckBox.setSelected(signalView.getMasterPlot().isSignalXOR());

			signalXORCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					for (SignalPlot plot : signalView.getPlots()) {
						plot.setSignalXOR(signalXORCheckBox.isSelected());
					}
				}

			});

		}
		return signalXORCheckBox;
	}

	private void createButtons() {

		SignalPlot plot = signalView.getMasterPlot();

		final JToggleButton antialiasButton = new JToggleButton(messageSource.getMessage("signalView.antialias"), IconUtils.loadClassPathIcon("org/signalml/app/icon/antialias.png"));
		antialiasButton.setToolTipText(messageSource.getMessage("signalView.antialiasToolTip"));
		antialiasButton.setSelected(plot.isAntialiased());

		final JToggleButton clampButton = new JToggleButton(messageSource.getMessage("signalView.clamp"), IconUtils.loadClassPathIcon("org/signalml/app/icon/clamp.png"));
		clampButton.setToolTipText(messageSource.getMessage("signalView.clampToolTip"));
		clampButton.setSelected(plot.isClamped());

		final JToggleButton offscreenChannelsDrawnButton = new JToggleButton(messageSource.getMessage("signalView.offscreenChannelsDrawn"));
		offscreenChannelsDrawnButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/drawoffscreen.png"));
		offscreenChannelsDrawnButton.setToolTipText(messageSource.getMessage("signalView.offscreenChannelsDrawnToolTip"));
		offscreenChannelsDrawnButton.setSelected(plot.isOffscreenChannelsDrawn());

		final JToggleButton pageLinesVisibleButton = new JToggleButton(messageSource.getMessage("signalView.pageLinesVisible"));
		pageLinesVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/pagelines.png"));
		pageLinesVisibleButton.setToolTipText(messageSource.getMessage("signalView.pageLinesVisibleToolTip"));
		pageLinesVisibleButton.setSelected(plot.isPageLinesVisible());

		final JToggleButton blockLinesVisibleButton = new JToggleButton(messageSource.getMessage("signalView.blockLinesVisible"));
		blockLinesVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/blocklines.png"));
		blockLinesVisibleButton.setToolTipText(messageSource.getMessage("signalView.blockLinesVisibleToolTip"));
		blockLinesVisibleButton.setSelected(plot.isBlockLinesVisible());

		final JToggleButton channelLinesVisibleButton = new JToggleButton(messageSource.getMessage("signalView.channelLinesVisible"));
		channelLinesVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/channellines.png"));
		channelLinesVisibleButton.setToolTipText(messageSource.getMessage("signalView.channelLinesVisibleToolTip"));
		channelLinesVisibleButton.setSelected(plot.isChannelLinesVisible());

		final JToggleButton tagToolTipsVisibleButton = new JToggleButton(messageSource.getMessage("signalView.tagToolTipsVisible"));
		tagToolTipsVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/tagtooltips.png"));
		tagToolTipsVisibleButton.setToolTipText(messageSource.getMessage("signalView.tagToolTipsVisibleToolTip"));
		tagToolTipsVisibleButton.setSelected(plot.isTagToolTipsVisible());

		antialiasButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (SignalPlot plot : signalView.getPlots()) {
					plot.setAntialiased(antialiasButton.isSelected());
				}
			}

		});

		clampButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (SignalPlot plot : signalView.getPlots()) {
					plot.setClamped(clampButton.isSelected());
				}
			}

		});

		offscreenChannelsDrawnButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (SignalPlot plot : signalView.getPlots()) {
					plot.setOffscreenChannelsDrawn(offscreenChannelsDrawnButton.isSelected());
				}
			}

		});

		pageLinesVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (SignalPlot plot : signalView.getPlots()) {
					plot.setPageLinesVisible(pageLinesVisibleButton.isSelected());
				}
			}

		});

		blockLinesVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (SignalPlot plot : signalView.getPlots()) {
					plot.setBlockLinesVisible(blockLinesVisibleButton.isSelected());
				}
			}

		});

		channelLinesVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (SignalPlot plot : signalView.getPlots()) {
					plot.setChannelLinesVisible(channelLinesVisibleButton.isSelected());
				}
			}

		});

		tagToolTipsVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (SignalPlot plot : signalView.getPlots()) {
					plot.setTagToolTipsVisible(tagToolTipsVisibleButton.isSelected());
				}
			}

		});

		buttonPanelComponents.add(pageLinesVisibleButton);
		buttonPanelComponents.add(blockLinesVisibleButton);
		buttonPanelComponents.add(channelLinesVisibleButton);
		buttonPanelComponents.add(tagToolTipsVisibleButton);
		buttonPanelComponents.add(Box.createRigidArea(new Dimension(1,1)));
		buttonPanelComponents.add(antialiasButton);
		buttonPanelComponents.add(clampButton);
		buttonPanelComponents.add(offscreenChannelsDrawnButton);

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		// XXX nothing to do but should fill dialog from signalView if plot options can change
		// from anywhere else but here (some kind of load defaults perhaps)
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// nothing to do
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
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
	public boolean isControlPanelEquipped() {
		return false;
	}

}

/* BookPlotOptionsPopupDialog.java created 2008-02-23
 *
 */
package org.signalml.app.view.book.popup;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.book.BookPlot;
import org.signalml.app.view.book.BookView;
import org.signalml.app.view.common.components.spinners.IntegerSpinner;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;

/** BookPlotOptionsPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookPlotOptionsPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;

	private BookView bookView;

	private List<Component> buttonPanelComponents = new LinkedList<>();

	private JPanel aspectRatioPanel;

	private IntegerSpinner aspectUpSpinner;
	private IntegerSpinner aspectDownSpinner;

	private JToggleButton antialiasButton;
	private JToggleButton originalSignalVisibleButton;
	private JToggleButton fullReconstructionVisibleButton;
	private JToggleButton reconstructionVisibleButton;
	private JToggleButton legendVisibleButton;
	private JToggleButton scaleVisibleButton;
	private JToggleButton axesVisibleButton;
	private JToggleButton atomToolTipsVisibleButton;

	public BookPlotOptionsPopupDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	public BookView getBookView() {
		return bookView;
	}

	public void setBookView(BookView bookView) {
		this.bookView = bookView;
	}

	@Override
	public JComponent createInterface() {

		createButtons();

		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.setBorder(new TitledBorder(_("Plot options")));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(3,3,3,3));

		buttonPanel.setLayout(new GridLayout(buttonPanelComponents.size(), 1, 3, 3));

		for (Component c : buttonPanelComponents) {
			if (c instanceof JComponent) {
				((JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);
			}
			buttonPanel.add(c);
		}

		interfacePanel.add(buttonPanel, BorderLayout.CENTER);

		JPanel rightPanel = new JPanel(new BorderLayout());

		rightPanel.add(createSettingPanel(), BorderLayout.NORTH);
		rightPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		interfacePanel.add(rightPanel, BorderLayout.EAST);

		return interfacePanel;

	}

	private JPanel createSettingPanel() {

		JPanel settingPanel = new JPanel();
		settingPanel.setBorder(new EmptyBorder(3,3,3,3));

		GroupLayout layout = new GroupLayout(settingPanel);
		settingPanel.setLayout(layout);

		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel aspectRatioLabel = new JLabel(_("Map aspect ratio"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup(Alignment.LEADING)
			.addComponent(aspectRatioLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup(Alignment.TRAILING)
			.addComponent(getAspectRatioPanel())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(aspectRatioLabel)
			.addComponent(getAspectRatioPanel())
		);

		layout.setVerticalGroup(vGroup);

		return settingPanel;

	}

	public JPanel getAspectRatioPanel() {
		if (aspectRatioPanel == null) {
			aspectRatioPanel = new JPanel(new BorderLayout(3,3));

			aspectRatioPanel.add(getAspectUpSpinner(), BorderLayout.WEST);
			aspectRatioPanel.add(new JLabel("/"), BorderLayout.CENTER);
			aspectRatioPanel.add(getAspectDownSpinner(), BorderLayout.EAST);
		}
		return aspectRatioPanel;
	}

	public IntegerSpinner getAspectUpSpinner() {
		if (aspectUpSpinner == null) {
			aspectUpSpinner = new IntegerSpinner(new SpinnerNumberModel(bookView.getPlot().getMapAspectRatioUp(), 1, BookPlot.MAX_ASPECT_RATIO_SCALE, 1));
			aspectUpSpinner.setPreferredSize(new Dimension(40,25));

			aspectUpSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					bookView.getPlot().setMapAspectRatioUp(aspectUpSpinner.getValue());
				}

			});

		}
		return aspectUpSpinner;
	}

	public IntegerSpinner getAspectDownSpinner() {
		if (aspectDownSpinner == null) {
			aspectDownSpinner = new IntegerSpinner(new SpinnerNumberModel(bookView.getPlot().getMapAspectRatioDown(), 1, BookPlot.MAX_ASPECT_RATIO_SCALE, 1));
			aspectDownSpinner.setPreferredSize(new Dimension(40,25));

			aspectDownSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					bookView.getPlot().setMapAspectRatioDown(aspectDownSpinner.getValue());
				}

			});

		}
		return aspectDownSpinner;
	}

	protected BookPlot getPlot() {
		return bookView == null? null : bookView.getPlot();
	}

	private void createButtons() {

		buttonPanelComponents.add(getOriginalSignalVisibleButton());
		buttonPanelComponents.add(getFullReconstructionVisibleButton());
		buttonPanelComponents.add(getReconstructionVisibleButton());
		buttonPanelComponents.add(Box.createRigidArea(new Dimension(1,1)));
		buttonPanelComponents.add(getLegendVisibleButton());
		buttonPanelComponents.add(getScaleVisibleButton());
		buttonPanelComponents.add(getAxesVisibleButton());
		buttonPanelComponents.add(getAtomToolTipsVisibleButton());
		buttonPanelComponents.add(Box.createRigidArea(new Dimension(1,1)));
		buttonPanelComponents.add(getAntialiasButton());

	}

	public JToggleButton getAntialiasButton() {
		if (antialiasButton == null) {
			antialiasButton = new JToggleButton(_("Signal antialiasing"), IconUtils.loadClassPathIcon("org/signalml/app/icon/antialias.png"));
			antialiasButton.setToolTipText(_("Toggle reconstruction antialiasing"));
			antialiasButton.setSelected(getPlot().isSignalAntialiased());

			antialiasButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPlot().setSignalAntialiased(antialiasButton.isSelected());
				}

			});
		}
		return antialiasButton;
	}

	public JToggleButton getOriginalSignalVisibleButton() {
		if (originalSignalVisibleButton == null) {
			originalSignalVisibleButton = new JToggleButton(_("Show original signal"));
			originalSignalVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/originalsignalvisible.png"));
			originalSignalVisibleButton.setToolTipText(_("Toggle showing original signal graph"));
			originalSignalVisibleButton.setSelected(getPlot().isOriginalSignalVisible());

			originalSignalVisibleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPlot().setOriginalSignalVisible(originalSignalVisibleButton.isSelected());
				}

			});
		}
		return originalSignalVisibleButton;
	}

	public JToggleButton getFullReconstructionVisibleButton() {
		if (fullReconstructionVisibleButton == null) {
			fullReconstructionVisibleButton = new JToggleButton(_("Show full reconstruction"));
			fullReconstructionVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/fullreconstructionvisible.png"));
			fullReconstructionVisibleButton.setToolTipText(_("Toggle showing full reconstruction graph"));
			fullReconstructionVisibleButton.setSelected(bookView.getPlot().isFullReconstructionVisible());

			fullReconstructionVisibleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPlot().setFullReconstructionVisible(fullReconstructionVisibleButton.isSelected());
				}

			});
		}
		return fullReconstructionVisibleButton;
	}

	public JToggleButton getReconstructionVisibleButton() {
		if (reconstructionVisibleButton == null) {
			reconstructionVisibleButton = new JToggleButton(_("Show reconstruction"));
			reconstructionVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/reconstructionvisible.png"));
			reconstructionVisibleButton.setToolTipText(_("Toggle showing reconstruction graph"));
			reconstructionVisibleButton.setSelected(bookView.getPlot().isReconstructionVisible());

			reconstructionVisibleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPlot().setReconstructionVisible(reconstructionVisibleButton.isSelected());
				}

			});
		}
		return reconstructionVisibleButton;
	}

	public JToggleButton getLegendVisibleButton() {
		if (legendVisibleButton == null) {
			legendVisibleButton = new JToggleButton(_("Show legend"));
			legendVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/legendvisible.png"));
			legendVisibleButton.setToolTipText(_("Toggle showing reconstruction legend"));
			legendVisibleButton.setSelected(bookView.getPlot().isLegendVisible());

			legendVisibleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPlot().setLegendVisible(legendVisibleButton.isSelected());
				}

			});
		}
		return legendVisibleButton;
	}

	public JToggleButton getScaleVisibleButton() {
		if (scaleVisibleButton == null) {
			scaleVisibleButton = new JToggleButton(_("Show scale"));
			scaleVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/scalevisible.png"));
			scaleVisibleButton.setToolTipText(_("Toggle showing map scale"));
			scaleVisibleButton.setSelected(bookView.getPlot().isScaleVisible());

			scaleVisibleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPlot().setScaleVisible(scaleVisibleButton.isSelected());
				}

			});
		}
		return scaleVisibleButton;
	}

	public JToggleButton getAxesVisibleButton() {
		if (axesVisibleButton == null) {
			axesVisibleButton = new JToggleButton(_("Show axes"));
			axesVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/axesvisible.png"));
			axesVisibleButton.setToolTipText(_("Toggle showing map axes"));
			axesVisibleButton.setSelected(bookView.getPlot().isAxesVisible());

			axesVisibleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPlot().setAxesVisible(axesVisibleButton.isSelected());
				}

			});
		}
		return axesVisibleButton;
	}

	public JToggleButton getAtomToolTipsVisibleButton() {
		if (atomToolTipsVisibleButton == null) {
			atomToolTipsVisibleButton = new JToggleButton(_("Show atom tool tips"));
			atomToolTipsVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/atomtooltips.png"));
			atomToolTipsVisibleButton.setToolTipText(_("Show tool tips when mouse hovers over an atom"));
			atomToolTipsVisibleButton.setSelected(bookView.getPlot().isAtomToolTipsVisible());

			atomToolTipsVisibleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPlot().setAtomToolTipsVisible(atomToolTipsVisibleButton.isSelected());
				}

			});
		}
		return atomToolTipsVisibleButton;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		// nothing to do
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// nothing to do
	}

	@Override
	protected void onDialogClose() {
		super.onDialogClose();
		try {
			getAspectDownSpinner().commitEdit();
		} catch (ParseException ex) {
			JComponent editor = getAspectDownSpinner().getEditor();
			if (editor instanceof DefaultEditor) {
				((DefaultEditor) editor).getTextField().setValue(getAspectDownSpinner().getValue());
			}
		}
		try {
			getAspectUpSpinner().commitEdit();
		} catch (ParseException ex) {
			JComponent editor = getAspectUpSpinner().getEditor();
			if (editor instanceof DefaultEditor) {
				((DefaultEditor) editor).getTextField().setValue(getAspectUpSpinner().getValue());
			}
		}
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	/**
	 * Saves settings set in this dialog to {@ling ApplicationConfiguration}.
	 */
	public void saveSettingsToApplicationConfiguration() {
		ApplicationConfiguration applicationConfiguration = SvarogApplication.getApplicationConfiguration();

		applicationConfiguration.setMapAspectRatioUp(getAspectUpSpinner().getValue());
		applicationConfiguration.setMapAspectRatioDown(getAspectDownSpinner().getValue());

		applicationConfiguration.setSignalInBookAntialiased(getAntialiasButton().isSelected());
		applicationConfiguration.setOriginalSignalVisible(getOriginalSignalVisibleButton().isSelected());
		applicationConfiguration.setFullReconstructionVisible(getFullReconstructionVisibleButton().isSelected());
		applicationConfiguration.setReconstructionVisible(getReconstructionVisibleButton().isSelected());
		applicationConfiguration.setLegendVisible(getLegendVisibleButton().isSelected());
		applicationConfiguration.setScaleVisible(getScaleVisibleButton().isSelected());
		applicationConfiguration.setAxesVisible(getAxesVisibleButton().isSelected());
		applicationConfiguration.setAtomToolTipsVisible(getAtomToolTipsVisibleButton().isSelected());
	}

}

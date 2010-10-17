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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.book.BookPlot;
import org.signalml.app.view.book.BookView;
import org.signalml.app.view.element.TitledCrossBorder;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;
import org.springframework.context.support.MessageSourceAccessor;

/** BookPlotOptionsPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookPlotOptionsPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;

	private BookView bookView;

	private List<Component> buttonPanelComponents = new LinkedList<Component>();

	private JPanel aspectRatioPanel;

	private JSpinner aspectUpSpinner;
	private JSpinner aspectDownSpinner;

	public BookPlotOptionsPopupDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
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

		interfacePanel.setBorder(new TitledCrossBorder(messageSource.getMessage("bookView.plotOptions"), true));

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

		JLabel aspectRatioLabel = new JLabel(messageSource.getMessage("bookView.aspectRatio"));

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

	public JSpinner getAspectUpSpinner() {
		if (aspectUpSpinner == null) {
			aspectUpSpinner = new JSpinner(new SpinnerNumberModel(bookView.getPlot().getMapAspectRatioUp(), 1, BookPlot.MAX_ASPECT_RATIO_SCALE, 1));
			aspectUpSpinner.setPreferredSize(new Dimension(40,25));

			aspectUpSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					bookView.getPlot().setMapAspectRatioUp(((Integer) aspectUpSpinner.getValue()).intValue());
				}

			});

		}
		return aspectUpSpinner;
	}

	public JSpinner getAspectDownSpinner() {
		if (aspectDownSpinner == null) {
			aspectDownSpinner = new JSpinner(new SpinnerNumberModel(bookView.getPlot().getMapAspectRatioDown(), 1, BookPlot.MAX_ASPECT_RATIO_SCALE, 1));
			aspectDownSpinner.setPreferredSize(new Dimension(40,25));

			aspectDownSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					bookView.getPlot().setMapAspectRatioDown(((Integer) aspectDownSpinner.getValue()).intValue());
				}

			});

		}
		return aspectDownSpinner;
	}

	private void createButtons() {

		final BookPlot plot = bookView.getPlot();

		final JToggleButton antialiasButton = new JToggleButton(messageSource.getMessage("bookView.signalAntialias"), IconUtils.loadClassPathIcon("org/signalml/app/icon/antialias.png"));
		antialiasButton.setToolTipText(messageSource.getMessage("bookView.signalAntialiasToolTip"));
		antialiasButton.setSelected(plot.isSignalAntialiased());

		final JToggleButton originalSignalVisibleButton = new JToggleButton(messageSource.getMessage("bookView.originalSignalVisible"));
		originalSignalVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/originalsignalvisible.png"));
		originalSignalVisibleButton.setToolTipText(messageSource.getMessage("bookView.originalSignalVisibleToolTip"));
		originalSignalVisibleButton.setSelected(plot.isOriginalSignalVisible());

		final JToggleButton fullReconstructionVisibleButton = new JToggleButton(messageSource.getMessage("bookView.fullReconstructionVisible"));
		fullReconstructionVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/fullreconstructionvisible.png"));
		fullReconstructionVisibleButton.setToolTipText(messageSource.getMessage("bookView.fullReconstructionVisibleToolTip"));
		fullReconstructionVisibleButton.setSelected(plot.isFullReconstructionVisible());

		final JToggleButton reconstructionVisibleButton = new JToggleButton(messageSource.getMessage("bookView.reconstructionVisible"));
		reconstructionVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/reconstructionvisible.png"));
		reconstructionVisibleButton.setToolTipText(messageSource.getMessage("bookView.reconstructionVisibleToolTip"));
		reconstructionVisibleButton.setSelected(plot.isReconstructionVisible());

		final JToggleButton legendVisibleButton = new JToggleButton(messageSource.getMessage("bookView.legendVisible"));
		legendVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/legendvisible.png"));
		legendVisibleButton.setToolTipText(messageSource.getMessage("bookView.legendVisibleToolTip"));
		legendVisibleButton.setSelected(plot.isLegendVisible());

		final JToggleButton scaleVisibleButton = new JToggleButton(messageSource.getMessage("bookView.scaleVisible"));
		scaleVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/scalevisible.png"));
		scaleVisibleButton.setToolTipText(messageSource.getMessage("bookView.scaleVisibleToolTip"));
		scaleVisibleButton.setSelected(plot.isScaleVisible());

		final JToggleButton axesVisibleButton = new JToggleButton(messageSource.getMessage("bookView.axesVisible"));
		axesVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/axesvisible.png"));
		axesVisibleButton.setToolTipText(messageSource.getMessage("bookView.axesVisibleToolTip"));
		axesVisibleButton.setSelected(plot.isAxesVisible());

		final JToggleButton atomToolTipsVisibleButton = new JToggleButton(messageSource.getMessage("bookView.atomToolTipsVisible"));
		atomToolTipsVisibleButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/atomtooltips.png"));
		atomToolTipsVisibleButton.setToolTipText(messageSource.getMessage("bookView.atomToolTipsVisibleToolTip"));
		atomToolTipsVisibleButton.setSelected(plot.isAtomToolTipsVisible());

		antialiasButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				plot.setSignalAntialiased(antialiasButton.isSelected());
			}

		});

		originalSignalVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				plot.setOriginalSignalVisible(originalSignalVisibleButton.isSelected());
			}

		});

		fullReconstructionVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				plot.setFullReconstructionVisible(fullReconstructionVisibleButton.isSelected());
			}

		});

		reconstructionVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				plot.setReconstructionVisible(reconstructionVisibleButton.isSelected());
			}

		});

		legendVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				plot.setLegendVisible(legendVisibleButton.isSelected());
			}

		});

		scaleVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				plot.setScaleVisible(scaleVisibleButton.isSelected());
			}

		});

		axesVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				plot.setAxesVisible(axesVisibleButton.isSelected());
			}

		});

		atomToolTipsVisibleButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				plot.setAtomToolTipsVisible(atomToolTipsVisibleButton.isSelected());
			}

		});

		buttonPanelComponents.add(originalSignalVisibleButton);
		buttonPanelComponents.add(fullReconstructionVisibleButton);
		buttonPanelComponents.add(reconstructionVisibleButton);
		buttonPanelComponents.add(Box.createRigidArea(new Dimension(1,1)));
		buttonPanelComponents.add(legendVisibleButton);
		buttonPanelComponents.add(scaleVisibleButton);
		buttonPanelComponents.add(axesVisibleButton);
		buttonPanelComponents.add(atomToolTipsVisibleButton);
		buttonPanelComponents.add(Box.createRigidArea(new Dimension(1,1)));
		buttonPanelComponents.add(antialiasButton);

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

	@Override
	public boolean isFormClickApproving() {
		return true;
	}

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

}

/* EvokedPotentialResultDialog.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import org.signalml.app.model.components.PropertySheetModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.app.view.workspace.ViewerPropertySheet;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.plugin.export.SignalMLException;

/** EvokedPotentialResultDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialResultDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;

	private JSplitPane splitPane;
	private JScrollPane graphScrollPane;
	private EvokedPotentialGraphPanel graphPanel;

	private PropertySheetModel propertySheetModel;
	private ViewerPropertySheet propertySheet;
	private JScrollPane propertySheetScrollPane;

	private JPanel buttonPanel;

	private JButton saveChartsToFileButton;
	private JButton saveSamplesToFloatFileButton;

	private JLabel skippedMarkersLabelTitle;

	public EvokedPotentialResultDialog() {
		super();
	}

	public EvokedPotentialResultDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Evoked potential averaging result"));
		setIconImage(IconUtils.loadClassPathImage(EvokedPotentialMethodDialog.ICON_PATH));
		setResizable(true);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());
		interfacePanel.setBorder(new EmptyBorder(3,3,3,3));

		interfacePanel.add(getSplitPane(), BorderLayout.CENTER);

		return interfacePanel;

	}

	public JSplitPane getSplitPane() {
		if (splitPane == null) {

			splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			splitPane.setOneTouchExpandable(true);

			JPanel topPanel = new JPanel(new BorderLayout(3,3));
			topPanel.add(getGraphScrollPane(), BorderLayout.CENTER);

			JPanel bottomPanel = new JPanel(new BorderLayout());

			bottomPanel.add(getPropertySheetScrollPane(), BorderLayout.CENTER);
			bottomPanel.add(getButtonPanel(), BorderLayout.SOUTH);

			splitPane.setTopComponent(topPanel);
			splitPane.setBottomComponent(bottomPanel);
			splitPane.resetToPreferredSizes();
			splitPane.setContinuousLayout(true);
			splitPane.setBackground(Color.WHITE);

		}
		return splitPane;
	}

	public JScrollPane getGraphScrollPane() {
		if (graphScrollPane == null) {
			graphScrollPane = new JScrollPane(getGraphPanel());
			graphScrollPane.setPreferredSize(new Dimension(600,400));
			graphScrollPane.setBackground(Color.WHITE);
		}
		return graphScrollPane;
	}

	public EvokedPotentialGraphPanel getGraphPanel() {
		if (graphPanel == null) {
			graphPanel = new EvokedPotentialGraphPanel(fileChooser);
		}
		return graphPanel;
	}

	public PropertySheetModel getPropertySheetModel() {
		if (propertySheetModel == null) {
			propertySheetModel = new PropertySheetModel();
		}
		return propertySheetModel;
	}

	public ViewerPropertySheet getPropertySheet() {
		if (propertySheet == null) {
			propertySheet = new ViewerPropertySheet(getPropertySheetModel());
		}
		return propertySheet;
	}

	public JScrollPane getPropertySheetScrollPane() {
		if (propertySheetScrollPane == null) {
			propertySheetScrollPane = new JScrollPane(getPropertySheet());
			propertySheetScrollPane.setPreferredSize(new Dimension(400,170));
			propertySheetScrollPane.setBackground(Color.WHITE);
		}
		return propertySheetScrollPane;
	}

	public JPanel getButtonPanel() {
		if (buttonPanel == null) {

			buttonPanel = new JPanel(new GridLayout(1, 2, 3, 3));
			buttonPanel.setBorder(new EmptyBorder(3,3,3,3));

			buttonPanel.add(getSaveChartsToFileButton());
			buttonPanel.add(getSaveSamplesToFloatFileButton());

		}
		return buttonPanel;
	}

	public JButton getSaveChartsToFileButton() {
		if (saveChartsToFileButton == null) {
			saveChartsToFileButton = new JButton(getGraphPanel().getExportAllEPChartsToFileAction());
			saveChartsToFileButton.setHorizontalAlignment(JButton.LEFT);
		}
		return saveChartsToFileButton;
	}

	public JButton getSaveSamplesToFloatFileButton() {
		if (saveSamplesToFloatFileButton == null) {
			saveSamplesToFloatFileButton = new JButton(getGraphPanel().getExportAllEPSamplesToFloatFileAction());
			saveSamplesToFloatFileButton.setHorizontalAlignment(JButton.LEFT);
		}
		return saveSamplesToFloatFileButton;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		EvokedPotentialResult result = (EvokedPotentialResult) model;

		getGraphPanel().setResult(result);
		getPropertySheetModel().setSubject(new EvokedPotentialResultWrapper(result));

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		// nothing to do

	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return EvokedPotentialResult.class.isAssignableFrom(clazz);
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

}

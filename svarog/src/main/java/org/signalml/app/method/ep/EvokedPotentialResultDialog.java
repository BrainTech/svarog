/* EvokedPotentialResultDialog.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import org.signalml.app.method.ep.action.ExportAllEPChartsToFileAction;
import org.signalml.app.method.ep.action.ExportAllEPSamplesToFloatFileAction;
import org.signalml.app.model.components.PropertySheetModel;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
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
	private JButton showMinMaxButton;

	private ExportAllEPChartsToFileAction exportAllEPChartsToFileAction;
	private ExportAllEPSamplesToFloatFileAction exportAllEPSamplesToFileAction;
	private ShowMinMaxAction showMinMaxAction;

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
			graphPanel = new EvokedPotentialGraphPanel();
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

			buttonPanel = new JPanel(new GridLayout(1, 3, 3, 3));
			buttonPanel.setBorder(new EmptyBorder(3,3,3,3));

			buttonPanel.add(getSaveChartsToFileButton());
			buttonPanel.add(getSaveSamplesToFloatFileButton());
			buttonPanel.add(getShowMinMaxButton());

		}
		return buttonPanel;
	}

	public JButton getSaveChartsToFileButton() {
		if (saveChartsToFileButton == null) {
			saveChartsToFileButton = new JButton(getExportAllEPChartsToFileAction());
		}
		return saveChartsToFileButton;
	}

	public ExportAllEPChartsToFileAction getExportAllEPChartsToFileAction() {
		if (exportAllEPChartsToFileAction == null)
			exportAllEPChartsToFileAction = new ExportAllEPChartsToFileAction(getFileChooser(), getGraphPanel());
		return exportAllEPChartsToFileAction;
	}

	public ExportAllEPSamplesToFloatFileAction getExportAllEPSamplesToFloatFileAction() {
		if (exportAllEPSamplesToFileAction == null)
			exportAllEPSamplesToFileAction = new ExportAllEPSamplesToFloatFileAction(getFileChooser());
		return exportAllEPSamplesToFileAction;
	}

	public ShowMinMaxAction getShowMinMaxAction() {
		if (showMinMaxAction == null) {
			showMinMaxAction = new ShowMinMaxAction(getFileChooser());
		}
		return showMinMaxAction;
	}

	public JButton getSaveSamplesToFloatFileButton() {
		if (saveSamplesToFloatFileButton == null) {
			saveSamplesToFloatFileButton = new JButton(getExportAllEPSamplesToFloatFileAction());
		}
		return saveSamplesToFloatFileButton;
	}

	public JButton getShowMinMaxButton() {
		if (showMinMaxButton == null) {
			showMinMaxButton = new JButton(getShowMinMaxAction());
		}
		return showMinMaxButton;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		EvokedPotentialResult result = (EvokedPotentialResult) model;

		getGraphPanel().setResult(result);
		getPropertySheetModel().setSubject(new EvokedPotentialResultWrapper(result));
		getExportAllEPSamplesToFloatFileAction().setResult(result);
		getExportAllEPChartsToFileAction().setResult(result);
		getShowMinMaxAction().setResult(result);

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

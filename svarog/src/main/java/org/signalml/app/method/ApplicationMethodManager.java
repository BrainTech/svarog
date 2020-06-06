/* ApplicationMethodManager.java created 2007-10-22
 *
 */

package org.signalml.app.method;

import com.thoughtworks.xstream.XStream;
import java.awt.Window;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.model.components.TableToTextExporter;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.method.Method;

/** ApplicationMethodManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ApplicationMethodManager extends DefaultMethodManager {

	private Map<Method,ApplicationMethodDescriptor> methodData = new HashMap<Method,ApplicationMethodDescriptor>();

	private List<UnavailableMethodDescriptor> unavailableMethods = new LinkedList<UnavailableMethodDescriptor>();
	private File profileDir;
	private XStream streamer;
	private ViewerFileChooser fileChooser;
	private ApplicationConfiguration applicationConfig;

	private DocumentManager documentManager;
	private DocumentFlowIntegrator documentFlowIntegrator;
	private ActionFocusManager actionFocusManager;
	private MP5ExecutorManager mp5ExecutorManager;

	private TableToTextExporter tableToTextExporter;

	private Window dialogParent;

	public synchronized ApplicationMethodDescriptor getMethodData(Method method) {
		return methodData.get(method);
	}

	public synchronized void setMethodData(Method method, ApplicationMethodDescriptor descriptor) {
		if (!methods.contains(method)) {
			return;
		}
		methodData.put(method, descriptor);
	}

	@Override
	public synchronized void removeMethod(Method method) {
		super.removeMethod(method);
		methodData.remove(method);
	}

	public synchronized int getUnavailableMethodCount() {
		return unavailableMethods.size();
	}

	public synchronized UnavailableMethodDescriptor getUnavailableMethodAt(int index) {
		return unavailableMethods.get(index);
	}

	public synchronized void addUnavailableMethod(UnavailableMethodDescriptor method) {
		unavailableMethods.add(method);
	}

	public synchronized File getProfileDir() {
		return profileDir;
	}

	public synchronized void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

	public synchronized XStream getStreamer() {
		return streamer;
	}

	public synchronized void setStreamer(XStream streamer) {
		this.streamer = streamer;
	}

	public synchronized DocumentManager getDocumentManager() {
		return documentManager;
	}

	public synchronized void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public synchronized ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public synchronized void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	public synchronized Window getDialogParent() {
		return dialogParent;
	}

	public synchronized void setDialogParent(Window dialogParent) {
		this.dialogParent = dialogParent;
	}

	public synchronized ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public synchronized void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public synchronized DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public synchronized void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

	public synchronized ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public synchronized void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public synchronized MP5ExecutorManager getMp5ExecutorManager() {
		return mp5ExecutorManager;
	}

	public synchronized void setMp5ExecutorManager(MP5ExecutorManager mp5ExecutorManager) {
		this.mp5ExecutorManager = mp5ExecutorManager;
	}

	public synchronized TableToTextExporter getTableToTextExporter() {
		return tableToTextExporter;
	}

	public synchronized void setTableToTextExporter(TableToTextExporter tableToTextExporter) {
		this.tableToTextExporter = tableToTextExporter;
	}

}

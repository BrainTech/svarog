/* ApplicationMethodManager.java created 2007-10-22
 *
 */

package org.signalml.app.method;

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
import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.method.Method;
import org.springframework.context.support.MessageSourceAccessor;

import com.thoughtworks.xstream.XStream;

/** ApplicationMethodManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ApplicationMethodManager extends DefaultMethodManager {

	private Map<Method,ApplicationMethodDescriptor> methodData = new HashMap<Method,ApplicationMethodDescriptor>();

	private List<UnavailableMethodDescriptor> unavailableMethods = new LinkedList<UnavailableMethodDescriptor>();

	private MessageSourceAccessor messageSource;
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

	public ApplicationMethodDescriptor getMethodData(Method method) {
		return methodData.get(method);
	}

	public void setMethodData(Method method, ApplicationMethodDescriptor descriptor) {
		if (!methods.contains(method)) {
			return;
		}
		methodData.put(method, descriptor);
	}

	@Override
	public void removeMethod(Method method) {
		super.removeMethod(method);
		methodData.remove(method);
	}

	public int getUnavailableMethodCount() {
		return unavailableMethods.size();
	}

	public UnavailableMethodDescriptor getUnavailableMethodAt(int index) {
		return unavailableMethods.get(index);
	}

	public void addUnavailableMethod(UnavailableMethodDescriptor method) {
		unavailableMethods.add(method);
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public File getProfileDir() {
		return profileDir;
	}

	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

	public XStream getStreamer() {
		return streamer;
	}

	public void setStreamer(XStream streamer) {
		this.streamer = streamer;
	}

	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	public Window getDialogParent() {
		return dialogParent;
	}

	public void setDialogParent(Window dialogParent) {
		this.dialogParent = dialogParent;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public MP5ExecutorManager getMp5ExecutorManager() {
		return mp5ExecutorManager;
	}

	public void setMp5ExecutorManager(MP5ExecutorManager mp5ExecutorManager) {
		this.mp5ExecutorManager = mp5ExecutorManager;
	}

	public TableToTextExporter getTableToTextExporter() {
		return tableToTextExporter;
	}

	public void setTableToTextExporter(TableToTextExporter tableToTextExporter) {
		this.tableToTextExporter = tableToTextExporter;
	}

}

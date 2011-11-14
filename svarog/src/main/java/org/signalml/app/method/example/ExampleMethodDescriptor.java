/* ExampleMethodDescriptor.java created 2007-10-22
 *
 */

package org.signalml.app.method.example;

import static org.signalml.app.SvarogApplication._;
import org.signalml.app.method.ApplicationIterableMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodIterationResultConsumer;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.view.roc.RocDialog;
import org.signalml.method.example.ExampleMethod;

/** ExampleMethodDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExampleMethodDescriptor implements ApplicationIterableMethodDescriptor {

	public static final String RUN_METHOD_STRING = _("Find the meaning of life");
	private ExampleMethod method;
	private ExampleMethodDialog configurer;
	private ExampleMethodConsumer consumer;
	private ExampleMethodIterationConsumer iterationConsumer;

	private RocDialog rocDialog;

	public ExampleMethodDescriptor(ExampleMethod method) {
		this.method = method;
	}

	@Override
	public ExampleMethod getMethod() {
		return method;
	}

	@Override
	public String getName() {
		return RUN_METHOD_STRING;
	}

	@Override
	public String getIterationName() {
		return _("Iterate finding the meaning of life");
	}

	@Override
	public String getIconPath() {
		return "org/signalml/app/icon/runmethod.png";
	}

	@Override
	public String getIterationIconPath() {
		return "org/signalml/app/icon/iteratemethod.png";
	}

	@Override
	public MethodPresetManager getPresetManager(ApplicationMethodManager methodManager, boolean existingOnly) {
		return null;
	}

	@Override
	public ExampleMethodDialog getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new ExampleMethodDialog(methodManager.getDialogParent());
			configurer.initialize(methodManager);
		}
		return configurer;
	}

	@Override
	public ExampleMethodConsumer getConsumer(ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new ExampleMethodConsumer();
		}
		return consumer;
	}

	@Override
	public MethodIterationResultConsumer getIterationConsumer(ApplicationMethodManager methodManager) {
		if (iterationConsumer == null) {
			iterationConsumer = new ExampleMethodIterationConsumer();
			iterationConsumer.setRocDialog(getRocDialog(methodManager));
		}
		return iterationConsumer;
	}

	public RocDialog getRocDialog(ApplicationMethodManager methodManager) {
		if (rocDialog == null) {
			rocDialog = new RocDialog(methodManager.getDialogParent(),true);
			rocDialog.setFileChooser(methodManager.getFileChooser());
			rocDialog.setTableToTextExporter(methodManager.getTableToTextExporter());
		}
		return rocDialog;
	}

	@Override
	public Object createData(ApplicationMethodManager methodManager) {
		return method.createData();
	}

}

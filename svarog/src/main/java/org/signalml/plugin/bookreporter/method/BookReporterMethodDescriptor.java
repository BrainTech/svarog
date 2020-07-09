package org.signalml.plugin.bookreporter.method;

import org.apache.log4j.Logger;
import org.signalml.app.method.ApplicationIterableMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodConfigurer;
import org.signalml.app.method.MethodIterationResultConsumer;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.MethodResultConsumer;
import org.signalml.method.Method;
import org.signalml.plugin.bookreporter.data.BookReporterData;
import org.signalml.plugin.bookreporter.data.BookReporterParameters;
import org.signalml.plugin.export.method.BaseMethodData;
import org.signalml.plugin.method.PluginAbstractMethodDescriptor;
import org.signalml.plugin.method.helper.PluginPresetManagerHelper;

/**
 * @author piotr@develancer.pl
 * (based on NewStagerMethodDescriptor)
 */
public class BookReporterMethodDescriptor extends PluginAbstractMethodDescriptor
		implements ApplicationIterableMethodDescriptor {

	protected static final Logger logger = Logger
			.getLogger(BookReporterMethodDescriptor.class);

	private BookReporterMethodConfigurer configurer;
	private MethodPresetManager presetManager;

	private BookReporterMethodConsumer consumer;

	@Override
	public MethodIterationResultConsumer getIterationConsumer(
			ApplicationMethodManager methodManager) {
		return null;
	}

	@Override
	public String getIterationIconPath() {
		return null;
	}

	@Override
	public String getIterationName() {
		return null;
	}

	@Override
	public String getIconPath() {
		return null;
	}

	@Override
	public BaseMethodData createData(ApplicationMethodManager methodManager) {
		BookReporterData data = new BookReporterData();

		// ConfigurationDefaults.setBookReporterParameters(data.getParameters());
		// //FIXME: what's this?

		return data;
	}

	@Override
	public MethodConfigurer getConfigurer(ApplicationMethodManager methodManager) {
		if (configurer == null) {
			configurer = new BookReporterMethodConfigurer();
			configurer.setPresetManager(getPresetManager(methodManager, false));
			configurer.initialize(this.methodManager);
		}
		return configurer;
	}

	@Override
	public MethodResultConsumer getConsumer(
			ApplicationMethodManager methodManager) {
		if (consumer == null) {
			consumer = new BookReporterMethodConsumer();
			consumer.initialize(this.methodManager);
		}
		return consumer;
	}

	@Override
	public Method getMethod() {
		return this.methodManager.getMethodConfig().getMethod();
	}

	@Override
	public String getName() {
		return "";
	}

	@Override
	public MethodPresetManager getPresetManager(
			ApplicationMethodManager methodManager, boolean existingOnly) {
		if (presetManager == null && !existingOnly) {
			presetManager = PluginPresetManagerHelper.GetPresetForMethod(
					methodManager, this.methodManager, this.getMethod()
							.getName(), BookReporterParameters.class);
		}
		return presetManager;
	}

}

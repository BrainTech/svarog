/* JaninoDynamicCompiler.java created 2007-11-07
 *
 */

package org.signalml.compilation.janino;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import org.apache.log4j.Logger;
import org.codehaus.janino.DebuggingInformation;
import org.codehaus.janino.JavaSourceClassLoader;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.compilation.CompilationException;
import org.signalml.compilation.DynamicCompiler;
import org.signalml.compilation.JavaCodeProvider;

/** JaninoDynamicCompiler
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class JaninoDynamicCompiler implements DynamicCompiler {

	protected static final Logger logger = Logger.getLogger(JaninoDynamicCompiler.class);

	@Override
	public Class<?> compile(File srcDir, String fqClassName, JavaCodeProvider codeProvider) throws CompilationException {

		String[] parts = fqClassName.split("\\.");
		boolean ok;

		if (parts.length < 1) {
			throw new CompilationException(_R("Bad class name [{0}]", fqClassName));
		}
		File dir = srcDir.getAbsoluteFile();
		for (int i=0; i<parts.length-1; i++) {
			dir = new File(dir, parts[i]);
			if (!dir.exists()) {
				ok = dir.mkdir();
				if (!ok) {
					throw new CompilationException(_R("Failed to create directory [{0}]", dir.getAbsolutePath()));
				}
			}
		}
		File javaFile = new File(dir, parts[parts.length-1] + ".java");
		if (!javaFile.exists()) {

			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(javaFile));
				String code = codeProvider.getCode();
				writer.write(code);
			} catch (IOException ex) {

			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ex) {
					}
				}
			}

		}

		ClassLoader parentClassLoader = this.getClass().getClassLoader();

		if (logger.isDebugEnabled()) {

			logger.debug("Using srcDir [" + srcDir.getAbsolutePath() + "]");
			logger.debug("Using java file [" + javaFile.getAbsolutePath() + "]");
			logger.debug("Using class name [" + fqClassName + "]");

			logger.debug("Using parent class loader [" + parentClassLoader + "] class [" + (parentClassLoader != null ? parentClassLoader.getClass().getName() : "(null)") + "]");

			ClassLoader upperClassLoader = parentClassLoader.getParent();
			while (upperClassLoader != null) {
				logger.debug("Using upper class loader [" + upperClassLoader + "] class [" + upperClassLoader.getClass().getName() + "]");
				upperClassLoader = upperClassLoader.getParent();
			}

		}

		String loaderPath = srcDir.getAbsolutePath();
		loaderPath = (loaderPath.startsWith("/") ? "" : "/") + loaderPath + (loaderPath.endsWith("/") ? "" : "/");
		String urlPath = "file://" + loaderPath.replace('\\', '/');

		logger.debug("Using url path [" + urlPath + "]");

		URL url;
		try {
			url = new URL(urlPath);
		} catch (MalformedURLException ex) {
			logger.error("Failed to parse URL [" + urlPath + "]", ex);
			throw new CompilationException(_("Failed to parse URL"), ex);
		}

		return loadClassInternal(new URL[] { url }, new File[] { srcDir }, fqClassName, parentClassLoader);

	}

	@Override
	public Class<?> compile(File[] path, String fqClassName) throws CompilationException {

		ClassLoader parentClassLoader = this.getClass().getClassLoader();

		if (logger.isDebugEnabled()) {

			for (File file : path) {
				logger.debug("Using classpath [" + file.getAbsolutePath() + "]");
			}
			logger.debug("Using class name [" + fqClassName + "]");

			logger.debug("Using parent class loader [" + parentClassLoader + "] class [" + (parentClassLoader != null ? parentClassLoader.getClass().getName() : "(null)") + "]");

			ClassLoader upperClassLoader = parentClassLoader.getParent();
			while (upperClassLoader != null) {
				logger.debug("Using upper class loader [" + upperClassLoader + "] class [" + upperClassLoader.getClass().getName() + "]");
				upperClassLoader = upperClassLoader.getParent();
			}

		}

		URL[] classPath = new URL[path.length];
		File[] sourcePath = new File[path.length];
		int sourceIdx = 0;

		for (int i=0; i<path.length; i++) {

			try {
				classPath[i] = path[i].toURI().toURL();
			} catch (MalformedURLException ex) {
				logger.warn("Failed to convert path [" + path[i].getAbsolutePath() + "] to url", ex);
			}

			if (path[i].exists() && path[i].isDirectory()) {
				sourcePath[sourceIdx] = path[i];
				sourceIdx++;
			}

		}

		sourcePath = Arrays.copyOf(sourcePath, sourceIdx);

		return loadClassInternal(classPath, sourcePath, fqClassName, parentClassLoader);

	}

	private Class<?> loadClassInternal(URL[] classPath, File[] sourcePath, String fqClassName, ClassLoader parentClassLoader) throws CompilationException {

		URLClassLoader loader = new URLClassLoader(classPath, parentClassLoader);

		ClassLoader cl = new JavaSourceClassLoader(
			loader,  						   // parentClassLoader
			sourcePath,         			   // optionalSourcePath
			(String) null,                     // optionalCharacterEncoding
			DebuggingInformation.NONE          // debuggingInformation
		);

		Class<?> clazz = null;
		try {
			clazz = cl.loadClass(fqClassName);
		} catch (ClassNotFoundException ex) {
			throw new CompilationException(_("Failed to load class"), ex);
		}

		if (clazz == null) {
			throw new CompilationException(_("Failed to load class, null returned"));
		}

		return clazz;

	}

}

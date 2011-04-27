/* DynamicCompiler.java created 2007-11-07
 *
 */

package org.signalml.compilation;

import java.io.File;

/** DynamicCompiler
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface DynamicCompiler {

	Class<?> compile(File srcDir, String fqClassName, JavaCodeProvider codeProvider) throws CompilationException;

	Class<?> compile(File[] path, String fqClassName) throws CompilationException;

	void setWarning(DynamicCompilationWarning warning);

}

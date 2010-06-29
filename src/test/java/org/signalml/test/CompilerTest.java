/* CompilerTest.java created 2007-11-07
 *
 */

package org.signalml.test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/** CompilerTest
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CompilerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		// access the compiler provided
		if (compiler == null) {
			System.out.println("Compiler not found");
		} else {
			int result = compiler.run(null, null, null, args);
			// compile the files named in args
			if (result == 0)
				System.out.println("Compilation Succeeded");
			else
				System.out.println("Compilation Failed");
		}

	}

}

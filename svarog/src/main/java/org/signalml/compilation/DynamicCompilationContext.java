/* DynamicCompilationContext.java created 2008-03-03
 *
 */

package org.signalml.compilation;

import org.signalml.compilation.janino.JaninoDynamicCompiler;

/** DynamicCompilationContext
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DynamicCompilationContext {

	private static DynamicCompilationContext sharedInstance;

	private DynamicCompiler sharedCompiler;

	protected DynamicCompilationContext() {
	}

	public static DynamicCompilationContext getSharedInstance() {
		if (null == sharedInstance) {
		    synchronized (DynamicCompilationContext.class) {
		        if (null == sharedInstance)
		            sharedInstance = new DynamicCompilationContext();
		    }
		}

		return sharedInstance;
	}

	public DynamicCompiler getCompiler() {
		if (sharedCompiler == null) {
			sharedCompiler = new JaninoDynamicCompiler();
		}
		return sharedCompiler;
	}

}

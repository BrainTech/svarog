/* DelegatingAtomFilter.java created 2008-03-04
 *
 */

package org.signalml.domain.book.filter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.compilation.CompilationException;
import org.signalml.compilation.DynamicCompilationContext;
import org.signalml.compilation.DynamicCompiler;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

/**
 * DelegatingAtomFilter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
@XStreamAlias("delegatingbookfilter")
public class DelegatingAtomFilter extends AbstractAtomFilter {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(DelegatingAtomFilter.class);

	private static final String[] CODES = new String[] { "delegatingAtomFilter" };

	private ArrayList<File> classPath;

	private String fqClassName;

	private transient Class<?> delegateClass;
	private transient Method delegateMethod;
	private transient AtomFilter delegate;

	public DelegatingAtomFilter() {
		super();
		classPath = new ArrayList<File>();
	}

	@Override
	public void initialize() throws SignalMLException {
		try {
			getDelegate();
		} catch (CompilationException ex) {
			throw new SignalMLException("error.delegatingAtomFilter.initializationFailed", ex);
		} catch (InstantiationException ex) {
			throw new SignalMLException("error.delegatingAtomFilter.initializationFailed", ex);
		} catch (IllegalAccessException ex) {
			throw new SignalMLException("error.delegatingAtomFilter.initializationFailed", ex);
		}
	}

	public DelegatingAtomFilter(DelegatingAtomFilter filter) {
		super(filter);
		classPath = new ArrayList<File>(filter.classPath);
		fqClassName = filter.fqClassName;
	}

	@Override
	public AbstractAtomFilter duplicate() {
		return new DelegatingAtomFilter(this);
	}

	public String getFqClassName() {
		return fqClassName;
	}

	public void setFqClassName(String fqClassName) {
		if (!Util.equalsWithNulls(this.fqClassName, fqClassName)) {
			this.fqClassName = fqClassName;
			delegateClass = null;
			delegateMethod = null;
			delegate = null;
		}
	}

	public ArrayList<File> getClassPath() {
		return classPath;
	}

	public void setClassPath(ArrayList<File> classPath) {
		this.classPath = classPath;
		delegateClass = null;
		delegateMethod = null;
		delegate = null;
	}

	@Override
	public boolean matches(StandardBookSegment segment, StandardBookAtom atom) {
		if (delegate == null) {
			try {
				getDelegate();
			} catch (InstantiationException ex) {
				logger.error("Failed to instantiate delegate", ex);
				throw new SanityCheckException("Failed to instantiate verified filter", ex);
			} catch (IllegalAccessException ex) {
				logger.error("Failed to instantiate delegate", ex);
				throw new SanityCheckException("Failed to instantiate verified filter", ex);
			} catch (CompilationException ex) {
				logger.error("Failed to instantiate delegate", ex);
				throw new SanityCheckException("Failed to instantiate verified filter", ex);
			}
		}
		return delegate.matches(segment, atom);
	}

	public Class<?> getDelegateClass() throws CompilationException {
		if (delegateClass == null) {

			DynamicCompiler compiler = DynamicCompilationContext.getSharedInstance().getCompiler();
			File[] path = new File[classPath.size()];
			classPath.toArray(path);

			Class<?> cls = compiler.compile(path, fqClassName);

			if (!AtomFilter.class.isAssignableFrom(cls)) {

				try {
					delegateMethod = cls.getMethod("matches", StandardBookSegment.class, StandardBookAtom.class);
				} catch (SecurityException ex) {
					logger.error("Security exception", ex);
					throw new CompilationException(ex);
				} catch (NoSuchMethodException ex) {
					logger.info("Class doesn't implement filter", ex);
					throw new CompilationException("error.classNotFilter");
				}

			}

			delegateClass = cls;

		}
		return delegateClass;
	}

	public AtomFilter getDelegate() throws CompilationException, InstantiationException, IllegalAccessException {
		if (delegate == null) {

			Class<?> cls = getDelegateClass();

			if (AtomFilter.class.isAssignableFrom(cls)) {
				delegate = (AtomFilter) cls.newInstance();
			} else {
				delegate = new UnimplementingFilterWrapper(cls.newInstance(), delegateMethod);
			}

		}
		return delegate;
	}

	@Override
	public Object[] getArguments() {
		String name = "?";
		try {
			name = getDelegateClass().getSimpleName();
		} catch (Throwable t) {
			// ignore all exceptions & errors
		}
		return new Object[] { name };
	}

	@Override
	public String[] getCodes() {
		return CODES;
	}

	@Override
	public String getDefaultMessage() {
		return _("Delegating atom filter");
	}

	private class UnimplementingFilterWrapper implements AtomFilter {

		private Method delegateMethod;
		private Object delegate;

		private int warningCount;

		public UnimplementingFilterWrapper(Object delegate, Method delegateMethod) {
			if (delegateMethod == null) {
				throw new NullPointerException("No method");
			}
			this.delegate = delegate;
			this.delegateMethod = delegateMethod;
		}

		@Override
		public boolean matches(StandardBookSegment segment, StandardBookAtom atom) {
			try {
				return ((Boolean) delegateMethod.invoke(this.delegate, segment, atom)).booleanValue();
			} catch (IllegalArgumentException ex) {
				return onException(ex);
			} catch (IllegalAccessException ex) {
				return onException(ex);
			} catch (InvocationTargetException ex) {
				return onException(ex);
			}
		}

		private boolean onException(Exception ex) {

			if (warningCount < 10) {
				logger.error("Invocation failed", ex);
				if (warningCount == 9) {
					logger.error("... no more errors logged");
				}
			}
			warningCount++;

			return false;

		}

	}

}

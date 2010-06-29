/* SignalMLCodecReader.java created 2007-09-18
 *
 */

package org.signalml.codec;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.log4j.Logger;

/** SignalMLCodecReader
 *
 *
 * @author most of the code Copyright (C) 2003 Dobieslaw Ircha <dircha@eranet.pl> Artur Biesiadowski <abies@adres.pl> Piotr J. Durka     <Piotr-J.Durka@fuw.edu.pl>
 * 		adapted by Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLCodecReader {

	protected static final Logger logger = Logger.getLogger(SignalMLCodecReader.class);

	private SMLCodec delegate = null;
	private Class<?> delegateClass = null;
	private SignalMLCodec codec = null;

	private HashMap<String,Method> methodMap = new HashMap<String, Method>();
	private Method openMethod;
	private Method closeMethod;

	private Object[] singleArg = new Object[1];
	private Object[] noArg = new Object[0];

	private String currentFilename = null;

	public SignalMLCodecReader(Class<?> cobj, SignalMLCodec codec) throws SignalMLCodecException {
		this.codec = codec;
		this.delegateClass = cobj;

		if (!SMLCodec.class.isAssignableFrom(cobj)) {
			logger.error("Bad codec class [" + cobj + "]");
			throw new SignalMLCodecException("Bad codec class");
		}

		try {
			delegate = (SMLCodec) cobj.newInstance();
//			try {
//				delegate = new EASYS();
//			} catch (XMLCodecException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		} catch (InstantiationException ex) {
			logger.error("Failed to instantiate codec delegate", ex);
			throw new SignalMLCodecException("Failed to instantiate", ex);
		} catch (IllegalAccessException ex) {
			logger.error("Failed to instantiate codec delegate", ex);
			throw new SignalMLCodecException("Failed to instantiate", ex);
		}

	}

	public SignalMLCodec getCodec() {
		return codec;
	}

	private void setValue(String name, int value) throws SignalMLCodecException {
		if (delegate != null) {
			try {
				Method m = methodMap.get(name+" _int");
				if (m == null) {
					Class<?> targs[] = { int.class };
					m = delegateClass.getMethod(name, targs);
					methodMap.put(name+" _int", m);
				}
				singleArg[0] = new Integer(value);

				m.invoke(delegate, singleArg);
			} catch (Exception e) {
				throw new SignalMLCodecException(e);
			}
		} else {
			throw new SignalMLCodecException("object is null");
		}
	}

	private void setValue(String name, float value) throws SignalMLCodecException {
		if (delegate != null) {
			try {
				Method m = methodMap.get(name+" _float");
				if (m == null) {
					Class<?> targs[] = { float.class };
					m = delegateClass.getMethod(name, targs);
					methodMap.put(name+" _float", m);
				}
				singleArg[0] = new Float(value);

				m.invoke(delegate, singleArg);
			} catch (Exception e) {
				throw new SignalMLCodecException(e);
			}
		} else {
			throw new SignalMLCodecException("object is null");
		}
	}

	private Object getValue(String name) throws SignalMLCodecException {
		try {
			Method m = methodMap.get(name);
			if (m == null) {
				m = delegateClass.getMethod(name, (Class<?>[]) null);
				if (m == null) {
					throw new Exception("method: " + name + " not found");
				}
				methodMap.put(name, m);
			}
			return m.invoke(delegate, noArg);
		} catch (Exception e) {
			throw new SignalMLCodecException(e);
		}
	}

	private int getInteger(Object o) throws SignalMLCodecException {
		if (o instanceof Integer) {
			return ((Integer) o).intValue();
		} else if (o instanceof Float) {
			return ((Float) o).intValue();
		} else if (o instanceof Double) {
			return ((Double) o).intValue();
		} else if (o instanceof Short) {
			return ((Short) o).intValue();
		} else if (o instanceof String) {
			try {
				return Integer.parseInt((String) o);
			} catch (NumberFormatException e) {
				throw new SignalMLCodecException(e);
			}
		} else if (o instanceof Byte) {
			return ((Byte) o).intValue();
		} else {
			throw new SignalMLCodecException("Integer: type not supported !");
		}
	}

	private String getString(Object o) {
		return (o instanceof String) ? (String) o : o.toString();
	}

	private boolean getBoolean(Object o) {
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue();
		} else {
			return false;
		}
	}

	private float getFloat(Object o) throws SignalMLCodecException {
		if (o instanceof Integer) {
			return ((Integer) o).intValue();
		} else if (o instanceof Float) {
			return ((Float) o).floatValue();
		} else if (o instanceof Double) {
			return (float)((Double) o).doubleValue();
		} else if (o instanceof Short) {
			return ((Short) o).intValue();
		} else if (o instanceof Byte) {
			return ((Byte) o).intValue();
		} else if (o instanceof String) {
			try {
				return Float.valueOf((String) o).floatValue();
			} catch (NumberFormatException e) {
				throw new SignalMLCodecException(e);
			}
		}
		else if (o instanceof float[]) {
			float[] arr = (float[]) o;
			float max = 0;
			for (int i=0; i<arr.length; i++) {
				if (arr[i] > max) {
					max = arr[i];
				}
			}
			return max;
		}
		else {
			throw new SignalMLCodecException("Unable to interpret float value of class [" + o.getClass().getName() + "]");
		}
	}

	private float getFloat(Object o, int index) throws SignalMLCodecException {
		if (o instanceof Integer) {
			return ((Integer) o).intValue();
		} else if (o instanceof Float) {
			return ((Float) o).floatValue();
		} else if (o instanceof Double) {
			return (float)((Double) o).doubleValue();
		} else if (o instanceof Short) {
			return ((Short) o).intValue();
		} else if (o instanceof Byte) {
			return ((Byte) o).intValue();
		} else if (o instanceof String) {
			try {
				return Float.valueOf((String) o).floatValue();
			} catch (NumberFormatException e) {
				throw new SignalMLCodecException(e);
			}
		}
		else if (o instanceof float[]) {
			float[] arr = (float[]) o;
			try {
				return arr[index];
			} catch (IndexOutOfBoundsException ex) {
				throw new SignalMLCodecException(ex);
			}
		}
		else {
			throw new SignalMLCodecException("Unable to interpret float value of class [" + o.getClass().getName() + "]");
		}
	}

	public void open(String filename) throws SignalMLCodecException {
		if (delegate != null) {
			try {
				if (openMethod == null) {
					Class<?> targs[] = { String.class };
					openMethod = delegateClass.getMethod("open", targs);
				}
				singleArg[0] = filename;

				openMethod.invoke(delegate, singleArg);
			} catch (Exception e) {
				throw new SignalMLCodecException(e);
			}
		} else {
			throw new SignalMLCodecException("object is null");
		}
		this.currentFilename = filename;
	}

	public void close() {
		if (delegate != null) {
			try {
				if (closeMethod == null) {
					closeMethod = delegateClass.getMethod("close", (Class<?>[]) null);
				}
				closeMethod.invoke(delegate, noArg);
			} catch (Exception e) {
				;
			} finally {
				delegate = null;
				delegateClass = null;
				currentFilename = null;
			}
		}
	}

	public boolean is_number_of_channels() throws SignalMLCodecException {
		return getBoolean(getValue("is_number_of_channels"));
	}

	public int get_number_of_channels() throws SignalMLCodecException {
		return getInteger(getValue("get_number_of_channels"));
	}

	public String getFormatID() throws SignalMLCodecException {
		return getString(getValue("getFormatID"));
	}

	public String getFormatDescription() throws SignalMLCodecException {
		return getString(getValue("getFormatDescription"));
	}

	public float get_sampling_frequency(int channel) throws SignalMLCodecException {
		return getFloat(getValue("get_sampling_frequency"), channel);
	}

	public float get_sampling_frequency() throws SignalMLCodecException {
		return getFloat(getValue("get_sampling_frequency"));
	}

	public boolean is_sampling_frequency() throws SignalMLCodecException {
		return getBoolean(getValue("is_sampling_frequency"));
	}

	public boolean is_uniform_sampling_frequency() throws SignalMLCodecException {
		Object value = getValue("get_sampling_frequency");
		if (value instanceof float[]) {
			return false;
		}
		return true;
	}

	public int get_max_offset() throws SignalMLCodecException {
		return getInteger(getValue("get_max_offset"));
	}

	public boolean is_channel_names() throws SignalMLCodecException {
		return getBoolean(getValue("is_channel_names"));
	}

	public String[] get_channel_names() throws SignalMLCodecException {
		Object o = getValue("get_channel_names");
		if (o instanceof String[]) {
			return (String[]) o;
		}
		throw new SignalMLCodecException("Invalid return type: " + o.getClass());
	}

	public boolean is_calibration() throws SignalMLCodecException {
		return getBoolean(getValue("is_calibration"));
	}

	public void set_sampling_frequency(float freq) throws SignalMLCodecException {
		setValue("set_sampling_frequency", freq);
	}

	public void set_number_of_channels(int n) throws SignalMLCodecException {
		setValue("set_number_of_channels", n);
	}

	public void set_calibration(float calib) throws SignalMLCodecException {
		setValue("set_calibration", calib);
	}

	public boolean is_good_spec() throws SignalMLCodecException {
		return is_calibration() && is_sampling_frequency() && is_number_of_channels();
	}

	public float[] getSample(long offset) throws SignalMLCodecException {
		if (delegate != null) {
			try {
				return delegate.getSample(offset);
			} catch (Exception e) {
				throw new SignalMLCodecException(e);
			}
		} else {
			throw new SignalMLCodecException("object is null");
		}
	}

	public float getChannelSample(long offset, int chn) throws SignalMLCodecException {
		if (delegate != null) {
			try {
				return delegate.getChannelSample(offset,chn);
			} catch (Exception e) {
				throw new SignalMLCodecException(e);
			}
		} else {
			throw new SignalMLCodecException("object is null");
		}
	}

	public String getCurrentFilename() {
		return currentFilename;
	}

}

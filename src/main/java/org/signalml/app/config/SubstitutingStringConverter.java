/* ColorConverter.java created 2007-09-19
 *
 */

package org.signalml.app.config;

import java.util.HashMap;
import java.util.Map;

import org.signalml.app.SvarogApplication;
import org.signalml.util.Util;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/** ColorConverter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SubstitutingStringConverter implements Converter {

	private Map<String,String> tokens;
	private Map<String,String> invTokens;

	public SubstitutingStringConverter() {
		tokens = new HashMap<String,String>();
		tokens.put("profile", SvarogApplication.getProfileDir().getAbsolutePath());

		invTokens = Util.invertStringMap(tokens);
	}

	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		writer.setValue(Util.substituteForTokens((String) value, invTokens, false));
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		return Util.expandTokens(reader.getValue(), tokens);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class clazz) {
		return String.class.isAssignableFrom(clazz);
	}

}

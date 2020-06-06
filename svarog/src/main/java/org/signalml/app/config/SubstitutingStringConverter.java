/* ColorConverter.java created 2007-09-19
 *
 */

package org.signalml.app.config;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.signalml.app.SvarogApplication;
import org.signalml.util.Util;

/** ColorConverter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SubstitutingStringConverter implements Converter {

	private final Map<String,String> tokens, invTokens;

	public SubstitutingStringConverter() {
		tokens = new HashMap<String,String>();
		File dir = SvarogApplication.getSharedInstance().getProfileDir();
		if (dir != null)
			tokens.put("profile", dir.getAbsolutePath());

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

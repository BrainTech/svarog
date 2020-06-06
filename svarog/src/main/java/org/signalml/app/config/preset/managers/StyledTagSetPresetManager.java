/* TimeDomainSampleFilterPresetManager.java created 2010-09-22
 *
 */

package org.signalml.app.config.preset.managers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.NativeFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.XMLUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * {@link PresetManager} that stores and loads {@link StyledTagSet StyledTagSets}.
 * StyledTagSets are saved only for the future use of {@link TagStyle TagStyles}
 * that are defined inside of them.
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("styledTagSets")
public class StyledTagSetPresetManager extends AbstractPresetManager {

	public static String EMPTY_PRESET_NAME = _("< Empty preset—no styles defined >");

	@Override
	public String getStandardFilename() {
		return "tagStyles.xml";
	}

	@Override
	public Class<?> getPresetClass() {
		return StyledTagSet.class;
	}

	@Override
	public XStream getStreamer() {

		if (streamer == null)
			streamer = createStreamer();
		return streamer;

	}

	/**
	 * Creates a streamer capable of saving/loading the {@link StyledTagSet}.
	 * @return
	 */
	private XStream createStreamer() {

		XStream streamer = new XStream(
			new PureJavaReflectionProvider(new FieldDictionary(new NativeFieldKeySorter())),
		new DomDriver("UTF-8", new XmlFriendlyReplacer() {

			// the classes in question don't have $'s in their names and the
			// format specifies single underscores, so disregard replacing
			@Override
			public String escapeName(String name) {
				return name;
			}
			@Override
			public String unescapeName(String name) {
				return name;
			}

		}

						 ));
		Annotations.configureAliases(streamer,
									 StyledTagSet.class
									);
		XMLUtils.configureStreamerForMontage(streamer);

		return streamer;

	}

	/**
	 * Returns all presets that are stored using this PresetManager plus
	 * an empty preset in which no styles are defined.
	 * @return empty preset plus all presets that are returned by the
	 * {@link StyledTagSetPresetManager#getPresets()} method.
	 */
	public Preset[] getPresetsWithEmptyOption() {

		StyledTagSet emptyPreset = new StyledTagSet();
		emptyPreset.setName(EMPTY_PRESET_NAME);

		Preset[] nonEmptyPresets = getPresets();
		Preset[] allPresets = new Preset[nonEmptyPresets.length + 1];
		allPresets[0] = emptyPreset;

		for (int i = 0; i < nonEmptyPresets.length; i++)
			allPresets[i + 1] = nonEmptyPresets[i];

		return allPresets;
	}

}

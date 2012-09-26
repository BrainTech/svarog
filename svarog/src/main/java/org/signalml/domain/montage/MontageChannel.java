/* MontageChannel.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents a channel of a {@link Montage montage}.
 * Montage channel is a difference between the voltage of the electrode
 * and voltage of some reference (may be another electrode or average of electrodes).
 *
 * This class is composed of one selected {@link SourceChannel source channel} and
 * references to another channels.
 * It contains also exclusions for {@link MontageSampleFilter filters}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("montagechannel")
public class MontageChannel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * primary {@link SourceChannel soruce channel} to which the current
	 * object is related
	 */
	private SourceChannel primaryChannel;

	/**
	 * label for this channel
	 */
	private String label;

	/**
	 * a HashMap associating other {@link SourceChannel soruce channels}
	 * with Strings representing their reference to the primaryChannel
	 */
	private HashMap<SourceChannel,String> referenceMap;


	private boolean excludeAllFilters = false;

	private transient Boolean bipolarCache = null;

	/**
	 * Constructor. Creates an empty MontageChannel.
	 */
	protected MontageChannel() {
	}

	/**
	 * Constructor. Creates MontageChannel with a given primary channel.
	 * No references are set.
	 * @param primaryChannel a {@link SourceChannel soruce channel}
	 * which will be primary for created object
	 */
	public MontageChannel(SourceChannel primaryChannel) {
		this.primaryChannel = primaryChannel;
		referenceMap = new HashMap<SourceChannel, String>();
	}

	/**
	 * Copy constructor.
	 * @param channel a MontageChannel object to be copied
	 * @param sourceChannels a list of source channels needed for creation
	 */
	public MontageChannel(MontageChannel channel, ArrayList<SourceChannel> sourceChannels) {
		this.primaryChannel = sourceChannels.get(channel.primaryChannel.getChannel());
		this.label = channel.label;
		Set<Entry<SourceChannel, String>> entrySet = channel.referenceMap.entrySet();
		this.referenceMap = new HashMap<SourceChannel, String>(entrySet.size());
		for (Entry<SourceChannel, String> e : entrySet) {
			this.referenceMap.put(sourceChannels.get(e.getKey().getChannel()), e.getValue());
		}
		this.excludeAllFilters = channel.excludeAllFilters;
	}

	/**
	 * Returns the primary channel for this montage channel.
	 * @return the primary channel for this montage channel
	 */
	public SourceChannel getPrimaryChannel() {
		return primaryChannel;
	}

	/**
	 * Returns the label of this montage channel.
	 * @return the label of this montage channel
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets a label to a given value.
	 * @param label String with a label to be set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns a reference to a given {@link SourceChannel source channel}.
	 * @param channel a SourceChannel to which reference is to be found
	 * @return the found reference
	 */
	public String getReference(SourceChannel channel) {
		if (channel == primaryChannel) {
			return "1";
		}
		return referenceMap.get(channel);
	}

	/**
	 * Sets reference to a given {@link SourceChannel source channel} to
	 * a given value.
	 * @param channel a SourceChannel to which reference is to be set
	 * @param value a value of reference to be set
	 * @throws NumberFormatException if String is not a valid
	 * reference (holds neither float value nor a fraction)
	 */
	public void setReference(SourceChannel channel, String value) throws NumberFormatException {
		if (channel == primaryChannel) {
			return;
		}
		bipolarCache = null;
		if (value == null || value.isEmpty()) {
			referenceMap.remove(channel);
		} else {
			// validate reference without saving the float result
			parseReference(value);
			referenceMap.put(channel, value);
		}
	}

	/**
	 * Removes a reference to a given {@link SourceChannel source channel}.
	 * @param channel a source channel to which reference is to be removed
	 */
	public void removeReference(SourceChannel channel) {
		bipolarCache = null;
		referenceMap.remove(channel);
	}

	/**
	 * Checks if there is a reference to a given
	 * {@link SourceChannel source channel}.
	 * @param channel a source channel to which we are looking for
	 * a reference
	 * @return true if a reference to a given source channel exists,
	 * false otherwise
	 */
	public boolean hasReference(SourceChannel channel) {
		return referenceMap.containsKey(channel);
	}

	/**
	 * Checks if a current object has any reference.
	 * @return true if current object has any reference, false otherwise
	 */
	public boolean hasReference() {
		return !referenceMap.isEmpty();
	}

	/**
	 * Returns all references of this montage channel in the form of Strings.
	 * @param references all references of a current object
	 */
	public void getReferences(String[] references) {

		Set<Entry<SourceChannel, String>> entrySet = referenceMap.entrySet();
		Arrays.fill(references, null);
		references[ primaryChannel.getChannel()] = "1";
		for (Entry<SourceChannel, String> e : entrySet) {
			references[ e.getKey().getChannel()] = e.getValue();
		}

	}

	/**
	 * Returns all references of this montage channel in the form of floats.
	 * @param references an array in which references of a current object
	 * will be remembered
	 * @throws NumberFormatException if any String reference is
	 * not a valid reference (holds neither float value nor a fraction)
	 */
	public void getReferencesAsFloat(float[] references) throws NumberFormatException {
		Set<Entry<SourceChannel, String>> entrySet = referenceMap.entrySet();
		Arrays.fill(references, 0F);
		references[ primaryChannel.getChannel()] = 1F;
		for (Entry<SourceChannel, String> e : entrySet) {
			references[ e.getKey().getChannel()] = parseReference(e.getValue());
		}
	}

	/**
	 * Sets references of this montage channel to a given values.
	 * @param references an array of Strings representing references
	 * @param sourceChannels a list of source channels
	 * @throws NumberFormatException thrown when any String reference
	 * is not a valid reference (holds neither float value nor a fraction)
	 */
	public void setReferences(String[] references, ArrayList<SourceChannel> sourceChannels) throws NumberFormatException {
		int primaryIndex = primaryChannel.getChannel();
		int i;
		for (i=0; i<references.length; i++) {
			// validate reference without saving the result
			parseReference(references[i]);
		}
		bipolarCache = null;
		referenceMap.clear();
		for (i=0; i<references.length; i++) {
			if (references[i] == null || references[i].isEmpty()) {
				continue;
			}
			referenceMap.put(sourceChannels.get(i), references[i]);
		}
	}

	/**
	 * Parses a String with a reference and returns reference in the
	 * form of a float.
	 * @param ref a String to be parsed
	 * @return float a value of a reference
	 * @throws NumberFormatException thrown when String is not
	 * a valid reference (holds neither float value nor a fraction)
	 */
	public static float parseReference(String ref) throws NumberFormatException {

		if (ref == null) {
			return(0F);
		}
		String item = ref.trim();
		if (item.isEmpty()) {
			return(0F);
		}
		String[] parts;
		int hi, lo;
		if (item.contains("/")) {
			parts = item.split("\\s*/\\s*");
			if (parts == null || parts.length != 2) {
				throw new NumberFormatException("error.badReferenceValue");
			}
			hi = Integer.parseInt(parts[0]);
			lo = Integer.parseInt(parts[1]);
			if (lo == 0) {
				throw new NumberFormatException("error.badReferenceValue");
			}
			return (((float) hi) / lo);
		} else {
			try {
				return Float.parseFloat(item);
			} catch (NumberFormatException ex) {
				throw new NumberFormatException("error.badReferenceValue");
			}
		}
	}

	/**
	 * Checks if a String is a valid reference (holds float value or
	 * a fraction).
	 * @param ref a String to be checked
	 * @return true if a String is a valid reference, false otherwise
	 */
	public static boolean isCorrectReference(String ref) {

		try {
			parseReference(ref);
		} catch (NumberFormatException ex) {
			return false;
		}

		return true;

	}

	/**
	 * Returns if this montage channel has a bipolar reference
	 * (channel without references is bipolar).
	 * @return true if this montage channel has a bipolar reference,
	 * false otherwise
	 */
	public boolean isBipolarReference() {
		if (bipolarCache == null) {
			int size = referenceMap.size();
			if (size == 0) {
				bipolarCache = true;
			}
			else if (size > 1) {
				bipolarCache = false;
			}
			else if (!referenceMap.entrySet().iterator().next().getValue().equals("-1")) {
				bipolarCache = false;
			} else {
				bipolarCache = true;
			}
		}
		return bipolarCache.booleanValue();
	}

	/**
	 * Checks if the reference to a given
	 * {@link SourceChannel source channel} is symmetric.
	 * @param channel a SourceChannel object
	 * @return true if the reference is symmetric, false otherwise
	 */
	//TODO to dla size 2 będzie symetryczne przy reference "-1/2", ale nie będzie przy "-0.5". Czy tak ma być?
	public boolean isSymmetricWeight(SourceChannel channel) {
		if (channel == primaryChannel) {
			return false;
		}
		int size = referenceMap.size();
		String ref = referenceMap.get(channel);
		if (ref == null) {
			return false;
		}
		return ref.equals("-1/" + size);
	}

	/**
	 * Checks if a given montage channel has the same references as
	 * this montage channel.
	 * @param channel a MontageChannel to be compared to the current object
	 * @param sourceChannels a list of source channels
	 * @return true if a given montage channel has the same references as
	 * this montage channel, false otherwise
	 */
	public boolean isEqualReference(MontageChannel channel, ArrayList<SourceChannel> sourceChannels) {
		int size = referenceMap.size();
		if (size != channel.referenceMap.size()) {
			return false;
		}
		Set<Entry<SourceChannel,String>> entrySet = referenceMap.entrySet();
		String other;
		for (Entry<SourceChannel, String> entry : entrySet) {
			other = channel.referenceMap.get(sourceChannels.get(entry.getKey().getChannel()));
			if (other == null || !other.equals(entry.getValue())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns if all filters are excluded.
	 * @return true if all filters are excluded, false otherwise
	 */
	public boolean isExcludeAllFilters() {
		return excludeAllFilters;
	}

	/**
	 * Set excludeAllFilters parameter to a given value.
	 * @param excludeAllFilters a value to be set
	 */
	public void setExcludeAllFilters(boolean excludeAllFilters) {
		this.excludeAllFilters = excludeAllFilters;
	}

}

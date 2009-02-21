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

/** MontageChannel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("montagechannel")
public class MontageChannel implements Serializable {

	private static final long serialVersionUID = 1L;

	private SourceChannel primaryChannel;
	
	private String label;
	
	private HashMap<SourceChannel,String> referenceMap;
	private boolean excludeAllFilters = false;
	
	private transient Boolean bipolarCache = null;

	protected MontageChannel() {
	}
	
	public MontageChannel(SourceChannel primaryChannel) {
		this.primaryChannel = primaryChannel;
		referenceMap = new HashMap<SourceChannel, String>();
	}

	public MontageChannel(MontageChannel channel, ArrayList<SourceChannel> sourceChannels ) {
		this.primaryChannel = sourceChannels.get( channel.primaryChannel.getChannel() );
		this.label = channel.label;
		Set<Entry<SourceChannel, String>> entrySet = channel.referenceMap.entrySet();
		this.referenceMap = new HashMap<SourceChannel, String>(entrySet.size());
		for( Entry<SourceChannel, String> e : entrySet ) {
			this.referenceMap.put( sourceChannels.get( e.getKey().getChannel() ), e.getValue() );
		}
		this.excludeAllFilters = channel.excludeAllFilters;
	}

	public SourceChannel getPrimaryChannel() {
		return primaryChannel;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getReference( SourceChannel channel ) {
		if( channel == primaryChannel ) {
			return "1";
		}
		return referenceMap.get(channel);
	}
	
	public void setReference( SourceChannel channel, String value ) throws NumberFormatException {
		if( channel == primaryChannel ) {
			return;
		}
		bipolarCache = null;
		if( value == null || value.isEmpty() ) {
			referenceMap.remove( channel );
		} else {
			// validate reference without saving the float result
			parseReference(value);
			referenceMap.put( channel, value );
		}
	}
	
	public void removeReference( SourceChannel channel ) {
		bipolarCache = null;
		referenceMap.remove( channel );
	}
	
	public boolean hasReference( SourceChannel channel ) {
		return referenceMap.containsKey( channel );
	}

	public boolean hasReference() {
		return !referenceMap.isEmpty();
	}
	
	public void getReferences( String[] references ) {
		
		Set<Entry<SourceChannel, String>> entrySet = referenceMap.entrySet();
		Arrays.fill(references, null);
		references[ primaryChannel.getChannel() ] = "1";
		for( Entry<SourceChannel, String> e : entrySet ) {
			references[ e.getKey().getChannel() ] = e.getValue();
		}
		
	}
	
	public void getReferencesAsFloat( float[] references ) throws NumberFormatException {
		Set<Entry<SourceChannel, String>> entrySet = referenceMap.entrySet();
		Arrays.fill(references, 0F);
		references[ primaryChannel.getChannel() ] = 1F;
		for( Entry<SourceChannel, String> e : entrySet ) {
			references[ e.getKey().getChannel() ] = parseReference( e.getValue() );
		}		
	}
	
	public void setReferences( String[] references, ArrayList<SourceChannel> sourceChannels ) throws NumberFormatException {
		int primaryIndex = primaryChannel.getChannel();
		int i;
		for( i=0; i<references.length; i++ ) {
			if( i == primaryIndex ) {
				continue;
			}
			// validate reference without saving the result
			parseReference(references[i]);
		}
		bipolarCache = null;
		referenceMap.clear();
		for( i=0; i<references.length; i++ ) {
			if( i == primaryIndex ) {
				continue;
			}
			if( references[i] == null || references[i].isEmpty() ) {
				continue;
			}
			referenceMap.put( sourceChannels.get(i), references[i] );
		}
	}
		
	public static float parseReference( String ref ) throws NumberFormatException {
		
		if( ref == null ) {
			return( 0F );
		}
		String item = ref.trim();
		if( item.isEmpty() ) {
			return( 0F );
		}
		String[] parts;
		int hi, lo;
		if( item.contains("/") ) {
			parts = item.split("\\s*/\\s*");
			if( parts == null || parts.length != 2 ) {
				throw new NumberFormatException("error.badReferenceValue");
			}
			hi = Integer.parseInt(parts[0]);
			lo = Integer.parseInt(parts[1]);
			if( lo == 0 ) {
				throw new NumberFormatException("error.badReferenceValue");
			}
			return ( ((float) hi) / lo );
		} else {
			try {
				return Float.parseFloat(item);
			} catch( NumberFormatException ex ) {
				throw new NumberFormatException("error.badReferenceValue");
			}
		}		
		
	}
	
	public static boolean isCorrectReference( String ref ) {
		
		try {
			parseReference(ref);
		} catch( NumberFormatException ex ) {
			return false;
		}

		return true;
		
	}

	public boolean isBipolarReference() {
		if( bipolarCache == null ) {
			int size = referenceMap.size();
			if( size == 0 ) {
				bipolarCache = true;
			}
			else if( size > 1 ) {
				bipolarCache = false;
			}
			else if( !referenceMap.entrySet().iterator().next().getValue().equals("-1") ) {
				bipolarCache = false;
			}
			else {
				bipolarCache = true;
			}
		}
		return bipolarCache.booleanValue();
	}
	
	public boolean isSymmetricWeight( SourceChannel channel ) {
		if( channel == primaryChannel ) {
			return false;
		}
		int size = referenceMap.size();		
		String ref = referenceMap.get(channel);
		if( ref == null ) {
			return false;
		}
		return ref.equals( "-1/" + size );		
	}
	
	public boolean isEqualReference( MontageChannel channel, ArrayList<SourceChannel> sourceChannels ) {
		int size = referenceMap.size();		
		if( size != channel.referenceMap.size() ) {
			return false;
		}
		Set<Entry<SourceChannel,String>> entrySet = referenceMap.entrySet();
		String other;
		for( Entry<SourceChannel, String> entry : entrySet ) {
			other = channel.referenceMap.get( sourceChannels.get( entry.getKey().getChannel() ) );
			if( other == null || !other.equals(entry.getValue()) ) {
				return false;
			}
		}
		return true;
	}

	public boolean isExcludeAllFilters() {
		return excludeAllFilters;
	}

	public void setExcludeAllFilters(boolean excludeAllFilters) {
		this.excludeAllFilters = excludeAllFilters;
	}
	
}

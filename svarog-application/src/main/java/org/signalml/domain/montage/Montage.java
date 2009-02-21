/* Montage.java created 2007-10-23
 * 
 */

package org.signalml.domain.montage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.util.Util;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** Montage
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("montage")
public class Montage extends SourceMontage implements Preset {

	private static final long serialVersionUID = 1L;

	public static final String MONTAGE_GENERATOR_PROPERTY = "montageGenerator";
	public static final String NAME_PROPERTY = "montageGenerator";
	public static final String DESCRIPTION_PROPERTY = "description";
	public static final String MAJOR_CHANGE_PROPERTY = "majorChange";
	public static final String FILTERING_ENABLED_PROPERTY = "filteringEnabled";
	
	private String name;
	private String description;
	
	private MontageGenerator montageGenerator;
	private ArrayList<MontageChannel> montageChannels;
	
	private transient HashMap<SourceChannel,LinkedList<MontageChannel>> montageChannelsByPrimary;
	private transient HashMap<String,MontageChannel> montageChannelsByLabel;
	
	private ArrayList<MontageSampleFilter> filters = new ArrayList<MontageSampleFilter>();
	private boolean filteringEnabled = true;
	
	private transient boolean majorChange = false;
	
	protected Montage() {
		super();
	}
	
	public Montage( Montage montage ) {
		this();
		copyFrom(montage);
	}
	
	public Montage( SourceMontage sourceMontage ) {
		this();
		super.copyFrom(sourceMontage);
		
		montageChannels = new ArrayList<MontageChannel>();
	}
	
	public Montage( SignalDocument document ) {
		super( document );
		
		montageChannels = new ArrayList<MontageChannel>();
	}
	
	@Override
	public Montage clone() {
		Montage montage = new Montage();
		montage.copyFrom(this);
		return montage;
	}
	
	protected void copyFrom( Montage montage ) {
		super.copyFrom(montage);
				
		// listeners are not copied
		
		montageChannels = new ArrayList<MontageChannel>(montage.montageChannels.size());
		HashMap<String, MontageChannel> map = getMontageChannelsByLabel();
		map.clear();
		getMontageChannelsByPrimary().clear();
		MontageChannel newChannel;
		LinkedList<MontageChannel>  list;
		for( MontageChannel channel : montage.montageChannels ) {
			newChannel = new MontageChannel( channel, sourceChannels );
			list = getMontageChannelsByPrimaryList( newChannel.getPrimaryChannel() );
			montageChannels.add( newChannel );
			map.put( newChannel.getLabel(), newChannel );
			list.add( newChannel );
		}
		
		if( montage.filters != null ) {
			filters = new ArrayList<MontageSampleFilter>(montage.filters.size());
			MontageSampleFilter newFilter;
			for( MontageSampleFilter filter : montage.filters ) {
				newFilter = new MontageSampleFilter( filter, montageChannels, montage.montageChannels );
				filters.add( newFilter );
			}
		} else {
			filters = new ArrayList<MontageSampleFilter>();
		}
		filteringEnabled = montage.filteringEnabled;
		
		setName( montage.name );
		setDescription( montage.description );
		setMontageGenerator( montage.montageGenerator );
		
		fireMontageStructureChanged(this);		
		
	}
	
	public boolean isCompatible(Montage montage) {
		
		boolean sourceCompatible = super.isCompatible(montage);
		if( !sourceCompatible ) {
			return false;
		}
		
		int cnt = getMontageChannelCount();
		int mCnt = montage.getMontageChannelCount();
		
		if( cnt != mCnt ) {
			// different montage channel count
			return false;
		}
		
		// now for each source channel check that the montage channels
		// in each montage have same references. Order, labels don't matter
		int srcCnt = getSourceChannelCount();
		int i;
		LinkedList<MontageChannel> ourChannels;
		LinkedList<MontageChannel> theirChannels;
		int ourSize;
		int theirSize;
		boolean found;
		MontageChannel our;
		Iterator<MontageChannel> theirIt;
		Iterator<MontageChannel> ourIt;
		
		for( i=0; i<srcCnt; i++ ) {
			
			ourChannels = getMontageChannelsByPrimaryList(sourceChannels.get(i));
			theirChannels = montage.getMontageChannelsByPrimaryList(montage.sourceChannels.get(i));
			
			ourSize = ourChannels.size();
			theirSize = theirChannels.size();
			
			if( ourSize != theirSize ) {
				// different montage channel count
				return false;
			}
			if( ourSize == 0 ) {
				continue;
			}
						
			ourIt = ourChannels.iterator();
			while( ourIt.hasNext() ) {
				our = ourIt.next();
				theirIt = theirChannels.iterator();
				found = false;
				while( theirIt.hasNext() ) {
					if( our.isEqualReference( theirIt.next(), montage.sourceChannels ) ) {
						found = true;
						break;
					}
				}
				if( !found ) {
					return false;
				}
			}
						
		}
		
		return true;
		
	}
	
	public void reset() {
		
		getMontageChannelsByLabel().clear();
		getMontageChannelsByPrimary().clear();
		montageChannels.clear();

		for( MontageSampleFilter filter : filters ) {
			filter.clearExclusion();
		}
		
		if( !majorChange ) {
			fireMontageStructureChanged(this);
			fireMontageSampleFiltersChanged(this);
		}
		
		setMontageGenerator(null);
		setChanged(true);
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if( !Util.equalsWithNulls( this.name, name ) ) {
			String oldName = this.name;
			this.name = name;
			pcSupport.firePropertyChange(NAME_PROPERTY, oldName, name);
			setChanged( true );
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if( !Util.equalsWithNulls( this.description, description) ) {
			String oldDescription = this.description;
			this.description = description;
			pcSupport.firePropertyChange(DESCRIPTION_PROPERTY, oldDescription, description);
			setChanged( true );
		}
	}
	
	public MontageGenerator getMontageGenerator() {
		return montageGenerator;
	}

	public void setMontageGenerator(MontageGenerator montageGenerator) {
		if( !Util.equalsWithNulls( this.montageGenerator, montageGenerator ) ) {
			MontageGenerator oldGenerator = this.montageGenerator;
			this.montageGenerator = montageGenerator;
			pcSupport.firePropertyChange(MONTAGE_GENERATOR_PROPERTY, oldGenerator, montageGenerator);
			setChanged( true );
		}
	}
		
	public boolean isMajorChange() {
		return majorChange;
	}

	public void setMajorChange(boolean majorChange) {
		if( this.majorChange != majorChange ) {
			this.majorChange = majorChange;
			pcSupport.firePropertyChange(MAJOR_CHANGE_PROPERTY, !majorChange, majorChange);
			if( !majorChange ) {
				// turned off
				fireMontageStructureChanged(this);
				fireMontageSampleFiltersChanged(this);
				setChanged(true);
			}
		}
	}

	
	public boolean isFilteringEnabled() {
		return filteringEnabled;
	}

	public void setFilteringEnabled(boolean filteringEnabled) {
		if( this.filteringEnabled != filteringEnabled ) {
			this.filteringEnabled = filteringEnabled;
			pcSupport.firePropertyChange(FILTERING_ENABLED_PROPERTY, !filteringEnabled, filteringEnabled);
		}
	}

	@Override
	public String toString() {
		return name;
	}

	protected HashMap<SourceChannel, LinkedList<MontageChannel>> getMontageChannelsByPrimary() {
		if( montageChannelsByPrimary == null ) {
			montageChannelsByPrimary = new HashMap<SourceChannel, LinkedList<MontageChannel>>();
		}
		return montageChannelsByPrimary;
	}
	
	protected LinkedList<MontageChannel> getMontageChannelsByPrimaryList( SourceChannel channel ) {
		HashMap<SourceChannel, LinkedList<MontageChannel>> map = getMontageChannelsByPrimary();
		LinkedList<MontageChannel> list = map.get(channel);
		if( list == null ) {
			list = new LinkedList<MontageChannel>();
			map.put(channel, list);
			for( MontageChannel montageChannel : montageChannels ) {
				if( montageChannel.getPrimaryChannel() == channel ) {
					list.add(montageChannel);
				}
			}
		}
		return list;
	}

	protected HashMap<String,MontageChannel> getMontageChannelsByLabel() {
		if( montageChannelsByLabel == null ) {
			montageChannelsByLabel = new HashMap<String, MontageChannel>();
			for( MontageChannel channel : montageChannels ) {
				montageChannelsByLabel.put( channel.getLabel(), channel );
			}
		}
		return montageChannelsByLabel;
	}
	
	protected MontageChannel getMontageChannelByLabel( String label ) {
		return getMontageChannelsByLabel().get(label);
	}
	
	public int getMontageChannelCount() {
		return montageChannels.size();
	}
	
	public int getMontagePrimaryChannelAt( int index ) {
		return montageChannels.get(index).getPrimaryChannel().getChannel();
	}
	
	public String getMontageChannelLabelAt( int index ) {
		return montageChannels.get(index).getLabel();
	}
	
	public String setMontageChannelLabelAt( int index, String label ) throws MontageException {
		
		if( label == null || label.isEmpty() ) {
			throw new MontageException( "error.montageChannelLabelEmpty" );
		}
		if( !Util.validateString(label) ) {
			throw new MontageException( "error.montageChannelLabelBadChars" );
		}

		MontageChannel channel = montageChannels.get(index);
		String oldLabel = channel.getLabel();
		HashMap<String, MontageChannel> map = getMontageChannelsByLabel();
		if( !oldLabel.equals(label) ) {
			MontageChannel namedChannel = map.get(label);
			if( namedChannel != null && namedChannel != channel ) {
				throw new MontageException( "error.montageChannelLabelDuplicate" );
			}
			map.remove(oldLabel);		
			channel.setLabel(label);
			map.put(label, channel);
			if( !majorChange ) {
				fireMontageChannelsChanged(this, new int[] { index }, new int[] { channel.getPrimaryChannel().getChannel() } );
				setChanged(true);
			}
			setMontageGenerator(null);			
		}
		
		return oldLabel;
		
	}
	
	public boolean isSourceChannelInUse( int index ) {
		
		SourceChannel channel = sourceChannels.get(index);
		
		if( !getMontageChannelsByPrimaryList(channel).isEmpty() ) {
			return true;
		}
		
		for( MontageChannel montageChannel : montageChannels ) {
			if( montageChannel.hasReference(channel) ) {
				return true;
			}
		}
		
		return false;
				
	}
	
	public boolean hasReference( int montageIndex, int sourceIndex ) {
		return montageChannels.get(montageIndex).hasReference(sourceChannels.get(sourceIndex));
	}

	public boolean hasReference( int montageIndex ) {
		return montageChannels.get(montageIndex).hasReference();
	}
	
	public int[] getMontageChannelsForSourceChannel( int index ) {

		SourceChannel channel = sourceChannels.get(index);
		
		LinkedList<MontageChannel> list = getMontageChannelsByPrimaryList(channel);
		int[] result = new int[list.size()];
		int cnt = 0;
		
		for( MontageChannel montageChannel : list ) {
			result[cnt] = montageChannels.indexOf(montageChannel);
			cnt++;
		}
		
		return result;
		
	}
	
	public String getNewMontageChannelLabel( String stem ) {
		
		int cnt = 2;
		
		String candidate = stem;
		HashMap<String, MontageChannel> map = getMontageChannelsByLabel();
		while( map.containsKey(candidate) ) {			
			candidate = stem + " (" + cnt + ")";
			cnt++;
		}
		
		return candidate;
		
	}

	public String[] getReference( int index ) {
		String[] references = new String[sourceChannels.size()];
		montageChannels.get(index).getReferences(references);
		return references;
	}
	
	public float[] getReferenceAsFloat( int index ) {
		float[] references = new float[sourceChannels.size()];
		montageChannels.get(index).getReferencesAsFloat(references);
		return references;		
	}
	
	public void setReference( int index, String[] references ) throws NumberFormatException {
		if( references.length > sourceChannels.size() ) {
			throw new IndexOutOfBoundsException( "References too long [" + references.length + "]" );
		}
		MontageChannel channel = montageChannels.get(index); 
		channel.setReferences(references, sourceChannels);
		if( !majorChange ) {
			fireMontageReferenceChanged(this, new int[] { index }, new int[] { channel.getPrimaryChannel().getChannel() } );
			setChanged(true);
		}
		setMontageGenerator(null);		
	}
	
	public String getReference( int montageIndex, int sourceIndex ) {
		return montageChannels.get(montageIndex).getReference( sourceChannels.get(sourceIndex) );
	}
	
	public boolean isReferenceSymmetric( int montageIndex, int sourceIndex ) {
		return montageChannels.get(montageIndex).isSymmetricWeight( sourceChannels.get(sourceIndex) );
	}
	
	public void setReference( int montageIndex, int sourceIndex, String value ) throws NumberFormatException {
		MontageChannel channel = montageChannels.get(montageIndex);
		channel.setReference( sourceChannels.get(sourceIndex), value );		
		if( !majorChange ) {
			fireMontageReferenceChanged(this, new int[] { montageIndex }, new int[] { channel.getPrimaryChannel().getChannel() } );
			setChanged(true);
		}
		setMontageGenerator(null);		
	}
	
	public void removeReference( int montageIndex, int sourceIndex ) {
		MontageChannel channel = montageChannels.get(montageIndex);
		channel.removeReference( sourceChannels.get(sourceIndex) );
		if( !majorChange ) {
			fireMontageReferenceChanged(this, new int[] { montageIndex }, new int[] { channel.getPrimaryChannel().getChannel() } );
			setChanged(true);
		}
		setMontageGenerator(null);		
	}
	
	@Override
	public void addSourceChannel(String label, Channel function) throws MontageException {
		super.addSourceChannel(label, function);
		setMontageGenerator(null);		
	}
	
	@Override
	public SourceChannel removeSourceChannel() {
		
		if( sourceChannels.isEmpty() ) {
			return null;
		}
		SourceChannel channel = sourceChannels.get( sourceChannels.size() - 1 ) ;
		
		LinkedList<MontageChannel> list = getMontageChannelsByPrimaryList(channel);
		MontageChannel montageChannel;
		
		if( !list.isEmpty() ) {
			int[] indices = new int[list.size()];
			int[] primaryIndices = new int[indices.length];
			int cnt = 0;
			Iterator<MontageChannel> it = list.iterator();
			while( it.hasNext() ) {
				montageChannel = it.next();
				indices[cnt] = montageChannels.indexOf(montageChannel);
				primaryIndices[cnt] = montageChannel.getPrimaryChannel().getChannel();
				cnt++;
			}
			it = list.iterator();
			HashMap<String, MontageChannel> map = getMontageChannelsByLabel();

			LinkedList<Integer> filterIndexList = new LinkedList<Integer>();
			int filterCnt = filters.size();
			int i;
			
			while( it.hasNext() ) {
				montageChannel = it.next();			
				map.remove( montageChannel.getLabel() );
				montageChannels.remove(montageChannel);
												
				for( i=0; i<filterCnt; i++ ) {
					if( filters.get(i).removeExcludedChannel( montageChannel ) ) {
						if( !filterIndexList.contains(i) ) {
							filterIndexList.add(i);
						}
					}
				}								
			}
			
			list.clear();
			
			if( !majorChange ) {
				Collections.sort( filterIndexList );
				
				fireMontageChannelsRemoved(this, indices, primaryIndices);
				fireMontageSampleFilterExclusionChanged(this, filterIndexList);
			}
		}

		int size = montageChannels.size();
		for( int i=0; i<size; i++ ) {
			montageChannel = montageChannels.get(i);
			LinkedList<Integer> indexList = new LinkedList<Integer>();
			LinkedList<Integer> primaryIndexList = new LinkedList<Integer>();
			if( montageChannel.hasReference(channel) ) {
				montageChannel.removeReference(channel);
				indexList.add(i);
				primaryIndexList.add(montageChannel.getPrimaryChannel().getChannel());
			}
			if( !indexList.isEmpty() ) {
				if( !majorChange ) {
					fireMontageReferenceChanged(this, indexList, primaryIndexList);
				}
			}
		}
		
		super.removeSourceChannel();
		setMontageGenerator(null);
		
		return channel;
		
	}
			
	protected int addMontageChannelInternal( MontageChannel channel, int atIndex ) {
		getMontageChannelsByLabel().put( channel.getLabel(), channel );
		getMontageChannelsByPrimaryList( channel.getPrimaryChannel() ).add( channel );
		if( atIndex < 0 ) {
			montageChannels.add( channel );
			return montageChannels.size() - 1;
		} else {
			montageChannels.add( atIndex, channel );
			return atIndex;
		}
	}
	
	public int addMontageChannel( int sourceIndex, int atIndex ) {
		int[] sourceIndices = new int[] { sourceIndex };
		int[] indices = addMontageChannels(sourceIndices, atIndex); 
		return indices[0];
	}
	
	public int addMontageChannel( int sourceIndex ) {
		return addMontageChannel(sourceIndex, -1 );
	}
	
	public int[] addMontageChannels( int[] sourceIndices ) {
		return addMontageChannels( sourceIndices, -1 );
	}

	public int[] addMontageChannels( int fromSourceIndex, int count, int atIndex ) {
		int[] sourceIndices = new int[count];
		for( int i=0; i<count; i++ ) {
			sourceIndices[i] = fromSourceIndex + i;			
		}
		return addMontageChannels(sourceIndices, atIndex);
	}

	public int[] addMontageChannels( int fromSourceIndex, int count ) {
		return addMontageChannels(fromSourceIndex, count, -1);
	}
	
	public int[] addMontageChannels( int[] sourceIndices, int atIndex ) {
		int[] indices = new int[sourceIndices.length];
		if( sourceIndices.length == 0 ) {
			return indices; 
		}
		SourceChannel channel;
		MontageChannel montageChannel;
		for( int i=0; i<sourceIndices.length; i++ ) {
			channel = sourceChannels.get( sourceIndices[i] );
			montageChannel = new MontageChannel( channel );
			montageChannel.setLabel( getNewMontageChannelLabel( channel.getLabel() ) );			
			indices[i] = addMontageChannelInternal( montageChannel, ( atIndex < 0 ? atIndex : atIndex+i ) );			
		}
		if( !majorChange ) {
			fireMontageChannelsAdded(this, indices, sourceIndices);
			setChanged(true);
		}
		setMontageGenerator(null);		
		return indices;
	}
	
	public int addBipolarMontageChannel( int sourceIndex, int atIndex, String label, int referenceChannel ) {

		SourceChannel channel = sourceChannels.get( sourceIndex );
		MontageChannel montageChannel = new MontageChannel( channel );
		montageChannel.setLabel( getNewMontageChannelLabel( label ) );
		montageChannel.setReference( sourceChannels.get(referenceChannel), "-1" );
		int index = addMontageChannelInternal( montageChannel, atIndex );
		
		if( !majorChange ) {
			int[] indices = new int[] { index };
			int[] sourceIndices = new int[] { sourceIndex };
			fireMontageChannelsAdded(this, indices, sourceIndices);
			setChanged(true);
		}
		setMontageGenerator(null);		
		return index;
		
	}
	
	public int addBipolarMontageChannel( int sourceIndex, String label, int referenceChannel ) {
		return addBipolarMontageChannel(sourceIndex, -1, label, referenceChannel);
	}
	
	public MontageChannel removeMontageChannel( int index ) {
		int[] indices = new int[] { index };
		MontageChannel[] channels = removeMontageChannels(indices);
		return channels[0];
	}
	
	public MontageChannel[] removeMontageChannels( int fromIndex, int count ) {
		int[] indices = new int[count];
		for( int i=0; i<count; i++ ) {
			indices[i] = fromIndex + i;			
		}
		return removeMontageChannels(indices);
	}
	
	public MontageChannel[] removeMontageChannels( int[] indices ) {
		
		MontageChannel[] channels = new MontageChannel[indices.length];		
		if( indices.length == 0 ) {
			return channels;
		}
		
		int[] sourceIndices = new int[indices.length];
		
		for( int i=0; i<indices.length; i++ ) {
			channels[i] = montageChannels.get(indices[i]);
			sourceIndices[i] = channels[i].getPrimaryChannel().getChannel();
			getMontageChannelsByLabel().remove( channels[i].getLabel() );
			getMontageChannelsByPrimaryList( channels[i].getPrimaryChannel() ).remove( channels[i] );
		}
		
		LinkedList<Integer> filterIndexList = new LinkedList<Integer>();
		
		for( int i=0; i<indices.length; i++ ) {						
			montageChannels.remove( channels[i] );
		}
		
		Iterator<MontageSampleFilter> it = filters.iterator();
		MontageSampleFilter filter;
		while( it.hasNext() ) {
			filter = it.next();
			for( int i=0; i<indices.length; i++ ) {						
				if( filter.removeExcludedChannel( channels[i] ) ) {
					if( !filterIndexList.contains(i) ) {
						filterIndexList.add(i);
					}
				}
			}
		}
		
		if( !majorChange ) {
			
			Collections.sort( filterIndexList );
						
			fireMontageChannelsRemoved(this, indices, sourceIndices);
			fireMontageSampleFilterExclusionChanged(this, filterIndexList);
			setChanged(true);
		}
		
		setMontageGenerator(null);		

		return channels;
		
	}
	
	public int moveMontageChannelRange( int fromIndex, int count, int delta ) {
		
		if( delta == 0 || count == 0 ) {
			return 0;
		}
		
		int possibleDelta;
		int size = montageChannels.size();
		
		if( delta > 0 ) {
			possibleDelta = Math.min( delta, size - (fromIndex+count) );
		} else {
			possibleDelta = Math.max( delta, -fromIndex );
		}
				
		if( possibleDelta == 0 ) {
			return 0;
		}
			
		LinkedList<Integer> indexList = new LinkedList<Integer>();
		LinkedList<Integer> primaryIndexList = new LinkedList<Integer>();
		
		MontageChannel channel;
		
		int i;
		MontageChannel[] cache = new MontageChannel[count];

		for( i=0; i<count; i++ ) {
			cache[i] = montageChannels.get( fromIndex + i );
		}
		
		if( possibleDelta > 0 ) {						
			// rows moved down
			
			for( i=0; i<possibleDelta; i++ ) {
				channel = montageChannels.get( fromIndex + count + i );
				montageChannels.set( fromIndex + i, channel );
				indexList.add( fromIndex + i );
				primaryIndexList.add( channel.getPrimaryChannel().getChannel() );				
			}
							
		} else if( possibleDelta < 0 ) {
			// rows moved up
			
			for( i=-1; i>=possibleDelta; i-- ) {
				channel = montageChannels.get( fromIndex + i );
				montageChannels.set( fromIndex + count + i, channel );
				indexList.add( fromIndex + count + i );
				primaryIndexList.add( channel.getPrimaryChannel().getChannel() );				
			}
							
		}

		for( i=0; i<count; i++ ) {
			montageChannels.set( fromIndex + possibleDelta + i, cache[i] );
			indexList.add( fromIndex + possibleDelta + i );
			primaryIndexList.add( cache[i].getPrimaryChannel().getChannel() );
			
		}
									
		if( !majorChange ) {
			fireMontageStructureChanged(this);
			setChanged(true);
		}
		setMontageGenerator(null);		
		
		return possibleDelta;
		
	}	
		
	public boolean isBipolar() {
		for( MontageChannel channel : montageChannels ) {
			if( !channel.isBipolarReference() ) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isFiltered() {
		if( !filteringEnabled ) {
			return false;
		}
		if( filters.isEmpty() ) {
			return false;
		}
		return true;
	}
	
	public boolean isExcludeAllFilters( int channel ) {
		return montageChannels.get(channel).isExcludeAllFilters();
	}
	
	public void setExcludeAllFilters( int channel, boolean exclude ) {
		MontageChannel montageChannel = montageChannels.get(channel);		
		boolean oldValue = montageChannel.isExcludeAllFilters();
		if( oldValue != exclude ) {
			montageChannel.setExcludeAllFilters(exclude);
			if( !majorChange ) {
				fireMontageSampleFiltersChanged(this);
				setChanged(true);
			}			
		}
	}

	public int getSampleFilterCount() {
		return filters.size();
	}
	
	public SampleFilterDefinition getSampleFilterAt( int index ) {
		return filters.get(index).getDefinition();
	}
	
	public int addSampleFilter( SampleFilterDefinition definition ) {
		
		MontageSampleFilter filter = new MontageSampleFilter( definition );
		filters.add(filter);
		int index = filters.indexOf(filter);
		
		if( !majorChange ) {
			fireMontageSampleFilterAdded(this, new int[] { index } );
			setChanged(true);
		}
		
		return index;
		
	}
	
	public void updateSampleFilter( int index, SampleFilterDefinition definition ) {
		
		MontageSampleFilter montageSampleFilter = filters.get(index);
		montageSampleFilter.setDefinition(definition);
		
		if( !majorChange ) {
			fireMontageSampleFilterChanged(this, new int[] { index } );
			setChanged(true);
		}
				
	}
	
	public SampleFilterDefinition removeSampleFilter( int index ) {
		
		MontageSampleFilter removed = filters.remove(index);
		if( removed == null ) {
			return null;
		}
		
		if( !majorChange ) {
			fireMontageSampleFilterRemoved(this, new int[] { index } );
			setChanged(true);
		}
		
		return removed.getDefinition();
		
	}
	
	public void clearFilters() {
		
		filters.clear();
		
		if( !majorChange ) {
			fireMontageSampleFiltersChanged(this);
			setChanged(true);
		}
		
	}

	public boolean isFilteringExcluded( int filterIndex, int channelIndex ) {
		if( isExcludeAllFilters(channelIndex) ) {
			return true;
		} else {
			MontageSampleFilter filter = filters.get(filterIndex);
			if( !filter.isEnabled() || filter.isChannelExcluded( montageChannels.get(channelIndex) ) ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean[] getFilteringExclusionArray( int filterIndex ) {
		MontageSampleFilter filter = filters.get(filterIndex);
		if( !filter.isEnabled() ) {
			boolean[] trueArray = new boolean[montageChannels.size()];
			Arrays.fill( trueArray, true );
			return trueArray;
		}
		boolean[] exclusionArray = filter.getExclusionArray(montageChannels);
		for( int i=0; i<exclusionArray.length; i++ ) {
			exclusionArray[i] |= isExcludeAllFilters(i);
		}
		return exclusionArray;
	}
	
	public boolean isFilterEnabled( int filterIndex ) {
		return filters.get(filterIndex).isEnabled();
	}
	
	public void setFilterEnabled( int filterIndex, boolean enabled ) {
		MontageSampleFilter filter = filters.get(filterIndex);
		boolean oldEnabled = filter.isEnabled();
		if( oldEnabled != enabled ) {
			filter.setEnabled( enabled );
			
			if( !majorChange ) {
				fireMontageSampleFilterExclusionChanged(this, new int[] { filterIndex } );
				setChanged(true);
			}

		}
	}
	
	public boolean isFilterChannelExcluded( int filterIndex, int channelIndex ) {
		return filters.get(filterIndex).isChannelExcluded( montageChannels.get(channelIndex) );
	}
	
	public boolean[] getFilterExclusionArray( int filterIndex ) {
		return filters.get(filterIndex).getExclusionArray(montageChannels);
	}
	
	public void setFilterChannelExcluded( int filterIndex, int channelIndex, boolean excluded ) {
		
		boolean done;
		if( excluded ) {
			done = filters.get(filterIndex).addExcludedChannel( montageChannels.get(channelIndex) );
		} else {
			done = filters.get(filterIndex).removeExcludedChannel( montageChannels.get(channelIndex) );
		}

		if( done ) {
			if( !majorChange ) {
				fireMontageSampleFilterExclusionChanged(this, new int[] { filterIndex } );
				setChanged(true);
			}
		}
		
	}

	public void clearFilterExclusion( int filterIndex ) {
		boolean done = filters.get(filterIndex).clearExclusion();
		if( done ) {
			if( !majorChange ) {
				fireMontageSampleFilterExclusionChanged(this, new int[] { filterIndex } );
				setChanged(true);
			}
		}
	}
	
	public void addMontageListener(MontageListener l) {
        listenerList.add(MontageListener.class, l);
    }

    public void removeMontageListener(MontageListener l) {
        listenerList.remove(MontageListener.class, l);
    }

	public void addMontageSampleFilterListener(MontageSampleFilterListener l) {
        listenerList.add(MontageSampleFilterListener.class, l);
    }

    public void removeMontageSampleFilterListener(MontageSampleFilterListener l) {
        listenerList.remove(MontageSampleFilterListener.class, l);
    }
    
    protected void fireMontageStructureChanged(Object source) {
        Object[] listeners = listenerList.getListenerList();
        MontageEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageListener.class) {
                if (e == null) {
                    e = new MontageEvent(source, null, null);
                }
                ((MontageListener)listeners[i+1]).montageStructureChanged(e);
            }          
        }
    }
    
    protected void fireMontageChannelsAdded(Object source, int[] channels, int[] primaryChannels) {
        Object[] listeners = listenerList.getListenerList();
        MontageEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageListener.class) {
                if (e == null) {
                    e = new MontageEvent(source, channels, primaryChannels);
                }
                ((MontageListener)listeners[i+1]).montageChannelsAdded(e);
            }          
        }
    }

    protected void fireMontageChannelsRemoved(Object source, int[] channels, int[] primaryChannels) {
        Object[] listeners = listenerList.getListenerList();
        MontageEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageListener.class) {
                if (e == null) {
                    e = new MontageEvent(source, channels, primaryChannels);
                }
                ((MontageListener)listeners[i+1]).montageChannelsRemoved(e);
            }          
        }
    }

    private int[] toArray( LinkedList<Integer> list ) {
		int i = 0;
		int[] indices = new int[list.size()];
		Iterator<Integer> it = list.iterator();
		while( it.hasNext() ) {
			indices[i] = it.next();
			i++;
		}
		return indices;
    }
    
	protected void fireMontageChannelsChanged( Object source, LinkedList<Integer> indexList, LinkedList<Integer> primaryIndexList ) {
		fireMontageChannelsChanged(source, toArray(indexList), toArray(primaryIndexList));		
	}
    
    protected void fireMontageChannelsChanged(Object source, int[] channels, int[] primaryChannels) {
        Object[] listeners = listenerList.getListenerList();
        MontageEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageListener.class) {
                if (e == null) {
                    e = new MontageEvent(source, channels, primaryChannels);
                }
                ((MontageListener)listeners[i+1]).montageChannelsChanged(e);
            }          
        }
    }
    
	protected void fireMontageReferenceChanged(Object source, LinkedList<Integer> indexList, LinkedList<Integer> primaryIndexList) {
		fireMontageReferenceChanged(source, toArray(indexList), toArray(primaryIndexList));		
	}
    
    protected void fireMontageReferenceChanged(Object source, int[] channels, int[] primaryChannels) {
        Object[] listeners = listenerList.getListenerList();
        MontageEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageListener.class) {
                if (e == null) {
                    e = new MontageEvent(source, channels, primaryChannels);
                }
                ((MontageListener)listeners[i+1]).montageReferenceChanged(e);
            }          
        }
    }

    protected void fireMontageSampleFilterAdded(Object source, int[] indices) {
        Object[] listeners = listenerList.getListenerList();
        MontageSampleFilterEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageSampleFilterListener.class) {
                if (e == null) {
                    e = new MontageSampleFilterEvent(source, indices);
                }
                ((MontageSampleFilterListener)listeners[i+1]).filterAdded(e);
            }          
        }
    }

    protected void fireMontageSampleFilterChanged(Object source, int[] indices) {
        Object[] listeners = listenerList.getListenerList();
        MontageSampleFilterEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageSampleFilterListener.class) {
                if (e == null) {
                    e = new MontageSampleFilterEvent(source, indices);
                }
                ((MontageSampleFilterListener)listeners[i+1]).filterChanged(e);
            }          
        }
    }
    
    protected void fireMontageSampleFilterRemoved(Object source, int[] indices) {
        Object[] listeners = listenerList.getListenerList();
        MontageSampleFilterEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageSampleFilterListener.class) {
                if (e == null) {
                    e = new MontageSampleFilterEvent(source, indices);
                }
                ((MontageSampleFilterListener)listeners[i+1]).filterRemoved(e);
            }          
        }
    }

	protected void fireMontageSampleFilterExclusionChanged(Object source, LinkedList<Integer> indexList) {
		fireMontageSampleFilterExclusionChanged(source, toArray(indexList));		
	}
    
    protected void fireMontageSampleFilterExclusionChanged(Object source, int[] indices) {
        Object[] listeners = listenerList.getListenerList();
        MontageSampleFilterEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageSampleFilterListener.class) {
                if (e == null) {
                    e = new MontageSampleFilterEvent(source, indices);
                }
                ((MontageSampleFilterListener)listeners[i+1]).filterExclusionChanged(e);
            }          
        }
    }
    
    protected void fireMontageSampleFiltersChanged(Object source) {
        Object[] listeners = listenerList.getListenerList();
        MontageSampleFilterEvent e = null;
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==MontageSampleFilterListener.class) {
                if (e == null) {
                    e = new MontageSampleFilterEvent(source);
                }
                ((MontageSampleFilterListener)listeners[i+1]).filtersChanged(e);
            }          
        }
    }
    
    
}

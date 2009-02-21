/* AverageReferenceMontageGenerator.java created 2007-11-23
 * 
 */

package org.signalml.domain.montage;

import org.springframework.validation.Errors;

/** AverageReferenceMontageGenerator
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AverageReferenceMontageGenerator implements MontageGenerator {

	protected transient Channel[] refChannels;
	
	protected AverageReferenceMontageGenerator( Channel[] refChannels ) {
		if( refChannels == null || refChannels.length == 0 ) {
			throw new NullPointerException("Channels cannot be null or empty");
		}
		this.refChannels = refChannels;
	}
	
	@Override
	public void createMontage(Montage montage) throws MontageException {

		int[] refChannelIndices = new int[refChannels.length];
		int[] temp;
		int i,e;
		
		for( i=0; i<refChannels.length; i++ ) {
			
			temp = montage.getSourceChannelsByFunction(refChannels[i]);
			if( temp == null || temp.length != 1 ) {
				throw new MontageException( "Bad refChannel count [" + temp.length + "]  for channel [" + refChannels[i] + "]" );
			}
			
			refChannelIndices[i] = temp[0];
			
		}
		
		String token = "-1/" + Integer.toString(refChannels.length);
		
		boolean oldMajorChange = montage.isMajorChange();

		try {			
			montage.setMajorChange(true);
			
			montage.reset();
			
			int size = montage.getSourceChannelCount();
			int index;
			for( i=0; i<size; i++ ) {
				index = montage.addMontageChannel(i);
				if( montage.getSourceChannelFunctionAt(i).getType() == ChannelType.PRIMARY ) {
					for( e=0; e<refChannelIndices.length; e++ ) {
						montage.setReference(index, refChannelIndices[e], token);
					}
				}
			}
		} finally {
			montage.setMajorChange(oldMajorChange);
		}
			
		montage.setMontageGenerator( this );
		montage.setChanged( false );		
		
	}

	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, Errors errors) {
		
		int[] refChannelIndices;
		boolean ok = true;
		
		for( int i=0; i<refChannels.length; i++ ) {
			refChannelIndices = sourceMontage.getSourceChannelsByFunction(refChannels[i]);
			if( refChannelIndices == null || refChannelIndices.length == 0 ) {
				onNotFound(refChannels[i], errors);
				ok = false;
			}
			else if( refChannelIndices.length > 1 ) {
				onDuplicate(refChannels[i], errors);
				ok = false;
			}
		}
		
		return ok;
		
	}

	protected abstract void onNotFound(Channel refChannel, Errors errors);
	protected abstract void onDuplicate(Channel refChannel, Errors errors);
		
}

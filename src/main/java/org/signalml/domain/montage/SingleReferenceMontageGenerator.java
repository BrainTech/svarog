/* SingleChannelReferenceMontageGenerator.java created 2007-11-23
 * 
 */

package org.signalml.domain.montage;

import org.springframework.validation.Errors;

/** SingleChannelReferenceMontageGenerator
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SingleReferenceMontageGenerator implements MontageGenerator {

	private static final long serialVersionUID = 1L;

	protected transient Channel refChannel;
	
	protected SingleReferenceMontageGenerator( Channel refChannel ) {
		if( refChannel == null ) {
			throw new NullPointerException("Channel cannot be null");
		}
		this.refChannel = refChannel;
	}
	
	@Override
	public void createMontage(Montage montage) throws MontageException {

		int[] refChannelIndices = montage.getSourceChannelsByFunction(refChannel);
		if( refChannelIndices == null || refChannelIndices.length != 1 ) {
			throw new MontageException( "Bad refChannel count [" + refChannelIndices.length + "]" );
		}
				
		boolean oldMajorChange = montage.isMajorChange();

		try {			
			montage.setMajorChange(true);
			
			montage.reset();
					
			int size = montage.getSourceChannelCount();
			int index;
			for( int i=0; i<size; i++ ) {
				index = montage.addMontageChannel(i);
				if( montage.getSourceChannelFunctionAt(i).getType() == ChannelType.PRIMARY ) {
					montage.setReference(index, refChannelIndices[0], "-1");
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
		int[] refChannelIndices = sourceMontage.getSourceChannelsByFunction(refChannel);
		if( refChannelIndices == null || refChannelIndices.length == 0 ) {
			onNotFound(errors);
			return false;
		}
		else if( refChannelIndices.length > 1 ) {
			onDuplicate(errors);
			return false;
		}
		return true;		
	}

	protected abstract void onNotFound(Errors errors);
	protected abstract void onDuplicate(Errors errors);

}

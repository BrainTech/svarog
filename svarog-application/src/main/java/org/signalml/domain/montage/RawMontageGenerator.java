/* RawMontageGenerator.java created 2007-11-23
 * 
 */

package org.signalml.domain.montage;

import org.springframework.validation.Errors;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** RawMontageGenerator
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("rawmontage")
public class RawMontageGenerator implements MontageGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Object[] ARGUMENTS = new Object[0];
	private static final String[] CODES = new String[] { "montageGenerator.raw" };
	
	@Override
	public void createMontage( Montage montage ) {

		boolean oldMajorChange = montage.isMajorChange();

		try {
			montage.setMajorChange(true);	
		
			montage.reset();
			
			int size = montage.getSourceChannelCount();
			for( int i=0; i<size; i++ ) {
				montage.addMontageChannel(i);
			}
		} finally {
			montage.setMajorChange(oldMajorChange);
		}
		
		montage.setMontageGenerator( this );
		montage.setChanged( false );
						
	}

	@Override
	public boolean validateSourceMontage(SourceMontage sourceMontage, Errors errors) {
		// ok for any montage
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if( obj == null ) {
			return false;
		}
		return (obj.getClass() == RawMontageGenerator.class); // all raw montage generators are equal
	}
		
	@Override
	public Object[] getArguments() {
		return ARGUMENTS;
	}

	@Override
	public String[] getCodes() {
		return CODES;
	}

	@Override
	public String getDefaultMessage() {
		return CODES[0];
	}

}

/* SignalTypeConfigurer.java created 2007-11-22
 * 
 */

package org.signalml.domain.signal;

import java.awt.Image;
import java.util.Collection;

import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageGenerator;
import org.signalml.domain.montage.filter.SampleFilterDefinition;

/** SignalTypeConfigurer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalTypeConfigurer {

	Montage createMontage( int channelCount );
	Montage createMontage( SignalDocument signalDocument );
	
	Channel[] allChannels();
	Channel genericChannel();
	Channel channelForName( String name );
	
	int getMatrixWidth();
	int getMatrixHeight();

	Image getMatrixBackdrop( int width, int height );
	
	int getMontageGeneratorCount();
	Collection<MontageGenerator> getMontageGenerators();
	MontageGenerator getMontageGeneratorAt( int index );
		
	int getPredefinedFilterCount();
	Collection<SampleFilterDefinition> getPredefinedFilters();
	SampleFilterDefinition getPredefinedFilterAt( int index );
	
}

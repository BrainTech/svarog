/* ParameterRangeAtomFilter.java created 2008-02-25
 * 
 */

package org.signalml.domain.book.filter;

import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.util.MinMaxRange;
import org.signalml.util.MinMaxRangeFloat;
import org.signalml.util.MinMaxRangeInteger;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** ParameterRangeAtomFilter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("parameterbookfilter")
public class ParameterRangeAtomFilter extends AbstractAtomFilter {

	private static final long serialVersionUID = 1L;

	private static final String[] CODES = new String[] { "parameterRangeAtomFilter" };
	private static final Object[] ARGUMENTS = new Object[0];
	
	private MinMaxRangeFloat modulus;
	private MinMaxRangeFloat amplitude;
	private MinMaxRangeFloat position;
	private MinMaxRangeFloat scale;
	private MinMaxRangeFloat frequency;
	private MinMaxRangeFloat phase;
	
	private MinMaxRangeInteger iteration;
	
	public ParameterRangeAtomFilter() {
		super();
		modulus = new MinMaxRangeFloat(MinMaxRange.UNLIMITED, true);
		amplitude = new MinMaxRangeFloat(MinMaxRange.UNLIMITED, true);
		position = new MinMaxRangeFloat(MinMaxRange.UNLIMITED, true);
		scale = new MinMaxRangeFloat(MinMaxRange.UNLIMITED, true);
		frequency = new MinMaxRangeFloat(MinMaxRange.UNLIMITED, true);
		phase = new MinMaxRangeFloat(MinMaxRange.UNLIMITED, true);
		iteration = new MinMaxRangeInteger(MinMaxRange.UNLIMITED, 1,1,true,true);
	}
	
	public ParameterRangeAtomFilter( ParameterRangeAtomFilter template ) {
		super( template );
		modulus = new MinMaxRangeFloat( template.modulus );
		amplitude = new MinMaxRangeFloat( template.amplitude );
		position = new MinMaxRangeFloat( template.position );
		scale = new MinMaxRangeFloat( template.scale );
		frequency = new MinMaxRangeFloat( template.frequency );
		phase = new MinMaxRangeFloat( template.phase );
		iteration = new MinMaxRangeInteger( template.iteration );		
	}
		
	@Override
	public AbstractAtomFilter duplicate() {
		return new ParameterRangeAtomFilter(this);
	}

	public MinMaxRangeFloat getModulus() {
		return modulus;
	}

	public MinMaxRangeFloat getAmplitude() {
		return amplitude;
	}

	public MinMaxRangeFloat getPosition() {
		return position;
	}

	public MinMaxRangeFloat getScale() {
		return scale;
	}

	public MinMaxRangeFloat getFrequency() {
		return frequency;
	}

	public MinMaxRangeFloat getPhase() {
		return phase;
	}
	
	public MinMaxRangeInteger getIteration() {
		return iteration;
	}

	@Override
	public boolean matches(StandardBookSegment segment, StandardBookAtom atom) {

		// TODO what about types?
		
		if( !modulus.isInRangeInclusive( atom.getModulus() ) ) {
			return false;
		}
		if( !amplitude.isInRangeInclusive( atom.getAmplitude() ) ) {
			return false;
		}
		if( !position.isInRangeInclusive( atom.getTimePosition() ) ) {
			return false;
		}
		if( !scale.isInRangeInclusive( atom.getTimeScale() ) ) {
			return false;
		}
		if( !frequency.isInRangeInclusive( atom.getHzFrequency() ) ) {
			return false;
		}
		if( !phase.isInRangeInclusive( atom.getPhase() ) ) {
			return false;
		}
		if( !iteration.isInRangeInclusive( segment.indexOfAtom(atom)+1 ) ) {
			return false;
		}
		
		return true;
		
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
		return "Parameter range atom filter";
	}	
	
}

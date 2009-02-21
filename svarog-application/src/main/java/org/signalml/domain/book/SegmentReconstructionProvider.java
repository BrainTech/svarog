/* SegmentReconstructionProvider.java created 2008-03-03
 * 
 */

package org.signalml.domain.book;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.signalml.util.Util;

import pl.edu.fuw.MP.WignerMap.WignerMap;

/** SegmentReconstructionProvider
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 			(based on code by Dobieslaw Ircha)
 */
public class SegmentReconstructionProvider {

	private StandardBookSegment segment;
	
	private HashMap<StandardBookAtom, double[]> reconstructionMap;
	private double[][] atomReconstructions;
	
	private int width;
		
	private double[] fullReconstruction;
	
	private LinkedHashSet<StandardBookAtom> reconstruction = new LinkedHashSet<StandardBookAtom>();
	
	private double[] selectiveReconstruction;	
	private boolean selectiveReconstructionDirty;
	
	public SegmentReconstructionProvider() {		
	}

	private void clear() {
		
		fullReconstruction = null;
		selectiveReconstruction = null;
		atomReconstructions = null;
		reconstructionMap = null;
		
		if( !reconstruction.isEmpty() ) {
			if( segment == null ) {
				reconstruction.clear();
			} else {
	
				LinkedHashSet<StandardBookAtom> newReconstruction = new LinkedHashSet<StandardBookAtom>();
							
				int atomCount = segment.getAtomCount();
				StandardBookAtom atom;
				for( int i=0; i<atomCount; i++ ) {
					atom = segment.getAtomAt(i);
					if( reconstruction.contains(atom) ) {
						newReconstruction.add(atom);
					}
						
				}
				
				reconstruction = newReconstruction;
				
			}
		}
		
	}
	
	public StandardBookSegment getSegment() {
		return segment;
	}

	public void setSegment(StandardBookSegment segment) {
		if( this.segment != segment ) {
			this.segment = segment;
			clear();
		}
	}

	public void setSegmentWithNaturalWidth(StandardBookSegment segment, float samplingFrequency) {
		if( this.segment != segment ) {
			this.segment = segment;
			if( segment != null ) {
				if( segment.hasSignal() ) {
					width = segment.getSignalSamples().length;
				} else {
					width = (int) ( segment.getSegmentTimeLength() * samplingFrequency );
				}
			} else {
				width = 16;
			}
			clear();
		}
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if( this.width != width ) {
			this.width = width;
			clear();
		}
	}

	public int getAtomCount() {
		if( segment == null ) {
			return 0;
		}
		return segment.getAtomCount();
	}
	
	public double[] getAtomReconstructionAt( int index ) {
		if( atomReconstructions == null ) {
			if( segment == null ) {
				atomReconstructions = new double[0][width];
			} else {
				int atomCount = segment.getAtomCount();
				atomReconstructions = new double[atomCount][width];
			    double  ptr[] = new double[atomCount * width];
			    int i,itmp;
			    int DimBase = segment.getSegmentLength();
			    double tmpsig[]=new double[DimBase+1],sum;
			    double Exp[]=new double[DimBase],Cos[]=new double[DimBase];

			    for (int kk = 0; kk < atomCount; kk++) {
					StandardBookAtom atom = segment.getAtomAt(kk);

					ptr = atomReconstructions[kk];
					if (atom.getType() == StandardBookAtom.DIRACDELTA_IDENTITY) {
						itmp=atom.getPosition();		
						if(atom.getAmplitude()<0f) {
						   ptr[itmp] = -(double) atom.getModulus();
						} else {
						   ptr[itmp] = atom.getModulus();
						}
					} else if (atom.getType() == StandardBookAtom.SINCOSWAVE_IDENTITY) {
						double freq = Math.PI * 2 * atom.getFrequency() / atom.getBaseLength(), phase = atom.getPhase() - freq * atom.getPosition();

						for (i = 0, sum = 0.0; i < DimBase; i++) {
							sum += Util.sqr(tmpsig[i] = Math.cos(freq * i + phase));
						}

						sum = atom.getModulus() / Math.sqrt(sum);
						for (i = 0; i < DimBase; i++) {
							ptr[i] = (tmpsig[i] * sum);
						}
					} else {
						double freq = Math.PI * 2 * atom.getFrequency() / atom.getBaseLength(), phase = atom.getPhase() - freq * atom.getPosition();
						int start = 0, stop = DimBase - 1;

						WignerMap.MakeExpTable(Exp, Math.PI / Util.sqr(atom.getScale()), atom.getPosition(), start, stop);

						WignerMap.makeCosTable(Cos, start, stop, freq, phase);

						for (i = start, sum = 0.0; i <= stop; i++)
							sum += Util.sqr(tmpsig[i] = Exp[i] * Cos[i]);
						/* Math.cos(freq*i+phase) */
						sum = atom.getModulus() / Math.sqrt(sum);
						
						for (i = start; i <= stop; i++) {
							ptr[i] = (tmpsig[i] * sum);
						}
					}

				}
			}
		}
		return atomReconstructions[index];
	}
	
	public double[] getFullReconstruction() {
		if( fullReconstruction == null ) {
			fullReconstruction = new double[width];
			if( segment != null ) {
				int count = segment.getAtomCount();
				double[] atom;
				int i;
				int e;
				for( i=0; i<count; i++ ) {
					atom = getAtomReconstructionAt(i);
					for( e=0; e<width; e++ ) {
						fullReconstruction[e] += atom[e];
					}
				}
			}
		}
		return fullReconstruction;
	}
	
	public HashMap<StandardBookAtom, double[]> getReconstructionMap() {
		if( reconstructionMap == null ) {
			int atomCount = getAtomCount();
			reconstructionMap = new HashMap<StandardBookAtom, double[]>(atomCount);
			for( int i=0; i<atomCount; i++ ) {
				reconstructionMap.put( segment.getAtomAt(i), getAtomReconstructionAt(i) );
			}
		}
		return reconstructionMap;
	}
	
	public void clearSelectiveReconstruction() {
		reconstruction.clear();
		selectiveReconstructionDirty = true;
	}
	
	public int getSelectiveReconstructionSize() {
		return reconstruction.size();
	}
	
	public boolean isAtomInSelectiveReconstruction( int index ) {
		return reconstruction.contains( segment.getAtomAt(index) );
	}

	public boolean isAtomInSelectiveReconstruction( StandardBookAtom atom ) {
		return reconstruction.contains( atom );
	}
	
	public void addAtomToSelectiveReconstruction( int index ) {
		boolean added = reconstruction.add( segment.getAtomAt(index) );
		if( added && selectiveReconstruction != null && !selectiveReconstructionDirty ) {
			// incrementally add
			double[] atom = getAtomReconstructionAt(index);
			for( int i=0; i<width; i++ ) {
				selectiveReconstruction[i] += atom[i]; 
			}
		} else {
			selectiveReconstructionDirty = true;
		}
	}
	
	public void removeAtomFromSelectiveReconstruction( int index ) {
		boolean removed = reconstruction.remove( segment.getAtomAt(index) );
		if( removed ) {
			selectiveReconstructionDirty = true;			
		}
	}
	
	public Iterator<StandardBookAtom> selectiveReconstructionInterator() {
		return reconstruction.iterator();
	}
		
	public double[] getSelectiveReconstruction() {
		
		boolean zero = true;
		
		if( selectiveReconstruction == null ) {
			selectiveReconstruction = new double[width];
			selectiveReconstructionDirty = true;
			zero = false;
		}
		
		if( selectiveReconstructionDirty ) {
			if( zero ) {
				Arrays.fill(selectiveReconstruction, 0);
			}				
			if( segment != null ) {
				double[] atom;
				int e;
				HashMap<StandardBookAtom,double[]> map = getReconstructionMap();
				Iterator<StandardBookAtom> it = reconstruction.iterator();
				while( it.hasNext() ) {					
					atom = map.get( it.next() );
					for( e=0; e<width; e++ ) {
						selectiveReconstruction[e] += atom[e];
					}
				}
			}
		}
		
		return selectiveReconstruction;
		
	}
			
}

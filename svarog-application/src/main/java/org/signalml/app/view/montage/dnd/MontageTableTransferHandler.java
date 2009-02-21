/* MontageTableTransferHandler.java created 2008-01-04
 * 
 */

package org.signalml.app.view.montage.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import org.apache.log4j.Logger;
import org.signalml.app.view.montage.MontageTable;
import org.signalml.domain.montage.Montage;

/** MontageTableTransferHandler
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageTableTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MontageTableTransferHandler.class);
	
	private SourceMontageChannelsDataFlavor sourceFlavor = new SourceMontageChannelsDataFlavor();
	private MontageChannelsDataFlavor montageFlavor = new MontageChannelsDataFlavor(false);
				
	@Override
	public int getSourceActions(JComponent c) {
		return MOVE;
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		
		MontageTable table = (MontageTable) c;
		
		ListSelectionModel model = table.getSelectionModel();
		
		int[] array;
		
		int minIndex = model.getMinSelectionIndex();
		if( minIndex < 0 ) {
			array = new int[0];
		} else {
			int maxIndex = model.getMaxSelectionIndex();
			if( maxIndex < 0 ) {
				array = new int[0];
			} else {
		
				int cnt = 0;
				int[] candidates = new int[maxIndex-minIndex+1];
				
				for( int i=minIndex; i<=maxIndex; i++ ) {
					if( model.isSelectedIndex(i) ) {
						candidates[cnt] = i;
						cnt++;
					}
				}
				
				array = Arrays.copyOf(candidates, cnt);

			}
		}
		
		return new MontageTransferable(new MontageChannelIndices(array));
		
	}
	
	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		
		logger.debug( "Testing drop for [" + transferFlavors.length + "] flavors" );
		
		for( int i=0; i<transferFlavors.length; i++ ) {

			logger.debug( "Testing drop for flavor [" + transferFlavors[i].toString() + "]" );
			
			if( transferFlavors[i].equals(sourceFlavor) ) {
				logger.debug( "Accepted source" );
				return true;
			}
			if( transferFlavors[i].equals(montageFlavor) ) {
				if( ((MontageChannelsDataFlavor) transferFlavors[i]).isContinuous() ) {
					logger.debug( "Accepted target" );
					return true;					
				}
			}

		}
		
		logger.debug( "Nothing interesting in this drop" );
		return false;
			
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		
		if( !support.isDrop() ) {
			return false;
		}
		
		MontageTable table = (MontageTable) support.getComponent();
		
		DataFlavor[] dataFlavors = support.getDataFlavors();
		if( dataFlavors == null || dataFlavors.length == 0 ) {
			return false;
		}
		if( !canImport(table, dataFlavors) ) {
			return false;
		}
		
		JTable.DropLocation dropLocation = (JTable.DropLocation) support.getDropLocation();
		if( dropLocation == null ) {
			return false;
		}
		
		Transferable transferable = support.getTransferable();
		
		if( transferable.isDataFlavorSupported(sourceFlavor) ) {
			
			SourceChannelIndices indices = null;
			try {
				indices = (SourceChannelIndices) transferable.getTransferData(sourceFlavor);
			} catch (UnsupportedFlavorException ex) {
				logger.error( "Failed to drop", ex );
				return false;
			} catch (IOException ex) {
				logger.error( "Failed to drop", ex );
				return false;
			}
			if( indices == null ) {
				logger.warn( "Drop empty, no indices");
				return false;
			}
			
			int[] sourceChannels = indices.getSourceChannels();
			if( sourceChannels == null || sourceChannels.length == 0 ) {
				logger.warn( "Drop empty, no rows in int[] table");
				return false;
			}
			
			int row = dropLocation.getRow();
			
			Montage montage = table.getModel().getMontage();
			if( montage == null ) {
				logger.warn( "No montage");				
				return false;
			}
			
			montage.addMontageChannels(sourceChannels, row);
			
		} else if( transferable.isDataFlavorSupported(montageFlavor) ) {

			MontageChannelIndices indices = null;
			try {
				indices = (MontageChannelIndices) transferable.getTransferData(montageFlavor);
			} catch (UnsupportedFlavorException ex) {
				logger.error( "Failed to drop", ex );
				return false;
			} catch (IOException ex) {
				logger.error( "Failed to drop", ex );
				return false;
			}
			if( indices == null ) {
				logger.warn( "Drop empty, no indices");
				return false;
			}
			
			int[] montageChannels = indices.getMontageChannels();
			if( montageChannels == null || montageChannels.length == 0 ) {
				logger.warn( "Drop empty, no rows in int[] table");
				return false;
			}
			
			// check continuity
			for( int i=0; i<(montageChannels.length-1); i++ ) {
				if( montageChannels[i]+1 != montageChannels[i+1] ) {
					logger.debug( "Not contiguous" );
					return false;
				}
			}
			
			int row = dropLocation.getRow();
			
			int lastMovedRow = montageChannels[0] + (montageChannels.length-1);
			
			int delta = 0;
			
			// analyze/correct the delta to make this more intuitive
			if( row >= montageChannels[0] && row <= lastMovedRow ) {
				// the drop line is in the selection range, disregard
				return false;
			}
			else if( row < montageChannels[0] ) {
				// the drop line is above, move normally
				delta = row - montageChannels[0];
			} else {
				// the drop line is below - make sure that the end effect is that
				// the dragged row end up between the rows between which the line was
				delta = (row - montageChannels[0]) - montageChannels.length;
			}
						
			Montage montage = table.getModel().getMontage();
			if( montage == null ) {
				logger.warn( "No montage");				
				return false;
			}
						
			int movedDelta = montage.moveMontageChannelRange(montageChannels[0], montageChannels.length, delta);
			table.getSelectionModel().setSelectionInterval(montageChannels[0]+movedDelta, lastMovedRow+movedDelta);
			
		} else {
			return false;
		}		
		
		return true;
		
	}
	
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		
	}
		
}

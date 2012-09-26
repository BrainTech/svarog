/**
 * Provides the drag and drop functionality for
 * {@link org.signalml.app.view.montage.MontageChannelsPanel}.
 * With the use of this functionality the indexes of
 * {@link org.signalml.app.view.montage.dnd.SourceChannelIndices source} and
 * {@link org.signalml.app.view.montage.dnd.MontageChannelIndices montage}
 * channels are transfered.
 * The indexes can be transfered:
 * <ul>
 * <li>from {@link org.signalml.app.view.montage.SourceMontageTable
 * SourceMontageTable} to {@link org.signalml.app.view.montage.MontageTable
 * MontageTable} - the new {@link org.signalml.domain.montage.MontageChannel
 * montage channels} basing on the selected
 * {@link org.signalml.domain.montage.SourceChannel source channels} are added
 * to the {@link org.signalml.domain.montage.Montage montage},</li>
 * <li>from MontageTable to
 * {@link org.signalml.app.view.montage.dnd.MontageWasteBasket
 * MontageWasteBasket} - the selected montage channels are removed from the
 * montage,</li>
 * <li>from MontageTable to MontageTable - the order (the indexes) of montage
 * channels is changed - the selected channels are moved to the specified
 * index.</li>
 * </ul>
 * There are 4 types of classes in this package:
 * <ul>
 * <li>The {@link java.awt.datatransfer.DataFlavor data flavors} - they are
 * responsible for identifying the type of the data. We have:
 * <ul>
 * <li>{@link org.signalml.app.view.montage.dnd.MontageChannelsDataFlavor
 * MontageChannelsDataFlavor} - it states for MontageChannelIndices.</li>
 * <li>{@link org.signalml.app.view.montage.dnd.SourceMontageChannelsDataFlavor
 * SourceMontageChannelsDataFlavor} - it states for SourceChannelIndices.</li>
 * </ul></li>
 * <li>The {@link java.awt.datatransfer.Transferable transferable} objects -
 * they encapsulate the data for transportation. We have:
 * <ul>
 * <li>{@link org.signalml.app.view.montage.dnd.MontageTransferable
 * MontageTransferable} which encapsulates MontageChannelIndices,</li>
 * <li>{@link org.signalml.app.view.montage.dnd.SourceMontageTransferable
 * SourceMontageTransferable} which encapsulates SourceChannelIndices.</li>
 * </ul></li>
 * <li>The {@link javax.swing.TransferHandler transfer handlers} - they are
 * responsible for performing actions if the objects are dragged from or
 * dropped on the object to which they are attached. We have:
 * <ul>
 * <li>{@link org.signalml.app.view.montage.dnd.MontageTableTransferHandler
 * MontageTableTransferHandler} which is attached to the MontageTable and:
 * <ul>
 * <li>creates the list of indexes if the rows of the table are dragged,</li>
 * <li>adds the new montage channels to the montage if the SourceChannelIndices
 * are dropped,</li>
 * <li>moves the channels to the specified index if the MontageChannelIndices
 * are dropped.</li>
 * </ul></li>
 * <li>{@link org.signalml.app.view.montage.dnd.MontageWasteBasketTransferHandler
 * MontageWasteBasketTransferHandler} which is attached to the
 * MontageWasteBasket and:
 * <ul>
 * <li>removes the channels from the montage if the MontageChannelIndices
 * are dropped.</li>
 * </ul></li>
 * <li>{@link org.signalml.app.view.montage.dnd.SourceMontageTableTransferHandler
 * SourceMontageTableTransferHandler} which is attached to the
 * SourceMontageTable and:
 * <ul>
 * <li>creates the list of indexes if the rows of the table are dragged.</li>
 * </ul></li>
 * </ul></li>
 * <li>The transferred objects which are encapsulated in transferable objects.
 * We have:
 * <ul>
 * <li>SourceChannelIndices which contain the indexes of source channels,</li>
 * <li>MontageChannelIndices which contain the indexes of montage channels.</li>
 * </ul></li></ul>
 */
package org.signalml.app.view.montage.dnd;

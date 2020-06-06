/* VisualReferenceEditorMouseHandler.java created 2007-12-01
 *
 */

package org.signalml.app.view.montage.visualreference;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

/** VisualReferenceEditorMouseHandler
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceEditorMouseHandler extends MouseAdapter {

	private VisualReferenceEditor editor;
	private VisualReferenceModel model;

	private Point dragStart = null;
	private VisualReferenceSourceChannel draggedChannel;
	private int pickupOffsetX;
	private int pickupOffsetY;

	private boolean dragVisible = false;
	private int prospectiveDropChannel = -1;

	private ArrayList<VisualReferenceArrow> tempArrowList = new ArrayList<>();

	private VisualReferenceDraggedChannel draggedChannelComponent;

	public VisualReferenceEditorMouseHandler(VisualReferenceEditor editor) {
		this.editor = editor;
		model = editor.getModel();
		draggedChannelComponent = new VisualReferenceDraggedChannel(editor);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		editor.requestFocusInWindow();
		dragStart = e.getPoint();
		draggedChannel = null;
		setProspectiveDropChannel(-1);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (dragStart != null) {
			hideDrag();
			if (draggedChannel != null) {
				if (prospectiveDropChannel >= 0) {
					if (model.isBipolarMode()) {
						model.addReference(prospectiveDropChannel, draggedChannel.getChannel());
						model.setActiveChannel(null);
					} else {
						model.addReference(model.indexOfChannel(model.getActiveChannel()), draggedChannel.getChannel());
					}
				}
			}
			dragStart = null;
			draggedChannel = null;
			setProspectiveDropChannel(-1);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		if (dragStart != null) {

			Point point = e.getPoint();
			boolean bipolarMode = model.isBipolarMode();

			VisualReferenceChannel activeChannel = model.getActiveChannel();
			if (!bipolarMode && activeChannel == null) {
				dragStart = null;
				return;
			}

			if (draggedChannel == null) {

				draggedChannel = editor.findChannelAt(dragStart);
				if (draggedChannel == null) {
					dragStart = null;
					return;
				}
				else if (activeChannel != null && draggedChannel == model.getSourceChannel(activeChannel.getPrimaryChannel())) {
					dragStart = null;
					return;
				}

				Point location = draggedChannel.getLocation();

				pickupOffsetX = dragStart.x - location.x;
				pickupOffsetY = dragStart.y - location.y;

				draggedChannelComponent.setChannel(draggedChannel);

				showDrag(point);

			} else {

				moveDrag(point);

			}

			VisualReferenceSourceChannel dropChannel = editor.findChannelAt(point);
			if (dropChannel != null) {
				if (activeChannel == null && bipolarMode && dropChannel != draggedChannel) {
					setProspectiveDropChannel(dropChannel.getChannel());
				}
				else if (activeChannel != null && dropChannel != draggedChannel && dropChannel == model.getSourceChannel(activeChannel.getPrimaryChannel())) {
					setProspectiveDropChannel(dropChannel.getChannel());
				} else {
					setProspectiveDropChannel(-1);
				}
			} else {
				setProspectiveDropChannel(-1);
			}

		}

	}

	public int getProspectiveDropChannel() {
		return prospectiveDropChannel;
	}

	public void setProspectiveDropChannel(int prospectiveDropChannel) {
		if (this.prospectiveDropChannel != prospectiveDropChannel) {
			this.prospectiveDropChannel = prospectiveDropChannel;
			VisualReferenceArrow arrow = null;
			if (prospectiveDropChannel >= 0) {
				// note!!! in case of the "prospective" arrow the target index is that of a SOURCE channel
				// at the given location
				arrow = new VisualReferenceArrow(draggedChannel.getChannel(), prospectiveDropChannel);
			}
			editor.setProspectiveArrow(arrow);
		}
	}

	private void moveDrag(Point point) {
		if (dragVisible) {
			JLayeredPane layeredPane = editor.getRootPane().getLayeredPane();
			Point location = SwingUtilities.convertPoint(editor, point, layeredPane);
			draggedChannelComponent.setLocation(location.x-pickupOffsetX, location.y-pickupOffsetY);
		}
	}

	private void showDrag(Point point) {
		if (!dragVisible) {
			JLayeredPane layeredPane = editor.getRootPane().getLayeredPane();
			Point location = SwingUtilities.convertPoint(editor, point, layeredPane);
			Dimension size = draggedChannelComponent.getPreferredSize();
			draggedChannelComponent.setBounds(location.x-pickupOffsetX, location.y-pickupOffsetY, size.width, size.height);
			layeredPane.add(draggedChannelComponent, new Integer(JLayeredPane.DRAG_LAYER));
			dragVisible = true;
		}
	}

	private void hideDrag() {
		if (dragVisible) {
			JLayeredPane layeredPane = editor.getRootPane().getLayeredPane();
			layeredPane.remove(draggedChannelComponent);
			dragVisible = false;
			editor.repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Point point = e.getPoint();

		if (SwingUtilities.isLeftMouseButton(e)) {

			VisualReferenceSourceChannel selChannel = editor.findChannelAt(point);
			if (selChannel != null) {

				// this was a click on a channel
				int index = selChannel.getChannel();
				VisualReferenceChannel oldActiveChannel = model.getActiveChannel();
				VisualReferenceChannel newActiveChannel;

				if (model.isChannelsPerPrimaryEmpty(index)) {
					newActiveChannel = null;
				}
				else if (oldActiveChannel == null) {
					newActiveChannel = model.getChannelPerPrimary(index, 0);
				}
				else if (oldActiveChannel.getPrimaryChannel() != index) {
					newActiveChannel = model.getChannelPerPrimary(index, 0);
				} else {
					int oldActiveIndex = model.indexOfChannelPerPrimary(index, oldActiveChannel);
					int newActiveIndex = (oldActiveIndex + 1) % model.channelsPerPrimarySize(index);
					newActiveChannel = model.getChannelPerPrimary(index, newActiveIndex);
				}

				model.setActiveChannel(newActiveChannel);
				return;

			}

			// check arrows, return if matches
			editor.findArrowsAtPoint(point, tempArrowList);
			if (!tempArrowList.isEmpty()) {

				// this was a click on some arrows
				VisualReferenceArrow oldActiveArrow = model.getActiveArrow();
				VisualReferenceArrow newActiveArrow = null;

				if (oldActiveArrow == null) {
					newActiveArrow = tempArrowList.get(0);
				} else {
					int index = tempArrowList.indexOf(oldActiveArrow);
					if (index < 0) {
						index = -1;
					}
					index = (index + 1) % tempArrowList.size();
					newActiveArrow = tempArrowList.get(index);
				}

				model.setActiveArrow(newActiveArrow);
				return;

			}

			// if nothing was clicked
			model.setActiveChannel(null);

		}

	}

}

/* VisualReferenceEditor.java created 2007-11-30
 * 
 */

package org.signalml.app.view.montage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import org.signalml.app.util.GeometryUtils;

/** VisualReferenceEditor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceEditor extends JComponent implements VisualReferenceListener, PropertyChangeListener, Scrollable {

	private static final long serialVersionUID = 1L;

	public static final int MAX_CHANNEL_LABEL_LENGTH = 7;
	public static final int BIN_SPACING = 10;
	
	private static final BasicStroke WHITE_SELECTION_STROKE = new BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, new float[] {3,3}, 0F );
	private static final BasicStroke BLACK_SELECTION_STROKE = new BasicStroke(1F, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10F, new float[] {3,3}, 3F );
	
	private static final Color[] ARROW_COLORS = { 
		Color.RED,
		Color.GREEN.darker(),
		Color.BLUE,
		Color.CYAN,
		Color.MAGENTA,
		Color.YELLOW,
		Color.ORANGE,
		Color.BLACK
	};
	
	private static final Color INACTIVE_CHANNEL_COLOR = Color.LIGHT_GRAY;
	private static final Color ACTIVE_CHANNEL_COLOR = new Color(0, 198, 255);
	
	private VisualReferenceModel model;
	
	private Font binLabelFont;
	private Font channelLabelFont;
	
	private FontMetrics binLabelFontMetrics;
	private FontMetrics channelLabelFontMetrics;
	
	private Stroke defaultStroke;
	private Stroke activeChannelStroke;
	 	
	private JViewport viewport;
	
	private Dimension requiredSize = null;
	
	private LinkedList<VisualReferenceArrow> tempArrowsToDraw = new LinkedList<VisualReferenceArrow>();
	
	private VisualReferenceArrow prospectiveArrow;
			
	public VisualReferenceEditor(VisualReferenceModel model) {
		super();
		
		setAutoscrolls(true);
		
		if( model == null ) {
			throw new NullPointerException( "No model" );
		}
		this.model = model;
		
		setFocusable(true);
		
		model.addPropertyChangeListener(this);
		model.addVisualReferenceListener(this);
	
		VisualReferenceEditorMouseHandler mouseHandler = new VisualReferenceEditorMouseHandler(this);
		
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseHandler);
		
	}
		
	public JViewport getViewport() {
		return viewport;
	}

	public void setViewport(JViewport viewport) {
		this.viewport = viewport;
	}	

	public VisualReferenceArrow getProspectiveArrow() {
		return prospectiveArrow;
	}

	public void setProspectiveArrow(VisualReferenceArrow prospectiveArrow) {
		if( this.prospectiveArrow != prospectiveArrow ) {
			this.prospectiveArrow = prospectiveArrow;
			if( prospectiveArrow != null ) {
				positionArrow(prospectiveArrow, true);
			}
			repaint();
		}
	}

	@Override
	public boolean isOpaque() {
		return true;
	}
	
	public void paintChannel( String label, int perPrimarySize, Shape shape, Shape outlineShape, boolean source, boolean selected, Graphics2D g ) {
		
		if( source ) {
			g.setColor(INACTIVE_CHANNEL_COLOR);
		} else {
			g.setColor(ACTIVE_CHANNEL_COLOR);
		}
		
		g.fill( shape );
		
		if( selected ) {
			g.setColor(Color.RED);
			g.setStroke( activeChannelStroke );
		} else {
			g.setColor(Color.BLACK);
		}
		
		g.draw(outlineShape);
		g.setStroke( defaultStroke );

		g.setColor(Color.BLACK);
		g.setFont( channelLabelFont );
		String abbrLabel;
		if( label.length() > MAX_CHANNEL_LABEL_LENGTH ) {
			abbrLabel = label.substring(0,MAX_CHANNEL_LABEL_LENGTH-2) + "...";
		} else {
			abbrLabel = label;
		}
		Rectangle2D rect = channelLabelFontMetrics.getStringBounds(abbrLabel, g);
		
		int textHeight = (int)(rect.getHeight()); 
		int textWidth  = (int)(rect.getWidth());
		
		Rectangle r = shape.getBounds();
		
		g.drawString(abbrLabel, r.x + (r.width  - textWidth)  / 2, r.y + (r.height - textHeight) / 2  + channelLabelFontMetrics.getAscent() );
		
		if( perPrimarySize > 1 ) {

			abbrLabel = "(" + Integer.toString(perPrimarySize) + ")";
			rect = channelLabelFontMetrics.getStringBounds(abbrLabel, g);
			textWidth  = (int)(rect.getWidth());
			
			g.drawString(abbrLabel, r.x + (r.width  - textWidth)  / 2, r.y + (r.height - textHeight) / 2  + channelLabelFontMetrics.getAscent() + textHeight );
						
		}
				
	}
	
	protected void paintBin( VisualReferenceBin bin, Graphics2D g ) {
		
		Dimension size = bin.getSize();
		Point location = bin.getLocation();
		String name = bin.getName();
		
		g.setColor(Color.DARK_GRAY);
		g.drawRect(location.x, location.y, size.width-1, size.height-1);
		g.fillRect(location.x, location.y, size.width, VisualReferenceBin.HEADER_HEIGHT);
		
		if( name != null && !name.isEmpty() ) {

			g.setColor(Color.WHITE);
			
			g.setFont( binLabelFont );
			Rectangle2D rect = binLabelFontMetrics.getStringBounds(name, g);
			
			int textHeight = (int)(rect.getHeight()); 
			int textWidth  = (int)(rect.getWidth());

			g.drawString(name, location.x + (size.width  - textWidth)  / 2, location.y + (VisualReferenceBin.HEADER_HEIGHT - textHeight) / 2  + binLabelFontMetrics.getAscent() );
			
		}
		
		if( bin instanceof VisualReferencePositionedBin ) {
			VisualReferencePositionedBin positionedBin = (VisualReferencePositionedBin) bin;
			Insets backdropMargin = positionedBin.getBackdropMargin();
			if( backdropMargin == null ) {
				backdropMargin = new Insets(0,0,0,0);
			}
			Insets margin = positionedBin.getMargin();
			int width = size.width - ( margin.left + margin.right + backdropMargin.left + backdropMargin.right );
			int height = size.height - ( VisualReferenceBin.HEADER_HEIGHT + margin.top + margin.bottom + backdropMargin.top + backdropMargin.bottom );
			Image backdrop = model.getMontage().getSignalTypeConfigurer().getMatrixBackdrop(width, height);
			if( backdrop != null ) {
				g.drawImage(backdrop, location.x + margin.left + backdropMargin.left, location.y + VisualReferenceBin.HEADER_HEIGHT + margin.top + backdropMargin.top, null );
			}
		}
		
	}
	
	protected void paintBinContents( VisualReferenceBin bin, Graphics2D g ) {
	
		Iterator<VisualReferenceSourceChannel> it = bin.iterator();
		VisualReferenceSourceChannel channel;
		int channelIdx;
		Iterator<VisualReferenceChannel> montageIt;
		VisualReferenceChannel selChannel = model.getActiveChannel();
		VisualReferenceChannel montageChannel;
		boolean selectedPainted;
		int perPrimarySize;
		
		while( it.hasNext() ) {
			
			channel = it.next();
			channelIdx = channel.getChannel();
			perPrimarySize = model.channelsPerPrimarySize( channelIdx );
			
			if( perPrimarySize == 0 ) {
				
				paintChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), true, false, g);
				
			} else {
				
				selectedPainted = false;
				
				if( selChannel != null ) {
					montageIt = model.channelsPerPrimaryIterator( channelIdx );
					while( montageIt.hasNext() ) {
						montageChannel = montageIt.next();
						if( montageChannel == selChannel ) {
							paintChannel(montageChannel.getLabel(), perPrimarySize, channel.getShape(), channel.getOutlineShape(), false, true, g);
							selectedPainted = true;
							break;
						}
					}

					if( !selectedPainted ) {
						montageChannel = model.getChannelPerPrimary( channelIdx, 0 );
						paintChannel(channel.getLabel(), 0, channel.getShape(), channel.getOutlineShape(), ( selChannel != null ), false, g);
					}
				
				} else {
					
					if( !selectedPainted ) {
						montageChannel = model.getChannelPerPrimary( channelIdx, 0 );
						paintChannel(montageChannel.getLabel(), perPrimarySize, channel.getShape(), channel.getOutlineShape(), ( selChannel != null ), false, g);
					}
										
				}
				
				
			}
			
		}
		
	}
	
	protected void paintArrow(VisualReferenceArrow arrow, Graphics2D g, boolean active, boolean selected) {

		if( active ) {
			g.setColor(arrow.getColor());
		} else {
			g.setColor(Color.LIGHT_GRAY);
		}
		
		Shape arrowShape = arrow.getShape(); 
		g.fill( arrowShape );
		
		if( selected ) {
			
			g.setColor(Color.WHITE);
			g.setStroke(WHITE_SELECTION_STROKE);
			g.draw( arrowShape );
			g.setColor(Color.BLACK);
			g.setStroke(BLACK_SELECTION_STROKE);
			g.draw( arrowShape );
			
		}
		
	}
	
	@Override
	protected void paintComponent(Graphics gOrig) {
		
		Graphics2D g = (Graphics2D) gOrig;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if( defaultStroke == null ) {
			defaultStroke = g.getStroke();
			activeChannelStroke = new BasicStroke( 3 );
		}
		
		if( binLabelFont == null || channelLabelFont == null ) {
			binLabelFont = g.getFont();
			channelLabelFont = new Font( Font.DIALOG, Font.PLAIN, 9 );
			binLabelFontMetrics = g.getFontMetrics(binLabelFont);
			channelLabelFontMetrics = g.getFontMetrics(channelLabelFont);
		}
		
		Rectangle clip = g.getClipBounds();
		
		g.setColor(getBackground());
		g.fill(clip);
		
		VisualReferenceChannel selChannel = model.getActiveChannel();
		int selChannelIndex = -1;
		if( selChannel != null ) {
			selChannelIndex = model.indexOfChannel(selChannel);
		}
		
		VisualReferenceArrow selArrow = model.getActiveArrow();
		VisualReferenceArrow arrow;
		
		VisualReferenceBin othersBin = model.getOthersBin();
		VisualReferencePositionedBin positionedBin = model.getPositionedBin();
		VisualReferenceBin referencesBin = model.getReferencesBin();
		VisualReferenceBin primariesBin = model.getPrimariesBin();
		
		paintBin(positionedBin, g);
		if( !othersBin.isEmpty() ) {
			paintBin(othersBin, g);
		}
		if( !referencesBin.isEmpty() ) {
			paintBin(referencesBin, g);
		}
		if( !primariesBin.isEmpty() ) {
			paintBin(primariesBin, g);
		}
		
		tempArrowsToDraw.clear();
		
		int colorIndex;
		VisualReferenceChannel toChannel;
		
		Iterator<VisualReferenceArrow> arrowIt = model.arrowsIterator();
		while( arrowIt.hasNext() ) {
			arrow = arrowIt.next();
			if( selChannel == null ) {
				toChannel = model.getChannel( arrow.getTargetChannel() );
				colorIndex = model.indexOfChannelPerPrimary( toChannel.getPrimaryChannel(), toChannel );
				colorIndex = colorIndex % ARROW_COLORS.length;
				paintArrow(arrow, g, true, (arrow == selArrow) );
			} else {
				if( arrow.getTargetChannel() != selChannelIndex ) {
					paintArrow(arrow, g, false, false );					
				} else {
					tempArrowsToDraw.add(arrow);
				}
			}
		}
				
		paintBinContents(positionedBin, g);
		if( !othersBin.isEmpty() ) {
			paintBinContents(othersBin, g);
		}
		if( !referencesBin.isEmpty() ) {
			paintBinContents(referencesBin, g);
		}
		if( !primariesBin.isEmpty() ) {
			paintBinContents(primariesBin, g);
		}
			
		if( selChannel != null ) {
			arrowIt = tempArrowsToDraw.iterator();
			while( arrowIt.hasNext() ) {
				arrow = arrowIt.next();
				paintArrow(arrow, g, true, (arrow == selArrow) );				
			}
		}
		
		if( prospectiveArrow != null ) {
			paintArrow(prospectiveArrow, g, true, true);
		}
		
	}

	@Override
	public boolean isDoubleBuffered() {
		return true;
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		requiredSize = null;
	}
	
	@Override
	public Dimension getPreferredSize() {
		if( requiredSize == null ) {
			requiredSize = calculateRequiredSize();
		}
		return requiredSize;
	}
	
	@Override
	public Dimension getMinimumSize() {
		return super.getPreferredSize();
	}
	
	public VisualReferenceSourceChannel findChannelInBinAt( VisualReferenceBin bin, Point point ) {
		
		if( bin.isEmpty() ) {
			return null;
		}
		
		Iterator<VisualReferenceSourceChannel> it = bin.iterator();
		VisualReferenceSourceChannel channel;
		while( it.hasNext() ) {
			channel = it.next();
			if( channel.getShape().contains(point) ) {
				return channel; 
			}
		}
		
		return null;
		
	}
	
	public VisualReferenceSourceChannel findChannelAt( Point point ) {
		
		VisualReferencePositionedBin positionedBin = model.getPositionedBin();
		VisualReferenceBin primariesBin = model.getPrimariesBin();
		VisualReferenceBin othersBin = model.getOthersBin();
		VisualReferenceBin referencesBin = model.getReferencesBin();

		if( positionedBin.getBounds().contains(point) ) {
			return findChannelInBinAt( positionedBin, point );
		}
		else if( primariesBin.getBounds().contains(point) ) {
			return findChannelInBinAt( primariesBin, point );
		}		
		else if( othersBin.getBounds().contains(point) ) {
			return findChannelInBinAt( othersBin, point );
		}
		else if( referencesBin.getBounds().contains(point) ) {
			return findChannelInBinAt( referencesBin, point );
		}
		
		return null;
		
	}
	
	public ArrayList<VisualReferenceArrow> findArrowsAtPoint( Point point, ArrayList<VisualReferenceArrow> listToFill ) {
		
		ArrayList<VisualReferenceArrow> list;
		if( listToFill != null ) {
			list = listToFill;
			list.clear();
		} else {
			list = new ArrayList<VisualReferenceArrow>();
		}
		
		Iterator<VisualReferenceArrow> arrowIt = model.arrowsIterator();
		VisualReferenceArrow arrow;
		Shape arrowShape;
		
		while( arrowIt.hasNext() ) {
			
			arrow = arrowIt.next();
			arrowShape = arrow.getShape();
			
			if( arrowShape.getBounds().contains(point) ) {
				if( arrowShape.contains(point) ) {
					list.add( arrow );
				}
			}
						
		}
		
		return list;
		
	}
	
	private Dimension calculateRequiredSize() {
		
		Dimension viewportSize = viewport.getExtentSize();		
		int avHeight = viewportSize.height - (2 * BIN_SPACING);

		VisualReferenceBin othersBin = model.getOthersBin();		
		VisualReferencePositionedBin positionedBin = model.getPositionedBin();
		VisualReferenceBin referencesBin = model.getReferencesBin();
		VisualReferenceBin primariesBin = model.getPrimariesBin();

		int minimumHeight = 0;
		int minBinHeight;
		if( !othersBin.isEmpty() ) {
			minBinHeight = othersBin.getMinHeight();
			if( minBinHeight > minimumHeight ) {
				minimumHeight = minBinHeight;
			}
		}
				
		minimumHeight += (2 * BIN_SPACING);
		if( avHeight < minimumHeight ) {
			avHeight = minimumHeight;
		}
		
		Dimension size;

		positionedBin.setMaxHeight( avHeight );
				
		size = positionedBin.getSize();
				
		int width = 2*BIN_SPACING + size.width;
		
		if( !othersBin.isEmpty() ) {

			othersBin.setMaxHeight( avHeight);
			
			size = othersBin.getSize();
			width += (BIN_SPACING + size.width);
			
		}
				
		if( !referencesBin.isEmpty() ) {
			
			referencesBin.setMaxHeight( avHeight );
			size = referencesBin.getSize();
			
			width += (BIN_SPACING + size.width);
			
		}
		
		if( !primariesBin.isEmpty() ) {
			
			primariesBin.setMaxHeight(avHeight);
			size = primariesBin.getSize();
			
			width += (BIN_SPACING + size.width);		

		}
		
		int centeringOffset = 0;
		if( viewportSize.width > width ) {
			centeringOffset = (viewportSize.width - width) / 2;
		}
		
		// now position the bins and re-validate them
		int topX = centeringOffset + BIN_SPACING;
		
		if( !othersBin.isEmpty() ) {
		
			othersBin.setLocation( new Point( topX, BIN_SPACING ) );
			if( !othersBin.isPositioned() ) {
				othersBin.reposition();
			}
			size = othersBin.getSize();
			topX += (size.width + BIN_SPACING);

		}
		
		if( !primariesBin.isEmpty() ) {
			
			primariesBin.setLocation( new Point( topX, BIN_SPACING ) );
			if( !primariesBin.isPositioned() ) {
				primariesBin.reposition();
			}
			size = primariesBin.getSize();
			topX += (size.width + BIN_SPACING);
			
			
		}
		
		if( !referencesBin.isEmpty() ) {
			
			referencesBin.setLocation( new Point( topX, BIN_SPACING ) );
			if( !referencesBin.isPositioned() ) {
				referencesBin.reposition();
			}
			size = referencesBin.getSize();
			topX += (size.width + BIN_SPACING);
						
		}
		
		positionedBin.setLocation( new Point( topX, BIN_SPACING ) );
		if( !positionedBin.isPositioned() ) {
			positionedBin.reposition();
		}		
					
		positionArrows(true);
		
		return new Dimension( width, viewportSize.height );
		
	}
	
	private void positionArrows( boolean all ) {

		VisualReferenceArrow arrow;
		Iterator<VisualReferenceArrow> it = model.arrowsIterator();
		
		while( it.hasNext() ) {
			arrow = it.next();
			if( all || !arrow.isPositioned() ) {
				positionArrow(arrow, false);
			}			
		}
		
	}
	
	private void positionArrow( VisualReferenceArrow arrow, boolean targetIsPrimary ) {

		int source, target;
		VisualReferenceSourceChannel arrowSource;
		VisualReferenceSourceChannel arrowTarget;
		VisualReferenceChannel toChannel;
		Point point;
		Point fromPoint;
		Point toPoint;
		Rectangle bounds;
		
		source = arrow.getSourceChannel();
		target = arrow.getTargetChannel();
		
		arrowSource = model.getSourceChannel(source);
		int arrowOrder;
		int colorIndex;
		if( targetIsPrimary ) {
			arrowTarget = model.getSourceChannel( target );
			arrow.setColor(Color.WHITE);
			arrowOrder = 0;
		} else {
			toChannel = model.getChannel( target );
			arrowTarget = model.getSourceChannel( toChannel.getPrimaryChannel() );
			arrowOrder = model.getArrowOrder(target, source);
			colorIndex = model.indexOfChannelPerPrimary( toChannel.getPrimaryChannel(), toChannel );
			arrow.setColor( ARROW_COLORS[ colorIndex % ARROW_COLORS.length ] );
		}
		
		point = arrowSource.getLocation();
		bounds = arrowSource.getShape().getBounds();
		fromPoint = new Point( point.x + bounds.width / 2, point.y + bounds.height / 2);
		
		point = arrowTarget.getLocation();
		bounds = arrowTarget.getShape().getBounds();
		toPoint = new Point( point.x + bounds.width / 2, point.y + bounds.height / 2);
		
		GeometryUtils.translatePointToCircleBorder(fromPoint, toPoint, VisualReferenceSourceChannel.CIRCLE_DIAMETER/2);
		GeometryUtils.translatePointToCircleBorder(toPoint, fromPoint, VisualReferenceSourceChannel.CIRCLE_DIAMETER/2);
		if( arrowOrder > 0 ) {

			// this rotates the arrows slightly so that they superimpose less
			
			arrowOrder = Math.min( 10, arrowOrder ); // limit to 10 to prevent reaching anything close 90 deg
			double angle = Math.toRadians( 10*((arrowOrder+1)/2) * ( (arrowOrder % 2) == 0 ? -1 : 1 ) );
			Point centerPoint = (Point) arrowTarget.getLocation().clone();
			centerPoint.translate( VisualReferenceSourceChannel.CIRCLE_DIAMETER / 2, VisualReferenceSourceChannel.CIRCLE_DIAMETER / 2 );
			GeometryUtils.rotatePoint(toPoint, centerPoint, angle);
			
		}
		
		arrow.setFromPoint(fromPoint);
		arrow.setToPoint(toPoint);
		
		arrow.setPositioned(true);
		
	}

	public VisualReferenceModel getModel() {
		return model;
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 100;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return ( getPreferredSize().width < viewport.getExtentSize().width );
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object source = evt.getSource();
		String name = evt.getPropertyName();
		if( source == model ) {
			if( VisualReferenceModel.ACTIVE_ARROW_PROPERTY.equals( name ) ) {
				repaint();
			}
			else if( VisualReferenceModel.ACTIVE_CHANNEL_PROPERTY.equals( name ) ) {
				repaint();
			}
		}
	}

	@Override
	public void montageChannelsChanged(VisualReferenceEvent ev) {
		prospectiveArrow = null;
		// TODO maybe optimize this
		revalidate();
		repaint();
	}

	@Override
	public void montageStructureChanged(VisualReferenceEvent ev) {
		prospectiveArrow = null;
		revalidate();
		repaint();
	}

	@Override
	public void referenceChanged(VisualReferenceEvent ev) {
		prospectiveArrow = null;
		// TODO maybe optimize this
		positionArrows(true);
		repaint();
	}

	@Override
	public void sourceChannelsChanged(VisualReferenceEvent ev) {
		prospectiveArrow = null;
		revalidate();
		repaint();
	}

}

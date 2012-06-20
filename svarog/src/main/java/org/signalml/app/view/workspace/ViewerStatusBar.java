/* ViewerStatusBar.java created 2007-09-10
 *
 */
package org.signalml.app.view.workspace;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.action.selector.ActionFocusEvent;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.workspace.ViewModeAction;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.book.BookPlot;
import org.signalml.app.view.book.BookView;
import org.signalml.app.view.components.AntialiasedLabel;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.montage.Montage;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.util.FormatUtils;

/** ViewerStatusBar
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerStatusBar extends JPanel implements ActionFocusListener, PropertyChangeListener, ChangeListener {

	private static final ImageIcon FILTER_ON_ICON = IconUtils.loadClassPathIcon("org/signalml/app/icon/filteron.png");
	private static final ImageIcon FILTER_OFF_ICON = IconUtils.loadClassPathIcon("org/signalml/app/icon/filter.png");

	private static final long serialVersionUID = 1L;

	private String sampleAbbrevString;
	private String pageAbbrevString;
	private String blockAbbrevString;

	private String pageSelectionString;
	private String blockSelectionString;
	private String channelSelectionString;

	private String pageTagString;
	private String blockTagString;
	private String channelTagString;

	private String filterOnString;
	private String filterOffString;
	private String filterOnToolTipString;
	private String filterOffToolTipString;

	private Font statusFont;
	private Font smallFont;

	private JPanel statusPanel;
	private JPanel positionPanel;
	private JPanel selectionPanel;
	private JPanel filteringPanel;
	private JPanel buttonPanel;

	private JLabel statusLabel;
	private JLabel positionLabel;
	private JLabel filteringLabel;
	private JLabel selectionLabel;

	private JToggleButton viewModeButton;

	private ViewModeAction viewModeAction;
	private ActionFocusManager actionFocusManager;

	private SignalDocument currentSignal = null;
	private BookDocument currentBook = null;

	private Font filteringFont;

	public ViewerStatusBar() {

		super(new BorderLayout());
		statusFont = new Font(Font.DIALOG, Font.PLAIN, 12);
		smallFont = new Font(Font.DIALOG, Font.PLAIN, 9);
		filteringFont = new Font(Font.DIALOG, Font.BOLD, 16);

		sampleAbbrevString = _("S");
		pageAbbrevString = _("P");
		blockAbbrevString = _("B");

		pageSelectionString = _("Page selection");
		blockSelectionString = _("Block selection");
		channelSelectionString = _("Channel selection");

		pageTagString = _("Page tag");
		blockTagString = _("Block tag");
		channelTagString = _("Channel tag");

		filterOnString = _("ON");
		filterOffString = _("OFF");
		filterOnToolTipString = _("Filtering is enabled for the master plot");
		filterOffToolTipString = _("Filtering is disbled for the master plot");

	}

	public void initialize() {

		statusPanel = new JPanel(new BorderLayout());
		statusPanel.setBorder(new CompoundBorder(
								  new BevelBorder(BevelBorder.LOWERED),
								  new EmptyBorder(2,4,2,4)
							  ));
		statusPanel.add(getStatusLabel(), BorderLayout.CENTER);

		positionPanel = new JPanel(new BorderLayout());
		positionPanel.setBorder(new CompoundBorder(
									new BevelBorder(BevelBorder.LOWERED),
									new EmptyBorder(2,4,2,4)
								));
		positionPanel.setPreferredSize(new Dimension(360,20));
		positionPanel.add(getPositionLabel(), BorderLayout.CENTER);

		selectionPanel = new JPanel(new BorderLayout());
		selectionPanel.setBorder(new CompoundBorder(
									 new BevelBorder(BevelBorder.LOWERED),
									 new EmptyBorder(2,4,2,4)
								 ));
		selectionPanel.setPreferredSize(new Dimension(360,20));
		selectionPanel.add(getSelectionLabel(), BorderLayout.CENTER);

		filteringPanel = new JPanel(new BorderLayout());
		filteringPanel.setBorder(new CompoundBorder(
									 new BevelBorder(BevelBorder.LOWERED),
									 new EmptyBorder(2,4,2,4)
								 ));
		filteringPanel.setPreferredSize(new Dimension(65,20));
		filteringPanel.add(getFilteringLabel(), BorderLayout.CENTER);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBorder(new EmptyBorder(4,0,4,0));

		buttonPanel.add(getViewModeButton());

		JPanel fixedPanel = new JPanel();
		fixedPanel.setLayout(new BoxLayout(fixedPanel, BoxLayout.X_AXIS));

		fixedPanel.add(Box.createHorizontalStrut(3));
		fixedPanel.add(positionPanel);
		fixedPanel.add(Box.createHorizontalStrut(3));
		fixedPanel.add(selectionPanel);
		fixedPanel.add(Box.createHorizontalStrut(3));
		fixedPanel.add(filteringPanel);
		fixedPanel.add(Box.createHorizontalStrut(3));
		fixedPanel.add(buttonPanel);
		fixedPanel.add(Box.createHorizontalStrut(4));

		add(statusPanel, BorderLayout.CENTER);
		add(fixedPanel, BorderLayout.EAST);

	}

	public JLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new JLabel();
			statusLabel.setFont(statusFont);
			statusLabel.setVerticalAlignment(SwingConstants.CENTER);
		}
		return statusLabel;
	}

	public JLabel getPositionLabel() {
		if (positionLabel == null) {
			positionLabel = new AntialiasedLabel();
			positionLabel.setFont(smallFont);
			positionLabel.setVerticalAlignment(SwingConstants.CENTER);
		}
		return positionLabel;
	}

	public JLabel getSelectionLabel() {
		if (selectionLabel == null) {
			selectionLabel = new AntialiasedLabel();
			selectionLabel.setFont(smallFont);
			selectionLabel.setVerticalAlignment(SwingConstants.CENTER);
		}
		return selectionLabel;
	}

	public JLabel getFilteringLabel() {
		if (filteringLabel == null) {
			filteringLabel = new AntialiasedLabel();
			filteringLabel.setFont(filteringFont);
			filteringLabel.setVerticalAlignment(SwingConstants.CENTER);
		}
		return filteringLabel;
	}

	public JToggleButton getViewModeButton() {
		if (viewModeButton == null) {
			viewModeButton = new JToggleButton(viewModeAction) {

				private static final long serialVersionUID = 1L;

				@Override
				public Point getToolTipLocation(MouseEvent event) {
					// XXX unusual ergonomy hack: prevent the tooltip from completely obscuring the button,
					// which is always at the bottom of the screen
					return new Point(0,-20);
				}

			};
			viewModeButton.setBorder(null);
			viewModeButton.setHideActionText(true);
			viewModeButton.setMargin(new Insets(0,0,0,0));
			viewModeButton.setFocusPainted(false);
			viewModeButton.setContentAreaFilled(false);
			viewModeButton.setSelectedIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/viewmodeon.png"));
		}
		return viewModeButton;
	}

	public void setStatus(String status) {
		getStatusLabel().setText(status);
		getStatusLabel().setToolTipText(status);
	}

	public void setPosition(String position) {
		getPositionLabel().setText(position);
		getPositionLabel().setToolTipText(position);
	}

	public void setSelection(String selection) {
		getSelectionLabel().setText(selection);
		getSelectionLabel().setToolTipText(selection);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (currentSignal != null) {
			digestSignalInfo(currentSignal);
		}
		if (currentBook != null) {
			digestBookInfo(currentBook);
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (currentSignal != null) {
			digestSignalInfo(currentSignal);
		}
	}

	@Override
	public void actionFocusChanged(ActionFocusEvent e) {

		clearInfo();

		Document document = actionFocusManager.getActiveDocument();
		if (document != null) {

			if (document instanceof SignalDocument) {
				setCurrentSignal((SignalDocument) document);
			} else if (document instanceof BookDocument) {
				setCurrentBook((BookDocument) document);
			}

		}

	}

	private void setCurrentBook(BookDocument currentBook) {

		if (this.currentBook != currentBook) {
			BookView view;
			BookPlot plot;
			if (this.currentBook != null) {
				this.currentBook.removePropertyChangeListener(this);
				view = (BookView) this.currentBook.getDocumentView();
				view.removeActionFocusListener(this);
				plot = view.getPlot();
				plot.removePropertyChangeListener(this);
			}
			this.currentBook = currentBook;
			if (currentBook != null) {
				currentBook.addPropertyChangeListener(this);
				view = (BookView) currentBook.getDocumentView();
				view.addActionFocusListener(this);
				plot = view.getPlot();
				plot.addPropertyChangeListener(this);
			}
		}

		if (this.currentBook != null) {
			digestBookInfo(this.currentBook);
		}

	}

	private void digestBookInfo(BookDocument document) {

		BookView view = (BookView) document.getDocumentView();
//		BookPlot plot = view.getPlot();

		setFiltered(view.getFilter().getFilterChain().isFiltered());

	}

	private void setCurrentSignal(SignalDocument currentSignal) {

		if (this.currentSignal != currentSignal) {
			SignalView view;
			SignalPlot plot;
			if (this.currentSignal != null) {
				this.currentSignal.removePropertyChangeListener(this);
				view = (SignalView) this.currentSignal.getDocumentView();
				view.removeActionFocusListener(this);
				plot = view.getMasterPlot();
				plot.removePropertyChangeListener(this);
				plot.getViewport().removeChangeListener(this);
			}
			this.currentSignal = currentSignal;
			if (currentSignal != null) {
				currentSignal.addPropertyChangeListener(this);
				view = (SignalView) currentSignal.getDocumentView();
				view.addActionFocusListener(this);
				plot = view.getMasterPlot();
				plot.addPropertyChangeListener(this);
				plot.getViewport().addChangeListener(this);
			}
		}

		if (this.currentSignal != null) {
			digestSignalInfo(this.currentSignal);
		}

	}

	// TODO this could be optimized slightly by reacting separately
	// to changes which involve changing only postion or only selection
	// moreover in some situations this can be called repeatedly a few times in a row
	// (for example when selection is removed due to passing offscreen)
	private void digestSignalInfo(SignalDocument document) {

		SignalView view = (SignalView) document.getDocumentView();
		SignalPlot plot = view.getMasterPlot();

		Point position = plot.getViewport().getViewPosition();
		Dimension size = plot.getViewport().getExtentSize();
		Point endPosition = new Point(position.x + size.width - 1, position.y + size.height - 1);

		double timeZoomFactor = plot.getTimeZoomFactor();

		int minSample = (int) Math.max(0, Math.floor(((double)(position.x)) / timeZoomFactor)) + 1;
		int maxSample = (int) Math.max(0, Math.ceil(((double)(endPosition.x)) / timeZoomFactor)) + 1;

		float minTime = plot.toTimeSpace(position);
		float maxTime = plot.toTimeSpace(endPosition);

		int minPage = plot.toPageSpace(position) + 1;
		int maxPage = plot.toPageSpace(endPosition) + 1;

		int minBlock = plot.toBlockSpace(position) + 1;
		int maxBlock = plot.toBlockSpace(endPosition) + 1;

		StringBuilder sb = new StringBuilder();

		FormatUtils.addTime(minTime, sb);
		sb.append(" - ");
		FormatUtils.addTime(maxTime, sb);

		sb.append(" (").append(sampleAbbrevString).append(": ").append(minSample).append('-').append(maxSample).append(')');
		sb.append(" (").append(pageAbbrevString).append(": ").append(minPage).append('-').append(maxPage).append(')');
		sb.append(" (").append(blockAbbrevString).append(": ").append(minBlock).append('-').append(maxBlock).append(')');

		setPosition(sb.toString());

		sb = new StringBuilder();

		SignalSelection signalSelection = view.getSignalSelection();
		if (signalSelection != null) {
			addSignalSelection(document, view.getSignalSelectionPlot(), signalSelection, sb);
		} else {
			PositionedTag tagSelection = view.getTagSelection();
			if (tagSelection != null) {
				addTag(document, view.getTagSelectionPlot(), tagSelection.getTag(), sb);
			}
		}

		setSelection(sb.toString());

		setFiltered(document.getMontage().isFiltered());

	}

	private void setFiltered(boolean filtered) {

		JLabel label = getFilteringLabel();
		if (filtered) {
			label.setIcon(FILTER_ON_ICON);
			label.setForeground(Color.BLUE);
			label.setText(filterOnString);
			label.setToolTipText(filterOnToolTipString);
		} else {
			label.setIcon(FILTER_OFF_ICON);
			label.setForeground(Color.LIGHT_GRAY);
			label.setText(filterOffString);
			label.setToolTipText(filterOffToolTipString);
		}

	}

	private void addTag(SignalDocument document, SignalPlot plot, Tag tag, StringBuilder sb) {

		SignalSelectionType type = tag.getType();
		if (type.isPage()) {

			sb.append(pageTagString).append(" \"").append(tag.getStyle().getDescriptionOrName()).append("\" ");
			addPageSelectionInfo(plot, tag, sb);

		}
		else if (type.isBlock()) {

			sb.append(blockTagString).append(" \"").append(tag.getStyle().getDescriptionOrName()).append("\" ");
			addBlockSelectionInfo(plot, tag, sb);

		}
		else if (type.isChannel()) {

			sb.append(channelTagString).append(" \"").append(tag.getStyle().getDescriptionOrName()).append("\" ");
			addChannelTagInfo(document, plot, tag, sb);

		} else {
			throw new SanityCheckException("Bad type [" + type + "]");
		}

	}

	private void addSignalSelection(SignalDocument document, SignalPlot plot, SignalSelection signalSelection, StringBuilder sb) {

		SignalSelectionType type = signalSelection.getType();
		if (type.isPage()) {

			sb.append(pageSelectionString).append(' ');
			addPageSelectionInfo(plot, signalSelection, sb);

		}
		else if (type.isBlock()) {

			sb.append(blockSelectionString).append(' ');
			addBlockSelectionInfo(plot, signalSelection, sb);

		}
		else if (type.isChannel()) {

			sb.append(channelSelectionString).append(' ');
			addChannelSelectionInfo(document, plot, signalSelection, sb);

		} else {
			throw new SanityCheckException("Bad type [" + type + "]");
		}

	}

	private void addPageSelectionInfo(SignalPlot plot, SignalSelection signalSelection, StringBuilder sb) {

		double time = signalSelection.getPosition();
		double endTime = time + signalSelection.getLength();

		FormatUtils.addTime(time, sb);
		sb.append(" - ");
		FormatUtils.addTime(endTime, sb);

		sb.append(" (").append(pageAbbrevString).append(": ");
		sb.append(signalSelection.getStartSegment(plot.getPageSize())+1).append('-');
		sb.append(signalSelection.getEndSegment(plot.getPageSize())).append(')');

	}

	private void addBlockSelectionInfo(SignalPlot plot, SignalSelection signalSelection, StringBuilder sb) {

		double time = signalSelection.getPosition();
		double endTime = time + signalSelection.getLength();

		FormatUtils.addTime(time, sb);
		sb.append(" - ");
		FormatUtils.addTime(endTime, sb);

		sb.append(" (").append(blockAbbrevString).append(": ");
		sb.append(signalSelection.getStartSegment(plot.getBlockSize())+1).append('-');
		sb.append(signalSelection.getEndSegment(plot.getBlockSize())).append(')');

	}

	private void addChannelSelectionInfo(SignalDocument document, SignalPlot plot, SignalSelection signalSelection, StringBuilder sb) {

		double time = signalSelection.getPosition();
		double endTime = time + signalSelection.getLength();

		FormatUtils.addTime(time, sb);
		sb.append(" - ");
		FormatUtils.addTime(endTime, sb);

		Montage montage = plot.getLocalMontage();
		if (montage == null) {
			montage = document.getMontage();
		}

		if (signalSelection.getChannel() != SignalSelection.CHANNEL_NULL)
			sb.append(" (").append(montage.getMontageChannelLabelAt(signalSelection.getChannel())).append(')');
		else
			sb.append(" (all channels)");

	}

	private void addChannelTagInfo(SignalDocument document, SignalPlot plot, Tag tag, StringBuilder sb) {

		double time = tag.getPosition();
		double endTime = time + tag.getLength();

		FormatUtils.addTime(time, sb);
		sb.append(" - ");
		FormatUtils.addTime(endTime, sb);

		Montage montage = plot.getLocalMontage();
		if (montage == null) {
			montage = document.getMontage();
		}

		if (tag.getChannel() != -1)
			sb.append(" (").append(montage.getSourceChannelLabelAt(tag.getChannel())).append(')');
		else
			sb.append(" (all channels)");

	}

	public void clearInfo() {
		setCurrentSignal(null);
		setCurrentBook(null);
		setPosition("");
		setSelection("");
	}

	public ViewModeAction getMaximizeDocumentsAction() {
		return viewModeAction;
	}

	public void setMaximizeDocumentsAction(ViewModeAction viewModeAction) {
		this.viewModeAction = viewModeAction;
	}

	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}
}

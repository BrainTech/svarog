/* BookView.java created 2008-02-23
 *
 */

package org.signalml.app.view.book;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.action.book.BookFilterSwitchAction;
import org.signalml.app.action.book.EditBookFilterAction;
import org.signalml.app.action.book.ShowAtomTableAction;
import org.signalml.app.action.book.ShowNextBookChannelAction;
import org.signalml.app.action.book.ShowNextBookSegmentAction;
import org.signalml.app.action.book.ShowPreviousBookChannelAction;
import org.signalml.app.action.book.ShowPreviousBookSegmentAction;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.ActionFocusSupport;
import org.signalml.app.action.selector.BookDocumentFocusSelector;
import org.signalml.app.action.selector.BookPlotFocusSelector;
import org.signalml.app.action.selector.BookViewFocusSelector;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.book.filter.BookFilterDialog;
import org.signalml.app.view.book.popup.BookPlotOptionsPopupDialog;
import org.signalml.app.view.book.popup.BookZoomPopupDialog;
import org.signalml.app.view.book.tools.BookTool;
import org.signalml.app.view.book.tools.SelectAtomBookTool;
import org.signalml.app.view.book.tools.ZoomBookTool;
import org.signalml.app.view.book.wignermap.WignerMapPalette;
import org.signalml.app.view.book.wignermap.WignerMapPaletteComboBoxCellRenderer;
import org.signalml.app.view.book.wignermap.WignerMapScaleComboBoxCellRenderer;
import org.signalml.app.view.common.components.panels.TitledSliderPanel;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.common.dialogs.HelpDialog;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.domain.book.BookFilterProcessor;
import org.signalml.domain.book.WignerMapScaleType;
import org.signalml.domain.book.filter.AtomFilterChain;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.DocumentView;
import org.springframework.core.io.ClassPathResource;

/** BookView
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookView extends DocumentView implements PropertyChangeListener, BookDocumentFocusSelector, BookViewFocusSelector, BookPlotFocusSelector {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(BookView.class);

	public static final String CURRENT_SEGMENT_PROPERTY = "currentSegment";
	public static final String CURRENT_CHANNEL_PROPERTY = "currentChannel";

	private ActionFocusSupport afSupport = new ActionFocusSupport(this);

	private boolean closed = false;

	private ApplicationConfiguration applicationConfig;
	private PleaseWaitDialog pleaseWaitDialog;

	private ActionFocusManager actionFocusManager;
	private ShowAtomTableAction showAtomTableAction;
	private EditBookFilterAction editBookFilterAction;
	private BookFilterSwitchAction filterSwitchAction;

	private ShowPreviousBookSegmentAction previousSegmentAction;
	private ShowNextBookSegmentAction nextSegmentAction;
	private ShowPreviousBookChannelAction previousChannelAction;
	private ShowNextBookChannelAction nextChannelAction;

	private SegmentTextField segmentTextField;
	private ChannelTextField channelTextField;

	private JComboBox paletteComboBox;
	private JComboBox scaleComboBox;

	private JSlider reconstructionHeightSlider;

	private JToolBar mainToolBar;

	private BookDocument document;
	private BookFilterProcessor filter;

	private JPanel contentPane;

	private BookPlot plot;
	private ButtonGroup toolButtonGroup;

	private JButton bookOptionsButton;
	private JButton resetZoomToolButton;

	private JToggleButton selectToolButton;
	private JToggleButton zoomToolButton;

	private BookTool currentBookTool;

	private SelectAtomBookTool selectAtomTool;
	private ZoomBookTool zoomBookTool;

	private Map<ButtonModel,BookTool> toolMap = new HashMap<ButtonModel,BookTool>();

	private DocumentFlowIntegrator documentFlowIntegrator;
	private ViewerFileChooser fileChooser;

	private AtomTableDialog atomTableDialog;
	private BookFilterDialog bookFilterDialog;

	private BookPlotOptionsPopupDialog bookOptionsPopupDialog;
	private BookZoomPopupDialog bookZoomPopupDialog;

	private FocusListener plotFocusListener;
	private MouseListener plotActivationMouseListener;

	private BookToolForwardingMouseAdapter toolMouseAdapter;

	private final HelpDialog helpDialog = new HelpDialog(null, false);

	private int currentSegment;
	private int currentChannel;

	public BookView(BookDocument document) {
		super(new BorderLayout());
		this.document = document;
		document.addPropertyChangeListener(this);
	}

	public void initialize() throws SignalMLException {

		filter = new BookFilterProcessor(document.getBook());
		filter.setFilterChain(document.getFilterChain());

		plotFocusListener = new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				logger.debug("Focus gained by plot [" + e.getSource().toString() + "]");
			}

			@Override
			public void focusLost(FocusEvent e) {
				logger.debug("Focus lost by plot [" + e.getSource().toString() + "]");
			}
		};

		plotActivationMouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				plot.requestFocusInWindow();
			}
		};

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				plot.requestFocusInWindow();
			}
		});

		toolMouseAdapter = new BookToolForwardingMouseAdapter();

		contentPane = new JPanel(new BorderLayout());

		createBookPlot();

		buildMainToolBar();

		buildTools();

		add(mainToolBar, BorderLayout.NORTH);
		add(contentPane, BorderLayout.CENTER);

		currentSegment = 0;
		currentChannel = 0;

		KeyStroke plus = KeyStroke.getKeyStroke("typed +");
		KeyStroke right = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false);
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(plus, "nextSegment");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(right, "nextSegment");
		getActionMap().put("nextSegment", getNextSegmentAction());

		KeyStroke minus = KeyStroke.getKeyStroke("typed -");
		KeyStroke left = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false);
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(minus, "previousSegment");
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(left, "previousSegment");
		getActionMap().put("previousSegment", getPreviousSegmentAction());

		KeyStroke down = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false);
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(down, "nextChannel");
		getActionMap().put("nextChannel", getNextChannelAction());

		KeyStroke up = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false);
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(up, "previousChannel");
		getActionMap().put("previousChannel", getPreviousChannelAction());

	}

	private void createBookPlot() throws SignalMLException {

		plot = new BookPlot(this);
		plot.loadSettingsFromApplicationConfiguration();
		plot.setPleaseWaitDialog(pleaseWaitDialog);

		BookPlotPopupProvider bookPlotPopupProvider = new BookPlotPopupProvider(plot);

		// TODO any popup menu?
//		plot.setPopupMenuProvider(bookPlotPopupProvider);

		plot.addMouseListener(plotActivationMouseListener);

		plot.addMouseMotionListener(toolMouseAdapter);
		plot.addMouseListener(toolMouseAdapter);
		plot.addMouseWheelListener(toolMouseAdapter);

		contentPane.add(plot, BorderLayout.CENTER);

		plot.addFocusListener(plotFocusListener);

		plot.setSegment(filter.getSegmentAt(currentSegment, currentChannel));

		plot.initialize();

		plot.requestFocusInWindow();

	}

	@Override
	public BookDocument getActiveBookDocument() {
		return document;
	}

	@Override
	public Document getActiveDocument() {
		return document;
	}

	@Override
	public BookView getActiveBookView() {
		return this;
	}

	@Override
	public BookPlot getActiveBookPlot() {
		return plot;
	}

	public BookPlot getPlot() {
		return plot;
	}

	public int getCurrentSegment() {
		return currentSegment;
	}

	public void setCurrentSegment(int currentSegment) {
		if (this.currentSegment != currentSegment) {
			int oldSegment = this.currentSegment;
			this.currentSegment = currentSegment;
			plot.setSegment(filter.getSegmentAt(currentSegment, currentChannel));
			firePropertyChange(CURRENT_SEGMENT_PROPERTY, oldSegment, currentSegment);
		}
	}

	public int getSegmentCount() {
		return document.getBook().getSegmentCount();
	}

	public boolean hasPreviousSegment() {
		return(currentSegment > 0);
	}

	public void showPreviousSegment() {
		if (hasPreviousSegment()) {
			setCurrentSegment(currentSegment - 1);
		}
	}

	public boolean hasNextSegment() {
		return(currentSegment < (document.getBook().getSegmentCount()-1));
	}

	public void showNextSegment() {
		if (hasNextSegment()) {
			setCurrentSegment(currentSegment + 1);
		}
	}

	public int getCurrentChannel() {
		return currentChannel;
	}

	public void setCurrentChannel(int currentChannel) {
		if (this.currentChannel != currentChannel) {
			int oldChannel = this.currentChannel;
			this.currentChannel = currentChannel;
			plot.setSegment(filter.getSegmentAt(currentSegment, currentChannel));
			firePropertyChange(CURRENT_CHANNEL_PROPERTY, oldChannel, currentChannel);
		}
	}

	public int getChannelCount() {
		return document.getBook().getChannelCount();
	}

	public boolean hasPreviousChannel() {
		return(currentChannel > 0);
	}

	public void showPreviousChannel() {
		if (hasPreviousChannel()) {
			setCurrentChannel(currentChannel - 1);
		}
	}

	public boolean hasNextChannel() {
		return(currentChannel < (document.getBook().getChannelCount()-1));
	}

	public void showNextChannel() {
		if (hasNextChannel()) {
			setCurrentChannel(currentChannel + 1);
		}
	}

	@Override
	public void addActionFocusListener(ActionFocusListener listener) {
		afSupport.addActionFocusListener(listener);
	}

	@Override
	public void removeActionFocusListener(ActionFocusListener listener) {
		afSupport.removeActionFocusListener(listener);
	}

	private void buildTools() {

		selectAtomTool = new SelectAtomBookTool(this);
		zoomBookTool = new ZoomBookTool(this);

		currentBookTool = selectAtomTool;

		toolMouseAdapter.setSelectAtomBookTool(selectAtomTool);
		toolMouseAdapter.setBookTool(currentBookTool);

		toolMap.put(selectToolButton.getModel(), selectAtomTool);
		toolMap.put(zoomToolButton.getModel(), zoomBookTool);

		toolButtonGroup = new ButtonGroup();
		toolButtonGroup.add(selectToolButton);
		toolButtonGroup.add(zoomToolButton);

		ActionListener toolSelectionListener = new ToolSelectionListener();
		selectToolButton.addActionListener(toolSelectionListener);
		zoomToolButton.addActionListener(toolSelectionListener);

		selectToolButton.setSelected(true);

	}

	private void buildMainToolBar() {

		mainToolBar = new JToolBar();
		mainToolBar.setFloatable(false);

		bookOptionsButton = new JButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/plotoptions.png"));
		bookOptionsButton.setToolTipText(_("Change plot options"));
		bookOptionsButton.addActionListener(new PlotOptionsButtonListener());

		selectToolButton = new JToggleButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/arrow.png"));
		selectToolButton.setToolTipText(_("Selects atoms for reconstruction"));

		zoomToolButton = new JToggleButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/zoom.png"));
		zoomToolButton.setToolTipText(_("Zooms the map (hold down Ctrl to zoom out; hold down mouse button for options)"));
		zoomToolButton.addMouseListener(new ZoomBookToolButtonMouseListener());

		resetZoomToolButton = new JButton(IconUtils.loadClassPathIcon("org/signalml/app/icon/reset.png"));
		resetZoomToolButton.setToolTipText(_("Resets map to default view"));
		resetZoomToolButton.addMouseListener(new ResetZoomBookToolButtonMouseListener());

		mainToolBar.add(selectToolButton);
		mainToolBar.add(zoomToolButton);
		mainToolBar.add(resetZoomToolButton);
		selectToolButton.setSelected(true);

		mainToolBar.addSeparator();

		mainToolBar.add(getPreviousSegmentAction());
		mainToolBar.addSeparator(new Dimension(2,2));
		mainToolBar.add(getSegmentTextField());
		mainToolBar.addSeparator(new Dimension(2,2));
		mainToolBar.add(getNextSegmentAction());

		mainToolBar.addSeparator();

		mainToolBar.add(getPreviousChannelAction());
		mainToolBar.addSeparator(new Dimension(2,2));
		mainToolBar.add(getChannelTextField());
		mainToolBar.addSeparator(new Dimension(2,2));
		mainToolBar.add(getNextChannelAction());

		mainToolBar.addSeparator();

		mainToolBar.add(getShowAtomTableAction());

		mainToolBar.addSeparator();

		mainToolBar.add(Box.createHorizontalGlue());

		mainToolBar.add(getEditBookFilterAction());

		mainToolBar.addSeparator();

		mainToolBar.add(getScaleComboBox());
		mainToolBar.addSeparator(new Dimension(2,2));
		mainToolBar.add(getPaletteComboBox());
		mainToolBar.addSeparator(new Dimension(2,2));
		mainToolBar.add(bookOptionsButton);
		mainToolBar.add(new TitledSliderPanel(_("Reconstruction height"), getReconstructionHeightSlider()));

		JToggleButton filterSwitchButton = new JToggleButton(getFilterSwitchAction());
		filterSwitchButton.setHideActionText(true);
		filterSwitchButton.setSelectedIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/filteron.png"));
		mainToolBar.add(filterSwitchButton);

		try {
			AbstractAction helpAction = getContextHelpAction();
			mainToolBar.add(new JButton(helpAction));
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(BookView.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public BookFilterProcessor getFilter() {
		return filter;
	}

	public boolean isToolEngaged() {
		return (currentBookTool != null && currentBookTool.isEngaged());
	}

	public BookDocument getDocument() {
		return document;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		return documentFlowIntegrator;
	}

	public void setDocumentFlowIntegrator(DocumentFlowIntegrator documentFlowIntegrator) {
		this.documentFlowIntegrator = documentFlowIntegrator;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public AtomTableDialog getAtomTableDialog() {
		return atomTableDialog;
	}

	public void setAtomTableDialog(AtomTableDialog atomTableDialog) {
		this.atomTableDialog = atomTableDialog;
	}

	public BookFilterDialog getBookFilterDialog() {
		return bookFilterDialog;
	}

	public void setBookFilterDialog(BookFilterDialog bookFilterDialog) {
		this.bookFilterDialog = bookFilterDialog;
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	private BookPlotOptionsPopupDialog getPlotOptionsDialog() {
		if (bookOptionsPopupDialog == null) {
			bookOptionsPopupDialog = new BookPlotOptionsPopupDialog((Window) getTopLevelAncestor(), true);
			bookOptionsPopupDialog.setBookView(this);
		}

		return bookOptionsPopupDialog;
	}

	public BookZoomPopupDialog getBookZoomPopupDialog() {
		if (bookZoomPopupDialog == null) {
			bookZoomPopupDialog = new BookZoomPopupDialog((Window) getTopLevelAncestor(), true);
		}
		return bookZoomPopupDialog;
	}

	public ShowAtomTableAction getShowAtomTableAction() {
		if (showAtomTableAction == null) {
			showAtomTableAction = new ShowAtomTableAction(this);
			showAtomTableAction.setAtomTableDialog(getAtomTableDialog());
		}
		return showAtomTableAction;
	}

	public EditBookFilterAction getEditBookFilterAction() {
		if (editBookFilterAction == null) {
			editBookFilterAction = new EditBookFilterAction(this);
			editBookFilterAction.setBookFilterDialog(getBookFilterDialog());
		}
		return editBookFilterAction;
	}

	public BookFilterSwitchAction getFilterSwitchAction() {
		if (filterSwitchAction == null) {
			filterSwitchAction = new BookFilterSwitchAction(this);
		}
		return filterSwitchAction;
	}

	public AbstractAction getContextHelpAction() throws IOException {
		final URL contextHelpURL = (new ClassPathResource("org/signalml/help/viewerBookMP5.html")).getURL();
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				helpDialog.reset();
				helpDialog.showDialog(contextHelpURL, true);
			}
		};
		action.putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/help.png"));
		action.putValue(AbstractAction.SHORT_DESCRIPTION,_("Display context help for this dialog"));
		return action;
	}

	public ShowPreviousBookSegmentAction getPreviousSegmentAction() {
		if (previousSegmentAction == null) {
			previousSegmentAction = new ShowPreviousBookSegmentAction(this);

			this.addPropertyChangeListener(CURRENT_SEGMENT_PROPERTY, previousSegmentAction);
		}
		return previousSegmentAction;
	}

	public ShowNextBookSegmentAction getNextSegmentAction() {
		if (nextSegmentAction == null) {
			nextSegmentAction = new ShowNextBookSegmentAction(this);

			this.addPropertyChangeListener(CURRENT_SEGMENT_PROPERTY, nextSegmentAction);
		}
		return nextSegmentAction;
	}

	public ShowPreviousBookChannelAction getPreviousChannelAction() {
		if (previousChannelAction == null) {
			previousChannelAction = new ShowPreviousBookChannelAction(this);

			this.addPropertyChangeListener(CURRENT_CHANNEL_PROPERTY, previousChannelAction);
		}
		return previousChannelAction;
	}

	public ShowNextBookChannelAction getNextChannelAction() {
		if (nextChannelAction == null) {
			nextChannelAction = new ShowNextBookChannelAction(this);

			this.addPropertyChangeListener(CURRENT_CHANNEL_PROPERTY, nextChannelAction);
		}
		return nextChannelAction;
	}

	public SegmentTextField getSegmentTextField() {
		if (segmentTextField == null) {
			segmentTextField = new SegmentTextField(this);

			segmentTextField.setPreferredSize(new Dimension(100, 25));
			segmentTextField.setToolTipText(_("Current segment"));
		}
		return segmentTextField;
	}

	public ChannelTextField getChannelTextField() {
		if (channelTextField == null) {
			channelTextField = new ChannelTextField(this);

			channelTextField.setPreferredSize(new Dimension(100,25));
			channelTextField.setToolTipText(_("Current channel"));
		}
		return channelTextField;
	}

	public JComboBox getPaletteComboBox() {
		if (paletteComboBox == null) {

			DefaultComboBoxModel model = new DefaultComboBoxModel(WignerMapPalette.values());

			paletteComboBox = new JComboBox(model);
			Dimension boxDimension = new Dimension(110,25);
			paletteComboBox.setPreferredSize(boxDimension);
			paletteComboBox.setMinimumSize(new Dimension(60,25));
			paletteComboBox.setMaximumSize(boxDimension);
			paletteComboBox.setAlignmentY(Component.CENTER_ALIGNMENT);

			paletteComboBox.setRenderer(new WignerMapPaletteComboBoxCellRenderer());

			paletteComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					if (e.getStateChange() == ItemEvent.SELECTED) {
						plot.setPalette((WignerMapPalette) paletteComboBox.getSelectedItem());
					}

				}

			});

		}

		return paletteComboBox;
	}

	public JComboBox getScaleComboBox() {
		if (scaleComboBox == null) {

			DefaultComboBoxModel model = new DefaultComboBoxModel(WignerMapScaleType.values());

			scaleComboBox = new JComboBox(model);
			Dimension boxDimension = new Dimension(120,25);
			scaleComboBox.setPreferredSize(boxDimension);
			scaleComboBox.setMinimumSize(new Dimension(60,25));
			scaleComboBox.setMaximumSize(boxDimension);
			scaleComboBox.setAlignmentY(Component.CENTER_ALIGNMENT);

			scaleComboBox.setRenderer(new WignerMapScaleComboBoxCellRenderer());

			scaleComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {

					if (e.getStateChange() == ItemEvent.SELECTED) {
						WignerMapScaleType scaleType = (WignerMapScaleType) scaleComboBox.getSelectedItem();
						plot.setScaleType(scaleType);
					}

				}

			});

		}
		return scaleComboBox;
	}

	public JSlider getReconstructionHeightSlider() {
		if (reconstructionHeightSlider == null) {
			reconstructionHeightSlider = new JSlider(new DefaultBoundedRangeModel(BookPlot.RECONSTRUCTION_HEIGHT, 0, BookPlot.MIN_RECONSTRUCTION_HEIGHT, BookPlot.MAX_RECONSTRUCTION_HEIGHT)) {

				private static final long serialVersionUID = 1L;

				@Override
				public String getToolTipText(MouseEvent ev) {
					return _R("{0} px", getValue());
				}

			};
			reconstructionHeightSlider.setToolTipText("");
			Dimension size = reconstructionHeightSlider.getPreferredSize();
			size.width = 135;
			reconstructionHeightSlider.setPreferredSize(size);
			reconstructionHeightSlider.setMinimumSize(size);
			reconstructionHeightSlider.setMaximumSize(size);

			reconstructionHeightSlider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					plot.setReconstructionHeight(reconstructionHeightSlider.getValue());
				}
			});

		}
		return reconstructionHeightSlider;
	}

	@Override
	public void destroy() {
		document.removePropertyChangeListener(this);
		plot.destroy();
		closed = true;
	}

	public boolean isClosed() {
		return closed;
	}

	public ZoomBookTool getZoomBookTool() {
		return zoomBookTool;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		Object source = evt.getSource();
		String propertyName = evt.getPropertyName();

		if (source == document) {

			if (BookDocument.FILTER_CHAIN_PROPERTY.equals(propertyName)) {

				AtomFilterChain atomFilterChain = (AtomFilterChain) evt.getNewValue();
				filter.setFilterChain(atomFilterChain);

				getFilterSwitchAction().putValue(AbstractAction.SELECTED_KEY, new Boolean(atomFilterChain.isFilteringEnabled()));

				plot.setSegment(filter.getSegmentAt(currentSegment, currentChannel));

			}

		}

	}

	private void setCurrentBookTool(BookTool tool) {

		currentBookTool = tool;

		plot.setCursor(currentBookTool.getDefaultCursor());

		toolMouseAdapter.setBookTool(currentBookTool);

	}

	private class PlotOptionsButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			BookPlotOptionsPopupDialog dialog = getPlotOptionsDialog();
			dialog.initializeNow();
			Container ancestor = getTopLevelAncestor();
			Point containerLocation = ancestor.getLocation();
			Point location = SwingUtilities.convertPoint(bookOptionsButton, new Point(0,0), ancestor);
			location.translate(containerLocation.x-(dialog.getSize().width-bookOptionsButton.getSize().width), containerLocation.y);
			dialog.setLocation(location);
			dialog.showDialog(null);
		}

	}

	private class ToolSelectionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			BookTool bookTool = toolMap.get(toolButtonGroup.getSelection());
			if (bookTool != null) {
				setCurrentBookTool(bookTool);
			} else {
				logger.warn("WARNING: unbound book tool");
			}

		}

	}

	private class ZoomBookToolButtonMouseListener extends MouseAdapter {

		private Timer timer;

		ActionListener timerListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BookZoomPopupDialog dialog = getBookZoomPopupDialog();
				Container ancestor = getTopLevelAncestor();
				Point containerLocation = ancestor.getLocation();
				Point location = SwingUtilities.convertPoint(zoomToolButton, new Point(0,0), ancestor);
				location.translate(containerLocation.x, containerLocation.y);
				dialog.setLocation(location);
				zoomToolButton.doClick();
				dialog.showDialog(BookView.this);
			}
		};

		@Override
		public void mousePressed(MouseEvent e) {

			if (timer == null) {
				timer = new Timer(400, timerListener); // popup after 400 ms
				timer.setRepeats(false);
			}

			timer.start();

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (timer != null) {
				timer.stop();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (timer != null) {
				timer.stop();
			}
		}

	}

	private class ResetZoomBookToolButtonMouseListener extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {

			plot.setZoom(
				0,
				plot.getSegment().getSegmentLength(),
				0,
				plot.getSegment().getSamplingFrequency()
			);

		}

	}

	/**
	 * Saves settings set for this view to the {@ling ApplicationConfiguration}.
	 */
	public void saveSettingsToApplicationConfiguration()
	{
		applicationConfig.setScaleType((WignerMapScaleType) scaleComboBox.getSelectedItem());
		applicationConfig.setReconstructionHeight(reconstructionHeightSlider.getValue());
		applicationConfig.setPalette((WignerMapPalette) paletteComboBox.getSelectedItem());

		getPlotOptionsDialog().saveSettingsToApplicationConfiguration();
	}
}

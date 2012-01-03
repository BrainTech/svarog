package org.signalml.app.view.element;

import static org.signalml.app.SvarogI18n._;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.BlockSelectionModelProvider;
import org.signalml.app.model.ChannelSelectionModelProvider;
import org.signalml.app.model.PageSelectionModelProvider;
import org.signalml.app.util.SwingUtils;
import org.signalml.domain.signal.BoundedSignalSelection;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;

import org.springframework.validation.Errors;

/**
 * Panel which allows to select the parameters of a {@link SignalSelection
 * signal selection}.
 * Contains the {@link SignalSelectionTypePanel panel} which allows to select
 * the type of the selection.
 * Depending on this type activates the appropriate card in the card panel:
 * <ul>
 * <li>for a {@link SignalSelectionType#PAGE PAGE} selection -
 * the {@link #pageSignalSelectionPanel panel} which allows to select
 * the options of a page signal selection,</li>
 * <li>for a {@link SignalSelectionType#BLOCK BLOCK} selection -
 * the {@link #blockSignalSelectionPanel panel} which allows to select
 * the options of a block signal selection,</li>
 * <li>for a {@link SignalSelectionType#CHANNEL CHANNEL} selection -
 * the {@link #channelSignalSelectionPanel panel} which allows to select
 * the options of a channel signal selection.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(SignalSelectionPanel.class);
	/**
	 * the card layout for {@link #cardPanel} 
	 */
	private CardLayout cardLayout;
	/**
	 * the panel with {@link #cardLayout CardLayout} and 3 cards:
	 * <ul>
	 * <li>the {@link #pageSignalSelectionPanel panel} which allows to select
	 * the options of a page signal selection</li>
	 * <li>the {@link #blockSignalSelectionPanel panel} which allows to select
	 * the options of a block signal selection</li>
	 * <li>the {@link #channelSignalSelectionPanel panel} which allows to select
	 * the options of a channel signal selection</li>
	 * </ul>
	 */
	private JPanel cardPanel;
	/**
	 * the {@link SignalSelectionTypePanel panel} which allows to select
	 * the {@link SignalSelectionType type} of the selection
	 */
	private SignalSelectionTypePanel signalSelectionTypePanel;
	/**
	 * The {@link PageSignalSelectionPanel panel} which allows to select
	 * the options of a page signal selection.
	 * This panel is visible if {@link SignalSelectionType#PAGE PAGE} type
	 * is selected.
	 */
	private PageSignalSelectionPanel pageSignalSelectionPanel;
	/**
	 * The {@link BlockSignalSelectionPanel panel} which allows to select
	 * the options of a block signal selection.
	 * This panel is visible if {@link SignalSelectionType#BLOCK BLOCK} type
	 * is selected.
	 */
	private BlockSignalSelectionPanel blockSignalSelectionPanel;
	/**
	 * The {@link ChannelSignalSelectionPanel panel} which allows to select
	 * the options of a channel signal selection.
	 * This panel is visible if {@link SignalSelectionType#CHANNEL CHANNEL}
	 * type is selected.
	 */
	private ChannelSignalSelectionPanel channelSignalSelectionPanel;
	/**
	 * the {@link SignalSpaceConstraints parameters} of the signal
	 */
	private SignalSpaceConstraints currentConstraints;
	/**
	 * the selection created from the current status of the fields in this
	 * dialog
	 */
	private BoundedSignalSelection currentBss;
	/**
	 * {@code true} if this panel should allow to
	 * select a channel (for a channel selection), {@code false} otherwise
	 */
	private boolean withChannelSelection;

	/**
	 * Constructor. Initializes the panel.
	 * @param withChannelSelection {@code true} if this panel should allow to
	 * select a channel (for a channel selection), {@code false} otherwise
	 */
	public SignalSelectionPanel(boolean withChannelSelection) {
		super();
		this.withChannelSelection = withChannelSelection;
		initialize();
	}

	/**
	 * Initializes this panel with border layout and two sub-panels (from top
	 * to bottom):
	 * <ul>
	 * <li>the {@link SignalSelectionTypePanel panel} which allows to select
	 * the {@link SignalSelectionType type} of the selection,</li>
	 * <li>the card panel with 3 cards:
	 * <ul>
	 * <li>the {@link #pageSignalSelectionPanel panel} which allows to select
	 * the options of a page signal selection,</li>
	 * <li>the {@link #blockSignalSelectionPanel panel} which allows to select
	 * the options of a block signal selection,</li>
	 * <li>the {@link #channelSignalSelectionPanel panel} which allows to select
	 * the options of a channel signal selection.</li>
	 * </ul></li></ul>
	 * For every button in the {@link SignalSelectionTypePanel} adds a listener
	 * which activates the appropriate card.
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		signalSelectionTypePanel = new SignalSelectionTypePanel();
		add(signalSelectionTypePanel, BorderLayout.NORTH);

		pageSignalSelectionPanel = new PageSignalSelectionPanel();
		blockSignalSelectionPanel = new BlockSignalSelectionPanel();
		channelSignalSelectionPanel = new ChannelSignalSelectionPanel(withChannelSelection);

		cardLayout = new CardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		cardPanel.setBorder(new TitledBorder(_("Selection parameters")));

		cardPanel.add(pageSignalSelectionPanel, "page");
		cardPanel.add(blockSignalSelectionPanel, "block");
		cardPanel.add(channelSignalSelectionPanel, "channel");

		signalSelectionTypePanel.getPageRadio().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanel, "page");
			}
		});

		signalSelectionTypePanel.getBlockRadio().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanel, "block");
			}
		});

		signalSelectionTypePanel.getChannelRadio().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardLayout.show(cardPanel, "channel");
			}
		});

		add(cardPanel, BorderLayout.CENTER);

	}

	/**
	 * Fills this panel (actually the sub-panels) with the data form the given
	 * {@link BoundedSignalSelection model}:
	 * <ul>
	 * <li>Sets the model for
	 * {@link PageSelectionModelProvider#getStartPageSpinnerModel() start
	 * page spinner} and
	 * {@link PageSelectionModelProvider#getLengthSpinnerModel() length spinner}.
	 * <br>If it is a {@link SignalSelectionType#PAGE PAGE} selection the
	 * {@link PageSelectionModelProvider provider} for the model is created
	 * using the parameters from the given {@link BoundedSignalSelection selection}:
	 * <ul>
	 * <li>the maximum number of the {@link BoundedSignalSelection#getMaxPage() page}
	 * that can be used,</li>
	 * <li>the number of the {@link SignalSelection#getStartSegment(float) first
	 * page} in the selection,</li>
	 * <li>the {@link SignalSelection#getSegmentLength(float) number of pages}
	 * in the selection,</li></ul>
	 * otherwise default parameters are used.
	 * </li>
	 * <li>Sets the model for spinners in {@link BlockSignalSelectionPanel}
	 * ({@link BlockSelectionModelProvider#getStartPageSpinnerModel() start
	 * page spinner},
	 * {@link BlockSelectionModelProvider#getStartBlockSpinnerModel()
	 * start block spinner} and {@link
	 * BlockSelectionModelProvider#getLengthSpinnerModel() length spinner}).
	 * <br>If it is a {@link SignalSelectionType#BLOCK BLOCK} selection the
	 * {@link BlockSelectionModelProvider provider} for the model is created
	 * using the parameters from the given {@link BoundedSignalSelection selection}:
	 * <ul>
	 * <li>the maximum number of the {@link BoundedSignalSelection#getMaxPage()
	 * page} that can be used,</li>
	 * <li>the maximum number of the {@link BoundedSignalSelection#getMaxBlock()
	 * block} that can be used,</li>
	 * <li>the {@link BoundedSignalSelection#getBlocksPerPage() number of blocks
	 * in a page},</li>
	 * <li>the number of the {@link SignalSelection#getStartSegment(float) first
	 * page} in the selection,</li>
	 * <li>the number of the first block in the selection,</li>
	 * <li>the {@link SignalSelection#getSegmentLength(float) number of blocks}
	 * in the selection),</li></ul>
	 * otherwise default parameters are used.
	 * </li>
	 * <li>Sets the model for spinners in {@link ChannelSignalSelectionPanel}
	 * ({@link ChannelSelectionModelProvider#getStartTimeSpinnerModel() start
	 * time spinner} and {@link
	 * ChannelSelectionModelProvider#getLengthSpinnerModel() length spinner}).
	 * <br>If it is a {@link SignalSelectionType#CHANNEL CHANNEL} selection the
	 * {@link ChannelSelectionModelProvider provider} for the model is created
	 * using the parameters from the given {@link BoundedSignalSelection selection}:
	 * <ul>
	 * <li>the maximum {@link BoundedSignalSelection#getMaxTime() time} that
	 * can be used,</li>
	 * <li>the {@link BoundedSignalSelection#getSamplingFrequency() sampling
	 * frequency},</li>
	 * <li>the {@link BoundedSignalSelection#getChannels() names} of channels,
	 * </li>
	 * <li>the {@link SignalSelection#getPosition() starting position} of the
	 * selection,</li>
	 * <li>the {@link SignalSelection#getLength() length} of the selection,</li>
	 * <li>the {@link SignalSelection#getChannel() channel} for the
	 * selection,</li></ul>
	 * otherwise default parameters are used.
	 * </li>
	 * <li>depending on the type sets the appropriate button in
	 * {@link SignalSelectionTypePanel} to be selected.</li>
	 * <li>If it is a {@code CHANNEL} selection sets the
	 * {@link ChannelSelectionModelProvider#getChannelComboBoxModel() model}
	 * for the {@link ChannelSignalSelectionPanel#getChannelComboBox()
	 * combo-box}.</li>
	 * </ul>
	 * @param bss the bounded selection
	 */
	public void fillPanelFromModel(BoundedSignalSelection bss) {

		JSpinner startPageSpinner;
		JSpinner startBlockSpinner;
		JSpinner lengthSpinner;

		SignalSelection selection = bss.getSelection();
		SignalSelectionType type = null;
		if (selection != null) {
			type = selection.getType();
		}
		PageSelectionModelProvider pageSelectionModelProvider;
		if (type != null && type.isPage()) {
			pageSelectionModelProvider = new PageSelectionModelProvider(
				bss.getMaxPage(),
				selection.getStartSegment(bss.getPageSize()) + 1,
				selection.getSegmentLength(bss.getPageSize()));
			signalSelectionTypePanel.getPageRadio().setSelected(true);
			cardLayout.show(cardPanel, "page");
		} else {
			pageSelectionModelProvider = new PageSelectionModelProvider(
				bss.getMaxPage(),
				1,
				1);
		}

		startPageSpinner = pageSignalSelectionPanel.getStartPageSpinner();
		lengthSpinner = pageSignalSelectionPanel.getLengthSpinner();

		SwingUtils.replaceSpinnerModel(startPageSpinner, pageSelectionModelProvider.getStartPageSpinnerModel());
		startPageSpinner.setEditor(new SpinnerNumberEditor(startPageSpinner));

		SwingUtils.replaceSpinnerModel(lengthSpinner, pageSelectionModelProvider.getLengthSpinnerModel());
		lengthSpinner.setEditor(new SpinnerNumberEditor(lengthSpinner));

		BlockSelectionModelProvider blockSelectionModelProvider;
		if (type != null && type.isBlock()) {
			float blockSize = ((float) bss.getPageSize()) / bss.getBlocksPerPage();
			int startSegment = selection.getStartSegment(blockSize);
			blockSelectionModelProvider = new BlockSelectionModelProvider(
				bss.getMaxPage(),
				bss.getMaxBlock(),
				bss.getBlocksPerPage(),
				(startSegment / bss.getBlocksPerPage()) + 1,
				(startSegment % bss.getBlocksPerPage()) + 1,
				selection.getSegmentLength(blockSize));
			signalSelectionTypePanel.getBlockRadio().setSelected(true);
			cardLayout.show(cardPanel, "block");
		} else {
			blockSelectionModelProvider = new BlockSelectionModelProvider(
				bss.getMaxPage(),
				bss.getMaxBlock(),
				bss.getBlocksPerPage(),
				1,
				1,
				1);
		}

		startPageSpinner = blockSignalSelectionPanel.getStartPageSpinner();
		startBlockSpinner = blockSignalSelectionPanel.getStartBlockSpinner();
		lengthSpinner = blockSignalSelectionPanel.getLengthSpinner();

		SwingUtils.replaceSpinnerModel(startPageSpinner, blockSelectionModelProvider.getStartPageSpinnerModel());
		startPageSpinner.setEditor(new SpinnerNumberEditor(startPageSpinner));

		SwingUtils.replaceSpinnerModel(startBlockSpinner, blockSelectionModelProvider.getStartBlockSpinnerModel());
		startBlockSpinner.setEditor(new SpinnerNumberEditor(startBlockSpinner));

		SwingUtils.replaceSpinnerModel(lengthSpinner, blockSelectionModelProvider.getLengthSpinnerModel());
		lengthSpinner.setEditor(new SpinnerNumberEditor(lengthSpinner));

		ChannelSelectionModelProvider channelSelectionModelProvider;
		if (type != null && type.isChannel()) {
			channelSelectionModelProvider = new ChannelSelectionModelProvider(
				bss.getMaxTime(),
				bss.getSamplingFrequency(),
				bss.getChannels(),
				selection.getPosition(),
				selection.getLength(),
				selection.getChannel());
			signalSelectionTypePanel.getChannelRadio().setSelected(true);
			cardLayout.show(cardPanel, "channel");
		} else {
			channelSelectionModelProvider = new ChannelSelectionModelProvider(
				bss.getMaxTime(),
				bss.getSamplingFrequency(),
				bss.getChannels(),
				0,
				1,
				0 //select first channel
				);
		}

		JSpinner startTimeSpinner = channelSignalSelectionPanel.getStartTimeSpinner();
		lengthSpinner = channelSignalSelectionPanel.getLengthSpinner();

		SwingUtils.replaceSpinnerModel(startTimeSpinner, channelSelectionModelProvider.getStartTimeSpinnerModel());
		startTimeSpinner.setEditor(new SpinnerNumberEditor(startTimeSpinner));

		SwingUtils.replaceSpinnerModel(lengthSpinner, channelSelectionModelProvider.getLengthSpinnerModel());
		lengthSpinner.setEditor(new SpinnerNumberEditor(lengthSpinner));

		if (withChannelSelection) {
			JComboBox channelComboBox = channelSignalSelectionPanel.getChannelComboBox();
			channelComboBox.setModel(channelSelectionModelProvider.getChannelComboBoxModel());
		}

		if (type == null) {
			signalSelectionTypePanel.getPageRadio().setSelected(true);
			cardLayout.show(cardPanel, "page");
		}

	}

	/**
	 * Depending on the selected {@link SignalSelectionType type}:
	 * <ul>
	 * <li>if the {@link SignalSelectionTypePanel#getPageRadio() button}
	 * for a {@link SignalSelectionType#PAGE PAGE} selection is selected:
	 * <ul><li>creates the selection of that type,</li>
	 * <li>sets the {@link SignalSelection#setPosition(float) starting position}
	 * using the {@link PageSignalSelectionPanel#getStartPageSpinner() start
	 * page spinner},</li>
	 * <li>sets the {@link SignalSelection#setLength(float) length}
	 * using the {@link PageSignalSelectionPanel#getLengthSpinner() length
	 * spinner},</li></ul></li>
	 * 
	 * <li>if the {@link SignalSelectionTypePanel#getBlockRadio() button}
	 * for a {@link SignalSelectionType#BLOCK BLOCK} selection is selected:
	 * <ul><li>creates the selection of that type,</li>
	 * <li>sets the {@link SignalSelection#setPosition(float) starting position}
	 * using the {@link BlockSignalSelectionPanel#getStartPageSpinner() start
	 * page} and {@link BlockSignalSelectionPanel#getStartBlockSpinner() start
	 * block} spinners,</li>
	 * <li>sets the {@link SignalSelection#setLength(float) length}
	 * using the {@link BlockSignalSelectionPanel#getLengthSpinner() length
	 * spinner},</li></ul></li>
	 * 
	 * <li>if the {@link SignalSelectionTypePanel#getChannelRadio() button}
	 * for a {@link SignalSelectionType#CHANNEL CHANNEL} selection is selected:
	 * <ul><li>creates the selection of that type,</li>
	 * <li>sets the {@link SignalSelection#setPosition(float) starting position}
	 * using the {@link ChannelSignalSelectionPanel#getStartTimeSpinner() start
	 * time spinner},</li>
	 * <li>sets the {@link SignalSelection#setLength(float) length}
	 * using the {@link ChannelSignalSelectionPanel#getLengthSpinner() length
	 * spinner},</li>
	 * <li>sets the {@link SignalSelection#setChannel(int) channel} using the
	 * {@link ChannelSignalSelectionPanel#getChannelComboBox() channel
	 * combo-box},</ul></li></ul>
	 * Stores the created selection in the {@link BoundedSignalSelection model}.
	 * @param bss the model (bounded signal selection)
	 */
	public void fillModelFromPanel(BoundedSignalSelection bss) {

		SignalSelection selection = null;

		if (signalSelectionTypePanel.getPageRadio().isSelected()) {

			int startPage = (Integer) pageSignalSelectionPanel.getStartPageSpinner().getValue();
			int length = (Integer) pageSignalSelectionPanel.getLengthSpinner().getValue();

			selection = new SignalSelection(SignalSelectionType.PAGE);
			selection.setPosition((startPage - 1) * bss.getPageSize());
			selection.setLength(length * bss.getPageSize());
			selection.setChannel(SignalSelection.CHANNEL_NULL);

		} else if (signalSelectionTypePanel.getBlockRadio().isSelected()) {

			int startPage = (Integer) blockSignalSelectionPanel.getStartPageSpinner().getValue();
			int startBlock = (Integer) blockSignalSelectionPanel.getStartBlockSpinner().getValue();
			int length = (Integer) blockSignalSelectionPanel.getLengthSpinner().getValue();
			float blockSize = ((float) bss.getPageSize()) / bss.getBlocksPerPage();

			selection = new SignalSelection(SignalSelectionType.BLOCK);
			selection.setPosition((startPage - 1) * bss.getPageSize() + (startBlock - 1) * blockSize);
			selection.setLength(length * blockSize);
			selection.setChannel(SignalSelection.CHANNEL_NULL);

		} else if (signalSelectionTypePanel.getChannelRadio().isSelected()) {

			double startTime = (Double) channelSignalSelectionPanel.getStartTimeSpinner().getValue();
			double length = (Double) channelSignalSelectionPanel.getLengthSpinner().getValue();

			selection = new SignalSelection(SignalSelectionType.CHANNEL);
			selection.setPosition(startTime);
			selection.setLength(length);

			if (withChannelSelection) {
				int channel = channelSignalSelectionPanel.getChannelComboBox().getSelectedIndex();
				selection.setChannel(channel);
			}

		} else {
			logger.error("Unexpected situation - nothing selected");
			throw new SanityCheckException();
		}

		bss.setSelection(selection);

	}

	/**
	 * Creates the {@link BoundedSignalSelection bounded selection} based
	 * on the {@link SignalSpace#getSelectionTimeSpace() selection} obtained
	 * from the {@link SignalSpace model}.
	 * In this bounded selections sets the {@link SignalSpaceConstraints
	 * limitations} from the current constraints.
	 * Calls {@link #fillPanelFromModel(BoundedSignalSelection) with this
	 * selection}.
	 * @param space the signal space
	 */
	public void fillPanelFromModel(SignalSpace space) {

		BoundedSignalSelection bss = new BoundedSignalSelection(space.getSelectionTimeSpace());

		bss.setMaxTime(currentConstraints.getTimeSignalLength());
		bss.setChannels(currentConstraints.getChannels());

		bss.setPageSize(currentConstraints.getPageSize());
		bss.setBlocksPerPage(currentConstraints.getBlocksPerPage());

		bss.setMaxPage(currentConstraints.getMaxPage());
		bss.setMaxBlock(currentConstraints.getMaxBlock());

		bss.setSamplingFrequency(currentConstraints.getSamplingFrequency());

		currentBss = bss;

		fillPanelFromModel(bss);

	}

	/**
	 * Fills the {@link BoundedSignalSelection bounded selection} using
	 * {@link #fillModelFromPanel(BoundedSignalSelection)} and sets the
	 * {@link SignalSelection selection} from it in the {@link SignalSpace
	 * model}.
	 * @param space the model (signal space)
	 */
	public void fillModelFromPanel(SignalSpace space) {

		fillModelFromPanel(currentBss);

		space.setSelectionTimeSpace(currentBss.getSelection());

	}

	/**
	 * Sets the {@link SignalSpaceConstraints parameters} of the signal.
	 * @param constraints the parameters of the signal
	 */
	public void setConstraints(SignalSpaceConstraints constraints) {
		currentConstraints = constraints;
	}

	/**
	 * Validates this panel.
	 * This panel is always valid.
	 * @param errors the object in which errors are stored
	 */
	public void validatePanel(Errors errors) {

		// there is no validation - the dialog elements enforce valid values themselves
	}
}

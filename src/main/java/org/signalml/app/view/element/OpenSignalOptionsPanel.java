/* OpenSignalOptionsPanel.java created 2007-09-17
 *
 */

package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.exception.SanityCheckException;
import org.springframework.context.support.MessageSourceAccessor;


/**
 * Panel with options for opening the document with a signal.
 * Contains 3 sub-panels:
 * <ul>
 * <li>the {@link #getMethodPanel() panel} which allows to select the
 * method which will be used to open a document with the signal,</li>
 * <li>the {@link #getOptionsPanel() panel} which allows to select the
 * parameters for this method. This panel has a {@link CardLayout} with two
 * cards:
 * <ul><li>the {@link #getSignalMLOptionsPanel() panel} for SignalML method,
 * </li>
 * <li>the {@link #getRawSignalOptionsPanel() panel} for RAW method.</li>
 * </ul></li>
 * <li>the {@link #getPagingSignalParamersPanel() panel} which allows to
 * select the size of the page and number of blocks in a page.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenSignalOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the panel with {@link #methodComboBox}
	 */
	private JPanel methodPanel;

	/**
	 * the layout for {@link #optionsPanel}
	 */
	private CardLayout cardLayout;
	
	/**
	 * the panel with parameters for the method that will be used to
	 * open the document with the signal
	 */
	private JPanel optionsPanel;
	/**
	 * the {@link SignalMLOptionsPanel panel} with options for a SignalML
	 * method
	 */
	private SignalMLOptionsPanel signalMLOptionsPanel;
	/**
	 * the {@link RawSignalOptionsPanel panel} with options for a raw signal
	 */
	private RawSignalOptionsPanel rawSignalOptionsPanel;

	/**
	 * the combo-box which allows to select the method using which the signal
	 * document will be opened (raw or signalML) 
	 */
	private JComboBox methodComboBox;

	/**
	 * the panel that allows to select the size of the page and the number
	 * of blocks in a page
	 */
	private PagingParametersPanel pagingParametersPanel;

	/**
	 * the {@link MessageSourceAccessor source} of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * Constructor. Sets the {@link MessageSourceAccessor message source} and
	 * initializes this panel.
	 * @param messageSource the source of messages (labels)
	 */
	public OpenSignalOptionsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * Initializes this panel and adds three sub-panels to it:
	 * <ul>
	 * <li>the {@link #getMethodPanel() panel} which allows to select the
	 * method which will be used to open a document with the signal,</li>
	 * <li>the {@link #getOptionsPanel() panel} which allows to select the
	 * parameters for this method,</li>
	 * <li>the {@link #getPagingSignalParamersPanel() panel} which allows to
	 * select the size of the page and number of blocks in a page.</li></ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		add(getMethodPanel(),BorderLayout.NORTH);
		add(getOptionsPanel(),BorderLayout.CENTER);
		add(getPagingSignalParamersPanel(),BorderLayout.SOUTH);

	}

	/**
	 * Returns the panel which allows to select the method which will be used
	 * to open a document with the signal.
	 * The panel contains the {@link #getMethodComboBox() combo-box} which
	 * allows this selection.
	 * If the panel doesn't exist it is created
	 * @return the panel which allows to select the method which will be used
	 * to open a document with the signal
	 */
	private JPanel getMethodPanel() {
		if (methodPanel == null) {
			methodPanel = new JPanel(new BorderLayout());
			CompoundBorder cb = new CompoundBorder(
			        new TitledBorder(messageSource.getMessage("openSignal.options.methodTitle")),
			        new EmptyBorder(3,3,3,3)
			);
			methodPanel.setBorder(cb);
			methodPanel.add(getMethodComboBox(),BorderLayout.CENTER);
		}

		return methodPanel;
	}

	/**
	 * Returns the layout for the {@link #getOptionsPanel() options panel}.
	 * If the layout doesn't exist it is created.
	 * @return the layout for the options panel
	 */
	private CardLayout getCardLayout() {
		if (cardLayout == null) {
			cardLayout = new CardLayout();
		}
		return cardLayout;
	}

	/**
	 * Returns the panel with parameters for the method that will be used to
	 * open the document with the signal.
	 * The panel has a {@link CardLayout} and two cards:
	 * <ul><li>{@link #signalMLOptionsPanel} - panel with options for a
	 * SignalML method,</li>
	 * <li>{@link #rawSignalOptionsPanel} - panel with options for a raw
	 * signal.</li></ul>
	 * If the panel doesn't exist it is created.
	 * @return the panel with parameters for the method that will be used to
	 * open the document with the signal
	 */
	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setLayout(getCardLayout());
			optionsPanel.add(getSignalMLOptionsPanel(), "signalml");
			optionsPanel.add(getRawSignalOptionsPanel(), "raw");
		}
		return optionsPanel;
	}

	/**
	 * Returns the combo-box allows to select the method which will be used
	 * to open a document with the signal (SignalML or RAW).
	 * If the combo-box doesn't exist it is created and a listener is added
	 * to it.
	 * The listener changes the active card in {@link #getOptionsPanel()
	 * options panel} when the selected method changes.
	 * @return the combo-box allows to select the method which will be used
	 * to open a document with the signal
	 */
	public JComboBox getMethodComboBox() {
		if (methodComboBox == null) {
			methodComboBox = new JComboBox();
			methodComboBox.addItem(messageSource.getMessage("openSignal.options.method.SIGNALML"));
			methodComboBox.addItem(messageSource.getMessage("openSignal.options.method.RAW"));

			methodComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {

						int index = methodComboBox.getSelectedIndex();
						switch (index) {

						case 0 :
							getCardLayout().show(getOptionsPanel(), "signalml");
							break;

						case 1 :
							getCardLayout().show(getOptionsPanel(), "raw");
							break;

						default :
							throw new SanityCheckException("Bad setting [" + index + "]");

						}

					}
				}

			});
		}
		return methodComboBox;
	}

	/**
	 * Returns the {@link SignalMLOptionsPanel panel} with parameters of the
	 * SignalML method.
	 * If the panel doesn't exist it is created.
	 * @return the panel with the parameters of the SignalML method
	 */
	public SignalMLOptionsPanel getSignalMLOptionsPanel() {
		if (signalMLOptionsPanel == null) {
			signalMLOptionsPanel = new SignalMLOptionsPanel(messageSource);
		}
		return signalMLOptionsPanel;
	}

	/**
	 * Returns the {@link RawSignalOptionsPanel panel} with parameters of the
	 * RAW method.
	 * If the panel doesn't exist it is created.
	 * @return the panel with the parameters of the RAW method
	 */
	public RawSignalOptionsPanel getRawSignalOptionsPanel() {
		if (rawSignalOptionsPanel == null) {
			rawSignalOptionsPanel = new RawSignalOptionsPanel(messageSource);
		}
		return rawSignalOptionsPanel;
	}

	/**
	 * Returns the panel that allows to select the size of the page and the
	 * number of blocks in a page.
	 * If the panel doesn't exist it is created.
	 * @return the panel that allows to select the size of the page and the
	 * number of blocks in a page
	 */
	public PagingParametersPanel getPagingSignalParamersPanel() {
		if (pagingParametersPanel == null) {
			pagingParametersPanel = new PagingParametersPanel(messageSource);
		}
		return pagingParametersPanel;
	}

}

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

/** OpenSignalOptionsPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenSignalOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel methodPanel;

	private CardLayout cardLayout;
	private JPanel optionsPanel;
	private SignalMLOptionsPanel signalMLOptionsPanel;
	private RawSignalOptionsPanel rawSignalOptionsPanel;

	private JComboBox methodComboBox;

	private PagingParametersPanel pagingParametersPanel;

	private MessageSourceAccessor messageSource;

	/**
	 * This is the default constructor
	 */
	public OpenSignalOptionsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 *
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		add(getMethodPanel(),BorderLayout.NORTH);
		add(getOptionsPanel(),BorderLayout.CENTER);
		add(getPagingSignalParamersPanel(),BorderLayout.SOUTH);

	}

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

	private CardLayout getCardLayout() {
		if (cardLayout == null) {
			cardLayout = new CardLayout();
		}
		return cardLayout;
	}

	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setLayout(getCardLayout());
			optionsPanel.add(getSignalMLOptionsPanel(), "signalml");
			optionsPanel.add(getRawSignalOptionsPanel(), "raw");
		}
		return optionsPanel;
	}

	public JComboBox getMethodComboBox() {
		if (methodComboBox == null) {
			methodComboBox = new JComboBox();
			methodComboBox.addItem(messageSource.getMessage("openSignal.options.methodSignalML"));
			methodComboBox.addItem(messageSource.getMessage("openSignal.options.methodRaw"));

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

	public SignalMLOptionsPanel getSignalMLOptionsPanel() {
		if (signalMLOptionsPanel == null) {
			signalMLOptionsPanel = new SignalMLOptionsPanel(messageSource);
		}
		return signalMLOptionsPanel;
	}

	public RawSignalOptionsPanel getRawSignalOptionsPanel() {
		if (rawSignalOptionsPanel == null) {
			rawSignalOptionsPanel = new RawSignalOptionsPanel(messageSource);
		}
		return rawSignalOptionsPanel;
	}

	public PagingParametersPanel getPagingSignalParamersPanel() {
		if (pagingParametersPanel == null) {
			pagingParametersPanel = new PagingParametersPanel(messageSource);
		}
		return pagingParametersPanel;
	}

}

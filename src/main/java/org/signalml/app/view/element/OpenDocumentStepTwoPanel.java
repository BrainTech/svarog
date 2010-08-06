/* OpenDocumentStepTwoPanel.java created 2007-09-17
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.util.IconUtils;
import org.springframework.context.support.MessageSourceAccessor;

/** OpenDocumentStepTwoPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentStepTwoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private OpenSignalOptionsPanel signalOptionsPanel = null;
	private OpenTagOptionsPanel tagOptionsPanel = null;
	private OpenBookOptionsPanel bookOptionsPanel = null;

	private CardLayout cardLayout = null;

	private JPanel configsPanel = null;

	private JLabel infoLabel = null;

	/**
	 * This is the default constructor
	 */
	public OpenDocumentStepTwoPanel(MessageSourceAccessor messageSource) {
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

		add(getInfoLabel(), BorderLayout.NORTH);
		add(getConfigsPanel(),BorderLayout.CENTER);

	}

	public void setExpectedType(ManagedDocumentType type, boolean autodetected) {
		if (type.equals(ManagedDocumentType.SIGNAL)) {
			if (autodetected) {
				getInfoLabel().setText(messageSource.getMessage("openDocument.signalAutodetected"));
			} else {
				getInfoLabel().setText(messageSource.getMessage("openDocument.signalChosen"));
			}
			getCardLayout().show(getConfigsPanel(), "signal");
		} else if (type.equals(ManagedDocumentType.BOOK)) {
			if (autodetected) {
				getInfoLabel().setText(messageSource.getMessage("openDocument.bookAutodetected"));
			} else {
				getInfoLabel().setText(messageSource.getMessage("openDocument.bookChosen"));
			}
			getCardLayout().show(getConfigsPanel(), "book");
		} else if (type.equals(ManagedDocumentType.TAG)) {
			if (autodetected) {
				getInfoLabel().setText(messageSource.getMessage("openDocument.tagAutodetected"));
			} else {
				getInfoLabel().setText(messageSource.getMessage("openDocument.tagChosen"));
			}
			getCardLayout().show(getConfigsPanel(), "tag");
		}
		if (autodetected) {
			getInfoLabel().setIcon(IconUtils.getWarningIcon());
		} else {
			getInfoLabel().setIcon(IconUtils.getInfoIcon());
		}
	}

	private JPanel getConfigsPanel() {
		if (configsPanel == null) {
			configsPanel = new JPanel();
			configsPanel.setLayout(getCardLayout());
			configsPanel.add(getSignalOptionsPanel(), "signal");
			configsPanel.add(getTagOptionsPanel(), "tag");
			configsPanel.add(getBookOptionsPanel(), "book");
		}
		return configsPanel;
	}

	private JLabel getInfoLabel() {
		if (infoLabel == null) {
			infoLabel = new JLabel();
			infoLabel.setBorder(new EmptyBorder(3,0,3,0));
			infoLabel.setHorizontalAlignment(JLabel.CENTER);
		}
		return infoLabel;
	}

	public CardLayout getCardLayout() {
		if (cardLayout == null) {
			cardLayout = new CardLayout();
		}
		return cardLayout;
	}

	public OpenSignalOptionsPanel getSignalOptionsPanel() {
		if (signalOptionsPanel == null) {
			signalOptionsPanel = new OpenSignalOptionsPanel(messageSource);
		}

		return signalOptionsPanel;
	}

	public OpenTagOptionsPanel getTagOptionsPanel() {
		if (tagOptionsPanel == null) {
			tagOptionsPanel = new OpenTagOptionsPanel(messageSource);
		}

		return tagOptionsPanel;
	}

	public OpenBookOptionsPanel getBookOptionsPanel() {
		if (bookOptionsPanel == null) {
			bookOptionsPanel = new OpenBookOptionsPanel(messageSource);
		}
		return bookOptionsPanel;
	}

}

/* OpenDocumentStepTwoPanel.java created 2007-09-17
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.signalml.app.document.BookDocument;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.OpenDocumentDialog;
import org.signalml.plugin.export.signal.Document;

/**
 * Panel for a second step of {@link OpenDocumentDialog}.
 * Allows to select options for opening different {@link ManagedDocumentType
 * types} of {@link Document}s.
 * Contains a sub-panel with {@link CardLayout} and three cards:
 * <ul>
 * <li>the {@link #getSignalOptionsPanel() panel} with options for opening
 * a {@link SignalDocument signal document},</li>
 * <li>the {@link #getTagOptionsPanel() panel} with options for opening
 * a {@link TagDocument tag document},</li>
 * <li>the {@link #getBookOptionsPanel() panel} with options for opening
 * a {@link BookDocument book document}.</li></ul> 
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class OpenDocumentStepTwoPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link OpenSignalOptionsPanel panel} with options for opening
	 * a {@link SignalDocument signal document}
	 */
	private OpenSignalOptionsPanel signalOptionsPanel = null;
	
	/**
	 * the {@link OpenTagOptionsPanel panel} with options for opening
	 * a {@link TagDocument tag document}
	 */
	private OpenTagOptionsPanel tagOptionsPanel = null;
	
	/**
	 * the {@link OpenBookOptionsPanel panel} with options for opening
	 * a {@link BookDocument book document}
	 */
	private OpenBookOptionsPanel bookOptionsPanel = null;

	/**
	 * the layout for {@link #configsPanel}
	 */
	private CardLayout cardLayout = null;

	/**
	 * the panel with {@link CardLayout}, which contains three cards:
	 * <ul>
	 * <li>the {@link #signalOptionsPanel panel} with options for opening
	 * a {@link SignalDocument signal document}</li>
	 * <li>the {@link #tagOptionsPanel panel} with options for opening
	 * a {@link TagDocument tag document}</li>
	 * <li>the {@link #bookOptionsPanel panel} with options for opening
	 * a {@link BookDocument book document}</li></ul>
	 */
	private JPanel configsPanel = null;

	/**
	 * the label with the information about the type of the document
	 * and if this type was selected by user or autodetected
	 */
	private JLabel infoLabel = null;

	/**
	 * Constructor. Sets the message source and initializes this panel.
	 * @param messageSource the source of messages (labels)
	 */
	public  OpenDocumentStepTwoPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with {@link BorderLayout} and adds two
	 * elements to it:
	 * <ul><li>the {@link #getInfoLabel() label} with the type of the document,
	 * </li>
	 * <li>the {@link #getConfigsPanel() panel} with parameters for opening
	 * a document.</li></ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		add(getInfoLabel(), BorderLayout.NORTH);
		add(getConfigsPanel(),BorderLayout.CENTER);

	}

	/**
	 * Sets the icon and the text for the {@link #getInfoLabel() info label}:
	 * <ul><li>the icon informs if the type of a {@link Document document} was
	 * autodetected or chosen,</li>
	 * <li>the text informs about the {@link ManagedDocumentType type} of
	 * a document and if this type was autodetected or chosen.</li></ul>
	 * @param type the type of a document
	 * @param autodetected {@code true} if if the type of a document was
	 * autodetected, {@code false} if it was choosen by user
	 */
	public void setExpectedType(ManagedDocumentType type, boolean autodetected) {
		if (type.equals(ManagedDocumentType.SIGNAL)) {
			if (autodetected) {
				getInfoLabel().setText(_("Signal document autodetected"));
			} else {
				getInfoLabel().setText(_("Signal document chosen"));
			}
			getCardLayout().show(getConfigsPanel(), "signal");
		} else if (type.equals(ManagedDocumentType.BOOK)) {
			if (autodetected) {
				getInfoLabel().setText(_("Book document autodetected"));
			} else {
				getInfoLabel().setText(_("Book document chosen"));
			}
			getCardLayout().show(getConfigsPanel(), "book");
		} else if (type.equals(ManagedDocumentType.TAG)) {
			if (autodetected) {
				getInfoLabel().setText(_("Tag document autodetected"));
			} else {
				getInfoLabel().setText(_("Tag document chosen"));
			}
			getCardLayout().show(getConfigsPanel(), "tag");
		}
		if (autodetected) {
			getInfoLabel().setIcon(IconUtils.getWarningIcon());
		} else {
			getInfoLabel().setIcon(IconUtils.getInfoIcon());
		}
	}

	/**
	 * Returns the panel with parameters for opening a document.
	 * If the panel doesn't exist it is created with {@link CardLayout} and
	 * three cards:
	 * <ul>
	 * <li>the {@link #getSignalOptionsPanel() panel} with options for opening
	 * a {@link SignalDocument signal document}</li>
	 * <li>the {@link #getTagOptionsPanel() panel} with options for opening
	 * a {@link TagDocument tag document}</li>
	 * <li>the {@link #getBookOptionsPanel() panel} with options for opening
	 * a {@link BookDocument book document}</li></ul> 
	 * @return the panel with parameters for opening a document
	 */
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

	/**
	 * Returns the label with the information about the type of the document
	 * and if this type was selected by user or autodetected.
	 * If the label doesn't exist it is created.
	 * @return the label with the information about the type of the document
	 * and if this type was selected by user or autodetected
	 */
	private JLabel getInfoLabel() {
		if (infoLabel == null) {
			infoLabel = new JLabel();
			infoLabel.setBorder(new EmptyBorder(3,0,3,0));
			infoLabel.setHorizontalAlignment(JLabel.CENTER);
		}
		return infoLabel;
	}

	/**
	 * Returns the layout for the panel with parameters for opening a document.
	 * If the layout doesn't exist it is created.
	 * @return the layout for the panel with parameters for opening a document
	 */
	public CardLayout getCardLayout() {
		if (cardLayout == null) {
			cardLayout = new CardLayout();
		}
		return cardLayout;
	}

	/**
	 * Returns the {@link OpenSignalOptionsPanel panel} with options for
	 * opening a {@link SignalDocument signal document}.
	 * If the panel doesn't exist it is created.
	 * @return the panel with options for opening signal document
	 */
	public OpenSignalOptionsPanel getSignalOptionsPanel() {
		if (signalOptionsPanel == null) {
			signalOptionsPanel = new OpenSignalOptionsPanel();
		}

		return signalOptionsPanel;
	}

	/**
	 * Returns the {@link OpenTagOptionsPanel panel} with options for opening
	 * a {@link TagDocument tag document}.
	 * If the panel doesn't exist it is created.
	 * @return the panel with options for opening a tag document
	 */
	public OpenTagOptionsPanel getTagOptionsPanel() {
		if (tagOptionsPanel == null) {
			tagOptionsPanel = new OpenTagOptionsPanel();
		}

		return tagOptionsPanel;
	}

	/**
	 * Returns the {@link OpenBookOptionsPanel panel} with options for opening
	 * a {@link BookDocument book document}.
	 * If the panel doesn't exist it is created.
	 * @return the panel with options for opening book document
	 */
	public OpenBookOptionsPanel getBookOptionsPanel() {
		if (bookOptionsPanel == null) {
			bookOptionsPanel = new OpenBookOptionsPanel();
		}
		return bookOptionsPanel;
	}

}

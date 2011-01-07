/* CodecManagerConfigPanel.java created 2007-09-18
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.signalml.app.action.RegisterCodecAction;
import org.signalml.app.action.RemoveCodecAction;
import org.signalml.codec.SignalMLCodec;
import org.springframework.context.support.MessageSourceAccessor;


/**
 * Panel which allows the management of codecs ({@link RegisterCodecAction
 * registration}, {@link RemoveCodecAction removal}).
 * Contains two panels:
 * <ul>
 * <li>the {@link #getCodecListScrollPane() scroll pane} with the
 * {@link #getCodecList() list} of installed {@link SignalMLCodec codecs},
 * </li><li>the {@link #getButtonPanel() panel} with
 * codec {@link #getRegisterCodecButton() registration} and
 * {@link #getRemoveCodecButton() removal} buttons.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CodecManagerConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link MessageSourceAccessor source} of messages (labels)
	 */
	private MessageSourceAccessor messageSource;
	
	/**
	 * the list of installed {@link SignalMLCodec codecs}
	 */
	private JList codecList;
	
	/**
	 * the scroll pane with the {@link #codecList list} of installed
	 * {@link SignalMLCodec codecs}
	 */
	private JScrollPane codeclListScrollPane;
	
	/**
	 * the button which activates the {@link RegisterCodecAction registration}
	 * of the {@link SignalMLCodec codec}
	 */
	private JButton registerCodecButton;
	
	/**
	 * the button which activates the {@link RemoveCodecAction removal} of a
	 * {@link SignalMLCodec codec}
	 */
	private JButton removeCodecButton;
	
	/**
	 * the panel with {@link #registerCodecButton} and
	 * {@link #removeCodecButton}
	 */
	private JPanel buttonPanel;


	/**
	 * Constructor. Sets the {@link MessageSourceAccessor message source} and
	 * initializes this panel.
	 * @param messageSource the source of messages (labels)
	 */
	public CodecManagerConfigPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * Initializes this dialog with the {@link BorderLayout} and two
	 * panels:
	 * <ul>
	 * <li>the {@link #getCodecListScrollPane() scroll pane} with the
	 * {@link #getCodecList() list} of installed {@link SignalMLCodec codecs},
	 * </li><li>the {@link #getButtonPanel() panel} with
	 * codec {@link #getRegisterCodecButton() registration} and
	 * {@link #getRemoveCodecButton() removal} buttons.</li></ul>
	 */
	private void initialize() {

		setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BorderLayout());

		add(getCodecListScrollPane(),BorderLayout.CENTER);
		add(getButtonPanel(),BorderLayout.SOUTH);

	}

	public JList getCodecList() {
		if (codecList == null) {
			// model must be filled in by parent
			codecList = new JList(new Object[0]);
			codecList.setBorder(new LineBorder(Color.LIGHT_GRAY));
			codecList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			codecList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {

					JList list = (JList) e.getSource();
					getRemoveCodecButton().getAction().setEnabled(list.getSelectedIndex() >= 0);

				}

			});

		}
		return codecList;
	}

	/**
	 * Returns the scroll pane with the {@link #getCodecList() list} of
	 * installed {@link SignalMLCodec codecs}.
	 * If the pane doesn't exist it is created.
	 * @return the scroll pane with the list of installed codecs
	 */
	public JScrollPane getCodecListScrollPane() {
		if (codeclListScrollPane == null) {
			codeclListScrollPane = new JScrollPane(getCodecList(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			CompoundBorder cb = new CompoundBorder(
			        new TitledBorder(messageSource.getMessage("preferences.codecs.installedCodecs")),
			        new EmptyBorder(3,3,3,3)
			);
			codeclListScrollPane.setBorder(cb);

		}
		return codeclListScrollPane;
	}

	/**
	 * Returns the button which activates the {@link RegisterCodecAction
	 * registration} of the {@link SignalMLCodec codec}.
	 * If the button doesn't exist it is created.
	 * <p>NOTE: the action for this button must be filled in by parent.
	 * @return the button which activates the registration of the codec
	 */
	public JButton getRegisterCodecButton() {
		if (registerCodecButton == null) {
			// action must be filled in by parent
			registerCodecButton = new JButton((Action) null);
		}
		return registerCodecButton;
	}

	/**
	 * Returns the button which activates the {@link RemoveCodecAction removal} of a
	 * {@link SignalMLCodec codec}.
	 * If the button doesn't exist it is created.
	 * <p>NOTE: the action for this button must be filled in by parent.
	 * @return the button which activates the removal of the codec
	 */
	public JButton getRemoveCodecButton() {
		if (removeCodecButton == null) {
			// action must be filled in by parent
			removeCodecButton = new JButton((Action) null);
		}
		return removeCodecButton;
	}

	/**
	 * Returns the {@link #getButtonPanel() panel} with {@link SignalMLCodec
	 * codec} {@link #getRegisterCodecButton() registration} and
	 * {@link #getRemoveCodecButton() removal} button.
	 * If the panel doesn't exist it is created.
	 * @return the panel with codec registration and removal button
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			buttonPanel.setBorder(new EmptyBorder(3,0,0,0));
			buttonPanel.add(Box.createHorizontalGlue());
			buttonPanel.add(getRemoveCodecButton());
			buttonPanel.add(Box.createHorizontalStrut(5));
			buttonPanel.add(getRegisterCodecButton());
		}
		return buttonPanel;
	}

}

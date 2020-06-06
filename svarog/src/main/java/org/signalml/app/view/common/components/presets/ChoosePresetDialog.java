package org.signalml.app.view.common.components.presets;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetComboBoxModel;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.AnyChangeDocumentAdapter;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.util.Util;

/**
 * Dialog that allows to select the {@link #getPresetComboBox preset}
 * and specify the name for it.
 */
public class ChoosePresetDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private PresetManager presetManager;
	/**
	 * the model for a combo box that allows to select the preset
	 */
	private PresetComboBoxModel presetComboBoxModel;

	/**
	 * the combo box that allows to select the preset
	 */
	protected JComboBox presetComboBox;

	/**
	 * the text field to specify the name for the preset
	 */
	private JTextField nameTextField;

	protected boolean editable = true;

	/**
	 * Constructor. Sets the message source from enclosing class and
	 * uses the enclosing class as the parent window to this dialog.
	 */
	protected ChoosePresetDialog(Window parentWindow, PresetManager presetManager) {
		super(parentWindow, true);
		this.presetManager = presetManager;
	}

	/**
	 * Returns  if the text field with the name of the preset should be
	 * editable.
	 * @return {@code true} if the text field with the name of the
	 * preset should be editable, {@code false} otherwise
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Sets if the text field with the name of the preset should be
	 * editable.
	 * @param editable {@code true} if the text field with the name of the
	 * preset should be editable, {@code false} otherwise
	 */
	public void setEditable(boolean editable) {
		if (this.editable != editable) {
			this.editable = editable;
			getNameTextField().setEditable(editable);
		}
	}

	@Override
	public void fillDialogFromModel(Object model) {
		// do nothing
	}

	@Override
	public void fillModelFromDialog(Object model) {
		// do nothing
	}

	@Override
	protected void initialize() {
		setTitle(_("Select preset"));
		super.initialize();
	}

	/**
	 * Adds the panel with 3 elements:
	 * <ul>
	 * <li>icon with a question mark,</li>
	 * <li>{@link #getPresetComboBox() combo box} to select the preset,</li>
	 * <li>{@link #getNameTextField() text field} with the name of the
	 * preset (may be editable or not).</li>
	 * </ul>
	 */
	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());
		interfacePanel.setBorder(new CompoundBorder(
									 new TitledBorder(_("Select preset name")),
									 new EmptyBorder(3,3,3,3)
								 ));

		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(new EmptyBorder(0,8,0,0));
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

		inputPanel.add(getPresetComboBox());
		inputPanel.add(Box.createVerticalStrut(10));
		inputPanel.add(getNameTextField());

		JLabel iconLabel = new JLabel(IconUtils.getQuestionIcon());
		iconLabel.setVerticalAlignment(JLabel.TOP);
		interfacePanel.add(iconLabel, BorderLayout.WEST);
		interfacePanel.add(inputPanel, BorderLayout.CENTER);

		return interfacePanel;

	}

	/**
	 * Returns the {@link PresetComboBoxModel model} for a combo box
	 * to select presets.
	 * If the model doesn't exist it is created.
	 * @return the model
	 */
	public PresetComboBoxModel getPresetComboBoxModel() {
		if (presetComboBoxModel == null) {
			presetComboBoxModel = new PresetComboBoxModel(_("<< select to choose preset >>"), presetManager);
		}
		return presetComboBoxModel;
	}

	/**
	 * If the preset combo box already exists it is simply returned.
	 * If it doesn't, it is created.
	 * Created combo box contains the listener, which sets the
	 * {@link #getNameTextField() name field} depending on the selected
	 * preset.
	 * @return the preset combo box
	 */
	public JComboBox getPresetComboBox() {

		if (presetComboBox == null) {

			;
			presetComboBox = new JComboBox(getPresetComboBoxModel());
			presetComboBox.setSelectedIndex(0);
			presetComboBox.setPreferredSize(new Dimension(200,25));

			presetComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					int index = presetComboBox.getSelectedIndex();
					if (index <= 0) {
						return;
					}

					Preset p = presetManager.getPresetAt(index-1);
					if (p != null) {
						JTextField nameTextField = getNameTextField();
						nameTextField.setText(p.getName());
						if (editable) {
							nameTextField.selectAll();
							nameTextField.requestFocusInWindow();
						}
					}

					presetComboBox.setSelectedIndex(0);
					presetComboBox.repaint();

				}

			});

		}

		return presetComboBox;

	}

	/**
	 * Returns the text field with the name of the preset.
	 * If the field doesn't exist it is created.
	 * If this name contains at least one character the OK button is
	 * activated.
	 * @return the text field with the name of the preset.
	 */
	public JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setPreferredSize(new Dimension(200,25));

			nameTextField.getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {
				@Override
				public void anyUpdate(DocumentEvent e) {
					getOkAction().setEnabled(e.getDocument().getLength() > 0);
				}
			});

		}
		return nameTextField;
	}

	/**
	 * Shows this dialog and returns the entered name.
	 * @param initialName the name that (if it is not empty) will be
	 * set as the value of {@link #getNameTextField() name text field}
	 * @param editable {@code true} if the name text field should be
	 * editable, {@code false} otherwise
	 * @return the specified name or null if there is no name (or the name
	 * is empty)
	 */
	public String getName(String initialName, boolean editable) {

		initializeNow();

		setEditable(editable);

		JTextField nameTextField = getNameTextField();

		if (initialName != null && !initialName.isEmpty()) {
			nameTextField.setText(initialName);
			getOkAction().setEnabled(true);
		} else {
			nameTextField.setText("");
			getOkAction().setEnabled(false);
		}

		if (editable) {
			nameTextField.selectAll();
			nameTextField.requestFocusInWindow();
		}

		boolean ok = showDialog(null, true);
		if (!ok) {
			return null;
		}

		String name = nameTextField.getText();
		name = Util.trimString(name);
		if (name == null || name.isEmpty()) {
			return null;
		}

		return name;

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

}

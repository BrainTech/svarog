/* NewTagPanel.java created 2007-10-14
 *
 */
package org.signalml.app.view.tag;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import org.signalml.app.config.preset.managers.StyledTagSetPresetManager;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.document.opensignal.elements.TagPresetComboBoxModel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.filechooser.EmbeddedFileChooser;
import org.signalml.plugin.export.signal.TagStyle;

/**
 * Panel which allows to select which {@link TagStyle styles} should be used in
 * the new {@link TagDocument tag document}.
 * Contains 3 buttons:
 * <ul>
 * <li>the {@link #getEmptyRadio() radio button} that indicates that the
 * tag document should contain no style,</li>
 * <li>the {@link #getFromFileRadio() radio button} that indicates that the
 * tag document should contain the same styles as in the selected file</li>
 * </ul>
 * and one {@link #getFileChooser() file chooser} which is shown when the
 * third button is selected.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewTagPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the radio button that indicates that the {@link TagDocument tag document}
	 * should contain no {@link TagStyle style}
	 */
	private JRadioButton emptyRadio = null;
	
	/**
	 * radio button to select default tag styles
	 */
	private JRadioButton defaultPresetsRadio = null;

	/**
	 * the radio button that indicates that the {@link TagDocument tag document}
	 * should contain the same {@link TagStyle styles} as in the selected file
	 */
	private JRadioButton fromFileRadio = null;
	/**
	 * the radio button that indicates that the {@link TagDocument tag document}
	 * should contain the same {@link TagStyle styles} as in the selected
	 * tag styles preset.
	 */
	private JRadioButton presetRadio;

	/**
	 * the group of radio buttons which allows to select the {@link TagStyle
	 * styles} that should be located in the created {@link TagDocument tag
	 * document} ({@link #emptyRadio}, {@link #fromFileRadio})
	 */
	private ButtonGroup radioGroup;
	/**
	 * the {@link EmbeddedFileChooser chooser} of files which is shown when
	 * {@link #fromFileRadio} is selected
	 */
	private EmbeddedFileChooser fileChooser = null;
	/**
	 * ComboBox for selecting the tag style preset to be used in the tag document.
	 */
	private JComboBox presetComboBox;
	/**
	 * ComboBox for selecting the tag style preset to be used in the tag document, using the default (non user-made) presets.
	 */
	private JComboBox defaultPresetsComboBox;
	
	/**
	 * ComboBox for selecting the tag style preset to be used in
	 * the tag document, from default tag presets
	 */
	private JComboBox defaultPresetComboBox;
	/**
	 * {@link PresetManager} that handles the tag styles presets.
	 */
	private StyledTagSetPresetManager styledTagSetPresetManager;

	/**
	 * Constructor. Initializes the panel.
	 * @param styledTagSetPresetManager the {@link PresetManager} which handles
	 * the tag styles presets.
	 */
	public NewTagPanel(StyledTagSetPresetManager styledTagSetPresetManager) {
		super();
		this.styledTagSetPresetManager = styledTagSetPresetManager;
		initialize();
	}

	/**
	 * Initializes this panel with the box layout and 3 buttons:
	 * <ul>
	 * <li>the {@link #getEmptyRadio() radio button} that indicates that the
	 * {@link TagDocument tag document} should contain no {@link TagStyle
	 * style},</li>
	 * <li>the {@link #getFromFileRadio() radio button} that indicates that the
	 * tag document should contain the same styles as in the selected file</li>
	 * </ul>
	 * and one {@link #getFileChooser() file chooser} which is shown when the
	 * third button is selected.
	 */
	private void initialize() {

		setBorder(BorderFactory.createTitledBorder(_("Choose new tag type")));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		radioGroup = new ButtonGroup();

		add(getEmptyRadio());
		add(getDefaultPresetsRadioPanel());
		add(getPresetRadioPanel());
		add(getFromFileRadio());

		//getEmptyRadio().setSelected(true);
		getPresetComboBox().setEnabled(false);
		getDefaultPresetComboBox().setEnabled(false);
		getDefaultPresetComboBox().setSelectedIndex(0);

		getFileChooser().setVisible(false);
		add(getFileChooser());

	}

	/**
	 * Returns the radio button that indicates that the {@link TagDocument tag
	 * document} should contain no {@link TagStyle style}.
	 * If the button doesn't exist it is created and added to the radio group.
	 * @return the radio button that indicates that the tag document should
	 * contain no style
	 */
	public JRadioButton getEmptyRadio() {
		if (emptyRadio == null) {
			emptyRadio = new JRadioButton();
			emptyRadio.setText(_("An empty tag with no styles"));
			emptyRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(emptyRadio);
		}
		return emptyRadio;
	}

	/**
	 * Returns the panel containing the radio button that indicated that
	 * the tag document should use the tag styles from the defaults selection
	 * and a ComboBox for preset selection.
	 * @return a radio button plus ComboBox for default preset selection
	 */
	public JPanel getDefaultPresetsRadioPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(new EmptyBorder(0, 0, 0, 6));
		panel.add(getDefaultPresetsRadio(), BorderLayout.WEST);
		panel.add(getDefaultPresetComboBox(), BorderLayout.CENTER);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return panel;
	}
	
	public JRadioButton getDefaultPresetsRadio() {
		if (defaultPresetsRadio == null) {
			defaultPresetsRadio = new JRadioButton();
			defaultPresetsRadio.setText(_("Default presets"));
			defaultPresetsRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(defaultPresetsRadio);
			
			
			defaultPresetsRadio.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					getDefaultPresetComboBox().setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				}

			});
		}
		return defaultPresetsRadio;
	}
	
	/**
	 * Returns the panel containing the radio button that indicated that
	 * the tag document should use the tag styles from the selected preset
	 * and a ComboBox for preset selection.
	 * @return a radio button plus ComboBox for preset selection
	 */
	public JPanel getPresetRadioPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(new EmptyBorder(0, 0, 0, 6));
		panel.add(getPresetRadio(), BorderLayout.WEST);
		panel.add(getPresetComboBox(), BorderLayout.CENTER);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		return panel;
	}

	/**
	 * Returns the radio button that indicates that the tag document
	 * should use styles from the selected tag styles presets.
	 * @return the "use tag styles preset" radio button
	 */
	public JRadioButton getPresetRadio() {
		if (presetRadio == null) {
			presetRadio = new JRadioButton();
			presetRadio.setText(_("User created presets"));
			presetRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(presetRadio);

			presetRadio.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					getPresetComboBox().setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				}

			});
		}
		return presetRadio;
	}

	/**
	 * Returns the radio button that indicates that the {@link TagDocument tag
	 * document} should contain the same {@link TagStyle styles} as in the
	 * selected file.
	 * If the button doesn't exist it is created and added to the radio group.
	 * Also adds the listener to it, which shows the {@link #getFileChooser()
	 * file chooser} if this button is selected and hides the file chooser when
	 * the button is not selected.
	 * @return the radio button that indicates that the tag document
	 * should contain the same styles as in the selected file
	 */
	public JRadioButton getFromFileRadio() {
		if (fromFileRadio == null) {
			fromFileRadio = new JRadioButton();
			fromFileRadio.setText(_("Use styles from selected file"));
			fromFileRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
			radioGroup.add(fromFileRadio);
			fromFileRadio.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					getFileChooser().setVisible(e.getStateChange() == ItemEvent.SELECTED);

					NewTagPanel.this.revalidate();
					Dimension d = NewTagPanel.this.getTopLevelAncestor().getPreferredSize();
					NewTagPanel.this.getTopLevelAncestor().setSize(d);
					NewTagPanel.this.repaint();
				}

			});
		}
		return fromFileRadio;
	}

	/**
	 * Returns the {@link EmbeddedFileChooser chooser} of files which is shown
	 * when {@link #getFromFileRadio() from file radio button} is selected.
	 * If the file chooser doens't exist it is created without multiselection
	 * and with the filter for {@link ManagedDocumentType#TAG tag files}.
	 * @return the chooser of files with {@link TagDocument tag documents}
	 */
	public EmbeddedFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new EmbeddedFileChooser();
			fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
			fileChooser.setFileHidingEnabled(false);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setAcceptAllFileFilterUsed(true);
			fileChooser.resetChoosableFileFilters();
			fileChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.resetChoosableFileFilters();
			FileFilter[] filters = ManagedDocumentType.TAG.getFileFilters();
			for (FileFilter f : filters) {
				fileChooser.addChoosableFileFilter(f);
			}
			fileChooser.setPreferredSize(new Dimension(500,350));

		}
		return fileChooser;
	}

	/**
	 * Returns a ComboBox containing available tag style presets.
	 * @return ComboBox for tag style preset selection
	 */
	public JComboBox getPresetComboBox() {
		if (presetComboBox == null) {
			TagPresetComboBoxModel model = new TagPresetComboBoxModel(styledTagSetPresetManager);
			model.setShowEmptyOption(false);
			presetComboBox = new JComboBox(model);
			presetComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			presetComboBox.setPreferredSize(new Dimension(200, 10));
		}
		return presetComboBox;
	}
	
	/**
	 * Returns a ComboBox containing available default tag style presets.
	 * @return ComboBox for tag style preset selection
	 */
	public JComboBox getDefaultPresetComboBox() {
		if (defaultPresetsComboBox == null) {
			defaultPresetsComboBox = new JComboBox(TagDocument.DEFAULT_TAG_DOCUMENTS_STYLE_NAMES);
			defaultPresetsComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			defaultPresetsComboBox.setPreferredSize(new Dimension(200, 10));
		}
		return defaultPresetsComboBox;
	}

}

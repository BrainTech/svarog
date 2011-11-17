/* TagStylePropertiesPanel.java created 2007-11-10
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;

import org.signalml.app.view.dialog.KeyStrokeCaptureDialog;
import org.signalml.app.view.tag.TagRenderer;
import org.signalml.app.view.tag.styles.attributes.TagAttributesDefinitionsEditPanel;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.util.Util;

import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 * Panel with the properties of the tag style.
 * Contains the following sub-panels:
 * <ul>
 * <li>on the left:<ul>
 * <li>the {@link #getOutlineTopPanel() panel} which allows to select the
 * parameters of the outline of a {@link Tag tag} of the currently edited
 * {@link TagStyle style},</li>
 * <li>the {@link #getOutlineColorPanel() panel} which allows to select the
 * color of the outline,</li>
 * <li>the {@link #getFillColorPanel() panel} which allows to select the
 * color of the fill,</li></ul></li>
 * <li>on the right:
 * <ul>
 * <li>the {@link #getPropertiesPanel() panel} with the properties of the
 * currently edited {@link TagStyle style},</li>
 * <li>the {@link #getPreviewPanel() panel} with the preview of a tag
 * of the currently edited style.</li></ul></li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStylePropertiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final String CHANGED_PROPERTY = "changed";

	/**
	 * the chooser of the color of the outline of a {@link Tag} of the selected
	 * {@link TagStyle style}
	 */
	private JColorChooser outlineColorChooser;
	/**
	 * the chooser of the color of the fill of a {@link Tag} of the selected
	 * {@link TagStyle style}
	 */
	private JColorChooser fillColorChooser;

	/**
	 * the spinner which allows to choose the width of the outline of a {@link
	 * Tag} of the selected {@link TagStyle style}
	 */
	private JSpinner widthSpinner;
	/**
	 * the combo-box which allows to choose the outline dashing style of a
	 * {@link Tag} of the selected {@link TagStyle style}
	 */
	private JComboBox dashComboBox;

	/**
	 * the text field with the name of the {@link TagStyle style}
	 */
	private JTextField nameTextField;
	/**
	 * the text pane with the description of the {@link TagStyle style}
	 */
	private JTextPane descriptionTextPane;
	/**
	 * the scroll pane in which {@link #descriptionTextPane} is located
	 */
	private JScrollPane descriptionScrollPane;
	/**
	 * the text field in which the captured key is displayed
	 * @see #captureKeyButton
	 */
	private JTextField keyTextField;
	/**
	 * the button which displays the {@link KeyStrokeCaptureDialog dialog}
	 * which captures the key stroke;
	 * this key stroke is used to select the {@link TagStyle style}
	 */
	private JButton captureKeyButton;
	/**
	 * the check-box which tells if the {@link TagStyle style} should be a
	 * marker style of the regular one
	 */
	private JCheckBox markerCheckBox;
	/**
	 * the check-box which tells if the {@link TagStyle style} should be
	 * visible (whether it should be rendered in the signal view).
	 */
	private JCheckBox visibilityCheckBox;

	/**
	 * the panel which allows to select the outline of a {@link
	 * Tag tag} of the selected {@link TagStyle style}
	 */
	private JPanel outlineTopPanel;
	/**
	 * the panel which allows to select the properties of the {@link TagStyle
	 * style}
	 */
	private JPanel propertiesPanel;

	/**
	 * the renderer of {@link Tag tags}
	 */
	private TagRenderer tagRenderer;

	/**
	 * the currently edited {@link TagStyle style}
	 */
	private TagStyle currentStyle;
	/**
	 * the boolean which tells if the current {@link TagStyle style} was
	 * changed
	 */
	private boolean changed = false;

	/**
	 * the model for {@link #dashComboBox}
	 */
	private DefaultComboBoxModel dashComboBoxModel;

	/**
	 * the layout for {@link #fillColorPanel}
	 */
	private CardLayout fillColorLayout;
	/**
	 * the panel which allows to select the color of the fill of a {@link Tag}
	 * of the currently edited {@link TagStyle style}
	 */
	private JPanel fillColorPanel;

	/**
	 * the layout for {@link #outlineColorPanel}
	 */
	private CardLayout outlineColorLayout;
	/**
	 * the panel which allows to select the color of the outline of a {@link Tag}
	 * of the currently edited {@link TagStyle style}
	 */
	private JPanel outlineColorPanel;
	/**
	 * The panel for viewing and editing tag style attributes.
	 */
	private TagAttributesDefinitionsEditPanel tagAttributesDefinitionsEditPanel;

	/**
	 * the layout for {@link #previewPanel}
	 */
	private CardLayout previewLayout;
	/**
	 * the panel with the preview of a {@link Tag} of the currently edited
	 * {@link TagStyle style}
	 */
	private JPanel previewPanel;

	/**
	 * Constructor. Sets the source of messages (labels) and initializes this
	 * panel.
	 */
	public TagStylePropertiesPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with BorderLayout and following panels:
	 * <ul>
	 * <li>on the left:<ul>
	 * <li>the {@link #getOutlineTopPanel() panel} which allows to select the
	 * parameters of the outline of a {@link Tag tag} of the currently edited
	 * {@link TagStyle style},</li>
	 * <li>the {@link #getOutlineColorPanel() panel} which allows to select the
	 * color of the outline,</li>
	 * <li>the {@link #getFillColorPanel() panel} which allows to select the
	 * color of the fill,</li></ul></li>
	 * <li>on the right:
	 * <ul>
	 * <li>the {@link #getPropertiesPanel() panel} with the properties of the
	 * currently edited {@link TagStyle style},</li>
	 * <li>the {@link #getPreviewPanel() panel} with the preview of a tag
	 * of the currently edited style.</li></ul></li></ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0,5,0,0));

		JPanel graphicsPanel = new JPanel();

		graphicsPanel.setLayout(new BoxLayout(graphicsPanel, BoxLayout.Y_AXIS));

		JPanel outlinePanel = new JPanel(new BorderLayout());
		outlinePanel.setBorder(new TitledBorder(_("Outline style")));

		outlinePanel.add(getOutlineTopPanel(), BorderLayout.NORTH);
		outlinePanel.add(getOutlineColorPanel(), BorderLayout.CENTER);

		JPanel fillPanel = new JPanel(new BorderLayout());
		fillPanel.setBorder(new TitledBorder(_("Fill style")));

		fillPanel.add(getFillColorPanel(), BorderLayout.CENTER);

		graphicsPanel.add(outlinePanel);
		graphicsPanel.add(fillPanel);

		JPanel sidePanel = new JPanel(new BorderLayout());

		sidePanel.add(getPropertiesPanel(), BorderLayout.NORTH);
		sidePanel.add(getTagAttributesDefinitionsEditPanel(), BorderLayout.CENTER);
		sidePanel.add(getPreviewPanel(), BorderLayout.SOUTH);

		add(graphicsPanel, BorderLayout.CENTER);
		add(sidePanel, BorderLayout.EAST);

	}

	/**
	 * Returns the layout for the {@link #getFillColorPanel() fillColorPanel}. 
	 * If the layout doesn't exist it is created.
	 * @return the layout for the fillColorPanel
	 */
	public CardLayout getFillColorLayout() {
		if (fillColorLayout == null) {
			fillColorLayout = new CardLayout();
		}
		return fillColorLayout;
	}

	/**
	 * Returns the panel which allows to select the color of the fill of
	 * a {@link Tag tag} of the currently edited {@link TagStyle style}.
	 * If the panel doesn't exist it is created with:
	 * <ul>
	 * <li>the {@link #getFillColorChooser() chooser} of the color of the
	 * fill, which is shown if a style is selected,</li>
	 * </ul>the label which tells that the style must be created or chosen,
	 * which is shown if no style is selected</li></ul>
	 * @return the panel which allows to select the color of the fill of
	 * a tag of the currently edited style.
	 */
	public JPanel getFillColorPanel() {
		if (fillColorPanel == null) {
			fillColorPanel = new JPanel(getFillColorLayout());
			fillColorPanel.add(getFillColorChooser(), "on");
			fillColorPanel.add(createNoStyleLabel(), "off");
		}
		return fillColorPanel;
	}

	/**
	 * Returns the layout for the {@link #getOutlineColorPanel()
	 * outlineColorPanel}. 
	 * If the layout doesn't exist it is created.
	 * @return the layout for the outlineColorPanel
	 */
	public CardLayout getOutlineColorLayout() {
		if (outlineColorLayout == null) {
			outlineColorLayout = new CardLayout();
		}
		return outlineColorLayout;
	}

	/**
	 * Returns the panel which allows to select the color of the outline of
	 * a {@link Tag tag} of the currently edited {@link TagStyle style}.
	 * If the panel doesn't exist it is created with:
	 * <ul>
	 * <li>the {@link #getOutlineColorChooser() chooser} of the color of the
	 * outline, which is shown if a style is selected,</li>
	 * </ul>the label which tells that the style must be created or chosen,
	 * which is shown if no style is selected</li></ul>
	 * @return the panel which allows to select the color of the outline of
	 * a tag of the currently edited style.
	 */
	public JPanel getOutlineColorPanel() {
		if (outlineColorPanel == null) {
			outlineColorPanel = new JPanel(getOutlineColorLayout());
			outlineColorPanel.add(getOutlineColorChooser(), "on");
			outlineColorPanel.add(createNoStyleLabel(), "off");
		}
		return outlineColorPanel;
	}

	/**
	 * Returns the layout for the {@link #getPreviewPanel() previewPanel}. 
	 * If the layout doesn't exist it is created.
	 * @return the layout for the previewPanel
	 */
	public CardLayout getPreviewLayout() {
		if (previewLayout == null) {
			previewLayout = new CardLayout();
		}
		return previewLayout;
	}

	/**
	 * Returns the panel for viewing and editing tag attributes for the selected
	 * tag style.
	 * @return panel for editing tag style attributes
	 */
	private TagAttributesDefinitionsEditPanel getTagAttributesDefinitionsEditPanel() {
		if (tagAttributesDefinitionsEditPanel == null) {
			tagAttributesDefinitionsEditPanel = new TagAttributesDefinitionsEditPanel(this);
		}
		return tagAttributesDefinitionsEditPanel;
	}

	/**
	 * Returns the panel with the preview of a {@link Tag} of the currently
	 * edited {@link TagStyle style}.
	 * If the panel doesn't exist it is created with:
	 * <ul>
	 * <li>the {@link #getTagRenderer() tag renderer}, which is shown if
	 * a style is selected,</li>
	 * </ul>the label which tells that the style must be created or chosen,
	 * which is shown if no style is selected</li></ul>
	 * @return the panel with the preview of a tag of the currently
	 * edited style
	 */
	public JPanel getPreviewPanel() {
		if (previewPanel == null) {
			previewPanel = new JPanel(getPreviewLayout());
			previewPanel.setBorder(new TitledBorder(_("Preview")));
			previewPanel.add(getTagRenderer(), "on");
			previewPanel.add(createNoStyleLabel(), "off");
		}
		return previewPanel;
	}

	/**
	 * Creates the label, which informs that there is no {@link TagStyle style}
	 * selected and the user should create or select a style to proceed.
	 * @return the label, which informs that there is no style
	 * selected and the user should create or select a style to proceed.
	 */
	private JLabel createNoStyleLabel() {
		JLabel noStyleLabel = new JLabel(_("Select or add a style"));
		noStyleLabel.setHorizontalAlignment(JLabel.CENTER);
		noStyleLabel.setVerticalAlignment(JLabel.CENTER);
		return noStyleLabel;
	}

	/**
	 * Returns the panel with the parameters of the outline of a {@link
	 * Tag tag} of the selected {@link TagStyle style}:
	 * <ul>
	 * <li>the {@link #getWidthSpinner() width} of the outline,</li>
	 * <li>the {@link #getDashComboBox() dashing style} of the outline.</li>
	 * </ul>
	 * If the panel doesn't exist it is created.
	 * @return the panel with the parameters of the outline
	 */
	public JPanel getOutlineTopPanel() {
		if (outlineTopPanel == null) {

			outlineTopPanel = new JPanel();
			outlineTopPanel.setBorder(new EmptyBorder(3,3,3,3));

			GroupLayout layout = new GroupLayout(outlineTopPanel);
			outlineTopPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel widthLabel = new JLabel(_("Outline width"));
			JLabel dashLabel = new JLabel(_("Outline dash style"));

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(widthLabel)
			        .addComponent(dashLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(getWidthSpinner())
			        .addComponent(getDashComboBox())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(widthLabel)
					.addComponent(getWidthSpinner())
				);

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(dashLabel)
					.addComponent(getDashComboBox())
				);

			layout.setVerticalGroup(vGroup);

		}
		return outlineTopPanel;
	}

	/**
	 * Returns the panel with the properties of the currently edited
	 * {@link TagStyle style}:
	 * <ul>
	 * <li>the {@link #getNameTextField() name},</li>
	 * <li>the {@link #getDescriptionTextPane() description},</li>
	 * <li>the {@link #getCaptureKeyButton() key stroke} used to select
	 * the style.</li>
	 * </ul>
	 * @return the panel with the properties of the currently edited style
	 */
	public JPanel getPropertiesPanel() {
		if (propertiesPanel == null) {

			propertiesPanel = new JPanel();
			propertiesPanel.setBorder(new CompoundBorder(
			                                  new TitledBorder(_("Properties")),
			                                  new EmptyBorder(3,3,3,3)
			                          ));

			GroupLayout layout = new GroupLayout(propertiesPanel);
			propertiesPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel nameLabel = new JLabel(_("Name"));
			JLabel descriptionLabel = new JLabel(_("Description"));
			JLabel keyLabel = new JLabel(_("Key"));
			JLabel markerLabel = new JLabel(_("Marker style"));
			JLabel visibilityLabel = new JLabel(_("Visible"));

			JPanel keyPanel = new JPanel();
			keyPanel.setLayout(new BoxLayout(keyPanel, BoxLayout.X_AXIS));
			keyPanel.add(getKeyTextField());
			keyPanel.add(Box.createHorizontalStrut(3));
			keyPanel.add(getCaptureKeyButton());

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(nameLabel)
			        .addComponent(descriptionLabel)
			        .addComponent(keyLabel)
			        .addComponent(markerLabel)
				.addComponent(visibilityLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(getNameTextField())
			        .addComponent(getDescriptionScrollPane())
			        .addComponent(keyPanel)
			        .addComponent(getMarkerCheckBox())
				.addComponent(getVisiblityCheckbox())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(nameLabel)
					.addComponent(getNameTextField())
				);
			
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(descriptionLabel)
					.addComponent(getDescriptionScrollPane())
				);

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(keyLabel)
					.addComponent(keyPanel)
				);

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(markerLabel)
					.addComponent(getMarkerCheckBox())
				);
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(visibilityLabel)
					.addComponent(getVisiblityCheckbox())
				);

			layout.setVerticalGroup(vGroup);

		}
		return propertiesPanel;

	}

	/**
	 * Returns the chooser of the color of the outline of a {@link Tag} of
	 * the selected {@link TagStyle style}.
	 * If the chooser doesn't exist it is created and a change listener is
	 * added to it. This listener changes the color in the {@link
	 * #getTagRenderer() tag renderer} when the selected color is changed.
	 * @return the chooser of the color of the outline
	 */
	public JColorChooser getOutlineColorChooser() {
		if (outlineColorChooser == null) {
			outlineColorChooser = new JColorChooser();
			outlineColorChooser.setPreviewPanel(new JPanel());
			outlineColorChooser.setChooserPanels(new AbstractColorChooserPanel[] {outlineColorChooser.getChooserPanels()[1]});

			outlineColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (currentStyle != null) {
						currentStyle.setOutlineColor(outlineColorChooser.getColor());
						getTagRenderer().repaint();
						setChanged(true);
					}
				}
			});

		}
		return outlineColorChooser;
	}

	/**
	 * Returns the chooser of the color of the fill of a {@link Tag} of
	 * the selected {@link TagStyle style}.
	 * If the chooser doesn't exist it is created and a change listener is
	 * added to it. This listener changes the color in the {@link
	 * #getTagRenderer() tag renderer} when the selected color is changed.
	 * @return the chooser of the color of the fill
	 */
	public JColorChooser getFillColorChooser() {
		if (fillColorChooser == null) {
			fillColorChooser = new JColorChooser();
			fillColorChooser.setPreviewPanel(new JPanel());
			fillColorChooser.setChooserPanels(new AbstractColorChooserPanel[] {fillColorChooser.getChooserPanels()[1]});

			fillColorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (currentStyle != null) {
						currentStyle.setFillColor(fillColorChooser.getColor());
						getTagRenderer().repaint();
						setChanged(true);
					}
				}
			});

		}
		return fillColorChooser;
	}

	/**
	 * Returns the spinner which allows to choose the width of the outline of a
	 * {@link Tag tag} of the selected {@link TagStyle style}.
	 * If the spinner doesn't exist it is created and a change listener is
	 * added to it. This listener changes the width in the {@link
	 * #getTagRenderer() tag renderer} when the selected value changes.
	 * @return the spinner which allows to choose the width of the outline
	 */
	public JSpinner getWidthSpinner() {
		if (widthSpinner == null) {

			widthSpinner = new JSpinner(new SpinnerNumberModel(1F,1F,10F,1F));
			widthSpinner.setPreferredSize(new Dimension(150,25));

			widthSpinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (currentStyle != null) {
						Number value = (Number) widthSpinner.getValue();
						currentStyle.setOutlineWidth(value.floatValue());
						getTagRenderer().repaint();
						setChanged(true);
					}
				}
			});

		}
		return widthSpinner;
	}

	/**
	 * Returns the model for {@link #getDashComboBox() dashComboBox}.
	 * If the model doesn't exist it is created.
	 * @return the model for dashComboBox.
	 */
	public DefaultComboBoxModel getDashComboBoxModel() {
		if (dashComboBoxModel == null) {
			dashComboBoxModel = new DefaultComboBoxModel(new Dash[] {
			                        null,
			                        new Dash(new float[] { 8F, 8F }),
			                        new Dash(new float[] { 2F, 2F }),
			                        new Dash(new float[] { 8F, 2F, 2F, 2F }),
			                        new Dash(new float[] { 8F, 2F, 2F, 2F, 2F, 2F }),
			                });
		}
		return dashComboBoxModel;
	}

	/**
	 * Returns the combo-box which allows to choose the outline dashing style
	 * of a {@link Tag tag} of the selected {@link TagStyle style}.
	 * If the combo-box doesn't exist it is created and a change listener is
	 * added to it. This listener changes the outline style in the {@link
	 * #getTagRenderer() tag renderer} when the selected value changes.
	 * @return the combo-box which allows to choose the outline dashing style
	 */
	public JComboBox getDashComboBox() {
		if (dashComboBox == null) {

			dashComboBox = new JComboBox(getDashComboBoxModel());

			dashComboBox.setPreferredSize(new Dimension(150,25));
			dashComboBox.setRenderer(new DashListCellRenderer());

			dashComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (currentStyle != null) {
						Dash dash = (Dash) getDashComboBox().getSelectedItem();
						currentStyle.setOutlineDash(dash != null ? dash.dash : null);
						getTagRenderer().repaint();
						setChanged(true);
					}
				}
			});

		}
		return dashComboBox;
	}

	/**
	 * Returns the text field with the name of the {@link TagStyle style}.
	 * If the text field doesn't exist it is created and the listener is added
	 * to it. If the contents of the text field changes the style is {@link
	 * #setChanged(boolean) marked} as changed
	 * @return the text field with the name of the style
	 */
	public JTextField getNameTextField() {
		if (nameTextField == null) {

			nameTextField = new JTextField();
			nameTextField.setPreferredSize(new Dimension(100,25));

			nameTextField.getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {
				@Override
				public void anyUpdate(DocumentEvent e) {
					if (currentStyle != null) {
						setChanged(true);
					}
				}
			});

		}
		return nameTextField;
	}

	/**
	 * Returns the text pane with the description of the {@link TagStyle style}.
	 * If the text field doesn't exist it is created and the listener is added
	 * to it. If the contents of the text field changes the style is {@link
	 * #setChanged(boolean) marked} as changed
	 * @return the text pane with the description of the style
	 */
	public JTextPane getDescriptionTextPane() {
		if (descriptionTextPane == null) {

			descriptionTextPane = new JTextPane();

			descriptionTextPane.getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {
				@Override
				public void anyUpdate(DocumentEvent e) {
					if (currentStyle != null) {
						setChanged(true);
					}
				}
			});

		}
		return descriptionTextPane;
	}

	/**
	 * Returns the scroll pane which contains the {@link
	 * #getDescriptionTextPane() text pane} with the description of the {@link
	 * TagStyle style.}
	 * If the scroll pane doesn't exist it is created.
	 * @return the scroll pane which contains the text pane with the
	 * description of the style
	 */
	public JScrollPane getDescriptionScrollPane() {
		if (descriptionScrollPane == null) {
			descriptionScrollPane = new JScrollPane(getDescriptionTextPane());
			descriptionScrollPane.setPreferredSize(new Dimension(100,80));
		}
		return descriptionScrollPane;
	}

	/**
	 * Returns the text field in which the captured key is displayed.
	 * If the text field doesn't exist it is created and the listener is added
	 * to it. If the contents of the text field changes the style is {@link
	 * #setChanged(boolean) marked} as changed.
	 * @return the text field in which the captured key is displayed
	 * @see #getCaptureKeyButton()
	 */
	public JTextField getKeyTextField() {
		if (keyTextField == null) {
			keyTextField = new JTextField();
			keyTextField.setPreferredSize(new Dimension(100,25));
			keyTextField.setEditable(false);

			keyTextField.getDocument().addDocumentListener(new AnyChangeDocumentAdapter() {
				@Override
				public void anyUpdate(DocumentEvent e) {
					if (currentStyle != null) {
						setChanged(true);
					}
				}
			});

		}
		return keyTextField;
	}

	/**
	 * Returns the button which displays the {@link KeyStrokeCaptureDialog
	 * dialog} which captures the key stroke.
	 * This key stroke is used to select the {@link TagStyle style}.
	 * If the button doesn't exist it is created.
	 * @return the button which displays the dialog which captures the
	 * key stroke
	 */
	public JButton getCaptureKeyButton() {
		if (captureKeyButton == null) {
			captureKeyButton = new JButton();
		}
		return captureKeyButton;
	}

	/**
	 * Returns the check-box which tells if the {@link TagStyle style} should
	 * be a marker style of the regular one.
	 * If the check-box doesn't exist it is created and a change listener is
	 * added to it. This listener updates the outlook of the tag in the {@link
	 * #getTagRenderer() tag renderer} when the state of the check-box changes.
	 * @return the check-box which tells if the style should be a
	 * marker style of the regular one
	 */
	public JCheckBox getMarkerCheckBox() {
		if (markerCheckBox == null) {
			markerCheckBox = new JCheckBox();

			markerCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (currentStyle != null) {
						currentStyle.setMarker(markerCheckBox.isSelected());
						getTagRenderer().repaint();
						setChanged(true);
					}
				}

			});
		}
		return markerCheckBox;
	}

	/**
	 * Returns the check-box which tells whether this tag style should
	 * be rendered in the signal view.
	 * @return the check-box which tells if this tag style should be visible
	 */
	public JCheckBox getVisiblityCheckbox() {
		if (visibilityCheckBox == null) {
			visibilityCheckBox = new JCheckBox();

			visibilityCheckBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (currentStyle != null) {
						currentStyle.setVisible(visibilityCheckBox.isSelected());
						getTagRenderer().repaint();
						setChanged(true);
					}
				}

			});
		}
		return visibilityCheckBox;
	}

	/**
	 * Returns the {@link TagRenderer tag renderer}.
	 * If the renderer doesn't exist it is created.
	 * @return the tag renderer.
	 */
	public TagRenderer getTagRenderer() {
		if (tagRenderer == null) {
			tagRenderer = new TagRenderer();
			tagRenderer.setPreferredSize(new Dimension(150,150));
		}
		return tagRenderer;
	}

	/**
	 * Returns the currently edited {@link TagStyle style}.
	 * @return the currently edited style
	 */
	public TagStyle getCurrentStyle() {
		return currentStyle;
	}

	/**
	 * Sets the currently edited {@link TagStyle style}.
	 * @param style the currently edited style
	 */
	public void setCurrentStyle(TagStyle style) {
		if (style == null) {
			currentStyle = null;
		} else {
			currentStyle = new TagStyle(style);
		}
		updatePanel();
	}

	/**
	 * Updates the fields of this panel to using the {@link #currentStyle}.
	 * If there is no current style the fields are set to default values or
	 * to inform that the style has to be selected.
	 * <p>
	 * After this update the state is {@link #setChanged(boolean) set} to
	 * be unchanged.
	 */
	private void updatePanel() {

		boolean enabled;

		if (currentStyle != null) {

			getNameTextField().setText(currentStyle.getName());
			String description = currentStyle.getDescription();
			getDescriptionTextPane().setText(description != null ? description : "");
			KeyStroke keyStroke = currentStyle.getKeyStroke();
			if (keyStroke == null) {
				getKeyTextField().setText("");
			} else {
				String s = keyStroke.toString();
				s = s.replaceAll("pressed *", "");
				getKeyTextField().setText(s);
			}

			getOutlineColorChooser().setColor(currentStyle.getOutlineColor());
			getWidthSpinner().setValue(currentStyle.getOutlineWidth());

			float[] dashArr = currentStyle.getOutlineDash();
			Dash dash;
			if (dashArr != null) {
				dash = new Dash(dashArr);
				boolean add = true;
				DefaultComboBoxModel dashModel = getDashComboBoxModel();
				int cnt = dashModel.getSize();
				for (int i=1; i<cnt; i++) {
					if (((Dash) dashModel.getElementAt(i)).equals(dash)) {
						add = false;
						break;
					}
				}
				if (add) {
					dashModel.addElement(dash);
				}
			} else {
				dash = null;
			}
			getDashComboBox().setSelectedItem(dash);

			getFillColorChooser().setColor(currentStyle.getFillColor());

			JCheckBox markerCheckBox = getMarkerCheckBox();
			if (currentStyle.getType() == SignalSelectionType.CHANNEL) {
				markerCheckBox.setSelected(currentStyle.isMarker());
				markerCheckBox.setEnabled(true);
			} else {
				markerCheckBox.setSelected(false);
				markerCheckBox.setEnabled(false);
			}

			getVisiblityCheckbox().setEnabled(true);
			getVisiblityCheckbox().setSelected(currentStyle.isVisible());

			getTagRenderer().setTagStyle(currentStyle);

			enabled = true;

		} else {

			getNameTextField().setText("");
			getDescriptionTextPane().setText("");
			getKeyTextField().setText("");

			getOutlineColorChooser().setColor(Color.DARK_GRAY);
			getFillColorChooser().setColor(Color.LIGHT_GRAY);
			getWidthSpinner().setValue(1F);
			getDashComboBox().setSelectedIndex(0);
			getTagRenderer().setTagStyle(null);

			JCheckBox markerCheckBox = getMarkerCheckBox();
			markerCheckBox.setSelected(false);
			markerCheckBox.setEnabled(false);
			getVisiblityCheckbox().setEnabled(false);

			enabled = false;

		}

		getNameTextField().setEnabled(enabled);
		getDescriptionTextPane().setEnabled(enabled);
		getKeyTextField().setEnabled(enabled);
		getCaptureKeyButton().setEnabled(enabled);

		if (enabled) {
			getOutlineColorLayout().show(getOutlineColorPanel(), "on");
			getFillColorLayout().show(getFillColorPanel(), "on");
			getPreviewLayout().show(getPreviewPanel(), "on");
		} else {
			getOutlineColorLayout().show(getOutlineColorPanel(), "off");
			getFillColorLayout().show(getFillColorPanel(), "off");
			getPreviewLayout().show(getPreviewPanel(), "off");
		}
		getWidthSpinner().setEnabled(enabled);
		getDashComboBox().setEnabled(enabled);

		getTagAttributesDefinitionsEditPanel().fillPanelFromModel(currentStyle);

		setChanged(false);

	}

	/**
	 * Validates the changes in this panel.
	 * The changes are valid if the name is not empty and doesn't contain
	 * any bad characters.
	 * @return the object in which errors are stored
	 */
	public Errors validateChanges() {
		Errors errors = new BindException(currentStyle, "data");
		String name = getNameTextField().getText();
		if (name == null || name.isEmpty())
			errors.rejectValue("name", "error.style.nameEmpty", _("Tag style name cannot be empty"));

		if (Util.hasSpecialChars(name))
			errors.rejectValue("name", "error.style.nameBadCharacters", _("Tag style name must not contain control characters"));

		return errors;
	}

	/**
	 * Stores the changes (user input) in the {@link TagStyle style}.
	 */
	public void applyChanges() {

		if (currentStyle != null) {

			currentStyle.setName(getNameTextField().getText());
			String description = getDescriptionTextPane().getText();
			if (description != null && !description.isEmpty()) {
				currentStyle.setDescription(description);
			} else {
				currentStyle.setDescription(null);
			}
			currentStyle.setKeyStroke(KeyStroke.getKeyStroke(getKeyTextField().getText()));

			currentStyle.setOutlineColor(getOutlineColorChooser().getColor());
			currentStyle.setOutlineWidth(((Number) getWidthSpinner().getValue()).floatValue());
			Dash dash = (Dash) getDashComboBox().getSelectedItem();
			currentStyle.setOutlineDash(dash != null ? dash.dash : null);
			currentStyle.setFillColor(getFillColorChooser().getColor());

			if (currentStyle.getType() == SignalSelectionType.CHANNEL) {
				currentStyle.setMarker(getMarkerCheckBox().isSelected());
			}
			currentStyle.setVisible(getVisiblityCheckbox().isSelected());
			getTagAttributesDefinitionsEditPanel().fillModelFromPanel(currentStyle);

			setChanged(false);

		}

	}

	/**
	 * Returns if the changes are saved.
	 * @return {@code false} if there were no changes or changes were saved,
	 * {@code true} otherwise
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Sets if the changes are saved.
	 * @param changed value to be set:
	 * {@code false} if there were no changes or changes were saved,
	 * {@code true} otherwise
	 */
	public void setChanged(boolean changed) {
		if (this.changed != changed) {
			this.changed = changed;
			firePropertyChange(CHANGED_PROPERTY, !changed, changed);
		}
	}

	/**
	 * Class which represents the dashing pattern.
	 * Contains the dashing pattern in the form of the array of floats and allows
	 * to check if two dashing patterns are equal.
	 */
	class Dash {

		/**
		 * the dashing pattern in the form of the array of floats
		 */
		float[] dash;

		/**
		 * Constructor. Sets the dashing pattern.
		 * @param dash the dashing pattern in the form of the array of floats
		 */
		Dash(float[] dash) {
			this.dash = dash;
		}

		/**
		 * Compares this dashing pattern with another object.
		 * The object is equal with this if it of type {@link Dash} and has the
		 * same elements in {@code dash} array
		 */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Dash)) {
				return false;
			}
			return Arrays.equals(dash, ((Dash) obj).dash);
		}

	}

}

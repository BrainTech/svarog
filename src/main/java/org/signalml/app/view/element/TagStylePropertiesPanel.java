/* TagStylePropertiesPanel.java created 2007-11-10
 *
 */
package org.signalml.app.view.element;

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

import org.signalml.app.view.tag.TagRenderer;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.TagStyle;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/** TagStylePropertiesPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStylePropertiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final String CHANGED_PROPERTY = "changed";

	private MessageSourceAccessor messageSource;

	private JColorChooser outlineColorChooser;
	private JColorChooser fillColorChooser;

	private JSpinner widthSpinner;
	private JComboBox dashComboBox;

	private JTextField nameTextField;
	private JTextPane descriptionTextPane;
	private JScrollPane descriptionScrollPane;
	private JTextField keyTextField;
	private JButton captureKeyButton;
	private JCheckBox markerCheckBox;

	private JPanel outlineTopPanel;
	private JPanel propertiesPanel;

	private TagRenderer tagRenderer;

	private TagStyle currentStyle;
	private boolean changed = false;

	private DefaultComboBoxModel dashComboBoxModel;

	private CardLayout fillColorLayout;
	private JPanel fillColorPanel;

	private CardLayout outlineColorLayout;
	private JPanel outlineColorPanel;

	private CardLayout previewLayout;
	private JPanel previewPanel;

	public TagStylePropertiesPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0,5,0,0));

		JPanel graphicsPanel = new JPanel();

		graphicsPanel.setLayout(new BoxLayout(graphicsPanel, BoxLayout.Y_AXIS));

		JPanel outlinePanel = new JPanel(new BorderLayout());
		outlinePanel.setBorder(new TitledBorder(messageSource.getMessage("tagStylePalette.outlineTitle")));

		outlinePanel.add(getOutlineTopPanel(), BorderLayout.NORTH);
		outlinePanel.add(getOutlineColorPanel(), BorderLayout.CENTER);

		JPanel fillPanel = new JPanel(new BorderLayout());
		fillPanel.setBorder(new TitledBorder(messageSource.getMessage("tagStylePalette.fillTitle")));

		fillPanel.add(getFillColorPanel(), BorderLayout.CENTER);

		graphicsPanel.add(outlinePanel);
		graphicsPanel.add(fillPanel);

		JPanel sidePanel = new JPanel(new BorderLayout());

		sidePanel.add(getPropertiesPanel(), BorderLayout.NORTH);
		sidePanel.add(Box.createGlue(), BorderLayout.CENTER);
		sidePanel.add(getPreviewPanel(), BorderLayout.SOUTH);

		add(graphicsPanel, BorderLayout.CENTER);
		add(sidePanel, BorderLayout.EAST);

	}

	public CardLayout getFillColorLayout() {
		if (fillColorLayout == null) {
			fillColorLayout = new CardLayout();
		}
		return fillColorLayout;
	}

	public JPanel getFillColorPanel() {
		if (fillColorPanel == null) {
			fillColorPanel = new JPanel(getFillColorLayout());
			fillColorPanel.add(getFillColorChooser(), "on");
			fillColorPanel.add(createNoStyleLabel(), "off");
		}
		return fillColorPanel;
	}

	public CardLayout getOutlineColorLayout() {
		if (outlineColorLayout == null) {
			outlineColorLayout = new CardLayout();
		}
		return outlineColorLayout;
	}

	public JPanel getOutlineColorPanel() {
		if (outlineColorPanel == null) {
			outlineColorPanel = new JPanel(getOutlineColorLayout());
			outlineColorPanel.add(getOutlineColorChooser(), "on");
			outlineColorPanel.add(createNoStyleLabel(), "off");
		}
		return outlineColorPanel;
	}

	public CardLayout getPreviewLayout() {
		if (previewLayout == null) {
			previewLayout = new CardLayout();
		}
		return previewLayout;
	}

	public JPanel getPreviewPanel() {
		if (previewPanel == null) {
			previewPanel = new JPanel(getPreviewLayout());
			previewPanel.setBorder(new TitledBorder(messageSource.getMessage("tagStylePalette.previewTitle")));
			previewPanel.add(getTagRenderer(), "on");
			previewPanel.add(createNoStyleLabel(), "off");
		}
		return previewPanel;
	}

	private JLabel createNoStyleLabel() {
		JLabel noStyleLabel = new JLabel(messageSource.getMessage("tagStylePalette.noStyle"));
		noStyleLabel.setHorizontalAlignment(JLabel.CENTER);
		noStyleLabel.setVerticalAlignment(JLabel.CENTER);
		return noStyleLabel;
	}

	public JPanel getOutlineTopPanel() {
		if (outlineTopPanel == null) {

			outlineTopPanel = new JPanel();
			outlineTopPanel.setBorder(new EmptyBorder(3,3,3,3));

			GroupLayout layout = new GroupLayout(outlineTopPanel);
			outlineTopPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel widthLabel = new JLabel(messageSource.getMessage("tagStylePalette.width"));
			JLabel dashLabel = new JLabel(messageSource.getMessage("tagStylePalette.style"));

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

	public JPanel getPropertiesPanel() {
		if (propertiesPanel == null) {

			propertiesPanel = new JPanel();
			propertiesPanel.setBorder(new CompoundBorder(
			                                  new TitledBorder(messageSource.getMessage("tagStylePalette.propertiesTitle")),
			                                  new EmptyBorder(3,3,3,3)
			                          ));

			GroupLayout layout = new GroupLayout(propertiesPanel);
			propertiesPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel nameLabel = new JLabel(messageSource.getMessage("tagStylePalette.name"));
			JLabel descriptionLabel = new JLabel(messageSource.getMessage("tagStylePalette.description"));
			JLabel keyLabel = new JLabel(messageSource.getMessage("tagStylePalette.key"));
			JLabel markerLabel = new JLabel(messageSource.getMessage("tagStylePalette.marker"));

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
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(getNameTextField())
			        .addComponent(getDescriptionScrollPane())
			        .addComponent(keyPanel)
			        .addComponent(getMarkerCheckBox())
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

			layout.setVerticalGroup(vGroup);

		}
		return propertiesPanel;

	}

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

	public JScrollPane getDescriptionScrollPane() {
		if (descriptionScrollPane == null) {
			descriptionScrollPane = new JScrollPane(getDescriptionTextPane());
			descriptionScrollPane.setPreferredSize(new Dimension(100,80));
		}
		return descriptionScrollPane;
	}

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

	public JButton getCaptureKeyButton() {
		if (captureKeyButton == null) {
			captureKeyButton = new JButton();
		}
		return captureKeyButton;
	}

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

	public TagRenderer getTagRenderer() {
		if (tagRenderer == null) {
			tagRenderer = new TagRenderer();
			tagRenderer.setPreferredSize(new Dimension(150,150));
		}
		return tagRenderer;
	}

	public TagStyle getCurrentStyle() {
		return currentStyle;
	}

	public void setCurrentStyle(TagStyle style) {
		if (style == null) {
			currentStyle = null;
		} else {
			currentStyle = new TagStyle(style);
		}
		updatePanel();
	}

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

		setChanged(false);

	}

	public Errors validateChanges() {
		Errors errors = new BindException(currentStyle, "data");
		String name = getNameTextField().getText();
		if (name == null || name.isEmpty())
			errors.rejectValue("name", "error.style.nameEmpty");

		if (!Util.validateString(name))
			errors.rejectValue("name", "error.style.nameBadCharacters");

		return errors;
	}

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

			setChanged(false);

		}

	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		if (this.changed != changed) {
			this.changed = changed;
			firePropertyChange(CHANGED_PROPERTY, !changed, changed);
		}
	}

	class Dash {

		float[] dash;

		Dash(float[] dash) {
			this.dash = dash;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Dash)) {
				return false;
			}
			return Arrays.equals(dash, ((Dash) obj).dash);
		}

	}

}

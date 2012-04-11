/* EmbeddedFileChooserFavorites.java created 2011-07-01*/
package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.util.IconUtils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
/**
 * Favorites and visited-dirs history panel for file chooser.
 * @author Mateusz Kruszyński &copy; 2011 CC Titanis
 */
public class EmbeddedFileChooserFavorites extends JPanel
	implements PropertyChangeListener, ActionListener {

	private static final long serialVersionUID = 1L;

	/*
	 * length (in chars) of file path visible in combo boxes
	 */
	static int LEN_OF_PATH=15;

	/*
	 * number of remembered last-visited directories
	 */
	static int NUM_OF_LAST_DIRECTORIES=10;

	/*
	 * parent file chooser
	 */
	JFileChooser fc;

	/*
	 * an object providing application configuration stored in files
	 */
	private ApplicationConfiguration applicationConfiguration;

	/*
	 * favorites and history components panel
	 */

	private JPanel mainPanel;

	/*
	 * button to show and hide Favorites
	 */
	private JButton favoritesButton;

	/*
	 * indicates whether the panel is hidden or not. By default hidden
	 */
	private boolean hidden = true;

	/*
	 * an icon for 'show' mode of {@code favoritesButton} button
	 */
	private ImageIcon showFavoritesIcon;

	/*
	 * an icon fo 'hide' mode or {@code favoritesButton} button
	 */
	private ImageIcon hideFavoritesIcon;

	/*
	 * combo box with recently visited directories
	 */
	private JComboBox dirsCombo;

	/*
	 * combo box with favourities directories
	 */
	private JComboBox favoritesCombo;

	/* Creates panel and all its GUI components.
	 *
	 * @param fc parent file chooser
	 * @param applicationConfiguration an object providing application configuration stored in files
	 */
	public EmbeddedFileChooserFavorites(JFileChooser fc) {
		this.fc = fc;
		this.applicationConfiguration = SvarogApplication.getApplicationConfiguration();
		this.createGui();
		this.showHide();
	}

	/*
	 * Builds all GUI components.
	 */
	protected void createGui() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel b = this.getFavouriesButtonGui();
		add(b);

		this.mainPanel = new JPanel();
		this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS));
		this.mainPanel.add(getFavoritesGui());
		this.mainPanel.add(getHistoryGui());

		add(this.mainPanel);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String pname = e.getPropertyName();
		if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(pname)) {
			String dir = ((File) e.getNewValue()).getAbsolutePath();
			this.lastDirectoryChanged(dir);
		}
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		//if action is combo box - set current directory (or file)
		String path = (String) ((JComboBox) action.getSource()).getSelectedItem();
		if (path != null) {
			File dir = new File(path);
			this.fc.setCurrentDirectory(dir);
		}
	}

	/*
	 * Changes state of the panel - hides it when shown, shows when hidden.
	 */
	public void showHide() {
		this.hidden ^= true;
		if (this.hidden) {
			this.favoritesButton.setIcon(this.hideFavoritesIcon);
			this.favoritesButton.setToolTipText(_("Hide favorites"));
			this.mainPanel.show();
		} else {
			this.favoritesButton.setIcon(this.showFavoritesIcon);
			this.favoritesButton.setToolTipText(_("Show favorites"));
			this.mainPanel.hide();
		}
		this.updateUI();
	}

	/*
	 * Gets current directory from file chooser and adds it to favorites (to
	 * application configuration and to GUI).
	 */
	public void addCurrentDirectory(){
		String dir = this.fc.getCurrentDirectory().getAbsolutePath();
		String[] dirs = this.applicationConfiguration.getFavouriteDirs();
		String[] new_dirs;
		if (dirs != null) {
			new_dirs = new String[dirs.length+1];
			for (int i = 0; i < new_dirs.length-1; i++)
				new_dirs[i] = dirs[i];
			new_dirs[dirs.length] = dir;
		} else {
			new_dirs = new String[1];
			new_dirs[0] = dir;
		}
		this.updateCurrentDirectories(new_dirs);
		this.favoritesCombo.setSelectedItem(dir);
	}

	/*
	 * Updates application configuration and GUI regarding favourites with
	 * given list of directories.
	 *
	 * @param dirs list of directory absolute paths
	 */
	public void updateCurrentDirectories(String[] dirs) {
		if (dirs != null) {
			String[] new_dirs = new String[dirs.length+1];
			for (int i = 1; i < new_dirs.length; i++)
				new_dirs[i] = dirs[i-1];
			new_dirs[0] = "";
			this.applicationConfiguration.setFavouriteDirs(dirs);

			this.favoritesCombo.removeAllItems();
			DefaultComboBoxModel model = new DefaultComboBoxModel(new_dirs);
			this.favoritesCombo.setModel(model);
		}
	}

	/*
	 * Gets current directory from favorites combo and remove it from favorites.
	 */
	public void removeCurrentDirectory() {
		String dir = (String) this.favoritesCombo.getSelectedItem();
		String[] dirs = this.applicationConfiguration.getFavouriteDirs();
		if (dirs == null || dir == null)
			return;

		int contains = -1;
		for (int i = 0; i < dirs.length; i++)
			if (dirs[i].equals(dir)) {
				contains = i;
				break;
			}

		if (contains > -1) {
			String[] new_dirs = new String[dirs.length-1];
			int j = 0;
			for (int i = 0; i < dirs.length; i++) {
				if (i != contains) {
					new_dirs[j] = dirs[i];
					j++;
				}
			}
			this.updateCurrentDirectories(new_dirs);
		}
	}

	/*
	 * Updates last directories history with given directory
	 *
	 * @param dir directory's abs path that has just been selected
	 */
	public void lastDirectoryChanged(String dir) {
	 	String[] dirs = this.applicationConfiguration.getLastDirs();
	 	String[] new_dirs;
	 	if (dirs == null) {
	 		new_dirs = new String[1];
	 		new_dirs[0] = dir;
	 	} else {
	 		if (dirs.length < NUM_OF_LAST_DIRECTORIES) {
	 			new_dirs = new String[dirs.length+1];
	 			for (int i = 0; i < dirs.length; i++)
	 				new_dirs[i+1] = dirs[i];
	 			new_dirs[0] = dir;
	 		} else {
	 			new_dirs = new String[dirs.length];
	 			for (int i = 0; i < dirs.length-1; i++)
	 				new_dirs[i+1] = dirs[i];
	 			new_dirs[0] = dir;
	 		}
	 	}
		this.updateLastDirectories(new_dirs);
	}

	/*
	 * Updates last directories history with given directories
	 *
	 * @param dirs directories' abs path to be stored in GUI and application configuration
	 */
	public void updateLastDirectories(String[] dirs) {
		if (dirs != null) {
			String[] new_dirs = new String[dirs.length+1];
			for (int i = 1; i < new_dirs.length; i++)
				new_dirs[i] = dirs[i-1];
			new_dirs[0] = "";
			this.applicationConfiguration.setLastDirs(dirs);
			this.dirsCombo.removeAllItems();
			DefaultComboBoxModel model = new DefaultComboBoxModel(new_dirs);
			this.dirsCombo.setModel(model);
		}
	}

	/*
	 * Builds and returns GUI for favorites button
	 *
	 * @returns panel containing favorites button
	 */
	protected JPanel getFavouriesButtonGui() {
		JButton b = new JButton("");
		b.setBorder(BorderFactory.
			    createEmptyBorder(0, 0, 0, 0));
		b.setContentAreaFilled(false);
		final EmbeddedFileChooserFavorites fcf = this;
		b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fcf.showHide();
				}
			});
		this.favoritesButton = b;

		//Prepare icons for button
		Image iconImage;
		ImageIcon ic;
		iconImage = IconUtils.loadClassPathImage("org/signalml/app/icon/favorites.png");
		ic = new ImageIcon(iconImage);
		this.showFavoritesIcon = ic;

		iconImage = IconUtils.loadClassPathImage("org/signalml/app/icon/favorites_crossed.png");
		ic = new ImageIcon(iconImage);
		this.hideFavoritesIcon = ic;


		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.TRAILING));
		b.setAlignmentX(Component.RIGHT_ALIGNMENT);
		p.add(b);
		return p;
	}

	/*
	 * Builds and returns favorites GUI
	 *
	 * @returns panel containing favorites GUI
	 */
	protected JPanel getFavoritesGui() {

		//layout settings
 		JPanel p = new JPanel();
 		p.setBorder(new CompoundBorder(new TitledBorder(_("Favorites")),
					       new EmptyBorder(3, 3, 3, 3)));
 		p.setLayout(new BorderLayout(0, 10));

 		JPanel fieldsPanel = new JPanel();
		fieldsPanel.setLayout(new GridLayout(0, 1));

		//buttons preparing
		JComboBox locationsButton = this.getDropDownList();
		this.favoritesCombo = locationsButton;
		this.favoritesCombo.setMaximumRowCount(10);
		this.updateCurrentDirectories(this.applicationConfiguration.getFavouriteDirs());
		final EmbeddedFileChooserFavorites fcf = this;

		ImageIcon ic = new ImageIcon(IconUtils.loadClassPathImage("org/signalml/app/icon/add.png"));
		JButton addFavsButton = new JButton(ic);
		addFavsButton.setContentAreaFilled(false);
		addFavsButton.setToolTipText(_("Add current directory to favorites"));
		addFavsButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		addFavsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fcf.addCurrentDirectory();
				}
			});
		ic = new ImageIcon(IconUtils.loadClassPathImage("org/signalml/app/icon/remove.png"));
		JButton removeFavsButton = new JButton(ic);
		removeFavsButton.setContentAreaFilled(false);
		removeFavsButton.setToolTipText(_("Remove from favorites"));
		removeFavsButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		removeFavsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fcf.removeCurrentDirectory();
				}
			});

 		//layout filling
 		JPanel p2 = new JPanel();
 		p2.setLayout(new BorderLayout());
		JLabel locationsLabel = new JLabel(_("Choose location"));
		p2.add(locationsLabel, BorderLayout.WEST);
		p2.add(new JLabel(" "), BorderLayout.CENTER);

		JPanel p3 = new JPanel(new FlowLayout());
		p3.add(addFavsButton);
		p3.add(removeFavsButton);
		p2.add(p3, BorderLayout.EAST);

		fieldsPanel.add(p2);
		fieldsPanel.add(this.favoritesCombo);

		p.add(fieldsPanel);
		return p;
	}
 	/*
 	 * Builds and returns history GUI
	 *
 	 * @returns panel containing history GUI
 	 */

 	protected JPanel getHistoryGui() {
 		//layout settings
 		JPanel p = new JPanel();
 		p.setBorder(new CompoundBorder(new TitledBorder(_("History")),
					       new EmptyBorder(3, 3, 3, 3)));
 		p.setLayout(new BorderLayout(0, 10));

 		JPanel fieldsPanel = new JPanel();
 		fieldsPanel.setLayout(new GridLayout(0,1));//new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));

		//buttons prepating
		this.dirsCombo = this.getDropDownList();
		this.updateLastDirectories(this.applicationConfiguration.getLastDirs());

		//layout filling
		JLabel locationsLabel = new JLabel(_("Last directories"));
		fieldsPanel.add(locationsLabel);
		fieldsPanel.add(this.dirsCombo);

 		p.add(fieldsPanel);
 		return p;
 	}


	/*
	 * Creates and returns combo box for directory paths
	 *
	 * @returns  combo box for directory paths
	 */
	protected JComboBox getDropDownList() {
		class ComboBoxRenderer extends JLabel
			implements ListCellRenderer {

			private static final long serialVersionUID = 1L;
			public ComboBoxRenderer() {
				setOpaque(true);
				setHorizontalAlignment(LEFT);
				setBorder(new EmptyBorder(0, 3, 0, 3));
			}

			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
				}
				String oldText = ((String) value);
				String newText = oldText;
				String tooltip = oldText;
				if (oldText == null || (oldText.length() == 0)) {
					newText = "";
					tooltip = "";
				} else {
					int lastInd = oldText.length();
					int firstInd = oldText.lastIndexOf("/")+1;
					if (lastInd == firstInd) {
						lastInd--;
						firstInd = oldText.substring(0, lastInd).lastIndexOf("/")+1;
					}
					newText = oldText.substring(firstInd, lastInd);
					if (newText.length() > LEN_OF_PATH)
						newText = "..."+newText.substring(newText.length()-LEN_OF_PATH);
				}
				setText(newText);
				this.setToolTipText(tooltip);
				return this;
			}
		}

		JComboBox c = new JComboBox();
		c.addActionListener(this);
		ComboBoxRenderer r = new ComboBoxRenderer();
		c.setRenderer(r);
		return c;
	}
}

/** EmbeddedFileChooserFavorites.java created 2011-07-01*/
package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.util.IconUtils;

/***
 * Favorites and visited-dirs history panel for file chooser.
 *
 * @author Mateusz Kruszyński &copy; 2011 CC Titanis
 */
public class EmbeddedFileChooserFavorites extends JPanel implements PropertyChangeListener, ActionListener {
	protected static final Logger log = Logger.getLogger(EmbeddedFileChooserFavorites.class);

	private static final long serialVersionUID = 1L;

	/**
	 * length (in chars) of file path visible in combo boxes
	 */
	static int LEN_OF_PATH = 15;

	/**
	 * number of remembered last-visited directories
	 */
	static int NUM_OF_LAST_DIRECTORIES = 10;

	/**
	 * parent file chooser
	 */
	private JFileChooser fileChooser;

	/**
	 * favorites and history components panel
	 */

	private JComponent mainPanel;

	/**
	 * button to show and hide Favorites
	 */
	private JButton favoritesButton;

	/**
	 * indicates whether the panel is hidden or not. By default hidden
	 */
	private boolean hidden = true;

	/**
	 * an icon for 'show' mode of {@code favoritesButton} button
	 */
	private ImageIcon showFavoritesIcon;

	/**
	 * an icon fo 'hide' mode or {@code favoritesButton} button
	 */
	private ImageIcon hideFavoritesIcon;

	/**
	 * combo box with recently visited directories
	 */
	private JComboBox lastDirectoriesComboBox;

	/**
	 * combo box with favourities directories
	 */
	private JComboBox favoritesComboBox;

	/**
	 * Creates panel and all its GUI components.
	 *
	 * @param fc parent file chooser
	 *
	 * @param applicationConfiguration an object providing application
	 * configuration stored in files
	 */
	public EmbeddedFileChooserFavorites(JFileChooser fc) {
		this.fileChooser = fc;
		this.createGui();
		this.showHide();
	}

	protected ApplicationConfiguration getApplicationConfiguration() {
		return SvarogApplication.getApplicationConfiguration();
	}

	/**
	 * Builds all GUI components.
	 */
	protected void createGui() {
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(layout);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor = GridBagConstraints.NORTHEAST;

		add(this.getFavouritesButtonGui(), c);

		this.mainPanel = new Box(BoxLayout.PAGE_AXIS);
		this.mainPanel.add(getFavoritesGui());
		this.mainPanel.add(createHistoryGUI());
		add(this.mainPanel, c);

		c.weighty = 1;
		add(Box.createVerticalGlue(), c);
	}

	public JComboBox getFavoritesComboBox() {
		if (favoritesComboBox == null) {
			favoritesComboBox = new JComboBox();
			favoritesComboBox.addActionListener(this);
			favoritesComboBox.setRenderer(new ComboBoxRenderer());
			favoritesComboBox.setMaximumRowCount(10);
			favoritesComboBox.setSelectedItem(null);
		}
		return favoritesComboBox;
	}

	public JComboBox getLastDirectoriesComboBox() {
		if (lastDirectoriesComboBox == null) {
			lastDirectoriesComboBox =new JComboBox();
			lastDirectoriesComboBox.addActionListener(this);
			lastDirectoriesComboBox.setRenderer(new ComboBoxRenderer());
			lastDirectoriesComboBox.setSelectedItem(null);
		}
		return lastDirectoriesComboBox;
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
		// if action is combo box - set current directory (or file)
		String path = (String) ((JComboBox) action.getSource()).getSelectedItem();
		if (path != null) {
			File dir = new File(path);
			this.fileChooser.setCurrentDirectory(dir);
		}
	}

	/**
	 * Changes state of the panel - hides it when shown, shows when hidden.
	 */
	public void showHide() {
		this.hidden ^= true;
		if (this.hidden) {
			this.favoritesButton.setIcon(this.hideFavoritesIcon);
			this.favoritesButton.setToolTipText(_("Hide favorites"));
		} else {
			this.favoritesButton.setIcon(this.showFavoritesIcon);
			this.favoritesButton.setToolTipText(_("Show favorites"));
		}
		this.mainPanel.setVisible(this.hidden);
		this.updateUI();
	}

	/**
	 * Gets current directory from file chooser and adds it to favorites (to
	 * application configuration and to GUI).
	 */
	public void addCurrentDirectory() {
		String dir = this.fileChooser.getCurrentDirectory().getAbsolutePath();
		String[] dirs = getApplicationConfiguration().getFavouriteDirs();
		String[] new_dirs;
		if (dirs != null) {
			new_dirs = new String[dirs.length + 1];
			for (int i = 0; i < new_dirs.length - 1; i++)
				new_dirs[i] = dirs[i];
			new_dirs[dirs.length] = dir;
		} else {
			new_dirs = new String[1];
			new_dirs[0] = dir;
		}
		this.updateCurrentDirectories(new_dirs);
		getFavoritesComboBox().setSelectedItem(dir);
	}

	/**
	 * Updates application configuration and GUI regarding favourites with given
	 * list of directories.
	 *
	 * @param dirs list of directory absolute paths
	 */
	public void updateCurrentDirectories(String[] dirs) {
		if (dirs != null) {
			String[] new_dirs = new String[dirs.length + 1];
			for (int i = 1; i < new_dirs.length; i++)
				new_dirs[i] = dirs[i - 1];
			new_dirs[0] = null;
			getApplicationConfiguration().setFavouriteDirs(dirs);

			getFavoritesComboBox().removeAllItems();
			DefaultComboBoxModel model = new DefaultComboBoxModel(dirs);
			getFavoritesComboBox().setModel(model);
		}
	}

	/**
	 * Gets current directory from favorites combo and remove it from favorites.
	 */
	public void removeCurrentDirectory() {
		String dir = (String) getFavoritesComboBox().getSelectedItem();
		String[] dirs = getApplicationConfiguration().getFavouriteDirs();
		if (dirs == null || dir == null)
			return;

		int contains = -1;
		for (int i = 0; i < dirs.length; i++)
			if (dirs[i].equals(dir)) {
				contains = i;
				break;
			}

		if (contains > -1) {
			String[] new_dirs = new String[dirs.length - 1];
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

	/**
	 * Updates last directories history with given directory
	 *
	 * @param dir directory's abs path that has just been selected
	 */
	public void lastDirectoryChanged(String dir) {
		log.debug("lastDirectoryChanged: " + dir);
		String[] dirs = getApplicationConfiguration().getLastDirs();
		LinkedList<String> list = new LinkedList<String>(Arrays.asList(dirs));
		list.remove(dir);
		list.addFirst(dir);
		if (list.size() > NUM_OF_LAST_DIRECTORIES)
			list.removeLast();
		this.updateLastDirectories(list.toArray(new String[0]));
	}

	/**
	 * Updates last directories history with given directories
	 *
	 * @param dirs directories' abs path to be stored in GUI and application
	 * configuration
	 */
	public void updateLastDirectories(String[] dirs) {
		if (dirs != null) {
			String[] new_dirs = new String[dirs.length + 1];
			for (int i = 1; i < new_dirs.length; i++)
				new_dirs[i] = dirs[i - 1];
			new_dirs[0] = null;

			getApplicationConfiguration().setLastDirs(dirs);
			getLastDirectoriesComboBox().removeAllItems();
			DefaultComboBoxModel model = new DefaultComboBoxModel(dirs);
			getLastDirectoriesComboBox().setModel(model);
		}
	}

	/**
	 * Builds and returns GUI for favorites button
	 *
	 * @returns panel containing favorites button
	 */
	protected JPanel getFavouritesButtonGui() {
		JButton b = new JButton("");
		b.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		b.setContentAreaFilled(false);
		final EmbeddedFileChooserFavorites fcf = this;
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fcf.showHide();
			}
		});
		this.favoritesButton = b;

		// Prepare icons for button
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

	/**
	 * Builds and returns favorites GUI
	 *
	 * @returns panel containing favorites GUI
	 */
	protected JComponent getFavoritesGui() {

		// layout settings
		Box fieldsPanel = new Box(BoxLayout.PAGE_AXIS);
		fieldsPanel.setBorder(new TitledBorder(_("Favorites")));

		// buttons preparing
		this.updateCurrentDirectories(getApplicationConfiguration().getFavouriteDirs());
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

		// layout filling
		JPanel p2 = new JPanel();
		p2.add(new JLabel(_("Choose location")));
		p2.add(addFavsButton);
		p2.add(removeFavsButton);

		fieldsPanel.add(p2);
		fieldsPanel.add(getFavoritesComboBox());

		return fieldsPanel;
	}

	/**
	 * Builds and returns a panel with history GUI
	 *
	 * @returns panel containing history GUI
	 */
	protected JComponent createHistoryGUI() {
		// layout settings
		Box fieldsPanel = new Box(BoxLayout.PAGE_AXIS);
		fieldsPanel.setBorder(new TitledBorder(_("History")));

		// buttons prepating
		this.updateLastDirectories(getApplicationConfiguration().getLastDirs());

		// layout filling
		JLabel locationsLabel = new JLabel(_("Last directories"));
		fieldsPanel.add(locationsLabel);
		fieldsPanel.add(getLastDirectoriesComboBox());

		return fieldsPanel;
	}

	class ComboBoxRenderer extends BasicComboBoxRenderer {
		private static final long serialVersionUID = 1L;

		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setBorder(new EmptyBorder(0, 3, 0, 3));
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component rendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			String oldText = ((String) value);
			String newText = oldText;
			String tooltip = oldText;

			if (oldText != null && oldText.length() > 0) {
				int lastInd = oldText.length();
				int firstInd = oldText.lastIndexOf("/") + 1;
				if (lastInd == firstInd) {
					lastInd--;
					firstInd = oldText.substring(0, lastInd).lastIndexOf("/") + 1;
				}
				newText = oldText.substring(firstInd, lastInd);
				if (newText.length() > LEN_OF_PATH)
					newText = "..." + newText.substring(newText.length() - LEN_OF_PATH);

				setText(newText);
				setToolTipText(tooltip);
			}
			return rendererComponent;
		}
	}

}

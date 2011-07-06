package org.signalml.app.view.element;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.openide.awt.DropDownButtonFactory;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.util.IconUtils;
import org.springframework.context.support.MessageSourceAccessor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class EmbeddedFileChooserFavorites extends JPanel implements
		PropertyChangeListener, ActionListener {
	static int LEN_OF_PATH=20;
	static int NUM_OF_LAST_DIRECTORIES=10;
  JFileChooser fc;
  private MessageSourceAccessor messageSource;
  private ApplicationConfiguration applicationConfiguration;

  
  private int hidden = 1;
  private JPanel mainPanel;
  private JButton favoritiesButton;
  private ImageIcon showFavoritiesIcon;
  private ImageIcon hideFavoritiesIcon;
  
  private JComboBox dirsCombo;
  private JComboBox favoritiesCombo;
  
  JLabel fileLabel;

  public EmbeddedFileChooserFavorites(JFileChooser fc, MessageSourceAccessor messageSource, ApplicationConfiguration applicationConfiguration) {
    // Set up the accessory. The file chooser will give us a reasonable
    // size.
	this.fc = fc;
	this.messageSource = messageSource;
	this.applicationConfiguration = applicationConfiguration;
	this.createGui();
    this.showHide();

  }
  
  protected void createGui() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    JPanel b = this.getFavouriesButtonGui();
    //b.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(b);

    this.mainPanel = new JPanel();
    this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS));
    this.mainPanel.add(getFavoritiesGui());
    this.mainPanel.add(new JLabel(" "));
    this.mainPanel.add(new JLabel(" "));
    this.mainPanel.add(getHistoryGui());
    
    add(this.mainPanel);
    
    JLabel packer = new JLabel(" ");
    packer.setPreferredSize(new Dimension(10, 500));
    add(packer);
    
      
  }

  public void propertyChange(PropertyChangeEvent e) {
    String pname = e.getPropertyName();
    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(pname)) {
      // Ok, the user selected a file in the chooser
      String dir = ((File) e.getNewValue()).getAbsolutePath();
      this.lastDirectoryChanged(dir);
    }
  }
  @Override
  public void actionPerformed(ActionEvent action) {
	  //if action is combo box - set current directory (or file)
	  String path = (String) ((JComboBox) action.getSource()).getSelectedItem();
	  System.out.println("PATH "+path);

	  if (path != null) {
		  File dir = new File(path);
		  this.fc.setCurrentDirectory(dir);
	  }
  }
  
  public void showHide() {
	  this.hidden = (this.hidden + 1) % 2;
	  if (this.hidden == 1) {
		  this.favoritiesButton.setIcon(this.hideFavoritiesIcon);
		  this.favoritiesButton.setToolTipText(this.messageSource.getMessage("opensignal.fileChooser.hideFavorities"));
  		  this.mainPanel.show();
	  } else {
		  this.favoritiesButton.setIcon(this.showFavoritiesIcon);
		  this.favoritiesButton.setToolTipText(this.messageSource.getMessage("opensignal.fileChooser.showFavorities"));
  		  this.mainPanel.hide();
	  }
  	  this.updateUI();//TODO
}
	  
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
	  //system set
  }
  
  public void updateCurrentDirectories(String[] dirs) {
	  if (dirs != null) {
		  String[] new_dirs = new String[dirs.length+1];
		  for (int i = 1; i < new_dirs.length; i++)
			  new_dirs[i] = dirs[i-1];
		  new_dirs[0] = "";
		  this.applicationConfiguration.setFavouriteDirs(new_dirs);
		  
		  this.favoritiesCombo.removeAllItems();
		  DefaultComboBoxModel model = new DefaultComboBoxModel(new_dirs);  
		  this.favoritiesCombo.setModel(model);  
	  }
		  
  }
  
  public void removeCurrentDirectory() {
	  String dir = (String) this.favoritiesCombo.getSelectedItem();//this.fc.getCurrentDirectory().getAbsolutePath();
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
	  //system set
  }
  
  public void lastDirectoryChanged(String dir) {
	  String[] dirs = this.applicationConfiguration.getLastDirs();
	  String[] new_dirs;
	  if (dirs == null) {
		  new_dirs = new String[1];
		  new_dirs[0] = dir;
	  } else {
	  if (dirs.length < this.NUM_OF_LAST_DIRECTORIES) {
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
	  this.updateLastDirectories(new_dirs);
	  }
  
  }
  
  public void updateLastDirectories(String[] dirs) {
	  if (dirs != null) {
		  String[] new_dirs = new String[dirs.length+1];
		  for (int i = 1; i < new_dirs.length; i++)
			  new_dirs[i] = dirs[i-1];
		  new_dirs[0] = "";
		  this.applicationConfiguration.setLastDirs(new_dirs);
	  	  this.dirsCombo.removeAllItems();
	  	  DefaultComboBoxModel model = new DefaultComboBoxModel(new_dirs);  
	  	  this.dirsCombo.setModel(model);
	  }
  }
  
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
	    this.favoritiesButton = b;
	    
	    //Prepare icons for button
	    Image iconImage;
	    ImageIcon ic;
	    iconImage = IconUtils.loadClassPathImage("org/signalml/app/icon/favorities.png");
	    ic = new ImageIcon(iconImage);
	    this.showFavoritiesIcon = ic;
	    
	    iconImage = IconUtils.loadClassPathImage("org/signalml/app/icon/favorities_crossed.png");
	    ic = new ImageIcon(iconImage);
	    this.hideFavoritiesIcon = ic;
	    
	    
	    JPanel p = new JPanel();
        //p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
	    //p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
	    //b.setAlignmentX(Component.LEFT_ALIGNMENT);
	    b.setAlignmentX(Component.RIGHT_ALIGNMENT);
	    p.add(b);
	    p.setBackground(Color.pink);
	  return p;
  }
  
  protected JPanel getFavoritiesGui() {
	    
	  //layout settings
	  JPanel p = new JPanel();
      p.setBorder(new CompoundBorder(
              new TitledBorder(messageSource.getMessage("opensignal.fileChooser.favorities")),
              new EmptyBorder(3, 3, 3, 3)));
      p.setLayout(new BorderLayout(0, 10));
      
      JPanel fieldsPanel = new JPanel();
      fieldsPanel.setLayout(new GridLayout(0, 1));      
      /*GridBagConstraints constraints = new GridBagConstraints();
      constraints.anchor = GridBagConstraints.CENTER;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.insets = new Insets(8, 8, 8, 8);*/
      
      //buttons prepating
      JComboBox locationsButton = this.getDropDownList();
      this.favoritiesCombo = locationsButton;
      this.favoritiesCombo.setMaximumRowCount(10);
      this.updateCurrentDirectories(this.applicationConfiguration.getFavouriteDirs());
      final EmbeddedFileChooserFavorites fcf = this;
      
	  ImageIcon ic = new ImageIcon(IconUtils.loadClassPathImage("org/signalml/app/icon/add.png"));
      JButton addFavsButton = new JButton(ic);  
	  addFavsButton.setContentAreaFilled(false);
      addFavsButton.setToolTipText(this.messageSource.getMessage("opensignal.fileChooser.addToFavorities"));
      addFavsButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
	  addFavsButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	fcf.addCurrentDirectory();
	        }
	      });
	  ic = new ImageIcon(IconUtils.loadClassPathImage("org/signalml/app/icon/remove.png"));
      JButton removeFavsButton = new JButton(ic);
	  removeFavsButton.setContentAreaFilled(false);
      removeFavsButton.setToolTipText(this.messageSource.getMessage("opensignal.fileChooser.removeFromFavorities"));
      removeFavsButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
	  removeFavsButton.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	fcf.removeCurrentDirectory();
	        }
	      });
	  
      
      
      //layout filling
	  JPanel p2 = new JPanel();
	  p2.setLayout(new BorderLayout());
      JLabel locationsLabel = new JLabel(messageSource.getMessage("opensignal.fileChooser.goToFavorities"));      
      p2.add(locationsLabel, BorderLayout.WEST);
      p2.add(new JLabel(" "), BorderLayout.CENTER);
      
      JPanel p3 = new JPanel(new FlowLayout());
      p3.add(addFavsButton);
      p3.add(removeFavsButton);
      p2.add(p3, BorderLayout.EAST);
        
      fieldsPanel.add(p2);
      fieldsPanel.add(this.favoritiesCombo);
                             
      p.add(fieldsPanel);
	return p;  
  }
  
  protected JPanel getHistoryGui() {
	  //layout settings
	  JPanel p = new JPanel();
      p.setBorder(new CompoundBorder(
              new TitledBorder(messageSource.getMessage("opensignal.fileChooser.history")),
              new EmptyBorder(3, 3, 3, 3)));
      p.setLayout(new BorderLayout(0, 10));
      
      JPanel fieldsPanel = new JPanel();
      fieldsPanel.setLayout(new GridLayout(0,1));//new BoxLayout(fieldsPanel, BoxLayout.PAGE_AXIS));
      
      //buttons prepating
      this.dirsCombo = this.getDropDownList();
      this.updateLastDirectories(this.applicationConfiguration.getLastDirs());
      
      //layout filling
      JLabel locationsLabel = new JLabel(messageSource.getMessage("opensignal.fileChooser.lastDirs"));
      fieldsPanel.add(locationsLabel);
      fieldsPanel.add(this.dirsCombo);
         
      p.add(fieldsPanel);
	return p;
  }
  
  
  protected JComboBox getDropDownList() {
	  final EmbeddedFileChooserFavorites fcf = this;
	  class ComboBoxRenderer extends JLabel
      implements ListCellRenderer {
		  public ComboBoxRenderer() {
			  setOpaque(true);
			  setHorizontalAlignment(CENTER);
			  setVerticalAlignment(CENTER);
		  }

		  public Component getListCellRendererComponent(
                      JList list,
                      Object value,
                      int index,
                      boolean isSelected,
                      boolean cellHasFocus) {
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
			  if (oldText == null) {
				  newText = "";
				  tooltip = "";
			  } else if (oldText.length() > fcf.LEN_OF_PATH) {
				  newText = "..."+oldText.substring(oldText.length()-fcf.LEN_OF_PATH);
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
package org.signalml.plugin.bookreporter.ui;

import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterChartExportDialog extends javax.swing.JDialog {

	private static final float MM_PER_INCH = 25.4f;

	private static final float STANDARD_DPI = 100.0f;

	private int dpi;

	private File selectedFile = null;

	/**
	 * Creates new form BookReporterChartExportDialog
	 */
	public BookReporterChartExportDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		dpi = Integer.parseInt((String) dpiCombo.getSelectedItem());
	}

	/**
	 * @return exported chart width in pixels
	 */
	public int getPixelWidth() {
		int px = (Integer) xSize.getValue();
		if (unitCombo.getSelectedIndex() == 1) {
			px = (int) Math.ceil(px / MM_PER_INCH * dpi);
		}
		return px;
	}

	/**
	 * @return exported chart height in pixels
	 */
	public int getPixelHeight() {
		int px = (Integer) ySize.getValue();
		if (unitCombo.getSelectedIndex() == 1) {
			px = (int) Math.ceil(px / MM_PER_INCH * dpi);
		}
		return px;
	}

	public File getSelectedFile() {
		return selectedFile;
	}

	/**
	 * @return scale of exported chart (1 = screen resolution)
	 */
	public float getScale() {
		return dpi / STANDARD_DPI;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			selectedFile = null;
		}
		super.setVisible(visible);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sizePanel = new javax.swing.JPanel();
        xSize = new javax.swing.JSpinner();
        ySize = new javax.swing.JSpinner();
        dpiCombo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        unitCombo = new javax.swing.JComboBox();
        fileChooser = new javax.swing.JFileChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(_("Export chart as PNG"));

        xSize.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        ySize.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        dpiCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "72", "100", "150", "200", "300", "600" }));
        dpiCombo.setSelectedIndex(2);
        dpiCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dpiComboActionPerformed(evt);
            }
        });

        jLabel1.setText("dpi");

        jLabel2.setText("Size:");

        jLabel3.setText("x");

        jLabel4.setText("at");

        unitCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "px", "mm" }));
        unitCombo.setSelectedIndex(1);

        javax.swing.GroupLayout sizePanelLayout = new javax.swing.GroupLayout(sizePanel);
        sizePanel.setLayout(sizePanelLayout);
        sizePanelLayout.setHorizontalGroup(
            sizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xSize, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ySize, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(unitCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dpiCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(61, Short.MAX_VALUE))
        );
        sizePanelLayout.setVerticalGroup(
            sizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(ySize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(dpiCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(unitCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        fileChooser.setFileFilter(new FileNameExtensionFilter(_("PNG images"), "png"));
        fileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooserActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sizePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(fileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sizePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void dpiComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dpiComboActionPerformed
		dpi = Integer.parseInt((String) dpiCombo.getSelectedItem());
	}//GEN-LAST:event_dpiComboActionPerformed

    private void fileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileChooserActionPerformed
		if (evt.getActionCommand().equals("ApproveSelection")) {
			selectedFile = fileChooser.getSelectedFile();
			this.setVisible(false);
		} else if (evt.getActionCommand().equals("CancelSelection")) {
			this.setVisible(false);
		}
    }//GEN-LAST:event_fileChooserActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox dpiCombo;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel sizePanel;
    private javax.swing.JComboBox unitCombo;
    private javax.swing.JSpinner xSize;
    private javax.swing.JSpinner ySize;
    // End of variables declaration//GEN-END:variables
}
package org.signalml.plugin.bookreporter.ui;

import java.awt.Dialog;
import javax.swing.DefaultComboBoxModel;
import org.signalml.app.util.IconUtils;
import org.signalml.plugin.bookreporter.chart.preset.*;
import org.signalml.plugin.bookreporter.data.BookReporterFASPThreshold;

/**
 * @author piotr@develancer.pl
 */
public class BookReporterNewChartDialog extends javax.swing.JDialog {

	private Class<? extends BookReporterChartPreset> chartClass
		= BookReporterChartPresetCount.class;
	private BookReporterChartPreset chartPreset = null;
	
	/**
	 * Creates new form BookReporterNewChartDialog
	 */
	public BookReporterNewChartDialog(Dialog parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	public BookReporterChartPreset getChartPreset() {
		return chartPreset;
	}

	/**
	 * This method is called from within the constructor to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                diagramTypeButtonGroup = new javax.swing.ButtonGroup();
                waveCountDiagramButton = new javax.swing.JRadioButton();
                timePercentageDiagramButton = new javax.swing.JRadioButton();
                singleOccurenceDiagramButton = new javax.swing.JRadioButton();
                addChartButton = new javax.swing.JButton();
                cancelButton = new javax.swing.JButton();
                waveTypeCombo = new javax.swing.JComboBox();
                timeIntervalPanel = new javax.swing.JPanel();
                timeIntervalSpinner = new javax.swing.JSpinner();
                jLabel1 = new javax.swing.JLabel();
                jLabel2 = new javax.swing.JLabel();

                setTitle("New chart properties");
                setModal(true);
                setName("newChartDialog"); // NOI18N
                setResizable(false);
                setType(java.awt.Window.Type.UTILITY);

                diagramTypeButtonGroup.add(waveCountDiagramButton);
                waveCountDiagramButton.setSelected(true);
                waveCountDiagramButton.setText("count (per time interval) of...");
                waveCountDiagramButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                waveCountDiagramButtonActionPerformed(evt);
                        }
                });

                diagramTypeButtonGroup.add(timePercentageDiagramButton);
                timePercentageDiagramButton.setText("time percentage occupied by...");
                timePercentageDiagramButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                timePercentageDiagramButtonActionPerformed(evt);
                        }
                });

                diagramTypeButtonGroup.add(singleOccurenceDiagramButton);
                singleOccurenceDiagramButton.setText("single occurences of...");
                singleOccurenceDiagramButton.setEnabled(false);
                singleOccurenceDiagramButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                singleOccurenceDiagramButtonActionPerformed(evt);
                        }
                });

                addChartButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/add.png"));
                addChartButton.setText("Add chart");
                addChartButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                addChartButtonActionPerformed(evt);
                        }
                });

                cancelButton.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/cancel.png"));
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                cancelButtonActionPerformed(evt);
                        }
                });

                waveTypeCombo.setEditable(true);
                waveTypeCombo.setModel(new DefaultComboBoxModel(BookReporterFASPThreshold.getPredefinedThresholdNames()));

                timeIntervalSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(180), Integer.valueOf(10), null, Integer.valueOf(10)));
                timeIntervalSpinner.setMinimumSize(new java.awt.Dimension(80, 28));
                timeIntervalSpinner.setPreferredSize(new java.awt.Dimension(80, 28));

                jLabel1.setText("in each");

                jLabel2.setText("second interval");

                javax.swing.GroupLayout timeIntervalPanelLayout = new javax.swing.GroupLayout(timeIntervalPanel);
                timeIntervalPanel.setLayout(timeIntervalPanelLayout);
                timeIntervalPanelLayout.setHorizontalGroup(
                        timeIntervalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, timeIntervalPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timeIntervalSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2))
                );
                timeIntervalPanelLayout.setVerticalGroup(
                        timeIntervalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(timeIntervalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(timeIntervalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                );

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(timeIntervalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(addChartButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(cancelButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(waveCountDiagramButton)
                                                        .addComponent(timePercentageDiagramButton)
                                                        .addComponent(singleOccurenceDiagramButton))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(waveTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(waveCountDiagramButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timePercentageDiagramButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(singleOccurenceDiagramButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(waveTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timeIntervalPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(addChartButton)
                                        .addComponent(cancelButton))
                                .addContainerGap())
                );

                pack();
        }// </editor-fold>//GEN-END:initComponents

        private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		this.setVisible(false);
        }//GEN-LAST:event_cancelButtonActionPerformed

        private void waveCountDiagramButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_waveCountDiagramButtonActionPerformed
		this.chartClass = BookReporterChartPresetCount.class;
		timeIntervalPanel.setVisible(true);
        }//GEN-LAST:event_waveCountDiagramButtonActionPerformed

        private void timePercentageDiagramButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timePercentageDiagramButtonActionPerformed
		this.chartClass = BookReporterChartPresetPercentage.class;
                timeIntervalPanel.setVisible(true);
        }//GEN-LAST:event_timePercentageDiagramButtonActionPerformed

        private void singleOccurenceDiagramButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleOccurenceDiagramButtonActionPerformed
		this.chartClass = BookReporterChartPresetOccurences.class;
                timeIntervalPanel.setVisible(false);
        }//GEN-LAST:event_singleOccurenceDiagramButtonActionPerformed

        private void addChartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addChartButtonActionPerformed
		String wavesName = (String) this.waveTypeCombo.getSelectedItem();
		BookReporterFASPThreshold threshold = BookReporterFASPThreshold.getPredefinedThreshold(wavesName);

		Integer timeInterval = (Integer) this.timeIntervalSpinner.getValue();
		try {
			BookReporterChartPreset preset = this.chartClass.newInstance();
			if (threshold != null) {
				preset.setThreshold(threshold);
			}
			if (preset instanceof BookReporterChartPresetPerInterval) {
				( (BookReporterChartPresetPerInterval) preset ).setTimeInterval(timeInterval);
			}
			preset.setWavesName(wavesName);
			this.chartPreset = preset;
			this.setVisible(false);
		} catch (IllegalAccessException ex) {
			// nothing
		} catch (IllegalArgumentException ex) {
			// nothing
		} catch (InstantiationException ex) {
			// nothing
		} catch (SecurityException ex) {
			// nothing
		}
        }//GEN-LAST:event_addChartButtonActionPerformed

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton addChartButton;
        private javax.swing.JButton cancelButton;
        private javax.swing.ButtonGroup diagramTypeButtonGroup;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JRadioButton singleOccurenceDiagramButton;
        private javax.swing.JPanel timeIntervalPanel;
        private javax.swing.JSpinner timeIntervalSpinner;
        private javax.swing.JRadioButton timePercentageDiagramButton;
        private javax.swing.JRadioButton waveCountDiagramButton;
        private javax.swing.JComboBox waveTypeCombo;
        // End of variables declaration//GEN-END:variables
}

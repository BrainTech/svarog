/* AverageBooksPopupDialog.java created 2008-03-12
 *
 */

package org.signalml.app.method.bookaverage;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.view.book.BookView;
import org.signalml.app.view.element.TitledCrossBorder;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;

// FIXME reuse this class in the mehtod

/** AverageBooksPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class AverageBooksPopupDialog extends AbstractPopupDialog {

	private static final long serialVersionUID = 1L;

	private BookView bookView;

	private JRadioButton averageOffRadio;
	private JRadioButton averageOnRadio;

	private JSpinner minSegmentSpinner;
	private JSpinner maxSegmentSpinner;

	private JCheckBox allChannelsCheckBox;

	private ButtonGroup averageGroup;

	public AverageBooksPopupDialog( Window w, boolean isModal) {
		super( w, isModal);
	}

	@Override
	public JComponent createInterface() {

		JPanel averagePanel = new JPanel();
		averagePanel.setLayout(new BoxLayout(averagePanel, BoxLayout.Y_AXIS));
		averagePanel.setBorder(new CompoundBorder(
		                               new TitledCrossBorder(_("Book averaging"), true),
		                               new EmptyBorder(3,3,3,3)
		                       ));

		averagePanel.add(getAverageOffRadio());
		averagePanel.add(getAverageOnRadio());

		JPanel settingsPanel = new JPanel();
		settingsPanel.setBorder(new CompoundBorder(
		                                new TitledBorder(_("Settings")),
		                                new EmptyBorder(3,3,3,3)
		                        ));

		GroupLayout layout = new GroupLayout(settingsPanel);
		settingsPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel minSegmentLabel = new JLabel(_("From segment"));
		JLabel maxSegmentLabel = new JLabel(_("To segment"));
		JLabel allChannelsLabel = new JLabel(_("All channels together"));
		allChannelsLabel.setMinimumSize(new Dimension(25,35));

		Component minSegmentGlue = Box.createHorizontalGlue();
		Component maxSegmentGlue = Box.createHorizontalGlue();
		Component allChannelsGlue = Box.createHorizontalGlue();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(minSegmentLabel)
		        .addComponent(maxSegmentLabel)
		        .addComponent(allChannelsLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(minSegmentGlue)
		        .addComponent(maxSegmentGlue)
		        .addComponent(allChannelsGlue)
		);

		hGroup.addGroup(
		        layout.createParallelGroup(Alignment.TRAILING)
		        .addComponent(getMinSegmentSpinner())
		        .addComponent(getMaxSegmentSpinner())
		        .addComponent(getAllChannelsCheckBox())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(minSegmentLabel)
				.addComponent(minSegmentGlue)
				.addComponent(getMinSegmentSpinner())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(maxSegmentLabel)
				.addComponent(maxSegmentGlue)
				.addComponent(getMaxSegmentSpinner())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(allChannelsLabel)
				.addComponent(allChannelsGlue)
				.addComponent(getAllChannelsCheckBox())
			);
		
		layout.setVerticalGroup(vGroup);		
				
		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(averagePanel, BorderLayout.NORTH);
		interfacePanel.add(settingsPanel, BorderLayout.CENTER);

		Dimension size = averagePanel.getPreferredSize();
		if (size.width < 220) {
			size.width = 220;
		}
		averagePanel.setPreferredSize(size);

		return interfacePanel;

	}

	public ButtonGroup getAverageGroup() {
		if (averageGroup == null) {
			averageGroup = new ButtonGroup();
		}
		return averageGroup;
	}

	public JRadioButton getAverageOnRadio() {
		if (averageOnRadio == null) {

			averageOnRadio = new JRadioButton(_("Book averaging on"));
			getAverageGroup().add(averageOnRadio);

		}
		return averageOnRadio;
	}

	public JRadioButton getAverageOffRadio() {
		if (averageOffRadio == null) {

			averageOffRadio = new JRadioButton(_("Book averaging off"));
			getAverageGroup().add(averageOffRadio);

		}
		return averageOffRadio;
	}

	public JSpinner getMinSegmentSpinner() {
		if (minSegmentSpinner == null) {

			minSegmentSpinner = new JSpinner(new SpinnerNumberModel(1, 1, bookView.getDocument().getBook().getSegmentCount(), 1));
			minSegmentSpinner.setPreferredSize(new Dimension(75,25));

		}
		return minSegmentSpinner;
	}

	public JSpinner getMaxSegmentSpinner() {
		if (maxSegmentSpinner == null) {

			int segmentCount = bookView.getDocument().getBook().getSegmentCount();
			maxSegmentSpinner = new JSpinner(new SpinnerNumberModel(segmentCount, 1, segmentCount, 1));
			maxSegmentSpinner.setPreferredSize(new Dimension(75, 25));

		}
		return maxSegmentSpinner;
	}

	public JCheckBox getAllChannelsCheckBox() {
		if (allChannelsCheckBox == null) {
			allChannelsCheckBox = new JCheckBox();
		}
		return allChannelsCheckBox;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		/*
		if( bookView.isAveraging() ) {
			getAverageOnRadio().setSelected(true);
		} else {
			getAverageOffRadio().setSelected(true);
		}

		getMinSegmentSpinner().setValue( provider.getMinSegment()+1 );
		getMaxSegmentSpinner().setValue( provider.getMaxSegment()+1 );
		getAllChannelsCheckBox().setSelected( provider.getChannel() < 0 );
		*/

	}

	@Override
	public void fillModelFromDialog(Object model) {

		/*

		if( getAverageOnRadio().isSelected() ) {

			provider.setBook( bookView.getFilter() );
			provider.setMinSegment( ((Number) getMinSegmentSpinner().getValue()).intValue()-1 );
			provider.setMaxSegment( ((Number) getMaxSegmentSpinner().getValue()).intValue()-1 );
			if( getAllChannelsCheckBox().isSelected() ) {
				provider.setChannel(-1);
			} else {
				provider.setChannel(0);
			}

			bookView.setAveraging(true);

		} else {

			bookView.setAveraging(false);

		}

		*/

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return true;
	}

	@Override
	public boolean isFormClickApproving() {
		return true;
	}

	public BookView getBookView() {
		return bookView;
	}

	public void setBookView(BookView bookView) {
		this.bookView = bookView;
	}

}

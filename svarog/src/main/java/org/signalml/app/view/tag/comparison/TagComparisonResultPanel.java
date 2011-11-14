/* TagComparisonResultPanel.java created 2007-11-14
 *
 */
package org.signalml.app.view.tag.comparison;

import static org.signalml.app.SvarogApplication._;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.domain.tag.TagComparisonResult;
import org.signalml.domain.tag.TagComparisonResults;

/** TagComparisonResultPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagComparisonResultPanel extends JPanel {

	public static final boolean SHOW_PERCENT_DEFAULT = false;

	private static final long serialVersionUID = 1L;

	private TagIconProducer tagIconProducer;
	private TagComparisonResults results;

	private JTabbedPane tabbedPane;

	private TagStatisticTableModel topPageStatisticModel;
	private TagStatisticTableModel topBlockStatisticModel;
	private TagStatisticTableModel topChannelStatisticModel;

	private TagStatisticTableModel bottomPageStatisticModel;
	private TagStatisticTableModel bottomBlockStatisticModel;
	private TagStatisticTableModel bottomChannelStatisticModel;

	private TagComparisonTableModel pageComparisonModel;
	private TagComparisonTableModel blockComparisonModel;
	private TagComparisonTableModel channelComparisonModel;

	private TagStatisticTable topPageStatisticTable;
	private TagStatisticTable topBlockStatisticTable;
	private TagStatisticTable topChannelStatisticTable;

	private TagStatisticTable bottomPageStatisticTable;
	private TagStatisticTable bottomBlockStatisticTable;
	private TagStatisticTable bottomChannelStatisticTable;

	private TagComparisonTable pageComparisonTable;
	private TagComparisonTable blockComparisonTable;
	private TagComparisonTable channelComparisonTable;

	private JPanel pageStatisticPanel;
	private JPanel blockStatisticPanel;
	private JPanel channelStatisticPanel;

	private JPanel pageComparisonPanel;
	private JPanel blockComparisonPanel;
	private JPanel channelComparisonPanel;

	private SourceChannelListModel channelListModel;

	private JComboBox selectedChannelComboBox;
	private JCheckBox percentCheckBox;

	private Integer selectedChannel = null;
	private TableToTextExporter tableToTextExporter;
	private ViewerFileChooser fileChooser;

	private String cornerPanelSeconds;
	private String cornerPanelPercent;

	public TagComparisonResultPanel(TableToTextExporter tableToTextExporter, ViewerFileChooser fileChooser) {
		super();
		this.tableToTextExporter = tableToTextExporter;
		this.fileChooser = fileChooser;

		cornerPanelSeconds = _("[s]");
		cornerPanelPercent = _("[%]");

		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(3,3,3,3));

		JPanel controlPanel = new JPanel(new GridLayout(1,2,5,5));
		controlPanel.setBorder(new EmptyBorder(5,0,0,0));

		controlPanel.add(getPercentCheckBox());
		controlPanel.add(getSelectedChannelComboBox());

		add(getTabbedPane(), BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);

	}

	public TagComparisonResults getResults() {
		return results;
	}

	public void setResults(TagComparisonResults results) {
		if (this.results != results) {
			this.results = results;
			if (results == null) {

				getTopPageStatisticModel().setStatistic(null);
				getTopBlockStatisticModel().setStatistic(null);
				getBottomPageStatisticModel().setStatistic(null);
				getBottomBlockStatisticModel().setStatistic(null);
				getPageComparisonModel().setResult(null);
				getBlockComparisonModel().setResult(null);

				getChannelListModel().setResults(null);

				setSelectedChannel(null);

			} else {

				getTopPageStatisticModel().setStatistic(results.getPageTagResult().getTopStatistic());

				getTopBlockStatisticModel().setStatistic(results.getBlockTagResult().getTopStatistic());

				getBottomPageStatisticModel().setStatistic(results.getPageTagResult().getBottomStatistic());

				getBottomBlockStatisticModel().setStatistic(results.getBlockTagResult().getBottomStatistic());

				getPageComparisonModel().setResult(results.getPageTagResult());
				getBlockComparisonModel().setResult(results.getBlockTagResult());

				getChannelListModel().setResults(results);

				if (results.getChannelCount() == 0) {
					selectedChannel = null;
				}
				else if (selectedChannel < 0) {
					selectedChannel = 0;
				}
				fillChannelModels();

			}
		}
	}

	public int getSelectedChannel() {
		return selectedChannel;
	}

	public void setSelectedChannel(Integer selectedChannel) {

		if (this.selectedChannel != selectedChannel) {
			this.selectedChannel = selectedChannel;
			fillChannelModels();
		}

	}

	private void fillChannelModels() {

		JTabbedPane tabbedPane = getTabbedPane();

		if (results == null || selectedChannel == null) {

			getTopChannelStatisticModel().setStatistic(null);
			getBottomChannelStatisticModel().setStatistic(null);

			getChannelComparisonModel().setResult(null);

			if (tabbedPane.getSelectedIndex() > 3) {
				tabbedPane.setSelectedIndex(0);
			}
			tabbedPane.setEnabledAt(4, false);
			tabbedPane.setEnabledAt(5, false);

		} else {

			TagComparisonResult result = results.getChannelResult(selectedChannel);

			getTopChannelStatisticModel().setStatistic(result.getTopStatistic());

			getBottomChannelStatisticModel().setStatistic(result.getBottomStatistic());

			getChannelComparisonModel().setResult(result);

			tabbedPane.setEnabledAt(4, true);
			tabbedPane.setEnabledAt(5, true);

		}

	}

	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		if (this.tagIconProducer != tagIconProducer) {
			this.tagIconProducer = tagIconProducer;

			getTopPageStatisticTable().setTagIconProducer(tagIconProducer);
			getTopBlockStatisticTable().setTagIconProducer(tagIconProducer);
			getTopChannelStatisticTable().setTagIconProducer(tagIconProducer);

			getBottomPageStatisticTable().setTagIconProducer(tagIconProducer);
			getBottomBlockStatisticTable().setTagIconProducer(tagIconProducer);
			getBottomChannelStatisticTable().setTagIconProducer(tagIconProducer);

			getPageComparisonTable().setTagIconProducer(tagIconProducer);
			getBlockComparisonTable().setTagIconProducer(tagIconProducer);
			getChannelComparisonTable().setTagIconProducer(tagIconProducer);

		}
	}

	private JPanel createStatisticPanel(TagStatisticTable topTable, TagStatisticTable bottomTable) {

		JPanel statisticPanel = new JPanel(new GridLayout(1,2,5,5));

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(new CompoundBorder(
		                            new TitledBorder(_("First document")),
		                            new EmptyBorder(3,3,3,3)
		                    ));
		leftPanel.add(new JScrollPane(topTable), BorderLayout.CENTER);

		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBorder(new CompoundBorder(
		                             new TitledBorder(_("Second document")),
		                             new EmptyBorder(3,3,3,3)
		                     ));
		rightPanel.add(new JScrollPane(bottomTable), BorderLayout.CENTER);

		statisticPanel.add(leftPanel);
		statisticPanel.add(rightPanel);

		return statisticPanel;

	}

	public JPanel getPageStatisticPanel() {
		if (pageStatisticPanel == null) {
			pageStatisticPanel = createStatisticPanel(getTopPageStatisticTable(), getBottomPageStatisticTable());
		}
		return pageStatisticPanel;
	}

	public JPanel getBlockStatisticPanel() {
		if (blockStatisticPanel == null) {
			blockStatisticPanel = createStatisticPanel(getTopBlockStatisticTable(), getBottomBlockStatisticTable());
		}
		return blockStatisticPanel;
	}

	public JPanel getChannelStatisticPanel() {
		if (channelStatisticPanel == null) {
			channelStatisticPanel = createStatisticPanel(getTopChannelStatisticTable(), getBottomChannelStatisticTable());
		}
		return channelStatisticPanel;
	}

	private JPanel createComparisonPanel(TagComparisonTable table) {

		JPanel comparisonPanel = new JPanel(new BorderLayout());
		comparisonPanel.setBorder(new CompoundBorder(
		                                  new TitledBorder(_("Comparison result")),
		                                  new EmptyBorder(3,3,3,3)
		                          ));
		comparisonPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		return comparisonPanel;

	}

	public JPanel getPageComparisonPanel() {
		if (pageComparisonPanel == null) {
			pageComparisonPanel = createComparisonPanel(getPageComparisonTable());
		}
		return pageComparisonPanel;
	}

	public JPanel getBlockComparisonPanel() {
		if (blockComparisonPanel == null) {
			blockComparisonPanel = createComparisonPanel(getBlockComparisonTable());
		}
		return blockComparisonPanel;
	}

	public JPanel getChannelComparisonPanel() {
		if (channelComparisonPanel == null) {
			channelComparisonPanel = createComparisonPanel(getChannelComparisonTable());
		}
		return channelComparisonPanel;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {

			tabbedPane = new JTabbedPane();

			tabbedPane.addTab(_("Page statistic"), getPageStatisticPanel());
			tabbedPane.addTab(_("Page comparison"), getPageComparisonPanel());
			tabbedPane.addTab(_("Block statistic"), getBlockStatisticPanel());
			tabbedPane.addTab(_("Block comparison"), getBlockComparisonPanel());
			tabbedPane.addTab(_("Channel statistic"), getChannelStatisticPanel());
			tabbedPane.addTab(_("Channel comparison"), getChannelComparisonPanel());

			tabbedPane.setSelectedIndex(0);

			tabbedPane.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					int index = tabbedPane.getSelectedIndex();

					boolean comboEnabled = (index == 4 || index == 5);
					boolean checkBoxEnabled = (index == 1 || index == 3 || index == 5);

					getSelectedChannelComboBox().setEnabled(comboEnabled);
					getPercentCheckBox().setEnabled(checkBoxEnabled);

				}

			});

		}
		return tabbedPane;
	}

	public TagStatisticTableModel getTopPageStatisticModel() {
		if (topPageStatisticModel == null) {
			topPageStatisticModel = new TagStatisticTableModel();
		}
		return topPageStatisticModel;
	}

	public TagStatisticTableModel getTopBlockStatisticModel() {
		if (topBlockStatisticModel == null) {
			topBlockStatisticModel = new TagStatisticTableModel();
		}
		return topBlockStatisticModel;
	}

	public TagStatisticTableModel getTopChannelStatisticModel() {
		if (topChannelStatisticModel == null) {
			topChannelStatisticModel = new TagStatisticTableModel();
		}
		return topChannelStatisticModel;
	}

	public TagStatisticTableModel getBottomPageStatisticModel() {
		if (bottomPageStatisticModel == null) {
			bottomPageStatisticModel = new TagStatisticTableModel();
		}
		return bottomPageStatisticModel;
	}

	public TagStatisticTableModel getBottomBlockStatisticModel() {
		if (bottomBlockStatisticModel == null) {
			bottomBlockStatisticModel = new TagStatisticTableModel();
		}
		return bottomBlockStatisticModel;
	}

	public TagStatisticTableModel getBottomChannelStatisticModel() {
		if (bottomChannelStatisticModel == null) {
			bottomChannelStatisticModel = new TagStatisticTableModel();
		}
		return bottomChannelStatisticModel;
	}

	public TagComparisonTableModel getPageComparisonModel() {
		if (pageComparisonModel == null) {
			pageComparisonModel = new TagComparisonTableModel();
			pageComparisonModel.setShowPercent(SHOW_PERCENT_DEFAULT);
		}
		return pageComparisonModel;
	}

	public TagComparisonTableModel getBlockComparisonModel() {
		if (blockComparisonModel == null) {
			blockComparisonModel = new TagComparisonTableModel();
			blockComparisonModel.setShowPercent(SHOW_PERCENT_DEFAULT);
		}
		return blockComparisonModel;
	}

	public TagComparisonTableModel getChannelComparisonModel() {
		if (channelComparisonModel == null) {
			channelComparisonModel = new TagComparisonTableModel();
			channelComparisonModel.setShowPercent(SHOW_PERCENT_DEFAULT);
		}
		return channelComparisonModel;
	}

	public SourceChannelListModel getChannelListModel() {
		if (channelListModel == null) {
			channelListModel = new SourceChannelListModel();
		}
		return channelListModel;
	}

	public TagStatisticTable getTopPageStatisticTable() {
		if (topPageStatisticTable == null) {
			topPageStatisticTable = new TagStatisticTable(getTopPageStatisticModel());
			topPageStatisticTable.setTableToTextExporter(tableToTextExporter);
			topPageStatisticTable.setFileChooser(fileChooser);
		}
		return topPageStatisticTable;
	}

	public TagStatisticTable getTopBlockStatisticTable() {
		if (topBlockStatisticTable == null) {
			topBlockStatisticTable = new TagStatisticTable(getTopBlockStatisticModel());
			topBlockStatisticTable.setTableToTextExporter(tableToTextExporter);
			topBlockStatisticTable.setFileChooser(fileChooser);
		}
		return topBlockStatisticTable;
	}

	public TagStatisticTable getTopChannelStatisticTable() {
		if (topChannelStatisticTable == null) {
			topChannelStatisticTable = new TagStatisticTable(getTopChannelStatisticModel());
			topChannelStatisticTable.setTableToTextExporter(tableToTextExporter);
			topChannelStatisticTable.setFileChooser(fileChooser);
		}
		return topChannelStatisticTable;
	}

	public TagStatisticTable getBottomPageStatisticTable() {
		if (bottomPageStatisticTable == null) {
			bottomPageStatisticTable = new TagStatisticTable(getBottomPageStatisticModel());
			bottomPageStatisticTable.setTableToTextExporter(tableToTextExporter);
			bottomPageStatisticTable.setFileChooser(fileChooser);
		}
		return bottomPageStatisticTable;
	}

	public TagStatisticTable getBottomBlockStatisticTable() {
		if (bottomBlockStatisticTable == null) {
			bottomBlockStatisticTable = new TagStatisticTable(getBottomBlockStatisticModel());
			bottomBlockStatisticTable.setTableToTextExporter(tableToTextExporter);
			bottomBlockStatisticTable.setFileChooser(fileChooser);
		}
		return bottomBlockStatisticTable;
	}

	public TagStatisticTable getBottomChannelStatisticTable() {
		if (bottomChannelStatisticTable == null) {
			bottomChannelStatisticTable = new TagStatisticTable(getBottomChannelStatisticModel());
			bottomChannelStatisticTable.setTableToTextExporter(tableToTextExporter);
			bottomChannelStatisticTable.setFileChooser(fileChooser);
		}
		return bottomChannelStatisticTable;
	}

	public TagComparisonTable getPageComparisonTable() {
		if (pageComparisonTable == null) {
			pageComparisonTable = new TagComparisonTable(getPageComparisonModel());
			pageComparisonTable.setTableToTextExporter(tableToTextExporter);
			pageComparisonTable.setFileChooser(fileChooser);
			pageComparisonTable.setCornerPanelText(cornerPanelSeconds);
		}
		return pageComparisonTable;
	}

	public TagComparisonTable getBlockComparisonTable() {
		if (blockComparisonTable == null) {
			blockComparisonTable = new TagComparisonTable(getBlockComparisonModel());
			blockComparisonTable.setTableToTextExporter(tableToTextExporter);
			blockComparisonTable.setFileChooser(fileChooser);
			blockComparisonTable.setCornerPanelText(cornerPanelSeconds);
		}
		return blockComparisonTable;
	}

	public TagComparisonTable getChannelComparisonTable() {
		if (channelComparisonTable == null) {
			channelComparisonTable = new TagComparisonTable(getChannelComparisonModel());
			channelComparisonTable.setTableToTextExporter(tableToTextExporter);
			channelComparisonTable.setFileChooser(fileChooser);
			channelComparisonTable.setCornerPanelText(cornerPanelSeconds);
		}
		return channelComparisonTable;
	}

	public JComboBox getSelectedChannelComboBox() {
		if (selectedChannelComboBox == null) {
			selectedChannelComboBox = new JComboBox(getChannelListModel());
			selectedChannelComboBox.setPreferredSize(new Dimension(200,25));

			selectedChannelComboBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					int index = selectedChannelComboBox.getSelectedIndex();
					if (index < 0) {
						setSelectedChannel(null);
					} else {
						setSelectedChannel(index);
					}
				}

			});

		}
		return selectedChannelComboBox;
	}

	public JCheckBox getPercentCheckBox() {
		if (percentCheckBox == null) {
			percentCheckBox = new JCheckBox(_("Show as percent"));
			percentCheckBox.setSelected(SHOW_PERCENT_DEFAULT);

			percentCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					boolean showPercent = percentCheckBox.isSelected();
					getPageComparisonModel().setShowPercent(showPercent);
					getBlockComparisonModel().setShowPercent(showPercent);
					getChannelComparisonModel().setShowPercent(showPercent);

					String cornerText = (showPercent ? cornerPanelPercent : cornerPanelSeconds);
					getPageComparisonTable().setCornerPanelText(cornerText);
					getBlockComparisonTable().setCornerPanelText(cornerText);
					getChannelComparisonTable().setCornerPanelText(cornerText);

				}

			});

		}
		return percentCheckBox;
	}

	public TableToTextExporter getTableToTextExporter() {
		return tableToTextExporter;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

}


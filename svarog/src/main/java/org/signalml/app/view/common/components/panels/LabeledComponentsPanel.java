package org.signalml.app.view.common.components.panels;

import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;

public abstract class LabeledComponentsPanel extends AbstractPanel {

	private List<ComponentWithLabel> components;

	public LabeledComponentsPanel() {
		super();
		createInterface();
	}

	public LabeledComponentsPanel(String label) {
		super(label);
		createInterface();
	}

	protected List<ComponentWithLabel> getComponentsWithLabels() {
		if (components == null)
			components = createComponents();
		return components;
	}

	protected abstract List<ComponentWithLabel> createComponents();

	protected abstract int getNumberOfColumns();

	protected int getNumberOfRows() {
		return (int) Math.ceil(((double) getComponentsWithLabels().size()) / getNumberOfColumns());
	}

	private List<ComponentWithLabel> getComponentsWithLabelsForColumn(int column) {
		List<ComponentWithLabel> columnComponents = new ArrayList<>();

		for (int rowNumber = 0; rowNumber < getNumberOfRows(); rowNumber++) {
			int index = column * getNumberOfRows() + rowNumber;
			columnComponents.add(components.get(index));
		}

		return columnComponents;
	}

	private List<ComponentWithLabel> getComponentsWithLabelsForRow(int rowNumber) {
		List<ComponentWithLabel> columnComponents = new ArrayList<>();

		for (int columnNumber = 0; columnNumber < getNumberOfColumns(); columnNumber++) {
			int componentIndex = columnNumber * getNumberOfRows() + rowNumber;
			columnComponents.add(components.get(componentIndex));
		}

		return columnComponents;
	}

	protected void createInterface() {
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		//hgroup
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		for (int columnNumber = 0; columnNumber < getNumberOfColumns(); columnNumber++) {

			ParallelGroup labelsParallelGroup = layout.createParallelGroup();
			ParallelGroup componentsParallelGroup = layout.createParallelGroup();

			for (ComponentWithLabel componentWithLabel: getComponentsWithLabelsForColumn(columnNumber)) {
				labelsParallelGroup.addComponent(componentWithLabel.getLabel());
				componentsParallelGroup.addComponent(componentWithLabel.getComponent());
			}

			hGroup.addGroup(labelsParallelGroup);
			hGroup.addGroup(componentsParallelGroup);

		}
		layout.setHorizontalGroup(hGroup);

		//vgroup
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		for (int rowNumber = 0; rowNumber < getNumberOfRows(); rowNumber++) {
			ParallelGroup parallelGroup = layout.createParallelGroup(Alignment.BASELINE);

			for (ComponentWithLabel componentWithLabel: getComponentsWithLabelsForRow(rowNumber)) {
				parallelGroup.addComponent(componentWithLabel.getLabel());
				parallelGroup.addComponent(componentWithLabel.getComponent());
			}

			vGroup.addGroup(parallelGroup);
		}

		layout.setVerticalGroup(vGroup);
	}

}

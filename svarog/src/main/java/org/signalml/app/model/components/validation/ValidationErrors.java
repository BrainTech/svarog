package org.signalml.app.model.components.validation;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

public class ValidationErrors extends AbstractListModel {

	protected List<String> errorStrings = new ArrayList<String>();

	public void addError(String error) {
		errorStrings.add(error);
	}

	public List<String> getErrorStrings() {
		return errorStrings;
	}

	@Override
	public Object getElementAt(int index) {
		return errorStrings.get(index);
	}

	public boolean hasErrors() {
		return getSize() > 0;
	}

	@Override
	public int getSize() {
		return errorStrings.size();
	}

	public void addAllErrors(ValidationErrors other) {
		for (String error: other.errorStrings) {
			this.addError(error);
		}
	}

}

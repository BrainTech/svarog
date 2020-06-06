/* BookAverageMethodDialog.java created 2007-10-22
 *
 */

package org.signalml.app.method.bookaverage;

import java.awt.Window;
import javax.swing.JComponent;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.method.bookaverage.BookAverageData;
import org.signalml.plugin.export.SignalMLException;

/**
 * BookAverageMethodDialog
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * (+ fixed by) piotr@develancer.pl
 */
public class BookAverageMethodDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	BookAverageMethodPanel panel;

	public BookAverageMethodDialog(Window window) {
		super(window,true);
	}

	@Override
	protected void initialize() {
		setTitle(_("Configure book averaging"));
		setIconImage(IconUtils.loadClassPathImage(BookAverageMethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {
		this.panel = new BookAverageMethodPanel();
		return this.panel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		BookAverageData data = (BookAverageData) model;
		panel.setSelectedWidth(data.getWidth());
		panel.setSelectedHeight(data.getHeight());
		panel.setTimeStart(data.getMinPosition());
		panel.setTimeEnd(data.getMaxPosition());
		panel.setFrequencyStart(data.getMinFrequency());
		panel.setFrequencyEnd(data.getMaxFrequency());
		panel.setPageStart(data.getMinSegment() + 1);
		panel.setPageEnd(data.getMaxSegment() + 1);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		BookAverageData data = (BookAverageData) model;
		data.setWidth(panel.getSelectedWidth());
		data.setHeight(panel.getSelectedHeight());
		data.setMinPosition(panel.getTimeStart());
		data.setMaxPosition(panel.getTimeEnd());
		data.setMinFrequency(panel.getFrequencyStart());
		data.setMaxFrequency(panel.getFrequencyEnd());
		data.setMinSegment(panel.getPageStart() - 1);
		data.setMaxSegment(panel.getPageEnd() - 1);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return BookAverageData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		super.validateDialog(model, errors);
		double timeEnd = panel.getTimeEnd();
		double timeStart = panel.getTimeStart();
		double freqEnd = panel.getFrequencyEnd();
		double freqStart = panel.getFrequencyStart();
		int pageEnd = panel.getPageEnd();
		int pageStart = panel.getPageStart();
		if (pageEnd <= 0 || pageStart <= 0 || pageStart > pageEnd) {
			errors.addError(_("Invalid page selection."));
		}
		if (Double.isNaN(timeEnd) || Double.isInfinite(timeEnd) || timeEnd < 0) {
			errors.addError(_("Invalid end time."));
		}
		if (Double.isNaN(timeStart) || Double.isInfinite(timeStart) || timeStart < 0) {
			errors.addError(_("Invalid start time."));
		}
		if (timeStart >= timeEnd) {
			errors.addError(_("Invalid time interval."));
		}
		if (Double.isNaN(freqEnd) || Double.isInfinite(freqEnd) || freqEnd < 0) {
			errors.addError(_("Invalid highest frequency."));
		}
		if (Double.isNaN(freqStart) || Double.isInfinite(freqStart) || freqStart < 0) {
			errors.addError(_("Invalid lowest frequency."));
		}
		if (freqStart >= freqEnd) {
			errors.addError(_("Invalid frequency interval."));
		}
		if (panel.getSelectedWidth() <= 0 || panel.getSelectedHeight() <= 0) {
			errors.addError(_("Invalid image dimensions."));
		}
	}

}

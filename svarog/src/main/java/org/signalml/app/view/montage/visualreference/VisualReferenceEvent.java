/* VisualReferenceEvent.java created 2007-11-30
 *
 */

package org.signalml.app.view.montage.visualreference;

import java.util.EventObject;
import org.signalml.domain.montage.MontageEvent;
import org.signalml.domain.montage.SourceMontageEvent;

/** VisualReferenceEvent
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private SourceMontageEvent sourceMontageEvent;
	private MontageEvent montageEvent;

	public VisualReferenceEvent(Object source) {
		super(source);
	}

	public VisualReferenceEvent(Object source, SourceMontageEvent sourceMontageEvent) {
		super(source);
		this.sourceMontageEvent = sourceMontageEvent;
	}

	public VisualReferenceEvent(Object source, MontageEvent montageEvent) {
		super(source);
		this.montageEvent = montageEvent;
	}

	public SourceMontageEvent getSourceMontageEvent() {
		return sourceMontageEvent;
	}

	public MontageEvent getMontageEvent() {
		return montageEvent;
	}

}

/* TagComparisonDescriptor.java created 2007-11-14
 *
 */

package org.signalml.app.model.tag;

import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.tag.TagIconProducer;

/** TagComparisonDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagComparisonDescriptor {

	private SignalDocument signalDocument;
	private TagIconProducer tagIconProducer;

	private TagDocument topTagDocument;
	private TagDocument bottomTagDocument;

	public TagComparisonDescriptor(SignalDocument signalDocument, TagDocument topTagDocument, TagDocument bottomTagDocument) {
		this.signalDocument = signalDocument;
		this.topTagDocument = topTagDocument;
		this.bottomTagDocument = bottomTagDocument;
	}

	public TagComparisonDescriptor(SignalDocument signalDocument) {
		this.signalDocument = signalDocument;
	}

	public SignalDocument getSignalDocument() {
		return signalDocument;
	}

	public TagDocument getTopTagDocument() {
		return topTagDocument;
	}

	public void setTopTagDocument(TagDocument topTagDocument) {
		this.topTagDocument = topTagDocument;
	}

	public TagDocument getBottomTagDocument() {
		return bottomTagDocument;
	}

	public void setBottomTagDocument(TagDocument bottomTagDocument) {
		this.bottomTagDocument = bottomTagDocument;
	}

	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		this.tagIconProducer = tagIconProducer;
	}

}

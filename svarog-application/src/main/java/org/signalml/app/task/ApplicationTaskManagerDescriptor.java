/* ApplicationTaskManagerDescriptor.java created 2008-02-15
 * 
 */

package org.signalml.app.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.signalml.app.config.AbstractXMLConfiguration;
import org.signalml.app.util.XMLUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/** ApplicationTaskManagerDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("tasks")
public class ApplicationTaskManagerDescriptor extends AbstractXMLConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<ApplicationTaskDescriptor> tasks;
		
	public ApplicationTaskManagerDescriptor() {
		tasks = new ArrayList<ApplicationTaskDescriptor>();
	}
	
	public ApplicationTaskManagerDescriptor(ArrayList<ApplicationTaskDescriptor> tasks) {
		this.tasks = tasks;
	}

	public Iterator<ApplicationTaskDescriptor> taskIterator() {
		return tasks.iterator();
	}

	@Override
	public String getStandardFilename() {
		return "tasks.xml";
	}
	
	@Override
	public XStream getStreamer() {
		if( streamer == null ) {
			streamer = createTaskManagerStreamer();
		}
		return streamer;
	}

	private XStream createTaskManagerStreamer() {
		XStream streamer = XMLUtils.getDefaultStreamer();
		Annotations.configureAliases(
				streamer,
				ApplicationTaskManagerDescriptor.class,
				ApplicationTaskDescriptor.class
		);
		streamer.setMode(XStream.NO_REFERENCES);
				
		return streamer;
	}	

}

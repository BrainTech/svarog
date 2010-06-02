package org.signalml.app.worker;

import java.awt.Color;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.IncomingMessageData;
import multiplexer.jmx.client.JmxClient;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.signalml.domain.signal.SignalSelectionType;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.Tag;
import org.signalml.domain.tag.TagStyle;
import org.signalml.multiplexer.protocol.SvarogConstants;
import org.signalml.multiplexer.protocol.SvarogProtocol;

import com.google.protobuf.ByteString;

/** MonitorWorker
 *
 */
public class TagRecorderWorker extends SwingWorker< Void, Tag> {

    protected static final Logger logger = Logger.getLogger( TagRecorderWorker.class);

    static final int TIMEOUT_MILIS = 50;

    private StyledTagSet tagSet = new StyledTagSet();
	private JmxClient jmxClient;
//	private OpenMonitorDescriptor monitorDescriptor;
    private volatile boolean finished;


	public TagRecorderWorker( JmxClient jmxClient) {// , OpenMonitorDescriptor monitorDescriptor
	    this.jmxClient = jmxClient;
//	    this.monitorDescriptor = monitorDescriptor;
	}

    @Override
	protected Void doInBackground() throws Exception {

	    logger.debug( "Tag recorder: start...");

        while (!isCancelled()) {

            logger.debug( "Tag recorder: receiving!");
            // receive message
            IncomingMessageData msgData = null;
            try {
                msgData = jmxClient.receive( TIMEOUT_MILIS, TimeUnit.MILLISECONDS);
                if (msgData == null)
                    continue;
                MultiplexerMessage sampleMsg = msgData.getMessage();
                int type = sampleMsg.getType();
                logger.debug( "Tag recorder: received message type: " + type);
                if (!(sampleMsg.getType() == SvarogConstants.MessageTypes.TAG)) {
                    continue;
                }

                logger.debug( "Tag recorder: got a tag!");

                ByteString msgString = sampleMsg.getMessage();
                SvarogProtocol.Tag tagMsg = null;
                try {
                    tagMsg = SvarogProtocol.Tag.parseFrom( msgString);
                } 
                catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                // TODO dodać obsługę stylów - może wybór z jakiejś palety dla poszczególnych nazw i kanałów
                TagStyle style = new TagStyle( SignalSelectionType.CHANNEL, tagMsg.getName(), tagMsg.getName(), Color.RED, Color.BLUE, 2);
                String channels = tagMsg.getChannels();
                StringTokenizer st = new StringTokenizer( channels, " ");
                int n = st.countTokens();
                for (int i=0; i<n; i++) {
                    String s = st.nextToken();
                    int channel = Integer.parseInt( s);
                    Tag tag = new Tag( style, 
                            (float) tagMsg.getStartTimestamp(), 
                            (float) tagMsg.getEndTimestamp(), 
                            channel);
                    publish( tag);
                }
            }
            catch (InterruptedException e) {
                logger.error("receiveing failed! " + e.getMessage());
                return null;
            }
        }
        // TODO dorobić nawiązywanie połączenia dla tagów i rozłączanie razem z nawiązywaniem połączenia sygnałowego
		return null;
	}

    @Override
    protected void process( List< Tag> tags) {
        for (Tag tag : tags)
            tagSet.addTag( tag);
        firePropertyChange( "newTags", null, tags);
    }

    @Override
    protected void done() {
        finished = true;
        firePropertyChange( "tagsRead", null, tagSet);
    }

    public boolean isFinished() {
        return finished;
    }

    public StyledTagSet getTagSet() {
        return tagSet;
    }

}

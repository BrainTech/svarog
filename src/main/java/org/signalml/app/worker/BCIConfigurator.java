package org.signalml.app.worker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.Queue;

import multiplexer.jmx.client.JmxClient;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import multiplexer.protocol.Protocol.MultiplexerMessage;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.springframework.context.support.MessageSourceAccessor;

/** 
 * SignalSaverWorker
 */
public class BCIConfigurator implements ChannelFutureListener {

    protected static final Logger logger = Logger.getLogger( BCIConfigurator.class);

    private Queue< MultiplexerMessage> messageQueue;
    private volatile JmxClient jmxClient;
    private volatile boolean pendingAbort;

    private PropertyChangeSupport propertyChangeSupport;

	public BCIConfigurator( MessageSourceAccessor messageSource,
	                        JmxClient jmxClient,
                            PropertyChangeListener eventHandler) {
	    this.jmxClient = jmxClient;
	    messageQueue = new LinkedList<MultiplexerMessage>();
	    pendingAbort = false;
	    propertyChangeSupport = new PropertyChangeSupport( this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

	public void enqueueMessage( MultiplexerMessage message) {
	    messageQueue.offer( message);
	}

	protected MultiplexerMessage poll() {
	    return messageQueue.poll();
	}

    public void abort() {
        pendingAbort = true;
    }

    public boolean pendingAbort() {
        return pendingAbort;
    }

    public void execute() {
        MultiplexerMessage mm = poll();
        if (mm != null) {
            ChannelFuture f = null;
            try {
                f = jmxClient.send( mm, SendingMethod.THROUGH_ONE);
                f.addListener( this);
            }
            catch (NoPeerForTypeException e) {
                e.printStackTrace();
                // TODO logowanie i komunikat o błędzie
            }
        }
    }

    @Override
    public void operationComplete(ChannelFuture arg0) throws Exception {
        execute();
    }

}

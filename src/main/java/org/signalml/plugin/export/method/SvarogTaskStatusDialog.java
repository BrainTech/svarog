package org.signalml.plugin.export.method;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * Status dialog for a Svarog task.
 * 
 * @author Stanislaw Findeisen
 */
public interface SvarogTaskStatusDialog {
    void setMessageSource(MessageSourceAccessor source);
    void showDialog(boolean b);
}

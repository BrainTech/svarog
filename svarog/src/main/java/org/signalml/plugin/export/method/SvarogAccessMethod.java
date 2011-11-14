package org.signalml.plugin.export.method;


/**
 * Methods and tasks in Svarog core (facade for Svarog plugins).
 *
 * @author Stanislaw Findeisen
 */
public interface SvarogAccessMethod {
    void registerMethod(SvarogMethod method);
    void setMethodDescriptor(SvarogMethod method, SvarogMethodDescriptor methodDescriptor);
    SvarogMethodDescriptor getMethodDescriptor(SvarogMethod method);
    SvarogMethodConfigurer getConfigurer(SvarogMethodDescriptor descriptor);
    Object createData(SvarogMethodDescriptor descriptor);

    void addTask(SvarogTask task);
    void startTask(SvarogTask task);
    SvarogTaskStatusDialog getTaskStatusDialog(SvarogTask task);
}

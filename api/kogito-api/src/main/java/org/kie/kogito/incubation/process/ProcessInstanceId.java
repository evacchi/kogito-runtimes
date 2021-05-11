package org.kie.kogito.incubation.process;

public class ProcessInstanceId {
    private final ProcessId processId;
    private final String processInstanceId;

    public ProcessInstanceId(ProcessId processId, String processInstanceId) {
        this.processId = processId;
        this.processInstanceId = processInstanceId;
    }

    public ProcessId processId() {
        return processId;
    }

    public String processInstanceId() {
        return processInstanceId;
    }
}

package org.kie.kogito.incubation.process.workitem;

public class WorkItemId {
    private final String processId;
    private final String processInstanceId;
    private final String workItemId;

    public WorkItemId(String processId, String processInstanceId, String workItemId) {
        this.processId = processId;
        this.processInstanceId = processInstanceId;
        this.workItemId = workItemId;
    }

    public String processId() {
        return processId;
    }

    public String processInstanceId() {
        return processInstanceId;
    }

    public String workItemId() {
        return workItemId;
    }


}

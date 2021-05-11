package org.kie.kogito.incubation.process.workitem;

import org.kie.kogito.incubation.process.ProcessId;
import org.kie.kogito.incubation.process.ProcessInstanceId;

public class WorkItemId {
    private final ProcessInstanceId processInstanceId;
    private final String workItemId;

    public WorkItemId(ProcessInstanceId processInstanceId, String workItemId) {
        this.processInstanceId = processInstanceId;
        this.workItemId = workItemId;
    }

    public WorkItemId(String processId, String processInstanceId, String workItemId) {
        this.processInstanceId = new ProcessInstanceId(new ProcessId(processId), processInstanceId);
        this.workItemId = workItemId;
    }

    public ProcessInstanceId processInstanceId() {
        return processInstanceId;
    }

    public String workItemId() {
        return workItemId;
    }


}

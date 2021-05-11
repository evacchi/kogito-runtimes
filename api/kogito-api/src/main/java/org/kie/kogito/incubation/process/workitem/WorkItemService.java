package org.kie.kogito.incubation.process.workitem;

import org.kie.kogito.MapOutput;
import org.kie.kogito.Model;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;

import java.util.List;
import java.util.Optional;

public interface WorkItemService {
    /*all*/
    Optional<List<WorkItem>> get(String processId, String processInstanceId, Policy<?>... policies);
    Optional<WorkItem> get(WorkItemId workItemId, Policy<?>... policies);
    Optional<Model> transition(WorkItemId workItemId, Transition<?> transition);
    Optional<Model> abort(WorkItemId workItemId, Transition<?> transition);
    Optional<Model> complete(WorkItemId workItemId, Transition<?> transition);
    Optional<Model> save(WorkItemId workItemId, MapOutput model, Policy<?>... policies);
}

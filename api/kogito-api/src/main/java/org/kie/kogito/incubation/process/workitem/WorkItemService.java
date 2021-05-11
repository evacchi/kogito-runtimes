package org.kie.kogito.incubation.process.workitem;

import org.kie.kogito.MapOutput;
import org.kie.kogito.Model;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;

import java.util.Optional;

public interface WorkItemService {
    /*all*/
    void get();
    Optional<WorkItem> get(WorkItemId workItemId, Policy<?>... policies);
    void transition(WorkItemId workItemId);
    Optional<Model> abort(WorkItemId workItemId, Transition<?> transition);
    void complete(WorkItemId workItemId);
    Optional<Model> save(WorkItemId workItemId, MapOutput model, Policy<?>... policies);
}

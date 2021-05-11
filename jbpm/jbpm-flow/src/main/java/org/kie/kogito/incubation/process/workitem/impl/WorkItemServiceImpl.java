package org.kie.kogito.incubation.process.workitem.impl;

import org.jbpm.process.instance.impl.humantask.HumanTaskHelper;
import org.kie.kogito.Application;
import org.kie.kogito.MapOutput;
import org.kie.kogito.Model;
import org.kie.kogito.incubation.process.workitem.WorkItemId;
import org.kie.kogito.incubation.process.workitem.WorkItemService;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitem.Policies;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;

import java.util.Optional;

public class WorkItemServiceImpl implements WorkItemService {
    final Application application;
    private final Processes processes;

    public WorkItemServiceImpl(Application application) {
        this.application = application;
        this.processes = application.get(Processes.class);
    }


    @Override
    public void get() {

    }

    public Optional<WorkItem> get(WorkItemId workItemId, Policy<?>... policies) {
        return processes.processById(workItemId.processId())
                .instances()
                .findById(workItemId.processInstanceId(), ProcessInstanceReadMode.READ_ONLY)
                .map(pi -> pi.workItem(workItemId.workItemId(), policies));

    }

    @Override
    public void transition(WorkItemId workItemId) {

    }

    @Override
    public Optional<Model> abort(WorkItemId workItemId, Transition<?> transition) {
        return UnitOfWorkExecutor.executeInUnitOfWork(
                application.unitOfWorkManager(), () ->
                        processes.processById(workItemId.processId())
                                .instances()
                                .findById(workItemId.processInstanceId())
                                .map(pi -> {
                                    pi.transitionWorkItem(workItemId.workItemId(), transition);
                                    return pi.variables();
                                }));
    }

    @Override
    public void complete(WorkItemId workItemId) {

    }

    @Override
    public Optional<Model> save(WorkItemId workItemId, MapOutput model, Policy<?>... policies) {
        return UnitOfWorkExecutor.executeInUnitOfWork(
                application.unitOfWorkManager(), () ->
                        processes.processById(workItemId.processId())
                        .instances()
                        .findById(workItemId.processInstanceId())
                        .map(pi -> (Model) pi.updateWorkItem(workItemId.workItemId(), wi -> HumanTaskHelper.updateContent(wi, model), policies)));

    }
}

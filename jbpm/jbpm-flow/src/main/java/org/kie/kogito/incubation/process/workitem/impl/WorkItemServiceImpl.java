package org.kie.kogito.incubation.process.workitem.impl;

import java.util.List;
import java.util.Optional;

import org.jbpm.process.instance.impl.humantask.HumanTaskHelper;
import org.kie.kogito.Application;
import org.kie.kogito.MapOutput;
import org.kie.kogito.Model;
import org.kie.kogito.incubation.process.workitem.WorkItemId;
import org.kie.kogito.incubation.process.workitem.WorkItemService;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitem.Transition;
import org.kie.kogito.services.uow.UnitOfWorkExecutor;

public class WorkItemServiceImpl implements WorkItemService {
    final Application application;
    private final Processes processes;

    public WorkItemServiceImpl(Application application) {
        this.application = application;
        this.processes = application.get(Processes.class);
    }

    @Override
    public Optional<List<WorkItem>> get(String processId, String processInstanceId, Policy<?>... policies) {
        return processes.processById(processId)
                .instances()
                .findById(processInstanceId, ProcessInstanceReadMode.READ_ONLY)
                .map(pi -> pi.workItems(policies));

    }

    public Optional<WorkItem> get(WorkItemId workItemId, Policy<?>... policies) {
        return processes.processById(workItemId.processInstanceId().processId().processId())
                .instances()
                .findById(workItemId.processInstanceId().processInstanceId(), ProcessInstanceReadMode.READ_ONLY)
                .map(pi -> pi.workItem(workItemId.workItemId(), policies));

    }

    public Optional<Model> transition(WorkItemId workItemId, Transition<?> transition) {
        return UnitOfWorkExecutor.executeInUnitOfWork(
                application.unitOfWorkManager(), () -> processes.processById(workItemId.processInstanceId().processId().processId())
                        .instances()
                        .findById(workItemId.processInstanceId().processInstanceId())
                        .map(pi -> {
                            pi.transitionWorkItem(workItemId.workItemId(), transition);
                            return pi.variables();
                        }));

    }

    @Override
    public Optional<Model> abort(WorkItemId workItemId, Transition<?> transition) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> processes.processById(workItemId.processInstanceId().processId().processId())
                .instances()
                .findById(workItemId.processInstanceId().processInstanceId())
                .map(pi -> {
                    pi.transitionWorkItem(workItemId.workItemId(), transition);
                    return pi.variables();
                }));
    }

    @Override
    public Optional<Model> complete(WorkItemId workItemId, Transition<?> transition) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> processes.processById(workItemId.processInstanceId().processId().processId())
                .instances()
                .findById(workItemId.processInstanceId().processInstanceId())
                .map(pi -> {
                    pi.transitionWorkItem(workItemId.workItemId(), transition);
                    return pi.variables();
                }));
    }

    @Override
    public Optional<Model> save(WorkItemId workItemId, MapOutput model, Policy<?>... policies) {
        return UnitOfWorkExecutor.executeInUnitOfWork(
                application.unitOfWorkManager(), () -> processes.processById(workItemId.processInstanceId().processId().processId())
                        .instances()
                        .findById(workItemId.processInstanceId().processInstanceId())
                        .map(pi -> (Model) pi.updateWorkItem(workItemId.workItemId(), wi -> HumanTaskHelper.updateContent(wi, model), policies)));

    }
}

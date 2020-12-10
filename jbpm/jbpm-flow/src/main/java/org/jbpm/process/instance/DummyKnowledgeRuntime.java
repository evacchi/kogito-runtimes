/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.instance;

import java.util.Collection;
import java.util.Map;

import org.drools.kogito.core.common.InternalKnowledgeRuntime;
import org.jbpm.workflow.instance.impl.CodegenNodeInstanceFactoryRegistry;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.kogito.internal.KieBase;
import org.kie.kogito.internal.runtime.Environment;
import org.kie.kogito.internal.runtime.process.ProcessInstance;
import org.kie.kogito.internal.runtime.process.WorkItemManager;
import org.kie.kogito.jobs.JobsService;

/**
 * A severely limited implementation of the WorkingMemory interface.
 * It only exists for legacy reasons.
 */
class DummyKnowledgeRuntime extends DeprecatedMethods {

    private final Environment environment;
    private InternalProcessRuntime processRuntime;

    DummyKnowledgeRuntime(InternalProcessRuntime processRuntime) {
        this.processRuntime = processRuntime;
        this.environment = new Environment() {
            private NodeInstanceFactoryRegistry codegenNodeInstanceFactoryRegistry = new CodegenNodeInstanceFactoryRegistry();

            @Override
            public Object get(String identifier) {
                if (identifier.equals("NodeInstanceFactoryRegistry")) {
                    return codegenNodeInstanceFactoryRegistry;
                } else {
                    return null;
                }
            }

            @Override
            public void set(String identifier, Object object) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setDelegate(Environment delegate) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public InternalProcessRuntime getProcessRuntime() {
        return this.processRuntime;
    }

    @Override
    public WorkItemManager getWorkItemManager() {
        return this.processRuntime.getWorkItemManager();
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }
}

abstract class DeprecatedMethods implements InternalKnowledgeRuntime {

    @Override
    public JobsService getJobsService() {
        return null;
    }

    @Override
    public void startOperation() {

    }

    @Override
    public void endOperation() {

    }

    @Override
    public Agenda getAgenda() {
        return null;
    }

    @Override
    public ProcessInstance startProcess(String processId) {
        return null;
    }

    @Override
    public ProcessInstance startProcess(String processId, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public ProcessInstance createProcessInstance(String processId, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public ProcessInstance startProcess(String processId, AgendaFilter agendaFilter) {
        return null;
    }

    @Override
    public ProcessInstance startProcess(String processId, Map<String, Object> parameters, AgendaFilter agendaFilter) {
        return null;
    }

    @Override
    public ProcessInstance startProcessInstance(String processInstanceId) {
        return null;
    }

    @Override
    public ProcessInstance startProcessInstance(String processInstanceId, String trigger) {
        return null;
    }

    @Override
    public void signalEvent(String type, Object event) {

    }

    @Override
    public void signalEvent(String type, Object event, String processInstanceId) {

    }

    @Override
    public Collection<ProcessInstance> getProcessInstances() {
        return null;
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return null;
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId, boolean readonly) {
        return null;
    }

    @Override
    public void abortProcessInstance(String processInstanceId) {

    }

    @Override
    public KieBase getKieBase() {
        return null;
    }

    @Override
    public Object getGlobal(String name) {
        return null;
    }

    @Override
    public void insert(Object value) {

    }

    @Override
    public Object getFactHandle(Object workItemNodeInstance) {
        return null;
    }

    @Override
    public void update(Object factHandle, Object value) {

    }

    @Override
    public void delete(Object factHandle) {

    }
}
/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.jbpm.bpmn2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.io.Resource;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.KieSessionsPool;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnVariables;

import static java.util.stream.Collectors.toMap;

public class TestKieBase implements KieBase {

    final List<Process> processes;
    final Resource resource;
    final List<BpmnProcess> bpmnProcesses;

    public TestKieBase(Resource[] resources) {
        List<Resource> forbiddenTypes =
                Arrays.stream(resources)
                        .filter(r -> ! r.getSourcePath().toLowerCase().endsWith("bpmn")
                                && ! r.getSourcePath().toLowerCase().endsWith("bpmn2"))
                        .collect(Collectors.toList());
        if (!forbiddenTypes.isEmpty()) {
            throw new IllegalArgumentException("Given list of resources contained unsupported types " + forbiddenTypes);
        }

        this.resource = resources[0];
        bpmnProcesses = BpmnProcess.from(resource);
        this.processes = bpmnProcesses.stream().map(BpmnProcess::process).collect(Collectors.toList());
    }

    @Override
    public Collection<KiePackage> getKiePackages() {
        return null;
    }

    @Override
    public KiePackage getKiePackage(String packageName) {
        return null;
    }

    @Override
    public void removeKiePackage(String packageName) {

    }

    @Override
    public Rule getRule(String packageName, String ruleName) {
        return null;
    }

    @Override
    public void removeRule(String packageName, String ruleName) {

    }

    @Override
    public Query getQuery(String packageName, String queryName) {
        return null;
    }

    @Override
    public void removeQuery(String packageName, String queryName) {

    }

    @Override
    public void removeFunction(String packageName, String functionName) {

    }

    @Override
    public FactType getFactType(String packageName, String typeName) {
        return null;
    }

    @Override
    public Process getProcess(String processId) {
        return null;
    }

    @Override
    public void removeProcess(String processId) {

    }

    @Override
    public Collection<Process> getProcesses() {
        return this.processes;
    }

    @Override
    public KieSession newKieSession(KieSessionConfiguration conf, Environment environment) {
        return null;
    }

    @Override
    public KieSession newKieSession() {
        return new TestKieSession(bpmnProcesses);
    }

    @Override
    public KieSessionsPool newKieSessionsPool(int initialSize) {
        return null;
    }

    @Override
    public Collection<? extends KieSession> getKieSessions() {
        return null;
    }

    @Override
    public StatelessKieSession newStatelessKieSession(KieSessionConfiguration conf) {
        return null;
    }

    @Override
    public StatelessKieSession newStatelessKieSession() {
        return null;
    }

    @Override
    public Set<String> getEntryPointIds() {
        return null;
    }

    @Override
    public void addEventListener(KieBaseEventListener listener) {

    }

    @Override
    public void removeEventListener(KieBaseEventListener listener) {

    }

    @Override
    public Collection<KieBaseEventListener> getKieBaseEventListeners() {
        return null;
    }
}


class TestKieSession implements StatefulKnowledgeSession {

    private final Map<String, BpmnProcess> processes;

    public TestKieSession(Collection<BpmnProcess> processes) {
        this.processes = processes.stream().collect(toMap(BpmnProcess::id, Function.identity()));
    }

    @Override
    public KieRuntimeLogger getLogger() {
        return null;
    }

    @Override
    public void addEventListener(ProcessEventListener listener) {

    }

    @Override
    public void removeEventListener(ProcessEventListener listener) {

    }

    @Override
    public Collection<ProcessEventListener> getProcessEventListeners() {
        return null;
    }

    @Override
    public void addEventListener(RuleRuntimeEventListener listener) {

    }

    @Override
    public void removeEventListener(RuleRuntimeEventListener listener) {

    }

    @Override
    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        return null;
    }

    @Override
    public void addEventListener(AgendaEventListener listener) {

    }

    @Override
    public void removeEventListener(AgendaEventListener listener) {

    }

    @Override
    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return null;
    }

    @Override
    public <T> T execute(Command<T> command) {
        return null;
    }

    @Override
    public <T extends SessionClock> T getSessionClock() {
        return null;
    }

    @Override
    public void setGlobal(String identifier, Object value) {

    }

    @Override
    public Object getGlobal(String identifier) {
        return null;
    }

    @Override
    public Globals getGlobals() {
        return null;
    }

    @Override
    public Calendars getCalendars() {
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return null;
    }

    @Override
    public KieBase getKieBase() {
        return null;
    }

    @Override
    public void registerChannel(String name, Channel channel) {

    }

    @Override
    public void unregisterChannel(String name) {

    }

    @Override
    public Map<String, Channel> getChannels() {
        return null;
    }

    @Override
    public KieSessionConfiguration getSessionConfiguration() {
        return null;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public long getIdentifier() {
        return 0;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void submit(AtomicAction action) {

    }

    @Override
    public <T> T getKieRuntime(Class<T> cls) {
        return null;
    }

    @Override
    public ProcessInstance startProcess(String processId) {
        return startProcess(processId, Collections.emptyMap());
    }

    @Override
    public ProcessInstance startProcess(String processId, Map<String, Object> parameters) {
        BpmnProcess bpmnProcess = processes.get(processId);
        BpmnVariables bpmnVariables = BpmnVariables.create();
        bpmnVariables.fromMap(parameters);
        BpmnProcessInstance instance = (BpmnProcessInstance) bpmnProcess.createInstance(bpmnVariables);
        instance.start();
        return instance.internalGetProcessInstance();
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
    public WorkItemManager getWorkItemManager() {
        return new DefaultWorkItemManager();
    }

    @Override
    public JobsService getJobsService() {
        return null;
    }

    @Override
    public String getEntryPointId() {
        return null;
    }

    @Override
    public FactHandle insert(Object object) {
        return null;
    }

    @Override
    public void retract(FactHandle handle) {

    }

    @Override
    public void delete(FactHandle handle) {

    }

    @Override
    public void delete(FactHandle handle, FactHandle.State fhState) {

    }

    @Override
    public void update(FactHandle handle, Object object) {

    }

    @Override
    public void update(FactHandle handle, Object object, String... modifiedProperties) {

    }

    @Override
    public FactHandle getFactHandle(Object object) {
        return null;
    }

    @Override
    public Object getObject(FactHandle factHandle) {
        return null;
    }

    @Override
    public Collection<? extends Object> getObjects() {
        return null;
    }

    @Override
    public Collection<? extends Object> getObjects(ObjectFilter filter) {
        return null;
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles() {
        return null;
    }

    @Override
    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        return null;
    }

    @Override
    public long getFactCount() {
        return 0;
    }

    @Override
    public void halt() {

    }

    @Override
    public Agenda getAgenda() {
        return null;
    }

    @Override
    public EntryPoint getEntryPoint(String name) {
        return null;
    }

    @Override
    public Collection<? extends EntryPoint> getEntryPoints() {
        return null;
    }

    @Override
    public QueryResults getQueryResults(String query, Object... arguments) {
        return null;
    }

    @Override
    public LiveQuery openLiveQuery(String query, Object[] arguments, ViewChangedEventListener listener) {
        return null;
    }

    @Override
    public int fireAllRules() {
        return 0;
    }

    @Override
    public int fireAllRules(int max) {
        return 0;
    }

    @Override
    public int fireAllRules(AgendaFilter agendaFilter) {
        return 0;
    }

    @Override
    public int fireAllRules(AgendaFilter agendaFilter, int max) {
        return 0;
    }

    @Override
    public void fireUntilHalt() {

    }

    @Override
    public void fireUntilHalt(AgendaFilter agendaFilter) {

    }
}
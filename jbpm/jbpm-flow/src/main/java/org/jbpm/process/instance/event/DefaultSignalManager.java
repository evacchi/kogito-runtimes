/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance.event;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.core.KogitoWorkingMemory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.phreak.PropagationEntry;
import org.drools.kogito.core.common.InternalKnowledgeRuntime;
import org.drools.serialization.protobuf.ProtobufMessages.ActionQueue.Action;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.kie.kogito.internal.runtime.process.EventListener;
import org.kie.kogito.internal.runtime.process.ProcessInstance;
import org.kie.kogito.signal.SignalManager;

public class DefaultSignalManager implements SignalManager {
	
	private Map<String, List<EventListener>> processEventListeners = new ConcurrentHashMap<String, List<EventListener>>();
	private InternalKnowledgeRuntime kruntime;
	
	public DefaultSignalManager(InternalKnowledgeRuntime kruntime) {
		this.kruntime = kruntime;
	}
	
	@Override
    public void addEventListener(String type, EventListener eventListener) {
		List<EventListener> eventListeners = processEventListeners.get(type);
		//this first "if" is not pretty, but allows to synchronize only when needed
		if (eventListeners == null) {
			synchronized(processEventListeners){
				eventListeners = processEventListeners.get(type);
				if(eventListeners==null){
					eventListeners = new CopyOnWriteArrayList<EventListener>();
					processEventListeners.put(type, eventListeners);
				}
			}
		}		
		eventListeners.add(eventListener);
	}
	
	@Override
    public void removeEventListener(String type, EventListener eventListener) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				eventListeners.remove(eventListener);
				if (eventListeners.isEmpty()) {
					processEventListeners.remove(type);
					eventListeners = null;
				}
			}
		}
	}
	
	@Override
    public void signalEvent(String type, Object event) {
	    ((DefaultSignalManager) ((InternalProcessRuntime) kruntime.getProcessRuntime()).getSignalManager()).internalSignalEvent(type, event);
	}
	
	public void internalSignalEvent(String type, Object event) {
		if (processEventListeners != null) {
			List<EventListener> eventListeners = processEventListeners.get(type);
			if (eventListeners != null) {
				for (EventListener eventListener: eventListeners) {
					eventListener.signalEvent(type, event);
				}
			}
		}
	}
	@Override
    public void signalEvent(String processInstanceId, String type, Object event) {
		ProcessInstance processInstance = kruntime.getProcessInstance(processInstanceId);
		if (processInstance != null) {
		    processInstance.signalEvent(type, event);
		}
	}

    @Override
    public boolean accept(String type, Object event) {
        return processEventListeners.containsKey(type);
    }	
}

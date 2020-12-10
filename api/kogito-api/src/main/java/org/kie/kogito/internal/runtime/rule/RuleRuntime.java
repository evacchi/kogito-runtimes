/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.internal.runtime.rule;

import org.kie.api.runtime.rule.Agenda;

@Deprecated
public interface RuleRuntime {

    @Deprecated
    Agenda getAgenda();

    @Deprecated
    Object getGlobal(String name);

    @Deprecated
    void insert(Object value);

    @Deprecated
    Object getFactHandle(Object workItemNodeInstance);

    @Deprecated
    void update(Object factHandle, Object value);

    @Deprecated
    void delete(Object factHandle);

}

/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.internal.rules;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.rule.Match;
import org.kie.kogito.rules.listeners.AgendaListener;

public final class AgendaEventListenerAdapter implements AgendaEventListener {

    private final AgendaListener agendaListener;

    public AgendaEventListenerAdapter(AgendaListener agendaListener) {
        this.agendaListener = agendaListener;
    }

    @Override
    public void matchCreated(MatchCreatedEvent event) {
        agendaListener.matchCreated();
    }

    @Override
    public void matchCancelled(MatchCancelledEvent event) {
        agendaListener.matchCancelled();
    }

    @Override
    public void beforeMatchFired(BeforeMatchFiredEvent event) {
        Match match = event.getMatch();
        MatchImpl m = new MatchImpl(match.getRule(), match.getObjects());
        agendaListener.beforeMatchFired(m);
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Match match = event.getMatch();
        MatchImpl m = new MatchImpl(match.getRule(), match.getObjects());
        agendaListener.afterMatchFired(m);
    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        agendaListener.agendaGroupPopped();
    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        agendaListener.agendaGroupPushed();
    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {

    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {

    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {

    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {

    }
}
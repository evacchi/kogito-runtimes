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
 */

package org.kie.kogito.conf.feature;

/**
 * Centralize the features under development or not completed to be released, that could be enabled or disabled during
 * runtime or build time. After the features are completed, the toggle could be removed along with the code checking
 * this feature.
 * Using this class make things easier to track the features in different points of code facilitating the removal of
 * them.
 */
public class FeatureToggle {

    public static final String ENDPOINTS_SPRING_API_ENABLED = "endpoints.spring.api.enabled";

    private final FeatureToggleManager manager;

    public FeatureToggle() {
        this(new DefaultFeatureToggleManager());
    }

    public FeatureToggle(FeatureToggleManager manager) {
        this.manager = manager;
    }

    public boolean isEnabled(String featureKey){
        return manager.isEnabled(featureKey);
    }
}
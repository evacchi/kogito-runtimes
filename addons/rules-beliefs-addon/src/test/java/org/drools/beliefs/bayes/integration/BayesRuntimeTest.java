/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.beliefs.bayes.integration;

import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.runtime.BayesRuntimeImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BayesRuntimeTest {

    @Test
    public void testBayesRuntimeManager() throws Exception {
        GardenUnit garden = new GardenUnit();
        BayesRuntimeImpl<GardenUnit> bayes = BayesRuntimeImpl.of(GardenUnit.class);
        BayesInstance<GardenUnit> gardenInstance = bayes.createInstance(garden);
        gardenInstance.marginalize();
        garden.setSprinklerEvidence(1.0, 0.0);
        garden.setCloudyEvidence(1.0, 0.0);
        Garden result = garden.getGarden();
        System.out.println(result);
        assertNotNull(result);
    }
}
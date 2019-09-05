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

import java.io.InputStream;

import org.drools.beliefs.bayes.BayesInstance;
import org.drools.beliefs.bayes.example.GardenUnit;
import org.drools.beliefs.bayes.runtime.BayesRuntimeImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BayesRuntimeTest {

    @Test
    public void testBayesRuntimeManager() throws Exception {
        GardenUnit garden = new GardenUnit();
        String gardenPath = "/org/drools/beliefs/bayes/integration/Garden.xmlbif";
        InputStream is = this.getClass().getResourceAsStream(gardenPath);
        BayesRuntimeImpl<GardenUnit, Garden> bayes = BayesRuntimeImpl.of(is, Garden.class);
        BayesInstance<GardenUnit, Garden> gardenInstance = bayes.createInstance(garden);
        Garden result = gardenInstance.marginalize();
        System.out.println(result);
        assertNotNull(result);
    }
}

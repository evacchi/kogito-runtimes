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

package org.jbpm.process.core.impl;

import java.lang.reflect.InvocationTargetException;

public class XmlProcessDumperFactory {

    public static XmlProcessDumper newXmlProcessDumperFactory() {
        return getXmlProcessDumperFactoryService().newXmlProcessDumper();
    }

    public static XmlProcessDumperFactoryService getXmlProcessDumperFactoryService() {
        return LazyHolder.service;
    }

    private static class LazyHolder {
        private static final XmlProcessDumperFactoryService service;
        static {
            try {
                service = (XmlProcessDumperFactoryService) Thread.currentThread().getContextClassLoader().loadClass("org.jbpm.bpmn2.xml.XmlProcessDumperFactoryServiceImpl").getConstructor().newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new Error(e);
            }
        }
    }

    private XmlProcessDumperFactory() {
        // It is not allowed to create instances of util classes.
    }
}

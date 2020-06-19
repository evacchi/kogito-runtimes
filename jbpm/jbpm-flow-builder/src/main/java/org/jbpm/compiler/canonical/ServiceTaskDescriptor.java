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

package org.jbpm.compiler.canonical;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jbpm.process.core.ParameterDefinition;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import static java.util.stream.Collectors.joining;

public class ServiceTaskDescriptor {
    private final ClassLoader contextClassLoader;
    private final String interfaceName;
    private final String operationName;
    private final Map<String, String> parameters;
    private final WorkItemNode workItemNode;
    private final String mangledName;
    Class<?> cls;

    ServiceTaskDescriptor(WorkItemNode workItemNode, ClassLoader contextClassLoader) {
        this.workItemNode = workItemNode;
        interfaceName = (String) workItemNode.getWork().getParameter("Interface");
        operationName = (String) workItemNode.getWork().getParameter("Operation");
        this.contextClassLoader = contextClassLoader;

        NodeValidator.of("workItemNode", workItemNode.getName())
                .notEmpty("interfaceName", interfaceName)
                .notEmpty("operationName", operationName)
                .validate();

        parameters = serviceTaskParameters();

        mangledName = mangledHandlerName(interfaceName, operationName, parameters);
    }

    public String mangledName() {
        return mangledName;
    }

    private Map<String, String> serviceTaskParameters() {
        String type = (String) workItemNode.getWork().getParameter("ParameterType");
        Map<String, String> parameters = null;
        if (type != null) {
            if (isDefaultParameterType(type)) {
                type = inferParameterType(workItemNode.getName(), interfaceName, operationName, type);
            }

            parameters = Collections.singletonMap("Parameter", type);
        } else {
            parameters = new LinkedHashMap<>();

            for (ParameterDefinition def : workItemNode.getWork().getParameterDefinitions()) {
                parameters.put(def.getName(), def.getType().getStringType());
            }
        }
        return parameters;
    }


    // assume 1 single arg as above
    private String inferParameterType(String nodeName, String interfaceName, String operationName, String defaultType) {
        try {
            Class<?> i = contextClassLoader.loadClass(interfaceName);
            for (Method m : i.getMethods()) {
                if (m.getName().equals(operationName) && m.getParameterCount() == 1) {
                    return m.getParameterTypes()[0].getCanonicalName();
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(MessageFormat.format("Invalid work item \"{0}\": class not found for interfaceName \"{1}\"", nodeName, interfaceName));
        }
        throw new IllegalArgumentException(MessageFormat.format("Invalid work item \"{0}\": could not find a method called \"{1}\" in class \"{2}\"", nodeName, operationName, interfaceName));
    }

    private boolean isDefaultParameterType(String type) {
        return type.equals("java.lang.Object") || type.equals("Object");
    }

    private String mangledHandlerName(String interfaceName, String operationName, Map<String, String> parameters) {
        String simpleName = interfaceName.substring(interfaceName.lastIndexOf(".") + 1);

        // mangle dotted identifiers foo.bar.Baz into foo$bar$Baz
        // then concatenate the collection with $$
        // e.g. List.of("foo.bar.Baz", "qux.Quux") -> "foo$bar$Baz$$qux$Quux"
        String mangledParameterTypes =
                parameters.values().stream().map(s -> s.replace('.', '$'))
                        .collect(joining("$$"));

        return String.format("%s_%s_%s_Handler", simpleName, operationName, mangledParameterTypes);
    }


    public CompilationUnit generateHandlerClassForService() {
        CompilationUnit compilationUnit = new CompilationUnit("org.kie.kogito.handlers");

        compilationUnit.getTypes().add(classDeclaration());

        return compilationUnit;
    }


    public ClassOrInterfaceDeclaration classDeclaration() {
        ClassOrInterfaceDeclaration cls = new ClassOrInterfaceDeclaration()
                .setName(mangledName)
                .setModifiers(Modifier.Keyword.PUBLIC)
                .addImplementedType(WorkItemHandler.class.getCanonicalName());
        ClassOrInterfaceType serviceType = new ClassOrInterfaceType(null, interfaceName);
        FieldDeclaration serviceField = new FieldDeclaration()
                .addVariable(new VariableDeclarator(serviceType, "service"));
        cls.addMember(serviceField);

        // executeWorkItem method
        BlockStmt executeWorkItemBody = new BlockStmt();
        MethodDeclaration executeWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(void.class)
                .setName("executeWorkItem")
                .setBody(executeWorkItemBody)
                .addParameter(WorkItem.class.getCanonicalName(), "workItem")
                .addParameter(WorkItemManager.class.getCanonicalName(), "workItemManager");

        MethodCallExpr callService = new MethodCallExpr(new NameExpr("service"), operationName);

        for (Map.Entry<String, String> paramEntry : parameters.entrySet()) {
            MethodCallExpr getParamMethod = new MethodCallExpr(new NameExpr("workItem"), "getParameter").addArgument(new StringLiteralExpr(paramEntry.getKey()));
            callService.addArgument(new CastExpr(new ClassOrInterfaceType(null, paramEntry.getValue()), getParamMethod));
        }
        Expression results = null;
        List<DataAssociation> outAssociations = workItemNode.getOutAssociations();
        if (outAssociations.isEmpty()) {

            executeWorkItemBody.addStatement(callService);
            results = new NullLiteralExpr();
        } else {
            VariableDeclarationExpr resultField = new VariableDeclarationExpr()
                    .addVariable(new VariableDeclarator(new ClassOrInterfaceType(null, Object.class.getCanonicalName()), "result", callService));

            executeWorkItemBody.addStatement(resultField);

            results = new MethodCallExpr(new NameExpr("java.util.Collections"), "singletonMap")
                    .addArgument(new StringLiteralExpr(outAssociations.get(0).getSources().get(0)))
                    .addArgument(new NameExpr("result"));
        }

        MethodCallExpr completeWorkItem = new MethodCallExpr(new NameExpr("workItemManager"), "completeWorkItem")
                .addArgument(new MethodCallExpr(new NameExpr("workItem"), "getId"))
                .addArgument(results);

        executeWorkItemBody.addStatement(completeWorkItem);

        // abortWorkItem method
        BlockStmt abortWorkItemBody = new BlockStmt();
        MethodDeclaration abortWorkItem = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(void.class)
                .setName("abortWorkItem")
                .setBody(abortWorkItemBody)
                .addParameter(WorkItem.class.getCanonicalName(), "workItem")
                .addParameter(WorkItemManager.class.getCanonicalName(), "workItemManager");

        // getName method
        MethodDeclaration getName = new MethodDeclaration()
                .setModifiers(Modifier.Keyword.PUBLIC)
                .setType(String.class)
                .setName("getName")
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new StringLiteralExpr(interfaceName + "." + operationName))));
        cls.addMember(executeWorkItem)
                .addMember(abortWorkItem)
                .addMember(getName);

        return cls;
    }

}

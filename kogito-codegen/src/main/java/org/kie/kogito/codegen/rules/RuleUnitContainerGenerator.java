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

package org.kie.kogito.codegen.rules;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.kie.kogito.codegen.AbstractApplicationSection;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.rules.KieRuntimeBuilder;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitMemory;
import org.kie.kogito.rules.RuleUnits;

public class RuleUnitContainerGenerator extends AbstractApplicationSection {

    private final String packageName;
    private final String generatedFilePath;
    private final String targetCanonicalName;
    private final List<RuleUnitSourceClass> ruleUnits;
    private String targetTypeName;
    private DependencyInjectionAnnotator annotator;
    private List<BodyDeclaration<?>> factoryMethods = new ArrayList<>();

    public RuleUnitContainerGenerator(String packageName) {
        super("RuleUnits", "ruleUnits", RuleUnits.class);
        this.packageName = packageName;
        this.targetTypeName = "Module";
        this.targetCanonicalName = packageName + "." + targetTypeName;
        this.generatedFilePath = targetCanonicalName.replace('.', '/') + ".java";
        this.ruleUnits = new ArrayList<>();
    }

    public String generatedFilePath() {
        return generatedFilePath;
    }

    void addRuleUnit(RuleUnitSourceClass rusc) {
        ruleUnits.add(rusc);
        addRuleUnitFactoryMethod(rusc);
    }

    private MethodDeclaration genericFactory() {
        ClassOrInterfaceType returnType = new ClassOrInterfaceType(null, RuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, "T"));

        SwitchStmt switchStmt = new SwitchStmt();
        switchStmt.setSelector(new MethodCallExpr(new NameExpr("clazz"), "getCanonicalName"));

        for (RuleUnitSourceClass ruleUnit : ruleUnits) {
            SwitchEntry switchEntry = new SwitchEntry();
            switchEntry.getLabels().add(new StringLiteralExpr(ruleUnit.getRuleUnitClass().getCanonicalName()));
            switchEntry.getStatements().add(new ReturnStmt(new CastExpr(returnType, new ObjectCreationExpr()
                                                                                .setType(ruleUnit.targetCanonicalName()))));
            switchStmt.getEntries().add(switchEntry);
        }

        SwitchEntry defaultEntry = new SwitchEntry();
        defaultEntry.getStatements().add(new ThrowStmt(new ObjectCreationExpr().setType(UnsupportedOperationException.class.getCanonicalName())));
        switchStmt.getEntries().add(defaultEntry);

        return new MethodDeclaration()
                .addModifier(Modifier.Keyword.PUBLIC)
                .addTypeParameter(new TypeParameter("T").setTypeBound(NodeList.nodeList(new ClassOrInterfaceType(null, RuleUnitMemory.class.getCanonicalName()))))
                .setType(returnType)
                .setName("create")
                .addParameter(new ClassOrInterfaceType(null, "Class")
                                      .setTypeArguments(new ClassOrInterfaceType(null, "T")), "clazz")
                .setBody(new BlockStmt().addStatement(switchStmt));
    }

    private MethodDeclaration addRuleUnitFactoryMethod(RuleUnitSourceClass r) {
        MethodDeclaration methodDeclaration = new MethodDeclaration()
                .addModifier(Modifier.Keyword.PUBLIC)
                .setName("create" + r.targetTypeName())
                .setType(r.targetCanonicalName())
                .setBody(new BlockStmt().addStatement(new ReturnStmt(
                        new ObjectCreationExpr()
                                .setType(r.targetCanonicalName()))));
        this.factoryMethods.add(methodDeclaration);
        return methodDeclaration;
    }

    @Override
    public ClassOrInterfaceDeclaration classDeclaration() {

        NodeList<BodyDeclaration<?>> declarations = new NodeList<>();
        FieldDeclaration kieRuntimeFieldDeclaration = new FieldDeclaration();

        // declare field `ruleRuntimeBuilder`
        kieRuntimeFieldDeclaration
                .addVariable(new VariableDeclarator(
                        new ClassOrInterfaceType(null, KieRuntimeBuilder.class.getCanonicalName()),
                        "ruleRuntimeBuilder")
                                     .setInitializer(new ObjectCreationExpr()
                                                             .setType(CanonicalModelKieProject.PROJECT_RUNTIME_CLASS)));
        declarations.add(kieRuntimeFieldDeclaration);

        // declare method ruleRuntimeBuilder()
        MethodDeclaration methodDeclaration = new MethodDeclaration()
                .addModifier(Modifier.Keyword.PUBLIC)
                .setName("ruleRuntimeBuilder")
                .setType(KieRuntimeBuilder.class.getCanonicalName())
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new FieldAccessExpr(new ThisExpr(), "ruleRuntimeBuilder"))));

        declarations.add(methodDeclaration);

        declarations.addAll(factoryMethods);
        declarations.add(genericFactory());

        ClassOrInterfaceDeclaration cls = super.classDeclaration()
                .setMembers(declarations);

        cls.getMembers().sort(new BodyDeclarationComparator());

        return cls;
    }

    public static ClassOrInterfaceType ruleUnitType(String canonicalName) {
        return new ClassOrInterfaceType(null, RuleUnit.class.getCanonicalName())
                .setTypeArguments(new ClassOrInterfaceType(null, canonicalName));
    }

    public RuleUnitContainerGenerator withDependencyInjection(DependencyInjectionAnnotator annotator) {
        this.annotator = annotator;
        return this;
    }

    List<RuleUnitSourceClass> getRuleUnits() {
        return ruleUnits;
    }
}

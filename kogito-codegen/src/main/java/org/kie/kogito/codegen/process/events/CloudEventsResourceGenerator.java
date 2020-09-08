package org.kie.kogito.codegen.process.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;

import static com.github.javaparser.StaticJavaParser.parse;

public class CloudEventsResourceGenerator {

    private static final String RESOURCE_TEMPLATE = "/class-templates/events/CloudEventsListenerResource.java";
    private static final String CLASS_NAME = "CloudEventListenerResource";

    // even if we only support Quarkus for now, this will come in handy when we add SpringBoot support.
    private final DependencyInjectionAnnotator annotator;
    private final List<TriggerMetaData> triggers;

    public CloudEventsResourceGenerator(final List<ProcessExecutableModelGenerator> generators, final DependencyInjectionAnnotator annotator) {
        this.triggers = this.filterTriggers(generators);
        this.annotator = annotator;
    }

    // constructor shortcode used on unit tests
    CloudEventsResourceGenerator(final List<ProcessExecutableModelGenerator> generators) {
        this.triggers = this.filterTriggers(generators);
        this.annotator = new CDIDependencyInjectionAnnotator();
    }

    protected String getResourceTemplate() {
        return RESOURCE_TEMPLATE;
    }

    /**
     * Triggers used to generate the channels
     *
     * @return
     */
    List<TriggerMetaData> getTriggers() {
        return triggers;
    }

    /**
     * Gets the full class name in path format like <code>org/my/ns/Class.java</code>
     *
     * @return
     */
    public String generatedFilePath() {
        return String.format("%s/%s.java", ApplicationGenerator.DEFAULT_PACKAGE_NAME.replace(".", "/"), CLASS_NAME);
    }

    /**
     * Generates the source code for a CloudEventListenerResource
     *
     * @return
     */
    public String generate() {
        final CompilationUnit clazz = this.parseTemplate();
        final ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        template.setName(CLASS_NAME);

        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    /**
     * Filter TriggerMetadata to keep only  {@link org.jbpm.compiler.canonical.TriggerMetaData.TriggerType#ConsumeMessage}
     *
     * @param generators Process generators
     * @return filtered list
     */
    private List<TriggerMetaData> filterTriggers(final List<ProcessExecutableModelGenerator> generators) {
        if (generators != null) {
            final List<TriggerMetaData> filteredTriggers = new ArrayList<>();
            generators
                    .stream()
                    .filter(m -> m.generate().getTriggers() != null)
                    .forEach(m -> filteredTriggers.addAll(m.generate().getTriggers().stream()
                                                                  .filter(t -> TriggerMetaData.TriggerType.ConsumeMessage.equals(t.getType()))
                                                                  .collect(Collectors.toList())));
            return filteredTriggers;
        }
        return Collections.emptyList();
    }

    private CompilationUnit parseTemplate() {
        return parse(this.getClass().getResourceAsStream(getResourceTemplate())).setPackageDeclaration(ApplicationGenerator.DEFAULT_PACKAGE_NAME);
    }

}

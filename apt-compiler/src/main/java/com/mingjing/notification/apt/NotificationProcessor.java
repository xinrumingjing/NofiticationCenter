package com.mingjing.notification.apt;

import com.mingjing.notification.annotation.Notification;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Created by liukui on 2017/4/24.
 */

public class NotificationProcessor implements IProcessor {

    private static String packageName = "com.mingjing.notification.proxy";

    @Override
    public void process(RoundEnvironment environment, AnnotationProcessor annotationProcessor) {

        try {


            for (TypeElement element : ElementFilter.typesIn(environment.getElementsAnnotatedWith(Notification.class))) {

                processInterface(element, annotationProcessor);

//                if (!Utils.isValidClass(mAbstractProcessor.mMessager, element)) return;
                ClassName currentType = ClassName.get(element);

            }
        } catch (Exception e) {
        }
    }

    private void processInterface(TypeElement element, AnnotationProcessor annotationProcessor) {

        for (Element e : element.getEnclosedElements()) {
            if (e instanceof TypeElement) {
                processInterface((TypeElement) e, annotationProcessor);
            }
        }

        processSingleInterface(element, annotationProcessor);

    }

    private void processSingleInterface(TypeElement element, AnnotationProcessor annotationProcessor) {
        try {
            TypeSpec.Builder tb = TypeSpec.classBuilder(element.getSimpleName() + "_Proxy");
            packageName = element.getQualifiedName().toString();
            packageName = packageName.replace(".", "._");
            tb.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            tb.addSuperinterface(TypeName.get(element.asType()));
            tb.addField(FieldSpec.builder(Map.class, "mObservers", Modifier.PRIVATE, Modifier.FINAL).build());

            MethodSpec.Builder mb = MethodSpec.constructorBuilder();
            mb.addModifiers(Modifier.PUBLIC);
            mb.addParameter(Map.class, "observers");
            mb.addStatement("mObservers = observers");

            tb.addMethod(mb.build());

            for (Element e : element.getEnclosedElements()) {
                if (e instanceof ExecutableElement) {
                    ExecutableElement ee = (ExecutableElement) e;


                    MethodSpec.Builder builder = MethodSpec.methodBuilder(ee.getSimpleName().toString());

                    Set<Modifier> sm = new LinkedHashSet<>();
                    for (Modifier m : ee.getModifiers()) {
                        if (m != Modifier.ABSTRACT) {
                            sm.add(m);
                        }
                    }
                    builder.addModifiers(sm);

                    TypeMirror returnTm = ee.getReturnType();
                    builder.returns(TypeName.get(returnTm));

                    List<? extends VariableElement> variableElements = ee.getParameters();

                    List<String> vns = new ArrayList<>();
                    for (VariableElement ve : variableElements) {


                        Modifier[] ms = ve.getModifiers().toArray(new Modifier[]{});
                        builder.addParameter(ParameterSpec.builder(TypeName.get(ve.asType()), ve.getSimpleName().toString(), ms).build());

                        vns.add(ve.getSimpleName().toString());
                    }

                    List<? extends TypeMirror> thrownTypes = ee.getThrownTypes();
                    for (TypeMirror m : thrownTypes) {
                        builder.addException(TypeName.get(m));
                    }

                    CodeBlock.Builder cb = CodeBlock.builder();

                    StringBuilder statementBuilder = new StringBuilder();
                    statementBuilder.append("for (Object item : mObservers.keySet()) {");
                    statementBuilder.append("\n   if (item instanceof ");
                    statementBuilder.append(element.getQualifiedName());
                    statementBuilder.append(") {\n      ((");
                    statementBuilder.append(element.getQualifiedName());
                    statementBuilder.append(")");
                    statementBuilder.append("item).");
                    statementBuilder.append(ee.getSimpleName());
                    statementBuilder.append("(");
                    boolean hasParameters = false;
                    for (String s : vns) {
                        statementBuilder.append(s);
                        statementBuilder.append(",");
                        hasParameters = true;
                    }
                    if (hasParameters) {
                        statementBuilder.deleteCharAt(statementBuilder.length() - 1);
                    }
                    statementBuilder.append(");\n      }\n }\n");
                    cb.add(statementBuilder.toString());

                    builder.addCode(cb.build());

                    tb.addMethod(builder.build());

                }
            }

            JavaFile javaFile = JavaFile.builder(packageName, tb.build()).build();
            javaFile.writeTo(annotationProcessor.mFiler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

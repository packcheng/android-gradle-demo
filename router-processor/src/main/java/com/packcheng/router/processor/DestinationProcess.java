package com.packcheng.router.processor;

import com.google.auto.service.AutoService;
import com.packcheng.router.annotations.Destination;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * {@link com.packcheng.router.annotations.Destination}的注解处理器
 *
 * @author packcheng <a href="mailto:packcheng_jo@outlook.com">Contact me.</a>
 * @version 1.0
 * @since 2022/4/4 18:01
 */
@AutoService(Processor.class)
public class DestinationProcess extends AbstractProcessor {
    private static final String TAG = "DestinationProcess";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 避免多次调用process
        if (roundEnv.processingOver()) {
            return false;
        }

        System.out.println(TAG + ">>> process start ...");

        // 获取所有标记了@Destination注解的 类的信息
        Set<? extends Element> allDestinationElements = roundEnv.getElementsAnnotatedWith(Destination.class);
        System.out.println(TAG + ">>> all Destination elements count = " + allDestinationElements.size());

        // 未收集到@Destination注解信息的时候，跳过后续的处理
        if (allDestinationElements.size() < 1) {
            return false;
        }

        ClassName map = ClassName.get("java.util", "Map");
        ClassName hashMap = ClassName.get("java.util", "HashMap");
        ClassName str = ClassName.get("java.lang", "String");
        ParameterizedTypeName mapOfStrStr = ParameterizedTypeName.get(map, str, str);

        MethodSpec.Builder getBuilder = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(mapOfStrStr)
                .addStatement("$T mapping = new $T<>()", mapOfStrStr, hashMap);

        // 遍历所有 @Destination 注解信息，挨个获取详细信息
        for (Element element : allDestinationElements) {
            final TypeElement typeElement = (TypeElement) element;

            // 尝试在当前类中获取 @Destination注解信息
            final Destination destination = typeElement.getAnnotation(Destination.class);

            if (null == destination) {
                continue;
            }

            final String url = destination.url();
            final String description = destination.description();
            final String realPath = typeElement.getQualifiedName().toString();

            getBuilder.addStatement("mapping.put($S,$S)", url, realPath);

            System.out.println(TAG + ">>> url= " + url);
            System.out.println(TAG + ">>> description= " + description);
            System.out.println(TAG + ">>> realPath= " + realPath);
        }

        getBuilder.addStatement("return mapping");
        TypeSpec routerMappingClass = TypeSpec.classBuilder("RouterMapping_" + System.currentTimeMillis())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getBuilder.build())
                .build();

        JavaFile javaFile = JavaFile.builder("com.packcheng.routerapp", routerMappingClass)
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(TAG + ">>> process finished ...");

        return false;
    }

    /**
     * 告诉编译器当前处理器可以处理的注解类型
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Destination.class.getCanonicalName());
    }
}

package com.packcheng.router.processor;

import static com.packcheng.router.util.Consts.KEY_DOC_DESCRIPTION;
import static com.packcheng.router.util.Consts.KEY_DOC_REAL_PATH;
import static com.packcheng.router.util.Consts.KEY_DOC_URL;
import static com.packcheng.router.util.Consts.KEY_ROOT_PROJECT_DIR;
import static com.packcheng.router.util.Consts.METHOD_GET;
import static com.packcheng.router.util.Consts.NAME_OF_FILE_MAPPING;
import static com.packcheng.router.util.Consts.NAME_OF_FOLDER_MAPPING_DOC;
import static com.packcheng.router.util.Consts.NAME_OF_JSON;
import static com.packcheng.router.util.Consts.NAME_OF_MAPPING;
import static com.packcheng.router.util.Consts.NO_ROOT_PROJECT_DIR_TIPS;
import static com.packcheng.router.util.Consts.PACKAGE_OF_GENERATE_DOCS;
import static com.packcheng.router.util.Consts.PACKAGE_OF_GENERATE_FILE;
import static com.packcheng.router.util.Consts.WARNING_TIPS;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.auto.service.AutoService;
import com.packcheng.router.annotations.Destination;
import com.packcheng.router.util.Logger;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;

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

    Filer mFiler;
    Logger logger;
    private Writer docWriter;       // Writer used for write doc
    private String rootDir;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        logger = new Logger(processingEnv.getMessager());

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            rootDir = options.get(KEY_ROOT_PROJECT_DIR);
        }

        if (StringUtils.isNotEmpty(rootDir)) {
            logger.info("The user has configuration the root_project_dir, it was [" + rootDir + "]");
        } else {
            logger.error(NO_ROOT_PROJECT_DIR_TIPS);
            throw new RuntimeException("ZbcRouter::Compiler >>> No root_project_dir, for more information, look at gradle log.");
        }

        try {
            docWriter = mFiler.createResource(
                    StandardLocation.SOURCE_OUTPUT,
                    PACKAGE_OF_GENERATE_DOCS,
                    "arouter-map-of-" + System.currentTimeMillis() + ".json"
            ).openWriter();
        } catch (IOException e) {
            logger.error("Create doc writer failed, because " + e.getMessage());
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 避免多次调用process
        if (roundEnv.processingOver()) {
            return false;
        }

        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Destination.class);
            try {
                logger.info(TAG + ">>> process start ...");
                this.parseRoutes(routeElements);
            } catch (Exception e) {
                logger.error(e);
            }
            System.out.println(TAG + ">>> process finished ...");
            return true;
        }

        return false;
    }

    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        // 未收集到@Destination注解信息的时候，跳过后续的处理
        if (CollectionUtils.isEmpty(routeElements)) {
            return;
        }

        ClassName map = ClassName.get(Map.class);
        ClassName hashMap = ClassName.get(HashMap.class);
        ClassName str = ClassName.get(String.class);
        ParameterizedTypeName mapOfStrStr = ParameterizedTypeName.get(map, str, str);

        MethodSpec.Builder getBuilder = MethodSpec.methodBuilder(METHOD_GET)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(mapOfStrStr)
                .addStatement("$T mapping = new $T<>()", mapOfStrStr, hashMap);

        List<Map<String, String>> docSource = new ArrayList<>();
        Map<String, String> routerInfoMap;

        // 遍历所有 @Destination 注解信息，挨个获取详细信息
        for (Element element : routeElements) {
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

            routerInfoMap = new HashMap<>();
            routerInfoMap.put(KEY_DOC_URL, url);
            routerInfoMap.put(KEY_DOC_DESCRIPTION, description);
            routerInfoMap.put(KEY_DOC_REAL_PATH, realPath);
            docSource.add(routerInfoMap);

            logger.info(TAG + ">>> url= " + url);
            logger.info(TAG + ">>> description= " + description);
            logger.info(TAG + ">>> realPath= " + realPath);
        }

        getBuilder.addStatement("return mapping");

        // Output route doc
        final String jsonDoc = JSON.toJSONString(docSource, SerializerFeature.PrettyFormat);
        docWriter.append(jsonDoc);
        docWriter.flush();
        docWriter.close();
        genDocOnRootDir(jsonDoc);

        // Write provider into disk
        JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(NAME_OF_MAPPING + System.currentTimeMillis())
                        .addJavadoc(WARNING_TIPS)
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(getBuilder.build())
                        .build())
                .build().writeTo(mFiler);
    }

    /**
     * 在根目录创建生成json文档
     *
     * @param jsonDoc 需要生成文档的json格式字符串
     */
    private void genDocOnRootDir(String jsonDoc) throws IOException {
        if (StringUtils.isEmpty(jsonDoc)
                || StringUtils.isEmpty(rootDir)) {
            logger.error("Empty rootDir or json info");
            return;
        }

        File rootFile = new File(rootDir);
        if (!rootFile.exists()) {
            logger.error("RootDir not exits");
            return;
        }

        File jsonFileDir = new File(rootFile, NAME_OF_FOLDER_MAPPING_DOC);
        if (!jsonFileDir.exists()) {
            jsonFileDir.mkdirs();
        }

        final String jsonFileName = NAME_OF_FILE_MAPPING + System.currentTimeMillis() + NAME_OF_JSON;
        File jsonFile = new File(jsonFileDir, jsonFileName);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(jsonFile));
        bufferedWriter.write(jsonDoc, 0, jsonDoc.length());
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    /**
     * 告诉编译器当前处理器可以处理的注解类型
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Destination.class.getCanonicalName());
    }
}

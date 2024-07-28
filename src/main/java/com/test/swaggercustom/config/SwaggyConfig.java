package com.test.swaggercustom.config;

import com.test.swaggercustom.utils.ControllerMethodOrderUtil;
import com.test.swaggercustom.utils.MethodPath;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.*;

@Configuration
public class SwaggyConfig {

    private final ApplicationContext applicationContext;

    public SwaggyConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public OpenApiCustomizer sortOperationsCustomizer() {
        return openApi -> {
            Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);
            List<MethodPath> sortedMethodPathsOrder = new LinkedList<>();

            controllers.values().forEach(controller -> {
                Class<?> controllerClass = controller.getClass();
                List<Method> orderedMethods = ControllerMethodOrderUtil.getOrderedMethods(controllerClass);

                orderedMethods.forEach(method -> {
                    MethodPath methodPath = ControllerMethodOrderUtil.getMethodPath(controllerClass, method);
                    sortedMethodPathsOrder.add(methodPath);
                });
            });

            System.out.println("\n[sortedMethodPathsOrder]");
            for (MethodPath methodPath : sortedMethodPathsOrder) {
                System.out.println(methodPath.getHttpMethod() + " " + Arrays.toString(methodPath.getPath()));
            }

            Paths sortedPaths = new Paths();

            // PathItem을 관리할 Map
            Map<String, PathItem> pathToPathItemMap = new LinkedHashMap<>();

            sortedMethodPathsOrder.forEach(methodPath -> {
                for (String path : methodPath.getPath()) {
//                    PathItem pathItem = openApi.getPaths().get(path);
//                    if (pathItem == null) {
//                        pathItem = new PathItem();
//                    }
//
//                    switch (methodPath.getHttpMethod()) {
//                        case "GET":
//                            pathItem.setGet(new Operation());
//                            break;
//                        case "POST":
//                            pathItem.setPost(new Operation());
//                            break;
//                        case "PUT":
//                            pathItem.setPut(new Operation());
//                            break;
//                        case "DELETE":
//                            pathItem.setDelete(new Operation());
//                            break;
//                        // 필요한 경우 다른 HTTP 메소드도 추가
//                        case "REQUEST":
//                            // 스웨거에는 Requset가 없다,,
//                            break;
//                    }
//                    sortedPaths.addPathItem(path, pathItem);

                    PathItem pathItem = pathToPathItemMap.computeIfAbsent(path, k -> new PathItem());
                    switch (methodPath.getHttpMethod()) {
                        case "GET":
                            if (pathItem.getGet() == null) {
                                pathItem.setGet(new Operation());
                            }
                            break;
                        case "POST":
                            if (pathItem.getPost() == null) {
                                pathItem.setPost(new Operation());
                            }
                            break;
                        case "PUT":
                            if (pathItem.getPut() == null) {
                                pathItem.setPut(new Operation());
                            }
                            break;
                        case "DELETE":
                            if (pathItem.getDelete() == null) {
                                pathItem.setDelete(new Operation());
                            }
                            break;
                        case "REQUEST":
                            // Swagger에는 "REQUEST" 메소드가 없음
                            break;
                    }

                    // 업데이트된 PathItem을 map에 추가
                    pathToPathItemMap.put(path, pathItem);
                }
            });

            System.out.println("[sortedPaths to pathToPathItemMap]");
            // 정렬된 PathItem을 sortedPaths에 추가
            pathToPathItemMap.forEach((path, pathItem) -> {
                sortedPaths.addPathItem(path, pathItem);
                System.out.println(path + " " + pathItem);
            });

            openApi.setPaths(sortedPaths);
        };
    }
}


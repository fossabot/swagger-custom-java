package com.test.swaggercustom.utils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerMethodOrderUtil {
    public static List<Method> getOrderedMethods(Class<?> controllerClass) {
        try {
            // ASM을 사용하여 클래스의 메서드 순서를 읽어옴
            ClassReader classReader = new ClassReader(controllerClass.getName());
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            // 원래 순서대로 메서드 이름과 htttp 메소드 리스트 생성
            List<MethodDescriptor> methodDescriptorsInOrder = classNode.methods.stream()
                    .map(methodNode -> new MethodDescriptor(methodNode.name, getHttpMethodFromAnnotations(methodNode)))
                    .toList();

            System.out.println("\n[Original method names order]");
            for (MethodDescriptor methodDescriptor : methodDescriptorsInOrder) {
                System.out.println(methodDescriptor.getHttpMethod() + " " + methodDescriptor.getMethodName());
            }

            // Reflection을 사용하여 메서드 리스트를 가져옴
            Method[] methods = controllerClass.getDeclaredMethods();

            // 메서드를 원래 순서에 따라 정렬
            List<Method> sortedMethods = Arrays.stream(methods)
                    .sorted((m1, m2) -> {
                        MethodDescriptor md1 = new MethodDescriptor(m1.getName(), getHttpMethod(m1));
                        MethodDescriptor md2 = new MethodDescriptor(m2.getName(), getHttpMethod(m2));
                        int index1 = methodDescriptorsInOrder.indexOf(md1);
                        int index2 = methodDescriptorsInOrder.indexOf(md2);
                        return Integer.compare(index1, index2);
                    })
                    .collect(Collectors.toList());

            sortedMethods.forEach(method -> System.out.println("Sorted method: " + getHttpMethod(method) + " " + method.getName()));

            return sortedMethods;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read class with ASM", e);
        }
    }

    // asm을 사용해 http 메소드 정보 읽어오기
    private static String getHttpMethodFromAnnotations(MethodNode methodNode) {
        String httpMethod = "";

        if (methodNode.visibleAnnotations != null) {
            for (AnnotationNode annotationNode : methodNode.visibleAnnotations) {
                switch (annotationNode.desc) {
                    case "Lorg/springframework/web/bind/annotation/GetMapping;":
                        httpMethod = "GET";
                        break;
                    case "Lorg/springframework/web/bind/annotation/PostMapping;":
                        httpMethod = "POST";
                        break;
                    case "Lorg/springframework/web/bind/annotation/PutMapping;":
                        httpMethod = "PUT";
                        break;
                    case "Lorg/springframework/web/bind/annotation/DeleteMapping;":
                        httpMethod = "DELETE";
                        break;
                    // 필요한 경우 다른 HTTP 메소드 어노테이션도 추가
                    case "Lorg/springframework/web/bind/annotation/RequestMapping;":
                        httpMethod = "REQUEST";
                }
            }
        }

        return httpMethod;
    }

    // reflection을 사용해 http 메소드 정보 읽어오기
    private static String getHttpMethod(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            return "GET";
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            return "POST";
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            return "PUT";
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            return "DELETE";
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMethod[] methods = method.getAnnotation(RequestMapping.class).method();
            // GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;
            if (methods.length > 0) {
                return methods[0].name();
            } else {
                return "REQUEST";
            }
        }

        return "";
    }

    // reflection을 사용해 http 메소드와 path 정보 읽어오기
    public static MethodPath getMethodPath(Class<?> controllerClass, Method method) {
        RequestMapping classRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
        String[] classPaths = (classRequestMapping != null) ? classRequestMapping.value() : new String[]{""};

        String[] paths = new String[0];
        String httpMethod = "";

        if (method.isAnnotationPresent(GetMapping.class)) {
            paths = method.getAnnotation(GetMapping.class).value();
            httpMethod = "GET";
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            paths = method.getAnnotation(PostMapping.class).value();
            httpMethod = "POST";
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            paths = method.getAnnotation(PutMapping.class).value();
            httpMethod = "PUT";
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            paths = method.getAnnotation(DeleteMapping.class).value();
            httpMethod = "DELETE";
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            paths = method.getAnnotation(RequestMapping.class).value();
            RequestMethod[] methods = method.getAnnotation(RequestMapping.class).method();
            if (methods.length > 0) {
                httpMethod = methods[0].name();
            } else {
                httpMethod = "REQUEST";
            }
        }

        if (paths.length == 0) {
            paths = new String[]{""}; // 메서드 레벨 경로가 없을 경우 빈 문자열로 설정
        }

        String[] finalMethodPaths = paths;
        String finalHttpMethod = httpMethod;

        return new MethodPath(finalHttpMethod, Arrays.stream(classPaths)
                .flatMap(classPath -> Arrays.stream(finalMethodPaths)
                        .map(path -> classPath + path)
                ).toArray(String[]::new)
        );
    }
}

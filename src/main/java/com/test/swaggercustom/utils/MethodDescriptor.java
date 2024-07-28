package com.test.swaggercustom.utils;

import java.util.Objects;

public class MethodDescriptor {
    private final String methodName;
    private final String httpMethod;

    public MethodDescriptor(String methodName, String httpMethod) {
        this.methodName = methodName;
        this.httpMethod = httpMethod;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodDescriptor that = (MethodDescriptor) o;
        return Objects.equals(methodName, that.methodName) && Objects.equals(httpMethod, that.httpMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, httpMethod);
    }
}

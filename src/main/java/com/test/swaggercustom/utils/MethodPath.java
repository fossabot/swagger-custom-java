package com.test.swaggercustom.utils;

public class MethodPath {
    private final String httpMethod;
    private final String[] path;

    public MethodPath(String httpMethod, String[] path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public String[] getPath() {
        return path;
    }

    public String getHttpMethod() {
        return httpMethod;
    }
}

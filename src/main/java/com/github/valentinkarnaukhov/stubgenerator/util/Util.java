package com.github.valentinkarnaukhov.stubgenerator.util;

public class Util {

    public static void validateNotNull(Object o) {
        if (o == null) {
            throw new RuntimeException("Should be not null");
        }
    }
}

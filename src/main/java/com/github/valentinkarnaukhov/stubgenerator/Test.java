package com.github.valentinkarnaukhov.stubgenerator;

import com.google.common.base.MoreObjects;
import com.google.common.io.Resources;

import java.io.File;

public class Test {

    public void test() {
        System.out.println(new File(Test.class.getClassLoader().getResource("").getFile()));
        System.out.println(ClassLoader.getSystemResource("") + "::ClassLoader.getSystemResource(\"\")");
        System.out.println(ClassLoader.getSystemClassLoader().getResource("") + "::ClassLoader.getSystemClassLoader().getResource(\"\")");
        System.out.println(Test.class.getClassLoader().getResource("") + "::Test.class.getClassLoader().getResource(\"\")");
        System.out.println(Thread.currentThread().getContextClassLoader().getResource("") + "::Thread.currentThread().getContextClassLoader().getResource(\"\")");
    }
}

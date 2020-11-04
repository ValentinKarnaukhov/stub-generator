package com.github.valentinkarnaukhov.stubgeneratorv2.model;

import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public interface Item {

    String getType();

    String getName();

    String getSetter();

    String getGetter();

    boolean isPrimitive();

    boolean isCollection();

    String getCollectionType();

    List<Item> getFields();

}

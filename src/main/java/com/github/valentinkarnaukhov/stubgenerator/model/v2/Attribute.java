package com.github.valentinkarnaukhov.stubgenerator.model.v2;

/**
 * @author Valentin Karnaukhov
 */
public interface Attribute {

    String getName();

    String getType();

    String getItemType();

    boolean isPrimitive();

    boolean isCollection();

}

package com.github.valentinkarnaukhov.stubgeneratorv2.model;

/**
 * @author Valentin Karnaukhov
 */
public interface EmbeddedAttribute extends Item {

    String getName();

    boolean isCollection();

    String getItemType();

    String getSetter();

    String getGetter();

}

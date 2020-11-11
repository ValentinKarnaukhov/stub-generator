package com.github.valentinkarnaukhov.stubgeneratorv2.parser;

/**
 * @author Valentin Karnaukhov
 */
public interface Parser<I, T> {

    I parse(T input);

}

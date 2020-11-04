package com.github.valentinkarnaukhov.stubgeneratorv2.model;

import java.util.Map;

/**
 * @author Valentin Karnaukhov
 */
public interface Node extends Item{

    Map<String, Node> getChildren();

}

package com.github.valentinkarnaukhov.stubgeneratorv2.model;

import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public interface Node extends Item{

    List<Node> getChildren();

}

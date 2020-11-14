package com.github.valentinkarnaukhov.stubgeneratorv3.processor;

import com.github.valentinkarnaukhov.stubgenerator.model.TagTemplate;

import java.util.List;

/**
 * @author Valentin Karnaukhov
 */
public interface SpecProcessor {

    List<TagTemplate> process();

}

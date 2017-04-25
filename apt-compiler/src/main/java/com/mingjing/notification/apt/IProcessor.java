package com.mingjing.notification.apt;

import javax.annotation.processing.RoundEnvironment;

/**
 * Created by liukui on 2017/4/13.
 */

public interface IProcessor {
     void process(RoundEnvironment environment, AnnotationProcessor annotationProcessor);
}

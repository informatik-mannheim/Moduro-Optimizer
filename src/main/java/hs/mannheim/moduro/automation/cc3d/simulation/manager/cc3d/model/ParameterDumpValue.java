package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ParameterDumpValue {
    enum ParameterDumpValueType {BOOLEAN, STRING, DOUBLE, INTEGER}
    String key();
    ParameterDumpValueType type();
}

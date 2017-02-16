package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.util;


import java.lang.reflect.Field;
import java.util.Map;

public interface ReflectionHelper {
    void setParameterDumpFieldValue(Field field, Map<String, String> parsedBlock) throws IllegalAccessException;
}

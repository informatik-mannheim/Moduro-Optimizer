package hs.mannheim.moduro.automation.cc3d.simulation.manager.cc3d.model;


import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ParameterDumpBaseComponent {

    public ParameterDumpBaseComponent(Map<String, String> parsedBlock) throws IllegalAccessException {
        Field[] blockFields = this.getClass().getDeclaredFields();
        for (Field field : blockFields) {
            field.setAccessible(true);
            List<Annotation> declaredAnnotations = Arrays.asList(field.getDeclaredAnnotations());

            if (declaredAnnotations.size() == 0) {
                System.out.println(String.format("Ignoring field: %s of class %s", field.getName(),
                        getClass().getName()));
            }

            Collection<Annotation> filteredAnnotation = Collections2.filter(declaredAnnotations,
                    new Predicate<Annotation>() {
                        @Override
                        public boolean apply(Annotation annotation) {
                            return ParameterDumpValue.class.isInstance(annotation);
                        }
                    });

            if (filteredAnnotation.size() == 0) {
                System.out.println(String.format("Field %s of class %s has not ParameterDumpAnnotation. ignoring",
                        field.getName(), getClass().getName()));

                continue;
            }

            ParameterDumpValue parameterDumpValueAnnotation
                    = field.getDeclaredAnnotation(ParameterDumpValue.class);
            // I noticed some parameterDump.dat files got different keys for identical values. So we have to
            // support multiple keys for a parameter. When the parameter is exported we will use the first available parameter.
            // so be careful if you import the values again (which is currently not the case...)
            String hashmapAnnotationKey = parameterDumpValueAnnotation.key();
            String[] hashMapKeys = StringUtils.split(hashmapAnnotationKey.trim(), ",");
            String hashMapValue = StringUtils.EMPTY;
            for (String mapKey : hashMapKeys) {
                if (parsedBlock.containsKey(mapKey)) {
                    hashMapValue = parsedBlock.get(mapKey);
                }
            }

            switch (parameterDumpValueAnnotation.type()) {
                case BOOLEAN:
                    field.set(this, Boolean.parseBoolean(hashMapValue));
                    break;
                case DOUBLE:
                    field.set(this, NumberUtils.createDouble(hashMapValue));
                    break;
                case INTEGER:
                    field.set(this, Integer.parseInt(hashMapValue));
                    break;
                case STRING:
                    field.set(this, hashMapValue);
                    break;
                default:
                    throw new IllegalStateException("Type is not allowed for field: " + field.getName());
            }
            field.setAccessible(false);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            String value = StringUtils.EMPTY;

            ParameterDumpValue declaredAnnotation = declaredField.getDeclaredAnnotation(ParameterDumpValue.class);
            try {
                declaredField.setAccessible(true);
                Object fieldValue = declaredField.get(this);
                switch (declaredAnnotation.type()) {
                    case BOOLEAN:
                        // replace first letter with upper case
                        fieldValue = fieldValue.toString().substring(0, 1).toUpperCase()
                                + fieldValue.toString().substring(1);
                        break;
                    case STRING:
                        break;
                    case DOUBLE:
                        // some of the parameters look like "3e-05"
                        // using String.format or DecimalFormat would break this
                        fieldValue = String.valueOf(fieldValue);
                        break;
                    case INTEGER:
                        fieldValue = String.valueOf(fieldValue);
                }

                String parameterKeyString = declaredAnnotation.key();
                String[] availableParameterKeys = StringUtils.split(parameterKeyString, ",");
                if (availableParameterKeys.length == 0) {
                    throw new RuntimeException("Parameter " + declaredField.getName() +
                            " is not declared properly by ParameterDumpValueAnnotation");
                }

                // Please see ParameterDumpExecConfig class about details related to the multiple keys problem
                // We ignore this problem at this moment and we will always use the first key set in the annotation
                String targetKey = availableParameterKeys[0];
                sb.append(targetKey)
                        .append(": ")
                        .append(fieldValue)
                        .append("\n");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                declaredField.setAccessible(false);
            }
        }
        return sb.toString();
    }

    ParameterDumpBaseComponent() {
        System.out.println("Called Default Constructor of ParameterDumpBaseComponent()");
    }
}

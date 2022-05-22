package jvmdog.core.utils;

import jvmti.JVMTIAPI;

public class DogUtils {
    
    public static void init() {
        
    }

    public static <T> T getInstance(Class<T> clazz) {
        String className = clazz.getName();
        T[] ts = getInstances(className);
        if(ts == null || ts.length ==0) {
            return null;
        } else {
            return ts[0];
        }
    }
    
    public static <T> T[] getInstances(String className) {
        className = className.replace('.', '/');
        className = String.format("L%s;", className);
        Object[] objs = JVMTIAPI.queryObjects2(className);
        return (T[])objs;
    }
}

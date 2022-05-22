package jvmdog.core.utils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Set;

public class InstrumentationUtils {
    public static void retransformClasses(Instrumentation inst, ClassFileTransformer transformer, Set<Class<?>> classes){
        if(inst==null || transformer ==null){
            return;
        }
        if(classes ==null || classes.size() ==0){
            return;
        }
        
        try {
            inst.addTransformer(transformer, true);
            int size = classes.size();
            Class<?>[] classArray = classes.toArray(new Class<?>[size]);
            inst.retransformClasses(classArray);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inst.removeTransformer(transformer);
        }
    }
}

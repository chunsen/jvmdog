package jvmdog.core.command.reset;

import java.util.HashMap;
import java.util.Map;

public class OrignalBytecodeCache {
    private static Map<Class<?>, byte[]> codeMap = new HashMap<>();
    
    public static void add(Class<?> clazz, byte[] data){
        if(codeMap.containsKey(clazz)){
            return;
        }
        codeMap.put(clazz, data);
    }
    
    
    public static  Map<Class<?>, byte[]> getMap(){
        return codeMap;
    }
    
    public static void reset(){
        codeMap.clear();
    }
}

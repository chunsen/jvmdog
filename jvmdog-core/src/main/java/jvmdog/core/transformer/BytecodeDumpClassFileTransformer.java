package jvmdog.core.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BytecodeDumpClassFileTransformer implements ClassFileTransformer{
    
    private final Set<Class<?>> classes;
    private final Map<Class<?>, byte[]> byteCodeMap = new HashMap<>();
    public BytecodeDumpClassFileTransformer(Set<Class<?>> classes){
        this.classes = classes;
    }

    public Map<Class<?>, byte[]> getByteCodeMap() {
        return byteCodeMap;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classes.contains(classBeingRedefined)) {
            byteCodeMap.put(classBeingRedefined, classfileBuffer);
        }
        
        return null;
    }
}

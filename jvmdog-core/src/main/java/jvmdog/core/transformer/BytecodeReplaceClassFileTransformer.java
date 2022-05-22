package jvmdog.core.transformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;

import jvmdog.core.command.reset.OrignalBytecodeCache;

public class BytecodeReplaceClassFileTransformer implements ClassFileTransformer{
    private final Map<Class<?>, byte[]> byteCodeMap;
    public BytecodeReplaceClassFileTransformer(Map<Class<?>, byte[]> byteCodeMap){
        this.byteCodeMap = byteCodeMap;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (byteCodeMap.containsKey(classBeingRedefined)) {
            OrignalBytecodeCache.add(classBeingRedefined, classfileBuffer);
            return byteCodeMap.get(classBeingRedefined);
        }
        
        return null;
    }
}

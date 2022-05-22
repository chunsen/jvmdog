package jvmdog.core.command.objectmonitor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectMonitorClassFileTransformer implements ClassFileTransformer {
    private static final Logger logger = LoggerFactory.getLogger(ObjectMonitorClassFileTransformer.class);
    
    private final Pattern classNamePattern;

    public ObjectMonitorClassFileTransformer(Pattern classNamePattern) {
        this.classNamePattern = classNamePattern;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if(classNamePattern.matcher(className).find()) {
            try {
                logger.info("Constructor enhance: {}", className);
                
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                ObjectMonitorClassVisitor myClassVisitor = new ObjectMonitorClassVisitor(Opcodes.ASM8, cw);
                cr.accept(myClassVisitor, 0);
                byte[] bytecode = cw.toByteArray();
                return bytecode;
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        
        return null;
    }

}

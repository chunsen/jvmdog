package jvmdog.core.command.objectmonitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ObjectMonitorClassVisitor extends ClassVisitor {
    private String className;
    
    public ObjectMonitorClassVisitor(int api, ClassVisitor classVisito) {
        super(api, classVisito);
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature,
        final String superName, final String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor,
        final String signature, final String[] exceptions) {
        if (cv != null) {
            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            if (name.equals("<init>")) {
                return new ConstructorMethodVisitor(api, mv, className);
            } else {
                return mv;
            }
        }
        return null;
    }
}

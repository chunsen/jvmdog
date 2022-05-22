package jvmdog.core.command.objectmonitor;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ConstructorMethodVisitor extends MethodVisitor {
    
    private static final String CLASS_NAME = ObjectMonitorService.class.getName().replace('.', '/');
    private static final String METHOD = "onNewInstance";
    private static final String METHOD_DESCRIPTOR="(Ljava/lang/Object;)V";
    
    private boolean state = true;
    private final String className;

    public ConstructorMethodVisitor(int api, MethodVisitor methodVisitor, String className) {
        super(api, methodVisitor);
        this.className = className;
    }

    @Override
    public void visitInsn(final int opcode) {
        if (mv != null) {
            if (opcode == Opcodes.RETURN && state) {
//                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//                mv.visitLdcInsn("hello asm");
//                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",
//                    false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, CLASS_NAME, METHOD, METHOD_DESCRIPTOR, false);
            }
            mv.visitInsn(opcode);
        }
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, 
        final String descriptor, final boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        if(opcode == Opcodes.INVOKESPECIAL && name.equals("<init>") && owner.equals(className)) {
            state = false;
        }
    }

    @Override
    public void visitEnd() {
        if (mv != null) {
            mv.visitEnd();
        }
        state = true;
    }
}

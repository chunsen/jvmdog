package jvmdog.core.command.code;

import java.util.HashMap;
import java.util.Map;

public class MemoryClassLoader extends ClassLoader {
    private final Map<String, CompiledCode> compiledCodeMap = new HashMap<>();

    public MemoryClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public void add(CompiledCode byteCode) {
        compiledCodeMap.put(byteCode.getClassName(), byteCode);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        CompiledCode byteCode = compiledCodeMap.get(name);
        if (byteCode == null) {
            return super.findClass(name);
        }

        return super.defineClass(name, byteCode.getByteCode(), 0, byteCode.getByteCode().length);
    }
}

package jvmdog.core.command.code;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class ByteCodeFileObject extends SimpleJavaFileObject{
    private final String className;
    private final byte[] bytecode;
    
    protected ByteCodeFileObject(String className, byte[] bytecode) throws Exception {
        super(new URI(className), Kind.CLASS);
        this.className = className;
        this.bytecode = bytecode;
    }
    @Override
    public String getName() {
        return this.className;
    }
    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(bytecode);
    }
}

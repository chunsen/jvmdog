package jvmdog.core.command.code;

import java.io.IOException;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class SourceCode extends SimpleJavaFileObject{
    private final String contents;
    private final String name;
    
    protected SourceCode(String className, String contents) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.contents = contents;
        this.name = className;
    }
    
    public String binaryName() {
        return name;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return contents;
    }
}

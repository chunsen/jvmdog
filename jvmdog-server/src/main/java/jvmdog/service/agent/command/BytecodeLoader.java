package jvmdog.service.agent.command;

import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

public class BytecodeLoader implements Loader{
    private final String className;
    private final byte[] data;
    
    public BytecodeLoader(String className, byte[] data) {
        super();
        this.className = className;
        this.data = data;
    }

    @Override
    public boolean canLoad(String internalName) {
        return className.equals(internalName);
    }

    @Override
    public byte[] load(String internalName) throws LoaderException {
        if(className.equals(internalName)){
            return data;
        }
        return null;
    }

}

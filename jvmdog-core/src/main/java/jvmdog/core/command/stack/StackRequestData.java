package jvmdog.core.command.stack;

import jvmdog.protocol.api.model.MessageData;

public class StackRequestData extends MessageData{
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    
}

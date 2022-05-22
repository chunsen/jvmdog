package jvmdog.core.command.searchclass;

import jvmdog.protocol.api.model.MessageData;

public class SearchClassRequestData extends MessageData{
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    
}

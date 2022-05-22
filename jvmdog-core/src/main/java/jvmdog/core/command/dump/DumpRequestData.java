package jvmdog.core.command.dump;

import java.util.List;

import jvmdog.protocol.api.model.MessageData;

public class DumpRequestData extends MessageData{
    private List<String> classNames;

    public List<String> getClassNames() {
        return classNames;
    }

    public void setClassNames(List<String> classNames) {
        this.classNames = classNames;
    }
    
}

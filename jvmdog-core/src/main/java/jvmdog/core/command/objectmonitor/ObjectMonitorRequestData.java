package jvmdog.core.command.objectmonitor;

import jvmdog.protocol.api.model.MessageData;

public class ObjectMonitorRequestData extends MessageData{
    private String className;
    private String method;
    private String monitorClassPattern;
    
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getMonitorClassPattern() {
        return monitorClassPattern;
    }
    public void setMonitorClassPattern(String monitorClassPattern) {
        this.monitorClassPattern = monitorClassPattern;
    }
    @Override
    public String toString() {
        return "{className:" + className + ", method:" + method + ", monitorClassPattern:" + monitorClassPattern + "}";
    }
    
    
}

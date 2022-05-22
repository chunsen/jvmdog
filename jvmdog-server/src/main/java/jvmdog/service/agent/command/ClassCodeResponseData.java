package jvmdog.service.agent.command;

import java.util.Map;

import jvmdog.protocol.api.model.MessageData;

public class ClassCodeResponseData extends MessageData{
    private Map<String, String> classCodeMap;

    public Map<String, String> getClassCodeMap() {
        return classCodeMap;
    }

    public void setClassCodeMap(Map<String, String> classCodeMap) {
        this.classCodeMap = classCodeMap;
    }
    
}

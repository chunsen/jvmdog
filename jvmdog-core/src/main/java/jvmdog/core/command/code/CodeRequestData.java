package jvmdog.core.command.code;

import java.util.Map;

import jvmdog.protocol.api.model.MessageData;

public class CodeRequestData extends MessageData{
    private Map<String, String> codeMap;

    public Map<String, String> getCodeMap() {
        return codeMap;
    }

    public void setCodeMap(Map<String, String> codeMap) {
        this.codeMap = codeMap;
    }
    
}

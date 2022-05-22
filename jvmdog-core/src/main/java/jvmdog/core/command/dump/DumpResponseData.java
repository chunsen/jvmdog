package jvmdog.core.command.dump;

import java.util.Map;

import jvmdog.protocol.api.model.ResponseMessageData;

public class DumpResponseData extends ResponseMessageData{
    private Map<String, byte[]> byteCodeMap;

    public Map<String, byte[]> getByteCodeMap() {
        return byteCodeMap;
    }

    public void setByteCodeMap(Map<String, byte[]> byteCodeMap) {
        this.byteCodeMap = byteCodeMap;
    }
    
}

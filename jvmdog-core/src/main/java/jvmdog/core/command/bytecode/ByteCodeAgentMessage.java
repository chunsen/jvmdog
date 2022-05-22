package jvmdog.core.command.bytecode;

import java.util.Map;

import jvmdog.protocol.api.model.MessageData;

public class ByteCodeAgentMessage extends MessageData{
    private Map<String, byte[]> byteCodeMap;

    public Map<String, byte[]> getByteCodeMap() {
        return byteCodeMap;
    }

    public void setByteCodeMap(Map<String, byte[]> byteCodeMap) {
        this.byteCodeMap = byteCodeMap;
    }
}

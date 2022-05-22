package jvmdog.client.core;

import jvmdog.protocol.api.model.MessageData;

public class UpdateJarRequestData extends MessageData {
    private byte[] coreJar;
    private byte[] agentJar;
    private byte[] nativeAgent;
    
    public byte[] getCoreJar() {
        return coreJar;
    }
    public void setCoreJar(byte[] coreJar) {
        this.coreJar = coreJar;
    }
    public byte[] getAgentJar() {
        return agentJar;
    }
    public void setAgentJar(byte[] agentJar) {
        this.agentJar = agentJar;
    }
    public byte[] getNativeAgent() {
        return nativeAgent;
    }
    public void setNativeAgent(byte[] nativeAgent) {
        this.nativeAgent = nativeAgent;
    }
    
}

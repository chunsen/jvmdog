package jvmdog.client.core;

import jvmdog.protocol.api.model.ResponseMessageData;

public class JarDigestResponseData extends ResponseMessageData {
    private String coreJar;
    private String agentJar;
    private String nativeAgent;

    public String getCoreJar() {
        return coreJar;
    }

    public void setCoreJar(String coreJar) {
        this.coreJar = coreJar;
    }

    public String getAgentJar() {
        return agentJar;
    }

    public void setAgentJar(String agentJar) {
        this.agentJar = agentJar;
    }

    public String getNativeAgent() {
        return nativeAgent;
    }

    public void setNativeAgent(String nativeAgent) {
        this.nativeAgent = nativeAgent;
    }
    
}

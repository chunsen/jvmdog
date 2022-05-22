package jvmdog.client.core.service;

public interface JarService {
    String coreJarMD5();
    
    void updateCoreJar(byte[] data);
    
    String agentJarMD5();
    void updateAgentJar(byte[] data);
    
    String nativeAgentMD5();
    void updateNativeAgent(byte[] data);
}

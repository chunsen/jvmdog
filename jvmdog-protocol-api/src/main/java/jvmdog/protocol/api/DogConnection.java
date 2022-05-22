package jvmdog.protocol.api;

import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.OSInfo;

public interface DogConnection {
    String id();
    String ip();
    String peerPid();
    OSInfo osInfo();
    /**
     * client or agent
     * @return
     */
    String getType();
    
    void send(DogMessage dogMessage);
    
    void close();
}

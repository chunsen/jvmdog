package jvmdog.protocol.api;

import java.util.List;

import jvmdog.protocol.api.model.DogMessage;

public interface DogClient {
    void connect(List<MessageHandler> messageHandlers);
    
    void send(DogMessage dogMessage);
    
    void close();
}

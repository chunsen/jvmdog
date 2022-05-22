package jvmdog.protocol.api;

import jvmdog.protocol.api.model.DogMessage;

public interface MessageHandler {
    void handle(DogMessage message, DogConnection connection);
    
    int type();
}

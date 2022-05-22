package jvmdog.service;

import jvmdog.protocol.api.model.DogMessage;

public interface DogMessageHandler {
    DogMessage handle(DogMessage dogMessage);
}

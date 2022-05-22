package jvmdog.client.core.command;

import jvmdog.protocol.api.RemoteCommand;
import jvmdog.protocol.api.model.DogMessage;

public interface ClientCommand extends RemoteCommand{

    DogMessage run(byte[] data);
    
}

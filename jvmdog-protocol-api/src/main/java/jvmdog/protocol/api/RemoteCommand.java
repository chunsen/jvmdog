package jvmdog.protocol.api;

import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;

public interface RemoteCommand {
    String name();
    Class<? extends MessageData> requestClass();
    Class<? extends ResponseMessageData> responseClass();
}

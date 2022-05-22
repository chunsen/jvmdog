package jvmdog.service;

import jvmdog.protocol.api.model.MessageData;

public interface ResponseDataHandler {
    String name();
    MessageData handle(MessageData responseData);
}

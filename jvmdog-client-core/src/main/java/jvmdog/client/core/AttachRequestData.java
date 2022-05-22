package jvmdog.client.core;

import jvmdog.protocol.api.model.MessageData;

public class AttachRequestData extends MessageData {
    private String pid;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "{pid:" + pid + ", getId():" + getId() + "}";
    }

}

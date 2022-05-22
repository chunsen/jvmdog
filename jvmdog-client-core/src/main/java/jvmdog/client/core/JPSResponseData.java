package jvmdog.client.core;

import java.util.List;

import jvmdog.protocol.api.model.ResponseMessageData;

public class JPSResponseData extends ResponseMessageData {
    private List<JvmInfo> jvmInfos;

    public List<JvmInfo> getJvmInfos() {
        return jvmInfos;
    }

    public void setJvmInfos(List<JvmInfo> jvmInfos) {
        this.jvmInfos = jvmInfos;
    }

    @Override
    public String toString() {
        return "{jvmInfos:" + jvmInfos + "}";
    }

}

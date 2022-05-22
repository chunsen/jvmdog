package jvmdog.core.command.instance;

import java.util.List;

import jvmdog.protocol.api.model.ResponseMessageData;

public class InstanceResponseData extends ResponseMessageData{
    private List<String> instances;

    public List<String> getInstances() {
        return instances;
    }

    public void setInstances(List<String> instances) {
        this.instances = instances;
    }
    
}

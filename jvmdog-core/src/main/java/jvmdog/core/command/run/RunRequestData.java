package jvmdog.core.command.run;

import jvmdog.protocol.api.model.MessageData;

public class RunRequestData extends MessageData{
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}

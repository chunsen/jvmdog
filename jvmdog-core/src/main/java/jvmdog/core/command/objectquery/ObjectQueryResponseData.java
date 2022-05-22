package jvmdog.core.command.objectquery;

import java.util.List;
import java.util.Map;

import jvmdog.protocol.api.model.ResponseMessageData;

public class ObjectQueryResponseData extends ResponseMessageData{
    private List<Map<String,Object>> result;

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public void setResult(List<Map<String, Object>> result) {
        this.result = result;
    }

}

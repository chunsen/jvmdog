package jvmdog.core.command.objectupdate;

import jvmdog.protocol.api.model.MessageData;

public class ObjectUpdateRequestData extends MessageData{
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    
}

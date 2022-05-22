package jvmdog.core.command.objectquery;

import jvmdog.protocol.api.model.MessageData;

public class ObjectQueryRequestData extends MessageData{
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    
}

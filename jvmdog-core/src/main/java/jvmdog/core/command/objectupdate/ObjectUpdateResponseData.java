package jvmdog.core.command.objectupdate;

import jvmdog.protocol.api.model.ResponseMessageData;

public class ObjectUpdateResponseData extends ResponseMessageData{
    private int rowCount;

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

}

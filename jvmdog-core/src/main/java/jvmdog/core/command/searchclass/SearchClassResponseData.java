package jvmdog.core.command.searchclass;

import java.util.List;

import jvmdog.protocol.api.model.ResponseMessageData;

public class SearchClassResponseData extends ResponseMessageData{
    private List<ClassInfo> classInfos;

    public List<ClassInfo> getClassInfos() {
        return classInfos;
    }

    public void setClassInfos(List<ClassInfo> classInfos) {
        this.classInfos = classInfos;
    }
    
}

package jvmdog.core.command.stack;

import java.util.List;

import jvmdog.protocol.api.model.ResponseMessageData;

public class StackResponseData extends ResponseMessageData {
    private List<ThreadStack> threadStacks;

    public List<ThreadStack> getThreadStacks() {
        return threadStacks;
    }

    public void setThreadStacks(List<ThreadStack> threadStacks) {
        this.threadStacks = threadStacks;
    }

}

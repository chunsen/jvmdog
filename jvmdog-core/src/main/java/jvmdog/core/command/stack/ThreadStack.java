package jvmdog.core.command.stack;

import java.util.List;

public class ThreadStack {
    private String       threadName;
    private long         threadId;
    private String       lockName;
    private String       lockOwnerName;
    private Thread.State threadState;
    private List<StackTraceFrame> stackTraces;
    
    public String getThreadName() {
        return threadName;
    }
    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
    public long getThreadId() {
        return threadId;
    }
    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
    
    public String getLockName() {
        return lockName;
    }
    public void setLockName(String lockName) {
        this.lockName = lockName;
    }
    public String getLockOwnerName() {
        return lockOwnerName;
    }
    public void setLockOwnerName(String lockOwnerName) {
        this.lockOwnerName = lockOwnerName;
    }
    public Thread.State getThreadState() {
        return threadState;
    }
    public void setThreadState(Thread.State threadState) {
        this.threadState = threadState;
    }
    public List<StackTraceFrame> getStackTraces() {
        return stackTraces;
    }
    public void setStackTraces(List<StackTraceFrame> stackTraces) {
        this.stackTraces = stackTraces;
    }
    
}

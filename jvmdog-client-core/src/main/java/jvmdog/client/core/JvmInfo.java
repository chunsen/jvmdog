package jvmdog.client.core;

public class JvmInfo {
    private Integer pid;
    private String mainClass;
    private String mainArgs;
    private String commandLine;
    
    public Integer getPid() {
        return pid;
    }
    public void setPid(Integer pid) {
        this.pid = pid;
    }
    public String getMainClass() {
        return mainClass;
    }
    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
    public String getMainArgs() {
        return mainArgs;
    }
    public void setMainArgs(String mainArgs) {
        this.mainArgs = mainArgs;
    }
    public String getCommandLine() {
        return commandLine;
    }
    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }
    @Override
    public String toString() {
        return "{pid:" + pid + ", mainClass:" + mainClass + ", mainArgs:" + mainArgs + ", commandLine:" + commandLine
            + "}";
    }
    
}

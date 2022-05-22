package jvmdog.agent;

public class AgentArgument {
    private String jarFile;
    private String className;
    private String options;
    
    public String getJarFile() {
        return jarFile;
    }
    public void setJarFile(String jarFile) {
        this.jarFile = jarFile;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getOptions() {
        return options;
    }
    public void setOptions(String options) {
        this.options = options;
    }
    @Override
    public String toString() {
        return String.format("%s;%s;%s", jarFile, className, options);
    }
    
    public static AgentArgument fromString(String str){
        String[] items = str.split(";");
        AgentArgument result = new AgentArgument();
        result.jarFile = items[0];
        result.className = items[1];
        result.options = items[2];
        
        return result;
    }
}

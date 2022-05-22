package jvmdog.protocol.api.model;

public class OSInfo {
    private String name;
    private String arch;
    private String version;
    
    private final static OSInfo OS_INFO;
    
    static {
        OS_INFO =new OSInfo();
        OS_INFO.setName(System.getProperty("os.name").toLowerCase());
        OS_INFO.setArch(System.getProperty("os.arch").toLowerCase());
        OS_INFO.setVersion(System.getProperty("os.version").toLowerCase());
    }
    
    public static OSInfo getInstance() {
        return OS_INFO;
    }
    
    public boolean isLinux(){
        return name.indexOf("linux")>=0;
    }
    
    public boolean isWindows(){
        return name.indexOf("windows")>=0;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getArch() {
        return arch;
    }
    public void setArch(String arch) {
        this.arch = arch;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    
}

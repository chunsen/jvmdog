package jvmdog.protocol.api.model;

public class Version {
    private static final int CURRENT_MAJOR_VERSION = 1;
    private static final int CURRENT_MINOR_VERSION = 1;
    
    public static final int CURRENT_VERSION = CURRENT_MAJOR_VERSION * 1000 + CURRENT_MINOR_VERSION % 1000;
    
    public static boolean suuport(int version) {
        int majorVersion = version / 1000;
        if(majorVersion != CURRENT_MAJOR_VERSION) {
            return false;
        } else {
            return true;
        }
    }
}

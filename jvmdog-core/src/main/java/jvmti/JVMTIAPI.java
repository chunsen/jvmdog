package jvmti;

public class JVMTIAPI {
    public synchronized static native Object[] queryObjects2(String className);
    
    public synchronized static native long tagObjects(Object[] objects);
    
    public synchronized static native long tagObject(Object object);
    
    public synchronized static native Object[] collect(long tagValue);
    
}

package jvmdog.core.command.objectmonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmti.JVMTIAPI;

public class ObjectMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(ObjectMonitorService.class);
    
    private static volatile boolean start = false;
    private static volatile long tagValue = 0L;
    
    public static void onNewInstance(Object obj) {
        if(start) {
            tagValue = JVMTIAPI.tagObject(obj);
        }
    }
    
    
    public static void start() {
        start = true;
        logger.info("mon start...");
    }
    
    public static void stop() {
        long val = tagValue;
        start = false;
        Object[] objects = JVMTIAPI.collect(val);
        for(Object obj: objects) {
            logger.warn("find:" + obj);
        }
        
        logger.info("mon stop...");
    }
}

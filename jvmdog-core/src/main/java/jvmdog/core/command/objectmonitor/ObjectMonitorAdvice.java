package jvmdog.core.command.objectmonitor;

import java.lang.reflect.Method;

import net.bytebuddy.asm.Advice;

public class ObjectMonitorAdvice {
    
    @Advice.OnMethodEnter
    public static void enter(@Advice.This(optional = true)  Object obj) {
        ObjectMonitorService.start();
    }
    
    @Advice.OnMethodExit()
    public static void exit(@Advice.This(optional = true)  Object obj, @Advice.Origin Method method) {
        ObjectMonitorService.stop();
    }
}

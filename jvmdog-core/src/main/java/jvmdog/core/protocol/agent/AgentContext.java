package jvmdog.core.protocol.agent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentContext implements AutoCloseable{
    private static final Logger logger = LoggerFactory.getLogger(AgentContext.class);
    
    private final Instrumentation inst;
    private final Set<Class<?>> changedClasses = new HashSet<>();
    private final Map<String, SessionInfo> sessionMap = new ConcurrentHashMap<>();
    
    public AgentContext(Instrumentation inst) {
        this.inst = inst;
    }
    
    public Instrumentation getInst() {
        return inst;
    }

    @Override
    public void close(){
        if(!sessionMap.isEmpty()) {
            for(SessionInfo sessionInfo: sessionMap.values()) {
                for (ClassFileTransformer transformer : sessionInfo.transformers) {
                    inst.removeTransformer(transformer);
                }
            }
        }
        
        if(!changedClasses.isEmpty()) {
            redefineClasses(changedClasses);
            changedClasses.clear();
        }
        
        sessionMap.clear();
    }
    
    public Class<?>[] getAllLoadedClasses() {
        return inst.getAllLoadedClasses();
    }
    
    public void visitClasses(Set<Class<?>> classes, ClassFileTransformer... transformers) {
        retransformClasses(classes, true, false, transformers);
    }
    
    public void beginSession(String sessionId, Set<Class<?>> classes, ClassFileTransformer... transformers) {
        if(sessionId == null || classes==null || classes.isEmpty() || transformers==null || transformers.length==0) {
            return;
        }
        
        sessionMap.put(sessionId, new SessionInfo(classes, transformers));
        retransformClasses(classes, false, true, transformers);
    }
    
    public void stopSession(String sessionId) {
        SessionInfo sessionInfo = sessionMap.get(sessionId);
        if(sessionInfo == null) {
            logger.error("session not exist: {}", sessionId);
            return;
        }
        
        for(ClassFileTransformer transformer: sessionInfo.transformers) {
            inst.removeTransformer(transformer);
        }
        sessionMap.remove(sessionId);
        
        changedClasses.clear();
        if(!sessionMap.isEmpty()) {
            for(Entry<String, SessionInfo> entry: sessionMap.entrySet()) {
                changedClasses.addAll(entry.getValue().classes);
            }
        }

        redefineClasses(sessionInfo.classes);
    }
    
    private void retransformClasses(Set<Class<?>> classes, boolean removeTransformer, boolean changeClass, ClassFileTransformer... transformers){
        if(inst==null || transformers ==null || transformers.length==0){
            return;
        }
        if(classes ==null || classes.size() ==0){
            return;
        }
        
        try {
            for(ClassFileTransformer transformer: transformers) {
                inst.addTransformer(transformer, true);
            }
            
            int size = classes.size();
            Class<?>[] classArray = classes.toArray(new Class<?>[size]);
            inst.retransformClasses(classArray);
            
            if(changeClass) {
                changedClasses.addAll(classes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (removeTransformer) {
                for (ClassFileTransformer transformer : transformers) {
                    inst.removeTransformer(transformer);
                }
            }
        }
    }
    
    private void redefineClasses(Set<Class<?>> classes) {
        Set<ClassDefinition> targetClasses = new HashSet<ClassDefinition>();
        StringBuilder sb = new StringBuilder();
        for(Class<?> clazz : classes) {
            ClassDefinition cd = getClassDefinition(clazz);
            if(cd != null) {
                targetClasses.add(cd);
                if(sb.length()>0) {
                    sb.append(',');
                }
                sb.append(clazz.getName());
            }
        }
        
        logger.info("redefineClasses: [{}]", sb.toString());
        try {
            inst.redefineClasses(targetClasses.toArray(new ClassDefinition[0]));
        } catch (Throwable e) {
            logger.error("redefineClasses error"  ,e);
        }
    }
    
    private ClassDefinition getClassDefinition(Class<?> clazz) {
        String className = clazz.getName();
        ClassLoader classLoader = clazz.getClassLoader();
        if(classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
            logger.warn("getClassDefinition: use SystemClassLoader for {}, {}", className);
        }
        try (InputStream inputStream = classLoader.getResourceAsStream(className.replace('.', '/') + ".class")) {
            if (inputStream != null) {
                byte[] bytecode = toBytes(inputStream);
                ClassDefinition cd = new ClassDefinition(clazz, bytecode);
                return cd;
            } else {
                logger.warn("getClassDefinition, class {} resource is null", className);
            }
            return null;
        } catch (Exception e) {
            logger.error("getClassDefinition error:"+className ,e);
            return null;
        }
    }
    
    private static byte[] toBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 100)) > 0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    private static class SessionInfo{
        public Set<Class<?>> classes;
        public ClassFileTransformer[] transformers;
        public SessionInfo(Set<Class<?>> classes, ClassFileTransformer[] transformers) {
            this.classes = classes;
            this.transformers = transformers;
        }
        
    }
}

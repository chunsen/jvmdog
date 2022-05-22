package jvmdog.core.command.objectmonitor;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

public class ObjectMonitorAgentCommand implements AgentCommand{
    private static final Logger logger = LoggerFactory.getLogger(ObjectMonitorAgentCommand.class);

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        ObjectMonitorRequestData objectMonitorMessage = SerializeUtils.deserialize(data, ObjectMonitorRequestData.class);
        
        if(objectMonitorMessage == null) {
            logger.error("ObjectMonitorAgentCommand: message is null");
            return null;
        }
        
        logger.info("ObjectMonitorAgentCommand: {}", objectMonitorMessage);
        ResponseMessageData result = new ResponseMessageData();
        result.setId(objectMonitorMessage.getId());
        
        try {
            Pattern classNamePattern = Pattern.compile(objectMonitorMessage.getMonitorClassPattern());
            ObjectMonitorClassFileTransformer objectMonitorClassFileTransformer = new ObjectMonitorClassFileTransformer(classNamePattern);
            
            ClassFileTransformer classFileTransformer = new AgentBuilder.Default().disableClassFormatChanges()
            .type(ElementMatchers.named(objectMonitorMessage.getClassName())).transform(new AgentBuilder.Transformer() {
                @Override
                public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription,
                    ClassLoader classLoader, JavaModule module) {
                    logger.info("transform class... {}", typeDescription.getTypeName());
                    return builder.visit(Advice.to(ObjectMonitorAdvice.class).on(ElementMatchers.named(objectMonitorMessage.getMethod())));
                }
            }).makeRaw();
            
            Class<?>[] classes = agentContext.getAllLoadedClasses();
            Set<ClassDefinition> targetClasses = new HashSet<>();
            Set<Class<?>> targetClazzes = new HashSet<>();
            for(Class<?> clazz : classes) {
                String clazzName = clazz.getName();
                if(!clazzName.startsWith(objectMonitorMessage.getClassName()) && !classNamePattern.matcher(clazzName).matches()) {
                    continue;
                }
                ClassDefinition cd = getClassDefinition(clazz);
                if(cd != null) {
                    targetClasses.add(cd);
                    targetClazzes.add(clazz);
                }
            }

            agentContext.beginSession(objectMonitorMessage.getId().toString(), targetClazzes, objectMonitorClassFileTransformer, classFileTransformer);
        } catch (Throwable e) {
            logger.error("redefineClasses error"  ,e);
            result.setCode(ResponseMessageData.CODE_ERROR);
            result.setMessage(e.getMessage());
        }

        byte[] messageData = SerializeUtils.serialize(result);
        DogMessage message = DogMessage.clientResponse("objmon");
        message.setData(messageData);
        
        return message;
    }
    
    private ClassDefinition getClassDefinition(Class<?> clazz) {
        String className = clazz.getName();
        ClassLoader classLoader = clazz.getClassLoader();
        logger.info("getClassDefinition: {}, {}", className, classLoader);
        if(classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        try (InputStream inputStream = classLoader.getResourceAsStream(className.replace('.', '/') + ".class")) {
            if (inputStream != null) {
                byte[] bytecode = toBytes(inputStream);
                ClassDefinition cd = new ClassDefinition(FileInputStream.class, bytecode);
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
    
    public static byte[] toBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 100)) > 0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String name() {
        return "objmon";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return ObjectMonitorRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return ResponseMessageData.class;
    }

}

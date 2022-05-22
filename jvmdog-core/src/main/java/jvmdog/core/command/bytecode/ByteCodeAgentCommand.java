package jvmdog.core.command.bytecode;

import java.lang.instrument.ClassFileTransformer;
import java.util.HashMap;
import java.util.Map;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.core.transformer.BytecodeReplaceClassFileTransformer;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class ByteCodeAgentCommand implements AgentCommand{
    
    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        ByteCodeAgentMessage byteCodeClientMessage = SerializeUtils.deserialize(data, ByteCodeAgentMessage.class);
        
        Map<Class<?>, byte[]> byteCodeMap = new HashMap<>();
        Map<String, byte[]> nameByteCodeMap = byteCodeClientMessage.getByteCodeMap();
        for (Class<?> clazz : agentContext.getAllLoadedClasses()) {
            if(nameByteCodeMap.containsKey(clazz.getName())){
                byteCodeMap.put(clazz, nameByteCodeMap.get(clazz.getName()));
            }
        }
        ClassFileTransformer transformer = new BytecodeReplaceClassFileTransformer(byteCodeMap);
        agentContext.beginSession("123", byteCodeMap.keySet(), transformer);
//        InstrumentationUtils.retransformClasses(inst, transformer, byteCodeMap.keySet());
        
        return null;
    }

    @Override
    public String name() {
        // TODO Auto-generated method stub
        return "bytecode";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        // TODO Auto-generated method stub
        return ByteCodeAgentMessage.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        // TODO Auto-generated method stub
        return null;
    }

}

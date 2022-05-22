package jvmdog.core.command.reset;

import java.lang.instrument.ClassFileTransformer;
import java.util.Map;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.core.transformer.BytecodeReplaceClassFileTransformer;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;

public class ResetAgentCommand implements AgentCommand {

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        Map<Class<?>, byte[]> byteCodeMap = OrignalBytecodeCache.getMap();
        ClassFileTransformer transformer = new BytecodeReplaceClassFileTransformer(byteCodeMap);
//        InstrumentationUtils.retransformClasses(inst, transformer, byteCodeMap.keySet());
        agentContext.close();
        
        OrignalBytecodeCache.reset();
        return null;
    }

    @Override
    public String name() {
        return "reset";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        // TODO Auto-generated method stub
        return null;
    }

}

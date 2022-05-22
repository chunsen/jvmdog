package jvmdog.core.command.code;

import java.lang.instrument.ClassFileTransformer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.core.transformer.BytecodeReplaceClassFileTransformer;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class CodeAgentCommand implements AgentCommand {
    private static final Logger logger = LoggerFactory.getLogger(CodeAgentCommand.class);

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        CodeRequestData codeClientMessage = SerializeUtils.deserialize(data, CodeRequestData.class);
        ResponseMessageData result = new ResponseMessageData();
        result.setId(codeClientMessage.getId());
        
        try {
            CodeCompiler codeCompiler = new CodeCompiler();
            logger.info("complile code {}", codeClientMessage.getCodeMap().keySet());
            Map<Class<?>, byte[]> byteCodeMap = codeCompiler.compile(agentContext, codeClientMessage.getCodeMap());
            if(byteCodeMap == null || byteCodeMap.isEmpty()) {
                logger.warn("complile code is empty.");
            } else {
                logger.info("load complile code {}",byteCodeMap.keySet());
                ClassFileTransformer transformer = new BytecodeReplaceClassFileTransformer(byteCodeMap);
                agentContext.beginSession(codeClientMessage.getId().toString(), byteCodeMap.keySet(), transformer);
            }
        } catch (Throwable e) {
            logger.error("CodeAgentCommand error", e);
            result.setCode(ResponseMessageData.CODE_ERROR);
            result.setMessage(e.getMessage());
        }
        
        byte[] messageData = SerializeUtils.serialize(result);
        DogMessage message = DogMessage.clientResponse("code");
        message.setData(messageData);
        
        return message;
    }

    @Override
    public String name() {
        return "code";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return CodeRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return ResponseMessageData.class;
    }

}

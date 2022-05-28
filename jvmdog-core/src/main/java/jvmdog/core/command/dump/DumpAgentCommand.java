package jvmdog.core.command.dump;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.core.transformer.BytecodeDumpClassFileTransformer;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class DumpAgentCommand implements AgentCommand{
    private static final Logger logger = LoggerFactory.getLogger(DumpAgentCommand.class);

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        DumpRequestData dumpRequestData = SerializeUtils.deserialize(data, DumpRequestData.class);
        Set<Class<?>> classes = new HashSet<>();
        DumpResponseData dumpMessage = new DumpResponseData();
        dumpMessage.setId(dumpRequestData.getId());
        
        try {
            List<String> classNames = dumpRequestData.getClassNames();
            for (Class<?> clazz : agentContext.getAllLoadedClasses()) {
                if(classNames.contains(clazz.getName())){
                    classes.add(clazz);
                }
            }
            BytecodeDumpClassFileTransformer transformer = new BytecodeDumpClassFileTransformer(classes);
            agentContext.visitClasses(classes, transformer);
            
            Map<String, byte[]> byteCodeMap = new HashMap<>();
            for(Entry<Class<?>, byte[]> entry: transformer.getByteCodeMap().entrySet()){
                byteCodeMap.put(entry.getKey().getName(), entry.getValue());
//                dumpClass(entry.getKey(), entry.getValue());
            }
            
            dumpMessage.setByteCodeMap(byteCodeMap);
        }catch(Throwable e) {
            dumpMessage.setCode(ResponseMessageData.CODE_ERROR);
            dumpMessage.setMessage(e.getMessage());
            logger.error("DumpAgentCommand error", e);
        }
        
        byte[] messageData = SerializeUtils.serialize(dumpMessage);
        DogMessage message = DogMessage.clientResponse("dump");
        message.setData(messageData);
        
        return message;
    }
    
//    private void dumpClass(Class<?> clazz, byte[] data){
//        String folderPath = "d:\\temp\\dump\\";
//        File folder = new File(folderPath);
//        if(!folder.exists()){
//            folder.mkdirs();
//        }
//        File dumpClassFile = new File(folder, clazz.getName()+".class");
//        System.out.println("dump class file:" + dumpClassFile.getAbsolutePath());
//
//        // 将类字节码写入文件
//        try {
//            ByteArrayInputStream sr = new ByteArrayInputStream(data);
//            Files.copy(sr, dumpClassFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public String name() {
        return "dump";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return DumpRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return DumpResponseData.class;
    }

}

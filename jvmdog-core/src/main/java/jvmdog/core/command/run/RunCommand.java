package jvmdog.core.command.run;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.core.command.code.CodeCompiler;
import jvmdog.core.command.code.CompiledCode;
import jvmdog.core.command.code.MemoryClassLoader;
import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class RunCommand implements AgentCommand {
    private static final Logger logger = LoggerFactory.getLogger(RunCommand.class);
    
    private static final String CODE_FORMAT="package jvmdog.core.command.run;\r\n" + 
        "public class %s implements Runnable{\r\n" + 
        "    public void run() {\r\n" + 
        "        %s\r\n" + 
        "    }\r\n" + 
        "}";

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        RunRequestData runRequestData = SerializeUtils.deserialize(data, RunRequestData.class);
        ResponseMessageData result = new ResponseMessageData();
        result.setId(runRequestData.getId());
        
        String className = "Run" + runRequestData.getId();
        String code = String.format(CODE_FORMAT, className, runRequestData.getCode());
        
        Map<String, String> codeMap = new HashMap<>();
        String fullClassName = "jvmdog.core.command.run." + className;
        codeMap.put(fullClassName, code);
        
        try {
            CodeCompiler codeCompiler = new CodeCompiler();
            logger.info("RunCommand: complile code {}", fullClassName);
            Map<String, byte[]> byteCodeMap = codeCompiler.compile2(agentContext, codeMap);
            if(byteCodeMap == null || byteCodeMap.isEmpty()) {
                logger.warn("RunCommand: complile code is empty.");
            } else {
                ClassLoader classLoader = findClassLoader(agentContext);
                logger.info("RunCommand: load complile class {}, {}", byteCodeMap.keySet(), classLoader);
                MemoryClassLoader memoryClassLoader = new MemoryClassLoader(classLoader);
                for(Entry<String, byte[]> entry: byteCodeMap.entrySet()) {
                    CompiledCode compiledCode = new CompiledCode(entry.getKey());
                    try(OutputStream outputStream = compiledCode.openOutputStream()){
                        outputStream.write(entry.getValue());
                    }
                    memoryClassLoader.add(compiledCode);
                }
                
                Class<?> clazz = memoryClassLoader.loadClass(fullClassName);
                Runnable runnable = (Runnable)clazz.newInstance();
                Thread thread = new Thread(runnable);
                thread.setName("jvmdog-runcommand-"+runRequestData.getId());
               
                thread.setContextClassLoader(memoryClassLoader);
                thread.start();
            }
        }catch(Throwable e) {
            logger.error("RunCommand error", e);
            result.setCode(ResponseMessageData.CODE_ERROR);
            result.setMessage(e.getMessage());
        }
        
        byte[] messageData = SerializeUtils.serialize(result);
        DogMessage message = DogMessage.clientResponse("run");
        message.setData(messageData);
        
        return message;
    }
    
    private ClassLoader findClassLoader(AgentContext agentContext) {
        Class[] classes = agentContext.getAllLoadedClasses();
        for (Class<?> clazz : classes) {
            if ("org.springframework.context.ApplicationContext".equals(clazz.getName())) {
                return clazz.getClassLoader();
            }
        }
        return Thread.currentThread().getContextClassLoader();
    }

    @Override
    public String name() {
        return "run";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return RunRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return ResponseMessageData.class;
    }

}

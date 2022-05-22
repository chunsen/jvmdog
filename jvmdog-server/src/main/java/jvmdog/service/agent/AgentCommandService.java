package jvmdog.service.agent;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import jvmdog.core.command.bytecode.ByteCodeAgentMessage;
import jvmdog.core.command.bytecode.ByteCodeAgentMessageBuilder;
import jvmdog.core.command.code.CodeRequestData;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.protocol.api.utils.SerializeUtils;

@Service
public class AgentCommandService {

    public DogMessage command(long id, String command, String data){
      if("bytecode".equals(command)){
          return bytecode();
      } else if("code".equals(command)){
          return code();
      }  else if("dump".equals(command)){
          return dump();
      } else if("reset".equals(command)){
          return reset();
      } else if("close".equals(command)){
          return close();
      } else if("objmon".equals(command)){
          return build(command, data);
      }
      
      return null;
    }
    
    private DogMessage build(String command, String data) {
        DogMessage message = DogMessage.clientCommand();
        message.setHeader(command.getBytes(Charset.forName("utf-8")));
        if(data != null && data.length()>0){
            message.setData(data.getBytes(Charset.forName("utf-8")));
        } else {
            message.setData(null);
        }

        return message;
    }
    
    private DogMessage code(){
        DogMessage message = DogMessage.clientCommand();
        message.setHeader("code".getBytes(Charset.forName("utf-8")));
        Map<String, String> codeMap = new HashMap<>();
        String file = "D:\\my-code\\demo-boot\\demo-boot2\\src\\main\\java\\demo\\boot\\controller\\TestService.java";
        byte[] codeData;
        try {
            codeData = Files.readAllBytes(Paths.get(file));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        codeMap.put("demo.boot.controller.TestService", new String(codeData, Charset.forName("utf-8")));
        CodeRequestData agentMessage = new CodeRequestData();
        agentMessage.setCodeMap(codeMap);
        byte[] data = SerializeUtils.serialize(agentMessage);
        message.setData(data);
        
        return message;
    }
    
    private DogMessage bytecode(){
        DogMessage message = DogMessage.clientCommand();
        message.setHeader("bytecode".getBytes(Charset.forName("utf-8")));
        Map<String, String> classFileMap = new HashMap<>();
        classFileMap.put("demo.boot.controller.TestService", "D:\\my-code\\demo-boot\\demo-boot2\\target\\classes\\demo\\boot\\controller\\TestService.class");
        ByteCodeAgentMessage clientMessage = ByteCodeAgentMessageBuilder.build(classFileMap);
        byte[] data = SerializeUtils.serialize(clientMessage);
        message.setData(data);
        
        return message;
    }
    
    private DogMessage dump(){
      DogMessage message = DogMessage.clientCommand();
      message.setHeader("dump".getBytes(Charset.forName("utf-8")));
      message.setData("demo.boot.controller.TestService".getBytes(Charset.forName("utf-8")));
      return message;
    }
    
    private DogMessage reset(){
        DogMessage message = DogMessage.clientCommand();
        message.setHeader("reset".getBytes(Charset.forName("utf-8")));
        message.setData(null);
        
        return message;
    }
    
    private DogMessage close(){
        DogMessage message = DogMessage.from(DogMessageType.CLOSE_BY_SERVER.getValue());
        message.setHeader("close".getBytes(Charset.forName("utf-8")));
        message.setData(null);
        
        return message;
    }
}

package jvmdog.controller;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jvmdog.controller.model.ServerDataResult;
import jvmdog.controller.model.ServerPagingDataResult;
import jvmdog.core.command.searchclass.ClassInfo;
import jvmdog.core.command.searchclass.SearchClassResponseData;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.service.agent.Agent;
import jvmdog.service.agent.AgentService;

@RestController
@RequestMapping("/agent/")
public class AgentController {
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    
    @Autowired
    private AgentService agentService;
    
    @GetMapping
    public ServerPagingDataResult<Agent> get(@RequestParam(name="ip", required=false)String ip){
//        getClassDefinition(AgentController.class);
        List<Agent> agents = agentService.getAll();
        return ServerPagingDataResult.from(agents);
    }
    
    @GetMapping(value="/{name}")
    public ServerDataResult<Agent> getAgent(@PathVariable("name") String name){
        Agent agent = agentService.getAgent(name);
        return ServerDataResult.from(agent);
    }
    
    @GetMapping(value="/{name}/searchClass")
    public ServerPagingDataResult<ClassInfo> searchClass(@PathVariable("name") String name, @RequestParam("className")String className){
        logger.info("searchClass Command: agent={}, className ={}", name, className);
        String body = String.format("{\"className\":\"%s\"}", className);
        SearchClassResponseData data = agentService.runRemoteCommand(name, "searchClass", body);
        return ServerPagingDataResult.from(data!=null ? data.getClassInfos(): null);
    }
    
    @PostMapping(value="/{name}/{command}")
    public ServerDataResult<MessageData> agentCommand(@PathVariable("name") String name, @PathVariable("command")String command, @RequestBody String body){
        logger.info("agentCommand: agent={}, command ={}", name, command);
        MessageData data = agentService.runRemoteCommand(name, command, body);
        return ServerDataResult.from(data);
    }
    
    @PutMapping(value="/{name}/{command}")
    public ServerDataResult<ResponseMessageData> agentCommandStop(@PathVariable("name") String name, @PathVariable("command")String command, @RequestBody String body){
        logger.info("agentCommand: agent={}, command ={}", name, command);
        ResponseMessageData data = agentService.stopRemoteCommand(name, command, body);
        return ServerDataResult.from(data);
    }
    
    private ClassDefinition getClassDefinition(Class<?> clazz) {
        try (InputStream inputStream = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + ".class")) {
            if (inputStream != null) {
                byte[] bytecode = toBytes(inputStream);
                ClassDefinition cd = new ClassDefinition(FileInputStream.class, bytecode);
                return cd;
            }
            return null;
        } catch (Exception e) {
            logger.error("getClassDefinition error:"+clazz.getName() ,e);
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
}

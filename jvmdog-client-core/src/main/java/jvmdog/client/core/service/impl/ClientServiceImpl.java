package jvmdog.client.core.service.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sun.tools.attach.VirtualMachine;

import jvmdog.client.core.service.ClientService;
import jvmdog.core.agent.InitCommand;

@Service
public class ClientServiceImpl implements ClientService{
    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
    
    @Value("${jvmdog.server.host:localhost}")
    private String jvmdogServer;
    
    @Value("${jvmdog.server.port:8100}")
    private int port;

    @Autowired
    private JarServiceImpl jarService;
    
    private final ConcurrentHashMap<String, VirtualMachine> vmMap = new ConcurrentHashMap<>();
    
    public String server() {
        return jvmdogServer;
    }
    
    public int serverPort() {
        return port;
    }

    @Override
    public void attach(String pid){
        logger.info("attach process {}", pid);
        if(vmMap.contains(pid)){
            logger.warn("process already atached {}", pid);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(jarService.coreJar());
        sb.append(";");
        sb.append(InitCommand.class.getName());
        sb.append(";");
        sb.append(jvmdogServer);
        sb.append(",");
        sb.append(port);
        
        VirtualMachine virtualMachine = null;
        try{
            virtualMachine = VirtualMachine.attach(pid);
            virtualMachine.loadAgentPath(jarService.nativeAgent(),"");
            virtualMachine.loadAgent(jarService.agentJar(), sb.toString());
            vmMap.put(pid, virtualMachine);
        }catch(Exception e){
            logger.error("attach error: " + pid, e);
        } finally {
        }
    }
    
    @Override
    public void detach(String pid){
        logger.info("detach process {}", pid);
        if(!vmMap.contains(pid)){
            logger.warn("process not atached {}", pid);
            return;
        }
        
        VirtualMachine virtualMachine = vmMap.get(pid);
        vmMap.remove(pid);
        try{
            virtualMachine.detach();
        }catch(Exception e){
            logger.error("detach error: " + pid, e);
        }
    }
}

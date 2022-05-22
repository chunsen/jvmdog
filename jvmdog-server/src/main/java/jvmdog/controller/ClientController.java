package jvmdog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jvmdog.controller.model.ServerPagingDataResult;
import jvmdog.service.client.ClientService;
import jvmdog.service.client.model.Client;
import jvmdog.service.client.model.ClientProcess;

@RestController
@RequestMapping(value = "/client/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ClientController {
    
    @Autowired
    private ClientService clientService;
    
    @GetMapping
    public ServerPagingDataResult<Client> get(){
        List<Client> clients = clientService.getAll();
        return ServerPagingDataResult.from(clients);
    }
    
    @GetMapping(value="/{name}")
    public ServerPagingDataResult<ClientProcess> getProcessList(@PathVariable("name") String name){
        List<ClientProcess> processList = clientService.getProcessList(name);
        return ServerPagingDataResult.from(processList);
    }
    
    @PostMapping(value="/{name}/{command}")
    public void agentCommand(@PathVariable("name") String name, @PathVariable("command")String command, @RequestBody String body){
        clientService.runRemoteCommand(name, command, body);
    }
}

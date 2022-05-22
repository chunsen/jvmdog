package jvmdog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jvmdog.controller.model.ServerDataResult;
import jvmdog.server.log.LogService;

@RestController
@RequestMapping("/client-log/")
public class ClientLogController {
    
    @Autowired
    private LogService logService;

    @GetMapping(value="/{name}")
    public ServerDataResult<String> getLog(@PathVariable("name") String name){
        String log = logService.getClientLog(name);
        return ServerDataResult.from(log);
    }
}

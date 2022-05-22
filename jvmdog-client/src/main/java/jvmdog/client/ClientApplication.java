package jvmdog.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import jvmdog.client.core.service.impl.ClientServiceImpl;
import jvmdog.core.log.DogLogAppender;
import jvmdog.core.protocol.ProtocolManager;
import jvmdog.core.protocol.agent.CloseMessageHandler;
import jvmdog.protocol.api.DogClient;
import jvmdog.protocol.api.MessageHandler;

@SpringBootApplication
@ComponentScan(basePackages = {
        "jvmdog"
})
public class ClientApplication implements CommandLineRunner{
    private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);

    public static void main(String[] args){
        SpringApplication.run(ClientApplication.class, args);
    }

    
    @Autowired
    private ClientServiceImpl clientService;
    
    @Autowired
    private ClientCommandMessageHandler clientCommandMessageHandler;
    
    @Override
    public void run(String... args) throws Exception {
        List<MessageHandler> handlers = new ArrayList<>();
        handlers.add(clientCommandMessageHandler);
        handlers.add(new CloseMessageHandler());
        
        DogClient dogClient = ProtocolManager.get().client(clientService.server(), clientService.serverPort(), "client");
        try {
            createLog(dogClient);
            dogClient.connect(handlers);
        } catch (Exception e) {
            logger.error("ClientApplication error", e);
        }
    }
    
    private static DogLogAppender createLog(DogClient dogClient) {
        LoggerContext loggerContext =(LoggerContext)LoggerFactory.getILoggerFactory();
        DogLogAppender logAppender = new DogLogAppender(dogClient);
        logAppender.setContext(loggerContext);
        logAppender.setName("DogLogAppender");
        logAppender.start();
        
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(logAppender);
        
        return logAppender;
    }

}

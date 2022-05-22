package jvmdog.core.agent;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import jvmdog.core.command.bytecode.ByteCodeAgentCommand;
import jvmdog.core.command.code.CodeAgentCommand;
import jvmdog.core.command.dump.DumpAgentCommand;
import jvmdog.core.command.instance.InstanceCommand;
import jvmdog.core.command.objectmonitor.ObjectMonitorAgentCommand;
import jvmdog.core.command.objectquery.ObjectQueryCommand;
import jvmdog.core.command.objectupdate.ObjectUpdateCommand;
import jvmdog.core.command.reset.ResetAgentCommand;
import jvmdog.core.command.run.RunCommand;
import jvmdog.core.command.searchclass.SearchClassCommand;
import jvmdog.core.command.stack.StackCommand;
import jvmdog.core.log.DogLogAppender;
import jvmdog.core.protocol.ProtocolManager;
import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentCommandMessageHandler;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.core.protocol.agent.AgentStopCommandMessageHandler;
import jvmdog.core.utils.DogUtils;
import jvmdog.protocol.api.DogClient;
import jvmdog.protocol.api.MessageHandler;

public class InitCommand{
    private static final Logger logger = LoggerFactory.getLogger(InitCommand.class);
    
    private static Instrumentation instrumentation = null;
    
    static {
        DogUtils.init();
    }

    public static void run(Instrumentation inst, String options) {
        if(instrumentation != null) {
            logger.warn("Agent already inited.");
            return;
        }
        
        instrumentation = inst;
        List<MessageHandler> handlers = new ArrayList<>();
        
//        Map<String, AgentCommand> commandMap = new HashMap<>();
//        commandMap.put("dump", new DumpAgentCommand());
//        commandMap.put("bytecode", new ByteCodeAgentCommand());
//        commandMap.put("instance", new InstanceCommand());
//        commandMap.put("objectQuery", new ObjectQueryCommand());
//        commandMap.put("objectUpdate", new ObjectUpdateCommand());
//        commandMap.put("code", new CodeAgentCommand());
//        commandMap.put("reset", new ResetAgentCommand());
//        commandMap.put("objmon", new ObjectMonitorAgentCommand());
//        commandMap.put("searchClass", new SearchClassCommand());
//        commandMap.put("stack", new StackCommand());
//        commandMap.put("run", new RunCommand());
        
        AgentContext agentContext = new AgentContext(instrumentation);
        AgentCommandMessageHandler handler = new AgentCommandMessageHandler(agentContext);
        
        handlers.add(handler);
        handlers.add(new AgentStopCommandMessageHandler(agentContext));
//        handlerMap.put(DogMessageType.CLOSE_BY_SERVER.getValue(), new CloseMessageHandler());
        
        String[] items = options.split(",");
        String server = items[0];
        int port = Integer.parseInt(items[1]);
        DogLogAppender logAppender = null;
        try {
            logger.info("agent connection to server: {}@{}", port, server);
            DogClient dogClient = ProtocolManager.get().client(server, port, "agent");
            logAppender = createLog(dogClient);
            dogClient.connect(handlers);
            logger.warn("connection to server {}@{} end.", port, server);
        } catch (Exception e) {
            logger.error("InitCommand run error", e);
        } finally {
            instrumentation = null;
            agentContext.close();
            stopLogAppender(logAppender);
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
    
    private static void stopLogAppender(DogLogAppender logAppender) {
        if(logAppender == null) {
            return;
        }
        
        LoggerContext loggerContext =(LoggerContext)LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.detachAppender(logAppender);
        logAppender.stop();
    }

}

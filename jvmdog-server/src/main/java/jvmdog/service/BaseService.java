package jvmdog.service;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.RemoteCommand;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;
import jvmdog.server.utils.LongIdWorker;

public abstract class BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);
    
    protected final Map<String, DogConnection> connectionMap = new ConcurrentHashMap<>();
    
    @Autowired
    private LongIdWorker idworker;
    
    private final Map<String, Object> cmdWaitingMap = new ConcurrentHashMap<>();
    
    private final Map<String, RemoteCommand> commandMap = new HashMap<>();
    
    protected BaseService(RemoteCommand...remoteCommands) {
        for(RemoteCommand remoteCommand: remoteCommands) {
            commandMap.put(remoteCommand.name(), remoteCommand);
        }
    }
    
    public void add(DogConnection connection){
        connectionMap.put(connection.id(), connection);
        
        onNewConnection(connection);
    }
    
    protected void onNewConnection(DogConnection connection) {
        
    }
    
    public void disconnect(DogConnection connection) {
        if(connection != null) {
            DogConnection pre = connectionMap.remove(connection.id());
            if(pre != null) {
                logger.info("remove connection: {}", connection.id());
            } else {
                logger.warn("connection is null for remove result: {}", connection.id());
            }
        }
    }
    
    public DogConnection get(String name){
        return connectionMap.get(name);
    }
    
    public void commandStopResponse(String command, byte[] responseData){
        ResponseMessageData responseMessageData = SerializeUtils.deserialize(responseData, ResponseMessageData.class);
        
        logger.info("receive response for command stop {} with id {}", command, responseMessageData.getId());
        onResponse(responseMessageData);
    }
    
    public void commandResponse(String command, byte[] responseData){
        RemoteCommand remoteCommand = commandMap.get(command);
        if(remoteCommand == null) {
            logger.error("commandResponse, invalid command: {}", command);
            return;
        }
        
        MessageData commandData = SerializeUtils.deserialize(responseData, remoteCommand.responseClass());
        ResponseDataHandler responseDataHandler = getResponseDataHandler(command);
        if(responseDataHandler != null) {
            MessageData handleResult = responseDataHandler.handle(commandData);
            if(handleResult != null) {
                commandData = handleResult;
            }
        }
        
        logger.info("receive response for command {} with id {}", command, commandData.getId());
        onResponse(commandData);
    }
    
    protected ResponseDataHandler getResponseDataHandler(String command) {
        return null;
    }
    
    private void onResponse(MessageData messageData) {
        String id = messageData.getId();
        if(!cmdWaitingMap.containsKey(id)){
            logger.error("cmdWaitingMap does not contains key {}", id);
            return;
        }
        CountDownLatch countDownLatch = (CountDownLatch)cmdWaitingMap.get(id);
        if(countDownLatch ==null){
            logger.error("countDownLatch is null for key {}", id);
            return;
        }
        cmdWaitingMap.put(id, messageData);
        
        countDownLatch.countDown();
    }
    
    public synchronized ResponseMessageData stopRemoteCommand(String name, String command, String data) {
        DogConnection connection = connectionMap.get(name);
        if(connection ==null ){
            logger.error("connection not exists: {}", name);
            return null;
        }
        
        MessageData messageData = new Gson().fromJson(data, MessageData.class);
        DogMessage message =DogMessage.clientCommandStop();
        message.setHeader(SerializeUtils.fromString(command));
        message.setData(SerializeUtils.serialize(messageData));

        return run(connection, command, messageData.getId(), message);
    }
    
    public synchronized <T extends MessageData> T  runRemoteCommand(String name, String command, String data){
        DogConnection connection = connectionMap.get(name);
        if(connection ==null){
            logger.error("connection not exists: {}", name);
            return null;
        }
        
        if("close".equals(command)){
            close(connection);
            logger.info("close connection: {}", name);
            return null;
        }
        
        String id = String.valueOf(idworker.nextId());
        DogMessage dogMessage =buildDogMessage(id, command, data);
        if(dogMessage == null){
            logger.error("build message null: {}", command);
            return null;
        }
        
        return run(connection, command, id, dogMessage);
    }
    
    public synchronized <T extends MessageData> T  runRemoteCommand(String name, String command, MessageData commandData){
        DogConnection connection = connectionMap.get(name);
        if(connection ==null){
            logger.error("connection not exists: {}", name);
            return null;
        }
        
        String id = String.valueOf(idworker.nextId());
        DogMessage dogMessage =DogMessage.clientCommand();
        dogMessage.setHeader(command.getBytes(Charset.forName("utf-8")));
        commandData.setId(id);
        dogMessage.setData(SerializeUtils.serialize(commandData));
        
        return run(connection, command, id, dogMessage);
    }
    
    private <T extends MessageData> T  run(DogConnection connection, String command, String id, DogMessage dogMessage) {
        RemoteCommand remoteCommand = commandMap.get(command);
        if(remoteCommand instanceof DogMessageHandler) {
            DogMessage handleResult = ((DogMessageHandler)remoteCommand).handle(dogMessage);
            if(handleResult != null) {
                dogMessage = handleResult;
            }
        }
        
        logger.info("run remote command: {},{}", connection.id(), command);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        cmdWaitingMap.put(id, countDownLatch);
        try {
            connection.send(dogMessage);
            countDownLatch.await();
        } catch (Exception e) {
            logger.error("run error:" + connection.id() + ","+ command, e);
        }
        T result = (T)cmdWaitingMap.get(id);
        return result;
    }
    
    private void close(DogConnection connection) {
        DogMessage message = DogMessage.from(DogMessageType.CLOSE_BY_SERVER.getValue());
        message.setHeader(SerializeUtils.fromString("close"));
        message.setData(null);
        
        try {
            connection.send(message);
            connection.close();
        } catch (Exception e) {
            logger.error("close connection error:"+ connection.id(), e);
        }
    }
    
    protected DogMessage buildDogMessage(String id, String command, String data) {
        DogMessage message =DogMessage.clientCommand();
        message.setHeader(SerializeUtils.fromString(command));
        
        RemoteCommand remoteCommand = commandMap.get(command);
        if(remoteCommand == null) {
            return null;
        }
        MessageData commandData = null;
        if(data == null || data.length() ==0) {
            commandData = new MessageData();
        } else {
            commandData = new Gson().fromJson(data, remoteCommand.requestClass());
        }
        commandData.setId(id);

        message.setData(SerializeUtils.serialize(commandData));

        return message;
    }
}

package jvmdog.controller;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import jvmdog.service.AgentLogEvent;

@Service
@ServerEndpoint("/ws-agent/{agentId}")
public class AgentEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(AgentEndpoint.class);

    private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

    //发送消息
    private void sendMessage(Session session, String message) throws IOException {
        if(session != null){
            synchronized (session) {
                session.getBasicRemote().sendText(message);
            }
        }
    }

    @EventListener
    public void handleAgentLog(AgentLogEvent agentLogEvent) {
        Session session = sessionPools.get(agentLogEvent.getAgentId());
        try {
            sendMessage(session, agentLogEvent.getLog());
        }catch (Exception e){
            logger.error("handleAgentLog error:" + agentLogEvent.getAgentId(), e);
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "agentId") String agentId){
        sessionPools.put(agentId, session);
        
        try {
            sendMessage(session, "欢迎" + agentId + "加入连接！");
        } catch (IOException e) {
            logger.error("onOpen error:" + agentId, e);
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam(value = "agentId") String agentId){
        sessionPools.remove(agentId);
        logger.info("onclose: {}", agentId);
    }

    @OnMessage
    public void onMessage(@PathParam(value = "agentId") String agentId, String message) throws IOException{
        message = "客户端：" + message + ",已收到";
        //logger.info("onMessage: {}" ,message);
//        for (Session session: sessionPools.values()) {
//            try {
//                sendMessage(session, message);
//            } catch(Exception e){
//                e.printStackTrace();
//                continue;
//            }
//        }
    }

    @OnError
    public void onError(Session session, @PathParam(value = "agentId") String agentId, Throwable throwable){
        logger.error("onError:" + agentId, throwable);
    }

}

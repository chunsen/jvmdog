package jvmdog.netty.channelhandler;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.protocol.api.model.HeartBeatData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class HeartBeatRequestHandler extends SimpleChannelInboundHandler<DogMessage>{
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatRequestHandler.class);

    private volatile ScheduledFuture<?> heartbeatFuture;
    private final String type;
    private final String pid;
    
    public HeartBeatRequestHandler(String type, String pid){
        this.type = type;
        this.pid = pid;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        heartbeatFuture = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(type, pid, ctx), 0L, 30L, TimeUnit.SECONDS);
        ctx.fireChannelActive();
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DogMessage msg) throws Exception {
        if(msg.getType() == DogMessageType.HEARTBEAT.getValue()){
            logger.debug("recive heartbeat from server {}", ctx.channel().remoteAddress());
            return;
        } else {
            ctx.fireChannelRead(msg);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(heartbeatFuture != null){
            logger.info("exceptionCaught, cancel heartbeat with server {}", ctx.channel().remoteAddress());
            heartbeatFuture.cancel(true);
            heartbeatFuture = null;
        }
        ctx.fireExceptionCaught(cause);
    }
    
    private static class HeartBeatTask implements Runnable{
        
        private final ChannelHandlerContext ctx;
        private final String type;
        private final String pid;
        
        public HeartBeatTask(String type, String pid, ChannelHandlerContext ctx){
            this.type = type;
            this.pid = pid;
            this.ctx =ctx;
        }

        @Override
        public void run() {
            logger.info("send heartbeat message to server: {}", ctx.channel().remoteAddress());
            this.ctx.writeAndFlush(buildHeartBeatMessage());
        }
        
        private DogMessage buildHeartBeatMessage(){
            DogMessage message = DogMessage.from(DogMessageType.HEARTBEAT.getValue());
            message.setHeader(type.getBytes());
            
            HeartBeatData data = new HeartBeatData();
            data.setPid(pid);
            data.setType(type);
            message.setData(SerializeUtils.serialize(data));
            
            return message;
        }
        
    }

}

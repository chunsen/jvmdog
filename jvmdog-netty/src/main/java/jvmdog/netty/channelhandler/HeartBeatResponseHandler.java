package jvmdog.netty.channelhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;

public class HeartBeatResponseHandler extends SimpleChannelInboundHandler<DogMessage>{
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatResponseHandler.class);

    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DogMessage msg) throws Exception {
        if(msg.getType() == DogMessageType.HEARTBEAT.getValue()){
            logger.debug("recive heartbeat from client {}", ctx.channel().remoteAddress());
            ctx.writeAndFlush(buildHeartBeatMessage());
            return;
        } else {
            ctx.fireChannelRead(msg);
        }
    }
    
    
    private DogMessage buildHeartBeatMessage(){
        DogMessage message = DogMessage.from(DogMessageType.HEARTBEAT.getValue());
        message.setHeader("server".getBytes());
        message.setData(null);
        
        return message;
    }

}

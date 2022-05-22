package jvmdog.netty.channelhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jvmdog.netty.NettyDogClient;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;

public class CloseHandler extends SimpleChannelInboundHandler<DogMessage>{
    private static final Logger logger = LoggerFactory.getLogger(CloseHandler.class);
    
    private final NettyDogClient nettyDogClient;
    
    public CloseHandler(NettyDogClient nettyDogClient) {
        super();
        this.nettyDogClient = nettyDogClient;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DogMessage msg) throws Exception {
        if(msg.getType() == DogMessageType.CLOSE_BY_SERVER.getValue()){
            logger.debug("recive close from server {}", ctx.channel().remoteAddress());
            nettyDogClient.close();
            return;
        } else {
            ctx.fireChannelRead(msg);
        }
    }

}

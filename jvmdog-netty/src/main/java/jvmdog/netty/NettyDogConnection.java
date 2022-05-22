package jvmdog.netty;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.MessageHandler;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.protocol.api.model.OSInfo;
import jvmdog.protocol.api.model.RegistrationData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class NettyDogConnection extends SimpleChannelInboundHandler<DogMessage> implements DogConnection {
    private static final Logger logger = LoggerFactory.getLogger(NettyDogConnection.class);
    private final List<MessageHandler> messageHandlers;
    private String id;
    private String ip;
    private String peerPid;
    private String type;
    private OSInfo osInfo;
    private volatile ChannelHandlerContext context;
    private volatile boolean close = false;
    
    private final Consumer<DogConnection> onChannelActive;
    private final Consumer<DogConnection> onChannelInactive;
    
    public NettyDogConnection(List<MessageHandler> messageHandlers, Consumer<DogConnection> onChannelActive, Consumer<DogConnection> onChannelInactive) {
        this.messageHandlers = messageHandlers;
        this.onChannelActive = onChannelActive;
        this.onChannelInactive = onChannelInactive;
    }
    
    public boolean isClose(){
        return close;
    }
    
    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String ip() {
        return ip;
    }
    
    @Override
    public String peerPid() {
        return this.peerPid;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public void close(){
        close = true;
        if(context ==null){
            return;
        }
        context.close();
        context = null;
    }
   
    @Override
    public void send(DogMessage dogMassage) {
        if(context == null){
            logger.warn("context is null");
            return;
        }
        
        if(dogMassage == null){
            logger.warn("dogMassage is null");
            return;
        }
        context.writeAndFlush(dogMassage);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive: {}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
        this.context = ctx;
        if(onChannelActive !=null){
            onChannelActive.accept(this);
        }
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warn("channelInactive: {}" , ctx.channel().remoteAddress());
        if(onChannelInactive!=null){
            onChannelInactive.accept(this);
        }
        this.context = null;
        super.channelInactive(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DogMessage msg) throws Exception {
        logger.debug("channelRead0: {}, {}", ctx.channel().remoteAddress(), msg.getType());
        if(messageHandlers ==null || messageHandlers.isEmpty()){
            return;
        }
        
        try{
            
            boolean handled = false;
            if(DogMessageType.REGISTRATION.getValue() == msg.getType()) {
                RegistrationData registrationMessage = SerializeUtils.deserialize(msg.getData(), RegistrationData.class);
                this.type = registrationMessage.getType();
                this.id = registrationMessage.getPid() + "@" + this.ip;
                this.peerPid = registrationMessage.getPid();
                this.osInfo = registrationMessage.getOsInfo();
                handled = true;
            }
            
            for(MessageHandler messageHandler: messageHandlers){
                if(msg.getType() == messageHandler.type() || messageHandler.type() == DogMessageType.ALL.getValue()){
                    messageHandler.handle(msg, this);
                    handled = true;
                }
            }
            if(!handled) {
                logger.error("unkown message type: {}", msg.getType());
            }
        }catch(Throwable e){
            logger.error("channelRead0 error", e);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught: "+ctx.channel().remoteAddress(), cause);
        this.context = null;
        ctx.close();
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
        logger.debug("channelReadComplete: {}", ctx.channel().remoteAddress());
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public OSInfo osInfo() {
        // TODO Auto-generated method stub
        return this.osInfo;
    }

}

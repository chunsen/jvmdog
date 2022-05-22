package jvmdog.netty;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jvmdog.netty.channelhandler.CloseHandler;
import jvmdog.netty.channelhandler.HeartBeatRequestHandler;
import jvmdog.netty.channelhandler.MessageDecoder;
import jvmdog.netty.channelhandler.MessgeEncoder;
import jvmdog.protocol.api.DogClient;
import jvmdog.protocol.api.MessageHandler;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.OSInfo;
import jvmdog.protocol.api.model.RegistrationData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class NettyDogClient implements DogClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyDogClient.class);
    
    private static final OSInfo OS_INFO = OSInfo.getInstance();
    
    private ScheduledExecutorService executor =  Executors.newScheduledThreadPool(1);
    private final String server;
    private final int port;
    private final String type;
    private final String pid;
    private List<MessageHandler> messageHandlers;
    
    private NettyDogConnection dogConnection;
    
    public NettyDogClient(String server, int port, String type) {
        this.server = server;
        this.port = port;
        this.type = type;
        this.pid = getPid();
    }

    @Override
    public void connect(List<MessageHandler> messageHandlers) {
        this.messageHandlers = messageHandlers;
        
        try{
            connectInternal(null);
        }catch(Exception e){
            logger.error("reconnect server error,", e);
        }
    }
    
    private static String getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"
        try {
            return name.substring(0, name.indexOf('@'));
        } catch (Exception e) {
            return "-1";
        }
    }
    
    private void connectInternal(final Supplier<DogMessage> supplier){
        logger.info("connect to server- {}:{}", server, port);
        this.dogConnection= new NettyDogConnection(messageHandlers, connection-> {
            DogMessage dogMessage = DogMessage.registration();
            RegistrationData registration = new RegistrationData();
            registration.setPid(pid);
            registration.setType(type);
            registration.setOsInfo(OS_INFO);
            dogMessage.setData(SerializeUtils.serialize(registration));
            connection.send(dogMessage);
        }, null);
        
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
            .option(ChannelOption.SO_KEEPALIVE,true)
            .channel(NioSocketChannel.class).
                    handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            logger.info("initChannel to server- {}:{}", server, port);
                            
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder", new MessageDecoder());
                            pipeline.addLast("encoder", new MessgeEncoder());
                            pipeline.addLast("closeHandler", new CloseHandler(NettyDogClient.this));
                            pipeline.addLast("heartbeatHandler", new HeartBeatRequestHandler(type, pid));
                            pipeline.addLast("DogChannelHandler", dogConnection);
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(server, port).sync();
            channelFuture.channel().closeFuture().sync();
            logger.warn("dogClient closed");
        } catch(Exception e){
            logger.error("dogClient connect error", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
            
            if(!dogConnection.isClose()){
                executor.execute(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            TimeUnit.SECONDS.sleep(5);
                            connectInternal(supplier);
                        }catch(Exception e){
                            logger.error("reconnect server error,", e);
                        }
                    }
                });
            }
        }
    }
    
    @Override
    public void send(DogMessage dogMessage) {
        if(dogMessage == null) {
            return;
        }
        
//        logger.info("send dogMessage[type={}] to server: {}", dogMessage.getType(), this.server);
        if(dogConnection==null || dogConnection.isClose()) {
            logger.error("dogConnection is null or closed");
            return;
        }
        
        dogConnection.send(dogMessage);
    }

    @Override
    public void close() {
        dogConnection.close();
    }

}

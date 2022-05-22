package jvmdog.netty;

import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jvmdog.netty.channelhandler.MessageDecoder;
import jvmdog.netty.channelhandler.MessgeEncoder;
import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.DogMessageBuilder;
import jvmdog.protocol.api.DogServer;
import jvmdog.protocol.api.MessageHandler;
import jvmdog.protocol.api.model.DogMessage;

public class NettyDogServer implements DogServer {
    private static Logger logger = LoggerFactory.getLogger(NettyDogServer.class);
    
    private final int port;
    private Consumer<DogConnection> onConnect;
    private Consumer<DogConnection> onDisconnect;

    public NettyDogServer(int port) {
        this.port = port;
    }

    @Override
    public void start(List<MessageHandler> messageHandlers) {
        ServerBootstrap b = new ServerBootstrap();
        final NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
        .childOption(ChannelOption.ALLOCATOR, new UnpooledByteBufAllocator(true))
        .childOption(ChannelOption.SO_KEEPALIVE,true)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
//                        String id = String.format("%s_%s", ch.remoteAddress().getHostName().replace('.', '-'), ch.remoteAddress().getPort());
                        NettyDogConnection dogConnection = new NettyDogConnection(messageHandlers, onConnect, onDisconnect);
                        String ip= ch.remoteAddress().getAddress().toString().replace('.', '-').substring(1);
                        dogConnection.setIp(ip);
                        ch.pipeline()
                                .addLast("decoder", new MessageDecoder())   // 1
                                .addLast("encoder", new MessgeEncoder())  // 2
                                .addLast("handler", dogConnection);        // 4
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                logger.warn("JVM shutdown, shutdown NioEventLoopGroup");
                group.shutdownGracefully();
            }
        }));

        try {
            b.bind(port).sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection(DogConnection connection) {
        DogMessage dogMessage = DogMessageBuilder.buildClose();
        connection.send(dogMessage);
    }

    @Override
    public void onConnect(Consumer<DogConnection> onConnect) {
        this.onConnect = onConnect;
    }

    @Override
    public void onDisconnect(Consumer<DogConnection> onDisconnect) {
        this.onDisconnect = onDisconnect;
    }

}

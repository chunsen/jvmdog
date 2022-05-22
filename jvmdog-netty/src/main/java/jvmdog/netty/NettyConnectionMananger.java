package jvmdog.netty;

import jvmdog.protocol.api.ConnectionMananger;
import jvmdog.protocol.api.DogClient;
import jvmdog.protocol.api.DogServer;

public class NettyConnectionMananger implements ConnectionMananger {

    @Override
    public DogServer server(int port) {
        return new NettyDogServer(port);
    }

    @Override
    public DogClient client(String server, int port, String type) {
        return new NettyDogClient(server, port, type);
    }

}

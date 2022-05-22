package jvmdog.core.protocol;

import java.util.ServiceLoader;

import jvmdog.protocol.api.ConnectionMananger;

public class ProtocolManager {
    public static ConnectionMananger get(){
        ServiceLoader<ConnectionMananger> connectionManangerServiceLoader = ServiceLoader.load(ConnectionMananger.class);
        ConnectionMananger connectionManager = connectionManangerServiceLoader.iterator().next();
        return connectionManager;
    }
}

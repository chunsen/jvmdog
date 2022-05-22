package jvmdog.protocol.api;

public interface ConnectionMananger {
    DogServer server(int port);
    DogClient client(String server, int port, String type);
}

package jvmdog.protocol.api;

import java.util.List;
import java.util.function.Consumer;

public interface DogServer {
    void start(List<MessageHandler> messageHandlers);
    void closeConnection(DogConnection connection);
    void onConnect(Consumer<DogConnection> dogConnection);
    void onDisconnect(Consumer<DogConnection> dogConnection);
}

package jvmdog.protocol.api.model;

public enum DogMessageType {
    CLOSE_BY_SERVER("close_by_server", -1),
    ALL("all", 0),
    HEARTBEAT("heartbeat", 1), 
    REGISTRATION("registration", 2),
    LOG("log", 3),
    CLIENT_COMMAND("clientCommand", 10),
    CLIENT_COMMAND_STOP("clientCommandStop", 20),
    CLIENT_COMMAND_STOP_RESPONSE("clientCommandStopResponse", 21),
    CLIENT_COMMAND_RESPONSE("clientCommandResponse", 11);
    
    private int value;
    private String name;
    DogMessageType(String name, int value){
        this.value = value;
        this.name = name;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}

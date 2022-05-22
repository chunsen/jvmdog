package jvmdog.protocol.api;

import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.protocol.api.utils.SerializeUtils;

public class DogMessageBuilder {

    public static DogMessage buildClose(){
        DogMessage message = DogMessage.from(DogMessageType.CLOSE_BY_SERVER.getValue());
        message.setHeader(SerializeUtils.fromString("close"));
        message.setData(null);
        
        return message;
    }
    
    public static DogMessage buildClientCommand(String header, byte[] data){
        DogMessage message = DogMessage.clientCommand();
        message.setHeader(SerializeUtils.fromString(header));
        message.setData(data);
        
        return message;
    }
}

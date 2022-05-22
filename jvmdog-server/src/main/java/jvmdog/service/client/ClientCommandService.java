package jvmdog.service.client;

import java.nio.charset.Charset;

import org.springframework.stereotype.Service;

import jvmdog.client.core.AttachRequestData;
import jvmdog.client.core.JPSRequestData;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.utils.SerializeUtils;

@Service
public class ClientCommandService {
    
    
    public DogMessage command(String command, String id, String data) {
        if("objmon".equals(command) ) {
            return build(command, data);
        } else if( "attach".equals(command) ) {
            return attach(id, data);
        }  else if( "detach".equals(command) ) {
            return build(command, data);
        } else if( "jps".equals(command) ) {
            return jps(id);
        } else if( "close".equals(command) ) {
            return build(command, null);
        }

        return null;
    }
    private DogMessage jps(String id) {
        DogMessage message = DogMessage.clientCommand();
        message.setHeader("jps".getBytes(Charset.forName("utf-8")));
        JPSRequestData request = new JPSRequestData();
        request.setId(id);
        byte[] data = SerializeUtils.serialize(request);
        message.setData(data);

        return message;
    }
    
    private DogMessage attach(String id, String pid) {
        DogMessage message = DogMessage.clientCommand();
        message.setHeader("attach".getBytes(Charset.forName("utf-8")));
        AttachRequestData request = new AttachRequestData();
        request.setId(id);
        request.setPid(pid);
        byte[] data = SerializeUtils.serialize(request);
        message.setData(data);

        return message;
    }

    private DogMessage build(String command, String data) {
        DogMessage message = DogMessage.clientCommand();
        message.setHeader(command.getBytes(Charset.forName("utf-8")));
        if(data != null && data.length()>0){
            message.setData(data.getBytes(Charset.forName("utf-8")));
        } else {
            message.setData(null);
        }

        return message;
    }
}

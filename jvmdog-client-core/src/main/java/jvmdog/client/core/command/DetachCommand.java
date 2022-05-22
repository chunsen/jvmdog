package jvmdog.client.core.command;

import java.nio.charset.Charset;

import org.springframework.stereotype.Service;

import jvmdog.client.core.service.ClientService;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;

@Service
public class DetachCommand implements ClientCommand{
    private ClientService clientService;

    public DetachCommand(ClientService clientService) {
        this.clientService = clientService;
    }
    
    @Override
    public String name() {
        return "detach";
    }

    @Override
    public DogMessage run(byte[] data) {
        String pid = new String(data, Charset.forName("utf-8"));
        clientService.detach(pid);
        return null;
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        // TODO Auto-generated method stub
        return null;
    }
    
}

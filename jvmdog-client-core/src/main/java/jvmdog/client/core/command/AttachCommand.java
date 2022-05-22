package jvmdog.client.core.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jvmdog.client.core.AttachRequestData;
import jvmdog.client.core.service.ClientService;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

@Service
public class AttachCommand implements ClientCommand{
    private static final Logger logger = LoggerFactory.getLogger(AttachCommand.class);
    
    private final ClientService clientService;

    public AttachCommand(ClientService clientService) {
        this.clientService = clientService;
    }
    
    @Override
    public String name() {
        return "attach";
    }

    @Override
    public DogMessage run(byte[] data) {
        AttachRequestData attachRequest = SerializeUtils.deserialize(data, AttachRequestData.class);
        clientService.attach(attachRequest.getPid());
        logger.info("AttachCommand, pid={}", attachRequest);
        
        DogMessage response = DogMessage.clientResponse("attach");
        ResponseMessageData commandMessage = new ResponseMessageData();
        commandMessage.setId(attachRequest.getId());
        response.setData(SerializeUtils.serialize(commandMessage));
        return response;
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return AttachRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        // TODO Auto-generated method stub
        return ResponseMessageData.class;
    }

}

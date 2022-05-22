package jvmdog.client.core.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jvmdog.client.core.UpdateJarRequestData;
import jvmdog.client.core.service.JarService;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

@Service
public class UpdateJarCommand implements ClientCommand{

    @Autowired
    private JarService jarService;
    

    @Override
    public String name() {
        return "updateJar";
    }

    @Override
    public DogMessage run(byte[] data) {
        UpdateJarRequestData request = SerializeUtils.deserialize(data, UpdateJarRequestData.class);
        
        DogMessage response = DogMessage.clientResponse(name());
        ResponseMessageData responseData = new ResponseMessageData();
        responseData.setId(request.getId());
        
        if(request.getAgentJar()!=null) {
            jarService.updateAgentJar(request.getAgentJar());
        }
        
        if(request.getCoreJar()!=null) {
            jarService.updateCoreJar(request.getCoreJar());
        }
        
        if(request.getNativeAgent()!=null) {
            jarService.updateNativeAgent(request.getNativeAgent());
        }
        
        response.setData(SerializeUtils.serialize(responseData));
        return response;
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return UpdateJarRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return ResponseMessageData.class;
    }

}

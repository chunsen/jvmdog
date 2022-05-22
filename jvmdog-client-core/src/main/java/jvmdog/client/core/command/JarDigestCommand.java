package jvmdog.client.core.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jvmdog.client.core.JarDigestRequestData;
import jvmdog.client.core.JarDigestResponseData;
import jvmdog.client.core.service.JarService;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

@Service
public class JarDigestCommand implements ClientCommand{

    @Autowired
    private JarService jarService;
    

    @Override
    public String name() {
        return "jarDigest";
    }

    @Override
    public DogMessage run(byte[] data) {
        JarDigestRequestData request = SerializeUtils.deserialize(data, JarDigestRequestData.class);
        
        DogMessage response = DogMessage.clientResponse(name());
        JarDigestResponseData responseData = new JarDigestResponseData();
        responseData.setId(request.getId());
        
        String coreJarMD5 = jarService.coreJarMD5();
        responseData.setCoreJar(coreJarMD5);
        responseData.setAgentJar(jarService.agentJarMD5());
        responseData.setNativeAgent(jarService.nativeAgentMD5());
        
        response.setData(SerializeUtils.serialize(responseData));
        return response;
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return JarDigestRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return JarDigestResponseData.class;
    }

}

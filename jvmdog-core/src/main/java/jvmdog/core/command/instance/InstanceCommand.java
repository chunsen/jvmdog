package jvmdog.core.command.instance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.core.utils.DogUtils;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class InstanceCommand implements AgentCommand {
    private static final Logger logger = LoggerFactory.getLogger(InstanceCommand.class);

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        InstanceRequestData requestData = SerializeUtils.deserialize(data, InstanceRequestData.class);

        InstanceResponseData responseData = new InstanceResponseData();
        responseData.setId(requestData.getId());

        try {
            Object[] objects = DogUtils.getInstances(requestData.getClassName());
            if(objects != null && objects.length>0) {
                List<String> instances = new ArrayList<>();
                for(Object obj: objects) {
                    instances.add(String.valueOf(obj.hashCode()) +"@" + obj.getClass().getName());
                }
                responseData.setInstances(instances);
            }
        } catch (Throwable e) {
            logger.error("InstanceCommand error", e);
            responseData.setCode(ResponseMessageData.CODE_ERROR);
            responseData.setMessage(e.getMessage());
        }

        byte[] messageData = SerializeUtils.serialize(responseData);
        DogMessage message = DogMessage.clientResponse("instance");
        message.setData(messageData);

        return message;
    }

    @Override
    public String name() {
        return "instance";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return InstanceRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return InstanceResponseData.class;
    }

}

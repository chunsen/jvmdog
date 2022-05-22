package jvmdog.core.command.searchclass;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class SearchClassCommand implements AgentCommand {
    private static final Logger logger = LoggerFactory.getLogger(SearchClassCommand.class);

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        SearchClassRequestData searchClassRequestData = SerializeUtils.deserialize(data, SearchClassRequestData.class);

        SearchClassResponseData responseData = new SearchClassResponseData();
        responseData.setId(searchClassRequestData.getId());

        try {
            String className = searchClassRequestData.getClassName();
            List<ClassInfo> classInfos = new ArrayList<>();
            for (Class<?> clazz : agentContext.getAllLoadedClasses()) {
                if (clazz.getName().startsWith(className)) {
                    ClassInfo clsInfo = new ClassInfo();
                    clsInfo.setClassName(clazz.getName());
                    if (clazz.getClassLoader() != null) {
                        clsInfo.setClassLoader(clazz.getClassLoader().toString());
                    }

                    classInfos.add(clsInfo);
                }
            }
            responseData.setClassInfos(classInfos);

        } catch (Throwable e) {
            logger.error("SearchClassCommand error", e);
            responseData.setCode(ResponseMessageData.CODE_ERROR);
            responseData.setMessage(e.getMessage());
        }

        byte[] messageData = SerializeUtils.serialize(responseData);
        DogMessage message = DogMessage.clientResponse("searchClass");
        message.setData(messageData);

        return message;
    }

    @Override
    public String name() {
        return "searchClass";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return SearchClassRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return SearchClassResponseData.class;
    }

}

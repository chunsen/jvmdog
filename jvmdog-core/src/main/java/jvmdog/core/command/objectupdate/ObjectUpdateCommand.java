package jvmdog.core.command.objectupdate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class ObjectUpdateCommand implements AgentCommand {
    private static final Logger logger = LoggerFactory.getLogger(ObjectUpdateCommand.class);

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        jvmdog.core.sql.ObjectSchemaFactory.INSTANCE.setInstrumentation(agentContext.getInst());
        
        ObjectUpdateRequestData requestData = SerializeUtils.deserialize(data, ObjectUpdateRequestData.class);

        ObjectUpdateResponseData responseData = new ObjectUpdateResponseData();
        responseData.setId(requestData.getId());

        try {
            int rowCount = update(requestData.getQuery());
            responseData.setRowCount(rowCount);
        } catch (Throwable e) {
            logger.error("ObjectQueryCommand error", e);
            responseData.setCode(ResponseMessageData.CODE_ERROR);
            responseData.setMessage(e.getMessage());
        }

        byte[] messageData = SerializeUtils.serialize(responseData);
        DogMessage message = DogMessage.clientResponse("objectUpdate");
        message.setData(messageData);

        return message;
    }
    
    private static int update(String sql){
        Properties info = new Properties();
        info.put("schemaFactory", "jvmdog.core.sql.ObjectSchemaFactory");
        info.put("schemaType", "custom");
        info.put("schema", "OBJECTS");
        
        try {
            Class.forName("org.apache.calcite.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            throw new RuntimeException(e1);
        }
        
        try (Connection conn = DriverManager.getConnection("jdbc:calcite:", info);
            Statement stat = conn.createStatement()) {
            return stat.executeUpdate(sql);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name() {
        return "objectUpdate";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return ObjectUpdateRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return ObjectUpdateResponseData.class;
    }

}

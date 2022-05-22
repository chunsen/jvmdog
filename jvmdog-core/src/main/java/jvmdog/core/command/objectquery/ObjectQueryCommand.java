package jvmdog.core.command.objectquery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class ObjectQueryCommand implements AgentCommand {
    private static final Logger logger = LoggerFactory.getLogger(ObjectQueryCommand.class);

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        jvmdog.core.sql.ObjectSchemaFactory.INSTANCE.setInstrumentation(agentContext.getInst());
        
        ObjectQueryRequestData requestData = SerializeUtils.deserialize(data, ObjectQueryRequestData.class);

        ObjectQueryResponseData responseData = new ObjectQueryResponseData();
        responseData.setId(requestData.getId());

        try {
            List<Map<String,Object>> objects = objects(requestData.getQuery());
            responseData.setResult(objects);
        } catch (Throwable e) {
            logger.error("ObjectQueryCommand error", e);
            responseData.setCode(ResponseMessageData.CODE_ERROR);
            responseData.setMessage(e.getMessage());
        }

        byte[] messageData = SerializeUtils.serialize(responseData);
        DogMessage message = DogMessage.clientResponse("objectQuery");
        message.setData(messageData);

        return message;
    }
    
    private static List<Map<String,Object>> objects(String sql){
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
            final ResultSet resultSet = stat.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            
            List<Map<String,Object>> result = new ArrayList<>();
            while(resultSet.next()){
                Map<String,Object> item = new HashMap<>();
                for(int i=1; i<=resultSetMetaData.getColumnCount(); i++) {
                    item.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i).toString());
                }
                result.add(item);
            }
            System.out.println(result);
            
            return result;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String name() {
        return "objectQuery";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return ObjectQueryRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return ObjectQueryResponseData.class;
    }

}

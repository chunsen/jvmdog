package jvmdog.protocol.server;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;
import jvmdog.protocol.api.model.HeartBeatData;
import jvmdog.protocol.api.utils.SerializeUtils;

@Service
public class HeartbeatMessgeHandler implements ServerMessgeHandler{
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatMessgeHandler.class);
    
//    @Autowired
//    private AgentService agentService;
//    
//    @Autowired
//    private ClientService clisentService;
    
    @Override
    public void handle(DogMessage inputMessage, DogConnection connection) {
        byte[] header = inputMessage.getHeader();
        String peerType =  new String(header, Charset.forName("UTF-8"));
        HeartBeatData data = SerializeUtils.deserialize(inputMessage.getData(), HeartBeatData.class);
        logger.info("Receive Heartbeat from {} :{}@{}", peerType, data.getPid(), connection.ip());
    }

    @Override
    public int type() {
        return DogMessageType.HEARTBEAT.getValue();
    }
}

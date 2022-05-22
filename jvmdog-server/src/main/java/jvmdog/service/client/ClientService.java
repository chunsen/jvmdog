package jvmdog.service.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jvmdog.client.core.JPSResponseData;
import jvmdog.client.core.JarDigestResponseData;
import jvmdog.client.core.JvmInfo;
import jvmdog.client.core.UpdateJarRequestData;
import jvmdog.client.core.command.ClientCommand;
import jvmdog.protocol.api.DogConnection;
import jvmdog.protocol.api.RemoteCommand;
import jvmdog.service.BaseService;
import jvmdog.service.agent.Agent;
import jvmdog.service.agent.AgentService;
import jvmdog.service.client.model.Client;
import jvmdog.service.client.model.ClientProcess;

@Service
public class ClientService extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private ClientJarService clientJarService;
    
    @Autowired
    private AgentService agentService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ClientService(List<ClientCommand> remoteCommands) {
        super(remoteCommands.toArray(new RemoteCommand[0]));
    }

    @Override
    protected void onNewConnection(DogConnection connection) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    JarDigestResponseData response = runRemoteCommand(connection.id(), "jarDigest", "{}");
                    String coreJarMD5 = clientJarService.coreJarMD5();

                    UpdateJarRequestData updateJarRequestData = new UpdateJarRequestData();
                    if (coreJarMD5 != null && !coreJarMD5.equals(response.getCoreJar())) {
                        logger.warn("Need update coreJar for client {}, {}=>{} ", connection.id(),
                            response.getCoreJar(), coreJarMD5);
                        updateJarRequestData.setCoreJar(clientJarService.coreJarData());
                    }
                    String agentJarMD5 = clientJarService.agentJarMD5();
                    if (agentJarMD5 != null && !agentJarMD5.equals(response.getAgentJar())) {
                        logger.warn("Need update agentJar for client {}, {}=>{} ", connection.id(),
                            response.getAgentJar(), agentJarMD5);
                        updateJarRequestData.setAgentJar(clientJarService.agentJarData());
                    }
                    
                    String nativeAgentMD5 = clientJarService.nativeAgentMD5(connection.osInfo());
                    if (nativeAgentMD5 != null && !nativeAgentMD5.equals(response.getNativeAgent())) {
                        logger.warn("Need update nativeAgent for client {}, {}=>{} ", connection.id(),
                            response.getNativeAgent(), nativeAgentMD5);
                        updateJarRequestData.setNativeAgent(clientJarService.nativeAgentData(connection.osInfo()));
                    }

                    if (updateJarRequestData.getAgentJar() != null || updateJarRequestData.getCoreJar() != null || updateJarRequestData.getNativeAgent()!=null) {
                        runRemoteCommand(connection.id(), "updateJar", updateJarRequestData);
                    }
                } catch (Exception e) {
                    logger.error("Jar Digest check or update error", e);
                }
            }
        });

    }

    public List<Client> getAll() {
        List<Client> result = new ArrayList<>();
        for (Entry<String, DogConnection> entry : connectionMap.entrySet()) {
            Client client = new Client();
            client.setName(entry.getKey());
            client.setIp(entry.getValue().ip());
            result.add(client);
        }

        return result;
    }

    public List<ClientProcess> getProcessList(String clientName) {
        DogConnection connection = connectionMap.get(clientName);
        List<ClientProcess> result = new ArrayList<>();
        if (connection == null) {
            return result;
        }

        JPSResponseData jpsResponse = runRemoteCommand(clientName, "jps", "");
        if (jpsResponse == null || jpsResponse.getJvmInfos() == null) {
            return result;
        }
        
        List<Agent> agentList = agentService.getByIp(connection.ip());
        for (JvmInfo jvmInfo : jpsResponse.getJvmInfos()) {
            String pid = jvmInfo.getPid().toString();
            ClientProcess clientProcess = new ClientProcess();
            clientProcess.setPid(pid);
            clientProcess.setMainClass(jvmInfo.getMainClass());
            
            for(Agent agent: agentList) {
                if(agent.getPeerPid().equals(pid)) {
                    clientProcess.setStatus(1);
                    clientProcess.setAgentName(agent.getName());
                }
            }

            result.add(clientProcess);
        }
        return result;
    }

}

package jvmdog.client.core.command.jps;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jvmdog.client.core.command.ClientCommand;
import jvmdog.client.core.JPSRequestData;
import jvmdog.client.core.JPSResponseData;
import jvmdog.client.core.JvmInfo;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

@Service
public class JPSCommand implements ClientCommand {
    private static final Logger logger = LoggerFactory.getLogger(JPSCommand.class);

    private static final String PID = getPid();
    
    @Override
    public String name() {
        return "jps";
    }

    @Override
    public DogMessage run(byte[] data) {
        JPSRequestData jpsRequest = SerializeUtils.deserialize(data, JPSRequestData.class);

        JPSResponseData jpsResponse = new JPSResponseData();
        jpsResponse.setId(jpsRequest.getId());
        jpsResponse.setJvmInfos(getJvmInfos());
        byte[] messageData = SerializeUtils.serialize(jpsResponse);
        DogMessage message = DogMessage.clientResponse("jps");
        message.setData(messageData);

        return message;
    }

    private List<JvmInfo> getJvmInfos() {
        List<JvmInfo> result = new ArrayList<>();
        try {
            MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
            Set<Integer> vmlist = new HashSet<Integer>(local.activeVms());

            for (Integer process : vmlist) {
                if (PID.equals(String.valueOf(process))) {
                    continue;
                }

                MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + process));
                String processname = MonitoredVmUtil.mainClass(vm, true);
                if("jvmdog.server.DogServerApplication".equals(processname)) {
                    continue;
                }

                JvmInfo jvmInfo = new JvmInfo();
                jvmInfo.setPid(process);
                jvmInfo.setMainClass(processname);
                jvmInfo.setCommandLine(MonitoredVmUtil.commandLine(vm));
                jvmInfo.setMainArgs(MonitoredVmUtil.mainArgs(vm));
                result.add(jvmInfo);
            }
        } catch (Throwable e) {
            logger.error("getPids error", e);
        }
        return result;
    }

    private static String getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"
        try {
            return name.substring(0, name.indexOf('@'));
        } catch (Exception e) {
            return "-1";
        }
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return JPSRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return JPSResponseData.class;
    }
}

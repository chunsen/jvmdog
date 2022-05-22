package jvmdog.service.client;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jvmdog.core.utils.MD5Utils;
import jvmdog.protocol.api.model.OSInfo;

@Service
public class ClientJarService {
    @Value("${jvmdog.jar.agent:jvmdog-agent-jar-with-dependencies.jar}")
    private String agentJar;

    @Value("${jvmdog.jar.core:jvmdog-core-0.0.1-SNAPSHOT-all.jar}")
    private String coreJar;
    
    @Value("${jvmdog.native-agent.name:test}")
    private String nativeAgent;
    
    @Value("${jvmdog.native-agent.folder:D:\\my-code\\jvm-dog\\jvmti\\}")
    private String nativeAgentFolder;

    @Value("${jvmdog.jar.folder:D:\\my-code\\jvm-dog\\jvmdog-core\\target\\}")
    private String jarFolder;
    
    @Value("${jvmdog.jar.agent.folder:D:\\my-code\\jvm-dog\\jvmdog-agent\\target\\}")
    private String agentJarFolder;

    private Path agentJarPath() {
        return Paths.get(agentJarFolder, agentJar);
    }
    
    private Path coreJarPath() {
        return Paths.get(jarFolder, coreJar);
    }

    public String coreJarMD5() {
        Path path = coreJarPath();
        return MD5Utils.encodeByMD5(path);
    }

    public String agentJarMD5() {
        Path path = agentJarPath();
        return MD5Utils.encodeByMD5(path);
    }

    public byte[] agentJarData() {
        Path path = agentJarPath();
        return fileData(path);
    }

    public byte[] coreJarData() {
        Path path = coreJarPath();
        return fileData(path);
    }
    
    private Path getNativeAgentPath(OSInfo osInfo) {
        String extension ="";
        if(osInfo.isLinux()) {
            extension =".so";
        } else if(osInfo.isWindows()) {
            extension = ".dll";
        } else {
            
        }
        
        Path path = Paths.get(nativeAgentFolder, nativeAgent+extension);
        return path;
    }
    
    public String nativeAgentMD5(OSInfo osInfo) {
        Path path = getNativeAgentPath(osInfo);
        return MD5Utils.encodeByMD5(path);
    }
    
    public byte[] nativeAgentData(OSInfo osInfo) {
        Path path = getNativeAgentPath(osInfo);
        return fileData(path);
    }
    
    private byte[] fileData(Path path) {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            if(path.toFile().exists()) {
                Files.copy(path, outputStream);
                byte[] bytes = outputStream.toByteArray();
                return bytes;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
}

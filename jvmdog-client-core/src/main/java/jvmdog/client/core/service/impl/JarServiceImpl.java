package jvmdog.client.core.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jvmdog.client.core.service.JarService;
import jvmdog.core.utils.MD5Utils;
import jvmdog.protocol.api.model.OSInfo;

@Service
public class JarServiceImpl implements JarService{
    private static final Logger logger = LoggerFactory.getLogger(JarServiceImpl.class);
    
    @Value("${jvmdog.jar.agent:jvmdog-agent-jar-with-dependencies.jar}")
    private String agentJar;
    
    @Value("${jvmdog.jar.core:jvmdog-core-0.0.1-SNAPSHOT-all.jar}")
    private String coreJar;
    
    @Value("${jvmdog.nativa-agent:test}")
    private String nativeAgent;
    
    @Value("${jvmdog.jar.folder:jar}")
    private String jarFolder;
    
    private static final String NATIVE_FILE_EXTENSION;
    
    static {
        OSInfo osInfo = OSInfo.getInstance();
        if(osInfo.isLinux()) {
            NATIVE_FILE_EXTENSION=".so";
        } else if(osInfo.isWindows()) {
            NATIVE_FILE_EXTENSION =".dll";
        } else {
            NATIVE_FILE_EXTENSION = "";
        }
    }
    
    @PostConstruct
    private void init() {
        try {
            Path path = Paths.get(jarFolder);
            logger.info("init jar folder: {}", path.toAbsolutePath().toString());
            Files.createDirectories(path);
        } catch (IOException e) {
            logger.error("init jar folder error", e);
        }
    }
    
    private Path agentJarPath() {
        return Paths.get(jarFolder, agentJar).toAbsolutePath();
    }
    
    private Path coreJarPath() {
        return Paths.get(jarFolder, coreJar).toAbsolutePath();
    }
    
    private Path nativeAgentPath() {
        return Paths.get(jarFolder, nativeAgent+NATIVE_FILE_EXTENSION).toAbsolutePath();
    }
    
    public String agentJar() {
        return agentJarPath().toString();
    }
    
    public String coreJar() {
        return coreJarPath().toString();
    }
    
    public String nativeAgent() {
        return nativeAgentPath().toString();
    }
    
    @Override
    public String coreJarMD5() {
        Path path = coreJarPath();
        return MD5Utils.encodeByMD5(path);
    }
    
    @Override
    public void updateCoreJar(byte[] data) {
        Path path = coreJarPath();
        updateFile(data, path);
    }
    
    @Override
    public String agentJarMD5() {
        Path path = agentJarPath();
        return MD5Utils.encodeByMD5(path);
    }
    
    @Override
    public String nativeAgentMD5() {
        Path path = nativeAgentPath();
        return MD5Utils.encodeByMD5(path);
    }
    
    @Override
    public void updateNativeAgent(byte[] data) {
        Path path = nativeAgentPath();
        updateFile(data, path);
    }
    
    @Override
    public void updateAgentJar(byte[] data) {
        Path path = agentJarPath();
        updateFile(data, path);
    }
    
    private void updateFile(byte[] data, Path path) {
        if(data ==null || data.length ==0) {
            return;
        }
        
        logger.info("update file {}", path.toAbsolutePath());
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)){
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("updateFile error", e);
        }
    }
}

package jvmdog.server.log;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    public String getClientLog(String name) {
        return getLog(name, "client");
    }
    
    public String getAgentLog(String name) {
        return getLog(name, "agent");
    }
    
    private String getLog(String name, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dt = sdf.format(new Date());
        String fileName = String.format("%s_%s_%s.0.log", type, name, dt);
        Path logFile = Paths.get("logs", fileName).toAbsolutePath();
        if(!logFile.toFile().exists()) {
            logger.warn("log file not exist for {} {}", type, name);
            return "";
        }
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            Files.copy(logFile, out);
            return out.toString("utf-8");
        } catch (Exception e) {
            logger.error("getClientLog error:" + logFile, e);
            return "";
        }
    }
}

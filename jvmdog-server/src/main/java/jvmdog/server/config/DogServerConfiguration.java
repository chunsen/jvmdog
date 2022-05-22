package jvmdog.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import jvmdog.server.utils.LongIdWorker;

@Configuration
public class DogServerConfiguration {
    
    @Value("${jvmdog.idworker.id:0}")
    private long workerId;

    @Value("${jvmdog.idworker.datecenter:0}")
    private long datacenterId;

    @Bean("IdWorker")
    public LongIdWorker idWorker(){
        return new LongIdWorker(workerId, datacenterId);
    }
    
    /**
     * ServerEndpointExporter 作用
     *
                 * 这个Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

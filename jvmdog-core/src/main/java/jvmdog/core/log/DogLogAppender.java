package jvmdog.core.log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.ErrorStatus;
import jvmdog.protocol.api.DogClient;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.DogMessageType;

public class DogLogAppender extends AppenderBase<ILoggingEvent>{
    
    private final PatternLayoutEncoder encoder;
    private final DogClient dogClient;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    public DogLogAppender(DogClient dogClient) {
        this.dogClient = dogClient;
        this.encoder =new PatternLayoutEncoder();
        this.encoder.setPattern("%d %-5level [%thread] %logger{0}: %msg%n");
        
        Set<String> excludes = new HashSet<>();
        excludes.add("jvmdog.netty.");
        LogNameFilter logNameFilter = new LogNameFilter(excludes);
        
        this.addFilter(logNameFilter);
    }
    
    @Override
    public void start() {
        int errors = 0;
        if (this.encoder == null) {
            addStatus(new ErrorStatus("No encoder set for the appender named \"" + name + "\".", this));
            errors++;
        } else {
            this.encoder.start();
        }

        // only error free appenders should be activated
        if (errors == 0) {
            super.start();
        }
    }
    
    @Override
    public void setContext(Context context) {
        super.setContext(context);
        this.encoder.setContext(context);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if(dogClient == null) {
            return;
        }
        
        byte[] byteArray = this.encoder.encode(eventObject);
        DogMessage dogMessage = DogMessage.from(DogMessageType.LOG.getValue());
        dogMessage.setData(byteArray);
        
        executorService.execute(()->{
            dogClient.send(dogMessage);
        });
    }

}

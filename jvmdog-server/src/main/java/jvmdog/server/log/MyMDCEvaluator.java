package jvmdog.server.log;

import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import jvmdog.protocol.server.LogMessgeHandler;

public class MyMDCEvaluator extends EventEvaluatorBase<ILoggingEvent>{
    
    private String mdcName = LogMessgeHandler.REMOTE_ID;

    @Override
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        Map<String, String> mdc = event.getMDCPropertyMap();
        if(mdc!=null && mdc.containsKey(mdcName)) {
            return true;
        }
        return false;
    }

    public String getMdcName() {
        return mdcName;
    }

    public void setMdcName(String mdcName) {
        this.mdcName = mdcName;
    }
    
    

}
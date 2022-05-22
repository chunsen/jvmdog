package jvmdog.core.log;

import java.util.Set;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class LogNameFilter extends AbstractMatcherFilter<ILoggingEvent> {
    private final Set<String> excludes;
    
    public LogNameFilter(Set<String> excludes) {
        this.excludes = excludes;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        String logName = event.getLoggerName();
        for(String name: excludes) {
            if(logName.startsWith(name)) {
                return FilterReply.DENY;
            }
        }
        return FilterReply.ACCEPT;
    }

}

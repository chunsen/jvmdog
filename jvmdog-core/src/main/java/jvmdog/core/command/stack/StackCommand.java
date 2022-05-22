package jvmdog.core.command.stack;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jvmdog.core.protocol.agent.AgentCommand;
import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.core.utils.DogUtils;
import jvmdog.protocol.api.model.DogMessage;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.protocol.api.model.ResponseMessageData;
import jvmdog.protocol.api.utils.SerializeUtils;

public class StackCommand implements AgentCommand {
    private static final Logger logger = LoggerFactory.getLogger(StackCommand.class);

    private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    @Override
    public DogMessage run(AgentContext agentContext, byte[] data) {
        StackRequestData requestData = SerializeUtils.deserialize(data, StackRequestData.class);

        StackResponseData responseData = new StackResponseData();
        responseData.setId(requestData.getId());

        try {
            ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
            responseData.setThreadStacks(convert(threadInfos, requestData.getClassName()));
        } catch (Throwable e) {
            logger.error("StackCommand error", e);
            responseData.setCode(ResponseMessageData.CODE_ERROR);
            responseData.setMessage(e.getMessage());
        }

        byte[] messageData = SerializeUtils.serialize(responseData);
        DogMessage message = DogMessage.clientResponse("stack");
        message.setData(messageData);

        return message;
    }

    private List<ThreadStack> convert(ThreadInfo[] threadInfos, String className) {
        List<ThreadStack> result = new ArrayList<>();
        for (int i = 0; i < threadInfos.length; i++) {
            ThreadInfo threadInfo = threadInfos[i];
            ThreadStack threadStack = new ThreadStack();
            threadStack.setThreadName(threadInfo.getThreadName());
            threadStack.setThreadId(threadInfo.getThreadId());
            threadStack.setLockName(threadInfo.getLockName());
            
            String lockOwnerName = threadInfo.getLockOwnerName();
            threadStack.setLockOwnerName(lockOwnerName);
            threadStack.setThreadState(threadInfo.getThreadState());
            LockInfo lockInfo = threadInfo.getLockInfo();
            if(lockInfo != null && lockOwnerName==null) {
                lockOwnerName = getLockOwnerName(lockInfo);
                threadStack.setLockOwnerName(lockOwnerName);
            }
            
            List<StackTraceFrame> trace = convert(threadInfo.getStackTrace(), className);
            if(trace ==null) {
                continue;
            }
            
            threadStack.setStackTraces(trace);

            result.add(threadStack);
        }
        return result;
    }
    
    private String getLockOwnerName(LockInfo lockInfo) {
        if(FutureTask.class.getName().equals(lockInfo.getClassName())) {
            Object[] instances = DogUtils.getInstances(lockInfo.getClassName());
            if(instances!=null && instances.length>0) {
                for(Object instance: instances) {
                    int hashCode = System.identityHashCode(instance);
                    if(hashCode == lockInfo.getIdentityHashCode()) {
                        FutureTask<?> futureTask = (FutureTask<?>)instance;
                        Thread runner = getRunner(futureTask);
                        if(runner!=null) {
                            return runner.getName();
                        }
                    }
                }
                
            }
        }
        return null;
    }
    
    private Thread getRunner(FutureTask<?> futureTask) {
        try {
            Field field = futureTask.getClass().getDeclaredField("runner");
            field.setAccessible(true);
            return (Thread)field.get(futureTask);
        }catch(Exception ex) {
            logger.error("getRunner error", ex);
            return null;
        }
    }

    private List<StackTraceFrame> convert(StackTraceElement[] stackTraces, String className) {
        List<StackTraceFrame> result = new ArrayList<>();
        
        boolean match = (className==null || className.length() ==0);
        for (int i = 0; i < stackTraces.length; i++) {
            StackTraceElement trace = stackTraces[i];
            StackTraceFrame frame = new StackTraceFrame();
            frame.setDeclaringClass(trace.getClassName());
            frame.setMethodName(trace.getMethodName());
            frame.setFileName(trace.getFileName());
            frame.setLineNumber(trace.getLineNumber());

            result.add(frame);
            
            if(!match && frame.getDeclaringClass().startsWith(className)) {
                match = true;
            }
        }
        if(!match) {
            return null;
        }
        
        return result;
    }

    @Override
    public String name() {
        return "stack";
    }

    @Override
    public Class<? extends MessageData> requestClass() {
        return StackRequestData.class;
    }

    @Override
    public Class<? extends ResponseMessageData> responseClass() {
        return StackResponseData.class;
    }

}

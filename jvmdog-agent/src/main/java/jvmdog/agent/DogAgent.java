package jvmdog.agent;

import java.lang.instrument.Instrumentation;

public class DogAgent {
    public static void premain(String args, Instrumentation inst) {
        runAgent(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        runAgent(args, inst);
    }
    
    private static synchronized void runAgent(String args, Instrumentation inst){
//        try {
//            Class<?>  clazz = Class.forName("org.springframework.beans.factory.annotation.Value", false, Thread.currentThread().getContextClassLoader());
//            System.out.println("load class: " + clazz.getName());
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        System.out.println("DogAgent thread: " + Thread.currentThread().toString() + ", classLoader:" + Thread.currentThread().getContextClassLoader());
        
        Thread dogAgentThread = new Thread(new CommandRunner(args, inst));
        dogAgentThread.setName("dogAgentThread-commandRunner");
        dogAgentThread.start();
//        try {
//            dogAgentThread.join();
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        System.out.println("runAgent end.");
    }
    
}

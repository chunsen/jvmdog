package jvmdog.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.security.CodeSource;
import java.util.jar.JarFile;

public class CommandRunner implements Runnable{
    private final String args;
    private final Instrumentation inst;
    
    public CommandRunner(String args, Instrumentation inst) {
        this.args = args;
        this.inst = inst;
    }

    public void run() {
        AgentArgument agentArgument = AgentArgument.fromString(args);
        File jarFile = new File(agentArgument.getJarFile());
        if(!jarFile.exists()){
            return;
        }
        
        try{
            init(jarFile, agentArgument.getClassName());
            
//            ClassLoader parentClassLoader = findClassLoader();
//
//            Thread.currentThread().setContextClassLoader(parentClassLoader);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = classLoader.loadClass(agentArgument.getClassName());
            clazz.getMethod("run", Instrumentation.class, String.class).invoke(null, inst, agentArgument.getOptions());

        }catch(Throwable t){
            t.printStackTrace();
        }
        System.out.println("CommandRunner run end.");
    }
    
    private void init(File jarFile, String className) {
        try {
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className);
            if(clazz != null ) {
                System.out.println("Jar already loaded.");
                return;
            }
        } catch (ClassNotFoundException e) {
            //ignore
        }
        
        try{
            CodeSource codeSource = CommandRunner.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                File agentJarFile = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                inst.appendToSystemClassLoaderSearch(new JarFile(agentJarFile));
            }
            inst.appendToSystemClassLoaderSearch(new JarFile(jarFile));
        }catch(Throwable t){
            t.printStackTrace();
        }
    }
    
//    private ClassLoader findClassLoader(){
//        Class[] classes = inst.getAllLoadedClasses();
//        for(Class<?> clazz: classes){
//            if("org.slf4j.Logger".equals(clazz.getName())){
//                return clazz.getClassLoader();
//            }
//        }
//        return Thread.currentThread().getContextClassLoader();
//    }

}

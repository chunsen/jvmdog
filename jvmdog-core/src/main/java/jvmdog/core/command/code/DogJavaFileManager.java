package jvmdog.core.command.code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import jvmdog.core.protocol.agent.AgentContext;
import jvmdog.core.transformer.BytecodeDumpClassFileTransformer;

public class DogJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final List<CompiledCode> compiledCodes = new ArrayList<>();
    private final MemoryClassLoader classLoader;
    private final Map<String, Set<Class<?>>> packageMap;
    private final AgentContext agentContext;

    public DogJavaFileManager(JavaFileManager fileManager, ClassLoader classLoader, Map<String, Set<Class<?>>> packageMap, AgentContext agentContext) {
        super(fileManager);
        this.classLoader = new MemoryClassLoader(classLoader);
        this.packageMap = packageMap;
        this.agentContext = agentContext;
    }

    public List<CompiledCode> getCompiledCodes() {
        return compiledCodes;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
            FileObject sibling) throws IOException {

        for (CompiledCode compiledCode : compiledCodes) {
            if (compiledCode.getClassName().equals(className)) {
                return compiledCode;
            }
        }
        
        try {
            CompiledCode compiledCode = new CompiledCode(className);
            compiledCodes.add(compiledCode);
            
            classLoader.add(compiledCode);
            
            return compiledCode;

        } catch (Exception e) {
            throw new RuntimeException("Message while creating in-memory output file for " + className, e);
        }
    }
    
    @Override
    public ClassLoader getClassLoader(DogJavaFileManager.Location location) {
        return classLoader;
    }
    
    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof ByteCodeFileObject) {
            return ((ByteCodeFileObject)file).getName();
        }
//        String message =  String.format("inferBinaryName, location=%s, JavaFileObject=%s", location, file.getName());
//        System.out.println(message);
        String result = super.inferBinaryName(location, file);
//        message =  String.format("inferBinaryName, location=%s, JavaFileObject=%s, resuslt=%s", location, file.getName(), result);
//        System.out.println(message);
        return result;
    }
    
    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds,
                                         boolean recurse) throws IOException {
//        String message = String.format("list, location=%s, packageName=%s, kinds=%s", location, packageName, kinds);
//        System.out.println(message);
        if(location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)){
            Set<Class<?>> classSet = packageMap.get(packageName);
            if(classSet!=null && classSet.size()>0){
                BytecodeDumpClassFileTransformer transformer = new BytecodeDumpClassFileTransformer(classSet);
//                InstrumentationUtils.retransformClasses(inst, transformer, classSet);
                agentContext.visitClasses(classSet, transformer);
                
                Set<JavaFileObject> result = new HashSet<>();
                for(Entry<Class<?>, byte[]> entry: transformer.getByteCodeMap().entrySet()){
                    try{
                        ByteCodeFileObject fileObj = new ByteCodeFileObject(entry.getKey().getName(), entry.getValue());
                        result.add(fileObj);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                
//                System.out.println("list from map, result=" + result);
                return result;
            }
        }
        return super.list(location, packageName, kinds, recurse);
    }

}

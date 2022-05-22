package jvmdog.core.command.code;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import jvmdog.core.protocol.agent.AgentContext;

public class CodeCompiler {
    private JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    
    public Map<Class<?>, byte[]> compile(AgentContext agentContext, Map<String, String> codeMap){
        Map<String, Class<?>> nameClassMap = new HashMap<>();
        List<CompiledCode> compliedCodes = complileInternal(agentContext, codeMap, nameClassMap);
        Map<Class<?>, byte[]> byteCodeMap = new HashMap<>();
        for(CompiledCode compiledCode: compliedCodes){
            Class<?> clazz = nameClassMap.get(compiledCode.getName());
            byteCodeMap.put(clazz, compiledCode.getByteCode());
        }
        return byteCodeMap;
    }
    
    public Map<String, byte[]> compile2(AgentContext agentContext, Map<String, String> codeMap){
        Map<String, Class<?>> nameClassMap = new HashMap<>();
        List<CompiledCode> compliedCodes = complileInternal(agentContext, codeMap, nameClassMap);
        
        Map<String, byte[]> byteCodeMap = new HashMap<>();
        for(CompiledCode compiledCode: compliedCodes){
            byteCodeMap.put(compiledCode.getName(), compiledCode.getByteCode());
        }
        return byteCodeMap;
    }
    
    private List<CompiledCode> complileInternal(AgentContext agentContext, Map<String, String> codeMap, Map<String, Class<?>> nameClassMap){
        ClassLoader classLoader = null;
        Map<String, Set<Class<?>>> packageMap = new HashMap<>();
        for (Class<?> clazz : agentContext.getAllLoadedClasses()) {
            String className = clazz.getName();
            if(codeMap.containsKey(className)){
                nameClassMap.put(className, clazz);
            }
            if(className.contains("$$")){
                continue;
            }
            
            int index = className.lastIndexOf('.');
            if(index>0){
                String packageName = className.substring(0, index);
                if(packageName.startsWith("java.")){
                    continue;
                }
                Set<Class<?>> classSet = packageMap.computeIfAbsent(packageName, (key)-> new HashSet<>());
                classSet.add(clazz);
            }
        }
        
        List<SourceCode> sourceCodes = new ArrayList<>();
        for(Entry<String, String> entry: codeMap.entrySet()){
            SourceCode sourceCode = new SourceCode(entry.getKey(), entry.getValue());
            sourceCodes.add(sourceCode);
        }
        
        Iterator<Class<?>> iterator = nameClassMap.values().iterator();
        if(iterator.hasNext()) {
            Class<?> firstClass = nameClassMap.values().iterator().next();
            classLoader = firstClass.getClassLoader();
        } else if(classLoader == null){
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        List<String> options = new ArrayList<String>();
        options.add("-Xlint:unchecked");
        
        JavaFileManager parentFileManager = javac.getStandardFileManager(null, null, Charset.forName("UTF-8"));
        DogJavaFileManager fileManager = new DogJavaFileManager(parentFileManager,classLoader , packageMap, agentContext);
        
        JavaCompiler.CompilationTask task = javac.getTask(null, fileManager, collector, options, null, sourceCodes);
        
        boolean result = task.call();
        if (!result || collector.getDiagnostics().size() > 0) {
            for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
                switch (diagnostic.getKind()) {
                case NOTE:
                case MANDATORY_WARNING:
                case WARNING:
                    System.out.println("compile warn: " +  diagnostic);
                    break;
                case OTHER:
                case ERROR:
                default:
                    System.out.println("compile error: " +  diagnostic);
                    break;
                }

            }
        }
        
        return fileManager.getCompiledCodes();
    } 
   
}

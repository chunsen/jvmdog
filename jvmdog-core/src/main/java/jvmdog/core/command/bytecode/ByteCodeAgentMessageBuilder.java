package jvmdog.core.command.bytecode;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ByteCodeAgentMessageBuilder {
    public static ByteCodeAgentMessage build(Map<String, String> classFileMap){
        if(classFileMap ==null || classFileMap.isEmpty()){
            return null;
        }
        
        Map<String, byte[]> byteCodeMap = new HashMap<>();
        for(Entry<String, String> entry: classFileMap.entrySet()){
            try{
                byte[] data = Files.readAllBytes(Paths.get(entry.getValue()));
                byteCodeMap.put(entry.getKey(), data);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        ByteCodeAgentMessage result = new ByteCodeAgentMessage();
        result.setByteCodeMap(byteCodeMap);
        return result;
    }
}

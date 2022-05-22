package jvmdog.service.agent.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.printer.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jvmdog.core.command.dump.DumpResponseData;
import jvmdog.protocol.api.model.MessageData;
import jvmdog.service.agent.AgentResponseDataHandler;

@Service
public class DumpClassCodeCommand implements AgentResponseDataHandler {
    private static final Logger logger = LoggerFactory.getLogger(DumpClassCodeCommand.class);
    
    private final ClassFileToJavaSourceDecompiler decompiler = new ClassFileToJavaSourceDecompiler();

    private String getPackageName(String clazz){
        int index = clazz.lastIndexOf('.');
        return clazz.substring(0, index);
    }

    private String decompileClass(String clazz, byte[] data){
        Loader loader = new BytecodeLoader(clazz, data);
        
        Printer printer = new JDPrinter(getPackageName(clazz));
        try {
            decompiler.decompile(loader, printer, clazz);
        } catch (Exception e1) {
            logger.error("decompile error:" + clazz, e1);
            return "";
        }
        
        // 将类写入文件
        try {
//          String folderPath = "d:\\temp\\dump-server\\";
//          File folder = new File(folderPath);
//          if(!folder.exists()){
//              folder.mkdirs();
//          }
//          File dumpClassFile = new File(folder, clazz+".java");
            
            String code = printer.toString();
//            ByteArrayInputStream sr = new ByteArrayInputStream(code.getBytes(Charset.forName("utf-8")));
//            Files.copy(sr, dumpClassFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            return code;
        } catch (Exception e) {
            logger.error("decompile error:" + clazz, e);
            return "";
        }
    }

    @Override
    public MessageData handle(MessageData responseData) {
        DumpResponseData dumpMessage = (DumpResponseData)responseData;
        
        Map<String, String> classCodeMap = new HashMap<>();
        if(dumpMessage.getByteCodeMap()!=null) {
            for(Entry<String, byte[]> entry: dumpMessage.getByteCodeMap().entrySet()){
                String code = decompileClass(entry.getKey(), entry.getValue());
                classCodeMap.put(entry.getKey(), code);
            }
        }
        
        ClassCodeResponseData result = new ClassCodeResponseData();
        result.setId(responseData.getId());
        result.setClassCodeMap(classCodeMap);
        return result;
    }

    @Override
    public String name() {
        return "dump";
    }

}

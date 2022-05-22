package jvmdog.core.utils;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public class MD5Utils {
    private final static String[] hexDigits =
        {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    
    public static String encodeByMD5(Path path) {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            if(path.toFile().exists()) {
                Files.copy(path, outputStream);
                byte[] bytes = outputStream.toByteArray();
                return MD5Utils.encodeByMD5(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String encodeByMD5(byte[] data) {  
        if (data != null && data.length>0) {  
            try {   
                MessageDigest md = MessageDigest.getInstance("MD5");   
                byte[] results = md.digest(data);   
                String resultString = byteArrayToHexString(results);  
                return resultString.toUpperCase();  
            } catch (Exception ex) {  
                ex.printStackTrace();  
            }  
        }  
        return null;  
    }  
    
    private static String byteArrayToHexString(byte[] b) {  
        StringBuffer resultSb = new StringBuffer();  
        for (int i = 0; i < b.length; i++) {  
            resultSb.append(byteToHexString(b[i]));  
        }  
        return resultSb.toString();  
    } 
    
    private static String byteToHexString(byte b) {  
        int n = b;  
        if (n < 0)  
            n = 256 + n;  
        int d1 = n / 16;  
        int d2 = n % 16;  
        return hexDigits[d1] + hexDigits[d2];  
    } 
}

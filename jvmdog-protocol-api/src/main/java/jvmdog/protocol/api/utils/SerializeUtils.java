package jvmdog.protocol.api.utils;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class SerializeUtils {
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static final LinkedBuffer buffer = LinkedBuffer.allocate(512);
    
    private static final Charset UFT_8 = Charset.forName("utf-8");
    
    private static final Gson gson = new Gson();
    
    public static final String fromBytes(byte[] data){
        return new String(data, UFT_8);
    }
    
    public static final byte[] fromString(String data){
        return data.getBytes(UFT_8);
    }
    
    public static <T> byte[] serialize(T obj) {

        if (obj  == null){
            throw new NullPointerException();
        }
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }

        return data;
    }

    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }


    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (schema == null) {
            //这个schema通过RuntimeSchema进行懒创建并缓存
            //所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (schema != null) {
                schemaCache.put(clazz, schema);
            }
        }

        return schema;
    }
    
    public static String toJson(Object data) {
        return gson.toJson(data);
    }
    
    public static <T> T fromJson(byte[] data, Class<T> clazz) {
        return gson.fromJson(fromBytes(data), clazz);
    }
}

package jvmdog.controller.model;

public class ServerDataResult<T> extends ServerResult{
    
    public static <T> ServerDataResult<T> from(T data){
        return new ServerDataResult<T>(data, true, "", "0");
    }
    
    private final T data;
    public ServerDataResult(T data, boolean success, String message, String code) {
        super(success, message, code);
        this.data = data;
    }
    
    public T getData() {
        return data;
    }

}

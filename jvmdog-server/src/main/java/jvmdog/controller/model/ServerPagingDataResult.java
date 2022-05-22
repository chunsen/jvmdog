package jvmdog.controller.model;

import java.util.ArrayList;
import java.util.List;

public class ServerPagingDataResult<T> extends ServerResult {
    private final int pageSize;
    private final int current;
    private final int total;
    private final List<T> data;
    
    public static <T> ServerPagingDataResult<T> from(List<T> data) {
        if(data == null) {
            return new ServerPagingDataResult<T>(new ArrayList<>(), 0, 0, 20);
        } else {
            return new ServerPagingDataResult<T>(data, data.size(), 0, 20);
        }
    }
    public static <T> ServerPagingDataResult<T> from(List<T> data, int total, int current, int pageSize) {
        return new ServerPagingDataResult<T>(data, total, current, pageSize);
    }
    public static <T> ServerPagingDataResult<T> failResponse(String message){
        return  new ServerPagingDataResult<>(message,null,0,0,1);
    }
    public ServerPagingDataResult(String message,List<T> data, int total, int current, int pageSize){
        super(false, message, "-1");
        this.data = data;
        this.total =total;
        this.current = current;
        this.pageSize = pageSize;
    }

    public ServerPagingDataResult(List<T> data, int total, int current, int pageSize) {
        super(true, null, "0");
        this.data = data;
        this.total =total;
        this.current = current;
        this.pageSize = pageSize;
    }
    
    public int getPageSize() {
        return pageSize;
    }

    public int getCurrent() {
        return current;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getData() {
        return data;
    }
    
}
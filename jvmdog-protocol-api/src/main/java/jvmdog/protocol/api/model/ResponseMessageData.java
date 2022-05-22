package jvmdog.protocol.api.model;

public class ResponseMessageData extends MessageData {

    public final static String CODE_SUCCESS ="0";
    public final static String CODE_ERROR ="100";
    
    private String code = CODE_SUCCESS;
    private String message;
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
}

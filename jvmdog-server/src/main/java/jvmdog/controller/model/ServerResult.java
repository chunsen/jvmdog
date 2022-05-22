package jvmdog.controller.model;

public class ServerResult {
    private boolean success;
    private String code;
    private String message;

    public static final ServerResult SUCCESS = new ServerResult(true, null, "0");

    public static ServerResult fail(String message) {
        return new ServerResult(false, message, "-1");
    }

    public ServerResult(boolean success, String message, String code) {
        this.setSuccess(success);
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

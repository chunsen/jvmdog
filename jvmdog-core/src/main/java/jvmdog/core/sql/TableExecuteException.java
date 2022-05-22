package jvmdog.core.sql;

public class TableExecuteException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TableExecuteException(final Throwable cause) {
        super(cause);
    }

    public TableExecuteException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

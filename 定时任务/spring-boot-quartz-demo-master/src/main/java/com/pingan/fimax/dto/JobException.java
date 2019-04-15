package xxx.xxx.xxx.dto;

import xxx.xxx.xxx.enums.JobErrorCodeEnum;

/**
 * @Author:xxx
 * @Date: 2019/3/12 15:08
 */
public class JobException extends RuntimeException {
    private String errorCode;
    private String errorId;

    protected JobException() {
        this.errorCode = JobErrorCodeEnum.SERVER_ERROR.getVal();
    }

    public JobException(String errorCode, String message) {
        super(message);
        this.errorCode = JobErrorCodeEnum.SERVER_ERROR.getVal();
        this.errorCode = errorCode;
    }

    public JobException(String errorCode, String errorId, String message) {
        this(errorCode, message);
        this.errorId = errorId;
    }

    public JobException(String errorCode, String errorId, String message, Object... args) {
        this(errorCode, String.format(message, args));
        this.errorId = errorId;
    }

    public JobException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = JobErrorCodeEnum.SERVER_ERROR.getVal();
        this.errorCode = errorCode;
    }

    public JobException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = JobErrorCodeEnum.SERVER_ERROR.getVal();
        this.errorCode = errorCode;
    }

    public JobException(String errorCode, String errorId, String message, Throwable cause) {
        this(errorCode, message, cause);
        this.errorId = errorId;
    }

    public JobException(String errorCode, String errorId, String message, Throwable cause, Object... args) {
        this(errorCode, String.format(message, args), cause);
        this.errorId = errorId;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorId() {
        return this.errorId;
    }
}

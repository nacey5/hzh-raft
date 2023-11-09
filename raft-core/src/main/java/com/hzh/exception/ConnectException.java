package com.hzh.exception;

/**
 * @ClassName ConnecotException
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/9 12:36
 * @Version 0.0.1
 **/
public class ConnectException extends AbstractRaftException {
    public ConnectException(String message) {
        super(message);
    }

    public ConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return null;
    }
}

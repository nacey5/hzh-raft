package com.hzh.exception;

/**
 * @ClassName AbstractRaftException
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 11:12
 * @Version 0.0.1
 **/
public abstract class AbstractRaftException extends RuntimeException{
    public AbstractRaftException(String message) {
        super(message);
    }

    public AbstractRaftException(String message, Throwable cause) {
        super(message, cause);
    }
    public abstract String getMessage();
}

package com.hzh.exception;

/**
 * @ClassName LogException
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/5 17:08
 * @Version 0.0.1
 **/
public class LogException extends AbstractRaftException{
    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return null;
    }
}

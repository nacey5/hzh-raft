package com.hzh.exception;

/**
 * @ClassName EmptySequenceException
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/4 17:08
 * @Version 0.0.1
 **/
public class EmptySequenceException extends AbstractRaftException{
    public EmptySequenceException(String message) {
        super(message);
    }

    public EmptySequenceException(String message, Throwable cause) {
        super(message, cause);
    }
    public EmptySequenceException() {
        super("");
    }

    @Override
    public String getMessage() {
        return null;
    }

}

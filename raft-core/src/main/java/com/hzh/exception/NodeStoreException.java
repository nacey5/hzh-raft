package com.hzh.exception;

/**
 * @ClassName NodeStoreException
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/2 11:13
 * @Version 0.0.1
 **/
public class NodeStoreException extends AbstractRaftException{
    public NodeStoreException(String message) {
        super(message);
    }

    public NodeStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public NodeStoreException(Throwable cause) {
        super(cause.getMessage());
    }

    @Override
    public String getMessage() {
        return null;
    }
}

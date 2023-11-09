package com.hzh.exception;

/**
 * @ClassName ChannelConnectException
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/9 12:39
 * @Version 0.0.1
 **/
public class ChannelConnectException extends AbstractRaftException{
    public ChannelConnectException(String message) {
        super(message);
    }

    public ChannelConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return null;
    }
}

package com.hzh.kvstore.message;

/**
 * @ClassName GetCommand
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/9 23:08
 * @Version 0.0.1
 **/
public class GetCommand {
    private final String key;

    public GetCommand(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "GetCommand{" +
                "key='" + key + '\'' +
                '}';
    }
}

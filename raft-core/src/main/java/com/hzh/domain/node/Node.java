package com.hzh.domain.node;

/**
 * @ClassName Node
 * @Description
 * @Author DaHuangGo
 * @Date 2023/11/1 19:33
 * @Version 0.0.1
 **/
public interface Node {
    //start
    void start();
    //close
    void stop() throws InterruptedException;
}

package com.boot1.chovy.server_open_impl;

import com.boot1.chovy.entity.Student;
import com.boot1.rpc.netty_zookeeper_spring.server.RpcService;

/**
 * Created by wangchaohui on 2018/3/16.
 */

public interface HelloService {
    String Hello(String name);
    String Hello(Student student);
    String Hello(int id);
}

package com.boot1.rpc.netty_zookeeper_spring.client;


import com.boot1.rpc.netty_zookeeper_spring.client.proxy.IAsyncObjectProxy;
import com.boot1.rpc.netty_zookeeper_spring.client.proxy.ObjectProxy;
import com.boot1.rpc.netty_zookeeper_spring.registry.ServiceDiscovery;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangchaohui on 2018/3/16.
 */
public class RpcClient {

    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 8, 500L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2 << 15));

    public RpcClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T>(interfaceClass)
        );
    }

    public static <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass);
    }

    public static <T> IAsyncObjectProxy createAsync() {
        return new ObjectProxy<T>();
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public void stop() {
        threadPoolExecutor.shutdown();
        serviceDiscovery.stop();
        ZookeeperConnectManage.getInstance().stop();
    }
}


package com.boot1.rpc.netty_zookeeper_spring.client.proxy;

import com.boot1.rpc.netty_zookeeper_spring.client.ZookeeperConnectManage;
import com.boot1.rpc.netty_zookeeper_spring.client.RPCFuture;
import com.boot1.rpc.netty_zookeeper_spring.client.RpcClientHandler;
import com.boot1.rpc.netty_zookeeper_spring.util.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by wangchaohui on 2018/3/16.
 */
public class ObjectProxy<T> implements InvocationHandler, IAsyncObjectProxy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectProxy.class);
    private Class<T> clazz;

    private static final Map<String, Object> typeNameMap = new HashMap<String, Object>();

    static {
        typeNameMap.put("java.lang.Integer", Integer.TYPE);
        typeNameMap.put("java.lang.Long", Long.TYPE);
        typeNameMap.put("java.lang.Float", Float.TYPE);
        typeNameMap.put("java.lang.Double", Double.TYPE);
        typeNameMap.put("java.lang.Character", Character.TYPE);
        typeNameMap.put("java.lang.Boolean", Boolean.TYPE);
        typeNameMap.put("java.lang.Short", Short.TYPE);
        typeNameMap.put("java.lang.Byte", Byte.TYPE);
    }

    public ObjectProxy() {
    }

    public ObjectProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public RPCFuture call(String className, String funcName, Object... args) {
        RpcRequest request = createRequest(className, funcName, args);
        return rpcFutureHandel(request);
    }

    @Override
    public RPCFuture call(String funcName, Object... args) {
        RpcRequest request = createRequest(this.clazz.getName(), funcName, args);
        return rpcFutureHandel(request);
    }

    public RPCFuture rpcFutureHandel(RpcRequest request) {
        RpcClientHandler handler = ZookeeperConnectManage.getInstance().chooseHandler();
        RPCFuture rpcFuture = handler.sendRequest(request);
        return rpcFuture;
    }

    private RpcRequest createRequest(String className, String methodName, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(className);

        request.setMethodName(methodName);
        request.setParameters(args);

        Class[] parameterTypes = new Class[args.length];
        // 封装参数类型
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
        return request;
    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        return (Class<?>) typeNameMap.get(typeName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        RpcClientHandler handler = ZookeeperConnectManage.getInstance().chooseHandler();
        RPCFuture rpcFuture = handler.sendRequest(request);
        return rpcFuture.get();
    }
}

package com.vincent.spring.framework.aop;

import com.vincent.spring.framework.aop.aspect.VincentAdvice;
import com.vincent.spring.framework.aop.support.VincentAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * aop代理类处理
 */
public class VincentJdkDynamicAopProxy implements InvocationHandler {

    private VincentAdvisedSupport config;

    public VincentJdkDynamicAopProxy(VincentAdvisedSupport config) {
        this.config = config;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String,VincentAdvice> advices = config.getAdvices(method,null);
        Object returnValue;

        try {
            invokeAdvice(advices.get("before"));

            returnValue = method.invoke(this.config.getTarget(),args);

            invokeAdvice(advices.get("after"));

        }catch (Exception e){
            invokeAdvice(advices.get("afterThrow"));
            throw e;
        }

        return returnValue;
    }

    private void invokeAdvice(VincentAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public Object getProxy(){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),this.config.getTargetClass().getInterfaces(),this);
    }
}

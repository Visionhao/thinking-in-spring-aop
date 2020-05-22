package com.vincent.spring.framework.aop.aspect;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class VincentAdvice {

    private Object aspect;

    private Method adviceMethod;

    private String throwName;

    public VincentAdvice(Object aspect, Method adviceMethod) {
        this.aspect = aspect;
        this.adviceMethod = adviceMethod;
    }
}

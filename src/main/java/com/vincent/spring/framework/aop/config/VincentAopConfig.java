package com.vincent.spring.framework.aop.config;

import lombok.Data;

@Data
public class VincentAopConfig {

    //切点
    private String pointCut;

    //切入类
    private String aspectClass;

    private String aspectBefore;

    private String aspectAfter;

    private String aspectAfterThrow;

    private String aspectAfterThrowingName;

}

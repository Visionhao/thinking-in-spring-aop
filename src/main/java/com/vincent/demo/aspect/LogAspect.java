package com.vincent.demo.aspect;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogAspect {

    //在调用一个方法之前，执行before方法
    public void before(){
        //逻辑自己定义
        log.info("Invoker Before Method!!!");
    }

    //调用一个方法之后，执行after方法
    public void after(){
        log.info("Invoker After Method!!!");
    }

    //出现异常的时候执行该方法
    public void afterThrowing(){
        log.info("出现异常");
    }
}

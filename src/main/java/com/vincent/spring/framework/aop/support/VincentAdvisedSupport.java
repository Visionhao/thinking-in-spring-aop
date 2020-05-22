package com.vincent.spring.framework.aop.support;

import com.vincent.spring.framework.aop.aspect.VincentAdvice;
import com.vincent.spring.framework.aop.config.VincentAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析AOP配置的工具类
 */
public class VincentAdvisedSupport {

    private VincentAopConfig config;

    private Object target;

    private Class targetClass;

    private Pattern pointCutClassPattern;

    private Map<Method,Map<String,VincentAdvice>> methodCache;

    public VincentAdvisedSupport(VincentAopConfig config){
        this.config = config;
    }

    //解析配置文件的方法
    private void parse(){

        //把Spring的Excpress变成Java能够识别的正则表达式
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");

        //保存专门匹配Class的正则
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1 ));

        //享元的共享池
        methodCache =  new HashMap<Method, Map<String, VincentAdvice>>();

        //保存专门匹配方法的正则
        Pattern pointCutPattern = Pattern.compile(pointCut);

        try {
            //反射获取切点类
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()){
                aspectMethods.put(method.getName(),method);
            }

            for(Method method : this.targetClass.getMethods()){
                //获取方法名字
                String methodString = method.toString();
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pointCutPattern.matcher(methodString);
                if(matcher.matches()){
                    Map<String,VincentAdvice> advices = new HashMap<String, VincentAdvice>();

                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))){
                        advices.put("before",new VincentAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectBefore())));
                    }

                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))){
                        advices.put("after",new VincentAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectAfter())));
                    }

                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))){
                        VincentAdvice advice = new VincentAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectAfterThrow()));
                        advice.setThrowName(config.getAspectAfterThrowingName());
                        advices.put("afterThrow",advice);
                    }
                    //跟目标代理类的业务方法和Advices 建立一对多个关联关系，以便在Proxy 类中获得
                    methodCache.put(method,advices);
                }
            }
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    //根据一个目标代理类的方法，获得其对应的通知
    public Map<String,VincentAdvice> getAdvices(Method method,Object o) throws NoSuchMethodException {
        //享元设计模式的应用
        Map<String,VincentAdvice> cache = methodCache.get(method);
        if(null == cache){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cache = methodCache.get(m);
            this.methodCache.put(m,cache);
        }
        return cache;
    }

    //给applicacontext 首先IoC中的对象初始化时调用，决定要不要生成代理类的逻辑
    public boolean pointCutMath(){
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        parse();
    }
}

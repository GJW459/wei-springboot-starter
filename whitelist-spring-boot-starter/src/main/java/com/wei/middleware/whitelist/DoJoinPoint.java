package com.wei.middleware.whitelist;

import com.alibaba.fastjson.JSON;
import com.wei.middleware.whitelist.annotation.DoWhiteList;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author wei
 * 整个中间件的核心部分，负责对所有添加自定义注解的方法进行拦截和逻辑处理
 */
@Aspect
@Component
public class DoJoinPoint {

    private static final Logger logger = LoggerFactory.getLogger(DoJoinPoint.class);

    @Resource
    private String whiteListConfig;

    @Pointcut("@annotation(com.wei.middleware.whitelist.annotation.DoWhiteList)")
    public void aopPoint(){}

    @Around("aopPoint()")
    public Object doRouter(ProceedingJoinPoint jp) throws Throwable{
        // 获取方法内容
        Method method = getMethod(jp);
        DoWhiteList doWhiteList = method.getAnnotation(DoWhiteList.class);
        // 获取字段值
        String keyValue = getFiledValue(doWhiteList.key(),jp.getArgs());
        logger.info("middleware whiteList handler method:{},value:{}",method.getName(),keyValue);
        if (null==keyValue || "".equals(keyValue)) return jp.proceed();
        String[] whiteList = whiteListConfig.split(",");
        // 白名单过滤
        for (String white : whiteList) {
            if (keyValue.equals(white)){
                return jp.proceed();
            }
        }
        // 拦截
        return returnObject(doWhiteList,method);
    }

    private String getFiledValue(String filed, Object[] args) {
        String filedValue=null;
        for (Object arg : args) {
            try {
                if (null==filedValue ||"".equals(filedValue)){
                    filedValue= BeanUtils.getProperty(arg,filed);
                }else {
                    break;
                }
            }catch (Exception e){
                if (args.length==1){
                    return args[0].toString();
                }
            }
        }
        return filedValue;
    }

    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        return jp.getTarget().getClass().getMethod(signature.getName(),signature.getParameterTypes());
    }

    /**
     * 返回对象
     * @param whiteList
     * @param method
     * @return
     */
    private Object returnObject(DoWhiteList whiteList,Method method) throws InstantiationException, IllegalAccessException {
        Class<?> returnType = method.getReturnType();
        String returnJson = whiteList.returnJson();
        if ("".equals(returnJson)){
            return returnType.newInstance();
        }
        return JSON.parseObject(returnJson,returnType);
    }
}

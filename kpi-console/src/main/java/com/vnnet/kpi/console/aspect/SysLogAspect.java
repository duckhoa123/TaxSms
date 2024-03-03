package com.vnnet.kpi.console.aspect;

import com.alibaba.fastjson.JSONObject;
import com.vnnet.kpi.console.utils.MySecurityUtils;
import com.vnnet.kpi.web.model.SysLog;
import com.vnnet.kpi.web.service.SysLogService;
import com.vnnet.kpi.web.utils.HttpUtils;
import com.vnnet.kpi.web.utils.IPUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


/**
 * System log, aspect processing class, record log
 */
@Aspect
@Component
public class SysLogAspect {

    @Autowired
    private SysLogService sysLogService;

    @Pointcut("execution(* com.vnnet.kpi.*.service.*.*(..))")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        // execution method
        Object result = point.proceed();
        // execution time (ms)
        long time = System.currentTimeMillis() - beginTime;
        // save the log
        saveSysLog(point, time);
        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        String userName = MySecurityUtils.getUsername();
        if (joinPoint.getTarget() instanceof SysLogService) {
            return;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();


        // Requested method name
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        if (methodName == null || methodName.isEmpty() || (!methodName.startsWith("save") && !methodName.startsWith("update") && !methodName.startsWith("insert") && !methodName.startsWith("delete"))) {
            return;
        }

        SysLog sysLog = new SysLog();
        sysLog.setMethod(className + "." + methodName + "()");

        // Requested parameters
        Object[] args = joinPoint.getArgs();
        try {
            String params = JSONObject.toJSONString(args[0]);
            if (params.length() > 200) {
                params = params.substring(0, 200) + "...";
            }
            sysLog.setParams(params);
        } catch (Exception e) {
        }

        //Get request
        HttpServletRequest request = HttpUtils.getHttpServletRequest();
        // Set IP address
        sysLog.setIp(IPUtils.getIpAddr(request));

        // username
        sysLog.setUserName(userName);

        // Execution time (ms)
        sysLog.setTime((long) time);

        // Save system log
        sysLogService.save(sysLog);
    }


}

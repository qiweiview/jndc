package com.view.aspect;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.view.annotation.OperationLog;
import com.view.enums.RequestResultEnum;
import com.view.event.OperLogEvent;
import com.view.free_lite.common.utils.Jackson;
import com.view.utils.ServletUtils;
import com.view.utils.StringUtils;
import com.view.utils.TimeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 操作日志记录处理
 *
 * @author Lion Li
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperLogAspect {

    private final ApplicationContext applicationContext;


    /**
     * 排除敏感属性字段
     */
    public static final String[] EXCLUDE_PROPERTIES = { "password", "oldPassword", "newPassword", "confirmPassword" };


    /**
     * 计时 key
     */
    private static final ThreadLocal<StopWatch> KEY_CACHE = new ThreadLocal<>();

    /**
     * 处理请求前执行
     */
    @Before(value = "@annotation(controllerOperationLog)")
    public void doBefore(JoinPoint joinPoint, OperationLog controllerOperationLog) {
        StopWatch stopWatch = new StopWatch();
        KEY_CACHE.set(stopWatch);
        stopWatch.start();
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(controllerOperationLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperationLog controllerOperationLog, Object jsonResult) {
        handleLog(joinPoint, controllerOperationLog, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(controllerOperationLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog controllerOperationLog, Exception e) {
        handleLog(joinPoint, controllerOperationLog, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, OperationLog controllerOperationLog, final Exception e, Object jsonResult) {
        try {
            // *========数据库日志=========*//
            OperLogEvent operLog = new OperLogEvent();
            operLog.setStatus(RequestResultEnum.SUCCESS.getCode());
            // 请求的地址
            String ip = ServletUtils.getClientIP();
            operLog.setOperIp(ip);
            operLog.setOperUrl(StringUtils.substring(ServletUtils.getRequest().getRequestURI(), 0, 255));
            SaSession currentSession = StpUtil.getTokenSession();
            operLog.setOperUsername(currentSession.get(SaSession.USER).toString());

            if (e != null) {
                operLog.setStatus(RequestResultEnum.FAILURE.getCode());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerOperationLog, operLog, jsonResult);
            // 设置消耗时间
            StopWatch stopWatch = KEY_CACHE.get();
            stopWatch.stop();
            operLog.setCostTime(stopWatch.getTime());
            operLog.setOperTime(TimeUtils.now());
            // 发布事件保存数据库
            applicationContext.publishEvent(operLog);
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("异常信息:{}", exp.getMessage(),exp);
        } finally {
            KEY_CACHE.remove();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param operationLog     日志
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, OperationLog operationLog, OperLogEvent operLog, Object jsonResult) throws Exception {
        // 设置action动作
        operLog.setBusinessType(operationLog.businessType().ordinal());
        // 设置标题
        operLog.setTitle(operationLog.title());
        // 设置操作人类别
        operLog.setOperatorType(operationLog.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (operationLog.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog, operationLog.excludeParamNames());
        }
        // 是否需要保存response，参数和值
        if (operationLog.isSaveResponseData() && ObjectUtil.isNotNull(jsonResult)) {
            operLog.setJsonResult(StringUtils.substring(Jackson.toJson(jsonResult), 0, 2000));
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, OperLogEvent operLog, String[] excludeParamNames) throws Exception {
        Map<String, String> paramsMap = ServletUtils.getParamMap(ServletUtils.getRequest());
        String requestMethod = operLog.getRequestMethod();
        if (MapUtil.isEmpty(paramsMap)
                && HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs(), excludeParamNames);
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        } else {
            MapUtil.removeAny(paramsMap, EXCLUDE_PROPERTIES);
            MapUtil.removeAny(paramsMap, excludeParamNames);
            operLog.setOperParam(StringUtils.substring(Jackson.toJson(paramsMap), 0, 2000));
        }
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray, String[] excludeParamNames) {
        StringJoiner params = new StringJoiner(" ");
        if (ArrayUtil.isEmpty(paramsArray)) {
            return params.toString();
        }

        //可以处理敏感字段
        return params.toString();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.values()) {
                return value instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
               || o instanceof BindingResult;
    }
}

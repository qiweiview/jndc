package com.view.aspect;//package com.halcyon.aspect;
//
//
//import com.halcyon.annotation.LoginLog;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.AfterThrowing;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//
//
///**
// * @author sjh
// * @version 1.0
// * @date 2024-09-12 9:43
// * @description: 登录
// */
//@Slf4j
//@Aspect
//@Component
//public class LoginLogAspect {
//
//    @AfterReturning(pointcut = "@annotation(loginLog)", returning = "jsonResult")
//    public void afterUserLogin(JoinPoint joinPoint, LoginLog loginLog, Object jsonResult) {
//        // 执行你需要的监听事件逻辑
//        handleLog(joinPoint, loginLog, null, jsonResult);
//        // 你可以在这里添加任何额外的逻辑，比如记录日志或发送通知
//    }
//
//    /**
//     * 拦截异常操作
//     *
//     * @param joinPoint 切点
//     * @param e         异常
//     */
//    @AfterThrowing(value = "@annotation(loginLog)", throwing = "e")
//    public void doAfterThrowing(JoinPoint joinPoint, LoginLog loginLog, Exception e) {
//        handleLog(joinPoint, loginLog, e, null);
//    }
//
//    protected void handleLog(final JoinPoint joinPoint, LoginLog loginLog, final Exception e, Object jsonResult) {
////        try {
////            UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
////            LoginLogEvent loginInfoEvent = new LoginLogEvent();
////            if (e != null) {
////                loginInfoEvent.setStatus(RequestResultEnum.FAILURE.getCode());
////                loginInfoEvent.setMsg(e.getMessage());
////            }else{
////                loginInfoEvent.setStatus(RequestResultEnum.SUCCESS.getCode());
////                String json = Jackson.toJson(jsonResult);
////                JsonNode jsonNode = Jackson.objectMapper.readTree(json);
////                loginInfoEvent.setMsg(jsonNode.findValue("message").asText());
////            }
////            // 请求的地址
////            String ip = ServletUtils.getClientIP();
////            // 获取客户端操作系统
////            String os = userAgent.getOperatingSystem().getName();
////            // 获取客户端浏览器
////            String browser = userAgent.getBrowser().getName();
////            loginInfoEvent.setIpAddress(ip);
////            loginInfoEvent.setOs(os);
////            loginInfoEvent.setBrowser(browser);
////            loginInfoEvent.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
////            Object[] args = joinPoint.getArgs();
////            Field username = args[0].getClass().getDeclaredField("username");
////            username.setAccessible(true); // 允许访问 private 字段
////
////            loginInfoEvent.setAccount(username.get(args[0]).toString());
////            loginInfoEvent.setLoginTime(TimeUtils.now());
////            // 发布事件保存数据库
////            SpringUtils.context().publishEvent(loginInfoEvent);
////        } catch (Exception exp) {
////            // 记录本地异常日志
////            log.error("异常信息:", exp);
////        }
//    }
//}

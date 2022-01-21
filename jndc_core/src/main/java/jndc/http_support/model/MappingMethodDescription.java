package jndc.http_support.model;


import jndc.http_support.WebMapping;
import jndc.utils.Jackson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Data
@Slf4j
public class MappingMethodDescription {

    private WebMapping webMapping;
    private Method method;
    private Object object;

    public MappingMethodDescription(WebMapping webMapping, Method method, Object object) {
        this.webMapping = webMapping;
        this.method = method;
        this.object = object;
    }

    public byte[] invoke(NettyRequest nettyRequest) {
        try {
            int parameterCount = method.getParameterCount();

            Object invoke;

            if (parameterCount < 1) {
                //todo 不带参数
                invoke = method.invoke(object, null);
            } else {
                //todo 使用NettyRequest参数
                invoke = method.invoke(object, nettyRequest);
            }


            if (invoke instanceof byte[]) {
                return (byte[]) invoke;
            } else if (invoke instanceof String) {
                return invoke.toString().getBytes("utf-8");
            } else {
                //todo 自动序列化
                return Jackson.toJson(invoke).getBytes();
            }
        } catch (Exception e) {
            e.printStackTrace();

            log.error("执行控制层异常 " + e);
            throw new RuntimeException("invoke fail cause" + e);
        } finally {
            nettyRequest.release();
        }
    }


}

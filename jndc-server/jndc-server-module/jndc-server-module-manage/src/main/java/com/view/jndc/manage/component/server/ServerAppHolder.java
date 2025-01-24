package com.view.jndc.manage.component.server;

import com.view.core.server.http.HttpServer;
import com.view.core.server.http.HttpServerConfiguration;
import com.view.core.server.ndc.flow.DesignedServerFlow;
import com.view.core.utils.SSLContextGenerator;
import com.view.free_lite.common.config.exception.BizException;
import com.view.free_lite.common.utils.Jackson;
import com.view.jndc.manage.dao.jndc_server_app.JndcServerAppDao;
import com.view.jndc.manage.enums.server.JNDCServerAPPStatus;
import com.view.jndc.manage.enums.server.JNDCServerBindType;
import com.view.jndc.manage.model.jndc_server_app.dto.JndcServerAppDTO;
import com.view.jndc.manage.model.jndc_server_app.dto.MockServerDTO;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Slf4j
@Component
public class ServerAppHolder {
    private final JndcServerAppDao jndcServerAppDao;

    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private Map<Long, HttpServer> httpServerMap = new ConcurrentHashMap<>();


    public void startServer(JndcServerAppDTO dbData) {
        String bindType = dbData.getBindType();
        if (bindType.equals(JNDCServerBindType.MOCK_SERVER.value)) {
            //todo mock server
            executorService.submit(() -> {
                try {
                    startMockServer(dbData);
                } catch (Exception e) {
                    log.error("mockServer启动失败", e);
                }
            });
        } else {
            throw new BizException("未知的服务类型");
        }

    }


    public void stopServer(JndcServerAppDTO dbData) {
        Long id = dbData.getId();
        HttpServer httpServer = httpServerMap.get(id);
        if (httpServer != null) {
            httpServer.stop();
            httpServerMap.remove(id);
        } else {
            throw new BizException(id + "服务不存在");
        }
    }

    private void startMockServer(JndcServerAppDTO dbData) {
        Long id = dbData.getId();
        HttpServer server = new HttpServer();

        String metaData = dbData.getMetaData();

        MockServerDTO object = Jackson.toObject(metaData, MockServerDTO.class);

        HttpServerConfiguration httpServerConfiguration = new HttpServerConfiguration();

        Boolean useSSL = object.getUseSSL();
        if (useSSL != null && useSSL) {
            httpServerConfiguration.setSslContext(SSLContextGenerator.SSL_CONTEXT);
        }


        //设置数据读取回调
        httpServerConfiguration.setDataReadCallback((context, fullHttpRequest) -> {




            // 构造响应内容
            String mockData = object.getMockData();
            if (mockData == null) {
                mockData = "";
            }
            ByteBuf content = Unpooled.copiedBuffer(mockData, CharsetUtil.UTF_8);

            // 构造响应对象
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            // 设置响应头信息
            String contentType = object.getContentType();
            if (contentType != null) {
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
            }

            //计算响应长度
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            // 发送响应
            context.writeAndFlush(response);

        });

        //启动成功
        httpServerConfiguration.setStartCallBack(() -> {
            log.info("mock-server启动成功");
            jndcServerAppDao.updateStatus(id, JNDCServerAPPStatus.LISTEN.value);
            httpServerMap.put(id, server);
        });

        //启动失败
        httpServerConfiguration.setFailCallback(e -> {
            log.error("启动失败", e);
            jndcServerAppDao.updateStatus(id, JNDCServerAPPStatus.PAUSE.value);
        });

        httpServerConfiguration.setStopCallback(() -> {
            log.info("mock-server关闭");
            jndcServerAppDao.updateStatus(id, JNDCServerAPPStatus.PAUSE.value);
            httpServerMap.remove(id);
        });

        httpServerConfiguration.setHost(dbData.getBindHost());
        httpServerConfiguration.setPort(dbData.getBindPort());


        server.start(httpServerConfiguration);

    }
}

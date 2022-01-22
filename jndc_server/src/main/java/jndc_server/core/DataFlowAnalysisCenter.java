package jndc_server.core;

import jndc.core.NDCMessageProtocol;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 流量分析中心
 */
@Data
@Slf4j
public class DataFlowAnalysisCenter {

    public static final String METHOD_REQUEST = "METHOD_REQUEST";

    public static final String METHOD_RESPONSE = "METHOD_RESPONSE";

    private AsynchronousEventCenter asynchronousEventCenter;

    public DataFlowAnalysisCenter(AsynchronousEventCenter asynchronousEventCenter) {
        this.asynchronousEventCenter = asynchronousEventCenter;
    }

    /**
     * 数据分析
     *
     * @param data
     */
    public void analyse(NDCMessageProtocol data, String method) {
        asynchronousEventCenter.dataAnalyseJob(() -> {
//            log.info(method+" ---> "+data.getLocalInetAddress() + ":" + data.getRemotePort() + " to " + data.getRemoteInetAddress() + ":" + data.getLocalPort() + "\n" + new String(data.getData()));
        });
    }
}

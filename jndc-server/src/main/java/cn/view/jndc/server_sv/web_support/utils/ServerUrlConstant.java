package cn.view.jndc.server_sv.web_support.utils;

public class ServerUrlConstant {
    public interface DevelopDebug {
        public static final String reloadFront = "/reloadFront";//reload front project

        public static final String getDeviceIp = "/getDeviceIp";//get device ip
    }


    public interface ServerHttp {
        public static final String saveHostRouteRule = "/saveHostRouteRule";//

        public static final String updateHostRouteRule = "/updateHostRouteRule";//

        public static final String deleteHostRouteRule = "/deleteHostRouteRule";//

        public static final String listHostRouteRule = "/listHostRouteRule";//

    }

    public interface ServerManage {
        public static final String login = "/login";//登录

        public static final String getServerChannelTable = "/getServerChannelTable";//渠道列表

        public static final String getChannelRecord = "/getChannelRecord";//

        public static final String clearChannelRecord = "/clearChannelRecord";//

        public static final String sendHeartBeat = "/sendHeartBeat";//

        public static final String closeChannelByServer = "/closeChannelByServer";//

        public static final String getServiceList="/getServiceList";//

        public static final String getServerPortList="/getServerPortList";//

        public static final String createPortMonitoring="/createPortMonitoring";//

        public static final String doServiceBind="/doServiceBind";//

        public static final String doDateRangeEdit="/doDateRangeEdit";//

        public static final String deleteServiceBindRecord="/deleteServiceBindRecord";//

        public static final String resetBindRecord="/resetBindRecord";//

        public static final String stopServiceBind="/stopServiceBind";//

        public static final String blackList="/blackList";//

        public static final String whiteList="/whiteList";//

        public static final String addToIpWhiteList="/addToIpWhiteList";//

        public static final String addToIpBlackList="/addToIpBlackList";//

        public static final String deleteIpRuleByPrimaryKey="/deleteIpRuleByPrimaryKey";//

        public static final String releaseRecord="/releaseRecord";//

        public static final String blockRecord="/blockRecord";//

        public static final String getCurrentDeviceIp="/getCurrentDeviceIp";//获取当前设备IP

        public static final String clearIpRecord="/clearIpRecord";//清空ip记录
    }
}

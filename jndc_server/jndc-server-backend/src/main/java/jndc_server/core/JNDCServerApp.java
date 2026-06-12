package jndc_server.core;

import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.utils.StringUtils4V;
import jndc.web_support.config.ServeManageConfig;
import jndc.web_support.http_module.ManagementServer;
import jndc_server.config.JNDCServerConfig;
import jndc_server.core.app.JndcCoreServer;
import jndc_server.databases_object.ServerPortBind;
import jndc_server.web_support.http_module.JNDCHttpServer;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 主服务
 */
public class JNDCServerApp {

    public JNDCServerApp() {
    }


    /**
     * do some init operation only for server
     */
    public void initBelongOnlyInServer() {
        ScheduledTaskCenter scheduledTaskCenter = UniqueBeanManage.getBean(ScheduledTaskCenter.class);
        scheduledTaskCenter.start();
    }


    public void createServer() {
        //服务端初始化事件
        initBelongOnlyInServer();


        //重置所有端口使用信息
        DBWrapper<ServerPortBind> dbWrapper = DBWrapper.getDBWrapper(ServerPortBind.class);
        dbWrapper.customExecute("update server_port_bind set port_enable = 0", null);


        JNDCServerConfig jndcServerConfig = UniqueBeanManage.getBean(JNDCServerConfig.class);
        ServeManageConfig manageConfig = jndcServerConfig.getManageConfig();
        manageConfig.setBindIp(jndcServerConfig.getBindIp());

        // 静态资源优先跟随发布目录，避免落到 ~/.jndc 或 IDEA 工作目录的上级目录。
        manageConfig.setAdminProjectPath(resolveAdminProjectPath(manageConfig));


        //admin管理页面
        ManagementServer managementServer = new ManagementServer();
        managementServer.start(manageConfig);//start

        //核心服务
        JndcCoreServer jndcCoreServer = new JndcCoreServer();
        jndcCoreServer.start();

        //http层服务
        JNDCHttpServer jndcHttpServer = new JNDCHttpServer();
        jndcHttpServer.start();


    }

    private String resolveAdminProjectPath(ServeManageConfig manageConfig) {
        if (!StringUtils4V.isBlank(manageConfig.getAdminProjectPath())) {
            return manageConfig.getAdminProjectPath();
        }

        List<String> candidates = new ArrayList<String>();
        addCandidateDirectories(candidates, System.getProperty("app.home"));
        addCandidateDirectories(candidates, System.getProperty("user.dir"));
        addCodeSourceDirectories(candidates);

        for (String candidate : candidates) {
            if (new File(candidate).exists()) {
                return candidate;
            }
        }

        if (!candidates.isEmpty()) {
            return candidates.get(0);
        }
        return "page";
    }

    private void addCodeSourceDirectories(List<String> candidates) {
        CodeSource codeSource = JNDCServerApp.class.getProtectionDomain().getCodeSource();
        if (codeSource == null || codeSource.getLocation() == null) {
            return;
        }

        try {
            File location = new File(codeSource.getLocation().toURI());
            if (location.isFile()) {
                File libDir = location.getParentFile();
                File appHome = libDir == null ? null : libDir.getParentFile();
                if (appHome != null) {
                    addCandidateDirectories(candidates, appHome.getAbsolutePath());
                }
                return;
            }

            File parent = location.getParentFile();
            if (parent != null) {
                addTargetCandidateDirectories(candidates, parent.getAbsolutePath());
            }
        } catch (URISyntaxException ignored) {
        }
    }

    private void addCandidateDirectories(List<String> candidates, String baseDir) {
        if (StringUtils4V.isBlank(baseDir)) {
            return;
        }
        addCandidate(candidates, baseDir, "page");
        addCandidate(candidates, baseDir, "html");
        addCandidate(candidates, baseDir, "compare_dist");
        addTargetCandidateDirectories(candidates, baseDir);
    }

    private void addTargetCandidateDirectories(List<String> candidates, String baseDir) {
        if (StringUtils4V.isBlank(baseDir)) {
            return;
        }
        addCandidate(candidates, baseDir, "jndc_server", "page");
        addCandidate(candidates, baseDir, "jndc_server", "html");
        addCandidate(candidates, baseDir, "jndc_server", "compare_dist");
        addCandidate(candidates, baseDir, "target", "jndc_server", "page");
        addCandidate(candidates, baseDir, "target", "jndc_server", "html");
        addCandidate(candidates, baseDir, "target", "jndc_server", "compare_dist");
    }

    private void addCandidate(List<String> candidates, String baseDir, String... children) {
        File current = new File(baseDir);
        for (String child : children) {
            current = new File(current, child);
        }
        String path = current.getAbsolutePath();
        if (!candidates.contains(path)) {
            candidates.add(path);
        }
    }


}

package com.easychat.entity.config;

import com.easychat.utils.StringTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("appConfig")
public class AppConfig {

    //端口
    @Value("$(ws.port)")
    private String wsPort;
    //文件目录
    @Value("$(project,folder)")
    private String projectFolder;
    //超级管理员
    @Value("$(admin.email")
    private String adminEmail;

    public String getWsPort() {
        return wsPort;
    }

    public String getProjectFolder() {
        if(StringTools.isEmpty( projectFolder) && !projectFolder.endsWith("/")){
            projectFolder = projectFolder + "/";
        }
        return projectFolder;
    }

    public String getAdminEmail() {
        return adminEmail;
    }
}

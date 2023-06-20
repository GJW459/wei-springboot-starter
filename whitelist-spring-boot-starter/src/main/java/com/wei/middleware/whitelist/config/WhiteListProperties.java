package com.wei.middleware.whitelist.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wei
 * 白名单配置
 */
@ConfigurationProperties(prefix = "wei.whitelist")
public class WhiteListProperties {
    private String user;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

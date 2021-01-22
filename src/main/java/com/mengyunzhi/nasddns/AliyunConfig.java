package com.mengyunzhi.nasddns;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author panjie
 */
@Configuration
@ConfigurationProperties(prefix = "app.aliyun")
public class AliyunConfig {
    /**
     * AccessKey ID
     */
    private String id;
    /**
     * AccessKey Secret
     */
    private String secret;
    /**
     *  地域ID
     */
    private String region;

    /**
     * 域名
     */
    private List<Domain> domains;

    /**
     * 记录类型
     */
    private String type = "A";

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    static public class Domain {
        /**
         * 域名
         */
        private String name;
        /**
         * 主机记录
         */
        private String record;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRecord() {
            return record;
        }

        public void setRecord(String record) {
            this.record = record;
        }
    }
}



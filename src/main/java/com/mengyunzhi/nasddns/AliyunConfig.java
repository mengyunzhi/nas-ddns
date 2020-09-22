package com.mengyunzhi.nasddns;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.application.aliyun")
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
     * 主域名
     */
    private String domainName;

    /**
     * 主机记录
     */
    private String keyWord;

    /**
     * 记录类型
     */
    private String type = "A";



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

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

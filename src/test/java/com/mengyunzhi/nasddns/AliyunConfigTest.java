package com.mengyunzhi.nasddns;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AliyunConfigTest {

    @Autowired
    AliyunConfig aliyunConfig;

    @Test
    void test() {
        assertEquals(this.aliyunConfig.getId(), "5LTAI4GB2zNxSAeSByVcxHSa");
        assertEquals(this.aliyunConfig.getDomainName(), "yunzhi.club");
    }
}
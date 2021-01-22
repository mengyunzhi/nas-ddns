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
        assertEquals(this.aliyunConfig.getId(), "5LTA123I4G123B");
        assertEquals(this.aliyunConfig.getDomains().size(), 2);
    }

}
package com.mengyunzhi.nasddns;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author panjie 3792535@qq.com
 */
@SpringBootTest
class DdnsServiceTest {
    @Autowired
    DdnsService ddnsService;

    @Test
    void main() {
        this.ddnsService.main();
    }
}
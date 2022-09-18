package com.chuang.seckill;

import com.chuang.seckill.utils.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SeckillApplicationTests {

    @Test
    void contextLoads() {

        String s = MD5Util.inputPassToDBPass("123456", "123456");
        System.out.println(s);
    }

}

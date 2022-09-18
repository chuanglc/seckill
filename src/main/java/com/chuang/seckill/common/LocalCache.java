package com.chuang.seckill.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author chuang
 * @Date 2022/07/28 21:31
 **/

public class LocalCache {

    /**
     * 用于存放秒杀商品库存是否为0
     */
    public static Map<Long, Boolean> emptyStockMap = new HashMap<>();

}

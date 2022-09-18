package com.chuang.seckill.exception;


import com.chuang.seckill.common.RespBeanEnum;
import lombok.Data;

/**
 * 全局异常
 *
 * @author: LC
 * @date 2022/3/2 5:32 下午
 * @ClassName: GlobalException
 */

@Data
public class GlobalException extends RuntimeException {

    private RespBeanEnum respBeanEnum;

    public GlobalException(RespBeanEnum respBeanEnum) {
        this.respBeanEnum = respBeanEnum;
    }
}

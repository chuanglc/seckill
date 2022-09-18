package com.chuang.seckill.vo;

import com.chuang.seckill.annotation.IsMobile;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;


/**
 * 登录传参
 *
 * @author: LC
 * @date 2022/3/2 2:01 下午
 * @ClassName: LoginVo
 */
@Data
@ToString
public class LoginVo {

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
//    @Length(min = 32)
    private String password;
}

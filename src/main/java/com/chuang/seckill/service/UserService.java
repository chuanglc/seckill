package com.chuang.seckill.service;

import com.chuang.seckill.common.RespBean;
import com.chuang.seckill.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chuang.seckill.vo.LoginVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
public interface UserService extends IService<User> {

    /**
     * 登录方法
     * @param loginVo
     * @param request
     * @param response
     * @date 1:49 下午 2022/3/3
     **/
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);


    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

}

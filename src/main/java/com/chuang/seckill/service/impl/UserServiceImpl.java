package com.chuang.seckill.service.impl;

import com.chuang.seckill.common.RespBean;
import com.chuang.seckill.common.RespBeanEnum;
import com.chuang.seckill.entity.User;
import com.chuang.seckill.exception.GlobalException;
import com.chuang.seckill.mapper.UserMapper;
import com.chuang.seckill.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuang.seckill.utils.CookieUtil;
import com.chuang.seckill.utils.MD5Util;
import com.chuang.seckill.utils.UUIDUtil;
import com.chuang.seckill.utils.ValidatorUtil;
import com.chuang.seckill.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chuang
 * @since 2022-07-25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    public UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public synchronized RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //查询用户
        User user = userMapper.selectById(mobile);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
//        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
//            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
//        }
        //生成Cookie
        String userTicket = UUIDUtil.uuid();

        File file = new File("C:/Users/chuang/Desktop/data.txt");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, true);
            outputStream.write((userTicket+"\n").getBytes());
        } catch (Exception e) {
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //将用户信息存入redis
        redisTemplate.opsForValue().set("user:" + userTicket, user,1, TimeUnit.HOURS);
//        request.getSession().setAttribute(userTicket, user);
        CookieUtil.setCookie(request, response, "userTicket", userTicket);
        return RespBean.success(userTicket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

}
